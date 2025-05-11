package myGame.entity;

import myGame.GameClient;
import tage.GameObject;
import tage.ObjShape;
import tage.TextureImage;
import tage.physics.PhysicsObject;
import javax.vecmath.Vector3f;
import tage.physics.JBullet.JBulletPhysicsObject;
import tage.shapes.AnimatedShape;


import com.bulletphysics.dynamics.RigidBody;

public abstract class GameCharacter extends GameObject {

    // Class Variables
    private GameClient game;
    private PhysicsObject physicsBody;
    private float physicsBodyHeight, physicsBodyRadius;
    private int health;

    protected AnimatedShape animShape;
    protected String currentAnimation;


    /**
     * Construct GameCharacter object with the specified parameters
     */
    public GameCharacter(GameObject object, ObjShape shape, TextureImage texture, GameClient game, int health) {
        super(object, shape, texture);
        this.game = game;
        this.health = health;
    }

    /**
     * Initialize physics object and attach it to the character
     */
    public void initPhysics(float mass, float radius, float height) {
        physicsBodyHeight = height;
        physicsBodyRadius = radius;
        double[] transform = game.toDoubleArray(getLocalTranslation().get(new float[16]));
        physicsBody = game.getEngine().getSceneGraph().addPhysicsCapsule(mass, transform, radius, height);
        physicsBody.setBounciness(0.1f);
        this.setPhysicsObject(physicsBody);
    }

    /**
     * @return true if character is currently colliding with target object
     */
    public boolean checkCollision(GameObject target) {
        JBulletPhysicsObject objA = (JBulletPhysicsObject) this.getPhysicsObject();
        JBulletPhysicsObject objB = (JBulletPhysicsObject) target.getPhysicsObject();
        RigidBody a = objA.getRigidBody();
        RigidBody b = objB.getRigidBody();

        Vector3f amin = new Vector3f();
        Vector3f amax = new Vector3f();
        Vector3f bmin = new Vector3f();
        Vector3f bmax = new Vector3f();

        a.getAabb(amin, amax);
        b.getAabb(bmin, bmax);

        if (amax.x < bmin.x || amin.x > bmax.x) return false;
        if (amax.y < bmin.y || amin.y > bmax.y) return false;
        if (amax.z < bmin.z || amin.z > bmax.z) return false;

        return true;
    }

    /**
     * @return physics object for character
     */
    public PhysicsObject getPhysicsBody() {
        return physicsBody;
    }

    /**
     * @return the half height of the physics object
     */
    public float getPhysicsBodyHalfHeight() {
        return (physicsBodyHeight / 2f) + physicsBodyRadius;
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

    public AnimatedShape getAnimatedShape() {
    return animShape;
    }

    public String getCurrentAnimation() {
        return currentAnimation;
    }

    public void setCurrentAnimation(String animName) {
        this.currentAnimation = animName;
    }

}
