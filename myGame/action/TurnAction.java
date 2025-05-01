package myGame.action;

import tage.GameObject;
import tage.input.action.AbstractInputAction;
import net.java.games.input.Event;

public class TurnAction extends AbstractInputAction {
    private GameObject object;
    private boolean reverse;
    private float amount;

    public TurnAction(GameObject object, boolean reverse) {
        this.object = object;
        this.reverse = reverse;
    }

    @Override
    public void performAction(float elapsTime, Event e) {
        if (reverse) {
            amount = -e.getValue() * 0.1f;
        } else {
            amount = e.getValue() * 0.1f;
        }
        object.globalYaw(-amount * elapsTime);
    }
}
