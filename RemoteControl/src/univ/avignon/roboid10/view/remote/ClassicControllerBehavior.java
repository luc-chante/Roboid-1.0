package univ.avignon.roboid10.view.remote;

import univ.avignon.roboid10.R;
import univ.avignon.roboid10.RoboidCrontrol;
import univ.avignon.roboid10.RoboidCrontrol.ControllerBehavior;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.FrameLayout;

public class ClassicControllerBehavior extends FrameLayout implements
		ControllerBehavior {

	private int mCurrentSpeed = 0;
	private int mCurrentAngle = 0;

	public ClassicControllerBehavior(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
	}

	public ClassicControllerBehavior(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ClassicControllerBehavior(Context context) {
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
		switch (joystickId) {
			case R.id.leftJoystick:
				if (mCurrentSpeed == Math.round(event.getY() * 100)) {
					return;
				}
				mCurrentSpeed = Math.round(event.getY() * 100);
				break;
			case R.id.rightJoystick:
				if (mCurrentAngle == Math.round(event.getX() * 100) || mCurrentSpeed == 0) {
					return;
				}
				mCurrentAngle = Math.round(event.getX() * 100);
				break;
			default:
				throw new IllegalArgumentException(joystickId
						+ " is not a valid Joystick ID");
		}
		controller.setLeftEnginesSpeed(getLeftSeed());
		controller.setRightEnginesSpeed(getRightSeed());
	}

	private byte getLeftSeed() {
		float speed = mCurrentAngle < 0 ? 100 + mCurrentAngle : 100;
		return (byte) (Math.round(speed * mCurrentSpeed / 100f) & 0xFF);
	}

	private byte getRightSeed() {
		float speed = mCurrentAngle > 0 ? 100 - mCurrentAngle : 100;
		return (byte) (Math.round(speed * mCurrentSpeed / 100f) & 0xFF);
	}

}
