package com.wave.hextractor.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import com.wave.hextractor.object.HexTable;
import com.wave.hextractor.pojo.OffsetEntry;
import com.wave.hextractor.pojo.TableSearchResult;

/**
 * Utility class for files.
 * @author slcantero
 */
public class FileUtils {

	/**
	 * Getx the extension of a file, i.e.: file.ext => ext
	 * @param file file to look at.
	 * @return the extension.
	 */
	public static String getFileExtension(File file) {
		String extension = Constants.EMPTY;
		int i = file.getName().lastIndexOf(Constants.CHR_DOT);
		if (i > 0) {
			extension = file.getName().substring(i + 1);
		}
		return extension;
	}

	/**
	 * Returns the ascii file with only Constants.NEWLINE as line separators.
	 * @param filename .
	 * @return .
	 * @throws Exception .
	 */
	public static String getAsciiFile(String filename) throws Exception {
		Scanner scanner = new Scanner( new File(filename), Constants.UTF8_ENCODING);
		String input = scanner.useDelimiter("\\A").next();
		scanner.close();
		input = input.replace(String.valueOf(Constants.NEWLINE) + String.valueOf(Constants.CRETURN), String.valueOf(Constants.NEWLINE));
		input = input.replace(String.valueOf(Constants.CRETURN) + String.valueOf(Constants.NEWLINE), String.valueOf(Constants.NEWLINE));
		input = input.replace(String.valueOf(Constants.CRETURN), String.valueOf(Constants.NEWLINE));
		return input;
	}

	/**
	 * Gets a file as a byte[].
	 * @param filename file to read.
	 * @return byte[] of the file.
	 * @throws Exception if there is a error reading.
	 */
	public static byte[] getFileBytes(String filename) throws Exception {
		File f = new File(filename);
		byte[] bytes = new byte[(int) f.length()];
		int offset = 0;
		int numRead = 0;
		boolean readMore = true;
		InputStream is = new FileInputStream(f);
		int bytesToRead = 1024;
		if(bytesToRead + offset > bytes.length) {
			bytesToRead = bytes.length - offset;
		}
		try {
			while (offset < bytes.length
					&& readMore) {
				numRead = is.read(bytes, offset, bytesToRead);
				if(numRead < 0) {
					readMore = false;
				}
				else {
					offset += numRead;
					bytesToRead = 1024;
					if(bytesToRead + offset > bytes.length) {
						bytesToRead = bytes.length - offset;
					}
				}
			}
		} finally {
			is.close();
		}
		return bytes;
	}

	/**
	 * Writes all the bytes to the filename.</br>
	 * <b>Overwrites the file if exists.</b>
	 * @param filename file to write to.
	 * @param b file bytes.
	 * @throws Exception
	 */
	public static void writeFileBytes(String filename, byte[] b) throws Exception {
		FileOutputStream stream = new FileOutputStream(filename);
		try {
			stream.write(b);
		} finally {
			stream.close();
		}
	}

	/**
	 * Writes a string as UTF8 in the destination file.
	 * <b>Overwrites the file if exists.</b>
	 * @param filename file to write.
	 * @param ascii string containing the text.
	 * @throws Exception
	 */
	public static void writeFileAscii(String filename, String ascii) throws Exception {
		PrintWriter out = new PrintWriter(filename, Constants.UTF8_ENCODING);
		out.print(ascii);
		out.close();
	}

	/**
	 * Interleaves the EVEN lines of firstFile with the ODD lines of the</br>
	 * secondFile to the third file.
	 * @param firstFile EVEN lines.
	 * @param secondFile ODD lines.
	 * @param thirdFile resultingFile.
	 * @throws Exception .
	 */
	public static void interleaveFiles(String firstFile, String secondFile, String thirdFile) throws Exception {
		System.out.println("Interleaving files EVEN: \"" + firstFile + "\"\n to ODD: \"" + secondFile + "\".");
		String[] file1 = FileUtils.getAsciiFile(firstFile).split(Constants.S_NEWLINE);
		String[] file2 = FileUtils.getAsciiFile(secondFile).split(Constants.S_NEWLINE);
		StringBuilder sb = new StringBuilder();
		// file 1 is even, file 2 is odd
		for (int i = 0; i < file1.length; i++) {
			if (i % 2 == 0) {
				sb.append(file1[i]);
			} else {
				sb.append(file2[i]);
			}
			sb.append(Constants.NEWLINE);
		}
		FileUtils.writeFileAscii(thirdFile, sb.toString());
	}

