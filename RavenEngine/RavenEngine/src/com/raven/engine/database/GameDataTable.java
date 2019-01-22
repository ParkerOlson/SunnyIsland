package com.raven.engine.database;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameDataTable extends GameDataList {
	private String name;

	public GameDataTable(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}
