package myGame.actions;

import net.java.games.input.Event;
import tage.input.action.AbstractInputAction;
import myGame.entities.Avatar;
import tage.shapes.AnimatedShape;

public class StopWalkAction extends AbstractInputAction {
	private Avatar avatar;

	public StopWalkAction(Avatar avatar) {
		this.avatar = avatar;
	}

	@Override
	public void performAction(float time, Event e) {
		// Only on key release
		if (Math.abs(e.getValue()) < 0.1f && avatar.isWalking()) {
			avatar.setWalking(false);
			avatar.getAnimatedShape().playAnimation("IDLE", 0.5f, AnimatedShape.EndType.LOOP, 0);
		}
	}
}
