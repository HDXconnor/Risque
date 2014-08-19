/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package game.objects;

import game.data.CountriesData;
import java.util.ArrayList;
import java.util.HashMap;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author Simeon
 */
public class Board {
    HashMap<String, Country> countries;
    
    public Board() {
        countries = new HashMap<>();
        for (String c:CountriesData.countriesMap.keySet()) {
            countries.put(c, new Country(CountriesData.countriesMap.get(c)));
        }
        
    }
    
    public HashMap getAllCountries() {
        return this.countries;
    }
    
    public Country getCountry(String id) {
        return countries.get(id);
    }
    
    // check if every country has an owner => game can then start. otherwise, continue picking phase
    public boolean boardInitialised() {
        for (Country c:countries.values()) {
            if (c.getOwner() < 0) return false;
        }
        return true;
    }
    
    public JSONArray getBoardJSON() throws JSONException {
        JSONArray arr = new JSONArray();
        for (String key:countries.keySet()) {
            JSONObject json = new JSONObject();
            json.put("CountryID", key);
            json.put("CountryName", countries.get(key).getName());
            json.put("Owner", countries.get(key).getOwner());
            json.put("Troops", countries.get(key).getTroops());
            arr.put(json);
        }
        return arr;
    }
    
}
