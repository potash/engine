package engine.util;

public class Vector2d {
	public double x, y;

	public Vector2d() {
		x = y = 0;
	}

	public Vector2d(double x, double y) {
		this.x = x;
		this.y = y;
	}

	public Vector2d(Vector2d v) {
		x = v.x;
		y = v.y;
	}

	public boolean equals(Vector3d v) {
		return x == v.x && y == v.y;
	}

	public String toString() {
		return "(" + x + ", " + y + ")";
	}
}