package engine.util;

public class Matrix3d {
	private double[][] d;

	public Matrix3d() {
		d = new double[][] {{1, 0, 0}, {0, 1, 0}, {0, 0, 1}};
	}

	public Matrix3d(double[][] d) {
		this.d = new double[3][3];
		for(int i = 0; i < 3; i++)
			for(int j = 0; j < 3; j++)
				this.d[i][j] = d[i][j];
	}

	public Matrix3d(Vector3d a, Vector3d b, Vector3d c) {
		d = new double[][] {{a.x, a.y, a.z},
							{b.x, b.y, b.z},
							{c.x, c.y, c.z}};
	}

	public Matrix3d(Matrix3d m) {
		d = new double[3][3];
		for(int i = 0; i < 3; i++)
			for(int j = 0; j < 3; j++)
				d[i][j] = m.d[i][j];
	}

	public Matrix3d add(Matrix3d m) {
		Matrix3d r = new Matrix3d();
		for(int i = 0; i < 3; i++)
			for(int j = 0; j < 3; j++)
				r.d[i][j] = d[i][j] + m.d[i][j];
		return r;
	}

	public Matrix3d multiply(Matrix3d m) {
		Matrix3d r = new Matrix3d();
		for(int i = 0; i < 3; i++)
			for(int j = 0; j < 3; j++)
				r.d[i][j] = d[i][0] * m.d[0][j] +
							d[i][1] * m.d[1][j] +
							d[i][2] * m.d[2][j];
		return r;
	}

	public Vector3d multiply(Vector3d v) {
		return new Vector3d(
			v.x * d[0][0] + v.y * d[0][1] + v.z * d[0][2],
			v.x * d[1][0] + v.y * d[1][1] + v.z * d[1][2],
			v.x * d[2][0] + v.y * d[2][1] + v.z * d[2][2] );
	}

	public Matrix3d multiply(double a) {
		Matrix3d r = new Matrix3d();
		for(int i = 0; i < 3; i++)
			for(int j = 0; j < 3; j++)
				r.d[i][j] = d[i][j] * a;
		return r;
	}

	public Matrix3d divide(double a) {
		Matrix3d r = new Matrix3d();
		for(int i = 0; i < 3; i++)
			for(int j = 0; j < 3; j++)
				r.d[i][j] = d[i][j] / a;
		return r;
	}

	public double determinant() {
		return d[0][0] * (d[1][1] * d[2][2] - d[2][1] * d[1][2]) +
				d[0][1] * (d[1][0] * d[2][2] - d[2][0] * d[1][2]) +
				d[0][2] * (d[1][0] * d[2][1] - d[2][0] * d[1][1]);
	}

	public Matrix3d inverse() {
		double determinant = determinant();
		if(determinant == 0)
			return divide(0);

		Matrix3d r = new Matrix3d(new Vector3d(d[1][1] * d[2][2] - d[2][1] * d[1][2],
												d[0][2] * d[2][1] - d[2][2] * d[0][1],
												d[0][1] * d[1][2] - d[1][1] * d[0][2]),
									new Vector3d(d[1][2] * d[2][0] - d[2][2] * d[1][0],
												d[0][0] * d[2][2] - d[2][0] * d[0][2],
												d[0][2] * d[1][0] - d[1][2] * d[0][0]),
									new Vector3d(d[1][0] * d[2][1] - d[2][0] * d[1][1],
												d[0][1] * d[2][0] - d[2][1] * d[0][0],
												d[0][0] * d[1][1] - d[1][0] * d[0][1]));
		return r.divide(determinant);
	}

/*	public Vector3d getEigenvalues() {
		double a = d[0][0] + d[1][1] + d[2][2];			//commonly b, c, d in cubic formula
		double b = d[1][1] * (d[0][0] + d[2][2] + d[2][0]) + d[0][0] * d[2][2] + d[2][1] * d[1][2] - d[1][0] * d[0][1];
		double c = d[0][1] * (d[1][0] * d[2][2] - d[2][0] * d[1][2]) + d[0][2] * (d[1][0] * d[2][1] - d[2][0] * d[1][1]) +
						d[0][0] * (d[1][1] * d[2][2] - d[2][1] * d[1][2]);

		
	} This was interesting but ultimately required implementing complex numbers*/

	public double get(int i, int j) {
		return d[i][j];
	}

	public void set(double d, int i, int j) {
		this.d[i][j] = d;
	}

	public String toString() {
		return d[0][0] + ", " + d[0][1] + ", " + d[0][2] + "\n" +
				d[1][0] + ", " + d[1][1] + ", " + d[1][2] + "\n" +
				d[2][0] + ", " + d[2][1] + ", " + d[2][2];
	}
}