package myGame.entity;

import myGame.GameClient;
import myGame.action.FwdAction;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import tage.GameObject;
import tage.ObjShape;
import tage.TextureImage;
import java.util.UUID;

import tage.shapes.AnimatedShape;

public class Enemy extends GameCharacter {

    // Class Variables
    private GameClient game;
    private static final int DEFAULT_HEALTH = 2;
    private static final float DEFAULT_SPEED = 0.25f;
    private UUID id;
    private Vector3f targetLoc;
    private FwdAction fwdAction;
    private boolean isDead = false;
    private boolean isAttackingTower = false;

    /**
     * Construct Enemy object with the specified parameters
     */
    public Enemy(UUID id, GameObject object, ObjShape shape, TextureImage texture, GameClient game,
                 Vector3f spawnLoc, Vector3f targetLoc) {
        super(object, shape, texture, game, DEFAULT_HEALTH);
        this.id = id;
        this.game = game;
        this.targetLoc = targetLoc;
        this.fwdAction = new FwdAction(this, game, false);
        setLocalLocation(spawnLoc);
        setLocalScale(new Matrix4f().scaling(0.25f));
        initPhysics(1f, 0.5f, 2f);
        lookAt(targetLoc);
    }

    /**
     * Update the enemy's position towards the target
     */
    public void move(float elapsedTime) {

        if (getLocalLocation().distance(targetLoc) >= 5f && !checkCollision(game.getAvatar())) {
            fwdAction.moveForward(elapsedTime, DEFAULT_SPEED);
        }

        if (getLocalLocation().distance(targetLoc) <= 5f && !isAttackingTower) {
            isAttackingTower = true;
        }

        if (checkCollision(game.getAvatar()) && game.getAvatar().getHealth() > 0) {
            game.getAvatar().takeDamage();
            markAsDead();
        }

        if (!"WALKING".equals(getCurrentAnimation())) {
            getAnimatedShape().playAnimation("WALKING", 0.15f, AnimatedShape.EndType.LOOP, 0);
            setCurrentAnimation("WALKING");
        }
    }
    /**
     * Returns the AnimatedShape by casting the shape
     */
    public AnimatedShape getAnimatedShape() {
        return (AnimatedShape) this.getShape();
    }

    /**
     * Returns the current animation name
     */
    public String getCurrentAnimation() {
        return currentAnimation;
    }

    /**
     * Sets the current animation name
     */
    public void setCurrentAnimation(String animName) {
        this.currentAnimation = animName;
    }

    /**
     * @return id of the enemy
     */
    public UUID getId() {
        return id;
    }

    /**
     * @return if the enemy is attacking a tower
     */
    public boolean isAttackingTower() {
        return isAttackingTower;
    }

    /**
     * Mark the enemy as dead
     */
    public void markAsDead() {
        isDead = true;
    }

    /**
     * @return is the enemy is dead
     */
    public boolean isDead() {
        return isDead;
    }
}
