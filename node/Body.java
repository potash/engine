package engine.node;

import java.util.Vector;

import engine.SceneGraph;
import engine.node.Geometry;
import engine.node.BoundingBox;

import engine.util.Vector3d;
import engine.util.Quaternion;
import engine.util.Matrix3d;

public class Body extends Object3D {
	private Vector3d velocity, force, angVelocity, torque;
	private double mass;
	private Matrix3d inertia;
	private Vector constForce, constTorque;
	private SceneGraph sceneGraph;
	private BoundingBox bounds;

	public Body(Geometry geometry, double mass) {
		super(geometry);
		this.mass = mass;

		velocity    = new Vector3d();
		force       = new Vector3d();
		angVelocity = new Vector3d();
		torque      = new Vector3d();
		constForce  = new Vector();
		constTorque = new Vector();

		calculateInertiaTensor();
	}

	public Body(Geometry geometry, Vector3d position, Quaternion angPosition, double mass) {
		super(geometry, position, angPosition);
		this.mass = mass;

		velocity    = new Vector3d();
		force       = new Vector3d();
		angVelocity = new Vector3d();
		torque      = new Vector3d();
		constForce  = new Vector();
		constTorque = new Vector();

		calculateInertiaTensor();
	}

	public Body(Geometry geometry, Vector3d position, Vector3d velocity, Quaternion angPosition, Vector3d angVelocity, double mass) {
		super(geometry, position, angPosition);
		this.velocity    = new Vector3d(velocity);
		this.angVelocity = new Vector3d(angVelocity);
		this.mass = mass;

		force       = new Vector3d();
		torque      = new Vector3d();
		constForce  = new Vector();
		constTorque = new Vector();

		calculateInertiaTensor();
	}

	private void calculateInertiaTensor() {						//Function is misnamed: constructs boundign box too
		Vector3d min, max;
		Vector3d[] v = new Vector3d[geometry.vertices.length];

		min = new Vector3d(geometry.vertices[0]);
		max = new Vector3d(geometry.vertices[0]);
		for(int i = 0; i < geometry.vertices.length; i++) {
			v[i] = geometry.vertices[i];
			if(v[i].x < min.x)
				min.x = v[i].x;
			if(v[i].x > max.x)
				max.x = v[i].x;
			if(v[i].y < min.y)
				min.y = v[i].y;
			if(v[i].y > max.y)
				max.y = v[i].x;
			if(v[i].z < min.z)
				min.z = v[i].z;
			if(v[i].z > max.z)
				max.z = v[i].z;
		}
		bounds = new BoundingBox(min, max);

		inertia = new Matrix3d();
		int count = 0;
		/*for(double x = min.x; x <= max.x; x += (max.x - min.x) / 100d) {
			for(double y = min.y; y <= max.y; y += (max.y - min.y) / 100d) {
				for(double z = min.z; z <= max.z; z += (max.z - min.z) / 100d) {
					if(true) {															//FIXME
						inertia = inertia.add(new Matrix3d(new Vector3d(y * y + z * z, -x * y, -x * z),
														new Vector3d(-y * x, x * x + z * z, -y * z),
														new Vector3d(-z * x, -z * y, x * x + y * y)));
					count++;
					}
				}
			}
		}*/

		inertia = inertia.multiply(mass).inverse();
	}

	public void update(double dtime) {
		double collisionTime;

		double deltaTime = dtime;
		do {
			collisionTime = sceneGraph.checkCollision(this, deltaTime);
			if(collisionTime != -1)
				deltaTime -= collisionTime;
		} while(collisionTime != -1);

		position = position.add(velocity.multiply(deltaTime));
		velocity = velocity.add(force.multiply(dtime / mass));
		force    = new Vector3d(constForce);

		Quaternion rotate = Quaternion.rotation(angVelocity.length() * dtime, angVelocity.normalize());

		angPosition = rotate.multiply(angPosition);
		angVelocity = angVelocity.add(inertia.multiply(torque).multiply(dtime));
		torque      = new Vector3d(constTorque);
	}

	public void applyForce(Vector3d f) {
		force = force.add(f);
	}

	public void applyConstantForce(Vector3d f) {
		constForce.add(f);
		force = force.add(f);
	}

	public void removeConstantForce(Vector3d f) {
		if(constForce.remove(f))
			force = force.subtract(f);
	}

	public void applyForce(Vector3d f, Vector3d p) {
		force  = force.add(f);
		torque = torque.add(p.crossProduct(f));
	}

	public void applyConstantForce(Vector3d f, Vector3d p) {
		constForce.add(f);
		constTorque.add(p.crossProduct(f));
		force  = force.add(f);
		torque = torque.add(p.crossProduct(f));
	}

	public void removeConstantForce(Vector3d f, Vector3d p) {
		if(constTorque.remove(p.crossProduct(f)) && constForce.remove(f)) {
			force  = force.subtract(f);
			torque = torque.subtract(p.crossProduct(f));
		}
	}

	public void applyCollision(Collision c) {		
		velocity    = velocity.add(c.getNormal().multiply(c.getImpulse() / mass));
                position    = position.add(velocity.multiply(c.getTime()));
		angVelocity = angVelocity.add(inertia.multiply(angVelocity.crossProduct(c.getCollisionPoint())));
	}

	public void setSceneGraph(SceneGraph sceneGraph) {
		this.sceneGraph = sceneGraph;
	}

	public BoundingBox getBounds() {
		return bounds;
	}
	
	public Vector3d getVelocity() {
		return velocity;
	}

	public Vector3d getVelocity(Vector3d p) {
		return velocity.add(angVelocity.crossProduct(p));
	}

	public Vector3d getAngVelocity() {
		return angVelocity;
	}

	public double getMass() {
		return mass;
	}

	public Matrix3d getInertia() {
		return inertia;
	}

	public double getRestitution() {
		return 1;	//Collisions elastic for now, should be in Material
	}
}
