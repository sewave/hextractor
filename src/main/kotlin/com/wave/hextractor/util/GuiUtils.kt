package com.wave.hextractor.util

import javax.swing.JOptionPane

/**
 * Class for gui utilities.
 */
object GuiUtils {
    /**
     * Shows a confirmation dialog for the title and message passed.
     * @return true if accepted.
     */
    fun confirmActionAlert(title: String?, message: String?): Boolean {
        return JOptionPane.showConfirmDialog(
            null,
            message,
            title,
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        ) == JOptionPane.YES_OPTION
    }
}
