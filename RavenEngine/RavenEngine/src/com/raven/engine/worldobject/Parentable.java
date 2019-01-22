package com.raven.engine.worldobject;

public interface Parentable {
	float getGlobalX();
	float getGlobalY();
	float getGlobalZ();
	void addChild(WorldObject obj);
}
