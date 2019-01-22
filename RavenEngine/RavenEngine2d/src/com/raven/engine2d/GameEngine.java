package com.raven.engine2d;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;
import static org.lwjgl.opengl.GL11.glFinish;

import java.io.File;
import java.util.*;

import com.codedisaster.steamworks.*;
import com.raven.engine2d.database.GameDataTable;
import com.raven.engine2d.database.GameDatabase;
import com.raven.engine2d.graphics2d.GameWindow;
import com.raven.engine2d.graphics2d.ScreenQuad;
import com.raven.engine2d.graphics2d.sprite.SpriteAnimation;
import com.raven.engine2d.graphics2d.sprite.SpriteSheet;
import com.raven.engine2d.input.Keyboard;
import com.raven.engine2d.input.Mouse;
import com.raven.engine2d.worldobject.GameObject;

import javax.sound.sampled.*;

public class GameEngine<G extends Game<G>> {

    public static <G extends Game<G>> GameEngine Launch(G game) {
        GameEngine<G> engine = new GameEngine<>(game);

        engine.window = new GameWindow(engine);

        engine.run();

        return engine;
    }

    private G game;
    private GameWindow window;
    private List<GameObject> oldMouseList = new ArrayList<>();
    private GameDatabase gdb;
    private Map<String, SpriteSheet> spriteSheetsMap = new HashMap<>();
    private Map<String, SpriteAnimation> animationMap = new HashMap<>();
    private Map<String, Clip> audioMap = new HashMap<>();
    private float deltaTime;
    private long systemTime;
    private boolean steamInit = false;
    private Mouse mouse = new Mouse();
    private Keyboard keyboard = new Keyboard();

    // Accessors
    public G getGame() {
        return game;
    }

    private GameEngine(G game) {
        this.game = game;

        game.setEngine(this);

        systemTime = System.nanoTime() / 1000000L;
    }

    public float getDeltaTime() {
        return deltaTime;
    }

    public long getSystemTime() {
        return systemTime;
    }

    public GameWindow getWindow() {
        return window;
    }

    public GameDatabase getGameDatabase() {
        return gdb;
    }

    public Mouse getMouse() {
        return mouse;
    }

    private int frame = 0;
    private float framesdt = 0;


    public void run() {
        System.out.println("Started Run");

        System.out.println("Starting Steam API");
        try {
            steamInit = SteamAPI.init();
        } catch (SteamException se) {
            System.out.println("Couldn't load steam native libraries");
        }

        if (!steamInit) {
            System.out.println("Steam failed to start");
        } else {
            System.out.println("Steam started");
        }

        System.out.println("Starting OpenGL");
        window.create();
        window.printErrors("Create Error: ");

        System.out.println("Loading Assets");
        loadDatabase();

        game.setup();

        game.transitionScene(game.loadInitialScene());

        // Archaic code, but it should be safe
        ScreenQuad.loadQuad();
        ScreenQuad.compileBuffer(window);

        window.printErrors("Compile Buffer Error: ");

        while (game.isRunning()
                && !glfwWindowShouldClose(window.getWindowHandler())) {

            long start = System.nanoTime();

            if (SteamAPI.isSteamRunning()) {
                SteamAPI.runCallbacks();
            }

            input(deltaTime);

            game.update(deltaTime);

            window.printErrors("Update Error: ");

            draw();

            window.printErrors("Draw Error: ");

//            if (frame % 60 == 0) {
//                System.out.println("FPS: " + 1000f / (framesdt / 60f) + " MPF: " + framesdt / 60f);
//                framesdt = 0;
//            }

            glfwSwapBuffers(window.getWindowHandler()); // swap the color buffers

            glFinish();

            window.printErrors("Swap Error: ");

            long currentTime = System.nanoTime();
            deltaTime = (currentTime - start) / 1000000.0f;
            systemTime = currentTime / 1000000L;

            if (deltaTime > 20) {
                System.out.println("Lag: " + deltaTime);
            }

//            framesdt += deltaTime;
//            frame++;

            window.printErrors("Errors: ");
        }

        System.out.println("End Run");

        game.breakdown();

        window.destroy();

        SteamAPI.shutdown();

        System.out.println("Exit");

        System.exit(0);
    }

    private void draw() {
        game.draw(window);
    }

    private List<GameObject> newList = new ArrayList<>();

