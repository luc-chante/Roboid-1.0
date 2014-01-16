package univ.avignon.roboid10.view.remote;

import univ.avignon.roboid10.R;
import univ.avignon.roboid10.RoboidCrontrol;
import univ.avignon.roboid10.RoboidCrontrol.ControllerBehavior;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.FrameLayout;

public class CaterpillarControllerBehavior extends FrameLayout implements
		ControllerBehavior {

	public CaterpillarControllerBehavior(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
	}

	public CaterpillarControllerBehavior(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public CaterpillarControllerBehavior(Context context) {
		super(context);
	}

	@Override
	public void setOnTouchListener(OnTouchListener l) {
		findViewById(R.id.leftJoystick).setOnTouchListener(l);
		findViewById(R.id.rightJoystick).setOnTouchListener(l);
	}

	@Override
	public void handleTouchEvent(RoboidCrontrol controller, int joystickId,
			MotionEvent event) {
		final byte speed = (byte) (Math.round(event.getY() * 100) & 0xFF);
		switch (joystickId) {
			case R.id.leftJoystick:
				controller.setLeftEnginesSpeed(speed);
				break;
			case R.id.rightJoystick:
				controller.setRightEnginesSpeed(speed);
				break;
			default:
				throw new IllegalArgumentException(joystickId
						+ " is not a valid Joystick ID");
		}
	}

}
