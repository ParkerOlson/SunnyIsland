package com.raven.engine2d.graphics2d;

import com.raven.engine2d.GameEngine;
import com.raven.engine2d.GameProperties;
import com.raven.engine2d.database.GameData;
import com.raven.engine2d.graphics2d.shader.CompilationShader;
import com.raven.engine2d.graphics2d.shader.LayerShader;
import com.raven.engine2d.graphics2d.shader.Shader;
import com.raven.engine2d.graphics2d.shader.TextShader;
import com.raven.engine2d.util.math.Vector2i;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWImage;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GLCapabilities;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.List;
import java.util.Objects;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.ARBImaging.GL_TABLE_TOO_LARGE;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL45.GL_CONTEXT_LOST;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

public class GameWindow {

    // The window handle
    private long window;

    private LayerShader layerShader;
    private CompilationShader compilationShader;
    private TextShader textShader;

    private GameEngine engine;

    private Shader activeShader;

    public GameWindow(GameEngine engine) {
        this.engine = engine;
    }

    public void create() {
        // Setup an error callback. The default implementation
        // will print the error message in System.err.
        GLFWErrorCallback.createPrint(System.err).set();

        // Initialize GLFW.
        if (!glfwInit())
            throw new IllegalStateException("Unable to initialize GLFW");


        GameData res = engine.getGame().loadGameData("settings").get(0);


        // Configure GLFW
        glfwDefaultWindowHints();
        // this has caused so many problems, including with the steam overlay
        if (System.getProperty("os.name").toLowerCase().contains("mac")) {
            glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 4);
            glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 1);
            glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GL_TRUE);
            System.out.println("Setting Core Profile");
            glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        }
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
        glfwWindowHint(GLFW_REFRESH_RATE, GLFW_DONT_CARE);

        if (GameProperties.getWindowMode() == GameProperties.WINDOWED_BORDERLESS)
            glfwWindowHint(GLFW_DECORATED, GLFW_FALSE);
        else
            glfwWindowHint(GLFW_DECORATED, GLFW_TRUE);

