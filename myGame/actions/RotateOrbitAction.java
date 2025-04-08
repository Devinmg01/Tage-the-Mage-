package myGame.actions;

import tage.CameraOrbit3D;
import tage.input.action.AbstractInputAction;
import net.java.games.input.Event;

public class RotateOrbitAction extends AbstractInputAction {
    private CameraOrbit3D camera;
    private boolean reverse;
    private float amount;

    public RotateOrbitAction(CameraOrbit3D camera, boolean reverse) {
        this.camera = camera;
        this.reverse = reverse;
    }

    @Override
    public void performAction(float elapsTime, Event e) {
        if (reverse) {
            amount = -e.getValue() * 3.5f;
        } else {
            amount = e.getValue() * 3.5f;
        }
        camera.orbit(-amount * elapsTime);
    }
}
