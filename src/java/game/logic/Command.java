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
        String cmd = (String) json.get("Command");
        JSONObject data = (JSONObject) json.get("Data");
        if (cmd.equals(Phase.SETUP)) {
            String country = (String) data.get("CountryClicked");
            int player = (Integer) data.get("CurrentPlayer");
            
            if (game.getGameState().getCurrentPlayer() != player) return;
            
            if (game.getBoard().getCountry(country).getOwner() == -1) {
                game.getBoard().getCountry(country).setOwner(player);
                game.getBoard().getCountry(country).setTroops(1);
                game.nextPlayer();//game.endTurn();
            } else {
                // country already owned by another player
            }
        }

        else if (cmd.equals(Phase.DEPLOY)) {
            String country = (String) data.get("CountryClicked");
            int player = (Integer) data.get("CurrentPlayer");
            Player p = (Player) game.getPlayerList().getPlayers().get(player);
            
            if (game.getGameState().getCurrentPlayer() != player) return;
            
            if (game.getBoard().getCountry(country).getOwner() == player && p.getTroopsToDeploy() > 0) {
                game.getBoard().getCountry(country).setTroops(game.getBoard().getCountry(country).getTroops() + (Integer) data.get("Troops"));
                p.decrementTroopsToDeploy();
            } else {
                // country not yours
            }
        }

        else if (cmd.equals(Phase.ATTACK)) {
            System.out.println("In attack command"); //remove
            
            // Get data from the sent JSON
            int player = (Integer) data.get("CurrentPlayer");
            String attacker = (String) data.get("AttackingCountry");
            String defender = (String) data.get("DefendingCountry");
            Country attackingCountry = game.getBoard().getCountry(attacker);
            Country defendingCountry = game.getBoard().getCountry(defender);
            
            // do nothing if it is not the player's turn
            if (game.getGameState().getCurrentPlayer() != player) return;
            
            // do nothing if attacking player owns the country he is trying to attack
            if (defendingCountry.getOwner() == player) return;
            
            // set the number of dice to be rolled
            int attackingDice = attackingCountry.getTroops();
            int defendingDice = defendingCountry.getTroops();
            if (attackingDice > 3) attackingDice = 3;
            if (defendingDice > 2) defendingDice = 2;
            
            // roll the dice
            try {
                AttackOutcome outcome = Dice.Roll(attackingDice, defendingDice);
                System.out.println(outcome); //remove

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
            String from = (String) data.get("SourceCountry");
            String to = (String) data.get("CountryClicked");
            int player = (Integer) data.get("CurrentPlayer");
            
            if (game.getGameState().getCurrentPlayer() != player) return;
            
            if (game.getBoard().getCountry(from).getOwner() != player || game.getBoard().getCountry(to).getOwner() != player) {
                // Player doesn't own one of these countries
            } else {
                if (BoardLogic.isNeighbour(to, from)) {
                    int troops = (Integer) data.getInt("Troops");
                    game.getBoard().getCountry(to).setTroops(game.getBoard().getCountry(to).getTroops() + troops);
                    game.getBoard().getCountry(from).setTroops(game.getBoard().getCountry(from).getTroops() - troops);
                }
            }
        }

        else if (cmd.equals(Phase.ENDPHASE)) {
            //if (game.getGameState().getCurrentPlayer() != player) return;
            
            game.endPhase();
        }


        
        else if (cmd.equals(ENDTURN)) {
            //if (game.getGameState().getCurrentPlayer() != player) return;

            game.nextPlayer();
        }
        
        else if (cmd.equals(CREATE)) {

        }

        else if (cmd.equals(JOIN)) {
            String name = (String) data.get("CurrentPlayer");
            try {
                game.getPlayerList().joinGame(new Player(name, 3));
            } catch (PlayerException ex) {
                //join game fails, response?
            }
        }

        else if (cmd.equals(QUIT)) {
            String name = (String) data.get("CurrentPlayer");
            game.removePlayer(name);
        }
        
        else if (cmd.equals(STARTGAME)) {
            game.getGameState().closeLobby();
        }
        
        else if (cmd.equals(DEBUG)) {
            Board b = game.getBoard();
            for (Object country: b.getAllCountries().keySet()) {
                b.getCountry((String) country).setOwner(game.getGameState().getCurrentPlayer());
                b.getCountry((String) country).setTroops(1);
                game.nextPlayer();
            }
        }  
    }
}