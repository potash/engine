package engine;

import net.java.games.jogl.GL;

import engine.node.Node;
import engine.node.Camera;
import engine.node.Body;

public interface SceneGraph {
	public void renderScene(Camera c, GL gl);
	public void addNode(Node n);
	public boolean removeNode(Node n);
	public double checkCollision(Body b, double dtime);
}
