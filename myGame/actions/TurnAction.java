package myGame.actions;

import tage.GameObject;
import tage.input.action.AbstractInputAction;
import net.java.games.input.Event;

public class TurnAction extends AbstractInputAction {
    private GameObject avatar;
    private boolean reverse;
    private float amount;

    public TurnAction(GameObject avatar, boolean reverse) {
        this.avatar = avatar;
        this.reverse = reverse;
    }

    @Override
    public void performAction(float elapsTime, Event e) { 
        if (reverse) {
            amount = -e.getValue() * 0.1f;
        } else {
            amount = e.getValue() * 0.1f;
        }
        avatar.globalYaw(amount * elapsTime);
	}
}

