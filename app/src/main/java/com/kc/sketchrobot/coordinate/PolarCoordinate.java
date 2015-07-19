package com.kc.sketchrobot.coordinate;

public class PolarCoordinate {
	private int angle;
	private int distance;

	public PolarCoordinate(int degree, int distance) {
		this.angle = degree;
		this.distance = distance;
	}

	public int getAngle() {
		return angle;
	}

	public void setAngle(int angle) {
		this.angle = angle;
	}

	public int getDistance() {
		return distance;
	}

	public void setDistance(int distance) {
		this.distance = distance;
	}

	public String toString() {
		return "P(" + distance + ", " + angle + ")";
	}
}
