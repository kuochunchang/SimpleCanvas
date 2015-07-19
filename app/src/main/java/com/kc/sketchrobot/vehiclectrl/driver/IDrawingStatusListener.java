package com.kc.sketchrobot.vehiclectrl.driver;

import java.util.List;

import com.kc.sketchrobot.vehiclectrl.cmd.MoveAction;

public interface IDrawingStatusListener {

	public abstract void addCompletedPoint(MoveAction moveAction);

	public abstract List<MoveAction> getCompletedPoints();

}