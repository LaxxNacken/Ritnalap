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
					System.out.println("currently in idle");
					System.out.println("move into sense");
					controller.moveIntoSense();
					state = StateMachineStates.SENSING;
				}
				break;
			case SENSING:
				if (buttonState == ButtonStates.DOUBLE_PRESS) {
					System.out.println("currently in sensing");
					System.out.println("move into idle");
					controller.moveIntoIdle();
					state = StateMachineStates.IDLE;
				}
				if (motionSensorState == MotionSensorStates.MOTION) {
					System.out.println("move into alarm");
					controller.moveIntoAlarm();
					state = StateMachineStates.ALARM;
				}
				break;
			case ALARM:
				if (buttonState == ButtonStates.SINGLE_PRESS) {
					System.out.println("currently in alarm");
					System.out.println("move into sense");
					System.out.println("turn off alarm");
					controller.turnOffAlarm();
					controller.moveIntoSense();
					state = StateMachineStates.SENSING;
				} else if (buttonState == ButtonStates.DOUBLE_PRESS) {
					System.out.println("currently in alarm");
					System.out.println("move into sense");
					controller.moveIntoIdle();
					state = StateMachineStates.IDLE;
				}
				break;
		}
	}
}
