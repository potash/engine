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
