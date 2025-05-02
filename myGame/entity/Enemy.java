package myGame.entity;

import myGame.GameClient;
import myGame.action.FwdAction;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import tage.GameObject;
import tage.ObjShape;
import tage.TextureImage;

import java.util.UUID;

public class Enemy extends GameCharacter {

    // Class Variables
    private GameClient game;
    private static final int DEFAULT_HEALTH = 2;
    private static final float DEFAULT_SPEED = 0.25f;
    private UUID id;
    private Vector3f targetLoc;
    private FwdAction fwdAction;

    /**
     * Construct Enemy object with the specified parameters
     */
    public Enemy(UUID id, GameObject object, ObjShape shape, TextureImage texture, GameClient game,
                 Vector3f spawnLoc, Vector3f targetLoc) {
        super(object, shape, texture, game, DEFAULT_HEALTH, DEFAULT_SPEED);
        this.id = id;
        this.game = game;
        this.targetLoc = targetLoc;
        this.fwdAction = new FwdAction(this, game.getClientManager(), game.getTerrain(), game, false);
        setLocalLocation(spawnLoc);
        setLocalScale(new Matrix4f().scaling(0.25f));
        initPhysics(1f, 0.5f, 2f);
        lookAt(targetLoc);
    }

    /**
     * Update the enemy's position towards the target
     */
    public void move(float elapsedTime) {
        if (getLocalLocation().distance(targetLoc) >= 5.5f &&
            !checkCollision(game.getAvatar())) {
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
