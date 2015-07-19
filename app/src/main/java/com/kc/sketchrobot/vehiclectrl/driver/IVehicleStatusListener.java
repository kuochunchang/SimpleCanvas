package com.kc.sketchrobot.vehiclectrl.driver;

import com.kc.sketchrobot.vehiclectrl.status.VehicleStatus;

public interface IVehicleStatusListener {
	void onVehicleStatusUpdated(VehicleStatus status);

}
