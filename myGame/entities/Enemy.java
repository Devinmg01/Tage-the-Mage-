package myGame.entities;

import tage.GameObject;
import tage.ObjShape;
import tage.TextureImage;

public class Enemy extends GameCharacter{

    /**
     * Construct Enemy object with the specified parameters
     */
    public Enemy(GameObject object, ObjShape shape, TextureImage texture) {
        super(object, shape, texture, 2, 1.0f);
    }
}
