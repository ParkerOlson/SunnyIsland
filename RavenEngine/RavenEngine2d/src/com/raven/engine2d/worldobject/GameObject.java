package com.raven.engine2d.worldobject;

import com.raven.engine2d.graphics2d.shader.LayerShader;
import com.raven.engine2d.graphics2d.shader.RenderTarget;
import com.raven.engine2d.scene.Layer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public abstract class GameObject<GO extends GameObject, P extends Parentable<? extends GameObject>, C extends GameObject>
        implements Childable<P>, Parentable<C> {

    private static int last_id = 0;
    private static HashMap<Integer, GameObject> gameObjectIDMap = new HashMap<>();

    public static void resetObjectIDs() {
        gameObjectIDMap.clear();
        last_id = 0;
    }

    public static GameObject getGameObjectFromID(int id) {
        return gameObjectIDMap.get(id);
    }

    private boolean visibility = true;
    private int id;
    private boolean mouseHovering = false;
    private List<MouseHandler> clickHandlers = new ArrayList<MouseHandler>();

    public GameObject() {
        // click id
        newID();
    }

    public final int getID() {
        return id;
    }

    public final void clearID() {
        gameObjectIDMap.remove(this.id);
        this.id = 0;
    }

    public final void newID() {
        id = last_id += 20;
        gameObjectIDMap.put(id, this);
    }

    public abstract Layer.Destination getDestination();

    public abstract float getZ();

    public void addMouseHandler(MouseHandler c) {
        this.clickHandlers.add(c);
    }

    public void removeMouseHandler(MouseHandler c) {
        this.clickHandlers.remove(c);
    }

    public boolean isMouseHovering() {
        return mouseHovering;
    }

    final public void checkMouseMovement(boolean hovering, float delta) {
        if (!isMouseHovering() && hovering) {
            mouseHovering = hovering;
            onMouseEnter();
        } else if (isMouseHovering() && !hovering) {
            mouseHovering = hovering;
            onMouseLeave();
        } else if (hovering) {
            onMouseHover(delta);
        }

        mouseHovering = hovering;
    }

    final public void onMouseEnter() {
        for (MouseHandler c : clickHandlers) c.handleMouseEnter();
    }

    final public void onMouseHover(float delta) {
        for (MouseHandler c : clickHandlers) c.handleMouseHover(delta);
    }

    final public void onMouseLeave() {
        for (MouseHandler c : clickHandlers) c.handleMouseLeave();
    }

    final public void onMouseClick() {
        for (MouseHandler c : clickHandlers) c.handleMouseClick();
    }

    public abstract void needsRedraw();

    public abstract void draw(LayerShader shader, RenderTarget target);

    public boolean isVisible() {
        if (getParent() instanceof GameObject) {
            return visibility & ((GameObject) getParent()).isVisible();
        }
        return visibility;
    }

    public void setVisibility(boolean visibility) {
        needsRedraw();
        this.visibility = visibility;
    }

    public void release() {
        for (C child : getChildren()) {
            child.release();
        }
    }

    public abstract List<? extends GO> getParentGameObjectList();
}
