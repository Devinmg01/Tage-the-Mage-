package myGame.actions;

import tage.input.action.AbstractInputAction;
import net.java.games.input.Event;

public class ExitGameAction extends AbstractInputAction {

    public ExitGameAction() {
    }

    @Override
    public void performAction(float elapsTime, Event e) {
        System.exit(0);
    }
}
