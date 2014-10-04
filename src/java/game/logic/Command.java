package game.logic;

import game.data.CountriesData;
import game.objects.*;
import game.objects.exceptions.CommandException;
import game.objects.exceptions.DiceException;
import game.objects.exceptions.PlayerException;
import game.objects.exceptions.TroopsException;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Translates the JSON commands sent to the server from player actions into the
 * method calls. Supported Commands:
 * <ul>
 * <li>Create</li> <li>Join</li><li>Quit</li><li>StartGame</li>
 * <li>EndTurn</li><li>Debug</li>
 * </ul>
 * <p>
 * Also, Setup, Deploy, Attack, Move, and EndPhase commands are supported.
 * </p>
 * <p>
 * A player's command will not be successful if it is not their turn.
 * </p>
 */
public class Command {

    private static final String CREATE = "Create";
    private static final String JOIN = "Join";
    private static final String QUIT = "Quit";
    private static final String STARTGAME = "StartGame";
    private static final String ENDTURN = "EndTurn";
    private static final String DEBUG = "Debug";

    /*
     Commands to support:
     Setup
     Deploy
     Attack
     Move
     EndPhase (end phase command)
     TODO:
     Need to check which player sent the command - other players are able to send another players commands currently
     Server checks player but currently client always sends '1'
     */
    public static void parseInput(JSONObject json, Game game) throws JSONException, TroopsException, CommandException, DiceException, PlayerException {
        
        JSONObject data = (JSONObject) json.get("Data");
        String cmd = json.getString("Command");

        switch (cmd) {
            case JOIN: {
                String name = (String) data.get("CurrentPlayer");
                game.getPlayerList().joinGame(new Player(name, 3));
                game.setLastModified();
                return;
            }
            
            case STARTGAME: {
                game.getGameState().closeLobby();
                game.setLastModified();
                return;
            }
            
            case Phase.ENDPHASE: {
                game.endPhase();
                game.setLastModified();
                return;
            }
        }

        Player commandingPlayer = game.getPlayerList().getPlayerByName(data.getInt("CurrentPlayer"));

        if (game.getGameState().isCurrentPlayer(commandingPlayer.getPlayerNum())) {

            Board board = game.getBoard();
            Country selectedCountry = board.getCountry(data.getString("CountryClicked"));

            // issue command from player
            switch (cmd) {
                case Phase.SETUP: {
                    claimSetupCountry(selectedCountry, commandingPlayer.getPlayerNum(), game);
                    break;
                }
                
                case Phase.DEPLOY: {
                    deploy(commandingPlayer, selectedCountry);
                    break;
                }
                
                case Phase.ATTACK: {
                    // Get data from the sent JSON
                    String attacker = data.getString("AttackingCountry");
                    String defender = data.getString("DefendingCountry");
                    Country attackingCountry = board.getCountry(attacker);
                    Country defendingCountry = board.getCountry(defender);

                    // do nothing if attacking player owns the country he is trying to attack
                    if (defendingCountry.isOwnedBy(commandingPlayer.getPlayerNum())) {
                        throw new CommandException("Player " + commandingPlayer + " cannot attack his own country.");
                    }

                    // do nothing if attacking player has less than 2 troops
                    if (attackingCountry.getTroops() < 2) {
                        throw new CommandException("Country needs at least 2 troops to be able to attack another country.");
                    }

                    attack(commandingPlayer, attackingCountry, defendingCountry);
                    break;
                }
                
                case Phase.MOVE: {
                    // Get data from the sent JSON
                    String from = data.getString("SourceCountry");
                    String to = data.getString("CountryClicked");

                    // player doesn't own both countries
                    if (board.getCountry(from).getOwner() != commandingPlayer.getPlayerNum() || board.getCountry(to).getOwner() != commandingPlayer.getPlayerNum()) {
                        throw new CommandException("Player " + commandingPlayer + " does not own both countries");
                    }

                    // check if the two countries are neighbours
                    if (CountriesData.isNeighbour(to, from)) {
                        // move troops
                        int troops = data.getInt("Troops");
                        board.getCountry(to).setTroops(board.getCountry(to).getTroops() + troops);
                        board.getCountry(from).setTroops(board.getCountry(from).getTroops() - troops);
                    }
                    break;
                }
                case ENDTURN: {
                    game.nextPlayer();
                    break;
                }
                
                case CREATE:
                    // TODO
                    break;
                    
                case QUIT: {
                    game.removePlayer(commandingPlayer.getName());
                    break;
                }
                
                // temp command for debug purposes
                case DEBUG: {
                    for (Object country : board.getAllCountries().keySet()) {
                        board.getCountry((String) country).setOwner(game.getGameState().getCurrentPlayer());
                        board.getCountry((String) country).setTroops(1);
                        game.nextPlayer();
                    }
                    break;
                }
            }
            game.setLastModified();
        }
    }

