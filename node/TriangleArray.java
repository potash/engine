package engine.node;

import net.java.games.jogl.GL;
import java.util.Vector;
import engine.util.Vector3d;
import engine.util.Quaternion;
import engine.util.Vector2d;
import engine.util.Color3d;

public class TriangleArray extends Geometry {
    protected Triangle[] triangles;

    public TriangleArray(Triangle[] triangles) {
        this.triangles = new Triangle[triangles.length];
        for(int i = 0; i < triangles.length; i++) {
            this.triangles[i] = new Triangle(triangles[i]);
        }
    }

    public TriangleArray(Polygon[] polygon) {
        int tris = 0;
        Vector vertex = new Vector();
        for(int i = 0; i < polygon.length; i++) {
            tris += polygon[i].vertex.length - 2;
            for(int j = 0; j < polygon[i].vertex.length; j++) {
                 Vector3d v = polygon[i].vertex[j];
                 if(vertex.indexOf(v) == -1)
                     vertex.add(v);
            }
        }
 
        vertices = new Vector3d[vertex.size()];
        for(int i = 0; i < vertices.length; i++)
            vertices[i] = (Vector3d)vertex.get(i);
         
        triangles = new Triangle[tris];
        for(int i = 0, n = 0; i < polygon.length; i++) {
            for(int j = 2; j < polygon[i].vertex.length; j++, n++) {
                triangles[n] = new Triangle(
                    new Vector3d(polygon[i].vertex[0]),
                    new Vector3d(polygon[i].vertex[j - 1]),
                    new Vector3d(polygon[i].vertex[j]));
                
                triangles[n].color = new Color3d[] {
                     new Color3d(polygon[i].color[0]), 
                     new Color3d(polygon[i].color[j - 1]),
                     new Color3d(polygon[i].color[j])
                };

                triangles[n].texCoord = new Vector2d[] {
                    new Vector2d(polygon[i].texCoord[0]),
                    new Vector2d(polygon[i].texCoord[j - 1]),
                    new Vector2d(polygon[i].texCoord[j])};
            }
        }
    }

    public Collision getCollision(Geometry geometry, Vector3d position, Vector3d velocity, Quaternion rot1, Quaternion rot2) {
        if(geometry instanceof TriangleArray) {
            Triangle[] triangles = ((TriangleArray)geometry).triangles;
            for(int i = 0; i < this.triangles.length; i++) {
                for(int j = 0; j < triangles.length; j++) {
                    Collision collision = this.triangles[i].rotate(rot1).getCollision(triangles[j], position, velocity, rot1.reverse(), rot2.reverse());
                    if(collision != null)
                        return collision;
                }
            }
            return null;
        }
        return null;
    }

    void compile(GL gl) {
        list = gl.glGenLists(1);
        gl.glNewList(list, GL.GL_COMPILE_AND_EXECUTE);
        gl.glBegin(GL.GL_TRIANGLES);
            for(int i = 0; i < triangles.length; i++) {
                for(int j = 0; j < 3; j++) {
                    gl.glTexCoord2d(triangles[i].texCoord[j].x,
                                    triangles[i].texCoord[j].y);
                    gl.glColor3d(triangles[i].color[j].red,
                                 triangles[i].color[j].green,
                                 triangles[i].color[j].blue);
                    gl.glVertex3d(triangles[i].vertex[j].x,
                                  triangles[i].vertex[j].y,
                                  triangles[i].vertex[j].z);
                }
            }
        gl.glEnd();
        gl.glEndList();
    }
}