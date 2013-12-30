package univ.avignon.roboid10;

import univ.avignon.roboid10.view.VideoStreamController;
import android.app.Activity;
import android.os.Bundle;

public class RemoteControlActivity extends Activity {

	VideoStreamController mVideoStream;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_remote_control);

		mVideoStream = (VideoStreamController) findViewById(R.id.stream);
		mVideoStream.setStreamPath("http://192.168.0.116:8080/videofeed");
	}

	@Override
	protected void onResume() {
		super.onResume();
		mVideoStream.start();
	}

	@Override
	protected void onPause() {
		super.onPause();
		mVideoStream.stop();
	}
}
