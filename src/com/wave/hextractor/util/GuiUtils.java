package com.wave.hextractor.util;

import javax.swing.JOptionPane;

/**
 * Class for gui utilities.
 */
public class GuiUtils {

	/**
	 * Shows a confirmation dialog for the title and message passed.
	 *
	 * @param title
	 *            .
	 * @param message
	 *            .
	 * @return true if accepted.
	 */
	public static final boolean confirmActionAlert(String title, String message) {
		boolean accepted = false;
		int response = JOptionPane.showConfirmDialog(null, message, title,
				JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
		if (response == JOptionPane.YES_OPTION) {
			accepted = true;
		}
		return accepted;
	}
}
