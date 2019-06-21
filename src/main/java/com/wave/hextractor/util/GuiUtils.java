package com.wave.hextractor.util;

import javax.swing.JOptionPane;

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
	 * @param title
	 * @param message
	 * @return true if accepted.
	 */
	public static final boolean confirmActionAlert(String title, String message) {
		return JOptionPane.showConfirmDialog(null, message, title,
				JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION;
	}
}
