package engine.node;

import net.java.games.jogl.GL;

import engine.util.Vector3d;
import engine.util.Vector2d;
import engine.util.Quaternion;
import engine.util.Color3d;

public class Triangle extends Polygon {
	private Vector3d[] edge;
	private Vector3d normal;
	private double mx, my;		//Plane defined by triangle: z = mx(x - x_1) + my(y - y_1) + z_1

	public Triangle(Vector3d v1, Vector3d v2, Vector3d v3) {
		vertex = new Vector3d[3];
		color = new Color3d[3];
		texCoord = new Vector2d[3];

		vertex[0] = new Vector3d(v1);
		vertex[1] = new Vector3d(v2);
		vertex[2] = new Vector3d(v3);

		init();

		color[0] = new Color3d();
		color[1] = new Color3d();
		color[2] = new Color3d();

		texCoord[0] = new Vector2d();
		texCoord[1] = new Vector2d();
		texCoord[2] = new Vector2d();
	}

	public Triangle(Vector3d[] vertex) {
		super(vertex);
		init();
	}

	public Triangle(Vector3d[] vertex, Color3d[] color) {
		super(vertex, color);
		init();
	}

	public Triangle(Vector3d[] vertex, Vector2d[] texCoord) {
		super(vertex, texCoord);
		init();
	}

	public Triangle(Vector3d[] vertex, Color3d[] color, Vector2d[] texCoord) {
		super(vertex, color, texCoord);
		init();
	}

	public Triangle(Triangle t) {
		super(t);
		init();
	}

	private void init() {
		edge = new Vector3d[3];
		edge[0] = vertex[1].subtract(vertex[0]);
		edge[1] = vertex[2].subtract(vertex[0]);
		edge[2] = edge[1].subtract(edge[0]);
		normal = edge[0].crossProduct(edge[1]);

		mx = -normal.z / normal.y;
		my = -normal.x / normal.z;
	}

	public Triangle rotate(Quaternion q) {
		Vector3d[] v = new Vector3d[vertex.length];
		for(int i = 0; i < vertex.length; i++)
			v[i] = vertex[i].rotate(q);
		return new Triangle(v, color, texCoord);
	}

	public Vector3d[] getEdge() {
		return edge;
	}

	public Vector3d getNormal() {
		return normal;
	}

	public double getMx() {
		return mx;
	}

	public double getMy() {
		return my;
	}

	public boolean checkCollision(BoundingBox b, Vector3d relativePosition, Vector3d relativeVelocity, double dtime) {
		return b.checkCollision(this, relativePosition.multiply(-1), relativeVelocity.multiply(-1), dtime);
	}

	//Should be in Collision(?)
	public Collision getCollision(Triangle tri, Vector3d relPos, Vector3d dX, Quaternion rot1, Quaternion rot2) {
		Vector3d collisionPoint;

		//Put in rest frame of this
		Vector3d deltaX = dX.rotate(rot1);
		Vector3d[] v = new Vector3d[]
                    { tri.vertex[0].add(relPos).rotate(rot1),
                      tri.vertex[1].add(relPos).rotate(rot1),
                      tri.vertex[2].add(relPos).rotate(rot1) };

		//vertex-face collisions
		collisionPoint = lineTriangle(this, v[0], v[0].add(deltaX));
		if(collisionPoint != null)
			return new Collision(0, this.normal, collisionPoint, collisionPoint.subtract(v[0]).length() / deltaX.length());
		collisionPoint = lineTriangle(this, v[1], v[1].add(deltaX));
		if(collisionPoint != null)
			return new Collision(0, this.normal, collisionPoint, collisionPoint.subtract(v[1]).length() / deltaX.length());
		collisionPoint = lineTriangle(this, v[2], v[2].add(deltaX));
		if(collisionPoint != null)
			return new Collision(0, this.normal, collisionPoint, collisionPoint.subtract(v[2]).length() / deltaX.length());

                
		//edge-edge collisions (parallel coplanar)
		//(v0->(v0+deltaX))->(v1->(v1+deltaX)) intersect this.vertex[0]->this.vertex[1]?
		//''v1''v2''
		//''v2''v0''

		//////''vertex[1]''vertex[2]''

		//////''vertex[2]''vertex[0]''

		//edge-face collisions
		//(v0->(v0+deltaX))->(v1->(v1+deltaX)) intersect this?
		//''v1''v2''
		//''v2''v0''

		//Put in rest frame of tri
		deltaX = dX.rotate(rot2).multiply(-1);
		v[0] = this.vertex[0].subtract(relPos).rotate(rot2);
		v[1] = this.vertex[1].subtract(relPos).rotate(rot2);
		v[2] = this.vertex[2].subtract(relPos).rotate(rot2);

		//vertex-face collisions
		collisionPoint = lineTriangle(tri, v[0], v[0].add(deltaX));
		if(collisionPoint != null)
			return new Collision(0, tri.normal, collisionPoint, collisionPoint.subtract(v[0]).length() / deltaX.length());
		collisionPoint = lineTriangle(tri, v[1], v[1].add(deltaX));
		if(collisionPoint != null)
			return new Collision(0, tri.normal, collisionPoint, collisionPoint.subtract(v[1]).length() / deltaX.length());
		collisionPoint = lineTriangle(tri, v[2], v[2].add(deltaX));
		if(collisionPoint != null)
			return new Collision(0, tri.normal, collisionPoint, collisionPoint.subtract(v[2]).length() / deltaX.length());

		//edge-face collisions
		//(v0->(v0+deltaX))->(v1->(v1+deltaX)) intersect tri?
		//''v1''v2''
		//''v2''v0''

		//face-face collisions (parallel, non-coplanar)
		//if(Math.abs(tri.normal.crossProduct(this.normal).length()) < Double.MIN_VALUE *) {
                    
                    //find when tris are equal level
                    //add edge intersections to list
                    //add vertices contained in other triangle to list
                    //if list empty, none
                    //else average points in list -> collision point
		//}

		return null;
	}

