package engine.util;

public class Quaternion {
	public double w;
	public Vector3d v;

	public Quaternion() {
		Quaternion q = Quaternion.rotation(0, new Vector3d(1, 0, 0));
		w = q.w;
		v = q.v;
	}

	public Quaternion(double w, Vector3d v) {
		this.w = w;
		this.v = v;
	}

	public Quaternion(Quaternion q) {
		w = q.w;
		v = new Vector3d(q.v);
	}

	public static Quaternion rotation(double a, Vector3d r) {
		return new Quaternion(Math.cos(a / 2), r.multiply(Math.sin(a / 2))).normalize();
	}

	public Quaternion add(Quaternion q) {
		return new Quaternion(w + q.w, v.add(q.v));
	}

	public Quaternion multiply(Quaternion q) {
		return new Quaternion(w * q.w - v.dotProduct(q.v),
							q.v.multiply(w).add(v.multiply(q.w)).add(v.crossProduct(q.v)));
	}

	public Quaternion multiply(double a) {
		return new Quaternion(w * a, v.multiply(a));
	}

	public Quaternion normalize() {
		double length = length();
		if(length == 0)
			return this;
		return new Quaternion(w / length, v.divide(length));
	}

	//inverse and reverse might be functionally equivalent
	//inverse is used for rotating vectors while reverse
	//returns the opposite rotation
	public Quaternion inverse() {
		return new Quaternion(w, v.multiply(-1));
	}

	public Quaternion reverse() {
		return rotation(-getRotationAngle(), getRotationVector());
	}

	public double length() {
		return Math.sqrt(w * w + v.length() * v.length());
	}

	public double getRotationAngle() {
		return 2 * Math.acos(w);
	}

	public Vector3d getRotationVector() {
		if (getRotationAngle() == 0)
			return v;
		else
			return v.divide(Math.sin(getRotationAngle() / 2));
	}

	public String toString() {
		return getRotationAngle() + ", " + getRotationVector();
	}
}