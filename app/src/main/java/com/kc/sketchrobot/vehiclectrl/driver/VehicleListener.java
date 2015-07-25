package com.kc.sketchrobot.vehiclectrl.driver;

import com.kc.sketchrobot.vehiclectrl.status.VehicleStatus;

public interface VehicleListener {
	void onStatusUpdate(VehicleStatus status);

}
