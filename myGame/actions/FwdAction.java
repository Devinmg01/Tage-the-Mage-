
package myGame.actions;

import myGame.network.ClientManager;
import myGame.entities.Avatar;
import tage.input.action.AbstractInputAction;
import net.java.games.input.Event;
import org.joml.*;
import tage.shapes.AnimatedShape;


public class FwdAction extends AbstractInputAction {
    private Avatar avatar;
    private ClientManager clientManager;
    private boolean reverse;
    private float amount;

    public FwdAction(Avatar avatar, ClientManager clientManager, boolean reverse) {
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

        // Animation control
         // Marks that the avatar is walking and update the idle timer
            avatar.setWalking(true);
            avatar.updateLastInputTime();

        
        if (!"WALKING".equals(avatar.getCurrentAnimation())) {
            avatar.getAnimatedShape().playAnimation("WALKING", 0.33f, AnimatedShape.EndType.LOOP, 0);
            avatar.setCurrentAnimation("WALKING");
        }

            

    }
}





