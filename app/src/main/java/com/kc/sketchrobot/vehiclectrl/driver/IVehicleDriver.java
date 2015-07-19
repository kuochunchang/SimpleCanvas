package com.kc.sketchrobot.vehiclectrl.driver;

import com.kc.sketchrobot.vehiclectrl.cmd.GoForwardCommand;
import com.kc.sketchrobot.vehiclectrl.cmd.TurnCommand;

public interface IVehicleDriver {
	//void sendCommand(TurnCommand turn, GoForwardCommand go);
	void go();
	int getCurrentStep();
}
