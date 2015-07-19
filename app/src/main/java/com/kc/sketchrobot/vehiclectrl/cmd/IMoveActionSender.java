package com.kc.sketchrobot.vehiclectrl.cmd;

public interface IMoveActionSender {
	 void sendCommandToVehicle(TurnCommand turn, GoForwardCommand go);
}
