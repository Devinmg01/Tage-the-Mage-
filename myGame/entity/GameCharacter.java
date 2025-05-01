package myGame.entity;

import org.joml.Vector3f;
import tage.GameObject;
import tage.ObjShape;
import tage.TextureImage;

public abstract class GameCharacter extends GameObject {

    // Class Variables
    private int health;
    private float speed;

    /**
     * Construct GameCharacter object with the specified parameters
     */
    public GameCharacter(GameObject object, ObjShape shape, TextureImage texture, int health, float speed) {
        super(object, shape, texture);
        this.health = health;
        this.speed = speed;
    }

    /**
     * @return health of the character
     */
    public int getHealth() {
        return health;
    }

    /**
     * Set character health to a specified amount
     */
    public void setHealth(int health) {
        this.health = Math.max(health, 0);
    }

    /**
     * @return speed of the character
     */
    public float getSpeed() {
        return speed;
    }

    /**
     * Set character speed to a specified amount
     */
    public void setSpeed(float speed) {
        this.speed = Math.max(speed, 0.1f);
    }
}
