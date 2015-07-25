package com.kc.sketchrobot.vehiclectrl.cmd;

import android.graphics.Point;

import com.kc.sketchrobot.coordinate.Coordinate;
import com.kc.sketchrobot.coordinate.PolarCoordinate;

public class MoveAction {

	private GoForwardCommand goCommand;
	private TurnCommand turnCommand;
	private PolarCoordinate polarCoordinate;
	private Point targetCartesianCoordinate;
	private Point fromScreenCoordinate;
	private Point toScreenCoordinate;
	private int penWidth = 0;

	public MoveAction() {
		super();
	}

//	public MoveAction(TurnCommand turnCommand, GoForwardCommand goCommand) {
//		super();
//		this.goCommand = goCommand;
//		this.turnCommand = turnCommand;
//	
//	}

	public MoveAction(Point fromPointOnScreen, Point toPointOnScreen, int standAngle, int penWidth) {
		this.fromScreenCoordinate = fromPointOnScreen;
		this.toScreenCoordinate = toPointOnScreen;
		this.penWidth = penWidth;
		
		// calculate the commands
		Point fromPointOnCartesianCoordinate = Coordinate.screenToCartesianFirstQuadrant(
				fromPointOnScreen, 300);
		Point toPointOnCartesianCoordinate = Coordinate.screenToCartesianFirstQuadrant(
				toPointOnScreen, 300);

		this.setTargetCartesianCoordinate(toPointOnCartesianCoordinate);

		PolarCoordinate pc = Coordinate.cartesianToPolar(fromPointOnCartesianCoordinate,
				toPointOnCartesianCoordinate);
		this.setPolarCoordinate(pc);


		TurnCommand.Direction direction = null;
		int angle = 0;

		direction = pc.getAngle() - standAngle >= 0 ? TurnCommand.Direction.LEFT
				: TurnCommand.Direction.RIGHT;

		angle = Math.abs(pc.getAngle() - standAngle);

		// if go to quadrant 4
		if (angle >= 180) {
			angle = 360 - angle;
			direction = (direction == TurnCommand.Direction.LEFT ? TurnCommand.Direction.RIGHT
					: TurnCommand.Direction.LEFT);
		}

		this.setTurnCommand(new TurnCommand(direction, angle));
		this.setGoCommand(new GoForwardCommand(pc.getDistance()));
	}

	public GoForwardCommand getGoCommand() {
		return goCommand;
	}

	public TurnCommand getTurnCommand() {
		return turnCommand;
	}

	public void setGoCommand(GoForwardCommand goCommand) {
		this.goCommand = goCommand;
	}

	public void setTurnCommand(TurnCommand turnCommand) {
		this.turnCommand = turnCommand;
	}

	public PolarCoordinate getPolarCoordinate() {
		return polarCoordinate;
	}

	public void setPolarCoordinate(PolarCoordinate polarCoordinate) {
		this.polarCoordinate = polarCoordinate;
	}

	public Point getCartesianCoordinate() {
		return targetCartesianCoordinate;
	}

	public void setTargetCartesianCoordinate(Point cartesianCoordinate) {
		this.targetCartesianCoordinate = cartesianCoordinate;
	}

	public Point getFromScreenCoordinate() {
		return fromScreenCoordinate;
	}

	public void setFromScreenCoordinate(Point screenCoordinate) {
		this.fromScreenCoordinate = screenCoordinate;
	}

	public int getPenWidth() {
		return penWidth;
	}

	public void setPenWidth(int penWidth) {
		this.penWidth = penWidth;
	}

	private String[] toCommandString() {
		String[] result = new String[2];
		if (turnCommand != null) {
			result[0] = turnCommand.toString();
		} else {
			result[0] = "STOP()";
		}
		if (goCommand != null) {
			result[1] = goCommand.toString();
		} else {
			result[1] = "STOP()";
		}
		return result;

	}

	public String toString() {
		String[] commands = toCommandString();

		return commands[0] + ", " + commands[1];
	}

	public Point getToScreenCoordinate() {
		return toScreenCoordinate;
	}

	public void setToScreenCoordinate(Point toScreenCoordinate) {
		this.toScreenCoordinate = toScreenCoordinate;
	}

}