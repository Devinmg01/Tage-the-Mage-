package myGame;

import myGame.action.*;
import myGame.entity.*;
import myGame.utility.ClientManager;
import myGame.utility.EnemyManager;
import myGame.utility.GhostManager;
import tage.*;
import tage.input.InputManager;
import tage.shapes.*;
import tage.audio.*;
import org.joml.Vector3f;
import org.joml.Matrix4f;

import java.io.IOException;
import java.net.InetAddress;
import tage.physics.PhysicsEngine;
import tage.physics.PhysicsObject;

public class GameClient extends VariableFrameRateGame {

	// Class Variables
	private static Engine engine;
	private InputManager im;
	private IAudioManager audioMgr;
	private ClientManager clientManager;
	private EnemyManager enemyManager;
	private GhostManager ghostManager;
	private PhysicsEngine physicsEngine;

	private CameraOrbit3D cam;
	private Avatar avatar;
	private GameObject terrain, tower;
	private ObjShape terrainShape, avatarShape, towerShape, goblinShape;
	private AnimatedShape avatarAnimShape, goblinAnimShape;
	private TextureImage terrainTex, towerTex, goblinTex;
	private TextureImage[] avatarTextures = new TextureImage[3];
	private Sound walkSound, goblinSound, backgroundMusic;
	private Vector3f hud1Color;

	private double lastFrameTime, currFrameTime, elapseFrameTime;
	private final String serverAddress;
	private final int serverPort;

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
        avatarShape = new ImportedModel("wizard.obj");
        towerShape = new ImportedModel("tower.obj");
        goblinShape = new ImportedModel("goblin.obj");




		avatarAnimShape = new AnimatedShape("wizardmeshy.rkm", "wizardskelly.rks");
		avatarAnimShape.loadAnimation("CAST", "fireball.rka");
		avatarAnimShape.loadAnimation("IDLE", "idle.rka");
		avatarAnimShape.loadAnimation("WALKING", "walking.rka");

