/**
 * Copyright 2014 Team Awesome Productions

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */
package game.logic;

import game.data.CountriesData;
import game.objects.Board;
import game.objects.Country;
import game.objects.Player;
import java.util.HashMap;

public class BoardLogic {
    
    /**
     * Returns whether or not two countries are neighbors.
     * <p>
     * Checks if the neighboursMap contains a key equal to country1
     * with a value equal to country2. If there is no key-value pair equal to the
     * two provided countries, the method will return false.
     * </p>
     *
     * @param country1  the 4 digit country code
     * @param country2  the 4 digit country code
     * @return          <code>true</code> if the two countries
     *                  are neighbors;
     *                  <code>false</code> otherwise.
     */
    public static boolean isNeighbour(String country1, String country2) {
        return CountriesData.neighboursMap.get(country1).contains(country2);
    }
    
    public static boolean checkWinner(Player player, Board board) {
        HashMap<String, Country> countries = board.getAllCountries();
        for (Country country : countries.values()) {
            if (country.getOwner() != player.getPlayerNum()) {
                return false;
            }
        }
        return true;
    }
}