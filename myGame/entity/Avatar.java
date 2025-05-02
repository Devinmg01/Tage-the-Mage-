package myGame.entity;

import org.joml.Matrix4f;
import tage.GameObject;
import tage.ObjShape;
import tage.TextureImage;
import tage.shapes.AnimatedShape;

public class Avatar extends GameCharacter {

    // Class Variables
    private static final int DEFAULT_HEALTH = 10;
    private static final float DEFAULT_SPEED = 1.5f;
    private static final int DEFAULT_ATTACK_POWER = 1;
    private static final float DEFAULT_ATTACK_SPEED = 1.0f;
    private static final long idleDelay = 500; // 0.5 seconds

    private int attackPower = DEFAULT_ATTACK_POWER;
    private float attackSpeed = DEFAULT_ATTACK_SPEED;
    private int score = 0;
    private boolean walking = false;
    private long lastInputTime = System.currentTimeMillis();
    private String currentAnimation = "";

    /**
     * Construct GameAvatar object with the specified parameters
     */
    public Avatar(GameObject object, ObjShape shape, TextureImage texture) {
        super(object, shape, texture, DEFAULT_HEALTH, DEFAULT_SPEED);
        setLocalScale(new Matrix4f().scaling(0.35f));
    }

    /**
     * @return attack power of the avatar
     */
    public int getAttackPower() {
        return attackPower;
    }

    /**
     * Set avatar attack power to a specified amount
     */
    public void setAttackPower(int amount) {
        this.attackPower = Math.max(attackPower, DEFAULT_ATTACK_POWER);
    }

    /**
     * @return attack speed of the avatar
     */
    public float getAttackSpeed() {
        return attackSpeed;
    }

    /**
     * Set avatar attack speed to a specified amount
     */
    public void setAttackSpeed(float amount) {
        this.attackSpeed = Math.max(attackSpeed, DEFAULT_ATTACK_SPEED);
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
