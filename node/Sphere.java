package engine.util;

public class Color3d {
	public double red, green, blue;

	public final static Color3d RED = new Color3d(1, 0, 0),
				GREEN = new Color3d(0, 1, 0),
				BLUE = new Color3d(0, 0, 1),
				WHITE = new Color3d(1, 1, 1);

	public Color3d() {
		red = green = blue = 1;
	}

	public Color3d(double red, double green, double blue) {
		this.red = red;
		this.green = green;
		this.blue = blue;
	}

	public Color3d(Color3d c) {
		this.red = c.red;
		this.green = c.green;
		this.blue = c.blue;
	}

	public boolean equals(Color3d c) {
		return red == c.red && green == c.green && blue == c.blue;
	}

	public String toString() {
		return "(" + red + ", " + green + ", " + blue + ")";
	}
}
                                                                                                                                                                                                                                                                                                     = gl.glGenLists(1);
        gl.glNewList(list, GL.GL_COMPILE_AND_EXECUTE);
        //gl.glColor3d(color.red, color.green, color.blue);
        Vector3d v = new Vector3d();
        double theta1,theta2,theta3;
        double TWOPI = Math.PI * 2,
               PID2 = Math.PI / 2;

        for (int j = 0; j < precision / 2; j++) {
            theta1 = j * TWOPI / precision - PID2;
            theta2 = (j + 1) * TWOPI / precision - PID2;

            gl.glBegin(GL.GL_QUAD_STRIP);
            for (int i = 0;i<=precision;i++) {
                theta3 = i * TWOPI / precision;

                v.x = Math.cos(theta2) * Math.cos(theta3);
                v.y = Math.sin(theta2);
                v.z = Math.cos(theta2) * Math.sin(theta3);

                gl.glNormal3d(v.x,v.y,v.z);
                gl.glTexCoord2d((double)i/precision,(double)2*(j+1)/(double)precision);
                gl.glVertex3d(radius*v.x, radius*v.y, radius*v.z);

                v.x = Math.cos(theta1) * Math.cos(theta3);
                v.y = Math.sin(theta1);
                v.z = Math.cos(theta1) * Math.sin(theta3);

                gl.glNormal3d(v.x,v.y,v.z);
                gl.glTexCoord2d((double)i/precision,(double)2*(j)/precision);
                gl.glVertex3d(radius*v.x, radius*v.y, radius*v.z);
            }
        gl.glEnd();
        }
        gl.glEndList();
    }
}
