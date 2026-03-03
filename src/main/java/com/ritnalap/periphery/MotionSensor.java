/**
 * Grove - mini PIR motion sensor
 * Grove - mini PIR motion sensorallows you to sense motion, usually human movement in
 * its range. Simply connect it to Grove - Base shield and program it, when
 *
 * @author  DIR@PMHS
 * @date    09/01/2025
 *
 */
package com.ritnalap.periphery;

import com.pi4j.Pi4J;
import com.pi4j.context.Context;
import com.pi4j.io.gpio.digital.DigitalInput;
import com.pi4j.io.gpio.digital.DigitalState;
import com.pi4j.io.gpio.digital.DigitalStateChangeListener;

public class MotionSensor {
	private int PIN;
	private Context pi4j;
	DigitalInput motionSensor;

	private Runnable onMotion;
	private Runnable onNoMotion;
	private DigitalState lastState = DigitalState.LOW;

	/**
	 * Klassenkonstruktor
	 * <p>
	 * Erzeugt ein Objekt der Klasse MotionSensor im übergeben pi4j-Context
	 *
	 * @param pi4j pi4j-Context
	 * @param iPin Pin an welchem der MotionSensor angeschlossen ist.
	 */
	public MotionSensor(Context pi4j, int iPin) {
		this.pi4j = pi4j;
		PIN = iPin;
		var motionConfig = DigitalInput.newConfigBuilder(pi4j).id("MotionSensor" + iPin)
				.name("MotionSensor" + iPin)
				.address(PIN)
				.provider("pigpio-digital-input");

		motionSensor = pi4j.create(motionConfig);
		motionSensor.addListener(digitalStateChangeEvent -> {
			DigitalState state = motionSensor.state();
			switch (state) {
				case HIGH:
					if (onMotion != null) {
						onMotion.run();
					}
					break;
				case LOW:
					if (onNoMotion != null) {
						onNoMotion.run();
					}
					break;
			}
		});

	}

	/**
	 * Prüft ob der Bewegung erkannt ist
	 * 
	 * @return boolean true wenn BEwegung erkannt ist
	 */
	public boolean isUp() {
		boolean ret = false;
		if (motionSensor.state() == DigitalState.HIGH) {
			ret = true;
		}
		return ret;
	}

	/**
	 * Fügt den übergebenen Event Handler an dein Eingang
	 *
	 * @param listener DigitalStateChangeListener
	 */
	public void addListener(DigitalStateChangeListener listener) {
		motionSensor.addListener(listener);
	}

	/**
	 * Setzt ein Eventhandler für Event Bewegung
	 *
	 * @param task Event handler to call or null to disable
	 */
	public void onNoMotion(Runnable task) {
		onNoMotion = task;
	}

	/**
	 * Setzt ein Eventhandler für Event keine Bewegung
	 *
	 * @param task Event handler to call or null to disable
	 */
	public void onMotion(Runnable task) {
		onMotion = task;
	}
}
