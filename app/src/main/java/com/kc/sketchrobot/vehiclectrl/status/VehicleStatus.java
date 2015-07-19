package com.kc.sketchrobot.vehiclectrl.status;

public enum VehicleStatus {
	READY, START, AT_THE_FIRST_POINT, NEXT_POINT_RECEIVED_BY_VEHICLE, ASK_NEXT_ACTION_FROM_VEHICLE, ASK_RESEND_NEXT_ACTION_FROM_VEHICLEEND, WATIING_FOR_RESPONSE_FROM_VEHICLE, CURRENT_ACTION_COMPLETED, ALL_COMPLTETED, NONE;

	public static VehicleStatus getByName(String name){
		for(VehicleStatus s : VehicleStatus.values()){
			if(s.toString().equals(name)){
				return s;
			}
		}
		
		return VehicleStatus.NONE;
		
	}

}