	/**
	 *
	 * @param firstFile
	 * @param secondFile
	 * @throws Exception
	 */
	public static void insertHexData(String firstFile, String secondFile) throws Exception {
		System.out.println("Inserting hex file \"" + firstFile + "\"\n to file \"" + secondFile + "\".");
		byte[] b = FileUtils.getFileBytes(secondFile);
		Utils.loadHex(FileUtils.getAsciiFile(firstFile), b);
		FileUtils.writeFileBytes(secondFile, b);
	}

	/**
	 *
	 * @param firstFile
	 * @param secondFile
	 * @param thirdFile
	 * @throws Exception
	 */
	public static void insertAsciiAsHex(String firstFile, String secondFile, String thirdFile) throws Exception {
		System.out.println("Inserting ascii file \"" + secondFile + "\"\n using table \"" + firstFile
				+ "\"\n to file \"" + thirdFile + "\".");
		HexTable hexTable = new HexTable(firstFile);
		String input = FileUtils.getAsciiFile(secondFile);
		byte[] outFileBytes = FileUtils.getFileBytes(thirdFile);
		String[] lines = input.split(Constants.S_NEWLINE);
		int totalBytesWritten = 0;
		for (int line = 0; line < lines.length; line++) {
			if(lines[line] != null && lines[line].contains(Constants.ADDR_STR)) {
				// Read entry
				OffsetEntry entry = new OffsetEntry(lines[line]);
				line++;

				// Read content (including end)
				StringBuffer content = new StringBuffer();

				// Put lines not starting with |
				while (!lines[line].contains(Constants.S_MAX_BYTES)) {
					if(lines[line] != null && lines[line].length() > 0 && !lines[line].contains(Constants.S_COMMENT_LINE)) {
						content.append(lines[line]);
						if(lines[line].contains(Constants.S_STR_NUM_CHARS)) {
							content.append(Constants.S_NEWLINE);
						}
					}
					line++;
				}
				// End line
				content.append(lines[line]).append(Constants.S_NEWLINE);

				// Process
				Map<Integer, Integer> pointerOffsets = new HashMap<Integer, Integer>();
				byte[] hex = hexTable.toHex(content.toString(), pointerOffsets, entry);
				if(Utils.isDebug()) {
					System.out.println(" TO OFFSET: " + Utils.intToHexString(entry.getStart(), Constants.HEX_ADDR_SIZE));
				}
				System.arraycopy(hex, 0, outFileBytes, entry.start, hex.length);
				totalBytesWritten += hex.length;

				// Insert offsets
				for (Map.Entry<Integer, Integer> e : pointerOffsets.entrySet()) {
					byte[] data = Utils.intToByteArray(e.getValue());
					if ((e.getKey() >= entry.start && e.getKey() <= entry.start + hex.length)
							|| (e.getKey() + data.length >= entry.start
							&& e.getKey() + data.length <= entry.start + hex.length)) {
						System.out.println("INSERTING OFFSET " + e.getValue() + "[" + e.getKey() + ":" + e.getKey()
						+ data.length + "] TO STRING AREA [" + entry.start + ":" + entry.start + hex.length + "]");
					}
					System.arraycopy(data, 0, outFileBytes, e.getKey(), data.length);
				}
			}
		}
		System.out.println("TOTAL BYTES WRITTEN: " + Utils.fillLeft(String.valueOf(totalBytesWritten), Constants.HEX_ADDR_SIZE) + " / " + Utils.intToHexString(totalBytesWritten, Constants.HEX_ADDR_SIZE) + " Hex");
		FileUtils.writeFileBytes(thirdFile, outFileBytes);
	}

