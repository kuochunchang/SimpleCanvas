package com.kc.sketchrobot.vehiclectrl.driver;

import android.graphics.Point;

import com.kc.sketchrobot.coordinate.Coordinate;
import com.kc.sketchrobot.vehiclectrl.cmd.IDriveNavigation;
import com.kc.sketchrobot.vehiclectrl.cmd.MoveAction;
import com.kc.sketchrobot.vehiclectrl.status.TransformStatus;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class DrawingManager implements IDriveNavigation, IDrawingStatusListener {
	private List<ArrayList<Point>> screenTraceList = new ArrayList<ArrayList<Point>>();
	private LinkedList<MoveAction> moveActionList;
	private ArrayList<MoveAction> completedPoints = new ArrayList<MoveAction>();
	private ICanvasStatusUpdateListener canvasStatusListener;

	public LinkedList<MoveAction> getMoveActionList() {
		return moveActionList;
	}

	private static final int LL = 20;

	public void setCanvasStatusUpdateListener(ICanvasStatusUpdateListener canvasStatusListener) {
		this.canvasStatusListener = canvasStatusListener;
	}

	@Override
	public void addCompletedPoint(MoveAction point) {
		completedPoints.add(point);
		canvasStatusListener.onCanvasUpdate();
	}

	@Override
	public List<MoveAction> getCompletedPoints() {

		return completedPoints;

	}

	public void addPoint(Point p) {
		ArrayList<Point> theLastList = getCurrentSegment();
		theLastList.add(p);

	}

	public ArrayList<Point> getCurrentSegment() {
		return screenTraceList.get(screenTraceList.size() - 1);
	}

	public void createSegment() {

		screenTraceList.add(new ArrayList<Point>());
	}

	public void clear() {
		screenTraceList = new ArrayList<ArrayList<Point>>();
		moveActionList = null;
		completedPoints = new ArrayList<MoveAction>();
	}

	public List<ArrayList<Point>> getTrace() {
		return screenTraceList;
	}

	public void drawingComplete() {
		screenCoordinateToMoveActions();

	}

	private void screenCoordinateToMoveActions() {
		TransformStatus status = TransformStatus.START;
		moveActionList = new LinkedList<MoveAction>();

		int standAngle = 90;

		for (int s = 0; s < screenTraceList.size(); s++) {
			if (s == 0) {
				status = TransformStatus.START_THE_FIRST_SEG;
			} else {
				status = TransformStatus.FROM_SEG_TO_SEG;
			}

			if (status == TransformStatus.START_THE_FIRST_SEG) {
				status = TransformStatus.IN_SEG;
			}

			if (status == TransformStatus.FROM_SEG_TO_SEG) {
				// create a connection move action for both segment
				MoveAction previousMoveAction = moveActionList.getLast();

				standAngle = previousMoveAction.getPolarCoordinate().getAngle();
				Point theFirstPointOfSegment = screenTraceList.get(s).get(0);

				MoveAction moveAction = new MoveAction(
						previousMoveAction.getToScreenCoordinate(),
						theFirstPointOfSegment, standAngle, 1);

				moveActionList.add(moveAction);
				standAngle = moveAction.getPolarCoordinate().getAngle();

				status = TransformStatus.IN_SEG;
			}

			ArrayList<Point> segment = screenTraceList.get(s);

			if (status == TransformStatus.IN_SEG) {
				MoveAction theLastMoveActionOfSegment = null;

				Point fromPointOnScreen = null;
				Point toPointOnScreen;
				for (int i = 0; i < segment.size() - 1; i++) {
					if (i == 0) {
						fromPointOnScreen = segment.get(0);
					} else if (theLastMoveActionOfSegment != null) {
						fromPointOnScreen = theLastMoveActionOfSegment
								.getToScreenCoordinate();
					}

					toPointOnScreen = segment.get(i + 1);
					if (Coordinate.getPointsDistance(toPointOnScreen,
							fromPointOnScreen) > LL) {

						MoveAction moveAction = new MoveAction(
								fromPointOnScreen, toPointOnScreen, standAngle,
								2);

						moveActionList.add(moveAction);

						standAngle = moveAction.getPolarCoordinate().getAngle();

						theLastMoveActionOfSegment = moveAction;
					}
				}
			}

		}
	}

	@Override
	public MoveAction getMoveAction(int step) {

		return moveActionList.get(step);
	}

	@Override
	public int getStepNumber() {

		return moveActionList.size();
	}

}
