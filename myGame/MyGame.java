package myGame;

import myGame.actions.*;
import myGame.entities.*;
import tage.*;

import tage.input.InputManager;
import tage.shapes.*;

import org.joml.*;

import net.java.games.input.*;
import net.java.games.input.Component.Identifier.*;

public class MyGame extends VariableFrameRateGame {
	private static Engine engine;
	private InputManager im;
	private CameraOrbit3D cam;
	private Avatar avatar;
	private GameObject terrain;
	private ObjShape terrainShape, avatarShape;
	private TextureImage terrainTex, avatarTex;
	private Vector3f hud1Color;
	private double lastFrameTime, currFrameTime, elapseFrameTime;

	public MyGame() {
		super();
	}

	public static void main(String[] args) {
		MyGame game = new MyGame();
		engine = new Engine(game);
		game.initializeSystem();
		game.game_loop();
	}

	@Override
	public void loadShapes() {
		terrainShape = new TerrainPlane();
		avatarShape = new ImportedModel("dolphinHighPoly.obj");
	}

	@Override
	public void loadTextures() {
		terrainTex = new TextureImage("Grass006_1K-PNG_Color.png");
		avatarTex = new TextureImage("Dolphin_HighPolyUV.png");
	}

	@Override
	public void buildObjects() {
		// Terrain
		terrain = new GameObject(GameObject.root(), terrainShape, terrainTex);
		terrain.setLocalScale(new Matrix4f().scaling(300f, 20f, 300f)); // x and z cover more area while y leads to
																		// taller and deeper valleys
		terrain.setLocalTranslation(new Matrix4f().translation(0f, -5f, 0f));
		terrain.getRenderStates().setTiling(1);
		terrain.getRenderStates().setTileFactor(50);
		terrain.setIsTerrain(true); // mark as terrain for height queries
		terrain.setHeightMap(new TextureImage("HeightmapTest.png"));

		// Avatar
		avatar = new Avatar(GameObject.root(), avatarShape, avatarTex);
		avatar.setLocalTranslation(new Matrix4f().translation(0, 1, 0));
		avatar.setLocalScale(new Matrix4f().scaling(3.0f));

		hud1Color = new Vector3f(1, 0, 0);
	}

	@Override
	public void initializeLights() {
		Light.setGlobalAmbient(0.5f, 0.5f, 0.5f);
		Light ambientLight = new Light();
		ambientLight.setDiffuse(0.0f, 0.0f, 0.0f);
		ambientLight.setSpecular(0.0f, 0.0f, 0.0f);
		ambientLight.setLocation(new Vector3f(5.0f, 4.0f, 2.0f));

		engine.getSceneGraph().addLight(ambientLight);
	}

	@Override
	public void loadSkyBoxes() {
		int fluffyClouds = engine.getSceneGraph().loadCubeMap("fluffyClouds");
		int lakeIslands = engine.getSceneGraph().loadCubeMap("lakeIslands");

		engine.getSceneGraph().setActiveSkyBoxTexture(fluffyClouds);
		engine.getSceneGraph().setSkyBoxEnabled(true);
	}

	@Override
	public void initializeGame() {
		(engine.getRenderSystem()).setWindowDimensions(1900, 1000);
		cam = new CameraOrbit3D((engine.getRenderSystem()).getViewport("MAIN").getCamera(), avatar);

		// Initialize game states
		lastFrameTime = System.currentTimeMillis();
		currFrameTime = System.currentTimeMillis();
		elapseFrameTime = 0.0;

		// Map key bindings
		mapKeyBindings();
	}

	@Override
	public void update() {
		// Calculate time since last update call and update game actions
		lastFrameTime = currFrameTime;
		currFrameTime = System.currentTimeMillis();
		elapseFrameTime = (currFrameTime - lastFrameTime) / 100.0;
		im.update((float) (elapseFrameTime));

		// Update game states
		updateStates();

		// Update HUD
		updateHUD();
	}

	/*
	 * =========================== HELPER FUNCTIONS ===========================
	 */

