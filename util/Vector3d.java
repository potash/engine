package engine.util;
import java.util.Vector;

public class Vector3d {
	public double x, y, z;

	public Vector3d() {
		x = y = z = 0;
	}

	public Vector3d(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public Vector3d(Vector3d v) {
		x = v.x;
		y = v.y;
		z = v.z;
	}

	public Vector3d(Vector v) {
		Vector3d temp = new Vector3d();
		for(int i = 0; i < v.size(); i++)
			temp = temp.add((Vector3d)v.get(i));
		x = temp.x;
		y = temp.y;
		z = temp.z;
	}

	public boolean equals(Vector3d v) {
		return (x == v.x && y == v.y && z == v.z);
	}

	public Vector3d add(Vector3d v) {
		return new Vector3d(x + v.x, y + v.y, z + v.z);
	}

	public Vector3d subtract(Vector3d v) {
		return new Vector3d(x - v.x, y - v.y, z - v.z);
	}

	public Vector3d multiply(double a) {
		return new Vector3d(x * a, y * a, z * a);
	}

	public Vector3d divide(double a) {
		return new Vector3d(x / a, y / a, z / a);
	}

	public double dotProduct(Vector3d v) {
		return x * v.x + y * v.y + z * v.z;
	}

	public Vector3d crossProduct(Vector3d v) {
		return new Vector3d(y * v.z - z * v.y,
							z * v.x - x * v.z,
							x * v.y - y * v.x);
	}

	public Matrix3d directProduct(Vector3d v) {
		return new Matrix3d(new Vector3d(x * v.x, x * v.y, x * v.z),
							new Vector3d(y * v.x, y * v.y, y * v.z), 
							new Vector3d(z * v.x, z * v.y, z * v.z));
	}

	public Vector3d normalize() {
		double l = length();
		if(l == 0)
			return new Vector3d(this);
		return new Vector3d(x / l, y / l, z / l);
	}

	public double length() {
		return Math.sqrt(x * x + y * y + z * z);
	}

	public Vector3d rotate(Quaternion q) {
		Quaternion q2 = new Quaternion(0, this).normalize();
		q = q.normalize();

		return q.multiply(q2).multiply(q.inverse()).v.multiply(length());
	}

	public String toString() {
		return "(" + x + ", " + y + ", " + z + ")";
	}
}