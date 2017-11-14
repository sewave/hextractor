package com.wave.hextractor;

public class TableSearchResult {
	private HexTable hexTable;
	private Integer offset;
	private String word;
	public HexTable getHexTable() {
		return hexTable;
	}

	public void setHexTable(HexTable hexTable) {
		this.hexTable = hexTable;
	}
	public Integer getOffset() {
		return offset;
	}
	public void setOffset(Integer offset) {
		this.offset = offset;
	}
	public String getWord() {
		return word;
	}
	public void setWord(String word) {
		this.word = word;
	}

	@Override
	public String toString() {
		return word + " @ 0x" + Utils.intToHexString(offset, 6) + " Table=" + hexTable.toSelectionString();
	}
}
