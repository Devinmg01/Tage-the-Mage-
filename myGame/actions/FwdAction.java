/*package myGame.actions;

import myGame.network.ClientManager;
import tage.GameObject;
import tage.input.action.AbstractInputAction;
import net.java.games.input.Event;
import org.joml.*;
import myGame.entities.Avatar;
import tage.shapes.AnimatedShape;



public class FwdAction extends AbstractInputAction {
	private Avatar avatar;
	private ClientManager clientManager;
	private boolean reverse;
	private float amount = 0f;
	private boolean isWalking = false;

	public FwdAction(Avatar avatar, ClientManager clientManager, boolean reverse) {
		this.avatar = avatar;
		this.clientManager = clientManager;
		this.reverse = reverse;
	}

	@Override
	public void performAction(float time, Event e) {
		// Start walk animation if not already walking
		if (!avatar.isWalking()) {
            avatar.updateLastInputTime();
			avatar.setWalking(true);
			avatar.getAnimatedShape().playAnimation("WALKING", 0.5f, AnimatedShape.EndType.LOOP, 0);
		}

		// Movement direction (forward or backward)
		float direction = reverse ? -1f : 1f;
		float speed = avatar.getSpeed() * direction;

		// Get forward vector
		Vector3f fwd = avatar.getWorldForwardVector();
		fwd.y = 0; // Keep movement horizontal
		fwd.normalize();

		// Update position
		Vector3f loc = avatar.getWorldLocation();
		Vector3f newLoc = loc.add(fwd.mul(speed * time, new Vector3f()));

		avatar.setLocalLocation(newLoc);
        


		if (clientManager != null)
			clientManager.sendMoveMessage(avatar.getWorldLocation());
	}
}
*/
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
         // Mark that the avatar is walking and update the idle timer
            avatar.setWalking(true);
            avatar.updateLastInputTime();

        
        if (!"WALKING".equals(avatar.getCurrentAnimation())) {
            avatar.getAnimatedShape().playAnimation("WALKING", 0.33f, AnimatedShape.EndType.LOOP, 0);
            avatar.setCurrentAnimation("WALKING");
        }

            

    }
}





