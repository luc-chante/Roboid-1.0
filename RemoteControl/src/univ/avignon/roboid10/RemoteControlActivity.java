package univ.avignon.roboid10;

import java.net.InetAddress;
import java.net.UnknownHostException;

import univ.avignon.roboid10.view.remote.ClassicControllerBehavior;
import univ.avignon.roboid10.view.remote.JoystickView;
import univ.avignon.roboid10.view.video.VideoStreamController;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewStub;

public class RemoteControlActivity extends Activity {

	VideoStreamController mVideoStream;
	JoystickView mLeftJoystick, mRightJoystick;
	RoboidCrontrol mController;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_remote_control);

		try {
			init(InetAddress.getByName(RoboidCrontrol.ROBOID_IP));
		} catch (UnknownHostException e) {
			e.printStackTrace();
			finish();
		}
	}

	private void init(InetAddress roboidAddr) {
		ViewStub stub = (ViewStub) findViewById(R.id.viewStubModelRC);
		ClassicControllerBehavior convertor = (ClassicControllerBehavior) stub
				.inflate();

		mController = new RoboidCrontrol(this, roboidAddr, RoboidCrontrol.ROBOID_COMMAND_PORT);
		mController.setControllerBehavior(convertor, convertor);

		mVideoStream = (VideoStreamController) findViewById(R.id.stream);
		mVideoStream.setStreamPath(RoboidCrontrol.getFullStreamUrl());
	}

	@Override
	protected void onResume() {
		super.onResume();
		Log.v("TEST", "onResume()");

		mVideoStream.start();
		Log.v("TEST", "mVideoStream started");

		mController.connect();
		Log.v("TEST", "mController connected");
	}

	@Override
	protected void onPause() {
		super.onPause();
		Log.v("TEST", "onPause()");
		
		mVideoStream.stop();
		Log.v("TEST", "mVideoStream stoped");

		mController.close();
		Log.v("TEST", "mController closed");
	}
}
