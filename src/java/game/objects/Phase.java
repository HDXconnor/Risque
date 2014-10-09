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

public class Phase {
    private String currentPhase;
    public static final String SETUP = "Setup";
    public static final String DEPLOY = "Deploy";
    public static final String ATTACK = "Attack";
    public static final String MOVE = "Move";
    
    public Phase() {
        this.currentPhase = SETUP;
    }

    /**
     * Returns the current phase name.
     *
     * @return the current phase string.
     */
    public String getPhase() {
        return currentPhase;
    }

    /**
     * Increments the current phase to the next one.
     */
    public void nextPhase() {
        switch (currentPhase) {
            case SETUP:
                this.currentPhase = DEPLOY;
                break;
            case DEPLOY:
                this.currentPhase = ATTACK;
                break;
            case ATTACK:
                this.currentPhase = MOVE;
                break;
            case MOVE:
                this.currentPhase = DEPLOY;
                break;
            default:
                break;
        }
    }
}