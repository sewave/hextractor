package com.wave.hextractor.util;

import com.wave.hextractor.object.HexTable;
import com.wave.hextractor.pojo.OffsetEntry;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Utility class.
 * @author slcantero
 */
public class Utils {

	/**
	 * Hidden constructor.
	 */
	private Utils() {
	}

	/** The Constant INVALID_FILE_CHARACTERS. */
	private static final List<String> INVALID_FILE_CHARACTERS = Arrays.asList("<", ">", ":", "\"", "\\", "/", "|", "?", "*");

	/**
	 * Returns true if the file name is valid.
	 *
	 * @param fileName the file name
	 * @return true, if is valid file name
	 */
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

	/**
	 * Copy the file using streams.
	 *
	 * @param source the source
	 * @param dest the dest
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static void copyFileUsingStream(String source, String dest) throws IOException {
		try(InputStream is = new FileInputStream(source); OutputStream os = new FileOutputStream(dest)) {
			byte[] buffer = new byte[1024];
			int length;
			while ((length = is.read(buffer)) > 0) {
				os.write(buffer, 0, length);
			}
		}
	}

	/**
	 * Creates an ascii file with the content.
	 *
	 * @param file the file
	 * @param content the content
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static void createFile(String file, String content) throws IOException {
		try(OutputStream os = new FileOutputStream(file)) {
			os.write(content.getBytes());
		}
	}

	/**
	 * Returns the path of the path + the file name.
	 *
	 * @param path the path
	 * @param fileName the file name
	 * @return the joined file name
	 */
	public static String getJoinedFileName(File path, String fileName) {
		return path.getAbsolutePath() + Constants.FILE_SEPARATOR + fileName;
	}

	/**
	 * Pads the string from the left up to size chars with '0'.
	 *
	 * @param text text to pad.
	 * @param size size to pad to.
	 * @return the string
	 */
	public static String fillLeft(String text, int size) {
		StringBuilder builder = new StringBuilder();
		while (builder.length()+text.length() < size) {
			builder.append(Constants.PAD_CHAR_STRING);
		}
		builder.append(text);
		return builder.toString();
	}

	/**
	 * Returns true if the system property logLevel is set to DEBUG.
	 *
	 * @return true, if is debug
	 */
	public static boolean isDebug() {
		return "DEBUG".equals(System.getProperty("logLevel"));
	}

	/**
	 * Returns the Hex filled from the left with 0.</br>
	 * And in upper case.
	 *
	 * @param number the number
	 * @param size the size
	 * @return the hex filled left
	 */
	public static String getHexFilledLeft(int number, int size) {
		return Utils.fillLeft(Integer.toHexString(number), size).toUpperCase();
	}

	/**
	 * Tranforms the data to a hex string.
	 *
	 * @param data the data
	 * @return the string
	 */
	public static String toHexString(byte[] data) {
		StringBuilder sb = new StringBuilder("[");
		for(byte dataByte : data) {
			sb.append(String.format(Constants.HEX_16_FORMAT, dataByte)).append(Constants.SPACE_STR);
		}
		sb.append("]");
		return sb.toString();
	}

