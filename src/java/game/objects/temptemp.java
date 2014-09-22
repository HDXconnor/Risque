/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package game.objects;

import game.data.CountriesData;
import game.logic.Dice;


/**
 *
 * @author Simeon
 */
public class temptemp {
    
    public static void main(String[] args) throws Exception {
        PlayerList p = new PlayerList();
        Game g = new Game(p);
        p.joinGame(new Player("one", 123));
        p.joinGame(new Player("two", 123));
        p.joinGame(new Player("three", 123));
        p.joinGame(new Player("four", 123));
        p.joinGame(new Player("five", 123));
        p.joinGame(new Player("six", 123));
        p.removePlayer("three");
        System.out.println(g.getGameJSON());
        
        


    }
}
