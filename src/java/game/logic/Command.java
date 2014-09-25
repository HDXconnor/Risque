/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.logic;

import game.objects.Game;
import game.objects.Phase;
import game.objects.Player;
import game.objects.AttackOutcome;
import game.objects.exceptions.PlayerException;
import game.objects.exceptions.DiceException;
import game.logic.Dice;
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
    public static void parseInput(JSONObject json, Game game) throws JSONException {
        String cmd = (String) json.get("Command");
        JSONObject data = (JSONObject) json.get("Data");

        if (cmd.equals(Phase.SETUP)) {
            String country = (String) data.get("CountryClicked");
            int player = (Integer) data.get("CurrentPlayer");
            
            if (game.getCurrentPlayer() != player) return;
            
            if (game.getBoard().getCountry(country).getOwner() == -1) {
                game.getBoard().getCountry(country).setOwner(player);
                game.getGameState().endTurn();
            } else {
                // country already owned by another player
            }
        }

        else if (cmd.equals(Phase.DEPLOY)) {
            String country = (String) data.get("CountryClicked");
            int player = (Integer) data.get("CurrentPlayer");
            if (game.getBoard().getCountry(country).getOwner() != player) {
                // country aint yours bitch
            } else {
                game.getBoard().getCountry(country).setTroops(game.getBoard().getCountry(country).getTroops() + (Integer) data.get("Troops"));
            }
        }

        else if (cmd.equals(Phase.ATTACK)) {
            int player = (Integer) data.get("CurrentPlayer");
            String attacker = (String) data.get("AttackingCountry");
            String defender = (String) data.get("DefendingCountry");
            if (game.getBoard().getCountry(defender).getOwner() == player) {
                // can't attack yourself
            } else {
                int attackingTroops = game.getBoard().getCountry(attacker).getTroops();
                int defendingTroops = game.getBoard().getCountry(defender).getTroops();
                int attackDice, defendDice;
                AttackOutcome outcome;
                try {
                    // For now we attack until resolved
                    while (attackingTroops != 1 || defendingTroops != 0) {
                        if (attackingTroops >= 3) {
                            attackDice = 3;
                        } else {
                            attackDice = attackingTroops;
                        }
                        if (defendingTroops >= 2) {
                            defendDice = 3;
                        } else {
                            defendDice = defendingTroops;
                        }
                        outcome = Dice.Roll(attackDice, defendDice);
                        attackingTroops -= outcome.getTroopsLostByAttacker();
                        defendingTroops -= outcome.getTroopsLostByDefender();
                    }
                    if (defendingTroops == 0) {
                        game.getBoard().getCountry(defender).setOwner(player);
                        game.getBoard().getCountry(defender).setTroops(attackingTroops);
                        game.getBoard().getCountry(attacker).setTroops(1);
                    }
                } catch (DiceException e) {

                }
            }
        }

        else if (cmd.equals(Phase.MOVE)) {
            String from = (String) data.get("SourceCountry");
            String to = (String) data.get("CountryClicked");
            int player = (Integer) data.get("CurrentPlayer");
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
            game.endPhase();
        }

        else if (cmd.equals(CREATE)) {

        }

        else if (cmd.equals(JOIN)) {
            String name = (String) data.get("CurrentPlayer");
            try {
                game.getPlayers().joinGame(new Player(name, 20));
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
    }
}
