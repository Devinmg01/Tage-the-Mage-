package myGame.entity;

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
    public GhostAvatar(ObjShape shape, TextureImage texture, UUID id, Vector3f position) {
        super(new GameObject(GameObject.root(), shape, texture), shape, texture);
        this.id = id;
        setLocalLocation(position);
    }

    /**
     * @return UUID of the ghost avatar
     */
    public UUID getId() {
        return id;
    }
}
