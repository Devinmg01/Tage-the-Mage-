package tage;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public class CameraOrbit3D {
    private final GameObject object;
    private final Camera camera;
    private float orbitAngle;
    private float elevationAngle;
    private float zoomDistance;

    private float minDistance = 3.0f;
    private float maxDistance = 25.0f;
    private float minElevationAngle = 0.0f;
    private float maxElevationAngle = 85.0f;

    /**
     * Constructs the orbit camera controller with the given camera and game object.
     */
    public CameraOrbit3D(Camera cam, GameObject object) {
        this.camera = cam;
        this.object = object;

        // Initialize default orbit parameters.
        this.orbitAngle = 0.0f;
        this.elevationAngle = 20.0f;
        this.zoomDistance = 10.0f;
    }
    
    /**
     * Adjusts the camera's elevation by a specified angle.
     */
    public void adjustElevation(float amount) {
        elevationAngle += amount;

        // Clamp the elevation angle if needed
        if (elevationAngle < minElevationAngle)
            elevationAngle = minElevationAngle;
        if (elevationAngle > maxElevationAngle)
            elevationAngle = maxElevationAngle;
    }

    /**
     * Rotates the camera around the object by specified amount
     */
    public void orbit(float amount) {
        orbitAngle += amount;
    }
    
    /**
     * Zooms the camera in or out by a specified amount
     */
    public void zoom(float amount) {
        zoomDistance += amount;

        // Clamp the zoom distance if needed
        if (zoomDistance < minDistance)
            zoomDistance = minDistance;
        if (zoomDistance > maxDistance)
            zoomDistance = maxDistance;
    }
    
    /**
     * Updates the camera position and orientation, meant to be called each frame
     */
    public void update() {

        // Get the object's world position and rotation
        Vector3f objPos = object.getWorldLocation();
        Matrix4f objRot = object.getWorldRotation();
        
        // Convert angles to radians
        float radOrbit = (float) Math.toRadians(orbitAngle);
        float radElev = (float) Math.toRadians(elevationAngle);

        // Compute local offsets
        float xLocal = zoomDistance * (float) Math.cos(radElev) * (float) Math.sin((float) Math.toRadians(orbitAngle));
        float yLocal = zoomDistance * (float) Math.sin(radElev);
        float zLocal = zoomDistance * (float) Math.cos(radElev) * (float) Math.cos(radOrbit);
        Vector3f localOffset = new Vector3f(xLocal, yLocal, zLocal);

        // Convert object's local space to world space
        Vector3f worldOffset = new Vector3f();
        objRot.transformDirection(localOffset, worldOffset);
        
        // Compute the new camera position
        Vector3f newCameraPos = new Vector3f(objPos).add(worldOffset);
        camera.setLocation(newCameraPos);
        
        // Have the camera look at the object
        camera.lookAt(object.getWorldLocation());
    }
}
