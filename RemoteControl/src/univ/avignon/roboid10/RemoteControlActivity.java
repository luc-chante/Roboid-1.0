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

public class RemoteControlActivity extends Activity {

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
		
		mController = new RoboidCrontrol(this, roboidAddr, RoboidCrontrol.ROBOID_COMMAND_PORT);

		txt_vitesse = (TextView) findViewById(R.id.txt_vitesse);
	}

	@Override
	protected void onResume() {
		super.onResume();
		//mVideoStream.start();
		mController.connect();
	}

	@Override
	protected void onPause() {
		super.onPause();
		//mVideoStream.stop();
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
	
		view = findViewById(viewId);
		if (view == null) {
			view = ((ViewStub) findViewById(viewStubId)).inflate();
		}
		if (currentConvertor != view) {
			currentConvertor.setVisibility(View.GONE);
			view.setVisibility(View.VISIBLE);
			mController.setControllerBehavior(view, (ControllerBehavior) view);
			currentConvertor = view;
		}
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.action_mode1) {
			setController(R.id.view_model_rc_controller, R.id.viewStubModelRC);
			return true;
		} else if (item.getItemId() == R.id.action_mode2) {
			setController(R.id.view_caterpillar_controller, R.id.viewStubCaterpillar);
			return true;
		}

		return super.onContextItemSelected(item);
	}

}
