package com.kc.sketchrobot.activity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.simplecanvas.R;
import com.kc.sketchrobot.service.BluetoothHandler;

public class BluetoothSetupActivity extends ActionBarActivity {

	private ListView btDeviceListView;
	private BtDeviceAdapter mBtDeviceAdapter;
	private List<BluetoothDevice> bundedDeviceList;
	private BluetoothAdapter mAdapter;
	private final static int REQUEST_ENABLE_BT = 1;

	String tag = BluetoothSetupActivity.class.getSimpleName();
	private BluetoothHandler mBluetoothHandlerService;
	private BluetoothHandlerConnection mBluetoothHandlerConnection;

	class BluetoothHandlerConnection implements ServiceConnection {
		
		public void onServiceConnected(ComponentName name, IBinder boundService) {
			mBluetoothHandlerService = ((BluetoothHandler.LocalBinder) boundService)
					.getService();
			Log.i(tag, "onServiceConnected(): Connected");

		}

		public void onServiceDisconnected(ComponentName name) {
			mBluetoothHandlerService = null;
			Log.i(tag, "onServiceDisconnected(): Disconnected");

		}
	}

	private void initBluetoothService() {
		mBluetoothHandlerConnection = new BluetoothHandlerConnection();

		boolean ret = bindService(new Intent(BluetoothSetupActivity.this,
				BluetoothHandler.class), mBluetoothHandlerConnection,
				Context.BIND_AUTO_CREATE);
		Log.i(tag, "initService() bound value: " + ret);

		mAdapter = BluetoothAdapter.getDefaultAdapter();
		registerReceiver(mReceiver, filter);

		if (!mAdapter.isEnabled()) {
			Intent enableBtIntent = new Intent(
					BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
		}

		bundedDeviceList.addAll(getPairedBluetoothDevices());
		mBtDeviceAdapter.notifyDataSetChanged();

	}

	private void releaseService() {

		unbindService(mBluetoothHandlerConnection);
		mBluetoothHandlerService = null;
		Log.d(tag, "releaseService(): unbound.");
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (requestCode == REQUEST_ENABLE_BT) {
			bundedDeviceList.addAll(getPairedBluetoothDevices());
			mBtDeviceAdapter.notifyDataSetChanged();
		}

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bluetooth_setup);

		bundedDeviceList = new ArrayList<BluetoothDevice>();
		btDeviceListView = (ListView) findViewById(R.id.btDeviceListView);
		mBtDeviceAdapter = new BtDeviceAdapter();
		
		initBluetoothService();
		
		btDeviceListView.setAdapter(mBtDeviceAdapter);
		btDeviceListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				Log.i(tag, "Device:" + bundedDeviceList.get(position)
						+ " selected");

				mBluetoothHandlerService.connectDevice(bundedDeviceList
						.get(position));

			}

		});

		

		// startService(new Intent(this, BluetoothService.class));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.bluetooth_setup, menu);
		return true;
	}

	// private MenuItem scanMenuItem;
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.

		int id = item.getItemId();
		if (id == R.id.action_settings) {
			bundedDeviceList.clear();
			mBtDeviceAdapter.notifyDataSetChanged();
			// scanMenuItem.setTitle("Scanning...");

			findNewDevice(3000);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onDestroy() {
		releaseService();
		unregisterReceiver(mReceiver);
		super.onDestroy();
	}

	private class BtDeviceAdapter extends BaseAdapter {
		private LayoutInflater inflator;

		public BtDeviceAdapter() {
			super();
			inflator = BluetoothSetupActivity.this.getLayoutInflater();
		}

		@Override
		public int getCount() {

			return bundedDeviceList.size();
		}

		@Override
		public Object getItem(int index) {

			return bundedDeviceList.get(index);
		}

		@Override
		public long getItemId(int i) {

			return i;
		}

		@Override
		public View getView(int i, View view, ViewGroup viewGroup) {
			BtDeviceItemViewHolder viewHolder;
			if (view == null) {
				view = inflator.inflate(R.layout.bt_device_list_item, null);
				viewHolder = new BtDeviceItemViewHolder();
				viewHolder.deviceName = (TextView) view
						.findViewById(R.id.btName);
				viewHolder.address = (TextView) view
						.findViewById(R.id.btAddress);

			} else {
				viewHolder = (BtDeviceItemViewHolder) view.getTag();
			}

			if (viewHolder != null) {
				BluetoothDevice device = bundedDeviceList.get(i);
				viewHolder.deviceName.setText(device.getName());
				viewHolder.address.setText(device.getAddress());
			}

			return view;
		}

	}

	class BtDeviceItemViewHolder {
		TextView deviceName;
		TextView address;

	}

	// Register the BroadcastReceiver
	IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
	// Create a BroadcastReceiver for ACTION_FOUND
	private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			// When discovery finds a device
			if (BluetoothDevice.ACTION_FOUND.equals(action)) {

				// Get the BluetoothDevice object from the Intent
				BluetoothDevice device = intent
						.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

				Log.i(tag, device + " found.");

				bundedDeviceList.add(device);
				mBtDeviceAdapter.notifyDataSetChanged();
			}
		}
	};

	public void findNewDevice(final int duration) {
		Log.i(tag, "Start to discover devices in " + duration + " miliseconds");
		mAdapter.startDiscovery();

		Thread t = new Thread(new Runnable() {

			long startTime = System.currentTimeMillis();

			boolean discovereryCanceled = false;

			@Override
			public void run() {
				while (!discovereryCanceled) {
					if (System.currentTimeMillis() - startTime > duration) {
						mAdapter.cancelDiscovery();

						discovereryCanceled = true;

					}

					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

			}
		});

		t.start();

	}

	public Set<BluetoothDevice> getPairedBluetoothDevices() {
		if (mAdapter.isEnabled()) {
			Set<BluetoothDevice> pairedBluetoothDevices = mAdapter
					.getBondedDevices();
			Log.i(tag, pairedBluetoothDevices.size() + " paired devices.");

			return pairedBluetoothDevices;
		} else {
			return new HashSet<BluetoothDevice>();
		}
	}

}
