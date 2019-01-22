package com.raven.engine.worldobject;

import java.awt.FontMetrics;
import java.awt.Graphics;

public class TextObject {
	private String text;
	private int x = 0, y = 0;

	public TextObject() {
		this("");
	}
	
	public TextObject(String text) {
		this.text = text;
	}
	
	public void draw(Graphics g, int gx, int gy) {
		FontMetrics m = g.getFontMetrics();

		int h = m.getHeight();
		int w = m.stringWidth(text);

		g.drawString(text, gx + x, gy + y + h / 2);
	}

	public void setText(String text) {
		this.text = text;
	}

	public void setX(int x) {
		this.x = x;
	}
	
	public void setY(int y) {
		this.y = y;
	}
}
