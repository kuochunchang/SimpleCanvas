package com.kc.sketchrobot.vehiclectrl.driver;

import android.util.Log;

import com.kc.sketchrobot.vehiclectrl.cmd.GoForwardCommand;
import com.kc.sketchrobot.vehiclectrl.cmd.IDriveNavigation;
import com.kc.sketchrobot.vehiclectrl.cmd.IMoveActionSender;
import com.kc.sketchrobot.vehiclectrl.cmd.MoveAction;
import com.kc.sketchrobot.vehiclectrl.cmd.TurnCommand;
import com.kc.sketchrobot.vehiclectrl.status.VehicleStatus;

public class VehicleDriver implements IVehicleStatusListener, IVehicleDriver {

	private IDriveNavigation driveNavigation;
	private IMoveActionSender moveActionSender;
	private IDrawingStatusListener drawingStatusManager;

	private int actionStepCounter = 0;
	private VehicleStatus status;

	public VehicleDriver(IMoveActionSender moveActionSender,
			IDriveNavigation driveNavigation, IDrawingStatusListener drawingStatusManager) {
		this.moveActionSender = moveActionSender;
		this.driveNavigation = driveNavigation;
		this.drawingStatusManager = drawingStatusManager;
	}

	private void sendCommandToVehicle(TurnCommand turn, GoForwardCommand go) {
		moveActionSender.sendCommandToVehicle(turn, go);
	}

	@Override
	public void onVehicleStatusUpdated(VehicleStatus status) {
		MoveAction moveAction = null;
		this.status = status;
		if (VehicleStatus.ALL_COMPLTETED != status) {

			switch (status) {
			case ASK_NEXT_ACTION_FROM_VEHICLE:
				if (actionStepCounter < driveNavigation.getStepNumber()) {
					moveAction = driveNavigation
							.getMoveAction(actionStepCounter);
					sendCommandToVehicle(moveAction.getTurnCommand(),
							moveAction.getGoCommand());
				}
				break;

			case ASK_RESEND_NEXT_ACTION_FROM_VEHICLEEND:
				moveAction = driveNavigation.getMoveAction(actionStepCounter);
				sendCommandToVehicle(moveAction.getTurnCommand(),
						moveAction.getGoCommand());
				break;

			case CURRENT_ACTION_COMPLETED:
				
				actionStepCounter += 1;
				if (actionStepCounter >= driveNavigation.getStepNumber()) {
					status = VehicleStatus.ALL_COMPLTETED;
					
					Log.i("Current Action:", status.toString());
					
				}else{
					drawingStatusManager.addCompletedPoint(driveNavigation.getMoveAction(actionStepCounter-1));
				}
				
				
				// notify to Canvas view to show this status

				break;

			default:
				break;
			}
		}
	}

	@Override
	public void go() {
		// if (status == VehicleStatus.READY) {
		MoveAction moveAction = driveNavigation.getMoveAction(0);
		
		moveActionSender.sendCommandToVehicle(moveAction.getTurnCommand(),
				moveAction.getGoCommand());
		
		actionStepCounter = 1;
		
		status = VehicleStatus.START;
		// }
	}

	@Override
	public int getCurrentStep() {

		return actionStepCounter;
	}

}
