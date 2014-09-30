package game.logic;

import game.objects.Game;
import game.objects.Phase;
import game.objects.Player;
import game.objects.AttackOutcome;
import game.objects.exceptions.PlayerException;
import game.objects.exceptions.DiceException;
import game.logic.Dice;
import game.objects.Board;
import game.objects.Country;
import game.objects.exceptions.CommandException;
import game.objects.exceptions.TroopsException;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author Simeon
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
    public static void parseInput(JSONObject json, Game game) throws JSONException, TroopsException, CommandException {
        
        // Get command data from the sent JSON
        String cmd = (String) json.get("Command");
        JSONObject data = (JSONObject) json.get("Data");
        
        // pick which command to use
        if (cmd.equals(Phase.SETUP)) {
            
            // Get data from the sent JSON
            String country = (String) data.get("CountryClicked");
            int player = (Integer) data.get("CurrentPlayer");
            
            // do nothing if it is not the player's turn
            if (game.getGameState().getCurrentPlayer() != player)
                throw new CommandException("Player " + player + " sent a command during " + "'s turn.");
            
            // if country doesn't have an owner, player claims country
            if (game.getBoard().getCountry(country).getOwner() == -1) {
                game.getBoard().getCountry(country).setOwner(player);
                game.getBoard().getCountry(country).setTroops(1);
                game.nextPlayer();//game.endTurn();
            } else {
                // country already owned by another player
            }
        }

        else if (cmd.equals(Phase.DEPLOY)) {
            
            // Get data from the sent JSON
            String country = (String) data.get("CountryClicked");
            int player = (Integer) data.get("CurrentPlayer");
            Player p = (Player) game.getPlayerList().getPlayers().get(player);
            
            // do nothing if it is not the player's turn
            if (game.getGameState().getCurrentPlayer() != player)
                throw new CommandException("Player " + player + " sent a command during " + "'s turn.");
            
            // if country is owned by player, and player has troops to deploy, deploy 1x troop
            if (game.getBoard().getCountry(country).getOwner() == player && p.getTroopsToDeploy() > 0) {
                game.getBoard().getCountry(country).incrementTroops();
                p.decrementTroopsToDeploy();
            } else {
                throw new CommandException("DEPLOY: Country is not the player's, or player has no troops to deploy");
            }
        }

        else if (cmd.equals(Phase.ATTACK)) {
            
            // Get data from the sent JSON
            int player = (Integer) data.get("CurrentPlayer");
            String attacker = (String) data.get("AttackingCountry");
            String defender = (String) data.get("DefendingCountry");
            Country attackingCountry = game.getBoard().getCountry(attacker);
            Country defendingCountry = game.getBoard().getCountry(defender);
            
            // do nothing if it is not the player's turn
            if (game.getGameState().getCurrentPlayer() != player)
                throw new CommandException("Player " + player + " sent a command during " + "'s turn.");
            
            // do nothing if attacking player owns the country he is trying to attack
            if (defendingCountry.getOwner() == player)
                throw new CommandException("Player " + player + " cannot attack his own country.");
            
            // set the number of dice to be rolled
            int attackingDice = attackingCountry.getTroops();
            int defendingDice = defendingCountry.getTroops();
            if (attackingDice > 3) attackingDice = 3;
            if (defendingDice > 2) defendingDice = 2;
            
            // roll the dice
            try {
                AttackOutcome outcome = Dice.Roll(attackingDice, defendingDice);
                System.out.println(outcome);

                // country loses troops
                attackingCountry.removeTroops(outcome.getTroopsLostByAttacker());
                defendingCountry.removeTroops(outcome.getTroopsLostByDefender());

                // check if takeover occurred
                if (defendingCountry.getTroops() == 0) {
                    defendingCountry.setOwner(player);
                    defendingCountry.setTroops(attackingCountry.getTroops() - 1);
                    attackingCountry.setTroops(1);
                }
            } catch (DiceException e) {
                System.err.println(e);
            }

        }

        else if (cmd.equals(Phase.MOVE)) {
            
            // Get data from the sent JSON
            String from = (String) data.get("SourceCountry");
            String to = (String) data.get("CountryClicked");
            int player = (Integer) data.get("CurrentPlayer");
            
            // do nothing if it is not the player's turn
            if (game.getGameState().getCurrentPlayer() != player)
                throw new CommandException("Player " + player + " sent a command during " + "'s turn.");
            
            // player doesn't own both countries
            if (game.getBoard().getCountry(from).getOwner() != player || game.getBoard().getCountry(to).getOwner() != player)
                throw new CommandException("Player " + player + " does not own both countries");
            
            // check if the two countries are neighbours
            if (BoardLogic.isNeighbour(to, from)) {
                
                // move troops
                int troops = (Integer) data.getInt("Troops");
                game.getBoard().getCountry(to).setTroops(game.getBoard().getCountry(to).getTroops() + troops);
                game.getBoard().getCountry(from).setTroops(game.getBoard().getCountry(from).getTroops() - troops);
            }
        }

        else if (cmd.equals(Phase.ENDPHASE)) {
            
            // do nothing if it is not the player's turn
            //if (game.getGameState().getCurrentPlayer() != player) throw new CommandException("Player " + player + " sent a command during " + "'s turn.");
            
            game.endPhase();
        }


        
        else if (cmd.equals(ENDTURN)) {
            
            // do nothing if it is not the player's turn
            //if (game.getGameState().getCurrentPlayer() != player) throw new CommandException("Player " + player + " sent a command during " + "'s turn.");

            game.nextPlayer();
        }
        
        else if (cmd.equals(CREATE)) {
            // TODO
        }

        else if (cmd.equals(JOIN)) {
            
            // Get data from the sent JSON
            String name = (String) data.get("CurrentPlayer");
            
            try {
                game.getPlayerList().joinGame(new Player(name, 3));
            } catch (PlayerException ex) {
                //join game fails, response?
            }
        }

        else if (cmd.equals(QUIT)) {
            // Get data from the sent JSON
            String name = (String) data.get("CurrentPlayer");
            
            game.removePlayer(name);
        }
        
        else if (cmd.equals(STARTGAME)) {
            game.getGameState().closeLobby();
        }
        
        // temp command for debug purposes
        else if (cmd.equals(DEBUG)) {
            Board board = game.getBoard();
            for (Object country: board.getAllCountries().keySet()) {
                board.getCountry((String) country).setOwner(game.getGameState().getCurrentPlayer());
                board.getCountry((String) country).setTroops(1);
                game.nextPlayer();
            }
        }  
    }
}