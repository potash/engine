package engine.node;

import engine.util.Vector3d;
import engine.util.Quaternion;

public class Object3D extends Node {
	protected Geometry geometry;

	public Object3D(Geometry geometry) {
		this.geometry = geometry;
	}

	public Object3D(Geometry geometry, Vector3d position, Quaternion angPosition) {
		super(position, angPosition);
		this.geometry = geometry;
	}

	public Geometry getGeometry() {
		return geometry;
	}

	public boolean isTextureEnabled() {
		return geometry.getTexture() != null;
	}

	public void render(net.java.games.jogl.GL gl) {
		Vector3d rotation = angPosition.getRotationVector();

		gl.glTranslated(position.x, position.y, position.z);
		gl.glRotated(Math.toDegrees(angPosition.getRotationAngle()), rotation.x, rotation.y, rotation.z);
		geometry.render(gl);
	}	
}