	/**
	 *Extracts the ascii from secondFile using table firstFile to thirdFile.
	 * @param firstFile
	 * @param secondFile
	 * @param thirdFile
	 * @param offsetsArg
	 * @throws Exception
	 */
	public static void extractAsciiFile(String firstFile, String secondFile, String thirdFile,
			String offsetsArg) throws Exception {
		System.out.println("Extracting ascii file from \"" + secondFile + "\"\n using table \"" + firstFile
				+ "\"\n to file \"" + thirdFile + "\".");
		HexTable hexTable = new HexTable(firstFile);
		StringBuffer fileOut = new StringBuffer();
		byte[] secondFileBytes = FileUtils.getFileBytes(secondFile);
		for (OffsetEntry entry : Utils.getOffsets(offsetsArg)) {
			fileOut.append(hexTable.toAscii(secondFileBytes, entry));
		}
		FileUtils.writeFileAscii(thirdFile, fileOut.toString());
	}

	/**
	 * Cleans the firstFile extraction data to the secondFile as only
	 * printable chars.
	 * @param firstFile extraction file.
	 * @param secondFile ascii text.
	 * @throws Exception .
	 */
	public static void cleanAsciiFile(String firstFile, String secondFile) throws Exception {
		System.out.println("Cleaning ascii extraction \"" + firstFile + "\" to \"" + secondFile + "\".");
		String[] lines = FileUtils.getAsciiFile(firstFile).split(Constants.S_NEWLINE);
		StringBuffer fileOut = Utils.getLinesCleaned(lines);
		FileUtils.writeFileAscii(secondFile, fileOut.toString());
	}

	/**
	 * Generates a table file for the input string if found on the rom.
	 * @param firstFile .
	 * @param outFilePrefix .
	 * @param searchString .
	 * @throws Exception
	 */
	public static void searchRelative8Bits(String firstFile, String outFilePrefix, String searchString) throws Exception {
		System.out.println("Searching relative string \"" + searchString + "\"\n in \"" + firstFile+"\" "
				+ "\n Generating table files from  \"" + outFilePrefix + ".001\"");
		List<TableSearchResult> hexTables = searchRelative8Bits(FileUtils.getFileBytes(firstFile), searchString);
		int tablesFound = 1;
		List<HexTable> usedTables = new ArrayList<HexTable>();
		for(TableSearchResult t : hexTables) {
			if(!usedTables.contains(t.getHexTable())) {
				FileUtils.writeFileAscii(outFilePrefix + "." + Utils.fillLeft(String.valueOf(tablesFound), 3), t.getHexTable().toAsciiTable());
				usedTables.add(t.getHexTable());
			}
			tablesFound++;
		}
	}

	/**
	 * Searches tables that meet the letter correlation for the target phrase.
	 * @param fileBytes
	 * @param searchString
	 * @return list of tables.
	 * @throws Exception
	 */
	public static List<TableSearchResult> searchRelative8Bits(byte[] fileBytes, String searchString) throws Exception {
		List<TableSearchResult> res = new ArrayList<TableSearchResult>();
		int wordLength = searchString.length();
		if(wordLength < Constants.MIN_SEARCH_WORD_LENGTH) {
			throw new Exception("Minimal word length / Longitud minima de palabra : " + Constants.MIN_SEARCH_WORD_LENGTH);
		}
		byte[] searchBytes = searchString.getBytes(StandardCharsets.US_ASCII);
		for(int i = 0; i < fileBytes.length - wordLength; i++) {
			int displacement = (searchBytes[0] - fileBytes[i]) & Constants.MASK_8BIT;
			if(equivalentChars(displacement, searchBytes, Arrays.copyOfRange(fileBytes, i, i + wordLength))) {
				TableSearchResult tr = new TableSearchResult();
				HexTable ht = new HexTable(displacement);
				tr.setHexTable(ht);
				tr.setOffset(i);
				tr.setWord(searchString);
				if(!res.contains(ht)) {
					res.add(tr);
				}
				i += wordLength - 1;
			}
			if(res.size() > 999) {
				break;
			}
		}
		return res;
	}

