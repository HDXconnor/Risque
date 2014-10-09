package game.logic;

import game.data.CountriesData;

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
}