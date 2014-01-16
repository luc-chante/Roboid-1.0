package univ.avignon.roboid10.view.video;

import univ.avignon.roboid10.view.video.VideoStreamAsyncClient.OnNextFrameStreamedListener;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.AsyncTask.Status;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class VideoStreamView extends SurfaceView implements
		VideoStreamController, Runnable,
		OnNextFrameStreamedListener,
		SurfaceHolder.Callback {

	private Object mLocker = new Object();

	private static final int STATE_IDLE = 0;
	private static final int STATE_START = 1;

	private int mTargetState = STATE_IDLE;

	private VideoStreamAsyncClient mClient;
	private String mPath;
	private Thread mPaintingThread;

	private volatile Bitmap mNextFrame = null;
	private int mWidth;
	private int mHeight;

	public VideoStreamView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public VideoStreamView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public VideoStreamView(Context context) {
		super(context);
		init();
	}

	private void init() {
		getHolder().addCallback(this);
	}

	private synchronized void _start() {
		if (mClient == null) {
			mClient = new VideoStreamAsyncClient(this);
		}
		if (mPaintingThread == null) {
			mPaintingThread = new Thread(this);
			mPaintingThread.setPriority(Thread.MIN_PRIORITY);
		}
		if (mTargetState == STATE_START
				&& mClient.getStatus() == Status.PENDING && mPath != null) {
			mClient.execute(mPath);
			mTargetState = STATE_IDLE;
			mPaintingThread.start();
		}
	}

	private synchronized void _stop() {
		if (mClient != null) {
			mClient.cancel(true);
			mClient = null;
			mTargetState = STATE_IDLE;
		}
		if (mPaintingThread != null) {
			mPaintingThread.interrupt();
			mPaintingThread = null;
		}
	}

	@Override
	public void setStreamPath(String path) {
		mPath = path;
		_start();
	}

	@Override
	public void start() {
		mTargetState = STATE_START;
		_start();
	}

	@Override
	public void stop() {
		_stop();
	}

	@Override
	public void onNextFrameStreamed(Bitmap bitmap) {
		Bitmap previous;
		synchronized (mLocker) {
			previous = mNextFrame;
			mNextFrame = bitmap;
		}
		if (previous != null) {
			previous.recycle();
		}
	}

	@Override
	public void run() {
		Bitmap frame;

		Paint paint = new Paint();

		while (true) {
			synchronized (mLocker) {
				while (mNextFrame == null) {
					try {
						mLocker.wait(50);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				frame = mNextFrame;
				mNextFrame = null;
			}

			final SurfaceHolder holder = getHolder();
			final Canvas canvas = holder.lockCanvas();
			final int sWidth = frame.getWidth();
			final int sHeight = frame.getHeight();

			float ratio = Math.min((float) mWidth / (float) sWidth,
					(float) mHeight / (float) sHeight);
			final Matrix matrix = new Matrix();
			matrix.postScale(ratio, ratio);
			matrix.postTranslate(Math.max(0f, (mWidth - sWidth * ratio) / 2f),
					Math.max(0f, (mHeight - sHeight * ratio) / 2f));

			canvas.drawBitmap(frame, matrix, paint);
			holder.unlockCanvasAndPost(canvas);
			paint.reset();
			frame.recycle();
		}
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		_start();
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		mWidth = width;
		mHeight = height;
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		_stop();
	}
}
