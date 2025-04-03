package myGame;

import tage.*;

import tage.shapes.*;

import java.lang.Math;
import java.awt.*;
import java.awt.event.*;
import java.util.HashSet;
import javax.swing.*;

import org.joml.*;

import net.java.games.input.*;
import net.java.games.input.Component.Identifier.*;

public class MyGame extends VariableFrameRateGame
{
	private static Engine engine;

	private boolean paused = false;
	private int counter = 0;
	private double lastFrameTime, currFrameTime, elapsTime;

	private GameObject dol;
	private ObjShape dolS;
	private TextureImage doltx;
	private Light light1;

	private HashSet<Integer> activeKeys = new HashSet<>();

	public MyGame() { super(); }

	public static void main(String[] args)
	{
		MyGame game = new MyGame();
		engine = new Engine(game);
		game.initializeSystem();
		game.game_loop();
	}

	@Override
	public void loadShapes()
	{
		dolS = new ImportedModel("wizard.obj");
	}

	@Override
	public void loadTextures()
	{
		doltx = new TextureImage("WizardUV.png");
	}

	@Override
	public void buildObjects()
	{
		// dolphin (temp)
		dol = new GameObject(GameObject.root(), dolS, doltx);
		Matrix4f initialTranslation = new Matrix4f().translation(0, 0, 0);
		Matrix4f initialScale = new Matrix4f().scaling(1.0f);

		dol.setLocalTranslation(initialTranslation);
		dol.setLocalScale(initialScale);

		// ground plane

		ObjShape terrainShape = new TerrainPlane(); // grayscale heightmap image
		TextureImage terrainTex = new TextureImage("Grass006_1K-PNG_Color.png");

		GameObject terrain = new GameObject(GameObject.root(), terrainShape, terrainTex);

		// Scale and position it
		terrain.setLocalScale(new Matrix4f().scaling(300f, 20f, 300f)); // x and z cover more area whiule y leads to taller and deeper valleys
		terrain.setLocalTranslation(new Matrix4f().translation(0f, -5f, 0f));

		// Mark as terrain for height queries
		terrain.setIsTerrain(true);
		terrain.setHeightMap(new TextureImage("HeightmapTest.png"));
		
	}

	@Override
	public void initializeLights()
	{
		Light.setGlobalAmbient(0.5f, 0.5f, 0.5f);
		light1 = new Light();
		light1.setLocation(new Vector3f(5.0f, 4.0f, 2.0f));
		engine.getSceneGraph().addLight(light1);
	}

	@Override
	public void initializeGame()
	{
		lastFrameTime = System.currentTimeMillis();
		currFrameTime = System.currentTimeMillis();
		elapsTime = 0.0;
		engine.getRenderSystem().setWindowDimensions(1900, 1000);

		Camera cam = engine.getRenderSystem().getViewport("MAIN").getCamera();
		cam.setLocation(new Vector3f(0, 0, 5));
	}

	@Override
	public void update()
	{
		lastFrameTime = currFrameTime;
		currFrameTime = System.currentTimeMillis();
		if (!paused) elapsTime += (currFrameTime - lastFrameTime) / 1000.0;

		//dol.setLocalRotation(new Matrix4f().rotate((float)elapsTime, 0, 1, 0));

		int elapsTimeSec = Math.round((float) elapsTime);
		String dispStr1 = "Time = " + elapsTimeSec;
		String dispStr2 = "Keyboard hits = " + counter;

		Vector3f hud1Color = new Vector3f(1f, 0f, 0f);
		Vector3f hud2Color = new Vector3f(0f, 0f, 1f);

		engine.getHUDmanager().setHUD1(dispStr1, hud1Color, 15, 15);
		engine.getHUDmanager().setHUD2(dispStr2, hud2Color, 500, 15);

		// Dolphin movement
		Vector3f loc = dol.getWorldLocation();
		Vector3f fwd = dol.getWorldForwardVector();
		Camera cam = (engine.getRenderSystem().getViewport("MAIN").getCamera());
		Vector3f camLoc = cam.getLocation();
		float moveSpeed = 0.055f;

		if (activeKeys.contains(KeyEvent.VK_W)) {
			loc = loc.add(fwd.mul(moveSpeed));
		}
		if (activeKeys.contains(KeyEvent.VK_S)) {
			loc = loc.add(fwd.mul(-moveSpeed));
		}

		dol.setLocalLocation(loc);
		cam.setU(dol.getWorldRightVector());
        cam.setV(dol.getWorldUpVector());
        cam.setN(dol.getWorldForwardVector());
        cam.setLocation(loc.add(dol.getWorldUpVector().mul(1.3f)).add(dol.getWorldForwardVector().mul(-3.5f)));

		
	}

	@Override
	public void keyPressed(KeyEvent e)
	{
		activeKeys.add(e.getKeyCode());

		if (e.getKeyCode() == KeyEvent.VK_1)
			paused = !paused;
		else if (e.getKeyCode() == KeyEvent.VK_ESCAPE)
			System.exit(0);
	}

	@Override
	public void keyReleased(KeyEvent e)
	{
		activeKeys.remove(e.getKeyCode());
	}

	@Override
	public void loadSkyBoxes()
	{
		int fluffyClouds = engine.getSceneGraph().loadCubeMap("fluffyClouds");
		int lakeIslands = engine.getSceneGraph().loadCubeMap("lakeIslands");

		engine.getSceneGraph().setActiveSkyBoxTexture(lakeIslands);
		engine.getSceneGraph().setSkyBoxEnabled(true);
	}
}
