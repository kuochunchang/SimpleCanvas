package com.kc.sketchrobot.coordinate;


import android.graphics.Point;



public class Coordinate {
	static public Point screenToCartesianFirstQuadrant(Point p, int screenHight) {

		return new Point(p.x, screenHight - p.y);

	}
	
	static public PolarCoordinate cartesianToPolar(Point base, Point to) {

		double radian = Math.atan2(to.y - base.y, to.x - base.x);
		int angle = (int) (radian * 180 / Math.PI);
		int distance = getPointsDistance(base, to);
		return new PolarCoordinate(angle, distance);
	}
	
	public static int getPointsDistance(Point from, Point to) {
		return (int) Math.sqrt((Math.pow(from.x - to.x, 2) + Math.pow(from.y
				- to.y, 2)));

	}

}
