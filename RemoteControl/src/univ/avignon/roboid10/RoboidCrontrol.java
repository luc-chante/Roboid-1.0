package univ.avignon.roboid10;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;

import android.os.AsyncTask;
import android.view.MotionEvent;
import android.view.View;

public class RoboidCrontrol implements View.OnTouchListener {

	public static final String ROBOID_IP = "10.0.0.1";
	public static final int ROBOID_STREAM_PORT = 8081;
	public static final int ROBOID_COMMAND_PORT = 8082;

	public static String getFullStreamUrl() {
		return "http://" + ROBOID_IP + ":" + ROBOID_STREAM_PORT;
	}

	private static final byte CMD_SET_LEFT_SPEED = 0x01;
	private static final byte CMD_SET_RIGHT_SPEED = 0x02;
	private static final byte CMD_SET_ACCELERATION = 0x04;
	private static final byte CMD_CLOSE_CONNECTION = 0x08;

	public static final byte ACCELERATION_DEFAULT = 0x01;
	public static final byte ACCELERATION_SPORT = 0x02;
	public static final byte ACCELERATION_SMOOTH = 0x04;

	AtomicBoolean mConnected = new AtomicBoolean(false);

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
	public RoboidCrontrol(InetAddress destAddr, int port) {
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
	 */
	public synchronized void connect(final ConnectionListener listener) {
		if (mSocket == null && mConnected.compareAndSet(false, true)) {
			new AsyncTask<Void, Void, Boolean>() {
				@Override
				protected Boolean doInBackground(Void... params) {
					Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
					try {
						mSocket = new Socket(mDestAddr, mDestPort);
						return true;
					} catch (IOException e) {
						e.printStackTrace();
					}
					return false;
				}

				@Override
				protected void onPostExecute(Boolean result) {
					if (listener != null) {
						if (result) {
							listener.OnConnectionOpened();
						} else {
							listener.OnConnectionFailed();
						}
					}
					mConnected.set(false);
				}
			}.execute();
		}
	}

	/**
	 * Close the current socket connection.
	 */
	public synchronized void close() {
		if (mSocket != null) {
			try {
				sendCmd(CMD_CLOSE_CONNECTION, (byte) 0x00);
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
			connect(null);
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

	public interface ConnectionListener {
		void OnConnectionOpened();

		void OnConnectionFailed();
	}
}
