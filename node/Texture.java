package engine.node;

import net.java.games.jogl.GL;
import java.awt.image.BufferedImage;

public class Texture {
	private String resourceName; 
	private int target; 
	private int textureID;
	private int height;
	private int width;
	private int texWidth;
	private int texHeight;
	private float widthRatio;
	private float heightRatio;

	private BufferedImage buffer;

	public Texture(String resourceName,int target,int textureID) {
		this.resourceName = resourceName;
		this.target = target;
		this.textureID = textureID;
	}

	public void setBufferedImage(BufferedImage buffer) {
		this.buffer = buffer;
	}

	public void bind(GL gl) {
		gl.glBindTexture(target, textureID); 
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getImageHeight() {
		return height;
	}

	public int getImageWidth() {
		return width;
	}

	public float getHeight() {
		return heightRatio;
	}

	public float getWidth() {
		return widthRatio;
	}
}