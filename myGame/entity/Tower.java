package myGame.entity;

import myGame.GameClient;
import myGame.utility.EnemyManager;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import tage.GameObject;
import tage.ObjShape;
import tage.TextureImage;
import tage.physics.PhysicsObject;

public class Tower extends GameObject {
    // Class Variables
    private GameClient game;
    private int health = 100;
    private int damageLock = 0;

    /**
     * Construct Tower object with the specified parameters
     */
    public Tower(GameObject object, ObjShape shape, TextureImage texture, GameClient game) {
        super(object, shape, texture);
        this.game = game;
    }

    /**
     * Update the tower
     */
    public void update() {
        if (damageLock == 0) {
            EnemyManager enemyManager = game.getEnemyManager();
            if (enemyManager == null) {
                return;
            }

            for (Enemy enemy : enemyManager.getEnemies()) {
                if (enemy.isAttackingTower()) {
                    takeDamage();
                    damageLock++;
                    break;
                }
            }
        }
        damageLock++;
        if (damageLock >= 25) {
            damageLock = 0;
        }
    }

    /**
     * Make the tower take damage
     */
    public void takeDamage() {

        if (health >= 70) {
            game.getTowerLight().setDiffuse(0f, 1f, 0f);
        }
        else if (health >= 30) {
            game.getTowerLight().setDiffuse(1f, 0.5f, 0f);
        }
        else {
            game.getTowerLight().setDiffuse(1f, 0f, 0f);
        }

        setHealth(health - 1);
    }

    /**
     * Get the health of the tower
     */
    public int getHealth() {
        return health;
    }

    /**
     * Set the health of the tower
     */
    public void setHealth(int health) {
        this.health = Math.max(health, 0);
    }
}
