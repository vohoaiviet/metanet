/* $Id: FTTextureGlyph.java,v 1.3 2005/07/27 23:14:31 joda Exp $ */
package net.sourceforge.ftgl.glyph;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import javax.media.opengl.GL;
import com.sun.opengl.util.BufferUtil;
import net.sourceforge.ftgl.FTGlyphContainer;
import net.sourceforge.ftgl.util.Vector3f;


/**
 * FTTextureGlyph is a specialisation of FTGlyph for creating texture glyphs.
 * 
 * @see FTGlyphContainer
 */
public class FTTextureGlyph extends FTGlyph
{

	/**
	 * The width of the glyph 'image'
	 */
	private int destWidth = 0;

	/**
	 * The height of the glyph 'image'
	 */
	private int destHeight = 0;

	/**
	 * Vector from the pen position to the topleft corner of the pixmap
	 */
	private Vector3f pos = new Vector3f();

	/**
	 * The texture co-ords of this glyph within the texture.
	 */
	private Vector3f[] uv = new Vector3f[2];

	/**
	 * The texture index that this glyph is contained in.
	 */
	private int glTextureID;

	/**
	 * The texture index of the currently active texture We call glGetIntegerv(
	 * GL_TEXTURE_2D_BINDING, activeTextureID); to get the currently active
	 * texture to try to reduce the number of texture bind operations
	 */
	private int [] activeTextureID = new int[1];

	private int xOffset = 0;
	private int yOffset = 0;
	private int width  = 0;
	private int height = 0;

	/**
	 * Constructor
	 * 
	 * @param glyph The Freetype glyph to be processed
	 * @param id The id of the texture that this glyph will be drawn in
	 * @param xOffset The x offset into the parent texture to draw this glyph
	 * @param yOffset The y offset into the parent texture to draw this glyph
	 * @param width The width of the parent texture
	 * @param height The height (number of rows) of the parent texture
	 */
	public FTTextureGlyph(Shape glyph, int id, int xOffset, int yOffset, int width, int height)
	{
		super(glyph);
		this.glTextureID = id;
		this.activeTextureID[0] = -1;

		this.xOffset = xOffset;
		this.yOffset = yOffset;

		this.width = width;
		this.height = height;

		this.uv[0] = new Vector3f();
		this.uv[1] = new Vector3f();
	}

	/**
	 * {@inheritDoc}
	 */
	protected void createDisplayList()
	{
		Rectangle bounds = this.glyph.getBounds();

		this.destWidth  = bounds.width;
		this.destHeight = bounds.height;

		if (this.destWidth == 0 || this.destHeight == 0) return;

//		int[] rgbarray = new int[this.destWidth*this.destHeight];

		BufferedImage image = new BufferedImage(bounds.width, bounds.height, BufferedImage.TYPE_BYTE_GRAY);
		Graphics2D g2d = (Graphics2D)image.getGraphics();
		g2d.scale(1.0f, -1.0f);
		g2d.setColor(Color.BLACK);
		g2d.fillRect(0, 0, bounds.width, bounds.height);
		g2d.setColor(Color.WHITE);
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.translate(-bounds.getX(), -bounds.getMaxY());
		g2d.fill(this.glyph);

//		image.getRGB(0, 0, this.destWidth, this.destHeight, rgbarray, 0, this.destWidth);

		
		ByteBuffer pix = BufferUtil.newByteBuffer(this.destWidth*this.destHeight);
		
		for(int i = 0; i < this.destHeight; i++)
			for (int j = 0; j < this.destWidth; j++)
				pix.put(i*this.destWidth + j, (byte) image.getRGB(j, i));
		if(this.destWidth != 0 && this.destHeight != 0)
		{
			this.gl.glPushClientAttrib( GL.GL_CLIENT_PIXEL_STORE_BIT);
			this.gl.glPixelStorei(GL.GL_UNPACK_LSB_FIRST, GL.GL_FALSE);
			this.gl.glPixelStorei(GL.GL_UNPACK_ROW_LENGTH, 0);
			this.gl.glPixelStorei(GL.GL_UNPACK_ALIGNMENT, 1);

			this.gl.glBindTexture(GL.GL_TEXTURE_2D, this.glTextureID);
			this.gl.glTexSubImage2D(GL.GL_TEXTURE_2D, 0, this.xOffset, this.yOffset, this.destWidth, this.destHeight, GL.GL_ALPHA, GL.GL_UNSIGNED_BYTE, pix);

			this.gl.glPopClientAttrib();
//		      0
//		      +----+
//		      |    |
//		      |    |
//		      |    |
//		      +----+
//		           1

			this.uv[0].x = (float)this.xOffset/this.width;
			this.uv[0].y = (float)this.yOffset/this.height;
			this.uv[1].x = (float)(this.xOffset+this.destWidth)/this.width;
			this.uv[1].y = (float)(this.yOffset+this.destHeight)/this.height;
			assert this.xOffset==(this.uv[0].x*this.width):"floating point conversion problem (xOffset)";
			assert this.yOffset==(this.uv[0].y*this.height):"floating point conversion problem (yOffset)";
			assert this.xOffset+this.destWidth==(this.uv[1].x*this.width):"floating point conversion problem (xOffset+width)";
			assert this.yOffset+this.destHeight==(this.uv[1].y*this.height):"floating point conversion problem (yOffset+height)";
		}

		this.pos.x = (float)this.glyph.getBounds().getMinX();
		this.pos.y = (float)this.glyph.getBounds().getMaxY();
	}

	/**
	 * {@inheritDoc}
	 */
	public float render(final float x, final float y, final float z)
	{
		if (this.destWidth == 0 || this.destHeight == 0) return this.advance;

		this.gl.glGetIntegerv(GL.GL_TEXTURE_2D_ARRAY_EXT, this.activeTextureID,0);
		if(this.activeTextureID[0] != this.glTextureID)
		{
			this.gl.glBindTexture(GL.GL_TEXTURE_2D, this.glTextureID);
		}

		this.gl.glBegin(GL.GL_QUADS);
		this.gl.glTexCoord2f( this.uv[0].x, this.uv[0].y);
		this.gl.glVertex2f( (float)x + this.pos.x, (float)y + this.pos.y);

		this.gl.glTexCoord2f(this.uv[0].x, this.uv[1].y);
		this.gl.glVertex2f((float)x + this.pos.x, (float)y + this.pos.y - this.destHeight);

		this.gl.glTexCoord2f(this.uv[1].x, this.uv[1].y);
		this.gl.glVertex2f((float)x + this.destWidth + this.pos.x, (float)y + this.pos.y - this.destHeight);

		this.gl.glTexCoord2f(this.uv[1].x, this.uv[0].y);
		this.gl.glVertex2f((float)x + this.destWidth + this.pos.x, (float)y + this.pos.y);
		this.gl.glEnd();

		return this.advance;
	}

}