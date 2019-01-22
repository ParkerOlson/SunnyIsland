package com.raven.engine2d.launcher;

import com.raven.engine2d.Game;
import com.raven.engine2d.GameEngine;
import com.raven.engine2d.GameProperties;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GLCapabilities;

import javax.swing.*;
import java.awt.*;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.system.MemoryUtil.NULL;

/**
 * Created by cookedbird on 11/26/17.
 */
public class GameLauncher {

    public static <G extends com.raven.engine2d.Game> void Open(G game) {
        OpenAdvanced(game); // assume opengl 4 is supported
    }

    // OpenGL 4.0
    private static <G extends Game> void OpenAdvanced(G game) {
        // Doesn't work on linux and nvidia
        // correction, didn't work on nvidia optimus?
        // TODO allow selection
        GraphicsEnvironment g = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice[] devices = g.getScreenDevices();
        for (GraphicsDevice device : devices) {
            System.out.println(device.getIDstring() + ":");
            for (DisplayMode mode : device.getDisplayModes()) {
                System.out.println(mode.getWidth() + "x" + mode.getHeight());
            }
        }

        // Window
        JFrame winMain = new JFrame(game.getTitle());

        winMain.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        winMain.setResizable(false);
        winMain.setLocationRelativeTo(null);
        winMain.setLayout(new GridLayout(3, 1));

        // Multisample
        Container conMultisample = new Container();
        conMultisample.setLayout(new FlowLayout());
        winMain.add(conMultisample);

        JLabel lblMultisampleCount = new JLabel("Scaling");
        conMultisample.add(lblMultisampleCount);

        JComboBox cbMultisampleCount = new JComboBox(new String[]{"1", "2", "3", "4"});
        conMultisample.add(cbMultisampleCount);
        cbMultisampleCount.setSelectedIndex(1);

        // Launch Button
        Container conLaunch = new Container();
        conLaunch.setLayout(new FlowLayout());
        winMain.add(conLaunch);

        JButton btnLaunch = new JButton("Launch");
        conLaunch.add(btnLaunch);

        btnLaunch.addActionListener(actionEvent -> {

            GameProperties.setScaling(Integer.parseInt(cbMultisampleCount.getSelectedItem().toString()));

            GameEngine.Launch(game);
            winMain.setVisible(false);
        });

        // Show
        winMain.pack();
        winMain.setVisible(true);
    }
}
