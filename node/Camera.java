package engine.node;

import engine.util.Vector3d;
import engine.util.Quaternion;

public class Camera extends Node {
	public Camera() {
	}

	public Camera(Vector3d position, Quaternion angPosition) {
		super(position, angPosition);
	}

	public void translate(Vector3d v) {
		position = position.add(v);
	}

	public void rotate(double a, Vector3d r) {
		angPosition = Quaternion.rotation(a, r).multiply(angPosition);
	}
}
