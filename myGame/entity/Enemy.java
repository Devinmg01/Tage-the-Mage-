package myGame.entity;

import myGame.action.FwdAction;
import myGame.utility.ClientManager;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import tage.GameObject;
import tage.ObjShape;
import tage.TextureImage;
import java.util.UUID;

public class Enemy extends GameCharacter {

    // Class Variables
    private static final int DEFAULT_HEALTH = 2;
    private static final float DEFAULT_SPEED = 0.25f;
    private UUID id;
    private Vector3f targetLoc;
    private FwdAction fwdAction;

    /**
     * Construct Enemy object with the specified parameters
     */
    public Enemy(UUID id, GameObject object, ObjShape shape, TextureImage texture, Vector3f spawnLoc,
            Vector3f targetLoc, GameObject terrain, ClientManager clientManager) {
        super(object, shape, texture, DEFAULT_HEALTH, DEFAULT_SPEED);
        this.id = id;
        this.targetLoc = targetLoc;
        this.fwdAction = new FwdAction(this, clientManager, terrain, false);
        setLocalLocation(spawnLoc);
        setLocalScale(new Matrix4f().scaling(0.25f));
    }

    /**
     * Update the enemy's position towards the target
     */
    public void move(float elapsedTime) {
        if (getLocalLocation().distance(targetLoc) >= 5.5f) {
            lookAt(targetLoc);
            fwdAction.moveForward(elapsedTime, getSpeed());
        }
    }

    /**
     * @return id of the enemy
     */
    public UUID getId() {
        return id;
    }
}
