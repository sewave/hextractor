package com.wave.hextractor.pojo;

import com.wave.hextractor.object.HexTable;
import com.wave.hextractor.util.Utils;

/**
 * The Class TableSearchResult.
 */
public class TableSearchResult {

	/** The hex table. */
	private HexTable hexTable;

	/** The offset. */
	private Integer offset;

	/** The word. */
	private String word;

	/**
	 * Gets the hex table.
	 *
	 * @return the hex table
	 */
	public HexTable getHexTable() {
		return hexTable;
	}

	/**
	 * Sets the hex table.
	 *
	 * @param hexTable the new hex table
	 */
	public void setHexTable(HexTable hexTable) {
		this.hexTable = hexTable;
	}

	/**
	 * Gets the offset.
	 *
	 * @return the offset
	 */
	public Integer getOffset() {
		return offset;
	}

	/**
	 * Sets the offset.
	 *
	 * @param offset the new offset
	 */
	public void setOffset(Integer offset) {
		this.offset = offset;
	}

	/**
	 * Gets the word.
	 *
	 * @return the word
	 */
	public String getWord() {
		return word;
	}

	/**
	 * Sets the word.
	 *
	 * @param word the new word
	 */
	public void setWord(String word) {
		this.word = word;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return word + " @ 0x" + Utils.intToHexString(offset, 6) + " Table=" + hexTable.toSelectionString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (offset == null ? 0 : offset.hashCode());
		result = prime * result + (word == null ? 0 : word.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		TableSearchResult other = (TableSearchResult) obj;
		if (offset == null) {
			if (other.offset != null) {
				return false;
			}
		}
		else if (!offset.equals(other.offset)) {
			return false;
		}
		if (word == null) {
			return other.word == null;
		}
		else return word.equals(other.word);
	}

}
