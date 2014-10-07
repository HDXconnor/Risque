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