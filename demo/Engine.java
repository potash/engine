package engine.demo;

import engine.Scene;
import engine.SceneRenderer;
import engine.RenderFrame;

import engine.node.Body;
import engine.node.Box;
import engine.node.Camera;

import engine.util.Vector3d;
import engine.util.Quaternion;

public class Engine {
    Scene scene;
    SceneRenderer renderer;
    RenderFrame frame;
    
    public Engine() throws java.io.IOException {
        scene = new Scene();
        renderer = new SceneRenderer(scene, 
			new Camera(new Vector3d(0, -5, 20), new Quaternion()));
        frame = new RenderFrame(renderer, "Engine", 800, 600, false);
   
	Body a = new Body(new Box(3, 3, 3),
			new Vector3d(), new Vector3d(), 
			Quaternion.rotation(-1, new Vector3d(0, 1, 1)), 
			new Vector3d(), 1);
	
	Body b = new Body(new Box(3, 3, 3), 
			new Vector3d(0, -10, 0), 
			Quaternion.rotation(0, new Vector3d(1, 0, 0)),
			10000000000d);
		
	scene.addNode(a);
	scene.addNode(b);
		
	a.applyConstantForce(new Vector3d(0, -109.8 * a.getMass(), 0));
	a.applyConstantForce(new Vector3d(1, 1, 0), new Vector3d(1, 1, 1));
	frame.start();
    }

    public static void main(String args[]) throws java.io.IOException {
        Engine demo = new Engine();
    }
}
