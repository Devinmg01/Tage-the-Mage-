package myGame.entities;

import tage.GameObject;
import tage.ObjShape;
import tage.TextureImage;

public class Avatar extends GameCharacter {

    // Class Variables
    private static final int DEFAULT_HEALTH = 10;
    private static final float DEFAULT_SPEED = 1.5f;
    private static final int DEFAULT_ATTACK_POWER = 1;
    private static final float DEFAULT_ATTACK_SPEED = 1.0f;

    private int attackPower = DEFAULT_ATTACK_POWER;
    private float attackSpeed = DEFAULT_ATTACK_SPEED;
    private int score = 0;

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
        this.attackPower = amount;
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
        this.attackSpeed = amount;
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
        this.score = score;
    }
}
