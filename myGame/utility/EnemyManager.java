package myGame.utility;

import myGame.GameClient;
import myGame.entity.Enemy;
import tage.GameObject;
import tage.ObjShape;
import tage.TextureImage;
import org.joml.Vector3f;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import java.util.UUID;
import tage.audio.*;
import myGame.entity.Avatar;

public class EnemyManager {

    // Class Variables
    private ArrayList<Enemy> enemies;
    private Random random;
    private Avatar avatar;
    private ObjShape shape;
    private TextureImage texture;
    private GameClient game;
    private Sound walkSound;
    private Sound goblinSound;
    private Vector3f targetLoc, spawnLoc;
    private float gruntTimer = 0f;
    private float lastSpawnTime = 0f;
    private final float SPAWN_RATE = 1.0f;
    private final float MIN_DIST = 50.0f;
    private final float MAX_DIST = 150.0f;


    public EnemyManager(ObjShape shape, TextureImage texture, GameClient game) {
        this.enemies = new ArrayList<>();
        this.random = new Random();
        this.shape = shape;
        this.texture = texture;
        this.game = game;
        this.walkSound = game.getWalkSound();
        this.avatar = game.getAvatar();
        this.goblinSound = game.getGoblinSound();
        this.targetLoc = new Vector3f();
    }

    public void spawnEnemy(UUID enemyId, Vector3f spawnLoc) {
        Enemy enemy = new Enemy(enemyId, GameObject.root(), shape, texture, game, spawnLoc, targetLoc);
        enemies.add(enemy);
    }

    public void spawnEnemy() {
        UUID enemyId = UUID.randomUUID();

        do {
            float dx = (random.nextFloat() * 2f - 1f) * MAX_DIST;
            float dz = (random.nextFloat() * 2f - 1f) * MAX_DIST;
            spawnLoc = new Vector3f(targetLoc.x + dx, targetLoc.y, targetLoc.z + dz);
        } while (spawnLoc.distance(targetLoc) < MIN_DIST || spawnLoc.distance(targetLoc) > MAX_DIST);

        spawnEnemy(enemyId, spawnLoc);

        if (game.getClientManager() != null) {
            game.getClientManager().sendEnemySpawn(enemyId, spawnLoc);
        }
    }

    public void removeEnemy(UUID enemyID) {
        Enemy enemy = enemies.stream().filter(e -> e.getId().equals(enemyID)).findFirst().orElse(null);
        if (enemy != null) {
            enemies.remove(enemy);
            if (game.getClientManager() != null) {
                game.getClientManager().sendEnemyRemove(enemyID);
            }
            game.getEngine().getSceneGraph().removeGameObject(enemy);
        }
    }

    public void update(float elapsedTime) {
        Iterator<Enemy> iterator = enemies.iterator();
        while (iterator.hasNext()) {
            Enemy enemy = iterator.next();
            enemy.move(elapsedTime);
            enemy.getAnimatedShape().updateAnimation();

            if (enemy.isDead()) {
                iterator.remove();
                game.getEngine().getSceneGraph().removeGameObject(enemy);
                if (game.getClientManager() != null) {
                    game.getClientManager().sendEnemyRemove(enemy.getId());
                }
            }
        }

        lastSpawnTime += elapsedTime;
        if (lastSpawnTime >= (SPAWN_RATE * 100f) && enemies.size() < 50) {
            spawnEnemy();
            lastSpawnTime = 0f;
        }

        // Grunt logic
        gruntTimer += elapsedTime;

        if (gruntTimer >= 60f) { // play every 60 seconds max
            for (Enemy enemy : enemies) {
                float distance = enemy.getWorldLocation().distance(avatar.getWorldLocation());
                if (distance <= 20f) {
                    if (goblinSound != null && !goblinSound.getIsPlaying()) {
                        goblinSound.setLocation(enemy.getWorldLocation());
                        goblinSound.play();
                        //System.out.println("Goblin grunt played near: " + enemy.getWorldLocation());
                        break; // only play once from one goblin
                    }
                }
            }
            gruntTimer = 0f;
        }
    }

    public ArrayList<Enemy> getEnemies() {
        return enemies;
    }
}