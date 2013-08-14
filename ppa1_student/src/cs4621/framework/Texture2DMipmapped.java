package cs4621.framework;

import java.io.File;
import java.io.IOException;
import java.nio.Buffer;

import javax.media.opengl.GL2;
import javax.media.opengl.GLProfile;
import javax.media.opengl.glu.GLU;

import com.jogamp.opengl.util.texture.TextureData;
import com.jogamp.opengl.util.texture.TextureIO;

public class Texture2DMipmapped extends TextureTwoDim {
	private static GLU glu = new GLU();
		
	public Texture2DMipmapped(GL2 gl)
	{
		super(gl, GL2.GL_TEXTURE_2D, GL2.GL_RGBA);
	}
	
	public Texture2DMipmapped(GL2 gl, int internalFormat)
	{
		super(gl, GL2.GL_TEXTURE_2D, internalFormat);
	}
	
	public Texture2DMipmapped(GL2 gl, String filename, int internalFormat) throws IOException
	{
		super(gl, GL2.GL_TEXTURE_2D, internalFormat);
		File file = new File(filename);		
		TextureData data = TextureIO.newTextureData(GLProfile.getDefault(), file, false, null);
		setImage(data);
	}
	
	public Texture2DMipmapped(GL2 gl, File file) throws IOException
	{
		super(gl, GL2.GL_TEXTURE_2D, GL2.GL_RGBA);
		TextureData data = TextureIO.newTextureData(GLProfile.getDefault(), file, false, null);
		setImage(data);
	}
	
	public Texture2DMipmapped(GL2 gl, File file, int internalFormat) throws IOException
	{
		super(gl, GL2.GL_TEXTURE_2D, internalFormat);
		TextureData data = TextureIO.newTextureData(GLProfile.getDefault(), file, false, null);
		setImage(data);
	}
	
	@Override
	public void setImage(int width, int height, int format, int type, Buffer buffer)
	{
		this.width = width;
		this.height = height;
		
		Texture oldTexture = TextureUnit.getActiveTextureUnit(gl).getBoundTexture();
		if (oldTexture != this)
			bind();
		
		glu.gluBuild2DMipmaps(target, internalFormat, width, height, format, type, buffer);		
		
		if (oldTexture == null)
			unbind();
		else if (oldTexture != this)
			oldTexture.bind();
		
		allocated = true;
	}	
}
