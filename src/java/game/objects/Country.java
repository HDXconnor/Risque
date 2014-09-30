/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package game.objects;

/**
 *
 * @author Simeon
 */
public class Country {
    private int troops;
    private int owner;
    private String name;
    //private String id;
    
    public Country(String name) {
        this.name = name;
        //this.id = id;
        this.troops = 0;
        this.owner = -1;
    }
    
    public void setOwner(int owner) {
        this.owner = owner;
    }
    
    public int getOwner() {
        return owner;
    }
    
    public void setTroops(int troops) {
        this.troops = troops;
    }
    
    public void incrementTroops() {
        this.troops++;
    }

    public void removeTroops(int troops) {
        this.troops -= troops;
    }

    public int getTroops() {
        return troops;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

//    public String getId() {
//        return id;
//    }
//
//    public void setId(String id) {
//        this.id = id;
//    }
    
    
}
