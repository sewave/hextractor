package com.wave.hextractor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class HexTable {
	private static final String TABLE_KEY_A = "A";
	private static final String TABLE_KEY_LOWA = "a";
	private static final String TABLE_KEY_ZERO = "0";
	private static final Map<String, String> SPANISH_CHARS = new HashMap<String, String>();
	static {
		SPANISH_CHARS.put("a", "á");
		SPANISH_CHARS.put("e", "é");
		SPANISH_CHARS.put("i", "í");
		SPANISH_CHARS.put("o", "ó");
		SPANISH_CHARS.put("u", "ú");
		SPANISH_CHARS.put("n", "ñ");
		SPANISH_CHARS.put("!", "¡");
		SPANISH_CHARS.put("?", "¿");
		SPANISH_CHARS.put("A", "Á");
		SPANISH_CHARS.put("E", "É");
		SPANISH_CHARS.put("I", "Í");
		SPANISH_CHARS.put("O", "Ó");
		SPANISH_CHARS.put("U", "Ú");
		SPANISH_CHARS.put("N", "Ñ");
	}

	private Map<Byte, String> table;
	private Map<String, Byte> reversed;

	public String toString(byte aByte) {
		String res;
		if(table.containsKey(aByte)) {
			res = table.get(Byte.valueOf(aByte));
		}
		else {
			res = Constants.HEX_VIEWER_UNKNOWN_CHAR;
		}
		return res;
	}

	public String toSelectionString() {
		StringBuffer res = new StringBuffer();
		if(reversed.containsKey(TABLE_KEY_A)) {
			res.append(TABLE_KEY_A).append(Constants.OFFSET_LENGTH_SEPARATOR).append(Constants.SPACE_STR);
			res.append(Utils.intToHexString(reversed.get(TABLE_KEY_A), Constants.HEXSIZE_8BIT_VALUE)).append(Constants.SPACE_STR);
		}
		if(reversed.containsKey(TABLE_KEY_LOWA)) {
			res.append(TABLE_KEY_LOWA).append(Constants.OFFSET_LENGTH_SEPARATOR).append(Constants.SPACE_STR);
			res.append(Utils.intToHexString(reversed.get(TABLE_KEY_LOWA), Constants.HEXSIZE_8BIT_VALUE)).append(Constants.SPACE_STR);
		}
		if(reversed.containsKey(TABLE_KEY_ZERO)) {
			res.append(TABLE_KEY_ZERO).append(Constants.OFFSET_LENGTH_SEPARATOR).append(Constants.SPACE_STR);
			res.append(Utils.intToHexString(reversed.get(TABLE_KEY_ZERO), Constants.HEXSIZE_8BIT_VALUE)).append(Constants.SPACE_STR);
		}
		return res.toString();
	}

	private void loadLines(List<String> tableLines) {
		table = new HashMap<Byte, String>();
		reversed = new HashMap<String, Byte>();
		for(String s : tableLines) {
			if(!s.contains(Constants.TABLE_SEPARATOR + Constants.SPACE_STR)) {
				s = s.replaceAll(Constants.SPACE_STR, Constants.EMPTY);
			}
			if(s.length() >= 4) {
				boolean isEquals = s.contains(Constants.TABLE_SEPARATOR + Constants.TABLE_SEPARATOR);
				String tablechar;
				String[] items = s.split(Constants.TABLE_SEPARATOR);
				if(isEquals) {
					tablechar = Constants.TABLE_SEPARATOR;
				}
				else {
					tablechar = items[1].substring(0, 1);
				}
				if(Constants.RESERVED_CHARS.contains(tablechar)) {
					if(Utils.isDebug()) {
						System.out.println("WARNING - Table char \"" + tablechar + "\" will not be used beacause it is reserved.");
					}
				}
				else {
					Byte tableCode = Utils.hexStringCharToByte(items[0].toUpperCase());
					table.put(tableCode, tablechar);
					reversed.put(tablechar, tableCode);
				}
			}
		}
	}

	/**
	 * Based in a displacement, reconstruct full table from $20 to $7E (included)
	 * @param displacement
	 * @return
	 */
	public HexTable(int displacement) {
		List<String> tableLines = new ArrayList<String>();
		StringBuffer sb = new StringBuffer();
		byte currChar = (byte) ((Constants.MIN_PRINTABLE_CHAR - displacement) & Constants.MASK_8BIT);
		for(int i = Constants.MIN_PRINTABLE_CHAR; i <= Constants.MAX_PRINTABLE_CHAR; i++) {
			sb.setLength(0);
			sb.append(String.format(Constants.HEX_16_FORMAT, currChar));
			sb.append(Constants.TABLE_SEPARATOR);
			sb.append((char) i);
			sb.append(Constants.S_NEWLINE);
			tableLines.add(sb.toString());
			currChar++;
		}
		loadLines(tableLines);
	}

	public HexTable(List<String> tableLines) {
		loadLines(tableLines);
	}

	public HexTable(String tableFile) throws Exception {
		loadLines(Arrays.asList(FileUtils.getAsciiFile(tableFile).replaceAll(Constants.UTF_8_BOM_BE, Constants.EMPTY).replaceAll(Constants.UTF_8_BOM_LE, Constants.EMPTY).split(String.valueOf(Constants.NEWLINE))));
	}

	public String toAscii(byte[] hexString) {
		StringBuffer sb = new StringBuffer();
		for(byte b : hexString) {
			sb.append(toString(b));
		}
		return sb.toString();
	}

	public String toAscii(byte[] hexString, OffsetEntry entry) {
		StringBuilder sb = new StringBuilder();
		int bytesreaded = 0;
		int bytesreadedStart = 0;
		StringBuilder line = new StringBuilder();
		System.out.println("Extracting [" + Utils.fillLeft(Integer.toHexString(entry.start), Constants.HEX_ADDR_SIZE).toUpperCase() + ":" +
				Utils.fillLeft(Integer.toHexString(entry.end), Constants.HEX_ADDR_SIZE).toUpperCase() + "]");
		sb.append(entry.toString()).append(Constants.NEWLINE);
		for(int i = entry.start; i <= entry.end; i++) {
			Byte hex = Byte.valueOf(hexString[i]);
			bytesreaded++;
			if(table.containsKey(hex)) {
				line.append(table.get(hex));
			}
			if(!table.containsKey(hex) || i == entry.end) {
				String hexStr = String.format(Constants.HEX_16_FORMAT, hexString[i]);
				if(!table.containsKey(hex)) {
					line.append(Constants.HEX_CHAR).append(hexStr).append(Constants.HEX_CHAR);
				}
				if(entry.endChars.contains(hexStr) || i == entry.end) {
					String originalLine = line.toString();
					String numChars = Utils.fillLeft(String.valueOf(originalLine.length()), Constants.LEN_NUM_CHARS);
					String numCharsHex = Utils.fillLeft(String.valueOf(bytesreaded - bytesreadedStart), Constants.LEN_NUM_CHARS);
					sb.append(Constants.COMMENT_LINE).append(Utils.fillLeft(Integer.toHexString(bytesreadedStart), Constants.HEX_ADDR_SIZE).toUpperCase());
					sb.append(Constants.ORG_STR_OPEN).append(originalLine).append(Constants.ORG_STR_CLOSE);
					sb.append(Constants.STR_NUM_CHARS).append(numChars).append(Constants.STR_NUM_CHARS).append(numCharsHex);
					sb.append(Constants.NEWLINE);
					sb.append(line).append(Constants.STR_NUM_CHARS).append(numCharsHex);
					sb.append(Constants.NEWLINE);
					line.setLength(0);
					bytesreadedStart = bytesreaded;
				}
			}
		}
		sb.append(String.valueOf(Constants.MAX_BYTES) + bytesreaded).append(Constants.NEWLINE);
		System.out.println("TOTAL BYTES TO ASCII: " + bytesreaded);
		return sb.toString();
	}

	public byte[] toHex(String string, Map<Integer, Integer> pointerOffsets, OffsetEntry entry) {
		int offset = 0;
		int offsetStart = 0;
		byte[] hex = new byte[string.length()];
		int maxsize = 0;
		boolean end = false;
		char next;
		boolean incomment = false;
		byte hexSpace;
		if(reversed.containsKey(Constants.SPACE_STR)) {
			hexSpace = reversed.get(Constants.SPACE_STR);
		}
		else {
			hexSpace = 0;
		}
		int stringStart = 0;
		for(int i = 0; i <string.length() && !end; i++) {
			next = string.substring(i, i+1).charAt(0);
			if(incomment) {
				if(Constants.NEWLINE == next) {
					incomment = false;
				}
			}
			else {
				switch(next) {
				case Constants.COMMENT_LINE:
					incomment = true;
					break;
				case Constants.MAX_BYTES:
					maxsize = Integer.parseInt(string.substring(i+1, string.length()-1));
					end = true;
					break;
				case Constants.HEX_CHAR:
					String hexchar = string.substring(i+1, i+3);
					i+=3;
					if(Constants.HEX_CHAR!= string.substring(i, i+1).charAt(0)) {
						int j = i - 100;
						if(j < 0) {
							j = 0;
						}
						System.out.println("ERROR! HEX CHAR NOT CLOSED AT: " + i + " -> " + string.substring(j, i+1));
					}
					if(entry.endChars.contains(hexchar)) {
						char nextchar = string.substring(i+1, i+2).charAt(0);
						while(Constants.ADDR_CHAR == nextchar) {
							i++;
							String hexTo = string.substring(i+1, i+1+8);
							System.out.println("INSERTING OFFSET " +
									Utils.fillLeft(Integer.toHexString(offsetStart), Constants.HEX_ADDR_SIZE) + " TO " + hexTo);
							int addrTo = Integer.parseInt(hexTo, Constants.HEX_RADIX);
							if(pointerOffsets.containsKey(addrTo)) {
								System.out.println("ERROR!!! DUPLICATED OFFSET ENTRY");
							}
							pointerOffsets.put(addrTo, offsetStart);
							i+=8;
							nextchar = string.substring(i+1, i+2).charAt(0);
						}
						//Check size
						if(Constants.STR_NUM_CHARS == nextchar) {
							i++;
							//Search end char
							int j = i;
							char testEnd = nextchar;
							while(testEnd != Constants.NEWLINE) {
								j++;
								testEnd = string.substring(j, j+1).charAt(0);
							}
							int length = Integer.valueOf(string.substring(i+1, j));
							if(offset - offsetStart > length-1) {
								System.out.println("ERROR!!! STRING TOO LARGE (" +
										Utils.fillLeft(String.valueOf(offset - offsetStart+1), 4) + " - " +
										Utils.fillLeft(String.valueOf(length), 4) +
										")!!!");
								System.out.println(string.substring(stringStart, i));
								//Para no superar la longitud máxima
								hex = Arrays.copyOf(hex, length);
							}
							else {
								if(offset - offsetStart < length-1) {
									System.out.println("WARNING!!! STRING TOO SMALL (" +
											Utils.fillLeft(String.valueOf(offset - offsetStart+1), 4) + " - " +
											Utils.fillLeft(String.valueOf(length), 4) + ")!!!");
									System.out.println(string.substring(stringStart, i));
									while(offset - offsetStart < length-1) {
										hex[offset++] = hexSpace;
									}
								}
							}
							i+=(j-i)-1;
							stringStart = i+2;
						}
						hex[offset++] = Utils.hexStringCharToByte(hexchar);
						offsetStart = offset;
					}
					else {
						hex[offset++] = Utils.hexStringCharToByte(hexchar);
					}
					break;
				case Constants.STR_NUM_CHARS:
					//Search end char
					int j = i;
					char testEnd = next;
					while(testEnd != Constants.NEWLINE) {
						j++;
						testEnd = string.substring(j, j+1).charAt(0);
					}
					int length = Integer.valueOf(string.substring(i+1, j));
					if(offset - offsetStart - 1 > length-1) {
						System.out.println("ERROR!!! NOENDED STRING TOO LARGE (" +
								Utils.fillLeft(String.valueOf(offset - offsetStart), 4) + " - " +
								Utils.fillLeft(String.valueOf(length), 4) +
								")!!!");
						System.out.println(string.substring(stringStart, i));
					}
					else {
						if(offset - offsetStart - 1 < length-1) {
							while(offset - offsetStart - 1 < length-1) {
								hex[offset++] = hexSpace;
							}
						}
					}
					i+=(j-i)-1;
					stringStart = i+2;
					break;

				case Constants.NEWLINE:
					break;
				default:
					String nextString = String.valueOf(next);
					byte value = hexSpace;
					if(reversed.containsKey(nextString)) {
						value = reversed.get(nextString);
					}
					else {
						System.out.println("WARNING!!! CHARACTER NOT IN TABLE: " + nextString);
						System.out.println(string.substring(stringStart, i));
					}
					hex[offset++] = value;
					break;
				}
			}
		}
		if(offset > maxsize) {
			offset = maxsize;
		}
		//No dejemos que la siguiente cadena empiece tarde
		if(offset < maxsize) {
			System.out.println("WARNING!!! STRING TOO SMALL");
			System.out.println(string.substring(stringStart));
			for(int i = offset; i < maxsize; i++) {
				hex[i] = Constants.PAD_CHAR;
			}
		}
		if(maxsize == 0) {
			maxsize = offset;
		}
		if(Utils.isDebug()) {
			System.out.print("BYTES TO HEX: " + Utils.fillLeft(String.valueOf(offset), 5) + " / " +  Utils.fillLeft(String.valueOf(maxsize), 5));
		}
		return Arrays.copyOf(hex, maxsize);
	}

	enum ENTRIES_STATUS {
		SEARCHING_START_OF_STRING,
		SEARCHING_END_OF_STRING,
		SKIPPING_CHARS,
	};

	public String getAllEntries(byte[] secondFileBytes, int numMinChars, int numIgnoredChars,
			List<String> endCharsList, String dictFile) throws Exception {
		List<OffsetEntry> offsetEntryList = new ArrayList<OffsetEntry>();
		HashSet<String> dict = new HashSet<String>(Arrays.asList(FileUtils.getAsciiFile(dictFile).split(Constants.S_NEWLINE)));
		int entryStart = 0;
		boolean validString = false;
		StringBuilder word = new StringBuilder();
		StringBuilder sentence = new StringBuilder();
		String dataChar = null;
		List<String> skippedChars = new ArrayList<String>();
		ENTRIES_STATUS status = ENTRIES_STATUS.SEARCHING_START_OF_STRING;
		for(int i = 0; i < secondFileBytes.length - numMinChars; i++) {
			Byte readedByteObj = Byte.valueOf(secondFileBytes[i]);
			String dataCharHex = String.format(Constants.HEX_16_FORMAT, readedByteObj);
			if(table.containsKey(readedByteObj)) {
				dataChar = table.get(readedByteObj);
			}
			else {
				dataChar = null;
			}
			switch(status) {
			case SEARCHING_START_OF_STRING:
				if(dataChar != null) {
					entryStart = i;
					word.setLength(0);
					sentence.setLength(0);
					sentence.append(dataChar);
					word.append(dataChar);
					validString = false;
					status = ENTRIES_STATUS.SEARCHING_END_OF_STRING;
				}
				break;
			case SEARCHING_END_OF_STRING:
				if(dataChar != null) {
					sentence.append(dataChar);
					word.append(dataChar);
				}
				else {
					if(Utils.getCleanedString(word.toString()).length() > 1) {
						if(!validString) {
							validString = Utils.stringHasWords(dict, word.toString());
						}
						sentence.append(Constants.SPACE_STR);
						word.append(Constants.SPACE_STR);
						skippedChars.clear();
						skippedChars.add(dataCharHex);
						status = ENTRIES_STATUS.SKIPPING_CHARS;
					}
					else {
						if(validString) {
							offsetEntryList.add(new OffsetEntry(entryStart, i, endCharsList));
						}
						entryStart = 0;
						status = ENTRIES_STATUS.SEARCHING_START_OF_STRING;
					}
				}
				break;
			case SKIPPING_CHARS:
				if(dataChar != null) {
					word.setLength(0);
					sentence.append(dataChar);
					word.append(dataChar);
					status = ENTRIES_STATUS.SEARCHING_END_OF_STRING;
				}
				else {
					skippedChars.add(dataCharHex);
					boolean skippedAreEndings = listContainsFromList(endCharsList, skippedChars);
					if(skippedChars.size() > numIgnoredChars) {
						if(sentence.length() > numMinChars) {
							if(Utils.stringHasWords(dict, word.toString()) || (validString && skippedAreEndings)) {
								offsetEntryList.add(new OffsetEntry(entryStart, i, endCharsList));
							}
							else {
								if(validString) {
									offsetEntryList.add(new OffsetEntry(entryStart, i, endCharsList));
								}
							}
						}
						entryStart = 0;
						status = ENTRIES_STATUS.SEARCHING_START_OF_STRING;
					}
				}
				break;
			default:
				break;
			}
		}
		//Cleanup?
		if(entryStart > 0) {
			offsetEntryList.add(new OffsetEntry(entryStart, secondFileBytes.length - 1, endCharsList));
		}
		word.setLength(0);
		for(OffsetEntry oe : offsetEntryList) {
			word.append(oe.toEntryString()).append(Constants.OFFSET_STR_SEPARATOR);
		}
		return word.toString();
	}

	private boolean listContainsFromList(List<String> list1, List<String> list2) {
		boolean res = false;
		for(String skippedChar : list2) {
			if(list1.contains(skippedChar)) {
				res = true;
				break;
			}
		}
		return res;
	}

	public String toAsciiTable() {
		StringBuffer sb = new StringBuffer();
		for(Map.Entry<Byte, String> entry : Utils.sortByValue(table).entrySet()) {
			if(SPANISH_CHARS.containsKey(entry.getValue())) {
				String spaChar = SPANISH_CHARS.get(entry.getValue());
				sb.append(String.format(Constants.HEX_16_FORMAT, entry.getKey())).append(Constants.TABLE_SEPARATOR).append(spaChar);
				sb.append(Constants.S_NEWLINE);
			}
			sb.append(String.format(Constants.HEX_16_FORMAT, entry.getKey())).append(Constants.TABLE_SEPARATOR).append(entry.getValue());
			sb.append(Constants.S_NEWLINE);
		}
		return sb.toString();
	}

	public boolean equals(Object obj) {
		if(!(obj instanceof HexTable)) {
			return false;
		}
		HexTable objHt = (HexTable) obj;
		return table.equals(objHt.table);
	}

}