	private static Vector3d lineTriangle(Triangle tri, Vector3d v0, Vector3d v1) {
		//find point where line intersects plane
		Vector3d point;
		double mx = tri.getMx();
		double my = tri.getMy();

		if(Double.isNaN(mx) || Double.isInfinite(mx)) {
			if(v0.x == v1.x)
				return null;
			double m0 = (v1.y - v0.y) / (v1.x - v0.x);
			double m1 = (v1.z - v0.z) / (v1.x - v0.x);

			point = new Vector3d(tri.vertex[0].x, m0 * (tri.vertex[0].x - v0.x) + v0.y, m1 * (tri.vertex[0].x - v0.x) + v0.z);
		} else if(Double.isNaN(my) || Double.isInfinite(my)) {
			if(v0.y == v1.y)
				return null;
			double m0 = (v1.x - v0.x) / (v1.y - v0.y);
			double m1 = (v1.z - v0.z) / (v1.y - v0.y);

			point = new Vector3d(m0 * (tri.vertex[0].y - v0.y) + v0.x, tri.vertex[0].y, m1 * (tri.vertex[0].y - v0.y) + v0.z);
		} else {
			if(v0.x == v1.x) {
				if(v0.y == v1.y) {
					point = new Vector3d(v0.x, v0.y, mx * (v0.x - tri.vertex[0].x) + my * (v0.y - tri.vertex[0].y) + tri.vertex[0].z);
				} else {
					double m = (v1.z - v0.z) / (v1.y - v0.y);
					if(Math.abs(m - my) < .00001)
						return null;
					double y = (m * v0.y - v0.z + mx * (v0.x - tri.vertex[0].x) - my * tri.vertex[0].y + tri.vertex[0].z) / (m - my);

					point = new Vector3d(v0.x, y, m * (y - v0.y) + v0.z);
				}
			} else {
				double m0 = (v1.y - v0.y) / (v1.x - v0.x);	//line defined by z = m1(x - v0.x) + v0.z and y = m0(x - v0.x) + v0.y
				double m1 = (v1.z - v0.z) / (v1.x - v0.x);
				double x = (m1 * v0.x - v0.z - mx * tri.vertex[0].x + my * (v0.y - m0 * v0.x - tri.vertex[0].y) + tri.vertex[0].z)
						/ (m1 - mx - my * m0);

				point = new Vector3d(x, m0 * (x - v0.x) + v0.y, m1 * (x - v0.x) + v0.z);
			}
		}

		//is that between v0 and v1 on line?
		if((point.x > v0.x && point.x > v1.x) || (point.x < v0.x && point.x < v1.x))
			return null;
		if((point.y > v0.y && point.y > v1.y) || (point.y < v0.y && point.y < v1.y))
			return null;
		if((point.z > v0.z && point.z > v1.z) || (point.z < v0.z && point.z < v1.z))
			return null;

		//is that within tri?
		Vector3d vec0 = point.subtract(tri.vertex[0]).normalize();
		Vector3d vec1 = point.subtract(tri.vertex[1]).normalize();
		Vector3d vec2 = point.subtract(tri.vertex[2]).normalize();
		double ang = Math.acos(vec0.dotProduct(vec1)) + Math.acos(vec1.dotProduct(vec2))
			     + Math.acos(vec2.dotProduct(vec0));
		if(Math.abs(ang - 2 * Math.PI) > .00001 || Double.isNaN(ang))
			return null;

		return point;
	}
}