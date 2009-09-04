/* $ Id$
 * Created on 11.11.2004
 */
package net.sourceforge.ftgl.glyph;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import javax.media.opengl.GL;

/**
 * FTPixmapGlyph is a specialisation of FTGlyph for creating pixmaps.
 * 
 * @see net.sourceforge.ftgl.FTGlyphContainer
 * @author joda
 */
public class FTPixmapGlyph extends FTGlyph
{
	/**
	 * The width of the glyph 'image'
	 */
	private int destWidth;

	/**
	 * The height of the glyph 'image'
	 */
	private int destHeight;

	/**
	 * offset from the pen position to the topleft corner of the pixmap
	 */
	private float offsetX;

	/**
	 * offset from the pen position to the topleft corner of the pixmap
	 */
	private float offsetY;

	/**
	 * Pointer to the 'image' data
	 */
	private ByteBuffer data;

	/**
	 * {@inheritDoc}
	 */
	protected void createDisplayList()
	{
		Rectangle bounds = this.glyph.getBounds();

		//check the pixel mode
		//ft_pixel_mode_grays

		this.offsetX = (float)bounds.getX();
		this.offsetY = (float)bounds.getY();
		int srcWidth = bounds.width;
		int srcHeight = bounds.height;

	   // FIXME What about dest alignment?
		this.destWidth = srcWidth;
		this.destHeight = srcHeight;

		if( destWidth>0 && destHeight>0)
		{
			BufferedImage image = new BufferedImage( bounds.width, bounds.height,BufferedImage.TYPE_BYTE_GRAY);
			Graphics2D g2d = (Graphics2D)image.getGraphics();
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
			g2d.setColor(Color.WHITE);
			g2d.translate(-bounds.getX(),-bounds.getY());
			g2d.fill(this.glyph);

			this.data = ByteBuffer.allocateDirect(destWidth * destHeight * 4);
			this.data.order(ByteOrder.BIG_ENDIAN);

			int[] line = new int[this.destWidth];

			// Get the current glColor.
			float[] ftglColour = new float[4];
			this.gl.glGetFloatv( GL.GL_CURRENT_COLOR, ftglColour,0);

			byte redComponent =   (byte)(ftglColour[0] * 255.0f + 0.5f);
			byte greenComponent = (byte)(ftglColour[1] * 255.0f + 0.5f);
			byte blueComponent =  (byte)(ftglColour[2] * 255.0f + 0.5f);
			float alpha = ftglColour[3];

			int colorComponent =
				((redComponent & 0xFF) << 24) |
				((greenComponent & 0xFF) << 16) |
				((blueComponent & 0xFF) << 8);

			Raster imager = image.getRaster();
			SampleModel sm = imager.getSampleModel();

			assert sm.getNumBands()==1 : "only 1-Band SampleModels supported";
			assert sm.getSampleSize(0)==8 : "SampleSize should be 8 bit";

			//unsigned char* src = bitmap.buffer;

			if( ftglColour[3] == 1.0f)
			{
				for (int y=0; y<destHeight; y++)
				{
					imager.getSamples(0, y, destWidth, 1, 0, line);
					for (int x=0; x<destWidth; x++)
					{
						this.data.putInt(colorComponent | line[x]);
					}
				}
			}
			else
			{
				for (int y=0; y<destHeight; y++)
				{
					imager.getSamples(0, y, destWidth, 1, 0, line);
					for (int x=0; x<destWidth; x++)
					{
						this.data.put(redComponent);
						this.data.put(greenComponent);
						this.data.put(blueComponent);
						this.data.put((byte)(alpha * line[x] + 0.5f));
					}
				}
			}
		}
	}

	/**
	 * Constructor
	 * 
	 * @param glyph The Freetype glyph to be processed
	 */
	public FTPixmapGlyph(Shape glyph)
	{
		super(glyph);
		this.destWidth = 0;
		this.destHeight = 0;
		this.data = null;
	}


	/**
	 * {@inheritDoc}
	 * Destructor
	 */
	public void dispose()
	{
		if (this.data!=null)
			data = null;
		super.dispose();
	}


	/**
	 * {@inheritDoc}
	 */
	public float render(final float x, final float y, final float z)
	{
		if( this.data!=null)
		{
			// Move the glyph origin
			this.gl.glBitmap( 0, 0, 0.0f, 0.0f, (float)x + this.offsetX, (float)y + this.offsetY, (byte[])null,0);

			this.gl.glPixelStorei(GL.GL_UNPACK_ROW_LENGTH, 0);
			data.rewind();
			this.gl.glDrawPixels( destWidth, destHeight, GL.GL_RGBA, GL.GL_UNSIGNED_BYTE, this.data);

			// Restore the glyph origin
			this.gl.glBitmap( 0, 0, 0.0f, 0.0f, (float)-x - this.offsetX, (float)-y - this.offsetY, (byte[])null,0);
		}

		return advance;
	}

}