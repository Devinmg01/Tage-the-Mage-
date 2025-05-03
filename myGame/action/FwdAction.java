package myGame.action;

import myGame.GameClient;
import myGame.entity.Enemy;
import myGame.utility.ClientManager;
import myGame.entity.Avatar;
import myGame.entity.GameCharacter;
import tage.GameObject;
import tage.input.action.AbstractInputAction;
import tage.shapes.AnimatedShape;
import net.java.games.input.Event;
import org.joml.*;
import tage.audio.*;


public class FwdAction extends AbstractInputAction {
    private GameClient game;
    private GameCharacter object;
    private ClientManager clientManager;
    private GameObject terrain;
    private boolean reverse;
    private float amount;
    private int ticks = 0;
    private Sound walkSound;

    public FwdAction(GameCharacter object, ClientManager clientManager, GameObject terrain, GameClient game, boolean reverse, Sound walkSound) {
        this.object = object;
        this.game = game;
        this.clientManager = clientManager;
        this.terrain = terrain;
        this.reverse = reverse;
        this.walkSound = walkSound;
    }

    public void moveForward(float elapsTime, float amount) {
        Vector3f fwd = object.getWorldForwardVector();
        Vector3f loc = object.getWorldLocation();
        Vector3f newLoc = loc.add(fwd.mul(amount * elapsTime));

        // Move game object
        float terrainY = terrain.getHeight(newLoc.x, newLoc.z);
        object.setLocalLocation(new Vector3f(newLoc.x, terrainY, newLoc.z));

        // Move physics object
        float physHalf = object.getPhysicsBodyHalfHeight();
        Matrix4f physMat = new Matrix4f().translation(new Vector3f(newLoc.x, terrainY + physHalf, newLoc.z));
        object.getPhysicsBody().setTransform(game.toDoubleArray(physMat.get(new float[16])));
    }

    public void animate() {
        Avatar avatar = (Avatar) object;

        // Mark walking state and update input time
        avatar.setWalking(true);
        avatar.updateLastInputTime();

        // Only plays if not already playing WALKING
        if (!"WALKING".equals(avatar.getCurrentAnimation())) {
            avatar.getAnimatedShape().playAnimation("WALKING", 0.5f, AnimatedShape.EndType.LOOP, 0);
            avatar.setCurrentAnimation("WALKING");
        }
    }

    @Override
    public void performAction(float elapsTime, Event e) {
        if (reverse) {
            amount = -e.getValue();
        } else {
            amount = e.getValue();
        }

        moveForward(elapsTime, amount);

        if (object instanceof Avatar) {
            animate();
        }

        if (clientManager != null) {
            clientManager.sendMoveMessage(object.getWorldLocation());
        }
    }
}