package univ.avignon.roboid10;

import java.net.InetAddress;
import java.net.UnknownHostException;

import univ.avignon.roboid10.view.remote.ClassicControllerBehavior;
import univ.avignon.roboid10.view.remote.JoystickView;
import univ.avignon.roboid10.view.video.VideoStreamController;
import android.app.Activity;
import android.os.Bundle;
import android.view.ViewStub;

public class RemoteControlActivity extends Activity {

	public static final String ROBOID_IP = "10.0.0.1";
	public static final int ROBOID_COMMAND_PORT = 8080;
	public static final int ROBOID_STREAM_PORT = 8081;

	VideoStreamController mVideoStream;
	JoystickView mLeftJoystick, mRightJoystick;
	RoboidCrontrol mController;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_remote_control);

		try {
			init(InetAddress.getByName(ROBOID_IP));
		} catch (UnknownHostException e) {
			e.printStackTrace();
			finish();
		}
	}

	private void init(InetAddress roboidAddr) {

		mController = new RoboidCrontrol(roboidAddr, ROBOID_COMMAND_PORT);

		ViewStub stub = (ViewStub) findViewById(R.id.viewStubModelRC);
		ClassicControllerBehavior convertor = (ClassicControllerBehavior) stub
				.inflate();
		mController.setControllerBehavior(convertor, convertor);

		mVideoStream = (VideoStreamController) findViewById(R.id.stream);
		mVideoStream.setStreamPath("http://" + ROBOID_IP + ":"
				+ ROBOID_STREAM_PORT);
	}

	@Override
	protected void onResume() {
		super.onResume();
		new Thread(new Runnable() {
			@Override
			public void run() {
				mVideoStream.start();
				mController.connect();
			}
		});
	}

	@Override
	protected void onPause() {
		super.onPause();
		mVideoStream.stop();
		mController.close();
	}
}
