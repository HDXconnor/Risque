package game.objects;

import game.data.CountriesData;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class Board {
    HashMap<String, Country> countriesMap;
    
    public Board() {
        countriesMap = new HashMap<>();
        for (String countries : CountriesData.countriesMap.keySet()) {
            countriesMap.put(countries, new Country(CountriesData.countriesMap.get(countries)));
        }    
    }

    /**
     * Returns a HashMap containing all of the countriesMap in their current
     * states.
     *
     * @return  HashMap of current countriesMap
     */
    public HashMap getAllCountries() {
        return this.countriesMap;
    }

    /**
     * Returns a country in it's current state by its specified id.
     *
     * @param id    4 digit country id
     * @return      country object
     */
    public Country getCountry(String id) {
        return countriesMap.get(id);
    }

    /**
     * Returns the number of countriesMap without an owner.
     * <p>
     * Iterates through all of the countriesMap in the HashMap counting
     * all countriesMap with an owner value of -1.
     * </p>
     *
     * @return      number of unowned countriesMap
     */
    public int getUnassigned() {
        int unassigned = 0;
        for (Country c : countriesMap.values()) {
            if (c.getOwner() < 0) unassigned++;
        }
        return unassigned;
    }

    /**
     * Generates a JSONArray containing each country's current state
     * in JSONObjects. JSON keys include:
     * <ul>
     * <li>CountryID</li><li>CountryName</li>
     * <li>Owner</li><li>Troops</li>
     * </ul>
     *
     * @return      JSONArray containing each country state
     * @throws JSONException
     */
    public JSONArray getBoardJSON() throws JSONException {
        JSONArray jarray = new JSONArray();
        for (String key : countriesMap.keySet()) {
            JSONObject json = new JSONObject();
            json.put("CountryID", key);
            json.put("CountryName", countriesMap.get(key).getName());
            json.put("Owner", countriesMap.get(key).getOwner());
            json.put("Troops", countriesMap.get(key).getTroops());
            jarray.put(json);
        }
        return jarray;
    }
}