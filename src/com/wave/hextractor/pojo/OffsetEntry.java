package com.wave.hextractor.pojo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.wave.hextractor.util.Constants;
import com.wave.hextractor.util.Utils;

/**
 * Offset pair entry.
 * @author slcantero
 */
public class OffsetEntry implements Comparable<OffsetEntry> {

	/** The start. */
	public int start;

	/**
	 * Gets the start.
	 *
	 * @return the start
	 */
	public int getStart() {
		return start;
	}

	/**
	 * Sets the start.
	 *
	 * @param start the new start
	 */
	public void setStart(int start) {
		this.start = start;
	}

	/**
	 * Gets the end.
	 *
	 * @return the end
	 */
	public int getEnd() {
		return end;
	}

	/**
	 * Sets the end.
	 *
	 * @param end the new end
	 */
	public void setEnd(int end) {
		this.end = end;
	}

	/**
	 * Gets the end chars.
	 *
	 * @return the end chars
	 */
	public List<String> getEndChars() {
		return endChars;
	}

	/**
	 * Sets the end chars.
	 *
	 * @param endChars the new end chars
	 */
	public void setEndChars(List<String> endChars) {
		this.endChars = endChars;
	}

	/** The end. */
	public int end;

	/** The end chars. */
	public List<String> endChars = new ArrayList<String>();

	/**
	 * Instantiates a new offset entry.
	 */
	public OffsetEntry() {
	}

	/**
	 * Instantiates a new offset entry.
	 *
	 * @param start the start
	 * @param end the end
	 * @param endChars the end chars
	 */
	public OffsetEntry(int start, int end, List<String> endChars) {
		this.start = start;
		this.end = end;
		this.endChars = endChars;
	}

	/**
	 * Instantiates a new offset entry.
	 *
	 * @param asciiString the ascii string
	 */
	public OffsetEntry(String asciiString) {
		String[] values = (asciiString.substring(1)).split(String.valueOf(Constants.OFFSET_CHAR_SEPARATOR));
		start = Integer.parseInt(values[0], Constants.HEX_RADIX);
		if(values.length > 1) {
			end = Integer.parseInt(values[1], Constants.HEX_RADIX);
			if(values.length > 2) {
				for(int i = 2; i < values.length; i++ ) {
					endChars.add(values[i]);
				}
			}
		}
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return String.valueOf(Constants.ADDR_CHAR) + toEntryString();
	}

	/**
	 * To entry string.
	 *
	 * @return the string
	 */
	public String toEntryString() {
		StringBuffer sb = new StringBuffer();
		sb.append(Utils.fillLeft(Integer.toHexString(start), Constants.HEX_ADDR_SIZE).toUpperCase())
		.append(Constants.OFFSET_CHAR_SEPARATOR).append(Utils.intToHexString(end, Constants.HEX_ADDR_SIZE));
		for(String endChar : endChars) {
			sb.append(Constants.OFFSET_CHAR_SEPARATOR);
			sb.append(Utils.fillLeft(endChar, Constants.HEX_SIZE).toUpperCase());
		}
		return sb.toString();
	}

	/**
	 * Comment in style: START-END:BYTES.
	 *
	 * @return the hex comment
	 */
	public String getHexComment() {
		StringBuilder sb = new StringBuilder();
		sb.append(Constants.COMMENT_LINE);
		sb.append(Utils.intToHexString(start, Constants.HEX_ADDR_SIZE));
		sb.append(Constants.OFFSET_CHAR_SEPARATOR);
		sb.append(Utils.intToHexString(end, Constants.HEX_ADDR_SIZE));
		sb.append(Constants.OFFSET_LENGTH_SEPARATOR);
		sb.append(Utils.intToHexString(end - start + 1, Constants.HEX_ADDR_SIZE));
		return sb.toString();
	}

