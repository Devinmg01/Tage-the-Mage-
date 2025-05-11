package myGame.entity;

import myGame.GameClient;
import org.joml.Matrix4f;
import tage.GameObject;
import tage.ObjShape;
import tage.TextureImage;
import tage.shapes.AnimatedShape;

public class Avatar extends GameCharacter {

    // Class Variables
    private GameClient game;
    private static final long idleDelay = 500; // 0.5 seconds
    private int score = 0;
    private boolean walking = false;
    private long lastInputTime = System.currentTimeMillis();
    private String currentAnimation = "";
    private int healTick = 0;

    /**
     * Construct GameAvatar object with the specified parameters
     */
    public Avatar(GameObject object, ObjShape shape, TextureImage texture, GameClient game) {
        super(object, shape, texture, game, 10);
        this.game = game;
        setLocalScale(new Matrix4f().scaling(0.35f));
        initPhysics(1f, 0.5f, 2f);
    }

    /**
     * @return score of the avatar
     */
    public int getScore() {
        return score;
    }

    public void updateSpotlightColor(int health) {
        if (health >= 7) {
            game.getAvatarLight().setDiffuse(0f, 1f, 0f);
        }
        else if (health >= 4) {
            game.getAvatarLight().setDiffuse(1f, 0.5f, 0f);
        }
        else {
            game.getAvatarLight().setDiffuse(1f, 0f, 0f);
        }

    }

    /**
     * Make the avatar take damage
     */
    public void takeDamage() {
        int health = getHealth();

        updateSpotlightColor(health);

        setHealth(health - 1);
    }

    /**
     * Heal the avatar
     */
    public void heal() {
        int health = getHealth();

        if (healTick == 0) {
            if (health < 10) {
                setHealth(health + 1);
            }
        }
        healTick++;
        if (healTick == 25) {
            healTick = 0;
        }

        updateSpotlightColor(health);
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
