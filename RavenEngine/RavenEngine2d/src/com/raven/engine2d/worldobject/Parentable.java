package com.raven.engine2d.worldobject;

import java.util.List;

public interface Parentable<C extends Childable> {
	void addChild(C obj);
	List<C> getChildren();
}
