package com.ritnalap;

import com.pi4j.Pi4J;
import com.pi4j.context.Context;
import com.pi4j.io.gpio.digital.DigitalState;
import com.ritnalap.controller.Controller;
import com.ritnalap.core.StateMachine;
import com.ritnalap.periphery.Buzzer;
import com.ritnalap.periphery.Lcd;
import com.ritnalap.periphery.LedButton;
import com.ritnalap.periphery.MotionSensor;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Main {
	Context pi4j;

	public static void main(String[] args) throws Exception {

		Main main = new Main();
		LedButton ledButton = new LedButton(main.pi4j, 5, 6);
		Buzzer buzzer = new Buzzer(main.pi4j, 12, true);
		Lcd lcd = new Lcd(main.pi4j);
		MotionSensor motionSensor = new MotionSensor(main.pi4j, 26);

		StateMachine stateMachine = new StateMachine(new Controller(ledButton, buzzer, lcd, motionSensor));

		try {

			while (true) {
				stateMachine.checkState();
				Thread.sleep(50);
			}
		} finally {

			main.pi4j.shutdown();
		}
	}

	Main() {
		pi4j = Pi4J.newAutoContext();
	}
}
