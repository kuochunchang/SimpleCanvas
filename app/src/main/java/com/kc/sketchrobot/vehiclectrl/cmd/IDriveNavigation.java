package com.kc.sketchrobot.vehiclectrl.cmd;

public interface IDriveNavigation {

	MoveAction getMoveAction(int step);

	int getStepNumber();

}
