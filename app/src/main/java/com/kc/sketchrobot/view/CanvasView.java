package com.kc.sketchrobot.view;

import java.util.ArrayList;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.example.simplecanvas.R;
import com.kc.sketchrobot.vehiclectrl.cmd.GoForwardCommand;
import com.kc.sketchrobot.vehiclectrl.cmd.MoveAction;
import com.kc.sketchrobot.vehiclectrl.cmd.TurnCommand;
import com.kc.sketchrobot.vehiclectrl.driver.ChannelValueListener;
import com.kc.sketchrobot.vehiclectrl.driver.DrawingManager;
import com.kc.sketchrobot.vehiclectrl.driver.ICanvasStatusUpdateListener;

public class CanvasView extends View {
	private String tag = "CanvasView";
	private DrawingManager drawingManager;

	
	
	private ChannelValueListener channelValueListener;

	public CanvasView(Context context) {
		super(context);
		init(null, 0);
	}

	public CanvasView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(attrs, 0);
	}

	public CanvasView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(attrs, defStyle);
	}

	private void init(AttributeSet attrs, int defStyle) {
		// Load attributes
		final TypedArray a = getContext().obtainStyledAttributes(attrs,
				R.styleable.CanvasView, defStyle, 0);

		normalPaint = new Paint();
		normalPaint.setColor(Color.BLACK);
		a.recycle();
		


	}

	private Paint normalPaint;

	@Override
	public boolean onTouchEvent(MotionEvent event) {

		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			drawingManager.createSegment();
		}

		if (drawingManager.getCurrentSegment() != null
				&& event.getAction() == MotionEvent.ACTION_MOVE) {

			int x = (int) event.getX();
			int y = (int) event.getY();

			drawingManager.addPoint(new Point(x, y));
		}

		if (drawingManager.getCurrentSegment() != null
				&& event.getAction() == MotionEvent.ACTION_UP) {

		}

		performClick();
		postInvalidate();
		return true;
	}

	@Override
	public boolean performClick() {
		super.performClick();

		return true;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		if (drawingManager.getMoveActionList() != null) {

			for (int i = 0; i < drawingManager.getMoveActionList().size(); i++) {
				MoveAction moveAction = drawingManager.getMoveActionList().get(
						i);
				Point from = moveAction.getFromScreenCoordinate();
				Point to = moveAction.getToScreenCoordinate();

				normalPaint.setStrokeWidth(moveAction.getPenWidth());
				normalPaint.setColor(Color.BLACK);

				canvas.drawLine(from.x, from.y, to.x, to.y, normalPaint);
				canvas.drawCircle(from.x, from.y, 3, normalPaint);
				canvas.drawCircle(to.x, to.y, 3, normalPaint);

				canvas.drawText(moveAction.toString(), from.x + 5, from.y + 10,
						normalPaint);
			}

		} else {
			for (ArrayList<Point> draw : drawingManager.getTrace()) {
				normalPaint.setColor(Color.BLACK);
				if (draw.size() > 2) {
					for (int i = 0; i < draw.size() - 1; i++) {
						Point from = draw.get(i);
						Point to = draw.get(i + 1);
						canvas.drawLine(from.x, from.y, to.x, to.y, normalPaint);

					}
				}
			}
		}

		for (int i = 0 ; i< drawingManager.getCompletedPoints().size(); i++) {
			normalPaint.setStrokeWidth(3);
			normalPaint.setColor(Color.BLUE);
			MoveAction ma = drawingManager.getCompletedPoints().get(i);
			
			if(i == drawingManager.getCompletedPoints().size()-1){
				//canvas.drawCircle(ma.getFromScreenCoordinate().x,	ma.getFromScreenCoordinate().y, 6, normalPaint);
				canvas.drawCircle(ma.getToScreenCoordinate().x, ma.getToScreenCoordinate().y, 6, normalPaint);
			}
			
			canvas.drawLine(ma.getFromScreenCoordinate().x,
					ma.getFromScreenCoordinate().y,
					ma.getToScreenCoordinate().x, ma.getToScreenCoordinate().y,
					normalPaint);
		
		}
	}

	enum Command {
		GO(1), TURN_LEFT(2), TURN_RIGHT(3);
		private int value;

		private Command(int value) {
			this.value = value;
		}

		public int getValue() {
			return value;
		}

	}

	private void robotGoto(int step) {

		MoveAction moveAction = drawingManager.getMoveActionList().get(step);
		GoForwardCommand go = moveAction.getGoCommand();
		channelValueListener.onChannelValueUpdate(Command.GO.getValue(),
				go.getDistance());

		TurnCommand turn = moveAction.getTurnCommand();
		if (turn.getDirection() == TurnCommand.Direction.LEFT) {
			channelValueListener.onChannelValueUpdate(
					Command.TURN_LEFT.getValue(), turn.getAngle());
		}
		if (turn.getDirection() == TurnCommand.Direction.RIGHT) {
			channelValueListener.onChannelValueUpdate(
					Command.TURN_RIGHT.getValue(), turn.getAngle());
		}

	}

	public void transform() {
		drawingManager.drawingComplete();
		this.invalidate();

	}

	public void clear() {
		// transformList = null;
		drawingManager.clear();
		this.invalidate();
	}

	public void rose() {
		// double K = Math.E;
		double K = 4.0 / 5.0;

		drawingManager.clear();
		drawingManager.createSegment();

		for (double d = 0; d <= 360 * 5; d++) {

			double theta = (Math.PI / 180) * d;
			double r = 300 * Math.cos(K * theta);

			int x = (int) (r * Math.cos(theta)) + 300;
			int y = (int) (r * Math.sin(theta)) + 300;

			drawingManager.addPoint(new Point(x, y));

		}

		this.invalidate();

	}

	public ChannelValueListener getChannelValueListener() {
		return channelValueListener;
	}

	public void setChannelValueListener(
			ChannelValueListener channelValueListener) {
		this.channelValueListener = channelValueListener;
	}

	public void setDrawingManager(DrawingManager drawingManager) {
		this.drawingManager = drawingManager;
		drawingManager.setCanvasStatusUpdateListener(new ICanvasStatusUpdateListener(){

			@Override
			public void onCanvasUpdate() {
				CanvasView.this.invalidate();
				
			}
			
		});

	}

}
