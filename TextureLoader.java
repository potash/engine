package engine;

import engine.node.Texture;

import javax.imageio.ImageIO;
import java.awt.image.*;
import java.awt.color.ColorSpace;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import java.net.URL;
import java.util.HashMap;
import java.util.Hashtable;

import net.java.games.jogl.GLDrawable;
import net.java.games.jogl.GL;
import net.java.games.jogl.GLU;

public class TextureLoader {
	private HashMap table = new HashMap();
	private GL gl;
	private GLU glu;

	private ColorModel glAlphaColorModel;
	private ColorModel glColorModel;

	public TextureLoader(GLDrawable drawable) {
		gl = drawable.getGL();
		glu = drawable.getGLU();

		glAlphaColorModel = new ComponentColorModel(ColorSpace.getInstance(ColorSpace.CS_sRGB),
							new int[] {8,8,8,8},
							true,
							false,
							ComponentColorModel.TRANSLUCENT,
							DataBuffer.TYPE_BYTE);

		glColorModel = new ComponentColorModel(ColorSpace.getInstance(ColorSpace.CS_sRGB),
							new int[] {8,8,8,0},
							false,
							false,
							ComponentColorModel.OPAQUE,
							DataBuffer.TYPE_BYTE);
	}

	private int createTextureID() {
		int[] tmp = new int[1];
		gl.glGenTextures(1, tmp);
		return tmp[0];
	}

	public Texture getTexture(String name, String resourceName) throws IOException {
		Texture tex = (Texture) table.get(resourceName);

		if(tex != null) {
			return tex;
		}

		tex = getTexture(name,resourceName,
				GL.GL_TEXTURE_2D, // target
				GL.GL_RGBA,	// dst pixel format
				GL.GL_LINEAR, // min filter (unused)
				GL.GL_LINEAR, // mag filter (unused)
				true, // wrap?
				false); // mipmap?

		table.put(resourceName,tex);

		return tex;
	}

	public Texture getTexture(String name, String resourceName, int target, int dstPixelFormat,
							int minFilter, int magFilter, boolean wrap, boolean mipmapped) throws IOException {
		int srcPixelFormat = 0;

		// create the texture ID for this texture
		int textureID = createTextureID();
		Texture texture = new Texture(resourceName,target,textureID);

		// bind this texture
		gl.glBindTexture(target, textureID);

		BufferedImage bufferedImage = loadImage(resourceName);
		// don't need it?
		texture.setBufferedImage(bufferedImage);
		texture.setWidth(bufferedImage.getWidth());
		texture.setHeight(bufferedImage.getHeight());

		if(bufferedImage.getColorModel().hasAlpha()) {
			srcPixelFormat = GL.GL_RGBA;
		} else {
			srcPixelFormat = GL.GL_RGB;
		}

		// convert that image into a byte buffer of texture data
		ByteBuffer textureBuffer = convertImageData(bufferedImage,texture);
		int wrapMode = wrap ? GL.GL_REPEAT : GL.GL_CLAMP;

		if(target == GL.GL_TEXTURE_2D) {
			gl.glTexParameteri(target, GL.GL_TEXTURE_WRAP_S, wrapMode);
			gl.glTexParameteri(target, GL.GL_TEXTURE_WRAP_T, wrapMode);
			gl.glTexParameteri(target, GL.GL_TEXTURE_MIN_FILTER, minFilter);
			gl.glTexParameteri(target, GL.GL_TEXTURE_MAG_FILTER, magFilter);
		}

		// create either a series of mipmaps of a single texture image based on what's loaded
		if(mipmapped) {
			glu.gluBuild2DMipmaps(target,
								dstPixelFormat,
								bufferedImage.getWidth(),
								bufferedImage.getHeight(),
								srcPixelFormat,
								GL.GL_UNSIGNED_BYTE,
								textureBuffer);
		} else {
			gl.glTexImage2D(target,
							0,
							dstPixelFormat,
							bufferedImage.getWidth(),
							bufferedImage.getHeight(),
							0,
							srcPixelFormat,
							GL.GL_UNSIGNED_BYTE,
							textureBuffer);
		}

		return texture;
	}

	private ByteBuffer convertImageData(BufferedImage bufferedImage,Texture texture) throws IOException {
		ByteBuffer imageBuffer = null;
		WritableRaster raster;
		BufferedImage texImage;

		int texWidth = 2;
		int texHeight = 2;

		while (texWidth < bufferedImage.getWidth()) {
			texWidth *= 2;
		}
		while (texHeight < bufferedImage.getHeight()) {
			texHeight *= 2;
		}

		texture.setHeight(texHeight);
		texture.setWidth(texWidth);

		if(bufferedImage.getColorModel().hasAlpha()) {
			raster = Raster.createInterleavedRaster(DataBuffer.TYPE_BYTE,texWidth,texHeight,4,null);
			texImage = new BufferedImage(glAlphaColorModel,raster,false,new Hashtable());
		} else {
			raster = Raster.createInterleavedRaster(DataBuffer.TYPE_BYTE,texWidth,texHeight,3,null);
			texImage = new BufferedImage(glColorModel,raster,false,new Hashtable());
		}

		texImage.getGraphics().drawImage(bufferedImage,0,0,null);

		byte[] data = ((DataBufferByte) texImage.getRaster().getDataBuffer()).getData();

		imageBuffer = ByteBuffer.allocateDirect(data.length);
		imageBuffer.order(ByteOrder.nativeOrder());
		imageBuffer.put(data, 0, data.length);

		return imageBuffer;
	}

	private BufferedImage loadImage(String ref) throws IOException {
		URL url = TextureLoader.class.getClassLoader().getResource(ref);

		if(url == null) {
			throw new IOException("Cannot find: "+ref);
		}

		BufferedImage bufferedImage = ImageIO.read(new BufferedInputStream(getClass().getClassLoader().getResourceAsStream(ref)));

		return bufferedImage;
	}
}