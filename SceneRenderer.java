package engine;

import net.java.games.jogl.GL;
import net.java.games.jogl.GLDrawable;

import engine.Renderer;
import engine.Scene;
import engine.node.Camera;
import engine.node.Object3D;
import engine.util.Vector3d;
import engine.util.Quaternion;

public class SceneRenderer extends Renderer {
	private Scene scene;
	private Camera camera;

	public SceneRenderer(Scene scene, Camera camera) {
	    this.scene = scene;
	    this.camera = camera;
	}

	public void init(GLDrawable drawable) {
	    super.init(drawable);
	    try {
		((Object3D)scene.getNode(0)).getGeometry().setTexture(getTexture("crate", "crate.png"));
                ((Object3D)scene.getNode(1)).getGeometry().setTexture(getTexture("crate", "crate.png"));
	    } catch(java.io.IOException e) {
		e.printStackTrace();
	    }
	}

	public void display(GL gl) {
	    scene.renderScene(camera, gl);
	}
}