    /**
     * Claims a country and adds a troop to it for the curreant player if
     * currently in the Setup phase.
     * <p>
     * To reach this method, it must be both the Setup phase and the commanding
     * player's turn in the game. After checking to see if selected country has
     * no owner, selected country gets +1 troop, commanding player becomes owner
     * and the turn is passed to next player.
     * </p>
     *
     * @param selectedCountry country clicked by player
     * @param commandingPlayer player issuing the command
     * @param game current game instance
     * @see game.objects.Country#hasOwner() hasOwner
     */
    private static void claimSetupCountry(Country selectedCountry, int commandingPlayer, Game game) {
        if (!selectedCountry.hasOwner()) {
            selectedCountry.setOwner(commandingPlayer);
            selectedCountry.setTroops(1);
            game.nextPlayer();//game.endTurn();
        } else {
            // country already owned by another player
        }
    }

    /**
     * Deploys one troop unit from the commanding player's army to the specified
     * country if it is owned by them and it is the Deploy phase.
     * <p>
     * One troop unit will be deployed to the target country only if it is both
     * the Deploy phase and the commanding player's turn. Player must have
     * troops to deploy.
     * </p>
     * <p>
     * Selected country's troop count is incremented while player's troop count
     * is decremented.
     * </p>
     *
     * @param commandingPlayer player object of commanding player
     * @param selectedCountry country clicked by player
     * @throws TroopsException ASK SIMEON :)
     * @throws CommandException if country does not belong to the commanding
     * player or commanding player has no troops.
     */
    private static void deploy(Player commandingPlayer, Country selectedCountry) throws TroopsException, CommandException {
        // if country is owned by player, and player has troops to deploy, deploy 1x troop
        if (selectedCountry.isOwnedBy(commandingPlayer.getPlayerNum()) && commandingPlayer.getTroopsToDeploy() > 0) {
            selectedCountry.incrementTroops();
            commandingPlayer.decrementTroopsToDeploy();
        } else {
            throw new CommandException("DEPLOY: Country is not the player's, or player has no troops to deploy");
        }
    }

    private static void attack(Player commandingPlayer, Country attackingCountry, Country defendingCountry) {
        // set the number of dice to be rolled
        int attackingDice = attackingCountry.getTroops();
        int defendingDice = defendingCountry.getTroops();
        if (attackingDice > 3) {
            attackingDice = 3;
        }
        if (defendingDice > 2) {
            defendingDice = 2;
        }

        // roll the dice
        AttackOutcome outcome = null;
        try {
            outcome = Dice.Roll(attackingDice, defendingDice);
        } catch (DiceException e) {
            e.printStackTrace();
        }
        System.out.println(outcome);

        // country loses troops
        attackingCountry.removeTroops(outcome.getTroopsLostByAttacker());
        defendingCountry.removeTroops(outcome.getTroopsLostByDefender());

        // check if takeover occurred
        if (defendingCountry.getTroops() == 0) {
            defendingCountry.setOwner(commandingPlayer.getPlayerNum());
            defendingCountry.setTroops(attackingCountry.getTroops() - 1);
            attackingCountry.setTroops(1);
        }
    }

    private static void move() {

    }
}
