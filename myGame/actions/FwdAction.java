

package myGame.actions;

import myGame.network.ClientManager;
import myGame.entities.Avatar;
import tage.GameObject;
import tage.input.action.AbstractInputAction;
import net.java.games.input.Event;
import org.joml.*;
import tage.shapes.AnimatedShape;

public class FwdAction extends AbstractInputAction {
    private GameObject gameObject;
    private ClientManager clientManager;
    private boolean reverse;
    private float amount;

    public FwdAction(GameObject gameObject, ClientManager clientManager, boolean reverse) {
        this.gameObject = gameObject;
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
        Vector3f fwd = gameObject.getWorldForwardVector();
        Vector3f loc = gameObject.getWorldLocation();
        Vector3f newLoc = loc.add(fwd.mul(amount * elapsTime));
        gameObject.setLocalLocation(newLoc);

         if (clientManager != null) {
            clientManager.sendMoveMessage(gameObject.getWorldLocation());
        }

         // Animation logic only for Avatar
        if (gameObject instanceof Avatar) {
            Avatar avatar = (Avatar) gameObject;

            // Mark walking state and update input time
            avatar.setWalking(true);
            avatar.updateLastInputTime();

            // Only plays if not already playing WALKING
            if (!"WALKING".equals(avatar.getCurrentAnimation())) {
                avatar.getAnimatedShape().playAnimation("WALKING", 0.33f, AnimatedShape.EndType.LOOP, 0);
                avatar.setCurrentAnimation("WALKING");
            }
        }
    }
}