	/**
	 * Gets the hex string.
	 *
	 * @param bytes the bytes
	 * @return the hex string
	 */
	public String getHexString(byte[] bytes) {
		StringBuilder sb = new StringBuilder();
		for(int i = this.getStart(); i <= this.getEnd(); i++) {
			sb.append(String.format(Constants.HEX_16_FORMAT, bytes[i])).append(Constants.SPACE_STR);
		}
		return sb.toString();
	}

	/**
	 * Split the entry into minMaxLength chunks.
	 *
	 * @param minMaxLength the min max length
	 * @param bytes the bytes
	 * @return the list
	 */
	public List<OffsetEntry> split(int minMaxLength, byte[] bytes) {
		List<OffsetEntry> res = new ArrayList<OffsetEntry>();
		int readedBytes = 0;
		int pointerStart = start;
		for(int pointer = start; pointer <= end; pointer++) {
			if(readedBytes >= minMaxLength && endChars.contains(
					String.format(Constants.HEX_16_FORMAT, bytes[pointer]))) {
				res.add(new OffsetEntry(pointerStart, pointer, endChars));
				pointerStart = pointer + 1;
				readedBytes = 0;
			}
			readedBytes++;
		}
		if(pointerStart < end) {
			res.add(new OffsetEntry(pointerStart, end, endChars));
		}
		return res;
	}

	/**
	 * Gets the hex target.
	 *
	 * @return the hex target
	 */
	public String getHexTarget() {
		StringBuilder sb = new StringBuilder(Constants.ADDR_STR);
		sb.append(Utils.intToHexString(start, Constants.HEX_ADDR_SIZE));
		sb.append(Constants.OFFSET_LENGTH_SEPARATOR);
		sb.append(Utils.intToHexString(end, Constants.HEX_ADDR_SIZE));
		return sb.toString();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(OffsetEntry entry) {
		int res = 0;
		//Returns a negative integer, zero, or a positive integer as this object
		//is less than, equal to, or greater than the specified object.
		if(this.getStart() < entry.getStart()) {
			res = -1;
		}
		if(this.getStart() > entry.getStart()) {
			res = 1;
		}
		return res;
	}

	/**
	 * The inserted block can't touch others, so if they are totally inside</br>
	 * it eats them and if they are mid inside mid outside it expands.
	 *
	 * @param offEntries the off entries
	 */
	public void mergeInto(List<OffsetEntry> offEntries) {
		List<OffsetEntry> result = new ArrayList<OffsetEntry>();
		for(OffsetEntry oEntry : offEntries) {
			boolean startInside = (oEntry.getStart() >= this.getStart() && oEntry.getStart() <= this.getEnd());
			boolean endInside = (oEntry.getEnd() >= this.getStart() && oEntry.getEnd() <= this.getEnd());
			//Si está fuera de rango, se incluye en el resultado
			if(!startInside && !endInside) {
				result.add(oEntry);
			}
			else {
				//Uno fuera y uno dentro
				if((!startInside || !endInside) && (startInside || endInside)) {
					if(!startInside) {
						this.setStart(oEntry.getStart());
					}
					if(!endInside) {
						this.setEnd(oEntry.getEnd());
					}
				}
			}
			//Si ambos estan dentro, el offset se pierde
		}
		result.add(this);

		//Como iteramos sobre ella, es mejor actualizarla al final
		offEntries.clear();
		offEntries.addAll(result);
	}
	
	/**
	 * Gets the offset length.
	 *
	 * @return the length
	 */
	public int getLength() {
		return end - start;
	}

	/**
	 * Creates a offset entry from a hex range (FF as ending char)
	 * @param string
	 * @return
	 */
	public static OffsetEntry fromHexRange(String string) {
		int[] numbers = Utils.hexStringListToIntList(string.split(Constants.OFFSET_LENGTH_SEPARATOR));
		return new OffsetEntry(numbers[0], numbers[1], Arrays.asList("FF"));
	}

}
