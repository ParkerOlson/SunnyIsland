package com.raven.engine.graphics3d;

import com.raven.engine.GameEngine;
import com.raven.engine.GameProperties;
import com.raven.engine.graphics3d.shader.*;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.ARBImaging.GL_TABLE_TOO_LARGE;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_MULTISAMPLE;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL30.GL_INVALID_FRAMEBUFFER_OPERATION;
import static org.lwjgl.opengl.GL30.glBindBufferBase;
import static org.lwjgl.opengl.GL31.GL_UNIFORM_BUFFER;
import static org.lwjgl.opengl.GL31.glGetUniformBlockIndex;
import static org.lwjgl.opengl.GL45.GL_CONTEXT_LOST;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

public class GameWindow {

    // The window handle
    private long window;

    private int ms_count = 4;

    private WorldMSShader worldMSShader;
    private WaterRefractionShader waterRefractionShader;
    private WaterReflectionShader waterReflectionShader;
    private WorldWaterShader worldWaterShader;
    private BloomShader bloomShader;
    private IDShader idShader;
    private SimpleDirectionalLightShader simpleDirLightShader;
    private ComplexDirectionalLightShader complexDirLightShader;
    private ComplexSampleStencilShader complexSampleStencilShader;

    private WorldShader worldShader;
    private DirectionalLightShader dirLightShader;
    private WaterShader waterShader;
    private CombinationShader combinationShader;
    private FXAAShader fxaaShader;

    private BasicShader basicShader;

    private int sun_light_buffer_handel, matrices_buffer_handel;

    private GameEngine engine;

    private Map<Integer, Boolean> keyboard = new HashMap<>();
    private Map<Integer, Boolean> mouse = new HashMap<>();
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

