package com.raven.engine;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;

import java.io.File;
import java.util.*;

import com.raven.engine.database.GameDatabase;
import com.raven.engine.graphics3d.GameWindow;
import com.raven.engine.graphics3d.ModelData;
import com.raven.engine.graphics3d.ModelReference;
import com.raven.engine.graphics3d.PlyModelData;
import com.raven.engine.input.Keyboard;
import com.raven.engine.input.Mouse;
import com.raven.engine.worldobject.WorldObject;
import org.lwjgl.glfw.GLFW;

public class GameEngine implements Runnable {
    private static GameEngine engine;

    public static GameEngine Launch(Game game) {
        GameEngine engine = new GameEngine(game);

        GameEngine.engine = engine;

        engine.window = new GameWindow(engine);

        engine.thread = new Thread(engine);
        engine.thread.start();

        return engine;
    }

    public static GameEngine getEngine() {
        return engine;
    }

    private Game game;
    private Thread thread;
    private GameWindow window;
    private List<WorldObject> oldMouseList = new ArrayList<>();
    private GameDatabase gdb;
    private Map<String, ModelData> modelDataMap = new HashMap<>();
    private float deltaTime;
    private long systemTime;
    private Mouse mouse = new Mouse();
    private Keyboard keyboard = new Keyboard();

    // Accessors
    public Thread getThread() {
        return thread;
    }

    public Game getGame() {
        return game;
    }

    private GameEngine(Game game) {
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

    public void breakThread() {
        System.out
                .println("Breaking Thread. Was it alive? " + thread.isAlive());

        game.breakdown();
    }

    int frame = 0;
    float framesdt = 0;

    @Override
    public void run() {
        System.out.println("Started Run");

        System.out.println("Starting OpenGL");
        window.create();

        System.out.println("Loading Assets");
        loadDatabase();

        game.setup();

        game.transitionScene(game.loadInitialScene());

        ModelReference.compileBuffer();
        window.printErrors("Compile Buffer Error: ");

        while (game.isRunning()
                && !glfwWindowShouldClose(window.getWindowHandler())) {

            long start = System.nanoTime();


            if (GameProperties.supportsOpenGL4()) {
                input(deltaTime);
                window.printErrors("Input MS Error: ");

                if (GameProperties.getMultisampleCount() != 0) {
                    draw4ms();
                    window.printErrors("Draw MSAA Error: ");
                } else {
                    draw4();
                    window.printErrors("Draw FXAA Error: ");
                }
            } else {
                input(deltaTime);
                draw2();
                window.printErrors("Draw Basic Error: ");
            }

            game.update(deltaTime);
            window.printErrors("Update Error: ");

            if (frame % 60 == 0) {
                System.out.println("FPS: " + 1000f / (framesdt / 60f) + " MPF: " + framesdt / 60f);
                framesdt = 0;
            }

            glfwSwapBuffers(window.getWindowHandler()); // swap the color buffers

            window.printErrors("Swap Error: ");

            long currentTime = System.nanoTime();
            deltaTime = (currentTime - start) / 1000000.0f;
            systemTime = currentTime / 1000000L;

            framesdt += deltaTime;
            frame++;

            window.printErrors("Errors: ");
        }

        System.out.println("End Run");

        game.breakdown();

        window.destroy();

        System.out.println("Exit");

        System.exit(0);
    }

    private void draw4ms() {
        game.renderWorld4ms(window);

        window.getIDShader().useProgram();
        window.drawQuad();

//        if (true) {
//
//        } else if (true) {
//            window.getComplexDirectionalLightShader().useProgram();
//            window.drawQuad();
//        } else {
//            // window.getWorldMSShader().blitComplexValue();
//        }
    }

    private void draw4() {
        game.renderWorld4(window);

//        window.getIDShader().useProgram();
//        window.drawQuad();
    }

    private void draw2() {
        game.renderWorld2(window);

        // render world as input
    }

    private List<WorldObject> newList = new ArrayList();
    private void input(float delta) {
        glfwPollEvents();

        int id = 0;

        if (GameProperties.getMultisampleCount() == 0) {
            id = window.getWorldShader().getWorldObjectID();
        } else {
            id = window.getIDShader().getWorldObjectID();
        }

        if (id != 0) {
            WorldObject hover = WorldObject.getWorldObjectFromID(id);

            // clicks - might cause a problem with the order of enter and leave
            if (mouse.isLeftButtonClick()) {
                hover.onMouseClick();
                mouse.setLeftButtonClick(false);
            }

            // hover
            newList.clear();
            newList.addAll(hover.getParentWorldObjectList());
            newList.add(hover);

            for (WorldObject o : oldMouseList) {
                if (newList.contains(o))
                    o.checkMouseMovement(true, delta);
                else {
                    o.checkMouseMovement(false, delta);
                }
            }

            oldMouseList.clear();
            oldMouseList.addAll(newList);
        } else {
            for (WorldObject o : oldMouseList) {
                o.checkMouseMovement(false, delta);
            }

            oldMouseList.clear();
        }
    }

    private void loadDatabase() {
        // load models

        File modelDirectory = new File(game.getMainDirectory() + File.separator + "models");
        loadModels(modelDirectory);

        // load database
        gdb = new GameDatabase();
        gdb.load();
    }

    private void loadModels(File base) {
        for (File f : base.listFiles()) {
            if (f.isFile()) {
                System.out.println(f.getPath());
                modelDataMap.put(f.getPath(), new PlyModelData(f.getPath()));
            } else if (f.isDirectory()) {
                loadModels(f);
            }
        }
    }

    public ModelData getModelData(String modelsrc) {
        return modelDataMap.get(game.getMainDirectory() + File.separator + modelsrc.replace("/", File.separator));
    }

    // input
    public void inputMouseButton(int button, int action, int mods) {
        mouse.buttonAction(button, action);
    }

    public void inputMouseMove(double xpos, double ypos) {
        if (mouse.isMiddleButtonDown())
            this.getGame().getCurrentScene().getCamera().rotate(xpos - mouse.getX(), ypos - mouse.getY());

        if (mouse.isRightButtonDown()) {
            this.getGame().getCurrentScene().getCamera().move(xpos - mouse.getX(), ypos - mouse.getY());
        }

        mouse.setPos(xpos, ypos);
    }

    public void inputKey(int key, int action, int mods) {
        if (GLFW.GLFW_KEY_ESCAPE == key && GLFW.GLFW_PRESS == action) {
            this.breakThread();
        }
    }

    public void inputScroll(double xoffset, double yoffset) {
        this.getGame().getCurrentScene().getCamera().zoom(yoffset);

    }
}
