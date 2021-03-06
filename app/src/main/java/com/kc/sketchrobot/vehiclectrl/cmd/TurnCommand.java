package com.kc.sketchrobot.vehiclectrl.cmd;

public class TurnCommand {
	public enum Direction {
		LEFT, RIGHT
	}

	private static final String TURN_RIGHT_CMD = "RIGHT";
	private static final String TURN_LEFT_CMD = "LEFT";

	private Direction direction;
	private int angle;

	public TurnCommand(Direction direction, int angle) {
		super();
		this.direction = direction;
		this.angle = angle;
	}

	
	
	public Direction getDirection() {
		return direction;
	}



	public int getAngle() {
		return angle;
	}



	@Override
	public String toString() {
		switch (direction) {
		
		case LEFT:
			return TURN_LEFT_CMD + "(" + angle + ")";
		case RIGHT:
			return TURN_RIGHT_CMD + "(" + angle + ")";
		
		default:
			break;

		}
		
		return null;
		
	}

}
