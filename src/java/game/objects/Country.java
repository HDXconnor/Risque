/**
 * Copyright 2014 Connor Anderson

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
package game.objects;

public class Country {
    private int troops;
    private int owner;
    private String name;
    
    public Country(String name) {
        this.name = name;
        this.troops = 0;
        this.owner = -1;
    }
    
    public void setOwner(int owner) {
        this.owner = owner;
    }
    
    public int getOwner() {
        return owner;
    }

    public boolean hasOwner(){
        return (getOwner() != -1);
    }

    public boolean isOwnedBy(int player){
        return (getOwner() == player);
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
}