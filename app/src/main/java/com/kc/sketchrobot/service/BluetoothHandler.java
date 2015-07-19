package com.kc.sketchrobot.service;

import java.lang.ref.WeakReference;

import android.app.Service;
import android.bluetooth.BluetoothDevice;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.kc.sketchrobot.activity.MainActivity;
import com.kc.sketchrobot.vehiclectrl.cmd.GoForwardCommand;
import com.kc.sketchrobot.vehiclectrl.cmd.IBtDeviceManager;
import com.kc.sketchrobot.vehiclectrl.cmd.IMoveActionSender;
import com.kc.sketchrobot.vehiclectrl.cmd.TurnCommand;
import com.kc.sketchrobot.vehiclectrl.driver.IVehicleStatusListener;
import com.kc.sketchrobot.vehiclectrl.status.VehicleStatus;

public class BluetoothHandler extends Service implements IMoveActionSender,
		IBtDeviceManager {

	private static final int GO_CMD = 1;
	private static final int TURN_LEFT_CMD = 2;
	private static final int TURN_RIGHT_CMD = 3;
	


	private IVehicleStatusListener vehicleStatusListener;

	public BluetoothHandler() {

	}

	private ChannelValues channelValues = new ChannelValues();
	private static BluetoothService mBluetooth;

	String tag = BluetoothHandler.class.getSimpleName();
	Binder mBinder = new LocalBinder();
	BluetoothServiceConnection mBluetoothServiceConnection;

	private void initBluetoothServie() {

		mBluetoothServiceConnection = new BluetoothServiceConnection();

		boolean ret = bindService(new Intent(BluetoothHandler.this,
				BluetoothService.class), mBluetoothServiceConnection,
				Context.BIND_AUTO_CREATE);

		Log.i(tag, "initService() bound value: " + ret);

	}

	private String mLastValues = "";

	public void setValue(int channelId, int value) {
		channelValues.setValue(channelId, value);
		Log.d(tag, channelValues.toString());

		// if (mBluetooth.getState() != BluetoothService.STATE_CONNECTED) {
		String values = channelValues.toString();
		if (!mLastValues.equals(values)) {
			Log.d(tag, "Send to bluetooth");
			mBluetooth.sendMessage(channelValues.toBytes());
			mLastValues = values;

			// }
		}
	}

	public int getValue(int channelId) {

		return channelValues.getValue(channelId);
	}

	public class LocalBinder extends Binder {
		public BluetoothHandler getService() {
			return BluetoothHandler.this;
		}
	}

	@Override
	public IBinder onBind(Intent arg0) {

		return mBinder;
	}

	@Override
	public void onCreate() {
		initBluetoothServie();
		super.onCreate();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.d("ChannelValueService", "onDestroy()");
	}

	class BluetoothServiceConnection implements ServiceConnection {

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			mBluetooth = ((BluetoothService.LocalBinder) service)
					.getService(new IncomingHandler(BluetoothHandler.this));
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			mBluetooth = null;
		}

	}

	class IncomingHandler extends Handler {
		private final WeakReference<BluetoothHandler> mTarget;

		IncomingHandler(BluetoothHandler context) {
			mTarget = new WeakReference<BluetoothHandler>(
					(BluetoothHandler) context);
		}

		@Override
		public void handleMessage(Message msg) {
			BluetoothHandler target = mTarget.get();
			switch (msg.what) {
			case BluetoothService.MESSAGE_TOAST:

				Toast.makeText(target, msg.getData().getString("Toast"),
						Toast.LENGTH_LONG).show();
				break;
			case BluetoothService.MESSAGE_STATE_CHANGE:
				if (mBluetooth.getState() == BluetoothService.STATE_CONNECTED) {
					Context context = mTarget.get().getApplicationContext();

					Intent intent = new Intent(context, MainActivity.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

					context.startActivity(intent);
				}
				break;
			case BluetoothService.MESSAGE_DEVICE_NAME:
				Toast.makeText(target,
						msg.getData().getString("Connected") + " connected.",
						Toast.LENGTH_SHORT).show();
				break;
			case BluetoothService.MESSAGE_READ:
				String data = (String) msg.obj;

				int length = msg.arg1;
				//Log.i("-----------> BT Received:", data);

				VehicleStatus status = VehicleStatus.getByName(data);
				//Log.i(tag , "data=" + data);
				Log.i(tag , "status=" + status.toString());
				
				if (status != VehicleStatus.NONE) {
					vehicleStatusListener.onVehicleStatusUpdated(status);
				}
			//	Log.i(tag, "VehicleResponse\"" + data + "\"");

				break;
			default:

			}

			super.handleMessage(msg);
		}

	}

	@Override
	public void sendCommandToVehicle(TurnCommand turn, GoForwardCommand go) {
		// Send TURN command

		if (TurnCommand.Direction.LEFT == turn.getDirection()) {
			channelValues.setValue(0, TURN_LEFT_CMD);
			channelValues.setValue(1, turn.getAngle());
		} else if (TurnCommand.Direction.RIGHT == turn.getDirection()) {
			channelValues.setValue(0, TURN_RIGHT_CMD);
			channelValues.setValue(1, turn.getAngle());
		}
		Log.i(tag, "MoveAction Send: " + turn);
		mBluetooth.sendMessage(channelValues.toBytes());

		// Send GO command
		channelValues.setValue(0, GO_CMD);
		channelValues.setValue(1, go.getDistance());
		
		Log.i(tag, "MoveAction Send: " + go);
		mBluetooth.sendMessage(channelValues.toBytes());

	}

	@Override
	public void connectDevice(BluetoothDevice device) {

		mBluetooth.connect(device);

	}

	public void setVehicleStatusListener(
			IVehicleStatusListener vehicleStatusListener) {
		this.vehicleStatusListener = vehicleStatusListener;
	}

}
