package myGame.entity;

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
    public AnimatedShape getAnimatedShape() {
    return (AnimatedShape) this.getShape();
    }

    

    public boolean isWalking() {
        return this.walking;
    }

    public void setWalking(boolean walking) {
        this.walking = walking;
    }

    public void updateLastInputTime() {
        lastInputTime = System.currentTimeMillis();
    }

    public long getLastInputTime() {
        return lastInputTime;
    }


    public boolean shouldPlayIdle() {
        return (System.currentTimeMillis() - lastInputTime) > idleDelay;
    }

    

    public String getCurrentAnimation() {
        return currentAnimation;
    }

    public void setCurrentAnimation(String animName) {
        this.currentAnimation = animName;
    }





}