        // Configure GLFW
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);

        ms_count = GameProperties.getMultisampleCount();
        if (ms_count != 0)
            glfwWindowHint(GLFW_SAMPLES, ms_count);

        // Create the window
        window = glfwCreateWindow(GameProperties.getScreenWidth(),
                GameProperties.getScreenHeight(),
                engine.getGame().getTitle(), glfwGetPrimaryMonitor(),
                NULL);
        if (window == NULL)
            throw new RuntimeException("Failed to create the GLFW window");

        // Get the thread stack and push a new frame
        try (MemoryStack stack = stackPush()) {
            IntBuffer pWidth = stack.mallocInt(1);
            IntBuffer pHeight = stack.mallocInt(1);

            // Get the window size passed to glfwCreateWindow
            glfwGetWindowSize(window, pWidth, pHeight);

            // Get the resolution of the primary monitor
            GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

            // Center the window
            glfwSetWindowPos(window, (vidmode.width() - pWidth.get(0)) / 2,
                    (vidmode.height() - pHeight.get(0)) / 2);
        } // the stack frame is popped automatically

        // Make the OpenGL context current
        glfwMakeContextCurrent(window);

        // Enable v-sync
        glfwSwapInterval(0);

        // Make the window visible
        glfwShowWindow(window);

        // Key and Mouse input
        glfwSetInputMode(window, GLFW_STICKY_MOUSE_BUTTONS, 1);
        glfwSetInputMode(window, GLFW_STICKY_KEYS, 1);
        glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> engine.inputKey(key, action, mods));
        glfwSetMouseButtonCallback(window, (window, button, action, mods) -> engine.inputMouseButton(button, action, mods));
        glfwSetCursorPosCallback(window, (window, xpos, ypos) -> engine.inputMouseMove(xpos, ypos));
        glfwSetScrollCallback(window, (window, xoffset, yoffset) -> engine.inputScroll(xoffset, yoffset));

        GL.createCapabilities();

        if (GameProperties.supportsOpenGL4()) {
            // Buffer Data
            sun_light_buffer_handel = glGenBuffers();
            glBindBuffer(GL_UNIFORM_BUFFER, sun_light_buffer_handel);
            glBindBufferBase(GL_UNIFORM_BUFFER, Shader.LIGHT, sun_light_buffer_handel);
            glBufferData(GL_UNIFORM_BUFFER, new float[4 * 3 + 1 + 16 * 2], GL_DYNAMIC_DRAW);
            glBindBuffer(GL_UNIFORM_BUFFER, 0);

            matrices_buffer_handel = glGenBuffers();
            glBindBuffer(GL_UNIFORM_BUFFER, matrices_buffer_handel);
            glBindBufferBase(GL_UNIFORM_BUFFER, Shader.MATRICES, matrices_buffer_handel);
            glBufferData(GL_UNIFORM_BUFFER, new float[4 * 4 * 7], GL_DYNAMIC_DRAW);
            glBindBuffer(GL_UNIFORM_BUFFER, 0);

            if (ms_count != 0) {
                // Shaders
                worldMSShader = new WorldMSShader();
//                waterRefractionShader = new WaterRefractionShader();
//                waterReflectionShader = new WaterReflectionShader();
//                worldWaterShader = new WorldWaterShader(worldMSShader);
//                bloomShader = new BloomShader();
                idShader = new IDShader();
                complexSampleStencilShader = new ComplexSampleStencilShader();
                simpleDirLightShader = new SimpleDirectionalLightShader(complexSampleStencilShader);
                complexDirLightShader = new ComplexDirectionalLightShader(complexSampleStencilShader);

                // Enable multisample
                glEnable(GL_MULTISAMPLE);
            } else {
                worldShader = new WorldShader();
                dirLightShader = new DirectionalLightShader();
                waterReflectionShader = new WaterReflectionShader();
                waterShader = new WaterShader(worldShader);
                combinationShader = new CombinationShader(waterShader);
                fxaaShader = new FXAAShader();
            }
        } else {
            basicShader = new BasicShader();
        }

        // Enable depth test
        glEnable(GL_DEPTH_TEST);

        // Enable face culling
        glEnable(GL_CULL_FACE);
        glCullFace(GL_BACK);

        // Setup ModelReference Error
        ModelReference.loadBlankModel();
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

    public WorldMSShader getWorldMSShader() {
        return worldMSShader;
    }

    public BloomShader getBloomShader() {
        return bloomShader;
    }

    public WorldWaterShader getWorldWaterShader() {
        return worldWaterShader;
    }

    public IDShader getIDShader() {
        return idShader;
    }

    public WaterRefractionShader getWaterRefractionShader() {
        return waterRefractionShader;
    }

    public WaterReflectionShader getWaterReflectionShader() {
        return waterReflectionShader;
    }

    public ComplexSampleStencilShader getComplexSampleStencilShader() {
        return complexSampleStencilShader;
    }

    public ComplexDirectionalLightShader getComplexDirectionalLightShader() {
        return complexDirLightShader;
    }

    public SimpleDirectionalLightShader getSimpleDirLightShader() {
        return simpleDirLightShader;
    }

    public WorldShader getWorldShader() {
        return worldShader;
    }

    public DirectionalLightShader getDirLightShader() {
        return dirLightShader;
    }

    public WaterShader getWaterShader() {
        return waterShader;
    }

    public CombinationShader getCombinationShader() {
        return combinationShader;
    }

    public FXAAShader getFXAAShader() {
        return fxaaShader;
    }

    public BasicShader getBasicShader() {
        return basicShader;
    }

    public int getLightHandel() {
        return sun_light_buffer_handel;
    }

    public int getMatricesHandel() {
        return matrices_buffer_handel;
    }

    public void drawQuad() {
        // Draw FBO
        glEnableVertexAttribArray(0);
        ModelReference.getBlankModel().draw();
        glDisableVertexAttribArray(0);
    }

    public int getMultisampleCount() {
        return ms_count;
    }

    public void endActiveShader() {
        if (activeShader != null)
            activeShader.endProgram();
    }

    public void printErrors() {
        printErrors("");
    }

    public void printErrors(String tag) {
        int err;
        while ((err = glGetError()) != GL_NO_ERROR) {
            switch (err) {
                case GL_INVALID_ENUM:
                    System.out.println(tag + "GL_INVALID_ENUM 0x" + Integer.toHexString(err));
                    break;
                case GL_INVALID_VALUE:
                    System.out.println(tag + "GL_INVALID_VALUE 0x" + Integer.toHexString(err));
                    break;
                case GL_INVALID_OPERATION:
                    System.out.println(tag + "GL_INVALID_OPERATION 0x" + Integer.toHexString(err));
                    break;
                case GL_STACK_OVERFLOW:
                    System.out.println(tag + "GL_STACK_OVERFLOW 0x" + Integer.toHexString(err));
                    break;
                case GL_OUT_OF_MEMORY:
                    System.out.println(tag + "GL_OUT_OF_MEMORY 0x" + Integer.toHexString(err));
                    break;
                case GL_INVALID_FRAMEBUFFER_OPERATION:
                    System.out.println(tag + "GL_INVALID_FRAMEBUFFER_OPERATION 0x" + Integer.toHexString(err));
                    break;
                case GL_CONTEXT_LOST:
                    System.out.println(tag + "GL_CONTEXT_LOST 0x" + Integer.toHexString(err));
                    break;
                case GL_TABLE_TOO_LARGE:
                    System.out.println(tag + "GL_TABLE_TOO_LARGE 0x" + Integer.toHexString(err));
                    break;
                default:
                    System.out.println(tag + "0x" + Integer.toHexString(err));
                    break;
            }
        }
    }

    public Shader getActiveShader() {
        return activeShader;
    }

    public void setActiveShader(Shader activeShader) {
        this.activeShader = activeShader;
    }
}