//        long monitor = glfwGetPrimaryMonitor();
        long monitor = glfwGetMonitors().get(0);
        Objects.requireNonNull(glfwGetVideoModes(monitor)).stream().forEach(m -> GameProperties.addResolution(m.width(), m.height()));

        if (res.has("width") && GameProperties.getResolutionList().stream().anyMatch(v2 -> v2.x == res.getInteger("width")) &&
                res.has("height") && GameProperties.getResolutionList().stream().anyMatch(v2 -> v2.y == res.getInteger("height"))) {
            GameProperties.setDisplayWidth(res.getInteger("width"));
            GameProperties.setDisplayHeight(res.getInteger("height"));
        } else {
            List<Vector2i> reses = GameProperties.getResolutionList();
            Vector2i vec = reses.get(reses.size() - 1);
            GameProperties.setDisplayWidth(vec.x);
            GameProperties.setDisplayHeight(vec.y);
        }

        // Create the window
        if (GameProperties.getWindowMode() == GameProperties.FULLSCREEN)
            window = glfwCreateWindow(
                    GameProperties.getDisplayWidth(),
                    GameProperties.getDisplayHeight(),
                    engine.getGame().getTitle(),
                    monitor,
                    NULL);
        else
            window = glfwCreateWindow(
                    GameProperties.getDisplayWidth(),
                    GameProperties.getDisplayHeight(),
                    engine.getGame().getTitle(),
                    NULL,
                    NULL);

        try {
            int[] x, y;
            ByteBuffer buffer = STBImage.stbi_load(
                    GameProperties.getMainDirectory() + File.separator + "sprites" + File.separator + "Icon.png",
                    x = new int[1], y = new int[1], new int[1], STBImage.STBI_rgb_alpha);

            GLFWImage image = GLFWImage.malloc();
            image.set(x[0], y[0], buffer);
            GLFWImage.Buffer imagebf = GLFWImage.malloc(1);
            imagebf.put(0, image);
            glfwSetWindowIcon(window, imagebf);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (window == NULL)
            throw new RuntimeException("Failed to create the GLFW window");

        // Get the thread stack and push a new frame
        try (MemoryStack stack = stackPush()) {
            IntBuffer pWidth = stack.mallocInt(1);
            IntBuffer pHeight = stack.mallocInt(1);

            // Get the window size passed to glfwCreateWindow
            glfwGetWindowSize(window, pWidth, pHeight);

            // Center the window
//            glfwSetWindowPos(window,
//                    (GameProperties.getDisplayWidth() - pWidth.get(0)) / 2,
//                    (GameProperties.getDisplayHeight() - pHeight.get(0)) / 2);
        } // the stack frame is popped automatically

        // Make the OpenGL context current
        glfwMakeContextCurrent(window);

        // Enable v-sync?
        glfwSwapInterval(GameProperties.getVSync() ? 1 : 0);


        // Make the window visible
        glfwShowWindow(window);

        // Key and Mouse input
        glfwSetInputMode(window, GLFW_STICKY_MOUSE_BUTTONS, 1);
        glfwSetInputMode(window, GLFW_STICKY_KEYS, 1);
        glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> engine.inputKey(key, action, mods));
        glfwSetMouseButtonCallback(window, (window, button, action, mods) -> engine.inputMouseButton(button, action, mods));
        glfwSetCursorPosCallback(window, (window, xpos, ypos) -> engine.inputMouseMove(xpos, ypos));
        glfwSetScrollCallback(window, (window, xoffset, yoffset) -> engine.inputScroll(xoffset, yoffset));

        GLCapabilities cat = GL.createCapabilities();

        layerShader = new LayerShader(engine, this);
        compilationShader = new CompilationShader(engine, this);
        textShader = new TextShader(engine, this);

        // Enable depth test
        glEnable(GL_DEPTH_TEST);

        // Enable face culling
        glEnable(GL_CULL_FACE);
        glCullFace(GL_BACK);

        // Setup ScreenQuad
        ScreenQuad.loadQuad();
    }

    public void destroy() {
        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);

        // Terminate GLFW and free the error callback
        glfwTerminate();
        glfwSetErrorCallback(null).free();

        System.out.println("Window Destroyed");
    }

    public long getWindowHandler() {
        return window;
    }

    public LayerShader getLayerShader() {
//        layerShader.release();
//        layerShader = new LayerShader(engine, this);
        return layerShader;
    }

    public CompilationShader getCompilationShader() {
        return compilationShader;
    }

    public TextShader getTextShader() {
        return textShader;
    }

    public void drawQuad() {
        // Draw FBO

        glEnableVertexAttribArray(0);
        ScreenQuad.getBlankModel().draw(this);
        glDisableVertexAttribArray(0);
    }

    public void endActiveShader() {
        if (activeShader != null)
            activeShader.endProgram();
    }

    public void printErrors() {
        printErrors("");
    }

    public void printErrors(String tag) {
        if (false) {
            int err;
            while ((err = glGetError()) != GL_NO_ERROR) {
                switch (err) {
                    case GL_INVALID_ENUM:
                        System.out.println(tag + " GL_INVALID_ENUM 0x" + Integer.toHexString(err));
                        break;
                    case GL_INVALID_VALUE:
                        System.out.println(tag + " GL_INVALID_VALUE 0x" + Integer.toHexString(err));
                        break;
                    case GL_INVALID_OPERATION:
                        System.out.println(tag + " GL_INVALID_OPERATION 0x" + Integer.toHexString(err));
                        break;
                    case GL_STACK_OVERFLOW:
                        System.out.println(tag + " GL_STACK_OVERFLOW 0x" + Integer.toHexString(err));
                        break;
                    case GL_OUT_OF_MEMORY:
                        System.out.println(tag + " GL_OUT_OF_MEMORY 0x" + Integer.toHexString(err));
                        break;
                    case GL_INVALID_FRAMEBUFFER_OPERATION:
                        System.out.println(tag + " GL_INVALID_FRAMEBUFFER_OPERATION 0x" + Integer.toHexString(err));
                        break;
                    case GL_CONTEXT_LOST:
                        System.out.println(tag + " GL_CONTEXT_LOST 0x" + Integer.toHexString(err));
                        break;
                    case GL_TABLE_TOO_LARGE:
                        System.out.println(tag + " GL_TABLE_TOO_LARGE 0x" + Integer.toHexString(err));
                        break;
                    default:
                        System.out.println(tag + " 0x" + Integer.toHexString(err));
                        break;
                }
            }
        }
    }

    public Shader getActiveShader() {
        return activeShader;
    }

    public void setActiveShader(Shader activeShader) {
        this.activeShader = activeShader;
    }

    public void setDimension(int width, int height) {
        GameProperties.setDisplayWidth(width);
        GameProperties.setDisplayHeight(height);

        glfwSetWindowSize(window, width, height);

        layerShader.release();
        layerShader = new LayerShader(engine, this);

        compilationShader.release();
        compilationShader = new CompilationShader(engine, this);

    }
}
