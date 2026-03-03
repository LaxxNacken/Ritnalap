/**
 * Grove - Red LED Button
 * The Grove - LED Button is composed of Grove - Yellow Button, Grove - Blue LED
 * Button and Grove - Red LED Button. This button is stable and reliable with a 100 000
 * times long life. With the build-in LED, you can apply it to many interesting projects, it is
 * really useful to use the LED to show the status of the button.
 *
 * @author  DIR@PMHS
 * @date    09/01/2025
 *
 */
package com.ritnalap.periphery;

import com.pi4j.Pi4J;
import com.pi4j.context.Context;
import com.pi4j.io.gpio.digital.DigitalInput;
import com.pi4j.io.gpio.digital.DigitalOutput;
import com.pi4j.io.gpio.digital.DigitalState;
import com.pi4j.io.gpio.digital.PullResistance;
import com.pi4j.io.gpio.digital.DigitalStateChangeListener;

public class LedButton {
	private int PIN_LED;
	private int PIN_Button;
	private Context pi4j;
	private DigitalInput button;
	private DigitalOutput led;
	private boolean bState;

	private Runnable onDown;
	private Runnable onUp;

	/**
	 * Klassenkonstruktor
	 * <p>
	 * Erzeugt ein Objekt der Klasse Button im übergeben pi4j-Context
	 *
	 * @param pi4j       pi4j-Context
	 * @param iPinLed    Pin an welchem die LED angeschlossen ist.
	 * @param iPinButton Pin an welchem die Button angeschlossen ist.
	 */
	public LedButton(Context pi4j, int iPinLed, int iPinButton) {
		this.pi4j = pi4j;
		PIN_LED = iPinLed;
		PIN_Button = iPinButton;
		var buttonConfig = DigitalInput.newConfigBuilder(pi4j)
				.id("button" + iPinLed)
				.name("Button" + iPinLed)
				.address(PIN_Button)
				.pull(PullResistance.PULL_DOWN)
				.debounce(300L)
				.provider("pigpio-digital-input");

		button = pi4j.create(buttonConfig);
		button.addListener(digitalStateChangeEvent -> {
			DigitalState state = getState();
			switch (state) {
				case HIGH:
					if (onDown != null) {
						onDown.run();
					}
					break;
				case LOW:
					if (onUp != null) {
						onUp.run();
					}
					break;
			}
		});

		var ledConfig = DigitalOutput.newConfigBuilder(pi4j)
				.id("led")
				.name("Led")
				.address(PIN_LED)
				.shutdown(DigitalState.LOW)
				.initial(DigitalState.LOW)
				.provider("pigpio-digital-output");
		led = pi4j.create(ledConfig);
		bState = false;
	}

	/**
	 * Prüft ob der Button losgelassen ist
	 * 
	 * @return boolean true wenn Button nicht gedrückt
	 */
	public boolean isUp() {
		return getState() == DigitalState.LOW;
	}

	/**
	 * Prüft ob der Button gedrückt ist
	 * 
	 * @return boolean true wenn Button gedrückt
	 */
	public boolean isDown() {
		return getState() == DigitalState.HIGH;
	}

	/**
	 * liefert den Status des Buttons zurück
	 * 
	 * @return state DigitalState des Buttons
	 */
	public DigitalState getState() {
		DigitalState state = DigitalState.UNKNOWN;
		if (button.state() == DigitalState.HIGH) {
			state = DigitalState.LOW;
		} else if (button.state() == DigitalState.LOW) {
			state = DigitalState.HIGH;
		}
		return state;
	}

	/**
	 * Fügt den übergebenen Event Handler an dein Eingang
	 * Achtung OnUp/OnDown wird überschrieben.
	 *
	 * @param listener DigitalStateChangeListener
	 */
	public void addListener(DigitalStateChangeListener listener) {
		button.addListener(listener);
		onDown = null;
		onUp = null;
	}

	/**
	 * Setzt ein Eventhandler für Event onDown
	 * ACHTUNG: Nur wenn nicht mit addListener ein alternativer gesetzt wird.
	 *
	 * @param task Event handler to call or null to disable
	 */
	public void onDown(Runnable task) {
		onDown = task;
	}

	/**
	 * Setzt ein Eventhandler für Event onUp
	 * ACHTUNG: Nur wenn nicht mit addListener ein alternativer gesetzt wird.
	 *
	 * @param task Event handler to call or null to disable
	 */
	public void onUp(Runnable task) {
		onUp = task;
	}

	/**
	 * Schaltet die LED an.
	 */
	public void on() {
		led.high();
		bState = true;
	}

	/**
	 * Schaltet die LED aus.
	 */
	public void off() {
		led.low();
		bState = false;
	}

	/**
	 * Wechselt LED zwischen an und aus.
	 */
	public void toggle() {
		if (bState) {
			off();
		} else {
			on();
		}
	}

	/**
	 * Liefert das DigitalOutput objekt der LED
	 * 
	 * @return led DigitalOutput Objekt der Spezifischen LED
	 */
	public DigitalOutput getLed() {
		return led;
	}
}
