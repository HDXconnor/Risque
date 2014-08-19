/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.data;

import java.util.HashMap;

/**
 *
 * @author Connor
 */
public class CountriesData {

    public static final HashMap<String, String> continentsMap;
    public static final HashMap<String, String> countriesMap;
    public static final HashMap<String, String> neighboursMap;

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

        neighboursMap.put("AS01", "AS02");
        neighboursMap.put("AS01", "AS03");
        neighboursMap.put("AS01", "AS04");
        neighboursMap.put("AS01", "NA01");
        neighboursMap.put("AS02", "AS01");
        neighboursMap.put("AS02", "AS03");
        neighboursMap.put("AS02", "AS04");
        neighboursMap.put("AS02", "AS08");
        neighboursMap.put("AS03", "AS01");
        neighboursMap.put("AS03", "AS02");
        neighboursMap.put("AS03", "AS08");
        neighboursMap.put("AS04", "AS01");
        neighboursMap.put("AS04", "AS02");
        neighboursMap.put("AS04", "AS05");
        neighboursMap.put("AS04", "AS08");
        neighboursMap.put("AS05", "AS04");
        neighboursMap.put("AS05", "AS06");
        neighboursMap.put("AS05", "AS08");
        neighboursMap.put("AS05", "AS09");
        neighboursMap.put("AS05", "EU01");
        neighboursMap.put("AS06", "AS05");
        neighboursMap.put("AS06", "OC01");
        neighboursMap.put("AS07", "AS08");
        neighboursMap.put("AS07", "EU01");
        neighboursMap.put("AS08", "AS02");
        neighboursMap.put("AS08", "AS03");
        neighboursMap.put("AS08", "AS04");
        neighboursMap.put("AS08", "AS05");
        neighboursMap.put("AS08", "AS07");
        neighboursMap.put("AS08", "EU01");
        neighboursMap.put("AS09", "AS05");
        neighboursMap.put("AS09", "AS10");
        neighboursMap.put("AS09", "EU01");
        neighboursMap.put("AS10", "AS09");
        neighboursMap.put("AS10", "AS11");
        neighboursMap.put("AS10", "EU06");
        neighboursMap.put("AS10", "AF01");
        neighboursMap.put("AS10", "AF03");
        neighboursMap.put("AS11", "AS10");
        neighboursMap.put("AS11", "AF03");
        neighboursMap.put("AS11", "AN01");
        neighboursMap.put("AS11", "OC03");

        countriesMap.put("EU01", "Ukraine");
        countriesMap.put("EU02", "Northern Europe");
        countriesMap.put("EU03", "Scandanavia");
        countriesMap.put("EU04", "Great Britain");
        countriesMap.put("EU05", "Western Europe");
        countriesMap.put("EU06", "Southern Europe");

        neighboursMap.put("EU01", "EU02");
        neighboursMap.put("EU01", "EU03");
        neighboursMap.put("EU01", "AS05");
        neighboursMap.put("EU01", "AS07");
        neighboursMap.put("EU01", "AS08");
        neighboursMap.put("EU01", "AS09");
        neighboursMap.put("EU02", "EU01");
        neighboursMap.put("EU02", "EU03");
        neighboursMap.put("EU02", "EU04");
        neighboursMap.put("EU02", "EU05");
        neighboursMap.put("EU03", "EU01");
        neighboursMap.put("EU03", "EU02");
        neighboursMap.put("EU03", "EU04");
        neighboursMap.put("EU03", "NA03");
        neighboursMap.put("EU04", "EU03");
        neighboursMap.put("EU04", "EU05");
        neighboursMap.put("EU04", "NA03");
        neighboursMap.put("EU05", "EU02");
        neighboursMap.put("EU05", "EU04");
        neighboursMap.put("EU05", "EU06");
        neighboursMap.put("EU05", "NA03");
        neighboursMap.put("EU05", "NA04");
        neighboursMap.put("EU05", "AF02");
        neighboursMap.put("EU06", "EU05");
        neighboursMap.put("EU06", "AS10");
        neighboursMap.put("EU06", "AF01");
        neighboursMap.put("EU06", "AF02");

        countriesMap.put("AF01", "Egypt");
        countriesMap.put("AF02", "North Africa");
        countriesMap.put("AF03", "East Africa");
        countriesMap.put("AF04", "Congo");
        countriesMap.put("AF05", "South Africa");

        neighboursMap.put("AF01", "EU06");
        neighboursMap.put("AF01", "AS10");
        neighboursMap.put("AF01", "AF03");
        neighboursMap.put("AF01", "AF02");
        neighboursMap.put("AF02", "NA08");
        neighboursMap.put("AF02", "NA04");
        neighboursMap.put("AF02", "EU05");
        neighboursMap.put("AF02", "EU06");
        neighboursMap.put("AF02", "AF01");
        neighboursMap.put("AF02", "AF03");
        neighboursMap.put("AF02", "AF04");
        neighboursMap.put("AF02", "SA02");
        neighboursMap.put("AF03", "AF01");
        neighboursMap.put("AF03", "AF02");
        neighboursMap.put("AF03", "AF04");
        neighboursMap.put("AF03", "AF05");
        neighboursMap.put("AF03", "AN01");
        neighboursMap.put("AF03", "AS11");
        neighboursMap.put("AF03", "AS10");

