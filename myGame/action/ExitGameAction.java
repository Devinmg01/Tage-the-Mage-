package myGame.action;

import myGame.GameClient;
import tage.input.action.AbstractInputAction;
import net.java.games.input.Event;

public class ExitGameAction extends AbstractInputAction {
    GameClient game;

    public ExitGameAction(GameClient game) {
        this.game = game;
    }

    @Override
    public void performAction(float elapsTime, Event e) {
        if (game.getClientManager() != null) {
            game.getClientManager().sendByeMessage();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
        System.exit(0);
    }
}
