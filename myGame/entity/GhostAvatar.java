package myGame.entity;

import myGame.GameClient;
import tage.GameObject;
import tage.ObjShape;
import tage.TextureImage;
import java.util.UUID;
import org.joml.*;

public class GhostAvatar extends Avatar {

    // Class Variables
    private final UUID id;

    /**
     * Construct GameAvatar object with the specified parameters
     */
    public GhostAvatar(ObjShape shape, TextureImage texture, GameClient game, UUID id, Vector3f position) {
        super(new GameObject(GameObject.root(), shape, texture), shape, texture, game);
        this.id = id;
        setLocalLocation(position);
        setLocalScale(new Matrix4f().scaling(1.0f));
    }

    /**
     * @return UUID of the ghost avatar
     */
    public UUID getId() {
        return id;
    }
}
