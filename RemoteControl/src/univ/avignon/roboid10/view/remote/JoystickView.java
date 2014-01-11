package univ.avignon.roboid10.view.remote;

import java.util.ArrayList;

import univ.avignon.roboid10.R;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class JoystickView extends View {

	private OnTouchListener mOnTouchListener = null;

	private int mCalculatedSize;
	private float[] mLargeLines;
	private float[] mThinLines;
	private ArrayList<Float> mLarges = new ArrayList<Float>();
	private ArrayList<Float> mThins = new ArrayList<Float>();

	public static final int AXES_VERTICAL = 0x01;
	public static final int AXES_HORIZONTAL = 0x02;
	private static final int AXES_BOTH = AXES_VERTICAL | AXES_HORIZONTAL;

	private int mAxes;
	private boolean mAutoRecentrate;
	private int mColor;

	private float mCenterX;
	private float mCenterY;
	private float mOuterRadius;

	private float mTouchX;
	private float mTouchY;
	private float mInnerRadius;
	private float mInnerMaxRadius;

	private Paint mCirclePaint;
	private Paint mLinePaint;

	public JoystickView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);

		TypedArray a = context.obtainStyledAttributes(attrs,
				R.styleable.JoystickView, defStyleAttr, 0);

		if (!a.hasValue(R.styleable.JoystickView_radius)) {
			throw new IllegalStateException(
					"JoystickView should have at least a radius value");
		}

		mAxes = a.getInt(R.styleable.JoystickView_axes, AXES_BOTH);
		mAutoRecentrate = a.getBoolean(R.styleable.JoystickView_autoRecentrate,
				true);
		mColor = a.getColor(R.styleable.JoystickView_color, Color.WHITE);
		mOuterRadius = a.getDimension(R.styleable.JoystickView_radius, 0f);
		mInnerRadius = a.getDimension(R.styleable.JoystickView_innerRadius,
				mOuterRadius / 5f);
		a.recycle();

		if (mOuterRadius < 10f || mInnerRadius >= mOuterRadius) {
			throw new RuntimeException(
					"JoystickView should have a radius greater than or equal to 10 pixels and strictly greater than the inner radius.");
		}

		mInnerMaxRadius = mOuterRadius - mInnerRadius;
		mCalculatedSize = (int) Math.ceil(mOuterRadius * 2);

		init();
	}

	public JoystickView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	@Override
	public void setOnTouchListener(OnTouchListener l) {
		mOnTouchListener = l;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		setMeasuredDimension(getMeasure(widthMeasureSpec),
				getMeasure(heightMeasureSpec));

		mTouchX = mCenterX = getMeasuredWidth() / 2f;
		mTouchY = mCenterY = getMeasuredHeight() / 2f;
	}

	@SuppressLint("DrawAllocation")
	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {

		final float width = right - left;
		final float centerX = width / 2f;
		final float height = bottom - top;
		final float centerY = height / 2f;

		mLarges.clear();
		mThins.clear();
		if ((mAxes & AXES_VERTICAL) == AXES_VERTICAL) {
			mLarges.add(centerX);
			mLarges.add(0f);
			mLarges.add(centerX);
			mLarges.add(height);
			for (int q = -4; q <= 4; q += 2) {
				float y = q / 4f * mInnerMaxRadius + centerY;
				mLarges.add(14f * centerX / 15f);
				mLarges.add(y);
				mLarges.add(16f * centerX / 15f);
				mLarges.add(y);
			}
			for (int q = -3; q <= 4; q += 2) {
				float y = q / 4f * mInnerMaxRadius + centerY;
				mThins.add(19f * centerX / 20f);
				mThins.add(y);
				mThins.add(21f * centerX / 20f);
				mThins.add(y);
			}
		} else {
			mThins.add(centerX);
			mThins.add(3f * centerY / 4f);
			mThins.add(centerX);
			mThins.add(5f * centerY / 4f);
		}
		if ((mAxes & AXES_HORIZONTAL) == AXES_HORIZONTAL) {
			mLarges.add(0f);
			mLarges.add(centerY);
			mLarges.add(width);
			mLarges.add(centerY);
			for (int q = -4; q <= 4; q += 2) {
				float x = q / 4f * mInnerMaxRadius + centerX;
				mLarges.add(x);
				mLarges.add(14f * centerY / 15f);
				mLarges.add(x);
				mLarges.add(16f * centerY / 15f);
			}
			for (int q = -3; q <= 4; q += 2) {
				float x = q / 4f * mInnerMaxRadius + centerX;
				mThins.add(x);
				mThins.add(19f * centerY / 20f);
				mThins.add(x);
				mThins.add(21f * centerY / 20f);
			}
		} else {
			mThins.add(3f * centerX / 4f);
			mThins.add(centerY);
			mThins.add(5f * centerX / 4f);
			mThins.add(centerY);
		}

		mLargeLines = new float[mLarges.size()];
		for (int i = mLarges.size() - 1; i >= 0; i--) {
			mLargeLines[i] = mLarges.get(i);
		}
		mThinLines = new float[mThins.size()];
		for (int i = mThins.size() - 1; i >= 0; i--) {
			mThinLines[i] = mThins.get(i);
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		MotionEvent boundEvent = null;
		final int action = event.getAction();

		if (action == MotionEvent.ACTION_UP && mAutoRecentrate) {
			mTouchX = mCenterX;
			mTouchY = mCenterY;
			invalidate();
			boundEvent = MotionEvent.obtain(event);
			boundEvent.setLocation(0f, 0f);
		}
		if (action == MotionEvent.ACTION_MOVE) {
			float x = mCenterX, dx = 0f, y = mCenterY, dy = 0f;

			if ((mAxes & AXES_HORIZONTAL) == AXES_HORIZONTAL) {
				x = event.getX();
				dx = x - mCenterX;
			}
			if ((mAxes & AXES_VERTICAL) == AXES_VERTICAL) {
				y = event.getY();
				dy = y - mCenterY;
			}

			float d = (float) Math.sqrt((dx * dx) + (dy * dy));

			if (d <= mInnerMaxRadius) {
				mTouchX = x;
				mTouchY = y;
			} else {
				mTouchX = dx / d * mInnerMaxRadius + mCenterX;
				mTouchY = dy / d * mInnerMaxRadius + mCenterY;
			}
			invalidate();
			boundEvent = MotionEvent.obtainNoHistory(event);
			boundEvent.setLocation((mTouchX - mCenterX) / mInnerMaxRadius,
					(mCenterY - mTouchY) / mInnerMaxRadius);
		}

		if (boundEvent != null) {
			if (mOnTouchListener != null) {
				mOnTouchListener.onTouch(this, boundEvent);
			}
			boundEvent.recycle();
		}
		return true;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		mCirclePaint.setAlpha(80);
		// canvas.drawCircle(mCenterX, mCenterY, mOuterRadius, mCirclePaint);
		mLinePaint.setStrokeWidth(4f);
		canvas.drawLines(mLargeLines, mLinePaint);
		mLinePaint.setStrokeWidth(2f);
		canvas.drawLines(mThinLines, mLinePaint);
		mCirclePaint.setAlpha(200);
		canvas.drawCircle(mTouchX, mTouchY, mInnerRadius, mCirclePaint);
	}

	private void init() {
		mCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mCirclePaint.setColor(mColor);

		mLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mLinePaint.setColor(mColor);
		mLinePaint.setAlpha(80);
		mLinePaint.setStyle(Paint.Style.STROKE);
	}

	private int getMeasure(int measureSpec) {
		final int mode = MeasureSpec.getMode(measureSpec);
		final int measure = MeasureSpec.getSize(measureSpec);
		switch (mode) {
			case MeasureSpec.AT_MOST:
				return Math.min(measure, mCalculatedSize);
			case MeasureSpec.EXACTLY:
				return measure;
			default:
				return mCalculatedSize;
		}
	}
}
