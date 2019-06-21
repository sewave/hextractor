package com.wave.hextractor.gui;

import java.io.File;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import javax.swing.filechooser.FileFilter;

/**
 * The Class SimpleFilter.
 */
public class SimpleFilter extends FileFilter implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -1735785449456394453L;

	/** The exts. */
	private List<String> exts;

	/** The message. */
	private String message;

	/**
	 * Instantiates a new simple filter.
	 *
	 * @param ext the ext
	 * @param message the message
	 */
	public SimpleFilter(String ext, String message) {
		this.exts = Arrays.asList(ext);
		this.message = message;
	}

	/**
	 * Instantiates a new simple filter.
	 *
	 * @param exts the exts
	 * @param message the message
	 */
	public SimpleFilter(List<String> exts, String message) {
		this.exts = exts;
		this.message = message;
	}

	/* (non-Javadoc)
	 * @see javax.swing.filechooser.FileFilter#accept(java.io.File)
	 */
	@Override
	public boolean accept(File selectedFile) {
		boolean isValid = selectedFile.isDirectory();
		for(String ext : exts) {
			isValid |= selectedFile.getName().endsWith(ext);
		}
		return isValid;
	}

	/* (non-Javadoc)
	 * @see javax.swing.filechooser.FileFilter#getDescription()
	 */
	@Override
	public String getDescription() {
		return message;
	}
}