	/**
	 * Maps key bindings to actions
	 */
	private void mapKeyBindings() {
		im = engine.getInputManager();

		// Actions
		FwdAction moveForward = new FwdAction(avatar, false);
		FwdAction moveBack = new FwdAction(avatar, true);
		TurnAction turnLeft = new TurnAction(avatar, false);
		TurnAction turnRight = new TurnAction(avatar, true);
		ZoomOrbitAction zoomInOrbit = new ZoomOrbitAction(cam, true);
		ZoomOrbitAction zoomOutOrbit = new ZoomOrbitAction(cam, false);
		ElevateOrbitAction elevateOrbit = new ElevateOrbitAction(cam, false);
		ElevateOrbitAction lowerOrbit = new ElevateOrbitAction(cam, true);
		RotateOrbitAction rotateRightOrbit = new RotateOrbitAction(cam, true);
		RotateOrbitAction rotateLeftOrbit = new RotateOrbitAction(cam, false);
		ExitGameAction exitGame = new ExitGameAction();

		// Keyboard bindings
		im.associateActionWithAllKeyboards(net.java.games.input.Component.Identifier.Key.W, moveForward,
				InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		im.associateActionWithAllKeyboards(net.java.games.input.Component.Identifier.Key.S, moveBack,
				InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		im.associateActionWithAllKeyboards(net.java.games.input.Component.Identifier.Key.A, turnLeft,
				InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		im.associateActionWithAllKeyboards(net.java.games.input.Component.Identifier.Key.D, turnRight,
				InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		im.associateActionWithAllKeyboards(net.java.games.input.Component.Identifier.Key.E, zoomInOrbit,
				InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		im.associateActionWithAllKeyboards(net.java.games.input.Component.Identifier.Key.Q, zoomOutOrbit,
				InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		im.associateActionWithAllKeyboards(net.java.games.input.Component.Identifier.Key.UP, elevateOrbit,
				InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		im.associateActionWithAllKeyboards(net.java.games.input.Component.Identifier.Key.DOWN, lowerOrbit,
				InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		im.associateActionWithAllKeyboards(net.java.games.input.Component.Identifier.Key.LEFT, rotateLeftOrbit,
				InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		im.associateActionWithAllKeyboards(net.java.games.input.Component.Identifier.Key.RIGHT, rotateRightOrbit,
				InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		im.associateActionWithAllKeyboards(net.java.games.input.Component.Identifier.Key.ESCAPE, exitGame,
				InputManager.INPUT_ACTION_TYPE.ON_PRESS_ONLY);

		// Gamepad bindings
		im.associateActionWithAllGamepads(net.java.games.input.Component.Identifier.Axis.Y, moveBack,
				InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN); // left stick y-axis
		im.associateActionWithAllGamepads(net.java.games.input.Component.Identifier.Axis.X, turnRight,
				InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN); // left stick x-axis
		im.associateActionWithAllGamepads(net.java.games.input.Component.Identifier.Axis.RX, rotateRightOrbit,
				InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN); // right stick x-axis
		im.associateActionWithAllGamepads(net.java.games.input.Component.Identifier.Axis.Z, zoomOutOrbit,
				InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN); // L2 out R2 in
		im.associateActionWithAllGamepads(net.java.games.input.Component.Identifier.Button._4, lowerOrbit,
				InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN); // L1
		im.associateActionWithAllGamepads(net.java.games.input.Component.Identifier.Button._5, elevateOrbit,
				InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN); // R1
		im.associateActionWithAllGamepads(net.java.games.input.Component.Identifier.Button._7, exitGame,
				InputManager.INPUT_ACTION_TYPE.ON_PRESS_ONLY); // start button (right settings)

		// im.associateActionWithAllGamepads(net.java.games.input.Component.Identifier.Button._0,
		// , InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN); // a button
		// im.associateActionWithAllGamepads(net.java.games.input.Component.Identifier.Button._1,
		// , InputManager.INPUT_ACTION_TYPE.ON_PRESS_ONLY); // b button
		// im.associateActionWithAllGamepads(net.java.games.input.Component.Identifier.Button._2,
		// , InputManager.INPUT_ACTION_TYPE.ON_PRESS_ONLY); // x button
		// im.associateActionWithAllGamepads(net.java.games.input.Component.Identifier.Button._3,
		// , InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN); // y button
		// im.associateActionWithAllGamepads(net.java.games.input.Component.Identifier.Button._6,
		// , InputManager.INPUT_ACTION_TYPE.ON_PRESS_ONLY); // back button (left
		// settings)

	}

	/**
	 * Updates the game states and parameters
	 */
	private void updateStates() {
		// Update the camera to follow the avatar
		cam.update();

		/* will add more states here later */
	}

	/**
	 * Updates the HUDs
	 */
	private void updateHUD() {
		Viewport main = (engine.getRenderSystem()).getViewport("MAIN");

		String hud1Str = "Score: " + avatar.getScore();

		(engine.getHUDmanager()).setHUD1(hud1Str, hud1Color,
				(int) ((main.getActualWidth() - (main.getActualWidth() / 2)) - (hud1Str.length() * 8 / 2)), 15);
	}
}
