package engine;

import net.java.games.jogl.GL;
import java.util.Vector;

import engine.node.Node;
import engine.node.Camera;
import engine.node.Body;
import engine.node.Object3D;
import engine.node.BoundingBox;
import engine.node.Triangle;
import engine.node.Collision;

import engine.util.Vector3d;
import engine.util.Timer;

public class Scene implements SceneGraph {
	private Vector nodes;
	private Timer t;
	private double ltime = -1;
	private boolean textureEnabled = false;

	public Scene() {
		nodes = new Vector();
		t = new Timer();
	}

	public void addNode(Node n) {
		if(n instanceof Body)
			((Body)n).setSceneGraph(this);
		nodes.add(n);
	}

	public Node getNode(int i) {
		return (Node)nodes.get(i);
	}

	public boolean removeNode(Node n) {
		return nodes.remove(n);
	}

    public double checkCollision(Body b, double dtime) {
        for(int i = nodes.indexOf(b) + 1; i < nodes.size(); i++) {
            Node n = (Node)nodes.get(i);
            if(n instanceof Body) {
                Body p = (Body)n;
                Vector3d relativePosition = p.getPosition().subtract(b.getPosition()),
                         relativeVelocity = p.getVelocity().subtract(b.getVelocity());

                Collision collision = b.getBounds().getCollision(p.getBounds(),    //b.rotate()?
                    relativePosition,
                    relativeVelocity,
                    dtime, .000000001);
                if(collision != null) {
                    Collision c = new Collision(relativeVelocity.y * 2, new Vector3d(0, 1, 0), new Vector3d(), collision.getTime() * dtime);
                    b.applyCollision(c);
                    p.applyCollision(c.reverseImpulse());
                    return c.getTime();
                }
            }
        }
        return -1;
    }

	public void renderScene(Camera c, GL gl) {
		gl.glLoadIdentity();

		if(ltime == -1)
			ltime = t.getTime();

		Vector3d position = c.getPosition().multiply(-1),
			 rotation = c.getAngPosition().getRotationVector().multiply(-1);

		gl.glTranslated(position.x, position.y, position.z);
		gl.glRotated(Math.toDegrees(c.getAngPosition().getRotationAngle()), rotation.x, rotation.y, rotation.z);
		gl.glPushMatrix();

		byte[] te = new byte[1];
		gl.glGetBooleanv(GL.GL_TEXTURE_2D, te);
		textureEnabled = te[0] != 0;

		double dtime = t.getTime() - ltime;
		ltime = t.getTime();
		for(int i = 0; i < nodes.size(); i++) {
			Node n = (Node)nodes.get(i);
			n.update(dtime);

			if((n instanceof Object3D)) {
				gl.glPopMatrix();
				gl.glPushMatrix();

				Object3D o = (Object3D)n;
				if(o.isTextureEnabled() && !textureEnabled) {
						gl.glEnable(GL.GL_TEXTURE_2D);
						textureEnabled = true;
				} else if(!o.isTextureEnabled() && textureEnabled) {
						gl.glDisable(GL.GL_TEXTURE_2D);
						textureEnabled = false;
				}

				o.render(gl);
			}
		}
	}
}
