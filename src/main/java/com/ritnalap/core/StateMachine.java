package com.ritnalap.core;

import com.ritnalap.controller.Controller;
import com.ritnalap.core.states.ButtonStates;
import com.ritnalap.core.states.MotionSensorStates;
import com.ritnalap.core.states.StateMachineStates;

public class StateMachine {
	private StateMachineStates state = StateMachineStates.IDLE;
	private final Controller controller;

	public StateMachine(Controller controller) {
		this.controller = controller;
		controller.moveIntoIdle();
	}

	public void checkState() {
		ButtonStates buttonState = controller.getButtonState();
		MotionSensorStates motionSensorState = controller.getMotionSensorState();
		switch (state) {
			case IDLE:
				if (buttonState == ButtonStates.DOUBLE_PRESS) {
					controller.moveIntoSense();
					state = StateMachineStates.SENSING;
				}
				break;
			case SENSING:
				if (buttonState == ButtonStates.DOUBLE_PRESS) {
					controller.moveIntoIdle();
					state = StateMachineStates.IDLE;
				}
				if (motionSensorState == MotionSensorStates.MOTION) {
					controller.moveIntoAlarm();
					state = StateMachineStates.ALARM;
				}
				break;
			case ALARM:
				if (buttonState == ButtonStates.SINGLE_PRESS) {
					controller.turnOffAlarm();
					controller.moveIntoSense();
					state = StateMachineStates.SENSING;
				} else if (buttonState == ButtonStates.DOUBLE_PRESS) {
					controller.moveIntoIdle();
					state = StateMachineStates.IDLE;
				}
				break;
		}
	}
}
