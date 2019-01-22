package com.raven.engine.scene;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.raven.engine.GameEngine;
import com.raven.engine.graphics3d.GameWindow;
import com.raven.engine.worldobject.WorldObject;

public class Layer {

    public enum Destination {Terrain, Water, Details};

	private Scene scene;
	private Destination destination;
	private List<WorldObject> gameObjectList = new CopyOnWriteArrayList<>();

	private GameWindow window;

	public Layer(Destination destination) {
		this.destination = destination;
		this.scene = scene;

		this.window = GameEngine.getEngine().getWindow();
	}

	public List<WorldObject> getGameObjectList() {
		return gameObjectList;
	}

	public void addWorldObject(WorldObject obj) {
		gameObjectList.add(obj);
	}

    public void setScene(Scene scene) {
        this.scene = scene;
    }


	public void update(float deltaTime) {
		for (WorldObject o : gameObjectList) {
			o.update(deltaTime);
		}
	}

	public Destination getDestination() {
	    return destination;
    }
}