package myGame.actions;

import myGame.network.ClientManager;
import tage.GameObject;
import tage.input.action.AbstractInputAction;
import net.java.games.input.Event;
import org.joml.*;

public class FwdAction extends AbstractInputAction {
    private GameObject avatar;
    private ClientManager clientManager;
    private boolean reverse;
    private float amount;

    public FwdAction(GameObject avatar, ClientManager clientManager, boolean reverse) {
        this.avatar = avatar;
        this.clientManager = clientManager;
        this.reverse = reverse;
    }

    @Override
    public void performAction(float elapsTime, Event e) {
        if (reverse) {
            amount = -e.getValue();
        } else {
            amount = e.getValue();
        }
        Vector3f fwd = avatar.getWorldForwardVector();
        Vector3f loc = avatar.getWorldLocation();
        Vector3f newLoc = loc.add(fwd.mul(amount * elapsTime));
        avatar.setLocalLocation(newLoc);
        if (clientManager != null) {
            clientManager.sendMoveMessage(avatar.getWorldLocation());
        }
    }
}