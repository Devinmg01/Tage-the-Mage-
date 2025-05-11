package myGame.action;

import myGame.GameClient;
import myGame.entity.Enemy;
import myGame.entity.GameCharacter;
import myGame.entity.GhostAvatar;
import net.java.games.input.Event;
import tage.Light;
import tage.input.action.AbstractInputAction;

public class ToggleSpotlightsAction extends AbstractInputAction {
    private GameClient game;
    private boolean spotlightEnabled = true;

    public ToggleSpotlightsAction(GameClient game) {
        this.game = game;
    }

    private void toggleSpotlight(Light light) {
        if (spotlightEnabled) {
            game.getEngine().getSceneGraph().addLight(light);
        } else {
            game.getEngine().getSceneGraph().removeLight(light);
        }
    }

    @Override
    public void performAction(float elapsTime, Event e) {
        spotlightEnabled = !spotlightEnabled;

        toggleSpotlight(game.getAvatarLight());
        toggleSpotlight(game.getTowerLight());
        toggleSpotlight(game.getHealingLight());
    }
}

