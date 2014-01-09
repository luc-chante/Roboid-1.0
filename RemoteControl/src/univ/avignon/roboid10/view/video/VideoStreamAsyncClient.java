package univ.avignon.roboid10.view.video;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;

class VideoStreamAsyncClient extends AsyncTask<String, Bitmap, Void> {

	AndroidHttpClient mClient;
	HttpUriRequest mRequest;
	ByteBuffer mBuffer;

	OnNextFrameStreamedListener mListener;

	public VideoStreamAsyncClient(OnNextFrameStreamedListener listener) {
		mListener = listener;
	}

	@Override
	protected void onCancelled() {
		super.onCancelled();

		if (mRequest != null) {
			mRequest.abort();
			mRequest = null;
		}
		if (mClient != null) {
			mClient.close();
			mClient = null;
		}
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();

		mClient = AndroidHttpClient.newInstance("(Roboïd-1.0)");
		mBuffer = ByteBuffer.allocate(40960);
	}

	@Override
	protected Void doInBackground(String... params) {
		DataInputStream stream;

		if (params.length == 0) {
			return null;
		}

		mRequest = new HttpGet(params[0]);
		try {
			HttpResponse response = mClient.execute(mRequest);
			final InputStream content = response.getEntity().getContent();
			stream = new DataInputStream(content);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}

		while (!this.isCancelled()) {
			try {
				publishProgress(readBitmap(stream));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		try {
			stream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	@Override
	protected void onProgressUpdate(Bitmap... values) {
		super.onProgressUpdate(values);
		mListener.onNextFrameStreamed(values[0]);
	}

	private byte read(DataInputStream is) throws IOException {
		byte b = (byte) is.readUnsignedByte();
		mBuffer.put(b);
		return b;
	}

	private Bitmap readBitmap(DataInputStream stream) throws IOException {
		byte b;
		byte[] bitmap;
		int size;

		bitmap = null;
		size = 0;
		do {
			if (read(stream) == (byte) 0xFF) {
				b = read(stream);
				if (b == (byte) 0xD8) {
					mBuffer.rewind();
					mBuffer.put((byte) 0xFF);
					mBuffer.put((byte) 0xD8);
				} else if (b == (byte) 0xD9) {
					bitmap = mBuffer.array();
					size = mBuffer.position();
					mBuffer.rewind();
				}
			}
		} while (bitmap == null);

		return BitmapFactory.decodeStream(new ByteArrayInputStream(bitmap, 0,
				size));
	}

	interface OnNextFrameStreamedListener {
		void onNextFrameStreamed(Bitmap bitmap);
	}
}