        countriesMap.put("OC01", "Indonisia");
        countriesMap.put("OC02", "East Australia");
        countriesMap.put("OC03", "West Australia");

        neighboursMap.put("OC01", "OC03");
        neighboursMap.put("OC01", "AS06");
        neighboursMap.put("OC02", "OC03");
        neighboursMap.put("OC02", "AN01");
        neighboursMap.put("OC02", "AN03");
        neighboursMap.put("OC02", "AS11");
        neighboursMap.put("OC03", "OC01");
        neighboursMap.put("OC03", "OC02");
        neighboursMap.put("OC03", "AN03");

        countriesMap.put("AN01", "Queen Maud Land");
        countriesMap.put("AN02", "South Pole");
        countriesMap.put("AN03", "Wilkes Land");
        countriesMap.put("AN04", "Marie Byrd Land");

        neighboursMap.put("AN01", "AF05");
        neighboursMap.put("AN01", "AF03");
        neighboursMap.put("AN01", "AS11");
        neighboursMap.put("AN01", "OC02");
        neighboursMap.put("AN01", "AN03");
        neighboursMap.put("AN01", "AN02");
        neighboursMap.put("AN01", "AN04");
        neighboursMap.put("AN02", "AN01");
        neighboursMap.put("AN02", "AN03");
        neighboursMap.put("AN02", "AN04");
        neighboursMap.put("AN03", "AN01");
        neighboursMap.put("AN03", "AN02");
        neighboursMap.put("AN03", "OC02");
        neighboursMap.put("AN03", "OC03");
        neighboursMap.put("AN04", "AN01");
        neighboursMap.put("AN04", "AN02");

        countriesMap.put("SA01", "Venezuela");
        countriesMap.put("SA02", "Brazil");
        countriesMap.put("SA03", "Peru");
        countriesMap.put("SA04", "Argentina");

        neighboursMap.put("SA01", "SA02");
        neighboursMap.put("SA01", "SA03");
        neighboursMap.put("SA02", "SA01");
        neighboursMap.put("SA02", "SA03");
        neighboursMap.put("SA02", "SA04");
        neighboursMap.put("SA02", "AF02");
        neighboursMap.put("SA02", "AF05");
        neighboursMap.put("SA02", "AF04");
        neighboursMap.put("SA03", "SA01");
        neighboursMap.put("SA03", "SA02");
        neighboursMap.put("SA03", "SA04");
        neighboursMap.put("SA03", "NA09");
        neighboursMap.put("SA04", "SA03");
        neighboursMap.put("SA04", "SA02");
        neighboursMap.put("SA04", "AF05");

        countriesMap.put("NA01", "Alaska");
        countriesMap.put("NA02", "Northwest Territory");
        countriesMap.put("NA03", "Greenland");
        countriesMap.put("NA04", "Quebec");
        countriesMap.put("NA05", "Ontario");
        countriesMap.put("NA06", "Alberta");
        countriesMap.put("NA07", "Western United States");
        countriesMap.put("NA08", "Eastern United States");
        countriesMap.put("NA09", "Central America");

        neighboursMap.put("NA01", "NA02");
        neighboursMap.put("NA01", "NA06");
        neighboursMap.put("NA01", "AS01");
        neighboursMap.put("NA02", "NA01");
        neighboursMap.put("NA02", "NA03");
        neighboursMap.put("NA02", "NA04");
        neighboursMap.put("NA02", "NA05");
        neighboursMap.put("NA02", "NA06");
        neighboursMap.put("NA03", "NA02");
        neighboursMap.put("NA03", "NA04");
        neighboursMap.put("NA03", "EU03");
        neighboursMap.put("NA03", "EU04");
        neighboursMap.put("NA03", "EU05");
        neighboursMap.put("NA04", "NA02");
        neighboursMap.put("NA04", "NA03");
        neighboursMap.put("NA04", "NA05");
        neighboursMap.put("NA04", "NA08");
        neighboursMap.put("NA04", "EU05");
        neighboursMap.put("NA04", "AF02");
        neighboursMap.put("NA05", "NA02");
        neighboursMap.put("NA05", "NA04");
        neighboursMap.put("NA05", "NA06");
        neighboursMap.put("NA05", "NA07");
        neighboursMap.put("NA05", "NA08");
        neighboursMap.put("NA06", "NA01");
        neighboursMap.put("NA06", "NA02");
        neighboursMap.put("NA06", "NA05");
        neighboursMap.put("NA06", "NA07");
        neighboursMap.put("NA07", "NA06");
        neighboursMap.put("NA07", "NA05");
        neighboursMap.put("NA07", "NA08");
        neighboursMap.put("NA07", "NA09");
        neighboursMap.put("NA08", "NA04");
        neighboursMap.put("NA08", "NA05");
        neighboursMap.put("NA08", "NA07");
        neighboursMap.put("NA08", "NA09");
        neighboursMap.put("NA08", "AF02");
        neighboursMap.put("NA08", "SA01");
        neighboursMap.put("NA09", "NA07");
        neighboursMap.put("NA09", "NA08");
        neighboursMap.put("NA09", "SA03");
    }

}