	/**
	 * Gets the offsets for the string on the file using the table.
	 * @param fileBytes .
	 * @param hexTable .
	 * @param searchString .
	 * @param ignoreCase .
	 * @return .
	 * @throws Exception .
	 */
	public static List<Integer> findString(byte[] fileBytes, HexTable hexTable, String searchString, boolean ignoreCase) throws Exception {
		List<Integer> res = new ArrayList<Integer>();
		int wordLength = searchString.length();
		if(ignoreCase) {
			searchString = searchString.toUpperCase();
		}
		if(wordLength < Constants.MIN_SEARCH_WORD_LENGTH) {
			throw new Exception("Minimal word length / Longitud minima de palabra : " + Constants.MIN_SEARCH_WORD_LENGTH);
		}
		for(int i = 0; i < fileBytes.length - wordLength; i++) {
			String word = hexTable.toAscii(Arrays.copyOfRange(fileBytes, i, i + wordLength));
			if(ignoreCase) {
				word = word.toUpperCase();
			}
			boolean areEqual = true;
			for(int j = 0; j < wordLength; j++) {
				if(searchString.charAt(j) != Constants.CHR_ASTER && searchString.charAt(j) != word.charAt(j)) {
					areEqual = false;
				}
			}
			if(areEqual) {
				if(!res.contains(i)) {
					res.add(i);
				}
				i += wordLength - 1;
			}
			if(res.size() > 999) {
				break;
			}
		}
		return res;
	}

	private static boolean equivalentChars(int displacement, byte[] searchBytes, byte[] fileBytes) {
		boolean res = true;
		for(int i = 0; i < searchBytes.length; i++) {
			if(searchBytes[i] != Constants.BYTE_ASTER  &&
					(searchBytes[i] & Constants.MASK_8BIT) !=
					((fileBytes[i] + displacement) & Constants.MASK_8BIT)) {
				res = false;
			}
		}
		return res;
	}

	/**
	 * Searches all the strings on the rom for the given table</br>
	 * for the default dictionary name (EngDict.txt).
	 * @param tableFile
	 * @param dataFile
	 * @param numIgnoredChars
	 * @param endChars
	 * @throws Exception
	 */
	public static void searchAllStrings(String tableFile, String dataFile,
			int numIgnoredChars, String endChars) throws Exception {
		searchAllStrings(tableFile, dataFile, numIgnoredChars, endChars, Constants.DEFAULT_DICT);
	}

	/**
	 * Searches all the strings on the rom for the given table.
	 * @param tableFile
	 * @param dataFile
	 * @param numIgnoredChars
	 * @param endChars
	 * @throws Exception
	 */
	public static void searchAllStrings(String tableFile, String dataFile,
			int numIgnoredChars, String endChars, String dictFile) throws Exception {
		String extractFile = dataFile + Constants.EXTRACT_EXTENSION;
		System.out.println(
				"Extracting all strings from \"" + dataFile + "\"\n to \"" + extractFile + "\" and \"" + extractFile
				+ Constants.OFFSET_EXTENSION + "\" \n " + "using \"" + tableFile + "\" \n numIgnoredChars: "
				+ numIgnoredChars + "\n endChars: " + endChars + "\n dictionary: " + dictFile);
		HexTable hexTable = new HexTable(tableFile);
		byte[] secondFileBytes = FileUtils.getFileBytes(dataFile);
		List<String> endCharsList = Arrays.asList(
				endChars.toUpperCase().replaceAll(Constants.SPACE_STR, Constants.EMPTY).split(Constants.OFFSET_CHAR_SEPARATOR));
		String offsets = hexTable.getAllEntries(secondFileBytes, Constants.MIN_NUM_CHARS_WORD, numIgnoredChars, endCharsList, dictFile);
		System.out.println("Extracted offsets: \n " + offsets);
		FileUtils.writeFileAscii(extractFile + Constants.OFFSET_EXTENSION, offsets);
		extractAsciiFile(tableFile, dataFile, extractFile, offsets);
	}

