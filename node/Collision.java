package engine.node;

import engine.node.Body;
import engine.util.Vector3d;

public class Collision {
	private double impulse, time;
	private Vector3d normal, collisionPoint;

	public Collision(double impulse, Vector3d normal, Vector3d collisionPoint, double time) {
		this.impulse = impulse;
		this.normal = normal;
		this.collisionPoint = collisionPoint;
		this.time = time;
	}

	public static double calculateImpulse(Body a, Body b, Vector3d n, Vector3d p) {
		Vector3d ap = a.toLocalSpace(p);
		Vector3d bp = b.toLocalSpace(p);
		return a.getVelocity(ap).subtract(b.getVelocity(bp)).multiply(-(1 + a.getRestitution() * b.getRestitution())).dotProduct(n) / (n.dotProduct(n) * (1 / a.getMass() + 1 / b.getMass()) + (a.getInertia().multiply(ap.crossProduct(n)).crossProduct(ap).add(b.getInertia().multiply(bp.crossProduct(n)).crossProduct(bp))).dotProduct(n));	//pretty ain't it
	}

        public Collision reverseImpulse() {
             return new Collision(-impulse, normal, collisionPoint, time);
        }

	public double getImpulse() {
		return impulse;
	}

	public Vector3d getNormal() {
		return normal;
	}

	public Vector3d getCollisionPoint() {
		return collisionPoint;
	}

	public double getTime() {
		return time;
	}
}