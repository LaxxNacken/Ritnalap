/**
 * Grove - 16 x 2 LCD (White on Blue)
 * This Grove – 162 LCD module is a 16 Characters 2 Lines LCD display, it uses I2C bus
 * interface to communicate with the development board, thus these will reduce the pin
 * header from 10 to 2 which is very convenient for the Grove system. This LCD display
 * module also supports customise characters, you can create and display heart symbol or
 * stick-man on this LCD module through a simple coding configuration.
 *
 * @author  DIR@PMHS
 * @date    07/01/2025
 *
 */
package com.ritnalap.periphery;

import com.pi4j.Pi4J;
import com.pi4j.context.Context;
import com.pi4j.io.i2c.I2C;
import com.pi4j.io.i2c.I2CConfig;
import com.pi4j.io.i2c.I2CProvider;

public class Lcd {

	private Context pi4j;
	private I2C i2cLcd;

	// Grove Base Hat for Raspberry Pi ConfigFile
	private static final int I2C_DEV = 1;
	private static final int I2C_LCD = 0x3e;

	/**
	 * Klassenkonstruktor
	 * <p>
	 * Erzeugt ein Objekt der Klasse Lcd im übergeben pi4j-Context
	 *
	 * @param pi4j pi4j-Context
	 */
	public Lcd(Context pi4j) {
		this.pi4j = pi4j;
		try {
			// I2C Init
			I2CProvider i2CProvider = pi4j.provider("linuxfs-i2c");
			I2CConfig i2cConfig = I2C.newConfigBuilder(pi4j).id("LCD").bus(I2C_DEV).device(I2C_LCD).build();
			i2cLcd = i2CProvider.create(i2cConfig);

			// LCD Init
			// start sequence??
			writeBlockData(LCD_COMMAND, LCD_START);
			Thread.sleep(4500);
			writeBlockData(LCD_COMMAND, LCD_START);
			Thread.sleep(100);
			writeBlockData(LCD_COMMAND, LCD_START);
			// Initialize function set
			writeBlockData(LCD_COMMAND, LCD_INIT_FUNCTION);
			// Initialize display control
			writeBlockData(LCD_COMMAND,
					(byte) (LCD_DISPLAYCONTROL | LCD_DISPLAYON | LCD_CURSOROFF | LCD_BLINKOFF));
			// Initialize entry mode set
			writeBlockData(LCD_COMMAND, (byte) (LCD_ENTRYMODESET | LCD_ENTRYINC | LCD_ENTRYNOSHIFT));
			// Initialize display
			writeBlockData(LCD_COMMAND, LCD_CLEARDISPLAY);
			Thread.sleep(2);
			writeBlockData(LCD_COMMAND, LCD_RETURNHOME);
			Thread.sleep(2);

		} catch (Exception ex) {
			System.out.println("Lcd - Init Fehler: " + ex.getMessage());
		}
	}

	/**
	 * schreibt einen Block auf dem I2C
	 *
	 * @param cmd  zu schreibendes Kommando
	 * @param data zu schreibende Daten
	 */
	private void writeBlockData(byte cmd, byte data) {
		try {
			i2cLcd.writeRegister(cmd, data);
			Thread.sleep(0, 100000);
		} catch (Exception ex) {
			System.out.println("Lcd - writeBlockData Fehler: " + ex.getMessage());
		}
	}

	/**
	 * Löscht den Text auf dem LCD
	 */
	public void clearText() {
		try {
			writeBlockData(LCD_COMMAND, LCD_CLEARDISPLAY);
			Thread.sleep(2);
		} catch (Exception ex) {
			System.out.println(
					"Lcd - clearText Fehler: Error while trying to initialise " + ex.getMessage());
		}
	}

	public void writeLine(String text, int line) {
		while (text.length() < 16) {
			text += " ";
		}
		displayText(text, line, 0);
	}

	/**
	 * Zeigt den Text auf dem Display an
	 *
	 * @param text zu schreibender Text
	 * @param line Zeile
	 * @param pos  Stelle
	 */
	public void displayText(String text, int line, int pos) {
		int len;

		pos &= 0x0f;
		line &= 0x01;
		if (line == 1)
			line = 0x00;
		else
			line = 0x40;

		writeBlockData(LCD_COMMAND, (byte) (pos | 0x80 | line));

		len = text.length();
		if (len > (16 - pos))
			len = 16 - pos;
		for (int i = 0; i < len; i++) {
			writeBlockData(LCD_DATA, (byte) text.charAt(i));
		}
	}

	// commands
	private final byte LCD_CLEARDISPLAY = (byte) 0x01;
	private final byte LCD_RETURNHOME = (byte) 0x02;
	private final byte LCD_ENTRYMODESET = (byte) 0x04;
	private final byte LCD_DISPLAYCONTROL = (byte) 0x08;
	private final byte LCD_CURSORSHIFT = (byte) 0x10;
	private final byte LCD_FUNCTIONSET = (byte) 0x20;
	private final byte LCD_SETCGRAMADDR = (byte) 0x40;
	private final byte LCD_SETDDRAMADDR = (byte) 0x80;

	// flags for display entry mode: LCD_ENTRYMODESET
	private final byte LCD_ENTRYSHIFT = (byte) 0x01;
	private final byte LCD_ENTRYNOSHIFT = (byte) 0x00;
	private final byte LCD_ENTRYINC = (byte) 0x02;
	private final byte LCD_ENTRYDEC = (byte) 0x00;

	// flags for display on/off control: LCD_DISPLAYCONTROL
	private final byte LCD_DISPLAYON = (byte) 0x04;
	private final byte LCD_DISPLAYOFF = (byte) 0x00;
	private final byte LCD_CURSORON = (byte) 0x02;
	private final byte LCD_CURSOROFF = (byte) 0x00;
	private final byte LCD_BLINKON = (byte) 0x01;
	private final byte LCD_BLINKOFF = (byte) 0x00;

	// flags for display/cursor shift: LCD_CURSORSHIFT
	private final byte LCD_DISPLAYMOVE = (byte) 0x08;
	private final byte LCD_CURSORMOVE = (byte) 0x00;
	private final byte LCD_MOVERIGHT = (byte) 0x04;
	private final byte LCD_MOVELEFT = (byte) 0x00;

	// flags for function set: LCD_FUNCTIONSET
	private final byte LCD_8BITMODE = (byte) 0x10;
	private final byte LCD_4BITMODE = (byte) 0x00;
	private final byte LCD_2LINE = (byte) 0x08;
	private final byte LCD_1LINE = (byte) 0x00;
	private final byte LCD_5x10DOTS = (byte) 0x04;
	private final byte LCD_5x8DOTS = (byte) 0x00;

	private final byte LCD_COMMAND = (byte) 0x80;
	private final byte LCD_DATA = (byte) 0x40;
	private final byte LCD_INIT_1804 = (byte) (LCD_FUNCTIONSET | LCD_8BITMODE | LCD_2LINE | LCD_5x8DOTS);

	private final byte LCD_START = (byte) LCD_INIT_1804;
	private final byte LCD_INIT_FUNCTION = (byte) LCD_INIT_1804;
}
