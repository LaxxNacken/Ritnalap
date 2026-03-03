/**
 * Grove - Buzzer
 * This module uses piezo buzzer as the main component, it can produce high pitch tone
 * while it is connected to digital output and logic level set to High, otherwise it can
 * produce various tones according to the frequencies generated from the Analog PWM
 * output that connected to it. (note: the frequency range that normal human ear can
 * distinguish is between 20 Hz and 20kHz.)
 *
 * @author  DIR@PMHS
 * @date    21/01/2025
 *
 */
package com.ritnalap.periphery;

import com.pi4j.Pi4J;
import com.pi4j.context.Context;
import com.pi4j.io.pwm.Pwm;
import com.pi4j.io.pwm.PwmConfig;
import com.pi4j.io.pwm.PwmType;

public class Buzzer {
	private int PIN;
	private Context pi4j;
	private Pwm buzzer;

	/**
	 * Klassenkonstruktor
	 * <p>
	 * Erzeugt ein Objekt der Klasse Buzzer im übergeben pi4j-Context
	 *
	 * @param pi4j         pi4j-Context
	 * @param iPin         Pin an welchem die Buzzer angeschlossen ist.
	 * @param bHardwarePwm true= Hardware PWM false=Software PWM
	 */
	public Buzzer(Context pi4j, int iPin, boolean bHardwarePwm) {
		this.pi4j = pi4j;
		PIN = iPin;
		PwmConfig buzzerConfig;
		if (bHardwarePwm) {
			buzzerConfig = Pwm.newConfigBuilder(pi4j)
					.id("buzzer" + iPin)
					.name("Buzzer" + iPin)
					.address(PIN)
					.pwmType(PwmType.HARDWARE)
					.provider("pigpio-pwm")
					.initial(0)
					.shutdown(0)
					.build();
		} else {
			buzzerConfig = Pwm.newConfigBuilder(pi4j)
					.id("buzzer" + iPin)
					.name("Buzzer" + iPin)
					.address(PIN)
					.pwmType(PwmType.HARDWARE)
					.provider("pigpio-pwm")
					.initial(0)
					.shutdown(0)
					.build();
		}
		buzzer = this.pi4j.create(buzzerConfig);
	}

	/**
	 * Schaltet den Buzzer mit der übergebenen Frequenz an
	 *
	 * @param iFrequency Frequenz für die PWM
	 */
	public void on(int iFrequency) {
		buzzer.on(50, iFrequency);
	}

	/**
	 * Schaltet den Buzzer aus
	 */
	public void off() {
		buzzer.off();
	}
}
