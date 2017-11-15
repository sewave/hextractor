package com.wave.hextractor.gui;

import java.io.File;

import javax.swing.filechooser.FileFilter;

/**
 * The Class SimpleFilter.
 */
public class SimpleFilter extends FileFilter {

	/** The ext. */
	String ext;

	/** The message. */
	String message;

	/**
	 * Instantiates a new simple filter.
	 *
	 * @param ext the ext
	 * @param message the message
	 */
	public SimpleFilter(String ext, String message) {
		this.ext = ext;
		this.message = message;
	}

	/* (non-Javadoc)
	 * @see javax.swing.filechooser.FileFilter#accept(java.io.File)
	 */
	@Override
	public boolean accept(File selectedFile) {
		return selectedFile.getName().endsWith(ext) || selectedFile.isDirectory();
	}

	/* (non-Javadoc)
	 * @see javax.swing.filechooser.FileFilter#getDescription()
	 */
	@Override
	public String getDescription() {
		return message;
	}
}
