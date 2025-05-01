package myGame.action;

import tage.CameraOrbit3D;
import tage.input.action.AbstractInputAction;
import net.java.games.input.Event;

public class ZoomOrbitAction extends AbstractInputAction {
    private CameraOrbit3D camera;
    private boolean reverse;
    private float amount;

    public ZoomOrbitAction(CameraOrbit3D camera, boolean reverse) {
        this.camera = camera;
        this.reverse = reverse;
    }

    @Override
    public void performAction(float elapsTime, Event e) {
        if (reverse) {
            amount = -e.getValue() * 0.3f;
        } else {
            amount = e.getValue() * 0.3f;
        }
        camera.zoom(-amount * elapsTime);
    }
}
