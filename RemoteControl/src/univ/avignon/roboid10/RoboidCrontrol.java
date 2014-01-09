package univ.avignon.roboid10;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

import univ.avignon.roboid10.view.remote.JoystickView;
import android.view.MotionEvent;

public class RoboidCrontrol implements JoystickView.OnTouchListener {

	private static final byte CMD_SET_SPEED = 0x01;

	private static final int ACCELERATION_MASK = 0x07;
	public static final int ACCELERATION_DEFAULT = 0x01;
	public static final int ACCELERATION_SPORT = 0x02;
	public static final int ACCELERATION_SMOOTH = 0x04;

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
	private Socket mSocket;

	/**
	 * Current acceleration mode.
	 *
	 * @see #setAcceleration()
	 */
	private int mAcceleration;

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
	 *
	 * @return <code>true</code> if the socket is successfully open,
	 *         <code>false</code> otherwise.
	 */
	public boolean connect() {
		if (mSocket == null) {
			try {
				mSocket = new Socket(mDestAddr, mDestPort);
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		}
		return true;
	}

	/**
	 * Close the current socket connection.
	 */
	public void close() {
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
	public void setAcceleration(int acceleration) {
		acceleration = acceleration & ACCELERATION_MASK;
		if (acceleration != mAcceleration) {
			switch (acceleration) {
				case ACCELERATION_DEFAULT:
					mAcceleration = ACCELERATION_DEFAULT;
					// sendCmd(am, 0);
					break;
				case ACCELERATION_SPORT:
					mAcceleration = ACCELERATION_DEFAULT;
					// sendCmd(am, 1);
					break;
				case ACCELERATION_SMOOTH:
					mAcceleration = ACCELERATION_DEFAULT;
					// sendCmd(am, 2);
					break;
			}
		}
	}

	/**
	 * Sends a command throw the opened socket to the robot.
	 *
	 * @param cmd
	 *            The command to send.
	 * @param value
	 *            The value of the command the pass
	 */
	private void sendCmd(byte cmd, byte value) {
		// mSocket.getOutputStream()
	}

	@Override
	public void onTouch(JoystickView v, MotionEvent event) {

	}
}
