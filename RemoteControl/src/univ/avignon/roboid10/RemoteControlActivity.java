package univ.avignon.roboid10;

import java.net.InetAddress;
import java.net.UnknownHostException;

import univ.avignon.roboid10.view.remote.JoystickView;
import univ.avignon.roboid10.view.video.VideoStreamController;
import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

public class RemoteControlActivity extends Activity implements
		View.OnTouchListener {

	VideoStreamController mVideoStream;
	JoystickView mLeftJoystick, mRightJoystick;
	RoboidCrontrol mController;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_remote_control);

		try {
			mController = new RoboidCrontrol(InetAddress.getByName("10.0.0.1"),
					8081);
			init();
		} catch (UnknownHostException e) {
			e.printStackTrace();
			finish();
		}
	}

	private void init() {

		mLeftJoystick = (JoystickView) findViewById(R.id.leftJoystick);
		mLeftJoystick.setOnTouchListener(mController);

		mRightJoystick = (JoystickView) findViewById(R.id.rightJoystick);
		mRightJoystick.setOnTouchListener(mController);

		mVideoStream = (VideoStreamController) findViewById(R.id.stream);
		mVideoStream.setStreamPath("http://" + mController.getIpAddr()
				+ ":8081");
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

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		return false;
	}
}
