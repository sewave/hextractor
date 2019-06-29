/*
 *
 */
package com.wave.hextractor.object;

import com.wave.hextractor.pojo.OffsetEntry;
import com.wave.hextractor.util.Constants;
import com.wave.hextractor.util.FileUtils;
import com.wave.hextractor.util.Utils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.util.*;

/**
 * Class for the table operations.
 * @author slcantero
 */
public class HexTable implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -2128637206318658102L;

	/** The Constant TABLE_KEY_A. */
	private static final String TABLE_KEY_A = "A";

	/** The Constant TABLE_KEY_LOWA. */
	private static final String TABLE_KEY_LOWA = "a";

	/** The Constant TABLE_KEY_ZERO. */
	private static final String TABLE_KEY_ZERO = "0";

	/** The Constant SPANISH_CHARS. */
	private static final Map<String, String> SPANISH_CHARS = new HashMap<>();
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

	/** The table. */
	private Map<Byte, String> table = new HashMap<>();

	/** The reversed. */
	private Map<String, Byte> reversed = new HashMap<>();

	/** The searchPercentCompleted. */
	private float searchPercent = 0;

	/**
	 * Transforms the byte into a String.
	 */
	public String toString(byte aByte, boolean expand, boolean decodeUnknown) {
		String res = table.get(aByte);
		if(res == null || (res.length() > 1 && !expand)) {
			if(decodeUnknown) {
				res = Constants.HEX_CHAR + Utils.intToHexString(aByte, 2) + Constants.HEX_CHAR;
			}
			else {
				res = Constants.HEX_VIEWER_UNKNOWN_CHAR;
			}
		}
		return res;
	}

	/**
	 * Transforms the byte into a String.
	 *
	 * @param aByte the a byte
	 * @return the string
	 */
	public String toString(byte aByte, boolean expand) {
		return toString(aByte, expand, false);
	}

	/**
	 * Converts the table selection to a line description.
	 *
	 * @return the string
	 */
	public String toSelectionString() {
		StringBuilder res = new StringBuilder();
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

	/**
	 * Load lines.
	 *
	 * @param tableLines the table lines
	 */
	private void loadLines(List<String> tableLines) {
		table = new HashMap<>();
		reversed = new HashMap<>();
		for(String s : tableLines) {
			if(s.length() >= 4 && s.contains(Constants.TABLE_SEPARATOR)) {
				boolean isEquals = s.contains(Constants.TABLE_SEPARATOR + Constants.TABLE_SEPARATOR);
				String tablechar;
				String[] items = s.split(Constants.TABLE_SEPARATOR);
				if(isEquals) {
					tablechar = s.substring(s.indexOf(Constants.TABLE_SEPARATOR) + 1);
				}
				else {
					tablechar = items[1];
				}
				//Eliminamos saltos de linea
				tablechar = tablechar.replaceAll(Constants.S_NEWLINE, Constants.EMPTY).replaceAll(Constants.S_CRETURN, Constants.EMPTY);
				if(Constants.RESERVED_CHARS.contains(tablechar)) {
					Utils.log("WARNING - Table char \"" + tablechar + "\" will not be used because it is reserved.");
				}
				else {
					addToTable(Utils.hexStringCharToByte(items[0].toUpperCase().trim()), tablechar);
				}
			}
			else {
				Utils.log("ERROR - Line not valid: '" + s + "'");
			}
		}
	}

	/**
	 * Based in a displacement, reconstruct full table from $20 to $7E (included).
	 *
	 * @param displacement the displacement
	 */
	public HexTable(int displacement) {
		List<String> tableLines = new ArrayList<>();
		StringBuilder sb = new StringBuilder();
		byte currChar = (byte) (Constants.MIN_PRINTABLE_CHAR - displacement & Constants.MASK_8BIT);
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

	/**
	 * Loads the table lines.
	 *
	 * @param tableLines the table lines
	 */
	public HexTable(List<String> tableLines) {
		loadLines(tableLines);
	}

	/**
	 * Empty table.
	 */
	public HexTable() {
	}

	/**
	 * Loads a table lines file.
	 *
	 * @param tableFile the table file
	 * @throws FileNotFoundException the exception
	 */
	public HexTable(String tableFile) throws FileNotFoundException {
		loadLines(Arrays.asList(FileUtils.getAsciiFile(tableFile).replaceAll(Constants.UTF_8_BOM_BE, Constants.EMPTY).replaceAll(Constants.UTF_8_BOM_LE, Constants.EMPTY).split(String.valueOf(Constants.NEWLINE))));
	}

	/**
	 * Translates a hex string to ascii.
	 */
	public String toAscii(byte[] hexString, boolean expand, boolean decodeUnknown) {
		StringBuilder sb = new StringBuilder();
		for(byte b : hexString) {
			sb.append(toString(b, expand, decodeUnknown));
		}
		return sb.toString();
	}

	/**
	 * Translates a hex string to ascii.
	 */
	public String toAscii(byte[] hexString, boolean expand) {
		return toAscii(hexString, expand, false);
	}

	/**
	 * Simple string to hex using table.
	 */
	public byte[] toHex(String aString) {
		byte[] res = new byte[aString.length()];
		byte hexSpace;
		if(reversed.containsKey(Constants.SPACE_STR)) {
			hexSpace = reversed.get(Constants.SPACE_STR);
		}
		else {
			hexSpace = 0;
		}
		int i = 0;
		for(char c : aString.toCharArray()) {
			Byte b = reversed.get(String.valueOf(c));
			if(b == null) {
				res[i] = hexSpace;
			}
			else {
				res[i] = b;
			}
			i++;
		}
		return res;
	}

	/**
	 * Adds the to table.
	 *
	 * @param entry the entry
	 * @param theChar the the char
	 */
	public void addToTable(Byte entry, String theChar) {
		table.put(entry, theChar);
		reversed.put(theChar, entry);
	}


	/**
	 * Translates to ascii entry to a hex string.
	 *
	 * @param hexString the hex string
	 * @param entry the entry
	 * @param showExtracting shows current extraction
	 * @return the string
	 */
	public String toAscii(byte[] hexString, OffsetEntry entry, boolean showExtracting) {
		StringBuilder sb = new StringBuilder();
		int bytesreaded = 0;
		int bytesreadedStart = 0;
		StringBuilder line = new StringBuilder();
		if(showExtracting) {
			Utils.log("Extracting [" + Utils.fillLeft(Integer.toHexString(entry.getStart()), Constants.HEX_ADDR_SIZE).toUpperCase() + ":" +
					Utils.fillLeft(Integer.toHexString(entry.getEnd()), Constants.HEX_ADDR_SIZE).toUpperCase() + "]");
		}
		sb.append(entry.toString()).append(Constants.NEWLINE);
		for(int i = entry.getStart(); i <= entry.getEnd(); i++) {
			Byte hex = hexString[i];
			bytesreaded++;
			if(table.containsKey(hex)) {
				String value = table.get(hex);
				if(value.length() > 1) {
					line.append(Constants.S_CODEWORD_START);
					line.append(value);
					line.append(Constants.S_CODEWORD_END);
				}
				else {
					line.append(value);
				}
			}
			if(!table.containsKey(hex) || i == entry.getEnd()) {
				String hexStr = String.format(Constants.HEX_16_FORMAT, hexString[i]);
				if(!table.containsKey(hex)) {
					line.append(Constants.HEX_CHAR).append(hexStr).append(Constants.HEX_CHAR);
				}
				if(entry.getEndChars().contains(hexStr) || i == entry.getEnd()) {
					String originalLine = line.toString();
					String numChars = Utils.fillLeft(String.valueOf(originalLine.length()), Constants.LEN_NUM_CHARS);
					String numCharsHex = Utils.fillLeft(String.valueOf(bytesreaded - bytesreadedStart), Constants.LEN_NUM_CHARS);
					sb.append(Constants.COMMENT_LINE).append(Utils.fillLeft(Integer.toHexString(entry.getStart() + bytesreadedStart), Constants.HEX_ADDR_SIZE).toUpperCase());
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
		sb.append(Constants.MAX_BYTES).append(bytesreaded).append(Constants.NEWLINE);
		if(showExtracting) {
			Utils.log("TOTAL BYTES TO ASCII: " + bytesreaded);
		}
		return sb.toString();
	}

	/**
	 * Transforms the ascii string to hex byte[].
	 *
	 * @param string the string
	 * @param entry the entry
	 * @return the byte[]
	 */
	public byte[] toHex(String string, OffsetEntry entry) {
		int offset = 0;
		int offsetStart = 0;
		byte[] hex = new byte[string.length() * 16];
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
						Utils.log("ERROR! HEX CHAR NOT CLOSED AT: " + i + " -> " + string.substring(j, i+1));
					}
					if(entry.getEndChars().contains(hexchar)) {
						char nextchar = string.substring(i+1, i+2).charAt(0);
						while(Constants.ADDR_CHAR == nextchar) {
							i++;
							String hexTo = string.substring(i+1, i+1+8);
							Utils.log("INSERTING OFFSET " +
									Utils.fillLeft(Integer.toHexString(offsetStart), Constants.HEX_ADDR_SIZE) + " TO " + hexTo);
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
							int length = Integer.parseInt(string.substring(i+1, j));
							if(offset - offsetStart > length-1) {
								Utils.log("ERROR!!! STRING TOO LARGE (" +
										Utils.fillLeft(String.valueOf(offset - offsetStart+1), 4) + " - " +
										Utils.fillLeft(String.valueOf(length), 4) +
										")!!!");
								Utils.log(string.substring(stringStart, i));
							}
							else {
								if(offset - offsetStart < length-1) {
									Utils.log("WARNING!!! STRING TOO SMALL (" +
											Utils.fillLeft(String.valueOf(offset - offsetStart+1), 4) + " - " +
											Utils.fillLeft(String.valueOf(length), 4) + ")!!!");
									Utils.log(string.substring(stringStart, i));
									while(offset - offsetStart < length-1) {
										hex[offset++] = hexSpace;
									}
								}
							}
							i += j - i - 1;
							stringStart = i + 2;
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
					int length = Integer.parseInt(string.substring(i+1, j));
					if(offset - offsetStart - 1 > length-1) {
						Utils.log("ERROR!!! NOENDED STRING TOO LARGE (" +
								Utils.fillLeft(String.valueOf(offset - offsetStart), 4) + " - " +
								Utils.fillLeft(String.valueOf(length), 4) +
								")!!!");
						Utils.log(string.substring(stringStart, i));
					}
					else {
						if(offset - offsetStart - 1 < length-1) {
							while(offset - offsetStart - 1 < length-1) {
								hex[offset++] = hexSpace;
							}
						}
					}
					i += j - i - 1;
					stringStart = i + 2;
					break;
				case Constants.NEWLINE:
					break;
				case Constants.CODEWORD_START:
					int k = i;
					//Search CODEWORD_END if not end, space char
					boolean foundCodeWord = false;
					while(!foundCodeWord && k < string.length() - 2) {
						k++;
						foundCodeWord = Constants.S_CODEWORD_END.equals(string.substring(k, k+1));
					}
					byte codeWordValue = hexSpace;
					if(foundCodeWord) {
						//Get Key/value
						String key = string.substring(i + 1, k);
						if(reversed.containsKey(key)) {
							codeWordValue = reversed.get(key);
						}
						else {
							Utils.log("WARNING!!! CODE WORD NOT IN TABLE: '" + key + "'");
						}
						i = k;
					}
					hex[offset++] = codeWordValue;
					break;
				default:
					String nextString = String.valueOf(next);
					byte value = hexSpace;
					if(reversed.containsKey(nextString)) {
						value = reversed.get(nextString);
					}
					else {
						Utils.log("WARNING!!! CHARACTER NOT IN TABLE: '" + nextString + "'");
						Utils.log(string.substring(stringStart, i));
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
			Utils.log("WARNING!!! STRING TOO SMALL");
			Utils.log(string.substring(stringStart));
			for(int i = offset; i < maxsize; i++) {
				hex[i] = Constants.PAD_CHAR;
			}
		}
		if(maxsize == 0) {
			maxsize = offset;
		}
		if(Utils.isDebug()) {
			Utils.logNoNL("BYTES TO HEX: " + Utils.fillLeft(String.valueOf(offset), 5) + " / " +  Utils.fillLeft(String.valueOf(maxsize), 5));
		}
		return Arrays.copyOf(hex, maxsize);
	}

	/**
	 * The Enum ENTRIES_STATUS.
	 */
	enum ENTRIES_STATUS {
		/** The searching start of string. */
		SEARCHING_START_OF_STRING,
		/** The searching end of string. */
		SEARCHING_END_OF_STRING,
		/** The skipping chars. */
		SKIPPING_CHARS,
	}

	/**
	 * Get all entries from the file.
	 *
	 * @param secondFileBytes the second file bytes
	 * @param numMinChars the num min chars
	 * @param numIgnoredChars the num ignored chars
	 * @param endCharsList the end chars list
	 * @param dictFile the dict file
	 * @return the all entries
	 * @throws IOException the exception
	 */
	public String getAllEntries(byte[] secondFileBytes, int numMinChars, int numIgnoredChars,
			List<String> endCharsList, String dictFile) throws IOException {
		searchPercent = 0;
		List<OffsetEntry> offsetEntryList = new ArrayList<>();
		HashSet<String> dict = new HashSet<>(Arrays.asList(FileUtils.getAsciiFile(dictFile).split(Constants.S_NEWLINE)));
		int entryStart = 0;
		boolean validString = false;
		StringBuilder word = new StringBuilder();
		StringBuilder sentence = new StringBuilder();
		String dataChar;
		List<String> skippedChars = new ArrayList<>();
		ENTRIES_STATUS status = ENTRIES_STATUS.SEARCHING_START_OF_STRING;
		long lastTime = System.currentTimeMillis();
		for(int i = 0; i < secondFileBytes.length - numMinChars && !Thread.currentThread().isInterrupted(); i++) {
			searchPercent = i * 100f / secondFileBytes.length;
			if(System.currentTimeMillis() - lastTime > 1000) {
				lastTime = System.currentTimeMillis();
				Utils.log(searchPercent + "% completed.");
			}
			Byte readedByteObj = secondFileBytes[i];
			String dataCharHex = String.format(Constants.HEX_16_FORMAT, readedByteObj);
			dataChar = table.getOrDefault(readedByteObj, null);
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
					boolean skippedAreEndings = endCharsList.stream().anyMatch(skippedChars::contains);
					if(skippedChars.size() > numIgnoredChars) {
						if(sentence.length() > numMinChars) {
							if(Utils.stringHasWords(dict, word.toString()) || validString && skippedAreEndings) {
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
		if(entryStart > 0) {
			offsetEntryList.add(new OffsetEntry(entryStart, secondFileBytes.length - 1, endCharsList));
		}
		word.setLength(0);
		for(OffsetEntry oe : offsetEntryList) {
			word.append(oe.toEntryString()).append(Constants.OFFSET_STR_SEPARATOR);
		}
		return word.toString();
	}

	/**
	 * Transforms the table into ascii.
	 *
	 * @return the string
	 */
	public String toAsciiTable() {
		StringBuilder sb = new StringBuilder();
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

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (table == null ? 0 : table.hashCode());
		return result;
	}

	/**
	 * Equals.
	 *
	 * @param obj the obj
	 * @return true, if successful
	 */
	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof HexTable)) {
			return false;
		}
		HexTable objHt = (HexTable) obj;
		return table.equals(objHt.table);
	}

	/**
	 * Current search completition percent.
	 * @return percent search
	 */
	public float getSearchPercent() {
		return searchPercent;
	}

}

