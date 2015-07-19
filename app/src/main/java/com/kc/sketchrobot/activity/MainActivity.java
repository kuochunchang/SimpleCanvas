package com.kc.sketchrobot.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.simplecanvas.R;
import com.kc.sketchrobot.service.BluetoothHandler;
import com.kc.sketchrobot.vehiclectrl.driver.DrawingManager;
import com.kc.sketchrobot.vehiclectrl.driver.VehicleDriver;
import com.kc.sketchrobot.view.CanvasView;

public class MainActivity extends ActionBarActivity {
	private CanvasView mCanvasView;
	private String tag = "MainActivity";
	private DrawingManager drawingManager;

	private BluetoothHandler mBluetoothHandlerService;
	private BluetoothHandlerConnection mBluetoothHandlerConnection;

	private static VehicleDriver mVehicleDriver;

	class BluetoothHandlerConnection implements ServiceConnection {
		public void onServiceConnected(ComponentName name, IBinder boundService) {
			mBluetoothHandlerService = ((BluetoothHandler.LocalBinder) boundService)
					.getService();		
			Log.i(tag, "onServiceConnected(): Connected");	
			
			mVehicleDriver = new VehicleDriver(mBluetoothHandlerService, drawingManager, drawingManager);
			mBluetoothHandlerService.setVehicleStatusListener(mVehicleDriver);

		}

		public void onServiceDisconnected(ComponentName name) {
			mBluetoothHandlerService = null;
			Log.i(tag, "onServiceDisconnected(): Disconnected");

		}
	}

	private void initBluetoothService() {
		mBluetoothHandlerConnection = new BluetoothHandlerConnection();

		boolean ret = bindService(new Intent(MainActivity.this,
				BluetoothHandler.class), mBluetoothHandlerConnection,
				Context.BIND_AUTO_CREATE);
		Log.i(tag, "initService() bound value: " + ret);
	
	}

	private void releaseService() {
		
		unbindService(mBluetoothHandlerConnection);
		mBluetoothHandlerService = null;
		Log.d(tag, "releaseService(): unbound.");
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		initBluetoothService();

		drawingManager = new DrawingManager();

		mCanvasView = (CanvasView) this.findViewById(R.id.canvasView);
		mCanvasView.setDrawingManager(drawingManager);

	}

	public void go(View v) {
		mCanvasView.transform();
		mVehicleDriver.go();
	}

	public void clear(View v) {
		mCanvasView.clear();
	}

	public void demo(View v) {
		mCanvasView.rose();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}

		if (id == R.id.bt_connection) {
			Intent intent = new Intent(this, BluetoothSetupActivity.class);

			startActivityForResult(intent, 888);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		releaseService();
	}
}
