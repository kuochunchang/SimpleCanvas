package com.kc.sketchrobot.vehiclectrl.driver;

import com.kc.sketchrobot.vehiclectrl.cmd.MoveAction;

import java.util.List;

interface IDrawingStatusListener {

	void addCompletedPoint(MoveAction moveAction);

	List<MoveAction> getCompletedPoints();

}