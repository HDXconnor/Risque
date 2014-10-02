package game.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * CountriesData class contains hash maps containing all map identification information for the
 * countries in the current map. The hash maps that can be accessed include:
 * <ul>
 * <li>countriesMap</li>
 * <li>neighboursMap</li>
 * <li>continentsMap</li>
 * </ul>
 * <p>
 * The countriesMap keys being each country's 4-digit code and value being the country's name.
 * The neighboursMap key-value pairs indicate countries that border each other on the map.
 * The continentsMap contains the names of the continents with the keys being the continent's
 * 2-digit code and value being the continent's full name.
 * </p>
 * <p>
 * The 4-digit country codes contain as their firt two digits the code corresponding to the
 * continent they each belong to. Thus, this code can be parsed to calculate contiental bonuses.
 * </p>
 */
public class CountriesData {
    public static final HashMap<String, String> countriesMap;
    public static final HashMap<String, ArrayList<String>> neighboursMap;
    public static final HashMap<String, String> continentsMap;

    static {
        continentsMap = new HashMap<>();
        countriesMap = new HashMap<>();
        neighboursMap = new HashMap<>();

        continentsMap.put("NA", "North America");
        continentsMap.put("SA", "South America");
        continentsMap.put("AF", "Africa");
        continentsMap.put("AN", "Antarctica");
        continentsMap.put("AS", "Asia");
        continentsMap.put("EU", "Europe");
        continentsMap.put("OC", "Oceania");
        
        // ASIA
        countriesMap.put("AS01", "Kamchatka");
        countriesMap.put("AS02", "Irkutsk");
        countriesMap.put("AS03", "Ykutsk");
        countriesMap.put("AS04", "Mongolia");
        countriesMap.put("AS05", "China");
        countriesMap.put("AS06", "Siam");
        countriesMap.put("AS07", "Ural");
        countriesMap.put("AS08", "Siberia");
        countriesMap.put("AS09", "Afghanistan");
        countriesMap.put("AS10", "Middle East");
        countriesMap.put("AS11", "India");
        neighboursMap.put("AS01", new ArrayList(Arrays.asList("AS02", "AS03", "AS04", "NA01")));
        neighboursMap.put("AS02", new ArrayList(Arrays.asList("AS01", "AS03", "AS04", "AS08")));
        neighboursMap.put("AS03", new ArrayList(Arrays.asList("AS01", "AS02", "AS08")));
        neighboursMap.put("AS04", new ArrayList(Arrays.asList("AS01", "AS02", "AS05", "AS08")));
        neighboursMap.put("AS05", new ArrayList(Arrays.asList("AS04", "AS06", "AS08", "AS09", "EU01")));
        neighboursMap.put("AS06", new ArrayList(Arrays.asList("AS05", "OC01")));
        neighboursMap.put("AS07", new ArrayList(Arrays.asList("AS08", "EU01")));
        neighboursMap.put("AS08", new ArrayList(Arrays.asList("AS02", "AS03", "AS04", "AS05", "AS07", "EU01")));
        neighboursMap.put("AS09", new ArrayList(Arrays.asList("AS05", "AS10", "EU01")));
        neighboursMap.put("AS10", new ArrayList(Arrays.asList("AS09", "AS11", "EU06", "AF01", "AF03")));
        neighboursMap.put("AS11", new ArrayList(Arrays.asList("AS10", "AF03", "AN01", "OC03")));

        // EUROPE
        countriesMap.put("EU01", "Ukraine");
        countriesMap.put("EU02", "Northern Europe");
        countriesMap.put("EU03", "Scandanavia");
        countriesMap.put("EU04", "Great Britain");
        countriesMap.put("EU05", "Western Europe");
        countriesMap.put("EU06", "Southern Europe");
        neighboursMap.put("EU01", new ArrayList(Arrays.asList("EU02", "EU03", "AS05", "AS07", "AS08", "AS09")));
        neighboursMap.put("EU02", new ArrayList(Arrays.asList("EU01", "EU03", "EU04", "EU05")));
        neighboursMap.put("EU03", new ArrayList(Arrays.asList("EU01", "EU02", "EU04", "NA03")));
        neighboursMap.put("EU04", new ArrayList(Arrays.asList("EU03", "EU05", "NA03")));
        neighboursMap.put("EU05", new ArrayList(Arrays.asList("EU02", "EU04", "EU06", "NA03", "NA04", "AF02")));
        neighboursMap.put("EU06", new ArrayList(Arrays.asList("EU05", "AS10", "AF01", "AF02")));
        
        // AFRICA
        countriesMap.put("AF01", "Egypt");
        countriesMap.put("AF02", "North Africa");
        countriesMap.put("AF03", "East Africa");
        countriesMap.put("AF04", "Congo");
        countriesMap.put("AF05", "South Africa");
        neighboursMap.put("AF01", new ArrayList(Arrays.asList("EU06", "AS10", "AF03", "AF02")));
        neighboursMap.put("AF02", new ArrayList(Arrays.asList("NA08", "NA04", "EU05", "EU06", "AF01", "AF03", "AF04", "SA02")));
        neighboursMap.put("AF03", new ArrayList(Arrays.asList("AF01", "AF02", "AF04", "AF05", "AN01", "AS11", "AS10")));
        neighboursMap.put("AF04", new ArrayList(Arrays.asList("AF03", "AF02", "SA02")));
        neighboursMap.put("AF05", new ArrayList(Arrays.asList("AF03", "AN01", "SA02", "SA04")));

        // OCEANIA
        countriesMap.put("OC01", "Indonisia");
        countriesMap.put("OC02", "East Australia");
        countriesMap.put("OC03", "West Australia");
        neighboursMap.put("OC01", new ArrayList(Arrays.asList("OC03", "AS06")));
        neighboursMap.put("OC02", new ArrayList(Arrays.asList("OC03", "AN01", "AN03", "AS11")));
        neighboursMap.put("OC03", new ArrayList(Arrays.asList("OC01", "OC02", "AN03")));
        
        // ANTARCTICA 
        countriesMap.put("AN01", "Queen Maud Land");
        countriesMap.put("AN02", "South Pole");
        countriesMap.put("AN03", "Wilkes Land");
        countriesMap.put("AN04", "Marie Byrd Land");
        neighboursMap.put("AN01", new ArrayList(Arrays.asList("AF05", "AF03", "AS11", "OC02", "AN03", "AN02", "AN04")));
        neighboursMap.put("AN02", new ArrayList(Arrays.asList("AN01", "AN03", "AN04")));
        neighboursMap.put("AN03", new ArrayList(Arrays.asList("AN01", "AN02", "OC02", "OC03")));
        neighboursMap.put("AN04", new ArrayList(Arrays.asList("AN01", "AN02")));

        // SOUTH AMERICA
        countriesMap.put("SA01", "Venezuela");
        countriesMap.put("SA02", "Brazil");
        countriesMap.put("SA03", "Peru");
        countriesMap.put("SA04", "Argentina");
        neighboursMap.put("SA01", new ArrayList(Arrays.asList("SA02", "SA03")));
        neighboursMap.put("SA02", new ArrayList(Arrays.asList("SA01", "SA03", "SA04", "AF02", "AF05", "AF04")));
        neighboursMap.put("SA03", new ArrayList(Arrays.asList("SA01", "SA02", "SA04", "NA09")));
        neighboursMap.put("SA04", new ArrayList(Arrays.asList("SA03", "SA02", "AF05")));

        // 'MURRICA
        countriesMap.put("NA01", "Alaska");
        countriesMap.put("NA02", "Northwest Territory");
        countriesMap.put("NA03", "Greenland");
        countriesMap.put("NA04", "Quebec");
        countriesMap.put("NA05", "Ontario");
        countriesMap.put("NA06", "Alberta");
        countriesMap.put("NA07", "Western United States");
        countriesMap.put("NA08", "Eastern United States");
        countriesMap.put("NA09", "Central America");
        neighboursMap.put("NA01", new ArrayList(Arrays.asList("NA02", "NA06", "AS01")));
        neighboursMap.put("NA02", new ArrayList(Arrays.asList("NA01", "NA03", "NA04", "NA05", "NA06")));
        neighboursMap.put("NA03", new ArrayList(Arrays.asList("NA02", "NA04", "EU03", "EU04", "EU05")));
        neighboursMap.put("NA04", new ArrayList(Arrays.asList("NA02", "NA03", "NA05", "NA08", "EU05", "AF02")));
        neighboursMap.put("NA05", new ArrayList(Arrays.asList("NA02", "NA04", "NA06", "NA07", "NA08")));
        neighboursMap.put("NA06", new ArrayList(Arrays.asList("NA01", "NA02", "NA05", "NA07")));
        neighboursMap.put("NA07", new ArrayList(Arrays.asList("NA06", "NA05", "NA08", "NA09")));
        neighboursMap.put("NA08", new ArrayList(Arrays.asList("NA04", "NA05", "NA07", "NA09", "AF02", "SA01")));
        neighboursMap.put("NA09", new ArrayList(Arrays.asList("NA07", "NA08", "SA03")));
    }

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
        return neighboursMap.get(country1).contains(country2);
    }
}