	/**
	 * Transforms the hex string to a byte [].
	 *
	 * @param s the s
	 * @return the byte[]
	 */
	public static byte[] hexStringToByteArray(String s) {
		int len = s.length();
		byte[] data = new byte[len / 2];
		for (int i = 0; i < len; i += 2) {
			data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
					+ Character.digit(s.charAt(i+1), 16));
		}
		return data;
	}

	/**
	 * Translates the hex string character to byte.
	 *
	 * @param s the s
	 * @return the byte
	 */
	public static byte hexStringCharToByte(String s) {
		return hexStringToByteArray(s)[0];
	}

	/**
	 * Generates a byte array with the bytes of the int, from MSB to LSB.
	 *
	 * @param value the value
	 * @return the byte[]
	 */
	public static byte[] intToByteArray(int value) {
		return new byte[] {
				(byte)(value >>> 24),
				(byte)(value >>> 16),
				(byte)(value >>> 8),
				(byte)value};
	}

	/**
	 * Converts two bytes to a int.
	 *
	 * @param value1 MSB
	 * @param value2 LSB
	 * @return short v1 * 256 + v2
	 */
	public static int bytesToInt(byte value1, byte value2) {
		return value1 << 8 & 0xFF00 | value2 & 0xFF;
	}

	/**
	 * Converts two bytes to a int.
	 *
	 * @param value1 MSB
	 * @param value2 Med
	 * @param value3 LSB
	 * @return short v1 + v2 *256 8
	 */
	public static int bytesToInt(byte value1, byte value2, byte value3) {
		return value1 << 16 & 0xFF0000 | value2 << 8 & 0xFF00 | value3 & 0xFF;
	}

	/**
	 * Converts two bytes to a int.
	 *
	 * @param value1 MSB
	 * @param value2 Med MSB
	 * @param value3 MED LSB
	 * @param value4 LSB
	 * @return short v1 + v2 *256 8
	 */
	public static int bytesToInt(byte value1, byte value2, byte value3, byte value4) {
		return value1 << 24 & 0xFF000000 | value2 << 16 & 0xFF0000 | value3 << 8 & 0xFF00| value4 & 0xFF;
	}

	/**
	 * Loads the input hex string into the b[].
	 *
	 * @param input the input
	 * @param b the b
	 */
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
					hexLine.append(input, i, i+2);
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
							log("DUMPING " + hexString);
							log("TO @" + adrrStartString + ":" + adrrEndString + " " + (addrEnd - addrStart + 1) + " BYTES");
							log("");
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

	/**
	 * Gets the offsets of the string offsets.
	 *
	 * @param string the string
	 * @return the offsets
	 * @throws IOException the exception
	 */
	public static List<OffsetEntry> getOffsets(String string) throws IOException {
		if(string.endsWith(Constants.OFF_EXTENSION)) {
			string = FileUtils.getCleanOffsets(string);
		}
		string = string.toUpperCase();
		List<OffsetEntry> offsets = new ArrayList<>();
		for(String offsetString : string.split(Constants.OFFSET_STR_SEPARATOR)) {
			OffsetEntry entry = new OffsetEntry();
			String[] values = offsetString.split(Constants.OFFSET_CHAR_SEPARATOR);
			entry.setStart(Integer.parseInt(values[0], Constants.HEX_RADIX));
			if(values.length > 1) {
				entry.setEnd(Integer.parseInt(values[1], Constants.HEX_RADIX));
				if(values.length > 2) {
					for(int i = 2; i < values.length; i++ ) {
						entry.getEndChars().add(values[i]);
					}
				}
			}
			offsets.add(entry);
		}
		return offsets;
	}

	/**
	 * Gets the lines cleaned (Only printable chars).
	 *
	 * @param lines the lines
	 * @return the lines cleaned
	 */
	public static StringBuilder getLinesCleaned(String[] lines) {
		StringBuilder cleanedLines = new StringBuilder();
		for(String line : lines) {
			line = line.replaceAll(Constants.REGEX_CLEAN_TEXT, Constants.SPACE_STR);
			line = line.replaceAll(Constants.REGEX_CLEAN_SPACES, Constants.SPACE_STR);
			line = line.replaceAll(Constants.REGEX_DICTIONARY_CHARS, Constants.EMPTY);
			line = line.trim();
			if(line.length() > 0) {
				cleanedLines.append(line).append(Constants.S_NEWLINE);
			}
		}
		return cleanedLines;
	}

	/**
	 * Returns true if the string has words from the dict.
	 *
	 * @param dict .
	 * @param sentence .
	 * @return true, if successful
	 */
	public static boolean stringHasWords(Collection<String> dict, String sentence) {
		return Arrays.stream(getCleanedString(sentence).toLowerCase().split(Constants.SPACE_STR)).
				anyMatch(word -> word != null && word.trim().length() > 3 && dict.contains(word));
	}

	/**
	 * Cleans a string from multiple spaces, to lower case, </br>
	 * and deletes the non digit characters.
	 *
	 * @param string the string
	 * @return the cleaned string
	 */
	public static String getCleanedString(String string) {
		return string.replaceAll(
				Constants.REGEX_MULTI_SPACES, Constants.SPACE_STR).toLowerCase().replaceAll(
						Constants.REGEX_NOT_LETTER_DIGIT, Constants.EMPTY);
	}

	/**
	 * Returns true if all bytes are equal.
	 *
	 * @param entryData the entry data
	 * @return true, if successful
	 */
	public static boolean allSameValue(byte[] entryData) {
		return IntStream.range(0, entryData.length).map(i->entryData[i]).distinct().count() <= 1;
	}

	/**
	 * Generates a hex string of at least length size, padded with zeroes on</br>
	 * the left, uppercase.
	 *
	 * @param value the value
	 * @param length the length
	 * @return the string
	 */
	public static String intToHexString(int value, int length) {
		String num = fillLeft(Integer.toHexString(value), length).toUpperCase();
		if (num.length() > length) {
			num = num.substring(num.length() - length);
		}
		return num;
	}

	/**
	 * Translates an array of hex values to int values.
	 *
	 * @param hexValues the hex values
	 * @return the int[]
	 */
	public static int[] hexStringListToIntList(String[] hexValues) {
		int[] numbers = new int[hexValues.length];
		for(int i= 0; i < hexValues.length; i++) {
			numbers[i] = Integer.parseInt(hexValues[i], Constants.HEX_RADIX);
		}
		return numbers;
	}

	/**
	 * Get the OffsetEntries of the string.
	 *
	 * @param entries the entries
	 * @return the hex offsets
	 */
	public static List<OffsetEntry> getHexOffsets(String entries) {
		List<OffsetEntry> entryList = new ArrayList<>();
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
					log("Invalid Offset entry!!!!! : " +entryStr);
				}
			}
			if(entry != null) {
				entryList.add(entry);
			}
		}
		return entryList;
	}

	/**
	 * Sorts the map by value.
	 *
	 * @param <K> the key type
	 * @param <V> the value type
	 * @param map the map
	 * @return the map
	 */
	public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
		return map.entrySet().stream().sorted(Map.Entry.comparingByValue()).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
				(oldValue, newValue) -> oldValue, LinkedHashMap::new));
	}

	/**
	 * Transforms the list of entries to the format:</br>
	 * (start-end-(endchar)+)*etc.
	 *
	 * @param offEntries the off entries
	 * @return the string
	 */
	public static String toFileString(List<OffsetEntry> offEntries) {
		StringBuilder entriesStr = new StringBuilder();
		for(OffsetEntry entry : offEntries) {
			if(entriesStr.length() > 0) {
				entriesStr.append(Constants.OFFSET_STR_SEPARATOR);
			}
			entriesStr.append(entry.toEntryString());
		}
		return entriesStr.toString();
	}

	/**
	 * Gets the dict key.
	 *
	 * @param line the line
	 * @return the dict key
	 */
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

	/**
	 * Checks if is dict value.
	 *
	 * @param line the line
	 * @return true, if is dict value
	 */
	private static boolean isDictValue(String line) {
		return !(line.startsWith(Constants.ADDR_STR) || line.startsWith(Constants.S_COMMENT_LINE) || line.startsWith(Constants.S_MAX_BYTES));
	}

	/**
	 * Extracts the dictionary of the file Lines.
	 *
	 * @param transFileLines the trans file lines
	 * @return the map
	 */
	public static Map<String, String> extractDictionary(String[] transFileLines) {
		Map<String, String> dict = new HashMap<>();
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

	/**
	 * Devuelve true si key y value tienen la misma longitud.
	 *
	 * @param key the key
	 * @param value the value
	 * @return true, if successful
	 */
	public static boolean checkLineLength(String key, String value) {
		return getLineLength(key).equals(getLineLength(value));
	}

	/**
	 * Gets the line length.
	 *
	 * @param value the value
	 * @return the line length
	 */
	private static String getLineLength(String value) {
		return value.substring(value.lastIndexOf(Constants.S_STR_NUM_CHARS));
	}

	/**
	 * Gets the text area using the table.
	 */
	public static String getTextArea(int offset, int length, byte[] data, HexTable hexTable) {
		StringBuilder sb = new StringBuilder(length);
		int end = offset + length;
		if(end > data.length) {
			end = data.length;
		}
		for(int i = offset; i < end; i++) {
			sb.append(hexTable.toString(data[i], false));
		}
		return sb.toString();
	}

	/**
	 * Gets the hex area as string 00 01 02 etc.
	 *
	 * @param offset the offset
	 * @param length the length
	 * @param data the data.
	 * @return the hex area
	 */
	public static String getHexArea(int offset, int length, byte[] data) {
		StringBuilder sb = new StringBuilder(length * Constants.HEX_VALUE_SIZE);
		int end = offset + length;
		if(end > data.length) {
			end = data.length;
		}
		for(int i = offset; i < end; i++) {
			sb.append(String.format(Constants.HEX_16_FORMAT, data[i]));
			sb.append(Constants.SPACE_STR);
		}
		return sb.toString();
	}
	
	/**
	 * Gets the hex area as string 00 01 02 etc.
	 *
	 * @param offset the offset
	 * @param length the length
	 * @param data the data.
	 * @return the hex area
	 */
	public static String getHexAreaFixedWidth(int offset, int length, byte[] data, int cols) {
		StringBuilder sb = new StringBuilder(length * Constants.HEX_VALUE_SIZE);
		int end = offset + length;
		if(end > data.length) {
			end = data.length;
		}
		int curCol = 0;
		for(int i = offset; i < end; i++) {
			sb.append(String.format(Constants.HEX_16_FORMAT, data[i]));
			if(curCol == cols - 1 && i < end -1) {
				sb.append(Constants.S_NEWLINE);
				curCol = 0;
			}
			else {
				sb.append(Constants.SPACE_STR);
				curCol++;
			}
		}
		return sb.toString();
	}
	

	/**
	 * Removes comment lines and joins nonaddress lines togheter
	 */
	public static String[] removeCommentsAndJoin(String asciiFile) {
		StringBuilder sb = new StringBuilder();
		for(String line : asciiFile.split(Constants.S_NEWLINE)) {
			if(!line.contains(Constants.S_COMMENT_LINE)) {
				if(line.contains(Constants.ADDR_STR)) {
					sb.append(line).append(Constants.S_NEWLINE);
				}
				else {
					sb.append(line);
				}
			}
		}
		return sb.toString().split(Constants.S_NEWLINE);
	}

	/**
	 * Compresses 4 6 bit bytes to 3 8 bit bytes
	 */
	public static byte[] getCompressed4To3Data(byte[] bytes) {
		byte[] res = new byte[bytes.length];
		int j = 0;
		int i;
		for(i = 0; i <= bytes.length - 4; i += 4) {
			res[j++] = (byte) (bytes[i] << 2 & 0xFF | bytes[i + 1]>> 4 & 0xFF);
			res[j++] = (byte) (bytes[i + 1] << 4 & 0xFF | bytes[i + 2] >> 2 & 0xFF);
			res[j++] = (byte) ((bytes[i + 3]  | (bytes[i + 2] & 0x3) << 6 & 0xFF) & 0xFF);
		}
		//Copy leftovers
		switch(bytes.length % 4) {
		case 1:
			//No debería poder pasar nunca
			res[j++] = bytes[i];
			break;
		case 2:
			//2->1
			res[j++] = (byte) (bytes[i] << 2 & 0xFF | bytes[i + 1]>> 4 & 0xFF);
			break;
		default:
		case 0:
		case 3:
			//3->2
			res[j++] = (byte) (bytes[i] << 2 & 0xFF | bytes[i + 1]>> 4 & 0xFF);
			res[j++] = (byte) (bytes[i + 1] << 4 & 0xFF | bytes[i + 2] >> 2 & 0xFF);
			break;
		}
		return Arrays.copyOfRange(res,  0,  j);
	}

	/**
	 * Expands 3 bytes to 4 6 bits bytes
	 * @param bytes .
	 * @return expanded data
	 */
	static byte[] getExpanded3To4Data(byte[] bytes) {
		byte[] res = new byte[bytes.length * 2];
		int j = 0;
		int i;
		for(i = 0; i <= bytes.length - 3; i += 3) {
			res[j++] = (byte) (bytes[i] >> 2 & 0x3F);
			res[j++] = (byte) (((bytes[i] & 0x3) << 4 | (bytes[i + 1] & 0xFF) >> 4) & 0x3F);
			res[j++] = (byte) (((bytes[i+2] & 0xFF) >> 6 | (bytes[i + 1] & 0xFF) << 2) & 0x3F);
			res[j++] = (byte) (bytes[i + 2] & 0x3F);
		}
		switch(bytes.length % 3) {
		case 1:
			res[j++] = (byte) (bytes[i] >> 2 & 0x3F);
			res[j++] = (byte) ((bytes[i] & 0x03) << 4 & 0x30);
			break;
		case 2:
			res[j++] = (byte) (bytes[i] >> 2 & 0x3F);
			res[j++] = (byte) (((bytes[i] & 0x3) << 4 & 0x30 | bytes[i + 1] >> 4 & 0x0F) & 0x3F);
			res[j++] = (byte) (bytes[i + 1] << 2 & 0x3F);
			break;
		default:
		case 0:
			break;
		}
		return Arrays.copyOfRange(res,  0,  j);
	}

	/**
	 * Log.
	 *
	 * @param msg the msg
	 */
	public static void log(String msg) {
		System.out.println(msg);
	}

	/**
	 * LogNoNL.
	 *
	 * @param msg the msg
	 */
	public static void logNoNL(String msg) {
		System.out.print(msg);
	}

	/**
	 * Log exception.
	 *
	 * @param e the e
	 */
	public static void logException(Exception e) {
		e.printStackTrace();
	}

	/**
	 * Short to bytes.
	 *
	 * @param value the value
	 * @return the byte[]
	 */
	public static byte[] shortToBytes(short value) {
		byte[] returnByteArray = new byte[2];
		returnByteArray[0] = (byte) (value & Constants.MASK_8BIT);
		returnByteArray[1] = (byte) (value >>> 8 & Constants.MASK_8BIT);
		return returnByteArray;
	}
	
	public static String bytesToHex(byte[] hashInBytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : hashInBytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString().toLowerCase();
    }

}
