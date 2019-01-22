package com.raven.engine2d.worldobject;

public interface MouseHandler {
	void handleMouseClick();
	void handleMouseEnter();
	void handleMouseLeave();
	void handleMouseHover(float delta);
}
