package engine.node;

import net.java.games.jogl.GL;
import engine.util.Vector3d;
import engine.util.Quaternion;
import engine.util.Matrix3d;
import engine.util.Color3d;

public class Box extends Geometry{
    private double x, y, z;

    public Box(double x, double y, double z) {
        this.x = x / 2;
        this.y = y / 2;
        this.z = z / 2;
        
        init();
    }
    
    private void init() {
        vertices = new Vector3d[] {
            new Vector3d(x, y, z),
            new Vector3d(x, -y, z),
	    new Vector3d(-x, -y, z),
            new Vector3d(-x, y, z),
	    new Vector3d(x, y, -z),
            new Vector3d(x, -y, -z),
	    new Vector3d(-x, -y, -z),
            new Vector3d(-x, y, -z)
        };
    }

    public Collision getCollision(Geometry geometry, Vector3d relativePosition, Vector3d relativeVelocity, Quaternion rot1, Quaternion rot2) {
        if(geometry instanceof Box) {
            return null;
        }
        
        return null;
    }

    void compile(GL gl) {
        list = gl.glGenLists(1);
        gl.glNewList(list, GL.GL_COMPILE_AND_EXECUTE);
        //gl.glColor3d(color.red, color.green, color.blue);
        gl.glBegin(GL.GL_QUADS);
            drawFace(gl, 0, 1, 2, 3);
            drawFace(gl, 1, 5, 6, 2);
            drawFace(gl, 5, 4, 7, 6);
            drawFace(gl, 4, 0, 3, 7);
            drawFace(gl, 5, 4, 0, 1);
            drawFace(gl, 6, 7, 3, 2);
        gl.glEnd();
        gl.glEndList();
    }

    private void drawFace(GL gl, int v0, int v1, int v2, int v3) {
        gl.glTexCoord2d(0, 0);
        gl.glVertex3d(vertices[v0].x, vertices[v0].y, vertices[v0].z);
        gl.glTexCoord2d(0, 1);
        gl.glVertex3d(vertices[v1].x, vertices[v1].y, vertices[v1].z);
        gl.glTexCoord2d(1, 1);
        gl.glVertex3d(vertices[v2].x, vertices[v2].y, vertices[v2].z);
        gl.glTexCoord2d(1, 0);
        gl.glVertex3d(vertices[v3].x, vertices[v3].y, vertices[v3].z);
    }
}
