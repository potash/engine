package engine.node;

import net.java.games.jogl.GL;

import engine.util.Vector2d;
import engine.util.Vector3d;
import engine.util.Quaternion;
import engine.util.Color3d;

public class Polygon {
	protected Vector3d[] vertex;
	protected Color3d[] color;
	protected Vector2d[] texCoord;

	public Polygon() {
		vertex = new Vector3d[] {new Vector3d()};
		color = new Color3d[] {new Color3d()};
		texCoord = new Vector2d[] {new Vector2d()};
	}

	public Polygon(Vector3d[] vertex) {
		this.vertex = new Vector3d[vertex.length];
		color = new Color3d[vertex.length];
		texCoord = new Vector2d[vertex.length];

		for(int i = 0; i < vertex.length; i++) {
			this.vertex[i] = new Vector3d(vertex[i]);
			color[i] = new Color3d();
			texCoord[i] = new Vector2d();
		}
	}

	public Polygon(Vector3d[] vertex, Color3d[] color) {
		this.vertex = new Vector3d[vertex.length];
		this.color = new Color3d[vertex.length];
		texCoord = new Vector2d[vertex.length];

		for(int i = 0; i < vertex.length; i++) {
			this.vertex[i] = new Vector3d(vertex[i]);
			this.color[i] = new Color3d(color[i]);
			texCoord[i] = new Vector2d();
		}
	}

	public Polygon(Vector3d[] vertex, Vector2d[] texCoord) {
		this.vertex = new Vector3d[vertex.length];
		color = new Color3d[vertex.length];
		this.texCoord = new Vector2d[vertex.length];

		for(int i = 0; i < vertex.length; i++) {
			this.vertex[i] = new Vector3d(vertex[i]);
			color[i] = new Color3d();
			this.texCoord[i] = new Vector2d(texCoord[i]);
		}
	}

	public Polygon(Vector3d[] vertex, Color3d[] color, Vector2d[] texCoord) {
		this.vertex = new Vector3d[vertex.length];
		this.color = new Color3d[vertex.length];
		this.texCoord = new Vector2d[vertex.length];

		for(int i = 0; i < vertex.length; i++) {
			this.vertex[i] = new Vector3d(vertex[i]);
			this.color[i] = new Color3d(color[i]);
			this.texCoord[i] = new Vector2d(texCoord[i]);
		}
	}

	public Polygon(Polygon p) {
		vertex = new Vector3d[p.vertex.length];
		color = new Color3d[p.vertex.length];
		texCoord = new Vector2d[p.vertex.length];

		for(int i = 0; i < vertex.length; i++) {
			vertex[i] = new Vector3d(p.vertex[i]);
			color[i] = new Color3d(p.color[i]);
			texCoord[i] = new Vector2d(p.texCoord[i]);
		}
	}

	public void setColor(Color3d c) {
		for(int i = 0; i < vertex.length; i++)
			color[i] = new Color3d(c);
	}

	public void setColor(int i, Color3d c) {
		color[i] = new Color3d(c);
	}

	public void setTexCoord(int i, Vector2d texCoord) {
		this.texCoord[i] = new Vector2d(texCoord);
	}

	public String toString() {
		String result = "[";
		for(int i = 0; i < vertex.length - 1; i++)
			result += vertex[i] + ", ";
		return result + vertex[vertex.length - 1] + "]";
	}
}