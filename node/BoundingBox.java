package engine.node;

import engine.util.Vector3d;
import engine.util.Quaternion;
import engine.util.Matrix3d;

public class BoundingBox {
	private Vector3d position, xNorm, yNorm, zNorm;
	private double xLength, yLength, zLength;

	public BoundingBox(Vector3d min, Vector3d max) {
		position = new Vector3d(max.add(min).divide(2));
		Vector3d temp = new Vector3d(max.x - min.x, 0, 0);
		xNorm = temp.normalize();
		xLength = temp.length();
		temp = new Vector3d(0, max.y - min.y, 0);
		yNorm = temp.normalize();
		yLength = temp.length();
		temp = new Vector3d(0, 0, max.z - min.z);
		zNorm = temp.normalize();
		zLength = temp.length();
	}

/*	public BoundingBox(Vector3d[] v) {
		position = new Vector3d();
		for(int i = 0; i < v.length; i++)
			position = position.add(v[i]);
		position = position.divide(v.length);

		Matrix3d covariance = new Matrix3d(new Vector3d(), new Vector3d(), new Vector3d());
		for(int i = 0; i < v.length - 1; i++)
			covariance = covariance.add(v[i].subtract(position).directProduct(v[i].subtract(position)));
		covariance = covariance.divide(v.length);
	}*/

	public void rotate(Quaternion q) {
		xNorm = xNorm.add(position).rotate(q).subtract(position);
		yNorm = yNorm.add(position).rotate(q).subtract(position);
		zNorm = zNorm.add(position).rotate(q).subtract(position);
	}

	public Collision getCollision(BoundingBox b, Vector3d relativePosition, Vector3d relativeVelocity, double dtime, double precision) {
		if(!checkCollision(b, relativePosition, relativeVelocity, dtime))
			return null;
		if(relativeVelocity.dotProduct(relativePosition) > 0)	//should include real normal?
			return null;

		double offset = 0;
		while(checkCollision(b, relativePosition, relativeVelocity, 0)) {	//0 or precision????
			relativePosition = relativePosition.subtract(relativeVelocity.multiply(dtime));
			offset -= dtime;
		}

		double deltaTime = precision / 2;
		dtime /= 2;
		while(dtime >= precision) {
			if(checkCollision(b, relativePosition, relativeVelocity, dtime)) {
				dtime /= 2;
			} else {
				relativePosition = relativePosition.add(relativeVelocity.multiply(dtime));
				deltaTime += dtime;
				dtime /= 2;
			}
		}

		return new Collision(0, null, null, deltaTime + offset);
	}

