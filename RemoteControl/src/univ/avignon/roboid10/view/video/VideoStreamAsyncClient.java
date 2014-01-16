package univ.avignon.roboid10.view.video;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

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
	byte[] mByteArray;

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
		mByteArray = new byte[4096];
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

	private Bitmap readBitmap(DataInputStream stream) throws IOException {
		int p = 0;
		int size;

		do {
			do {
				mByteArray[p] = (byte) stream.readUnsignedByte();
				p++;
				stream.mark(mByteArray.length - p);
			} while (mByteArray[p - 1] != (byte) 0xFF);
			mByteArray[p] = (byte) stream.readUnsignedByte();
			p++;
		} while (mByteArray[p - 1] != (byte) 0xD8);

		Properties props = new Properties();
		props.load(new ByteArrayInputStream(mByteArray, 0, p));
		size = Integer.parseInt(props.getProperty("Content-Length", "-1"));

		if (size > 0 && mByteArray.length < size) {
			mByteArray = new byte[size];
			mByteArray[0] = (byte) 0xFF;
			mByteArray[1] = (byte) 0xD8;
			stream.readFully(mByteArray, 2, size - 2);
			return BitmapFactory.decodeStream(new ByteArrayInputStream(mByteArray));
		}

		mByteArray[0] = (byte) 0xFF;
		mByteArray[1] = (byte) 0xD8;
		p = 2;

		do {
			do {
				if (p > (mByteArray.length - 2)) {
					return null;
				}
				mByteArray[p] = (byte) stream.readUnsignedByte();
				p++;
			} while (mByteArray[p - 1] != (byte) 0xFF);
			mByteArray[p] = (byte) stream.readUnsignedByte();
			p++;
		} while (mByteArray[p - 1] != (byte) 0xD9);

		return BitmapFactory.decodeStream(new ByteArrayInputStream(mByteArray, 0, p));
	}

	interface OnNextFrameStreamedListener {
		void onNextFrameStreamed(Bitmap bitmap);
	}
}
