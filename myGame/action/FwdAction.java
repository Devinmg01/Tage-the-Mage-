package myGame.action;

import myGame.utility.ClientManager;
import tage.GameObject;
import tage.input.action.AbstractInputAction;
import net.java.games.input.Event;
import org.joml.*;

public class FwdAction extends AbstractInputAction {
    private GameObject object;
    private ClientManager clientManager;
    private GameObject terrain;
    private boolean reverse;
    private float amount;
    private int ticks = 0;

    public FwdAction(GameObject object, ClientManager clientManager, GameObject terrian, boolean reverse) {
        this.object = object;
        this.clientManager = clientManager;
        this.terrain = terrian;
        this.reverse = reverse;
    }

    public void moveForward(float elapsTime, float amount) {
        Vector3f fwd = object.getWorldForwardVector();
        Vector3f loc = object.getWorldLocation();
        loc.y = loc.y + ((terrain.getHeight(loc.x, loc.z) + 0.1f) - loc.y) * 0.1f;
        Vector3f newLoc = loc.add(fwd.mul(amount * elapsTime));
        object.setLocalLocation(newLoc);
    }

    @Override
    public void performAction(float elapsTime, Event e) {
        if (reverse) {
            amount = -e.getValue();
        } else {
            amount = e.getValue();
        }

        moveForward(elapsTime, amount);

        if (clientManager != null) {
            clientManager.sendMoveMessage(object.getWorldLocation());
        }
    }
}