	/**
	 * Extracts all the offsets of a given extraction file, useful after cleaning invalid entries of
	 * search all strings.
	 * @param extractFile file to search.
	 * @param extractFileArgs output file.
	 * @throws Exception io error.
	 */
	public static void cleanExtractedFile(String extractFile, String extractFileArgs) throws Exception {
		System.out.println("Getting offsets from \"" + extractFile + "\"\n to \"" + extractFileArgs+"\"");
		StringBuilder fileArgs = new StringBuilder();
		String[] lines = FileUtils.getAsciiFile(extractFile).split(Constants.S_NEWLINE);
		for(String line : lines) {
			line = line.trim();
			if(line.startsWith(Constants.ADDR_STR)) {
				fileArgs.append(line.substring(Constants.ADDR_STR.length()));
				fileArgs.append(Constants.OFFSET_STR_SEPARATOR);
			}
		}
		List<OffsetEntry> entries = Utils.getOffsets(fileArgs.toString());
		Collections.sort(entries);
		fileArgs.setLength(0);
		for(OffsetEntry entry: entries) {
			if(fileArgs.length() > 0) {
				fileArgs.append(Constants.OFFSET_STR_SEPARATOR);
			}
			fileArgs.append(entry.toEntryString());
		}
		FileUtils.writeFileAscii(extractFileArgs, fileArgs.toString());
	}

	/**
	 * Extracts HEX data from the inputFile to the outputFile</br>
	 * using the entries.
	 * @param inputFile .
	 * @param outputFile .
	 * @param entries .
	 * @throws Exception .
	 */
	public static void extractHexData(String inputFile, String outputFile, String entries) throws Exception {
		System.out.println("Extracting hex from \"" + inputFile + "\"\n to \"" + outputFile + "\""
				+ "\n using \"" + entries + "\"");
		StringBuilder hexDataString = new StringBuilder();
		byte[] inputFileBytes = FileUtils.getFileBytes(inputFile);
		for(OffsetEntry entry : Utils.getHexOffsets(entries)) {
			hexDataString.append(entry.getHexComment());
			hexDataString.append(Constants.S_NEWLINE);
			hexDataString.append(entry.getHexString(inputFileBytes));
			hexDataString.append(entry.getHexTarget());
			hexDataString.append(Constants.S_NEWLINE);
		}
		FileUtils.writeFileAscii(outputFile, hexDataString.toString());
	}

	/**
	 * Returns offsets as a unique line.
	 * @param string
	 * @return
	 * @throws Exception
	 */
	public static String getCleanOffsets(String string) throws Exception {
		return FileUtils.getAsciiFile(string).replaceAll(Constants.S_NEWLINE, Constants.EMPTY).replaceAll(Constants.S_CRETURN, Constants.EMPTY);
	}

	/**
	 * Auto translates the text in 2 passes, 1 will
	 * @param toTransFile
	 * @param transFile
	 * @param outFile
	 * @throws Exception
	 */
	public static void autoTranslateWithDictionary(String toTransFile, String transFile, String outFile) throws Exception {
		//TODO???
	}

	/**
	 * Check if the line lengths are ok.
	 * @param toCheckFile
	 * @throws Exception
	 */
	public static void checkLineLength(String toCheckFile) throws Exception {
		System.out.println("Checking file lines of \"" + toCheckFile);
		//1-Read utf8 file
		String[] transFileLines = FileUtils.getAsciiFile(toCheckFile).split(Constants.S_NEWLINE);

		//2-Extract "dictionary"
		Map<String, String> dictionary = Utils.extractDictionary(transFileLines);

		//3-Check that pairs are length equal, else show a warning
		for(Map.Entry<String, String> e : dictionary.entrySet()) {
			if(!Utils.checkLineLength(e.getKey(), e.getValue())) {
				System.out.println("Error en lineas:");
				System.out.println(e.getKey());
				System.out.println(e.getValue());
			}
		}
	}

}
