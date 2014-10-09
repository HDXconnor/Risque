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