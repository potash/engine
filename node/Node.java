package engine.node;

import engine.util.Vector3d;
import engine.util.Quaternion;

public class Node {
	protected Vector3d position;
	protected Quaternion angPosition;

	public Node() {
		position = new Vector3d();
		angPosition = new Quaternion();
	}

	public Node(Vector3d position, Quaternion angPosition) {
		this.position = new Vector3d(position);
		this.angPosition = new Quaternion(angPosition);
	}

	public Vector3d toLocalSpace(Vector3d v) {
		return v.subtract(position).rotate(angPosition.inverse());
	}

	public Vector3d toWorldSpace(Vector3d v) {
		return v.rotate(angPosition).add(position);
	}

	public void update(double dtime) {}

	public Vector3d getPosition() {
		return position;
	}

	public Quaternion getAngPosition() {
		return angPosition;
	}
}
