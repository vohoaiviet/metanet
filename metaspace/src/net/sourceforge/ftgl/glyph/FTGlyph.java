/* $Id: FTGlyph.java,v 1.2 2005/07/27 23:14:31 joda Exp $ */
package net.sourceforge.ftgl.glyph;

import java.awt.Shape;
import java.awt.font.ShapeGraphicAttribute;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;
import net.sourceforge.ftgl.FTBBox;
import net.sourceforge.ftgl.FTGlyphContainer;

/**
 * FTGlyph is the base class for FTGL glyphs. It provides the interface between Freetype glyphs and
 * their openGL renderable counterparts. This is an abstract class and derived classes must
 * implement the <code>render</code> function.
 * 
 * @see FTGlyphContainer
 * @see FTBBox
 */
public abstract class FTGlyph
{

	protected FTBBox bBox;
	protected float advance = 0.0f;
	protected int err = 0;
	protected Shape glyph;

	/** the glcontext to render with. */
	protected GL gl;
	/** the glucontext to render with. */
	protected GLU glu;
	/** the identifier of the displaylist used by this FTGlyph. */
	protected int glList = -1;

	/**
	 * Constructor
	 * @param glyph The Shape to put in the FTGlyph.
	 */
	public FTGlyph(Shape glyph)
	{
		if(glyph != null)
		{
			this.bBox = new FTBBox(glyph);
			this.glyph = glyph;
			//several options:
			/** due to {@link ShapeGraphicAttribute#getAdvance()} description */
			this.advance = Math.abs(this.bBox.upperX - this.bBox.lowerX);
			/** due to {@link ShapeGraphicAttribute} */
			float shapeadvance = new ShapeGraphicAttribute(glyph, ShapeGraphicAttribute.CENTER_BASELINE, ShapeGraphicAttribute.STROKE).getAdvance();
			/** or create a {@link java.awt.font.TextLayout} and get the advance for the letter, the shape represents */
			//System.out.println("[Boxed] " + this.advance + " [ShapeAttribute] " + shapeadvance);
			this.advance = shapeadvance;
		}
	}

	/**
	 * Constructor
	 */
	public FTGlyph(Shape glyph, float advanceX) //FT_LIB
	{
		if(glyph != null)
		{
			this.bBox = new FTBBox(glyph);
			this.glyph = glyph;
			this.advance = advanceX;// / 64.0f;
		}
	}


	/**
	 * Destructor.
	 */
	public void dispose()
	{
		if (this.gl != null && this.gl.glIsList(this.glList))
			this.gl.glDeleteLists(this.glList, 1);
	}

	/**
	 * Returns the gl context of this FTGlyph.
	 * @return The gl context of this FTGlyph.
	 */
	public GL getGL()
	{
		return this.gl;
	}

	/**
	 * Sets the gl context for this FTGlyph.
	 * @param gl The new gl context.
	 * @param glu The new glu context.
	 */
	public void setGLGLU(GL gl, GLU glu)
	{
		assert gl != null && glu != null : "GL and GLU shouldnt be null";
		this.gl  = gl;
		this.glu = glu;
		this.createDisplayList();
	}

	/**
	 * Returns the displaylist index of this FTGlyph.
	 * @return The displaylist index of this FTGlyph.
	 */
	public int getDisplayList()
	{
		return this.glList;
	}

	/** Derived glyphs have to implement this method. This method is called, when
	 * the GL and GLU context is set.
	 */
	protected abstract void createDisplayList();

	/**
	 * Renders this glyph at the current pen position.
	 * 
	 * @param x The current pen position's x component.
	 * @param y The current pen position's y component.
	 * @param z The current pen position's z z component.
	 * @return The advance distance for this glyph.
	 */
	public abstract float render(final float x, final float y, final float z);

	/**
	 * Return the advance width for this glyph.
	 * 
	 * @return advance width.
	 */
	public float advance()
	{
		return this.advance;
	}

	/**
	 * Return the bounding box for this glyph.
	 * 
	 * @return bounding box.
	 */
	public final FTBBox getBBox()
	{
		return this.bBox;
	}

	/**
	 * Queries for errors.
	 * 
	 * @return The current error code.
	 */
	public final int error()
	{
		return this.err;
	}

}