package myGame.utility;

import myGame.GameClient;
import myGame.entity.GhostAvatar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import tage.ObjShape;
import tage.TextureImage;
import org.joml.*;

public class GhostManager {

    // Class Variables
    private HashMap<UUID, GhostAvatar> ghosts = new HashMap<>();
    private GameClient game;

    /**
     * Construct GhostManager
     */
    public GhostManager(GameClient game) {
        this.game = game;
    }

    /**
     * Create a ghost avatar with the specified id and skin at the specified
     * position
     */
    public void createGhost(UUID id, int skin, Vector3f position) {
        if (!ghosts.containsKey(id)) {
            ObjShape ghostShape = game.getAvatarShape();
            TextureImage ghostTex = game.getAvatarTexture(skin);
            GhostAvatar ghost = new GhostAvatar(ghostShape, ghostTex, game, id, position);
            ghosts.put(id, ghost);
        }
    }

    /**
     * Update the position of the specified ghost avatar
     */
    public void updateGhost(UUID id, Vector3f position) {
        GhostAvatar ghost = ghosts.get(id);
        if (ghost != null) {
            ghost.setLocalLocation(position);
        }
    }

    /**
     * @return the ghost avatar with the specified id
     */
    public GhostAvatar getGhost(UUID id) {
        GhostAvatar ghost = ghosts.get(id);
        if (ghost != null) {
            return ghost;
        } else {
            System.out.println("Ghost with ID " + id + " not found.");
            return null;
        }
    }

    /**
     * @return an array list of all the ghosts
     */
    public ArrayList<GhostAvatar> getAllGhosts() {
        return new ArrayList<>(ghosts.values());
    }

    /**
     * Remove the ghost avatar with the specified id
     */
    public void removeGhost(UUID id) {
        GhostAvatar ghost = ghosts.remove(id);
        if (ghost != null) {
            game.getEngine().getSceneGraph().removeGameObject(ghost);
        }
    }
}