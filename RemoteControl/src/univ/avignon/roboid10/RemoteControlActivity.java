package univ.avignon.roboid10;

import java.net.InetAddress;
import java.net.UnknownHostException;

import univ.avignon.roboid10.RoboidCrontrol.ControllerBehavior;
import univ.avignon.roboid10.view.remote.JoystickView;
import univ.avignon.roboid10.view.video.VideoStreamController;
import android.app.Activity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewStub;
import android.widget.TextView;
import android.widget.Toast;

public class RemoteControlActivity extends Activity implements
		RoboidCrontrol.ConnectionListener {

	VideoStreamController mVideoStream;
	JoystickView mLeftJoystick, mRightJoystick;
	RoboidCrontrol mController;
	View currentConvertor;
	TextView txt_vitesse;

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
		mVideoStream = (VideoStreamController) findViewById(R.id.stream);
		registerForContextMenu(((View) mVideoStream));
		mVideoStream.setStreamPath(RoboidCrontrol.getFullStreamUrl());

		mController = new RoboidCrontrol(roboidAddr,
				RoboidCrontrol.ROBOID_COMMAND_PORT);
	}

	@Override
	protected void onStart() {
		super.onStart();
		mController.connect(this);
	}

	@Override
	protected void onStop() {
		super.onStop();
		mVideoStream.stop();
		mController.close();
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		getMenuInflater().inflate(R.menu.main, menu);

	}

	private void setController(int viewId, int viewStubId) {
		View view;

		if (currentConvertor != null) {
			currentConvertor.setVisibility(View.GONE);
		}

		view = findViewById(viewId);
		if (view == null) {
			view = ((ViewStub) findViewById(viewStubId)).inflate();
		}
		if (currentConvertor != view) {
			view.setVisibility(View.VISIBLE);
			mController.setControllerBehavior(view, (ControllerBehavior) view);
			currentConvertor = view;
		}
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.action_mode1) {
			setController(R.id.view_classic_controller, R.id.viewStubModelRC);
			return true;
		} else if (item.getItemId() == R.id.action_mode2) {
			setController(R.id.view_caterpillar_controller,
					R.id.viewStubCaterpillar);
			return true;
		}

		return super.onContextItemSelected(item);
	}

	@Override
	public void OnConnectionOpened() {
		setController(R.id.view_classic_controller, R.id.viewStubModelRC);
		mVideoStream.start();
	}

	@Override
	public void OnConnectionFailed() {
		Toast.makeText(this, "Connexion avec Roboïd-1.0 impossible", Toast.LENGTH_LONG).show();
		finish();
	}

}
