package univ.avignon.roboid10;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;

import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

public class RoboidCrontrol implements View.OnTouchListener {

	private static final byte CMD_SET_LEFT_SPEED = 0x01;
	private static final byte CMD_SET_RIGHT_SPEED = 0x02;
	private static final byte CMD_SET_ACCELERATION = 0x04;

	public static final byte ACCELERATION_DEFAULT = 0x01;
	public static final byte ACCELERATION_SPORT = 0x02;
	public static final byte ACCELERATION_SMOOTH = 0x04;

	private Context mContext;
	AtomicBoolean mConnecting = new AtomicBoolean(false);

	/**
	 * IP address of the "Roboïd-1.0" accespoint.
	 */
	private final InetAddress mDestAddr;

	/**
	 * Port number used by the socket.
	 */
	private final int mDestPort;

	/**
	 * The socket used for the communication.
	 * 
	 * It should be closed when the application goes to background or quit and
	 * then recreate when needed.
	 */
	Socket mSocket;

	/**
	 * How this controller will interpret touch events.
	 */
	private ControllerBehavior mControllerBehavior;

	/**
	 * Current acceleration mode.
	 * 
	 * @see #setAcceleration()
	 */
	private byte mAcceleration;

	/**
	 * The array of bytes to send as a command.
	 */
	private byte[] mCommand = new byte[2];

	/**
	 * Constructor
	 * 
	 * @param destAddr
	 *            The IP adrress of Roboïd-1.0
	 * @param port
	 *            The port use to manipulate Roboïd-1.0
	 */
	public RoboidCrontrol(Context context, InetAddress destAddr, int port) {
		mContext = context;
		mDestAddr = destAddr;
		mDestPort = port;
		mAcceleration = ACCELERATION_DEFAULT;
	}

	/**
	 * @return
	 */
	public String getIpAddr() {
		return mDestAddr.getHostAddress();
	}

	/**
	 * Try to establish a connection to "Roboïd-1.0".
	 * 
	 * @return <code>true</code> if the socket is successfully open,
	 *         <code>false</code> otherwise.
	 */
	public synchronized void connect() {
		if (mSocket == null && mConnecting.compareAndSet(false, true)) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						mSocket = new Socket(mDestAddr, mDestPort);
					} catch (IOException e) {
						e.printStackTrace();
						mConnecting.set(false);
					}
				}
			}).start();
		}
		mConnecting.set(false);
	}

	/**
	 * Close the current socket connection.
	 */
	public synchronized void close() {
		if (mSocket != null) {
			try {
				mSocket.close();
				mSocket = null;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Define a new behavior for this controller.
	 * 
	 * @param handler
	 *            The new touch event handler
	 */
	public void setControllerBehavior(View joystick, ControllerBehavior handler) {
		mControllerBehavior = handler;
		joystick.setOnTouchListener(this);
	}

	/**
	 * Change the acceleration mode.
	 * 
	 * The given acceleration mode should be one of the
	 * RoboidCrontrol#ACCELERATION_XXX constants.
	 * <ul>
	 * <li>{@link RoboidCrontrol#ACCELERATION_DEFAULT}: it is quick progressive
	 * acceleration (aka exponential).</li>
	 * <li>{@link RoboidCrontrol#ACCELERATION_SPORT}: it is an on/off
	 * acceleration.</li>
	 * <li>{@link RoboidCrontrol#ACCELERATION_SMOOTH}: it is a very smooth
	 * progressive acceleration (aka natural logarithm).</li>
	 * </ul>
	 * 
	 * @param acceleration
	 */
	public void setAccelerationMode(byte acceleration) {
		if (acceleration != mAcceleration) {
			try {
				switch (acceleration) {
				case ACCELERATION_DEFAULT:
					mAcceleration = ACCELERATION_DEFAULT;
					sendCmd(CMD_SET_ACCELERATION, ACCELERATION_DEFAULT);
					break;
				case ACCELERATION_SPORT:
					mAcceleration = ACCELERATION_SPORT;
					sendCmd(CMD_SET_ACCELERATION, ACCELERATION_SPORT);
					break;
				case ACCELERATION_SMOOTH:
					mAcceleration = ACCELERATION_SMOOTH;
					sendCmd(CMD_SET_ACCELERATION, ACCELERATION_SMOOTH);
					break;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Set the speed which left engines have to each according to the selected
	 * acceleration mode.
	 * 
	 * @param speed
	 *            The new speed
	 */
	public void setLeftEnginesSpeed(byte speed) {
		try {
			sendCmd(CMD_SET_LEFT_SPEED, speed);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Set the speed which right engines have to each according to the selected
	 * acceleration mode.
	 * 
	 * @param speed
	 *            The new speed
	 */
	public void setRightEnginesSpeed(byte speed) {
		try {
			sendCmd(CMD_SET_RIGHT_SPEED, speed);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Sends a command throw the opened socket to the robot.
	 * 
	 * @param cmd
	 *            The command to send.
	 * @param value
	 *            The value of the command the pass
	 * @throws IOException
	 */
	void sendCmd(byte cmd, byte value) throws IOException {
		if (mSocket == null) {
			Toast.makeText(mContext, "En attente de connexion",
					Toast.LENGTH_SHORT).show();
			connect();
		} else {
			synchronized (this) {
				mCommand[0] = cmd;
				mCommand[1] = value;
				mSocket.getOutputStream().write(mCommand, 0, 2);
				mSocket.getOutputStream().flush();
				mCommand[0] = 0x00;
				mCommand[1] = 0x00;
			}
		}
	}

	@Override
	public boolean onTouch(View joystick, MotionEvent event) {
		if (mControllerBehavior != null) {
			mControllerBehavior.handleTouchEvent(this, joystick.getId(), event);
			return true;
		}
		return false;
	}

	/**
	 *
	 */
	public interface ControllerBehavior {
		/**
		 * 
		 * @param controller
		 * @param joystickId
		 * @param event
		 */
		void handleTouchEvent(RoboidCrontrol controller, int joystickId,
				MotionEvent event);
	}
}
