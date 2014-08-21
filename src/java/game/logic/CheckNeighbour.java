/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package game.logic;

import game.data.CountriesData;

/**
 *
 * @author Simeon
 */
public class CheckNeighbour {
    
    public static boolean isNeighbour(String country1, String country2) {
        return CountriesData.neighboursMap.get(country1).contains(country2);
    }
}
