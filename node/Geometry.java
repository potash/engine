package engine.node;

import net.java.games.jogl.GL;
import java.util.Vector;
import engine.util.Vector3d;
import engine.util.Quaternion;

public abstract class Geometry {
    protected Vector3d[] vertices;
    protected Texture texture;
    protected int list = -1;
    protected Geometry bounds;

    public void setTexture(Texture texture) {
        this.texture = texture;
    }

    public Texture getTexture() {
        return texture;
    }

    public void disableTexture() {
        texture = null;
    }

 //   abstract boolean   isIntersecting(Geometry geometry, Vector3d position, Vector3d velocity, Quaternion rot1, Quaternion rot2);
    public abstract Collision getCollision(Geometry geometry, Vector3d position, Vector3d velocity, Quaternion rot1, Quaternion rot2); 

    abstract void compile(GL gl);

    public void render(GL gl) {
        if(texture != null)
            texture.bind(gl);
        if(list == -1)
            compile(gl);
        else
            gl.glCallList(list);
    }
}
