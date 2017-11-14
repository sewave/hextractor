package com.wave.hextractor;

import java.util.ArrayList;
import java.util.List;

public class OffsetEntry implements Comparable<OffsetEntry> {
	public int start;
	public int getStart() {
		return start;
	}
	public void setStart(int start) {
		this.start = start;
	}
	public int getEnd() {
		return end;
	}
	public void setEnd(int end) {
		this.end = end;
	}
	public List<String> getEndChars() {
		return endChars;
	}
	public void setEndChars(List<String> endChars) {
		this.endChars = endChars;
	}

	public int end;
	public List<String> endChars = new ArrayList<String>();
	public OffsetEntry() {
	}

	public OffsetEntry(int start, int end, List<String> endChars) {
		this.start = start;
		this.end = end;
		this.endChars = endChars;
	}

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
	public String toString() {
		return String.valueOf(Constants.ADDR_CHAR) + toEntryString();
	}

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
	 * Comment in style: START-END:BYTES
	 * @return
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

	public String getHexString(byte[] bytes) {
		StringBuilder sb = new StringBuilder();
		for(int i = this.getStart(); i <= this.getEnd(); i++) {
			sb.append(String.format(Constants.HEX_16_FORMAT, bytes[i])).append(Constants.SPACE_STR);
		}
		return sb.toString();
	}

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

	public Object getHexTarget() {
		StringBuilder sb = new StringBuilder(Constants.ADDR_STR);
		sb.append(Utils.intToHexString(start, Constants.HEX_ADDR_SIZE));
		sb.append(Constants.OFFSET_LENGTH_SEPARATOR);
		sb.append(Utils.intToHexString(end, Constants.HEX_ADDR_SIZE));
		return sb.toString();
	}
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
	 * El bloque insertado no puede solaparse con otros, de manera que si estan
	 * totalmente dentro, se los come y si están una parte dentro y otra fuera
	 * se expande hacia los limites de esa entrada.
	 * @param offEntries
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

}
