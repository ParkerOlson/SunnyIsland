package com.raven.engine.worldobject;

public interface MouseHandler {
	void onMouseClick();
	void onMouseEnter();
	void onMouseLeave();
	void onMouseHover(float delta);
}
