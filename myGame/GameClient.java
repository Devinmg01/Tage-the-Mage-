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
	private Camera miniMap;
	private Viewport main, mini;
	private Light avatarLight, towerLight, healingLight;
	private Avatar avatar;
	private Tower tower;
	private GameObject terrain;
	private ObjShape terrainShape, avatarShape, towerShape, goblinShape;
	private AnimatedShape avatarAnimShape, goblinAnimShape;
	private TextureImage terrainTex, towerTex, goblinTex;
	private Vector3f hud1Color, hud2Color;
	private TextureImage[] avatarTextures = new TextureImage[4];
	private Sound walkSound, goblinSound, backgroundMusic;
	
	private double lastFrameTime, currFrameTime, elapseFrameTime;
	private final String serverAddress;
	private final int serverPort;
	private int skinIndex = 0; 


	public GameClient(String serverAddress, int serverPort, int skinIndex) {
		super();
		this.serverAddress = serverAddress;
		this.serverPort = serverPort;
		this.skinIndex = skinIndex;
	}

	public static void main(String[] args) {
		GameClient game = new GameClient(args[0], Integer.parseInt(args[1]), Integer.parseInt(args[2]));
		engine = new Engine(game);
		game.initializeSystem();
		game.game_loop();
	}

	@Override
	public void createViewports() {
		(engine.getRenderSystem()).addViewport("MAIN",0,0,1,1);
		(engine.getRenderSystem()).addViewport("MINI",0.8f,0,0.2f,0.3f);

		main = (engine.getRenderSystem()).getViewport("MAIN");
		mini = (engine.getRenderSystem()).getViewport("MINI");

		mini.setHasBorder(true);
		mini.setBorderWidth(3);
		mini.setBorderColor(1,1,1);

		cam = new CameraOrbit3D(main.getCamera(), avatar);
		miniMap = mini.getCamera();
		miniMap.setLocation(new Vector3f(0, 100, 0));
		miniMap.setN(new Vector3f(0, -1, 0));
		miniMap.setV(new Vector3f(0, 0, 1));
		miniMap.setU(new Vector3f(-1, 0, 0));
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
		avatarTextures[0] = new TextureImage("WizardUV1.png"); // Default
		avatarTextures[1] = new TextureImage("WizardUV2.png"); // Blue
		avatarTextures[2] = new TextureImage("WizardUV3.png"); // Red
		avatarTextures[3] = new TextureImage("WizardUV4.png"); // Green
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
		terrain.getRenderStates().hasLighting(true);
		terrain.getRenderStates().setTiling(2);
		terrain.getRenderStates().setTileFactor(75);
		terrain.setIsTerrain(true);
		terrain.setHeightMap(new TextureImage("../terrain/Heightmap.png"));

		// Avatar
		if (skinIndex < 0 || skinIndex > 3) {
			skinIndex = 0;
		}
		avatar = new Avatar(GameObject.root(), avatarAnimShape, avatarTextures[skinIndex], this);
		avatar.setLocalTranslation(new Matrix4f().translation(10f, 0f, 0f));
		avatar.setLocalRotation(new Matrix4f().rotateY((float)Math.toRadians(180)));

		// Tower
		tower = new Tower(GameObject.root(), towerShape, towerTex, this);
		tower.setLocalTranslation(new Matrix4f().translation(100f, 0f, 0f));
		tower.setLocalScale(new Matrix4f().scaling(4f));;

		// Hud Color
		hud1Color = new Vector3f(1, 0, 0);
		hud2Color = new Vector3f(0, 0, 1);
	}

	@Override
	public void initializeLights() {
		Light.setGlobalAmbient(0.5f, 0.5f, 0.5f);

		Light ambientLight = new Light();
		avatarLight = new Light();
		towerLight = new Light();
		healingLight = new Light();

		avatarLight.setType(Light.LightType.SPOTLIGHT);
		towerLight.setType(Light.LightType.SPOTLIGHT);
		healingLight.setType(Light.LightType.SPOTLIGHT);

		ambientLight.setLocation(new Vector3f(5.0f, 4.0f, 2.0f));
		avatarLight.setLocation(new Vector3f(10.0f, 2.0f, 0.0f));
		towerLight.setLocation(new Vector3f(0.0f, 12.0f, 0.0f));
		healingLight.setLocation(new Vector3f(-50.0f, 10.0f, 0.0f));

		avatarLight.setDirection(new Vector3f(0, -1, 0));
		towerLight.setDirection(new Vector3f(0, -1, 0));
		healingLight.setDirection(new Vector3f(0, -1, 0));

		avatarLight.setAmbient(0.0f, 0.0f, 0.0f);
		towerLight.setAmbient(0.0f, 0.0f, 0.0f);
		healingLight.setAmbient(0.0f, 0.0f, 0.0f);

		ambientLight.setDiffuse(0.0f, 0.0f, 0.0f);
		avatarLight.setDiffuse(0f, 1f, 0f);
		towerLight.setDiffuse(0f, 1f, 0f);
		healingLight.setDiffuse(1f, 0.5f, 1f);

		ambientLight.setSpecular(0.0f, 0.0f, 0.0f);
		avatarLight.setSpecular(0.0f, 0.0f, 0.0f);
		towerLight.setSpecular(0.0f, 0.0f, 0.0f);
		healingLight.setSpecular(0.0f, 0.0f, 0.0f);

		engine.getSceneGraph().addLight(ambientLight);
		engine.getSceneGraph().addLight(avatarLight);
		engine.getSceneGraph().addLight(towerLight);
		engine.getSceneGraph().addLight(healingLight);
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
		im = engine.getInputManager();
		physicsEngine = engine.getSceneGraph().getPhysicsEngine();
		//engine.enablePhysicsWorldRender();

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
		ExitGameAction exitGame = new ExitGameAction(this);
		ToggleSpotlightsAction toggleSpotlights = new ToggleSpotlightsAction(this);

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
		im.associateActionWithAllKeyboards(net.java.games.input.Component.Identifier.Key.L, toggleSpotlights,
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
			clientManager.sendCreateMessage(skinIndex, avatar.getWorldLocation());
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
		avatarLight.setLocation(avatar.getWorldLocation().add(0, 2, 0));
		tower.update();

		if (avatar.getLocalLocation().distance(new Vector3f(-50, 0, 0)) < 5.5f) {
			avatar.heal();
			healingLight.setDiffuse(0f, 0.5f, 0f);
		}
		else {
			healingLight.setDiffuse(1f, 0.5f, 0f);
		}

		if ((System.currentTimeMillis() - avatar.getLastInputTime()) > 500) {
			if (!"IDLE".equals(avatar.getCurrentAnimation())) {
				avatar.getAnimatedShape().playAnimation("IDLE", 0.5f, AnimatedShape.EndType.LOOP, 0);
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
		float width = main.getActualWidth();
		float height = main.getActualHeight();
		String hud2Str = "";

		if (tower.getHealth() <= 0) {
			hud2Str = "GAME OVER";
		}
		else {
			hud2Str = "Tower Health: " + tower.getHealth();
		}
		String hud1Str = "Avatar Health: " + avatar.getHealth();

		(engine.getHUDmanager()).setHUD1(hud1Str, hud1Color,
				(int) ((width - (width / 2)) - ((float) (hud1Str.length() * 8) / 2)), 15);
		(engine.getHUDmanager()).setHUD2(hud2Str, hud2Color,
				(int) ((width - (width / 2)) - ((float) (hud2Str.length() * 8) / 2)), (int) (height - 30));
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
	 * @return the tower
	 */
	public Tower getTower() {
		return tower;
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

	/**
	 * @return the walking sound
	 */
	public Sound getWalkSound() {
		return walkSound;
	}

	/**
	 * @return the goblin sound
	 */
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

	/**
	 * @return the avatar light
	 */
	public Light getAvatarLight() {
		return avatarLight;
	}

	/**
	 * @return the tower light
	 */
	public Light getTowerLight() {
		return towerLight;
	}

	/**
	 * @return the healing light
	 */
	public Light getHealingLight() {
		return healingLight;
	}

	/**
	 * @return skin index
	 */
	public int getSkinIndex() {
		return skinIndex;
	}

}