		goblinAnimShape = new AnimatedShape("GoblinFixed.rkm", "GoblinFixed.rks");
		goblinAnimShape.loadAnimation("WALKING", "GoblinRun.rka");
		goblinAnimShape.loadAnimation("ATTACK", "GoblinAttack.rka");
	}

	@Override
	public void loadTextures() {
		terrainTex = new TextureImage("../terrain/grass.png");
		avatarTextures[0] = new TextureImage("WizardUV1.png");
		towerTex = new TextureImage("wizardTowerUV.png");
		goblinTex = new TextureImage("goblin.png");
	}

	@Override
	public void loadSounds() {
    	audioMgr = engine.getAudioManager();

		// Walking Sound
    	AudioResource walkRes = audioMgr.createAudioResource("grassWalk.wav", AudioResourceType.AUDIO_SAMPLE);
    	walkSound = new Sound(walkRes, SoundType.SOUND_EFFECT, 50, true);
    	walkSound.initialize(audioMgr);
    	walkSound.setMaxDistance(10.0f);
    	walkSound.setMinDistance(5.0f);
    	walkSound.setRollOff(5.0f);

		// Goblin Sound
		AudioResource gruntRes = audioMgr.createAudioResource("goblinLaugh.wav", AudioResourceType.AUDIO_SAMPLE);
		goblinSound = new Sound(gruntRes, SoundType.SOUND_EFFECT, 100, false);
		goblinSound.initialize(audioMgr);
		goblinSound.setMaxDistance(10.0f);
    	goblinSound.setMinDistance(5.0f);
    	goblinSound.setRollOff(5.0f);
		//goblinSound.play();


		AudioResource musicRes = audioMgr.createAudioResource("BackgroundMusic.wav", AudioResourceType.AUDIO_SAMPLE);
		backgroundMusic = new Sound(musicRes, SoundType.SOUND_MUSIC,5, true); 
		backgroundMusic.initialize(audioMgr);

		
		

	}

	@Override
	public void buildObjects() {
		// Terrain
		terrain = new GameObject(GameObject.root(), terrainShape, terrainTex);
		terrain.setLocalScale(new Matrix4f().scaling(300f, 40f, 300f));
		terrain.setLocalTranslation(new Matrix4f().translation(0f, 0f, 0f));
		terrain.getRenderStates().setTiling(2);
		terrain.getRenderStates().setTileFactor(75);
		terrain.setIsTerrain(true);
		terrain.setHeightMap(new TextureImage("../terrain/Heightmap.png"));

		// Avatar
		avatar = new Avatar(GameObject.root(), avatarAnimShape, avatarTextures[0], this);
		avatar.setLocalTranslation(new Matrix4f().translation(10f, 0f, 0f));
		avatar.setLocalRotation(new Matrix4f().rotateY((float)Math.toRadians(180)));

		// Tower
		tower = new GameObject(GameObject.root(), towerShape, towerTex);
		tower.getRenderStates().hasLighting(true);
		tower.setLocalScale(new Matrix4f().scaling(8f));
		tower.setLocalTranslation(new Matrix4f().translation(0f, 0f, 0f));

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
		int sky = engine.getSceneGraph().loadCubeMap("clouds");

		engine.getSceneGraph().setActiveSkyBoxTexture(sky);
		engine.getSceneGraph().setSkyBoxEnabled(true);
	}

	@Override
	public void initializeGame() {
		(engine.getRenderSystem()).setWindowDimensions(1900, 1000);
		cam = new CameraOrbit3D((engine.getRenderSystem()).getViewport("MAIN").getCamera(), avatar);

		physicsEngine = engine.getSceneGraph().getPhysicsEngine();
		physicsEngine.setGravity(new float[]{0f, -9.8f, 0f});
		//engine.enablePhysicsWorldRender();

		double[] towerTransform = toDoubleArray(tower.getLocalTranslation().get(new float[16]));
		float[] size = new float[]{ 4f, 20f, 4f };
		PhysicsObject towerBody = engine.getSceneGraph().addPhysicsBox(0f, towerTransform, size);
		tower.setPhysicsObject(towerBody);

		// Initialize Network Client Manager
		setupNetworking();

		// Initialize Enemies
		setupEnemies();

		// Map key bindings
		mapKeyBindings();

		// Initialize game states
		lastFrameTime = System.currentTimeMillis();
		currFrameTime = System.currentTimeMillis();
		elapseFrameTime = 0f;

		backgroundMusic.play();

	}

	@Override
	public void update() {
		// Calculate time since last update call and update game actions
		lastFrameTime = currFrameTime;
		currFrameTime = System.currentTimeMillis();
		elapseFrameTime = (currFrameTime - lastFrameTime) / 100f;

		im.update((float) elapseFrameTime);

		avatarAnimShape.updateAnimation();

		setEarParameters();

		// Update game states
		updateStates((float) elapseFrameTime);

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
		FwdAction moveForward = new FwdAction(avatar, this, true);
		FwdAction moveBack = new FwdAction(avatar, this,  false);
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
		try {
			ghostManager = new GhostManager(this);
			clientManager = new ClientManager(InetAddress.getByName(serverAddress), serverPort, this);
		}
		catch (IOException e) {
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
	private void processNetworking() {
		if (clientManager != null) {
			clientManager.processPackets();
		}
	}

	/**
	 * Initializes the enemy manager and spawns initial enemies
	 */
	private void setupEnemies() {
		enemyManager = new EnemyManager(goblinAnimShape, goblinTex, this);
		for (int i = 0; i < 5; i++) {
			enemyManager.spawnEnemy();
		}
	}

	/**
	 * Updates the game states and parameters
	 */
	private void updateStates(float elapseFrameTime) {
		cam.update();
		enemyManager.update(elapseFrameTime);

		if ((System.currentTimeMillis() - avatar.getLastInputTime()) > 500) {
			if (!"IDLE".equals(avatar.getCurrentAnimation())) {
				avatar.getAnimatedShape().playAnimation("IDLE", 0.25f, AnimatedShape.EndType.LOOP, 0);
				avatar.setCurrentAnimation("IDLE");
				avatar.setWalking(false);
				if (walkSound.getIsPlaying()) {
					walkSound.stop();
				}
			}
		}
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
	 * Converts doubles array to float array
	 */
	public float[] toFloatArray(double[] arr)
	{ if (arr == null) return null;
		int n = arr.length;
		float[] ret = new float[n];
		for (int i = 0; i < n; i++)
		{ ret[i] = (float)arr[i];
		}
		return ret;
	}

	/**
	 * Converts the float array to a doubles array
	 */
	public double[] toDoubleArray(float[] arr)
	{ if (arr == null) return null;
		int n = arr.length;
		double[] ret = new double[n];
		for (int i = 0; i < n; i++)
		{ ret[i] = (double)arr[i];
		}
		return ret;
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
	 * @return the client manager
	 */
	public ClientManager getClientManager() {
		return clientManager;
	}

	/**
	 * @return the ghost manager
	 */
	public GhostManager getGhostManager() {
		return ghostManager;
	}

	/**
	 * @return the enemy manager
	 */
	public EnemyManager getEnemyManager() {
		return enemyManager;
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
		return avatarAnimShape;
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
	 * @return the terrain
	 */
	public GameObject getTerrain() {
		return terrain;
	}

	public Sound getWalkSound() {
		return walkSound;
	}

	public Sound getGoblinSound() {
		return goblinSound;
	}

	/**
	 * Set ear parameters
	 */
	public void setEarParameters() {
    	Camera camera = engine.getRenderSystem().getViewport("MAIN").getCamera();
    	audioMgr.getEar().setLocation(camera.getLocation());
		audioMgr.getEar().setOrientation(camera.getN(), new Vector3f(0.0f, 1.0f, 0.0f));
    }


}
