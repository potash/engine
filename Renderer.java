package engine;

import engine.node.Texture;

import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.KeyListener;
import java.io.IOException;

import net.java.games.jogl.GLEventListener;
import net.java.games.jogl.GLDrawable;
import net.java.games.jogl.GL;

public abstract class Renderer implements GLEventListener {
	private GLDrawable drawable;
	private GL gl;
	private TextureLoader textureLoader;

	public void init(GLDrawable drawable) {
		this.drawable = drawable;
		gl = drawable.getGL();
		gl.glEnable(GL.GL_DEPTH_TEST);
		textureLoader = new TextureLoader(drawable);
	}

	public void reshape(GLDrawable drawable, int x, int y, int width, int height) {
		gl.glViewport(0, 0, width, height);
		gl.glMatrixMode(gl.GL_PROJECTION);
		gl.glLoadIdentity();
		drawable.getGLU().gluPerspective(45, (float)width / height, .1f, 100);
		gl.glMatrixMode(gl.GL_MODELVIEW);
	}

	public void displayChanged(GLDrawable drawable, boolean modeChanged, boolean deviceChanged) {}

	public void display(GLDrawable drawable) {
		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
		display(gl);
	}

	public abstract void display(GL gl);

	public Texture getTexture(String name, String resourceName) throws IOException {
		return textureLoader.getTexture(name, resourceName);
	}

	public void addMouseListener(MouseListener listener) {
		drawable.addMouseListener(listener);
	}

	public void addMouseMotionListener(MouseMotionListener listener) {
		drawable.addMouseMotionListener(listener);
	}

	public void addKeyListener(KeyListener listener) {
		drawable.addKeyListener(listener);
	}
}