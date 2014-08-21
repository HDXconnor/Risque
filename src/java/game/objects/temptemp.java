/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package game.objects;

import game.data.CountriesData;
import game.logic.Dice;
import java.util.Map;
import java.util.Set;


/**
 *
 * @author Simeon
 */
public class temptemp {
    
    public static void main(String[] args) throws Exception {
        PlayerList p = new PlayerList("a", "b", "c", "d", "e", "f");
        Game g = new Game(p);

        System.out.println(g.getGameJSON());
//        AttackOutcome ao = Dice.Roll(3, 2);
//        System.out.println(CountriesData.neighboursMap.get("AS01").contains("AS02"));

        


    }
}