    private void input(float delta) {
        glfwPollEvents();

        int id = window.getCompilationShader().getWorldObjectID();

        GameObject hover = null;
        if (id != 0) {
            hover = GameObject.getGameObjectFromID(id);
        }

        if (hover != null) {

            newList.clear();
            newList.addAll(hover.getParentGameObjectList());
            newList.add(hover);

            // clicks - might cause a problem with the order of enter and leave
            if (mouse.isLeftButtonClick()) {
                for (GameObject o : oldMouseList) {
                    if (newList.contains(o))
                        o.onMouseClick();
                }

                mouse.setLeftButtonClick(false);
            }

            // hover
            for (GameObject o : oldMouseList) {
                if (newList.contains(o))
                    o.checkMouseMovement(true, delta);
                else {
                    o.checkMouseMovement(false, delta);
                }
            }

            for (GameObject o : newList) {
                if (!oldMouseList.contains(o)) {
                    o.checkMouseMovement(true, delta);
                }
            }

            oldMouseList.clear();
            oldMouseList.addAll(newList);

        } else {
            for (GameObject o : oldMouseList) {
                o.checkMouseMovement(false, delta);
            }

            oldMouseList.clear();
        }
    }

    private void loadDatabase() {
        // load models

        File modelDirectory = new File(game.getMainDirectory() + File.separator + "sprites");
        loadSprites(modelDirectory);

        File animationDir = new File(game.getMainDirectory() + File.separator + "animations");
        loadAnimations(animationDir);

        // load database
        gdb = new GameDatabase();
        gdb.load("data");
    }

    private void loadSprites(File base) {
        for (File f : base.listFiles()) {
            if (f.isFile()) {
//                System.out.println(f.getPath());

                spriteSheetsMap.put(f.getPath(), new SpriteSheet(this, f));
            } else if (f.isDirectory()) {
                loadSprites(f);
            }
        }
    }

    private String fixPath(String path) {
        String fixed;

        fixed = path.replace('\\', File.separatorChar);
        fixed = fixed.replace('/', File.separatorChar);

        return fixed;
    }

    public SpriteSheet getSpriteSheet(String spriteSrc) {
        SpriteSheet sheet = spriteSheetsMap.get(game.getMainDirectory() + File.separator + fixPath(spriteSrc));

        if (sheet == null) {
            throw new NoSuchElementException();
        }

        return sheet;
    }

    private void loadAnimations(File base) {
        GameDatabase d = new GameDatabase();
        d.load("animations");

        for (GameDataTable table : d.getTables()) {
            animationMap.put(table.getName(), new SpriteAnimation(table));
        }
    }

    public SpriteAnimation getAnimation(String name) {
        SpriteAnimation animation = animationMap.get(name);

        if (animation == null) {
            throw new NoSuchElementException();
        }

        return animation;
    }

    public Clip getAudioClip(String audioName) {

        if (audioMap.containsKey(audioName)) {
            return audioMap.get(audioName);
        } else {

            try {
                File f = new File(GameProperties.getMainDirectory() + File.separator + "audio" + File.separator + audioName);

                AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(f);
                AudioFormat af = audioInputStream.getFormat();
                int size = (int) (audioInputStream.getFrameLength() * af.getFrameSize());
                byte[] audioBytes = new byte[size];
                audioInputStream.read(audioBytes, 0, size);
                Clip clip = (Clip) AudioSystem.getLine(new DataLine.Info(Clip.class, af, size));

//                System.out.println(audioName);
//                System.out.println(audioInputStream.getFrameLength());

                clip.open(af, audioBytes, 0, size);

                audioMap.put(audioName, clip);

//                System.out.println(audioName);
//                System.out.println(clip.getLineInfo());
//                System.out.println(Arrays.stream(clip.getControls()).map(Control::getType).collect(Collectors.toList()));

                return clip;

            } catch (Exception e) {
                e.printStackTrace();
                System.exit(1);
            }
        }

        return null;
    }

    final public boolean changeSongVolume(int volume, Clip clip) {
        if (clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
            FloatControl gainControl = (FloatControl) clip
                    .getControl(FloatControl.Type.MASTER_GAIN);

            float dB = (float) (Math.log(volume / 100f) / Math.log(10.0) * 20.0);
            gainControl.setValue(dB);

            return true;
        } else if (clip.isControlSupported(FloatControl.Type.VOLUME)) {
            FloatControl gainControl = (FloatControl) clip
                    .getControl(FloatControl.Type.VOLUME);

            float dB = (float) (Math.log(volume / 100f) / Math.log(10.0) * 20.0);
            gainControl.setValue(dB);

            return true;
        }

        return false;
    }

    // input
    public void inputMouseButton(int button, int action, int mods) {
        mouse.buttonAction(button, action);
    }

    public void inputMouseMove(double xpos, double ypos) {

        if (mouse.isMiddleButtonDown()) {
        }

        if (mouse.isRightButtonDown()) {
        }

        mouse.setPos(xpos, ypos);
    }

    public void inputKey(int key, int action, int mods) {
        game.getCurrentScene().inputKey(key, action, mods);
    }

    public void inputScroll(double xoffset, double yoffset) {

    }
}
