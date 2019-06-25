package com.wave.hextractor.util;

import javax.swing.*;

/**
 * Class for gui utilities.
 */
public class GuiUtils {

	/**
	 * Instantiates a new gui utils.
	 */
	private GuiUtils() {
	}

	/**
	 * Shows a confirmation dialog for the title and message passed.
	 * @return true if accepted.
	 */
	public static boolean confirmActionAlert(String title, String message) {
		return JOptionPane.showConfirmDialog(null, message, title,
				JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION;
	}
}
