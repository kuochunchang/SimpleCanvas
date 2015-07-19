package com.kc.sketchrobot.vehiclectrl.driver;

import com.kc.sketchrobot.vehiclectrl.status.VehicleStatus;

public interface VehicleListener {
	public void onStatusUpdate(VehicleStatus status);

}
