package com.ritnalap.controller;

import com.ritnalap.core.states.ButtonStates;
import com.ritnalap.core.states.MotionSensorStates;
import com.ritnalap.core.states.StateMachineStates;
import com.ritnalap.periphery.Buzzer;
import com.ritnalap.periphery.Lcd;
import com.ritnalap.periphery.LedButton;
import com.ritnalap.periphery.MotionSensor;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Controller {
	private final LedButton ledButton;
	private boolean lastButtonDown = false;
	private long lastPressTime = 0;
	private boolean waitingSecondPress = false;
	private final long DOUBLE_PRESS_MS = 500;
	private ScheduledExecutorService buzzerScheduler;

	private final Buzzer buzzer;
	private final Lcd lcd;
	private final MotionSensor motionSensor;

	public Controller(LedButton ledButton, Buzzer buzzer, Lcd lcd,
			MotionSensor motionSensor) {
		this.ledButton = ledButton;
		this.buzzer = buzzer;
		this.lcd = lcd;
		this.motionSensor = motionSensor;
	}

	public void displayIdle() {
		lcd.clearText();
		lcd.writeLine("Ritnalap", 0);
		lcd.writeLine("Idle", 1);
	}

	public void displaySense() {
		lcd.clearText();
		lcd.writeLine("Ritnalap", 0);
		lcd.writeLine("Sensing", 1);
	}

	public void displayAlarm() {
		lcd.clearText();

		LocalDateTime now = LocalDateTime.now();
		DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

		lcd.writeLine(now.format(dateFormatter), 0);
		lcd.writeLine(now.format(timeFormatter), 1);
	}

	public void displayCounter(String text) {
		lcd.clearText();
		lcd.writeLine("Ritnalap", 0);
		lcd.writeLine(text, 1);
	}

	public ButtonStates getButtonState() {
		boolean down = ledButton.isDown();
		long now = System.currentTimeMillis();

		if (down && !lastButtonDown) {
			if (waitingSecondPress && (now - lastPressTime <= DOUBLE_PRESS_MS)) {
				waitingSecondPress = false;
				lastPressTime = 0;
				lastButtonDown = down;
				return ButtonStates.DOUBLE_PRESS;
			} else {
				waitingSecondPress = true;
				lastPressTime = now;
			}
		}

		if (!down && waitingSecondPress && (now - lastPressTime > DOUBLE_PRESS_MS)) {
			waitingSecondPress = false;
			lastPressTime = 0;
			lastButtonDown = down;
			return ButtonStates.SINGLE_PRESS;
		}

		lastButtonDown = down;
		return ButtonStates.RELEASED;
	}

	public MotionSensorStates getMotionSensorState() {
		if (motionSensor.isUp()) {
			return MotionSensorStates.MOTION;
		} else {
			return MotionSensorStates.NO_MOTION;
		}
	}

	public void moveIntoIdle() {
		displayIdle();
		turnOffAlarm();
		turnOffLed();
	}

	public void moveIntoSense() {
		try {
			for (int i = 5; i >= 0; i--) {
				displayCounter(String.valueOf(i));
				Thread.sleep(1000); // wait 1 second
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		displaySense();
		turnOnLed();
	}

	public void stopBuzzer() {
		if (buzzerScheduler != null && !buzzerScheduler.isShutdown()) {
			buzzerScheduler.shutdownNow();
		}
		buzzer.off(); // ensure buzzer stops immediately
	}

	public void playFurEliseFull() {
		stopBuzzer();

		buzzerScheduler = Executors.newSingleThreadScheduledExecutor();
		ScheduledExecutorService scheduler = buzzerScheduler;

		// Frequencies in Hz (simplified full Für Elise melody)
		int[] notes = {
				// A section (first motif)
				330, 311, 330, 311, 330, 247, 294, 262, 220,
				196, 262, 330, 247, 294, 220,

				// A section repeat
				330, 311, 330, 311, 330, 247, 294, 262, 220,
				196, 262, 330, 247, 294, 220,

				// B section (simplified, recognizable)
				262, 196, 220, 247, 330, 262, 294, 311, 330,
				247, 220, 196, 220, 247, 294, 262,

				// Back to A section
				330, 311, 330, 311, 330, 247, 294, 262, 220,
				196, 262, 330, 247, 294, 220
		};

		// Durations in ms (approximate, to mimic phrasing)
		int[] durations = {
				// A section
				200, 200, 200, 200, 200, 200, 200, 200, 400,
				400, 200, 200, 200, 200, 400,

				// A section repeat
				200, 200, 200, 200, 200, 200, 200, 200, 400,
				400, 200, 200, 200, 200, 400,

				// B section
				300, 300, 300, 300, 400, 300, 300, 300, 400,
				300, 300, 300, 300, 300, 300, 400,

				// Back to A section
				200, 200, 200, 200, 200, 200, 200, 200, 400,
				400, 200, 200, 200, 200, 400
		};

		int gap = 50; // short gap between notes

		Runnable playMelody = new Runnable() {
			@Override
			public void run() {
				int delay = 0;

				for (int i = 0; i < notes.length; i++) {
					final int note = notes[i];
					final int duration = durations[i];

					scheduler.schedule(() -> {
						buzzer.on(note);
						scheduler.schedule(() -> buzzer.off(), duration, TimeUnit.MILLISECONDS);
					}, delay, TimeUnit.MILLISECONDS);

					delay += duration + gap;
				}

				// Schedule the next loop after this sequence finishes
				scheduler.schedule(this, delay, TimeUnit.MILLISECONDS);
			}
		};

		scheduler.execute(playMelody);
	}

	public void moveIntoAlarm() {
		displayAlarm();
		turnOnAlarm();
	}

	public void turnOffAlarm() {
		stopBuzzer();
	}

	public void turnOnAlarm() {
		playFurEliseFull();
	}

	public void turnOffLed() {
		ledButton.off();
	}

	public void turnOnLed() {
		ledButton.on();
	}
}
