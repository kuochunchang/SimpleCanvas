package com.kc.sketchrobot.vehiclectrl.cmd;

public class GoForwardCommand {
	private static final String GO_FORWARD_CMD = "GO";
	int distance;

	public int getDistance() {
		return distance;
	}

	public GoForwardCommand(int distance) {
		super();
		this.distance = distance;
	}

	@Override
	public String toString() {
		return GO_FORWARD_CMD + "(" + distance + ")";
	}
}
