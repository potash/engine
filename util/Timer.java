package engine.util;

public class Timer {
	private static boolean useDefault = false;
	private double start;
	private double resolution;

	static {
		try {
			System.loadLibrary("JTimer");
		} catch(UnsatisfiedLinkError e) {
			useDefault = true;
		}
	}

	public Timer() {
		if(!useDefault)
			init();
		start = time();
	}

	private double time() {
		if(useDefault)
			return System.currentTimeMillis() / 1000d;
		else
			return getNativeTime();
	}

	public double getTime() {
		return time() - start;
	}

	public long getResolution() {
		if(useDefault)
			return 1000;
		else
			return getNativeResolution();
	}

	private static native double getNativeTime();
	private static native long getNativeResolution();
	private static native void init();
}
