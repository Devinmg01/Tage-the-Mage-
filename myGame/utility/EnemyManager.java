package myGame.utility;

import myGame.GameClient;
import myGame.entity.Enemy;
import tage.GameObject;
import tage.ObjShape;
import tage.TextureImage;
import org.joml.Vector3f;
import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;
import tage.audio.*;
import myGame.entity.Avatar;

public class EnemyManager {
    private ArrayList<Enemy> enemies;
    private Random random;
    private ObjShape shape;
    private TextureImage texture;
    private Vector3f targetLoc;
    private GameClient game;
    private float x1, x2, z1, z2;
    private float lastSpawnTime = 0;
    private float spawnRate;
    private Sound walkSound;
    private Sound goblinSound;
    private float gruntTimer = 0f;
    private Avatar avatar;



    public EnemyManager(ObjShape shape, TextureImage texture, Vector3f targetLoc, GameClient game,
                        float spawnRate, float x1, float x2, float z1, float z2, Sound walkSound, Sound goblinSound, Avatar avatar) {
        this.enemies = new ArrayList<>();
        this.random = new Random();
        this.shape = shape;
        this.texture = texture;
        //this.targetLoc = new Vector3f(targetLoc.x, targetLoc.y - 1.5f, targetLoc.z);
        this.targetLoc = targetLoc;
        this.game = game;
        this.spawnRate = spawnRate;
        this.x1 = x1;
        this.x2 = x2;
        this.z1 = z1;
        this.z2 = z2;
        this.walkSound = walkSound;
        this.avatar = avatar;
        this.goblinSound = goblinSound;
    }

    public void spawnEnemy(UUID enemyId, Vector3f spawnLoc) {
        Enemy enemy = new Enemy(enemyId, GameObject.root(), shape, texture, game,
                spawnLoc, targetLoc, walkSound);
        enemies.add(enemy);
    }

    public void spawnEnemy() {
        UUID enemyId = UUID.randomUUID();

        float x = x1 + random.nextFloat() * (x2 - x1);
        float z = z1 + random.nextFloat() * (z2 - z1);
        Vector3f spawnLoc = new Vector3f(x, 0f, z);

        spawnEnemy(enemyId, spawnLoc);

        if (game.getClientManager() != null) {
            game.getClientManager().sendEnemySpawn(enemyId, spawnLoc);
        }
    }

    public void update(float elapsedTime) {
        for (Enemy enemy : enemies) {
            enemy.move(elapsedTime);
        }
        lastSpawnTime += elapsedTime;
        if (lastSpawnTime >= (spawnRate * 100f) && enemies.size() < 50) {
            spawnEnemy();
            lastSpawnTime = 0;
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
                        System.out.println("Goblin grunt played near: " + enemy.getWorldLocation());
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