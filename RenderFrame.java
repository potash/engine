package engine;

import javax.swing.JFrame;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import net.java.games.jogl.GLCanvas;
import net.java.games.jogl.Animator;
import net.java.games.jogl.GLDrawableFactory;
import net.java.games.jogl.GLCapabilities;

public class RenderFrame extends JFrame {
	private Animator animator;

	public RenderFrame(Renderer r, String title, 
			int width, int height, boolean undecorated) {
		
	    GLCanvas canvas = 
		    GLDrawableFactory.getFactory()
		    .createGLCanvas(new GLCapabilities());

	    canvas.addGLEventListener(r);
	    getContentPane().add(canvas);

	    animator = new Animator(canvas);
	    addWindowListener(new WindowAdapter() {
		    public void windowClosing(WindowEvent e) {
			RenderFrame.this.stop();
			System.exit(0);
		    }
	    });

	    setTitle(title);
	    setSize(width, height);
	    setUndecorated(undecorated); 
	    setVisible(true);
	}

	public void start() {
	    animator.start();
	}

	public void stop() {
	    animator.stop();
	}
}
