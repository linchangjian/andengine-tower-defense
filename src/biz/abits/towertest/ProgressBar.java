package biz.abits.towertest;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.camera.hud.HUD;
import org.andengine.entity.primitive.Line;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

/**
 * @author Andrew Binning, Jared Meadows
 * 
 */
public class ProgressBar extends Rectangle {
	// ===========================================================
	// Constants
	// ===========================================================
	private static final float FRAME_LINE_WIDTH = 5f;
	// ===========================================================
	// Fields
	// ===========================================================
	private final Line[] mFrameLines = new Line[4];
	//private final Rectangle mBackgroundRectangle;
	private final Rectangle mProgressRectangle;

	private float mPixelsPerPercentRatio;
	private float mMaxValue;

	// ===========================================================
	// Constructors
	// ===========================================================
	public ProgressBar(final float pX, final float pY, final float pWidth, final float pHeight, float pMaxValue, VertexBufferObjectManager tvbom) {
		super(pX, pY, pWidth, pHeight, tvbom);
		this.mProgressRectangle = new Rectangle(0, 0, pWidth, pHeight, tvbom);
		this.mMaxValue = pMaxValue;
		this.mPixelsPerPercentRatio = pWidth / 100;
		this.mFrameLines[0] = new Line(0, 0, 0 + pWidth, 0, FRAME_LINE_WIDTH, tvbom); // Top line.
		this.mFrameLines[1] = new Line(0 + pWidth, 0, 0 + pWidth, 0 + pHeight, FRAME_LINE_WIDTH, tvbom); // Right line.
		this.mFrameLines[2] = new Line(0 + pWidth, 0 + pHeight, 0, 0 + pHeight, FRAME_LINE_WIDTH, tvbom); // Bottom line.
		this.mFrameLines[3] = new Line(0, 0 + pHeight, 0, 0, FRAME_LINE_WIDTH, tvbom); // Left line.
		this.attachChild(this.mProgressRectangle); // The progress is drawn afterwards.
		for (int i = 0; i < this.mFrameLines.length; i++)
			this.attachChild(this.mFrameLines[i]); // Lines are drawn last, so they'll override everything.
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================
	public ProgressBar setBackColor(final float pRed, final float pGreen, final float pBlue, final float pAlpha) {
		this.setColor(pRed, pGreen, pBlue, pAlpha);
		return this;
	}

	public ProgressBar setFrameColor(final float pRed, final float pGreen, final float pBlue, final float pAlpha) {
		for (int i = 0; i < this.mFrameLines.length; i++)
			this.mFrameLines[i].setColor(pRed, pGreen, pBlue, pAlpha);
		return this;
	}

	public ProgressBar setProgressColor(final float pRed, final float pGreen, final float pBlue, final float pAlpha) {
		this.mProgressRectangle.setColor(pRed, pGreen, pBlue, pAlpha);
		return this;
	}

	/**
	 * Set the current progress of this progress bar.
	 * 
	 * @param pProgress is <b> BETWEEN </b> 0 - mMaxValue.
	 */
	public ProgressBar setProgress(float pProgress) {
		if (pProgress < 0)
			this.mProgressRectangle.setWidth(0);
		else if (pProgress > mMaxValue) //set it to the minimum
			this.mProgressRectangle.setWidth(this.getWidth() * 1); //set it to the maximum
		else
			this.mProgressRectangle.setWidth(this.getWidth() * pProgress / mMaxValue);
		
		return this;
	}
	
	/**
	 * used to adjust the maximum value of the progressbar, it's initial value is set in the constructor though!
	 * @param pMax is the new maximum value
	 * @return the ProgressBar
	 */
	public ProgressBar setMax(final float pMax) {
		this.mMaxValue = pMax;
		return this;
	}

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	// ===========================================================
	// Methods
	// ===========================================================
	// example use object.setWidth(float original_width, float
	// intended_capacity);
	public ProgressBar setWidth(float width1, float width2) {
		this.mPixelsPerPercentRatio = width1 / width2;
		return this;
	}
	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================

}