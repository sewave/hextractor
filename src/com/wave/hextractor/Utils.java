package com.wave.hextractor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Utils {

	private static final List<String> INVALID_FILE_CHARACTERS = Arrays.asList(new String[]{"<", ">", ":", "\"", "\\", "/", "|", "?", "*"});

	public static boolean isValidFileName(String fileName) {
		boolean res = true;
		if(fileName != null) {
			for(String invalidChar : INVALID_FILE_CHARACTERS) {
				if(fileName.contains(invalidChar)) {
					res = false;
				}
			}
		}
		return res;
	}

	public static void copyFileUsingStream(String source, String dest) throws IOException {
		InputStream is = null;
		OutputStream os = null;
		try {
			is = new FileInputStream(source);
			os = new FileOutputStream(dest);
			byte[] buffer = new byte[1024];
			int length;
			while ((length = is.read(buffer)) > 0) {
				os.write(buffer, 0, length);
			}
		} finally {
			if(is != null) {
				is.close();
			}
			if(os != null) {
				os.close();
			}
		}
	}

	public static void createFile(String file, String content) throws IOException {
		OutputStream os = null;
		try {
			os = new FileOutputStream(file);
			os.write(content.getBytes());
		} finally {
			os.close();
		}
	}

	public static String getJoinedFileName(File path, String fileName) throws IOException {
		return path.getAbsolutePath() + Constants.FILE_SEPARATOR + fileName;
	}

	/**
	 * Pads the string from the left up to size chars with '0'
	 * @param text text to pad.
	 * @param size size to pad to.
	 * @return
	 */
	public static String fillLeft(String text, int size) {
		StringBuilder builder = new StringBuilder();
		while (builder.length()+text.length() < size) {
			builder.append(Constants.PAD_CHAR_STRING);
		}
		builder.append(text);
		return builder.toString();
	}

	public static boolean isDebug() {
		return "DEBUG".equals(System.getProperty("logLevel"));
	}

	public static String getHexFilledLeft(int number, int size) {
		return Utils.fillLeft(Integer.toHexString(number), size).toUpperCase();
	}

	public static String toHexString(byte[] data) {
		StringBuilder sb = new StringBuilder("[");
		for(byte dataByte : data) {
			sb.append(String.format(Constants.HEX_16_FORMAT, dataByte)).append(Constants.SPACE_STR);
		}
		sb.append("]");
		return sb.toString();
	}

	public static byte[] hexStringToByteArray(String s) {
		int len = s.length();
		byte[] data = new byte[len / 2];
		for (int i = 0; i < len; i += 2) {
			data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
					+ Character.digit(s.charAt(i+1), 16));
		}
		return data;
	}

	public static byte hexStringCharToByte(String s) {
		return hexStringToByteArray(s)[0];
	}

	/**
	 * Generates a byte array with the bytes of the int, from MSB to LSB
	 * @param value
	 * @return
	 */
	public static final byte[] intToByteArray(int value) {
		return new byte[] {
				(byte)(value >>> 24),
				(byte)(value >>> 16),
				(byte)(value >>> 8),
				(byte)value};
	}

	/**
	 * Converts two bytes to a int
	 * @param value1 MSB
	 * @param value2 LSB
	 * @return short v1 * 256 + v2
	 */
	public static final int bytesToInt(byte value1, byte value2) {
		return (value1 << 8) & 0xFF00 | (value2 & 0xFF);
	}

	/**
	 * Converts two bytes to a int
	 * @param value1 MSB
	 * @param value2 Med
	 * @param value3 LSB
	 * @return short v1 + v2 *256 8
	 */
	public static final int bytesToInt(byte value1, byte value2, byte value3) {
		return (value1 << 16) & 0xFF0000 | (value2 << 8) & 0xFF00 | (value3 & 0xFF);
	}

	/**
	 * Converts two bytes to a int
	 * @param value1 MSB
	 * @param value2 Med MSB
	 * @param value3 MED LSB
	 * @param value4 LSB
	 * @return short v1 + v2 *256 8
	 */
	public static final int bytesToInt(byte value1, byte value2, byte value3, byte value4) {
		return (value1 << 16) & 0xFF000000 | (value2 << 8) & 0xFF0000 | (value3 & 0xFF00)| (value4 & 0xFF);
	}

	public static void loadHex(String input, byte[] b) {
		input = input.toUpperCase();
		boolean incomment = false;
		StringBuilder hexLine = new StringBuilder();
		for(int i = 0; i < input.length(); i++) {
			String charString = input.substring(i, i+1);
			char charGot = charString.charAt(0);
			if(incomment) {
				if(Constants.NEWLINE == charGot) {
					incomment = false;
				}
			}
			else {
				if(Constants.HEXCHARS.contains(charString)) {
					hexLine.append(input.substring(i, i+2));
					i++;
				}
				else {
					switch(charGot) {
					case Constants.COMMENT_LINE:
						incomment = true;
						break;
					case Constants.ADDR_CHAR:
						String hexString = hexLine.toString();
						byte[] line = hexStringToByteArray(hexString);

						while(charGot == Constants.ADDR_CHAR) {
							i += Constants.CHAR_SIZE; //Jump ADDR_CHAR
							//Read start
							String adrrStartString = input.substring(i, i + Constants.HEX_ADDR_SIZE);
							int addrStart = Integer.parseInt(adrrStartString, Constants.HEX_RADIX);
							i += Constants.HEX_ADDR_SIZE + Constants.CHAR_SIZE;
							//Read end
							String adrrEndString = input.substring(i, i + Constants.HEX_ADDR_SIZE);
							int addrEnd = Integer.parseInt(adrrEndString, Constants.HEX_RADIX);
							i += Constants.HEX_ADDR_SIZE;
							//Dump buffer if has hex and address
							System.out.println("DUMPING " + hexString);
							System.out.println("TO @" + adrrStartString + ":" + adrrEndString + " " + (addrEnd - addrStart + 1) + " BYTES");
							System.out.println("");
							System.arraycopy(line, 0, b, addrStart, line.length);
							//pad with zeroes if needed
							for(int j = addrStart + line.length; j <= addrEnd; j++) {
								b[j] = Constants.PAD_CHAR;
							}
							if(i < input.length()-1) {
								charGot = input.substring(i, i+1).charAt(0);
							}
							else {
								charGot = Constants.MAX_BYTES;
							}
						}
						//Buffer clear
						hexLine.setLength(0);
						i--; //For increment will adjust it
						break;
					default:
						break;
					}
				}
			}
		}
	}

	public static List<OffsetEntry> getOffsets(String string) throws Exception {
		if(string.endsWith(Constants.OFF_EXTENSION)) {
			string = FileUtils.getCleanOffsets(string);
		}
		string = string.toUpperCase();
		List<OffsetEntry> offsets = new ArrayList<OffsetEntry>();
		for(String offsetString : string.split(Constants.OFFSET_STR_SEPARATOR)) {
			OffsetEntry entry = new OffsetEntry();
			String[] values = offsetString.split(Constants.OFFSET_CHAR_SEPARATOR);
			entry.start = Integer.parseInt(values[0], Constants.HEX_RADIX);
			if(values.length > 1) {
				entry.end = Integer.parseInt(values[1], Constants.HEX_RADIX);
				if(values.length > 2) {
					for(int i = 2; i < values.length; i++ ) {
						entry.endChars.add(values[i]);
					}
				}
			}
			offsets.add(entry);
		}
		return offsets;
	}

	public static StringBuffer getLinesCleaned(String[] lines) {
		StringBuffer cleanedLines = new StringBuffer();
		for(String line : lines) {
			line = line.replaceAll(Constants.REGEX_CLEAN_TEXT, Constants.SPACE_STR);
			line = line.replaceAll(Constants.REGEX_CLEAN_SPACES, Constants.SPACE_STR);
			line = line.trim();
			if(line.length() > 0) {
				cleanedLines.append(line).append(Constants.S_NEWLINE);
			}
		}
		return cleanedLines;
	}

	public static boolean stringHasWords(Collection<String> dict, String sentence) {
		boolean hasWords = false;
		sentence = getCleanedString(sentence);
		for(String word: sentence.toLowerCase().split(Constants.SPACE_STR)) {
			if(word != null && word.trim().length() > 2 && dict.contains(word)) {
				hasWords = true;
				break;
			}
		}
		return hasWords;
	}

	public static String getCleanedString(String string) {
		return string.replaceAll(
				Constants.REGEX_MULTI_SPACES, Constants.SPACE_STR).toLowerCase().replaceAll(
						Constants.REGEX_NOT_LETTER_DIGIT, Constants.EMPTY);
	}

	public static boolean allSameValue(byte[] entryData) {
		boolean isEquals = true;
		for(int i = 0; i < entryData.length; i++) {
			if(entryData[0] != entryData[i]) {
				isEquals = false;
				break;
			}
		}
		return isEquals;
	}

	/**
	 * Generates a hex string of at least length size, padded with zeroes on the left, uppercase.
	 * @param value
	 * @param length
	 * @return
	 */
	public static String intToHexString(int value, int length) {
		String num = Utils.fillLeft(Integer.toHexString(value), length).toUpperCase();
		if (num.length() > length) {
			num = num.substring(num.length() - length, num.length());
		}
		return num;
	}

	public static int[] hexStringListToIntList(String[] hexValues) {
		int[] numbers = new int[hexValues.length];
		for(int i= 0; i < hexValues.length; i++) {
			numbers[i] = Integer.parseInt(hexValues[i], Constants.HEX_RADIX);
		}
		return numbers;
	}

	public static List<OffsetEntry> getHexOffsets(String entries) {
		List<OffsetEntry> entryList = new ArrayList<OffsetEntry>();
		for(String entryStr : entries.replaceAll(Constants.SPACE_STR, Constants.EMPTY).split(Constants.OFFSET_STR_SEPARATOR)) {
			OffsetEntry  entry = null;
			if(entryStr.contains(Constants.OFFSET_CHAR_SEPARATOR)) {
				//00001-00003 type init-end (inclusives)
				int[] numbers = hexStringListToIntList(entryStr.split(Constants.OFFSET_CHAR_SEPARATOR));
				entry = new OffsetEntry(numbers[0], numbers[1], null);
			}
			else {
				if(entryStr.contains(Constants.OFFSET_LENGTH_SEPARATOR)) {
					//00001:00003 type init:length
					int[] numbers = hexStringListToIntList(entryStr.split(Constants.OFFSET_LENGTH_SEPARATOR));
					entry = new OffsetEntry(numbers[0], numbers[0] + numbers[1] - 1, null);
				}
				else {
					System.out.println("Invalid Offset entry!!!!! : " +entryStr);
				}
			}
			if(entry != null) {
				entryList.add(entry);
			}
		}
		return entryList;
	}

	public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
		List<Map.Entry<K, V>> list = new LinkedList<Map.Entry<K, V>>(map.entrySet());
		Collections.sort(list, new Comparator<Map.Entry<K, V>>() {
			public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2) {
				return (o1.getValue()).compareTo(o2.getValue());
			}
		});

		Map<K, V> result = new LinkedHashMap<K, V>();
		for (Map.Entry<K, V> entry : list) {
			result.put(entry.getKey(), entry.getValue());
		}
		return result;
	}

	public static String toFileString(List<OffsetEntry> offEntries) {
		StringBuffer entriesStr = new StringBuffer();
		for(OffsetEntry entry : offEntries) {
			if(entriesStr.length() > 0) {
				entriesStr.append(Constants.OFFSET_STR_SEPARATOR);
			}
			entriesStr.append(entry.toEntryString());
		}
		return entriesStr.toString();
	}

	private static String getDictKey(String line) {
		//;000000DE{text}#117#105
		String res = null;
		if(line.startsWith(Constants.S_COMMENT_LINE)) {
			res = line.substring(line.lastIndexOf(Constants.S_ORG_STR_OPEN) + Constants.S_ORG_STR_OPEN.length(), line.lastIndexOf(Constants.S_ORG_STR_CLOSE)) +
					line.substring(line.lastIndexOf(Constants.STR_NUM_CHARS),
							line.lastIndexOf(Constants.STR_NUM_CHARS) + Constants.LEN_NUM_CHARS + Constants.S_STR_NUM_CHARS.length());
		}
		return res;
	}

	private static boolean isDictValue(String line) {
		return !(line.startsWith(Constants.ADDR_STR) || line.startsWith(Constants.S_COMMENT_LINE) || line.startsWith(Constants.S_MAX_BYTES));
	}

	public static Map<String, String> extractDictionary(String[] transFileLines) {
		Map<String, String> dict = new HashMap<String, String>();
		String currKey = null;
		for(String line : transFileLines) {
			String aKey = getDictKey(line);
			if(aKey != null) {
				currKey = aKey;
			}
			else {
				if(isDictValue(line)) {
					dict.put(currKey, line);
				}
			}
		}
		return dict;
	}

	public static String[] translateDictionary(String[] toTransLines, Map<String, String> dictionary) {
		String[] translatedLines = new String[toTransLines.length];
		String currKey = null;
		for(int i = 0; i < toTransLines.length; i++) {
			String line = toTransLines[i];
			translatedLines[i] = line;
			String aKey = getDictKey(line);
			if(aKey != null) {
				currKey = aKey;
			}
			if(isDictValue(line)) {
				if(dictionary.containsKey(currKey)) {
					translatedLines[i] = dictionary.get(currKey);
				}
			}
		}
		return translatedLines;
	}

	/**
	 * Devuelve true si key y value tienen la misma longitud
	 * @param key
	 * @param value
	 * @return
	 */
	public static boolean checkLineLength(String key, String value) {
		return getLineLength(key).equals(getLineLength(value));
	}

	private static String getLineLength(String value) {
		return value.substring(value.lastIndexOf(Constants.S_STR_NUM_CHARS));
	}

}
