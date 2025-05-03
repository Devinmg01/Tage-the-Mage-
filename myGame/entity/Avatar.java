package myGame.entity;

import myGame.GameClient;
import org.joml.Matrix4f;
import tage.GameObject;
import tage.ObjShape;
import tage.TextureImage;
import tage.shapes.AnimatedShape;

public class Avatar extends GameCharacter {

    // Class Variables
    private static final int DEFAULT_HEALTH = 10;
    private static final long idleDelay = 500; // 0.5 seconds
    private int score = 0;
    private boolean walking = false;
    private long lastInputTime = System.currentTimeMillis();
    private String currentAnimation = "";

    /**
     * Construct GameAvatar object with the specified parameters
     */
    public Avatar(GameObject object, ObjShape shape, TextureImage texture, GameClient game) {
        super(object, shape, texture, game, DEFAULT_HEALTH);
        setLocalScale(new Matrix4f().scaling(0.35f));
        initPhysics(1f, 0.5f, 2f);
    }

    /**
     * @return score of the avatar
     */
    public int getScore() {
        return score;
    }

    /**
     * Set avatar score to a specified amount
     */
    public void setScore(int score) {
        this.score = Math.max(score, 0);
    }

    /**
     * @return the avatar's animated shape
     */
    public AnimatedShape getAnimatedShape() {
        return (AnimatedShape) this.getShape();
    }

    /**
     * @return if the avatar is walking
     */
    public boolean isWalking() {
        return this.walking;
    }

    /**
     * Set whether the avatar is walking
     */
    public void setWalking(boolean walking) {
        this.walking = walking;
    }

    /**
     * @return the time since the last input
     */
    public long getLastInputTime() {
        return lastInputTime;
    }

    /**
     * Update the time since the last input
     */
    public void updateLastInputTime() {
        lastInputTime = System.currentTimeMillis();
    }

    /**
     * @return whether idle animation should play
     */
    public boolean shouldPlayIdle() {
        return (System.currentTimeMillis() - lastInputTime) > idleDelay;
    }

    /**
     * @return the current animation
     */
    public String getCurrentAnimation() {
        return currentAnimation;
    }

    /**
     * Set the current animation
     */
    public void setCurrentAnimation(String animName) {
        this.currentAnimation = animName;
    }

}
