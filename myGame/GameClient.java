package myGame;

import myGame.actions.*;
import myGame.entities.*;
import myGame.network.ClientManager;
import myGame.network.GhostManager;
import tage.*;
import tage.input.InputManager;
import tage.shapes.*;
import org.joml.*;
import net.java.games.input.*;
import net.java.games.input.Component.Identifier.*;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class GameClient extends VariableFrameRateGame {

	// Class Variables
	private static Engine engine;
	private InputManager im;
	private ClientManager clientManager;
	private GhostManager ghostManager;

	private CameraOrbit3D cam;
	private Avatar avatar;
	private GameObject terrain;
	private ObjShape terrainShape, avatarShape, wizardTowerShape,goblinShape;
	private TextureImage terrainTex, wizardTowerTex, goblinTex;
	private TextureImage[] avatarTextures = new TextureImage[3];
	private Vector3f hud1Color;

	private double lastFrameTime, currFrameTime, elapseFrameTime;
	private boolean isClientConnected = false;
	private final String serverAddress;
	private final int serverPort;

	private AnimatedShape wizardShape;


	public GameClient(String serverAddress, int serverPort) {
		super();
		this.serverAddress = serverAddress;
		this.serverPort = serverPort;
	}

	public static void main(String[] args) {
		GameClient game = new GameClient(args[0], Integer.parseInt(args[1]));
		engine = new Engine(game);
		game.initializeSystem();
		game.game_loop();
	}

	@Override
	public void loadShapes() {
    
        terrainShape = new TerrainPlane();
        avatarShape = new ImportedModel("wizard999.obj");
        wizardTowerShape = new ImportedModel("wizardTower.obj");
        goblinShape = new ImportedModel("goblin3.obj"); 
		wizardShape = new AnimatedShape("wizardmeshy.rkm", "wizardskelly.rks");
		wizardShape.loadAnimation("CAST", "fireball.rka");
		wizardShape.loadAnimation("IDLE", "idle.rka");
		wizardShape.loadAnimation("WALKING", "walking.rka");
		//wizardShape.playAnimation("IDLE", 0.5f, AnimatedShape.EndType.LOOP, 0);


   
	}

	@Override
	public void loadTextures() {
   
        terrainTex = new TextureImage("Grass006_1K-PNG_Color.png");
        avatarTextures[0] = new TextureImage("WizardUV.png");
        wizardTowerTex = new TextureImage("wizardTowerUV.png");
        goblinTex = new TextureImage("goblin3.png"); // <- could be failing here
   
	}

	@Override
	public void buildObjects() {
		// Terrain
		terrain = new GameObject(GameObject.root(), terrainShape, terrainTex);
		terrain.setLocalScale(new Matrix4f().scaling(300f, 40f, 300f)); // x and z cover more area while y leads to
																		// taller and deeper valleys
		terrain.setLocalTranslation(new Matrix4f().translation(0f, 0f, 0f));
		terrain.getRenderStates().setTiling(1);
		terrain.getRenderStates().setTileFactor(50);
		terrain.setIsTerrain(true); // terrain for height queries
		terrain.setHeightMap(new TextureImage("HeightmapTest.png"));

		// Avatar
		avatar = new Avatar(GameObject.root(), wizardShape, avatarTextures[0]);
		avatar.setLocalTranslation(new Matrix4f().translation(0, 9.5f, 0)); // position the avatar above the terrain
		avatar.setLocalScale(new Matrix4f().scaling(.2f));
		

		//goblin
		GameObject goblin = new GameObject(GameObject.root(), goblinShape, goblinTex);
		goblin.getRenderStates().hasLighting(true);
		goblin.setLocalScale(new Matrix4f().scaling(0.2f));
		goblin.setLocalTranslation(new Matrix4f().translation(-20f, 9.5f, -30f));

		// Tower
		GameObject wizardTower = new GameObject(GameObject.root(), wizardTowerShape, wizardTowerTex);
		wizardTower.getRenderStates().hasLighting(true);
		wizardTower.setLocalScale(new Matrix4f().scaling(5f));
		wizardTower.setLocalTranslation(new Matrix4f().translation(-13f, 27f, -142f));

		// Hud Color
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
		int classicLand = engine.getSceneGraph().loadCubeMap("classicLand");

		engine.getSceneGraph().setActiveSkyBoxTexture(classicLand);
		engine.getSceneGraph().setSkyBoxEnabled(true);
	}

	@Override
	public void initializeGame() {
		(engine.getRenderSystem()).setWindowDimensions(1900, 1000);
		cam = new CameraOrbit3D((engine.getRenderSystem()).getViewport("MAIN").getCamera(), avatar);

		// Initialize Network Client Manager
		setupNetworking();

		// Map key bindings
		mapKeyBindings();

		// Initialize game states
		lastFrameTime = System.currentTimeMillis();
		currFrameTime = System.currentTimeMillis();
		elapseFrameTime = 0.0;
	}

	@Override
	public void update() {
		// Calculate time since last update call and update game actions
		lastFrameTime = currFrameTime;
		currFrameTime = System.currentTimeMillis();
		elapseFrameTime = (currFrameTime - lastFrameTime) / 100.0;

		im.update((float) (elapseFrameTime));

		wizardShape.updateAnimation();

		if ((System.currentTimeMillis() - avatar.getLastInputTime()) > 500) {
    		if (!"IDLE".equals(avatar.getCurrentAnimation())) {
        		avatar.getAnimatedShape().playAnimation("IDLE", 0.25f, AnimatedShape.EndType.LOOP, 0);
        		avatar.setCurrentAnimation("IDLE");
        		avatar.setWalking(false);
    		}
		}

		// Update game states
		updateStates();

		// Process networking
		processNetworking();

		// Update HUDs
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
		FwdAction moveForward = new FwdAction(avatar, clientManager, true);
		FwdAction moveBack = new FwdAction(avatar, clientManager, false);
		TurnAction turnLeft = new TurnAction(avatar, true);
		TurnAction turnRight = new TurnAction(avatar, false);
		ZoomOrbitAction zoomInOrbit = new ZoomOrbitAction(cam, false);
		ZoomOrbitAction zoomOutOrbit = new ZoomOrbitAction(cam, true);
		ElevateOrbitAction elevateOrbit = new ElevateOrbitAction(cam, false);
		ElevateOrbitAction lowerOrbit = new ElevateOrbitAction(cam, true);
		RotateOrbitAction rotateRightOrbit = new RotateOrbitAction(cam, false);
		RotateOrbitAction rotateLeftOrbit = new RotateOrbitAction(cam, true);
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
	 * Initializes the network client manager
	 */
	private void setupNetworking() {
		isClientConnected = false;
		try {
			ghostManager = new GhostManager(this);
			clientManager = new ClientManager(InetAddress.getByName(serverAddress), serverPort, this);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (clientManager == null) {
			System.out.println("Missing server host");
		} else {
			System.out.println("Sending join message to server host");
			clientManager.sendJoinMessage();
			clientManager.sendCreateMessage(0, avatar.getWorldLocation());
		}
	}

	/**
	 * Processes packets received by the client from the server
	 */
	protected void processNetworking() {
		if (clientManager != null) {
			clientManager.processPackets();
		}
	}

	/**
	 * Updates the game states and parameters
	 */
	private void updateStates() {
		// Update the camera to follow the avatar
		cam.update();

		// Update avatar's position to always be on the terrain
		updateAvatarTerrain();
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

	/**
	 * Updates the avatar's position to always be on the terrain
	 */
	private void updateAvatarTerrain() {
		Vector3f loc = avatar.getWorldLocation();
		float terrainY = terrain.getHeight(loc.x, loc.z);
		float currentY = loc.y;
		float targetY = terrainY + 0.1f;
		float newY = currentY + (targetY - currentY) * 0.1f;
		loc.y = newY;
		avatar.setLocalLocation(loc);
	}

	/*
	 * =========================== Getters and Setters ===========================
	 */

	/**
	 * @return the engine
	 */
	public Engine getEngine() {
		return engine;
	}

	/**
	 * @return the ghost manager
	 */
	public GhostManager getGhostManager() {
		return ghostManager;
	}

	/**
	 * @return the avatar
	 */
	public Avatar getAvatar() {
		return avatar;
	}

	/**
	 * @return the avatar shape
	 */
	public ObjShape getAvatarShape() {
		return avatarShape;
	}

	/**
	 * @return the specified avatar texture
	 */
	public TextureImage getAvatarTexture(int skin) {
		if (skin < 0 || skin >= avatarTextures.length) {
			System.out.println("Invalid skin index. Returning default texture.");
			return avatarTextures[0];
		}
		return avatarTextures[skin];
	}

	/**
	 * Set whether the client is connected to the server
	 */
	public void setIsClientConnected(boolean isClientConnected) {
		this.isClientConnected = isClientConnected;
	}

}
