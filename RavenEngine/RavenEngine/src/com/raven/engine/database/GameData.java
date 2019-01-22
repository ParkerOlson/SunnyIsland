package com.raven.engine.database;

import java.util.List;
import java.util.Map;

public class GameData {
	private Map<String, GameData> vals;
	private GameDataList list;
	private Integer integer;
	private boolean bool;
	private boolean isBool;
	private String str;

	// Constructors
	public GameData(Map<String, GameData> vals) {
		this.vals = vals;
	}

	public GameData(GameDataList list) {
		this.list = list;
	}

	public GameData(boolean bool) {
		this.isBool = true;
		this.bool = bool;
	}

	public GameData(String str) {
		this.str = str;
	}

	public GameData(int integer) {
		this.integer = new Integer(integer);
	}

	// Get
	public Map<String, GameData> getValues() {
		return vals;
	}

	public boolean isData() {
		return vals != null;
	}

	public Map<String, GameData> getData() {
		return vals;
	}
	
	public GameData getData(String prop) {
		return vals.get(prop.toLowerCase());
	}

	public boolean isList() {
		return list != null;
	}

	public GameDataList asList() {
		return list;
	}

	public GameDataList getList(String prop) {
        return getData(prop).asList();
    }

	public boolean isString() {
		return str != null;
	}
	
	public String asString() {
		return str;
	}

	public String getString(String prop) {
	    return getData(prop).asString();
    }
	
	public boolean isBoolean() {
		return isBool;
	}

	public boolean asBoolean() {
		return bool;
	}

	public boolean getBoolean(String prop) {
	    return getData(prop).asBoolean();
    }

	public boolean isInteger() {
		return integer != null;
	}
	
	public int asInteger() {
		return integer;
	}

	public int getInteger(String prop) {
	    return getData(prop).asInteger();
    }

	@Override
	public String toString() {
		if (isBoolean()) {
			return String.valueOf(this.asBoolean());
		} else if (isInteger()) {
			return String.valueOf(asInteger());
		} else if (isString()) {
			return String.format("\"%1$s\"", asString());
		} else if (this.isData()) {
			return String.valueOf(vals);
		} else if (this.isList()) {
			return String.valueOf(list);
		}
				
		return super.toString();
	}
}
