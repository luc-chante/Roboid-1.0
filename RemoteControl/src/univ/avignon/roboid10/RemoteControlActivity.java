package univ.avignon.roboid10;

import java.net.InetAddress;
import java.net.UnknownHostException;
import univ.avignon.roboid10.view.remote.ClassicControllerBehavior;
import univ.avignon.roboid10.view.remote.JoystickView;
import univ.avignon.roboid10.view.video.VideoStreamController;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipData.Item;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.util.Log;
import android.view.ViewStub;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.Toast;

public class RemoteControlActivity extends Activity {

	VideoStreamController mVideoStream;
	JoystickView mLeftJoystick, mRightJoystick;
	RoboidCrontrol mController;
	View currentStub;
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
		currentStub = convertor;

		mController = new RoboidCrontrol(this, roboidAddr, RoboidCrontrol.ROBOID_COMMAND_PORT);
		mController.setControllerBehavior(convertor, convertor);
		
		mVideoStream = (VideoStreamController) findViewById(R.id.stream);
		registerForContextMenu(((View)mVideoStream));
		mVideoStream.setStreamPath(RoboidCrontrol.getFullStreamUrl());
	}

	@Override
	protected void onResume() {
		super.onResume();
		//mVideoStream.start();
		//mController.connect();
	}

	@Override
	protected void onPause() {
		super.onPause();
		//mVideoStream.stop();
		//mController.close();
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		// TODO Stub de la méthode généré automatiquement
		super.onCreateContextMenu(menu, v, menuInfo);
		getMenuInflater().inflate(R.menu.main, menu);
		
	}
	
	@SuppressLint("NewApi") @Override
	public boolean onContextItemSelected(MenuItem item) {
		// TODO Stub de la méthode généré automatiquement
		
		
		if( item.getItemId() == R.id.action_mode1){
			
			ViewStub stub = (ViewStub) findViewById(R.id.viewStubModelRC);
			
			if(currentStub.isInLayout()){
			
				View view =  stub.inflate();
				currentStub = stub;
				}
			if(	true){
				currentStub.setVisibility(View.GONE);	
			}
	
			
			
		}
		else if(item.getItemId() == R.id.action_mode2){
			Toast.makeText(getApplicationContext(), "mode 2", Toast.LENGTH_LONG).show();
			
			
			
			ViewStub stub = (ViewStub) findViewById(R.id.viewStubCaterpillar);
			
			if(currentStub.isAttachedToWindow()){
			currentStub.setVisibility(View.GONE);
			View view =  stub.inflate();
			currentStub = stub;
			}
			
		}
		
		
			
		return super.onContextItemSelected(item);
	}
	
}
