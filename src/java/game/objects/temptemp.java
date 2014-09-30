/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package game.objects;

import game.data.CountriesData;
import game.logic.Command;
import game.logic.Dice;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONObject;


/**
 *
 * @author Simeon
 */
public class temptemp {
    
    public static void main(String[] args) throws Exception {
//        PlayerList p = new PlayerList();
//        Game g = new Game(p);
//        p.joinGame(new Player("one", 123));
//        p.joinGame(new Player("two", 123));
//        p.joinGame(new Player("three", 123));
//        p.joinGame(new Player("four", 123));
//        p.joinGame(new Player("five", 123));
//        p.joinGame(new Player("six", 123));
//        p.removePlayer("three");
//        System.out.println(g.getGameJSON());
//        JSONObject j = new JSONObject().put("Command", "Setup").put("Data", new JSONObject().put("CountryClicked", "AF02").put("CurrentPlayer", 1));
//        Command.parseInput(j, g);
//        System.out.println(g.getGameJSON());
        AttackOutcome ao = Dice.Roll(3,2);
        System.out.println(ao);
        


    }
}
