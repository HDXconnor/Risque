package game.objects;

import game.data.CountriesData;
import game.logic.Command;
import game.logic.Dice;
import org.json.JSONObject;

public class temptemp {
    
    public static void main(String[] args) throws Exception {
        PlayerList p = new PlayerList();
        Game g = new Game("fuckyou");
        Game gg = new Game("nigga");
        System.out.println(g.getGameID());
        System.out.println(gg.getGameID());
        System.out.println(g.getGameJSON());
        JSONObject j = new JSONObject().put("Command", "Setup").put("Data", new JSONObject().put("CountryClicked", "AF02").put("CurrentPlayer", 1));
        //Command.parseInput(j, g);
        System.out.println(g.getGameJSON());
    }
}