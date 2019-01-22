package com.raven.engine.util;

public class Pair {
	private int x, y;

	public Pair(int a, int b) {
		this.x = a;
		this.y = b;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	@Override
	public boolean equals(Object o) {
		try {
			if (this.getClass() == o.getClass()) {
				Pair p = (Pair) o;
				
				return x == p.x && y == p.y; // || a == p.b && b == p.a;
			}
			return super.equals(o);
		} catch (Exception e) {
			return false;
		}
	}

	@Override
	public int hashCode() {
		// return x.hashCode() + y.hashCode();
		return 0;
	}
	
	@Override
	public String toString() {
		return "(" + x + ", " + y + ")";
	}
}