	public boolean checkCollision(BoundingBox b, Vector3d relativePosition, Vector3d relativeVelocity, double dtime) {
		Vector3d temp = b.position.add(relativePosition).subtract(position).multiply(2);	//I'm not sure where the factor of 2 comes from
		Vector3d bposition = new Vector3d(temp.dotProduct(xNorm), temp.dotProduct(yNorm), temp.dotProduct(zNorm));
		temp = b.position.add(relativePosition.add(relativeVelocity.multiply(dtime))).subtract(position).multiply(2);
		Vector3d bposition2 = new Vector3d(temp.dotProduct(xNorm), temp.dotProduct(yNorm), temp.dotProduct(zNorm));

		Vector3d bx = new Vector3d(xNorm.dotProduct(b.xNorm), xNorm.dotProduct(b.yNorm), xNorm.dotProduct(b.zNorm));
		Vector3d babsx = new Vector3d(Math.abs(bx.x), Math.abs(bx.y), Math.abs(bx.z));
		double R01 = xLength + b.xLength * babsx.x + b.yLength * babsx.y + b.zLength * babsx.z;
		if(Math.abs(bposition.x) > R01 && Math.abs(bposition2.x) > R01 && bposition.x / bposition2.x > 0)
			return false;

		Vector3d by = new Vector3d(yNorm.dotProduct(b.xNorm), yNorm.dotProduct(b.yNorm), yNorm.dotProduct(b.zNorm));
		Vector3d babsy = new Vector3d(Math.abs(by.x), Math.abs(by.y), Math.abs(by.z));
		R01 = yLength + b.xLength * babsy.x + b.yLength * babsy.y + b.zLength * babsy.z;
		if(Math.abs(bposition.y) > R01 && Math.abs(bposition2.y) > R01 && bposition.y / bposition2.y > 0)
			return false;

		Vector3d bz = new Vector3d(zNorm.dotProduct(b.xNorm), zNorm.dotProduct(b.yNorm), zNorm.dotProduct(b.zNorm));
		Vector3d babsz = new Vector3d(Math.abs(bz.x), Math.abs(bz.y), Math.abs(bz.z));
		R01 = zLength + b.xLength * babsz.x + b.yLength * babsz.y + b.zLength * babsz.z;
		if(Math.abs(bposition.z) > R01 && Math.abs(bposition2.z) > R01 && bposition.z / bposition2.z > 0)
			return false;

		double R = bposition.x * bx.x + bposition.y * by.x + bposition.z * bz.x;
		double R2 = bposition2.x * bx.x + bposition2.y * by.x + bposition2.z * bz.x;
		R01 = b.xLength + xLength * babsx.x + yLength * babsy.x + zLength * babsz.x;
		if(Math.abs(R) > R01 && Math.abs(R2) > R01 && R / R2 > 0)
			return false;

		R = bposition.x * bx.y + bposition.y * by.y + bposition.z * bz.y;
		R2 = bposition2.x * bx.y + bposition2.y * by.y + bposition2.z * bz.y;
		R01 = b.yLength + xLength * babsx.y + yLength * babsy.y + zLength * babsz.y;
		if(Math.abs(R) > R01 && Math.abs(R2) > R01 && R / R2 > 0)
			return false;

		R = bposition.x * bx.z + bposition.y * by.z + bposition.z * bz.z;
		R2 = bposition2.x * bx.z + bposition2.y * by.z + bposition2.z * bz.z;
		R01 = b.zLength + xLength * babsx.z + yLength * babsy.z + zLength * babsz.z;
		if(Math.abs(R) > R01 && Math.abs(R2) > R01 && R / R2 > 0)
			return false;

		R = bposition.z * by.x - bposition.y * bz.x;
		R2 = bposition2.z * by.x - bposition2.y * bz.x;
		R01 = yLength * babsz.x + zLength * babsy.x + b.yLength * babsx.z + b.zLength * babsx.y;
		if(Math.abs(R) > R01 && Math.abs(R2) > R01 && R / R2 > 0)
			return false;

		R = bposition.z * by.y - bposition.y * bz.y;
		R2 = bposition2.z * by.y - bposition2.y * bz.y;
		R01 = yLength * babsz.y + zLength * babsy.y + b.xLength * babsx.z + b.zLength * babsx.x;
		if(Math.abs(R) > R01 && Math.abs(R2) > R01 && R / R2 > 0)
			return false;

		R = bposition.z * by.z - bposition.y * bz.z;
		R2 = bposition2.z * by.z - bposition2.y * bz.z;
		R01 = yLength * babsz.z + zLength * babsy.z + b.xLength * babsx.y + b.yLength * babsx.x;
		if(Math.abs(R) > R01 && Math.abs(R2) > R01 && R / R2 > 0)
			return false;

		R = bposition.x * bz.x - bposition.z * bx.x;
		R2 = bposition2.x * bz.x - bposition2.z * bx.x;
		R01 = xLength * babsz.x + zLength * babsx.x + b.yLength * babsy.z + b.zLength * babsy.y;
		if(Math.abs(R) > R01 && Math.abs(R2) > R01 && R / R2 > 0)
			return false;

		R = bposition.x * bz.y - bposition.z * bx.y;
		R2 = bposition2.x * bz.y - bposition2.z * bx.y;
		R01 = xLength * babsz.y + zLength * babsx.y + b.xLength * babsy.z + b.zLength * babsy.x;
		if(Math.abs(R) > R01 && Math.abs(R2) > R01 && R / R2 > 0)
			return false;

		R = bposition.x * bz.z - bposition.z * bx.z;
		R2 = bposition2.x * bz.z - bposition2.z * bx.z;
		R01 = xLength * babsz.z + zLength * babsx.z + b.xLength * babsy.y + b.yLength * babsy.x;
		if(Math.abs(R) > R01 && Math.abs(R2) > R01 && R / R2 > 0)
			return false;

		R = bposition.y * bx.x - bposition.x * by.x;
		R2 = bposition2.y * bx.x - bposition2.x * by.x;
		R01 = xLength * babsy.x + yLength * babsx.x + b.yLength * babsz.z + b.zLength * babsz.y;
		if(Math.abs(R) > R01 && Math.abs(R2) > R01 && R / R2 > 0)
			return false;

		R = bposition.y * bx.y - bposition.x * by.y;
		R2 = bposition2.y * bx.y - bposition2.x * by.y;
		R01 = xLength * babsy.y + yLength * babsx.y + b.xLength * babsz.z + b.zLength * babsz.x;
		if(Math.abs(R) > R01 && Math.abs(R2) > R01 && R / R2 > 0)
			return false;

		R = bposition.y * bx.z - bposition.x * by.z;
		R2 = bposition2.y * bx.z - bposition2.x * by.z;
		R01 = xLength * babsy.z + yLength * babsx.z + b.xLength * babsz.y + b.yLength * babsz.x;
		if(Math.abs(R) > R01 && Math.abs(R2) > R01 && R / R2 > 0)
			return false;

		temp = relativeVelocity.crossProduct(relativePosition);
		Vector3d alpha = new Vector3d(relativeVelocity.dotProduct(xNorm), relativeVelocity.dotProduct(yNorm), relativeVelocity.dotProduct(zNorm));
		if(Math.abs(xNorm.dotProduct(temp)) > yLength * Math.abs(alpha.z) + zLength * Math.abs(alpha.y) + b.xLength * Math.abs(by.x * alpha.z - bz.x * alpha.y) + b.yLength * Math.abs(by.y * alpha.z - bz.y * alpha.y) + b.zLength * Math.abs(by.z * alpha.z - bz.z * alpha.y))
			return false;
		if(Math.abs(yNorm.dotProduct(temp)) > xLength * Math.abs(alpha.z) + zLength * Math.abs(alpha.x) + b.xLength * Math.abs(bx.x * alpha.z - bz.x * alpha.x) + b.yLength * Math.abs(bx.y * alpha.z - bz.y * alpha.x) + b.zLength * Math.abs(bx.z * alpha.z - bz.z * alpha.x))
			return false;
		if(Math.abs(zNorm.dotProduct(temp)) > xLength * Math.abs(alpha.y) + yLength * Math.abs(alpha.x) + b.xLength * Math.abs(bx.x * alpha.y - by.x * alpha.x) + b.yLength * Math.abs(bx.y * alpha.y - by.y * alpha.x) + b.zLength * Math.abs(bx.z * alpha.y - by.z * alpha.x))
			return false;

		Vector3d beta = new Vector3d(relativeVelocity.dotProduct(b.xNorm), relativeVelocity.dotProduct(b.yNorm), relativeVelocity.dotProduct(b.zNorm));
		if(Math.abs(b.xNorm.dotProduct(temp)) > b.yLength * Math.abs(beta.z) + b.zLength * Math.abs(beta.y) + xLength * Math.abs(bx.y * beta.z - bx.z * beta.y) + yLength * Math.abs(by.y * beta.z - by.z * beta.y) + zLength * Math.abs(bz.y * beta.z - bz.z * beta.y))
			return false;
		if(Math.abs(b.yNorm.dotProduct(temp)) > b.xLength * Math.abs(beta.z) + b.zLength * Math.abs(beta.x) + xLength * Math.abs(bx.x * beta.z - bx.z * beta.x) + yLength * Math.abs(by.x * beta.z - by.z * beta.x) + zLength * Math.abs(bz.x * beta.z - bz.z * beta.x))
			return false;
		if(Math.abs(b.zNorm.dotProduct(temp)) > b.xLength * Math.abs(beta.y) + b.yLength * Math.abs(beta.x) + xLength * Math.abs(bx.x * beta.y - bx.y * beta.x) + yLength * Math.abs(by.x * beta.y - by.y * beta.x) + zLength * Math.abs(bz.x * beta.y - bz.y * beta.x))
			return false;

		return true;
	}

