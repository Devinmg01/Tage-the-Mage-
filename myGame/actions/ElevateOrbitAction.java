package myGame.actions;

import tage.CameraOrbit3D;
import tage.input.action.AbstractInputAction;
import net.java.games.input.Event;

public class ElevateOrbitAction extends AbstractInputAction {
    private CameraOrbit3D camera;
    private boolean reverse;
    private float amount;

    public ElevateOrbitAction(CameraOrbit3D camera, boolean reverse) {
        this.camera = camera;
        this.reverse = reverse;
    }

    @Override
    public void performAction(float elapsTime, Event e) {
        if (reverse) {
            amount = -e.getValue();
        } else {
            amount = e.getValue();
        }

        camera.adjustElevation(amount * elapsTime);
    }
}
