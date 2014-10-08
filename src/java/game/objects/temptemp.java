package game.objects;

import game.data.CountriesData;
import game.logic.Command;
import game.logic.Dice;
import org.json.JSONObject;

public class temptemp {
    
    public static void main(String[] args) throws Exception {
        PlayerList p = new PlayerList();
        Game g = new Game("this is a game name");
        Game gg = new Game("this is another game name");
        GameList.add(g);
        GameList.add(gg);
        System.out.println(GameList.getGameListJSON());
    }
}