	public Collision getCollision(Triangle t, Vector3d relativePosition, Vector3d relativeVelocity, double dtime, double precision) {
		if(!checkCollision(t, relativePosition, relativeVelocity, dtime))
			return null;
		if(relativeVelocity.dotProduct(relativePosition) > 0)	//should include real normal?
			return null;

		double offset = 0;
		while(checkCollision(t, relativePosition, relativeVelocity, 0)) {
			relativePosition = relativePosition.subtract(relativeVelocity.multiply(dtime));
			offset -= dtime;
		}

		double deltaTime = precision / 2;
		dtime /= 2;
		while(dtime >= precision) {
			if(checkCollision(t, relativePosition, relativeVelocity, dtime)) {
				dtime /= 2;
			} else {
				relativePosition = relativePosition.add(relativeVelocity.multiply(dtime));
				deltaTime += dtime;
				dtime /= 2;
			}
		}

		return new Collision(0, null, null, deltaTime + offset);
	}

	public boolean checkCollision(Triangle t, Vector3d relativePosition, Vector3d relativeVelocity, double dtime) {
		Vector3d tposition = t.vertex[0].add(relativePosition).subtract(position).multiply(2);
		Vector3d[] edge = t.getEdge();
		Vector3d normal = t.getNormal();
		Vector3d an = new Vector3d(xNorm.dotProduct(normal), yNorm.dotProduct(normal), zNorm.dotProduct(normal));
		relativeVelocity = relativeVelocity.multiply(dtime * 2);

		double p = normal.dotProduct(tposition);
		double w = normal.dotProduct(relativeVelocity);
		double R = xLength * Math.abs(an.x) + yLength * Math.abs(an.y) + zLength * Math.abs(an.z);
		if(p > R && p + w > R)
			return false;
		else if(p < -R && p + w < -R)
			return false;

		Vector3d ae0 = new Vector3d(xNorm.dotProduct(edge[0]), 0, 0);
		Vector3d ae1 = new Vector3d(xNorm.dotProduct(edge[1]), 0, 0);
		p = xNorm.dotProduct(tposition);
		w = xNorm.dotProduct(relativeVelocity);
		double d = ae0.x;
		double d2 = ae1.x;
		R = xLength;
		if(p > R) {
			if(d >= 0) {
				if(d2 >= 0) {
					if(p + w > R)
						return false;
				} else if(p + d2 > R && p + d2 + w > R)
					return false;
			} else if(d2 <= d) {
				if(p + d2 > R && p + d2 + w > R)
					return false;
			} else if(p + d > R && p + d + w > R)
				return false;
		} else if(p < -R) {
			if(d <= 0) {
				if(d2 <= 0) {
					if(p + w < -R)
						return false;
				} else if(p + d2 < -R && p + d2 + w < -R)
					return false;
			} else if(d2 >= d) {
				if(p + d2 < -R && p + d2 + w < -R)
					return false;
			} else if(p + d < -R && p + d + w < -R)
				return false;
		}

		ae0.y = yNorm.dotProduct(edge[0]);
		ae1.y = yNorm.dotProduct(edge[1]);
		p = yNorm.dotProduct(tposition);
		w = yNorm.dotProduct(relativeVelocity);
		d = ae0.y;
		d2 = ae1.y;
		R = yLength;
		if(p > R) {
			if(d >= 0) {
				if(d2 >= 0) {
					if(p + w > R)
						return false;
				} else if(p + d2 > R && p + d2 + w > R)
					return false;
			} else if(d2 <= d) {
				if(p + d2 > R && p + d2 + w > R)
					return false;
			} else if(p + d > R && p + d + w > R)
				return false;
		} else if(p < -R) {
			if(d <= 0) {
				if(d2 <= 0) {
					if(p + w < -R)
						return false;
				} else if(p + d2 < -R && p + d2 + w < -R)
					return false;
			} else if(d2 >= d) {
				if(p + d2 < -R && p + d2 + w < -R)
					return false;
			} else if(p + d < -R && p + d + w < -R)
				return false;
		}

		ae0.z = zNorm.dotProduct(edge[0]);
		ae1.z = zNorm.dotProduct(edge[1]);
		p = zNorm.dotProduct(tposition);
		w = zNorm.dotProduct(relativeVelocity);
		d = ae0.z;
		d2 = ae1.z;
		R = zLength;
		if(p > R) {
			if(d >= 0) {
				if(d2 >= 0) {
					if(p + w > R)
						return false;
				} else if(p + d2 > R && p + d2 + w > R)
					return false;
			} else if(d2 <= d) {
				if(p + d2 > R && p + d2 + w > R)
					return false;
			} else if(p + d > R && p + d + w > R)
				return false;
		} else if(p < -R) {
			if(d <= 0) {
				if(d2 <= 0) {
					if(p + w < -R)
						return false;
				} else if(p + d2 < -R && p + d2 + w < -R)
					return false;
			} else if(d2 >= d) {
				if(p + d2 < -R && p + d2 + w < -R)
					return false;
			} else if(p + d < -R && p + d + w < -R)
				return false;
		}

		ae0.z = Math.abs(ae0.z);
		ae0.y = Math.abs(ae0.y);
		Vector3d l = xNorm.crossProduct(edge[0]);
		p = l.dotProduct(tposition);
		w = l.dotProduct(relativeVelocity);
		d = an.x;
		R = yLength * ae0.z + zLength * ae0.y;
		if(p > R) {
			if(d >= 0) {
				if(p + w > R)
					return false;
			} else if(p + d > R && p + d + w > R)
				return false;
		} else if(p < -R) {
			if(d <= 0) {
				if(p + w < -R)
					return false;
			} else if(p + d < -R && p + d + w < -R)
				return false;
		}

		ae1.z = Math.abs(ae1.z);
		ae1.y = Math.abs(ae1.y);
		l = xNorm.crossProduct(edge[1]);
		p = l.dotProduct(tposition);
		w = l.dotProduct(relativeVelocity);
		d = -an.x;
		R = yLength * ae1.z + zLength * ae1.y;
		if(p > R) {
			if(d >= 0) {
				if(p + w > R)
					return false;
			} else if(p + d > R && p + d + w > R)
				return false;
		} else if(p < -R) {
			if(d <= 0) {
				if(p + w < -R)
					return false;
			} else if(p + d < -R && p + d + w < -R)
				return false;
		}

		edge[2] = edge[1].subtract(edge[0]);
		Vector3d ae2 = new Vector3d(0, Math.abs(yNorm.dotProduct(edge[2])), Math.abs(zNorm.dotProduct(edge[2])));
		l = xNorm.crossProduct(edge[2]);
		p = l.dotProduct(tposition);
		w = l.dotProduct(relativeVelocity);
		d = -an.x;
		R = yLength * ae2.z + zLength * ae2.y;
		if(p > R) {
			if(d >= 0) {
				if(p + w > R)
					return false;
			} else if(p + d > R && p + d + w > R)
				return false;
		} else if(p < -R) {
			if(d <= 0) {
				if(p + w < -R)
					return false;
			} else if(p + d < -R && p + d + w < -R)
				return false;
		}

		ae0.x = Math.abs(ae0.x);
		l = yNorm.crossProduct(edge[0]);
		p = l.dotProduct(tposition);
		w = l.dotProduct(relativeVelocity);
		d = an.y;
		R = xLength * ae0.z + zLength * ae0.x;
		if(p > R) {
			if(d >= 0) {
				if(p + w > R)
					return false;
			} else if(p + d > R && p + d + w > R)
				return false;
		} else if(p < -R) {
			if(d <= 0) {
				if(p + w < -R)
					return false;
			} else if(p + d < -R && p + d + w < -R)
				return false;
		}

		ae1.x = Math.abs(ae1.x);
		l = yNorm.crossProduct(edge[1]);
		p = l.dotProduct(tposition);
		w = l.dotProduct(relativeVelocity);
		d = -an.y;
		R = xLength * ae1.z + zLength * ae1.x;
		if(p > R) {
			if(d >= 0) {
				if(p + w > R)
					return false;
			} else if(p + d > R && p + d + w > R)
				return false;
		} else if(p < -R) {
			if(d <= 0) {
				if(p + w < -R)
					return false;
			} else if(p + d < -R && p + d + w < -R)
				return false;
		}

		ae2.x = Math.abs(xNorm.dotProduct(edge[2]));
		l = yNorm.crossProduct(edge[2]);
		p = l.dotProduct(tposition);
		w = l.dotProduct(relativeVelocity);
		d = -an.y;
		R = xLength * ae2.z + zLength * ae2.x;
		if(p > R) {
			if(d >= 0) {
				if(p + w > R)
					return false;
			} else if(p + d > R && p + d + w > R)
				return false;
		} else if(p < -R) {
			if(d <= 0) {
				if(p + w < -R)
					return false;
			} else if(p + d < -R && p + d + w < -R)
				return false;
		}

		l = zNorm.crossProduct(edge[0]);
		p = l.dotProduct(tposition);
		w = l.dotProduct(relativeVelocity);
		d = an.z;
		R = xLength * ae0.y + yLength * ae0.x;
		if(p > R) {
			if(d >= 0) {
				if(p + w > R)
					return false;
			} else if(p + d > R && p + d + w > R)
				return false;
		} else if(p < -R) {
			if(d <= 0) {
				if(p + w < -R)
					return false;
			} else if(p + d < -R && p + d + w < -R)
				return false;
		}

		l = zNorm.crossProduct(edge[1]);
		p = l.dotProduct(tposition);
		w = l.dotProduct(relativeVelocity);
		d = -an.z;
		R = xLength * ae1.y + yLength * ae1.x;
		if(p > R) {
			if(d >= 0) {
				if(p + w > R)
					return false;
			} else if(p + d > R && p + d + w > R)
				return false;
		} else if(p < -R) {
			if(d <= 0) {
				if(p + w < -R)
					return false;
			} else if(p + d < -R && p + d + w < -R)
				return false;
		}

		l = zNorm.crossProduct(edge[2]);
		p = l.dotProduct(tposition);
		w = l.dotProduct(relativeVelocity);
		d = -an.z;
		R = xLength * ae2.y + yLength * ae2.x;
		if(p > R) {
			if(d >= 0) {
				if(p + w > R)
					return false;
			} else if(p + d > R && p + d + w > R)
				return false;
		} else if(p < -R) {
			if(d <= 0) {
				if(p + w < -R)
					return false;
			} else if(p + d < -R && p + d + w < -R)
				return false;
		}
		
		return true;
	}

	public void render(net.java.games.jogl.GL gl) {
		Vector3d x = xNorm.multiply(xLength);
		Vector3d y = yNorm.multiply(yLength);
		Vector3d z = zNorm.multiply(zLength);

		byte[] te = new byte[1];
		gl.glGetBooleanv(gl.GL_TEXTURE_2D, te);
		boolean textureEnabled = te[0] != 0;
		gl.glDisable(gl.GL_TEXTURE_2D);

		gl.glBegin(gl.GL_LINES);
			gl.glColor3f(1, 0, 0);
			Vector3d v = position.subtract(x.divide(2)).subtract(y.divide(2)).subtract(z.divide(2));
			gl.glVertex3d(v.x, v.y, v.z);
			Vector3d t = v.add(x);
			gl.glVertex3d(t.x, t.y, t.z);
			gl.glVertex3d(v.x, v.y, v.z);
			t = v.add(y);
			gl.glVertex3d(t.x, t.y, t.z);
			gl.glVertex3d(v.x, v.y, v.z);
			t = v.add(z);
			gl.glVertex3d(t.x, t.y, t.z);
		gl.glEnd();

		if(textureEnabled) {
			gl.glEnable(gl.GL_TEXTURE_2D);
		}
	}
}
