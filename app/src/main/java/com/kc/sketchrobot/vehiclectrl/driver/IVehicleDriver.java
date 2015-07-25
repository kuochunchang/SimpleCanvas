package com.kc.sketchrobot.vehiclectrl.driver;

interface IVehicleDriver {
	//void sendCommand(TurnCommand turn, GoForwardCommand go);
	void go();
	int getCurrentStep();
}
