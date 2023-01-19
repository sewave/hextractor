package com.wave.hextractor.gui

import java.io.File
import java.io.Serializable
import javax.swing.filechooser.FileFilter

/**
 * The Class SimpleFilter.
 */
class SimpleFilter : FileFilter, Serializable {
    private var exts: List<String>
    private var message: String

    constructor(ext: String, message: String) {
        exts = listOf(ext)
        this.message = message
    }

    constructor(exts: List<String>, message: String) {
        this.exts = exts
        this.message = message
    }

    override fun accept(selectedFile: File): Boolean {
        var isValid = selectedFile.isDirectory
        for (ext in exts) {
            isValid = isValid or selectedFile.name.endsWith(ext)
        }
        return isValid
    }

    override fun getDescription(): String {
        return message
    }

    companion object {
        private const val serialVersionUID = -1735785449456394453L
    }
}
