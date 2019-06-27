package com.wave.hextractor.util;

import com.wave.hextractor.object.HexTable;
import com.wave.hextractor.pojo.FileWithDigests;
import com.wave.hextractor.pojo.OffsetEntry;
import com.wave.hextractor.pojo.TableSearchResult;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.zip.CRC32;

import static java.lang.String.format;
import static java.lang.String.valueOf;

/**
 * Utility class for files.
 * @author slcantero
 */
public class FileUtils {

	/** The Constant FILES_SEPARATOR. */
	private static final String FILES_SEPARATOR = "\"\n to file \"";
	
	/** The Constant MD5_DIGEST. */
	private static final String MD5_DIGEST = "MD5";
	
	/** The Constant SHA1_DIGEST. */
	private static final String SHA1_DIGEST = "SHA-1";
	
	private static final DateTimeFormatter GAME_DATE_DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

	private static final DateTimeFormatter GAME_YEAR_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy");

	private static final String COMMA_THE = ", The";
	
	/**
	 * Instantiates a new file utils.
	 */
	private FileUtils() {
	}

	/**
	 * Gets the extension of a file, i.e.: file.ext => ext
	 * @param file file to look at.
	 * @return the extension.
	 */
	public static String getFileExtension(File file) {
		return getFileExtension(file.getName());
	}

	/**
	 * Gets the extension of a file, i.e.: file.ext => ext
	 * @return the extension.
	 */
	public static String getFileExtension(String fileName) {
		String extension = Constants.EMPTY;
		int i = fileName.lastIndexOf(Constants.CHR_DOT);
		if (i > 0) {
			extension = fileName.substring(i + 1);
		}
		return extension;
	}

	/**
	 * Gets the file path without separator.
	 *
	 * @param file the file
	 * @return the file path
	 */
	public static String getFilePath(File file) {
		return getFilePath(file.getAbsolutePath());
	}

	/**
	 * Gets the file path without separator.
	 *
	 * @param absolutePath the absolute path
	 * @return the file path
	 */
	private static String getFilePath(String absolutePath) {
		return absolutePath.substring(0, absolutePath.lastIndexOf(File.separator));
	}

	/**
	 * Returns the ascii file with only Constants.NEWLINE as line separators.
	 * @param filename .
	 * @return .
	 * @throws FileNotFoundException .
	 */
	public static String getAsciiFile(String filename) throws FileNotFoundException {
		Scanner scanner = new Scanner(new File(filename), Constants.UTF8_ENCODING);
		String input = scanner.useDelimiter("\\A").next();
		scanner.close();
		input = input.replace(Constants.NEWLINE + valueOf(Constants.CRETURN),
				valueOf(Constants.NEWLINE));
		input = input.replace(Constants.CRETURN + valueOf(Constants.NEWLINE),
				valueOf(Constants.NEWLINE));
		input = input.replace(valueOf(Constants.CRETURN), valueOf(Constants.NEWLINE));
		return input;
	}

	/**
	 * Extracts Ascii data on packed 3 bytes to 4 characters</br>
	 * using the entries.
	 * @param inputFile .
	 * @param outputFile .
	 * @param entries .
	 * @throws IOException .
	 */
	public static void extractAscii3To4Data(String table, String inputFile, String outputFile, String entries)
			throws IOException {
		Utils.log("Extracting ascii 3 to 4 from \"" + inputFile + FILES_SEPARATOR + outputFile + "\" \n using \""
				+ entries + "\" and table: \"" + table + "\"");
		StringBuilder dataString = new StringBuilder();
		byte[] inputFileBytes = Files.readAllBytes(Paths.get(inputFile));
		HexTable hexTable = new HexTable(table);
		for (OffsetEntry entry : Utils.getOffsets(entries)) {
			byte[] origData = Arrays.copyOfRange(inputFileBytes, entry.getStart(), entry.getEnd() + 1);
			byte[] expData = Utils.getExpanded3To4Data(origData);
			byte[] compData = Utils.getCompressed4To3Data(expData);
			if (Utils.isDebug()) {
				Utils.log("Original data     " + entry.getHexTarget() + " - "
						+ Utils.getHexArea(0, origData.length, origData));
				Utils.log("Expanded data     " + entry.getHexTarget() + " - "
						+ Utils.getHexArea(0, expData.length, expData));
				Utils.log("Recompressed data " + entry.getHexTarget() + " - "
						+ Utils.getHexArea(0, compData.length, compData));
			}
			if (!Arrays.equals(origData, compData)) {
				Utils.log("ERROR! RECOMPRESSED DATA IS DIFFERENT!!!");
			}
			String line = hexTable.toAscii(expData, false, true);
			dataString.append(Constants.COMMENT_LINE);
			dataString.append(line);
			dataString.append(Constants.S_NEWLINE);
			dataString.append(line);
			dataString.append(entry.getHexTarget());
			dataString.append(Constants.S_NEWLINE);
		}
		writeFileAscii(outputFile, dataString.toString());
	}

	/**
	 * Inserts ascii as hex from a 4 to 3 data.
	 */
	public static void insertHex4To3Data(String table, String inputFile, String outputFile) throws IOException {
		Utils.log("Inserting ascii as hex 4 to 3 from \"" + inputFile + FILES_SEPARATOR + outputFile
				+ "\"\n using table: \"" + table + "\"");
		byte[] outFileBytes = Files.readAllBytes(Paths.get(outputFile));
		HexTable hexTable = new HexTable(table);
		for (String entry : Utils.removeCommentsAndJoin(getAsciiFile(inputFile))) {
			String[] entryDataAndOffset = entry.split(Constants.ADDR_STR);
			OffsetEntry offEntry = OffsetEntry.fromHexRange(entryDataAndOffset[1]);
			byte[] compData = Utils.getCompressed4To3Data(hexTable.toHex(entryDataAndOffset[0]));
			System.arraycopy(compData, 0, outFileBytes, offEntry.getStart(), compData.length);
		}
		writeFileBytes(outputFile, outFileBytes);
	}

	/**
	 * Writes all the bytes to the filename.</br>
	 * <b>Overwrites the file if exists.</b>
	 *
	 * @param filename file to write to.
	 * @param b file bytes.
	 * @throws IOException the exception
	 */
	static void writeFileBytes(String filename, byte[] b) throws IOException {
		try (FileOutputStream stream = new FileOutputStream(filename)) {
			stream.write(b);
		}
	}

	/**
	 * Writes a string as UTF8 in the destination file.
	 * <b>Overwrites the file if exists.</b>
	 *
	 * @param filename file to write.
	 * @param ascii string containing the text.
	 * @throws IOException the exception
	 */
	public static void writeFileAscii(String filename, String ascii) throws IOException {
		try (PrintWriter out = new PrintWriter(filename, Constants.UTF8_ENCODING)) {
			out.print(ascii);
		}
	}

	/**
	 * Insert hex data.
	 *
	 * @param firstFile the first file
	 * @param secondFile the second file
	 * @throws IOException the exception
	 */
	public static void insertHexData(String firstFile, String secondFile) throws IOException {
		Utils.log("Inserting hex file \"" + firstFile + FILES_SEPARATOR + secondFile + "\".");
		byte[] b = Files.readAllBytes(Paths.get(secondFile));
		Utils.loadHex(getAsciiFile(firstFile), b);
		writeFileBytes(secondFile, b);
	}

	/**
	 * Insert ascii as hex.
	 *
	 * @param firstFile the first file
	 * @param secondFile the second file
	 * @param thirdFile the third file
	 * @throws IOException the exception
	 */
	public static void insertAsciiAsHex(String firstFile, String secondFile, String thirdFile) throws IOException {
		Utils.log("Inserting ascii file \"" + secondFile + "\"\n using table \"" + firstFile + FILES_SEPARATOR
				+ thirdFile + "\".");
		HexTable hexTable = new HexTable(firstFile);
		String input = getAsciiFile(secondFile);
		byte[] outFileBytes = Files.readAllBytes(Paths.get(thirdFile));
		String[] lines = input.split(Constants.S_NEWLINE);
		int totalBytesWritten = 0;
		for (int line = 0; line < lines.length; line++) {
			if (lines[line] != null && lines[line].contains(Constants.ADDR_STR)) {
				// Read entry
				OffsetEntry entry = new OffsetEntry(lines[line]);
				line++;
				// Read content (including end)
				StringBuilder content = new StringBuilder();
				// Put lines not starting with |
				while (!lines[line].contains(Constants.S_MAX_BYTES)) {
					if (lines[line] != null && lines[line].length() > 0
							&& !lines[line].contains(Constants.S_COMMENT_LINE)) {
						content.append(lines[line]);
						if (lines[line].contains(Constants.S_STR_NUM_CHARS)) {
							content.append(Constants.S_NEWLINE);
						}
					}
					line++;
				}
				// End line
				content.append(lines[line]).append(Constants.S_NEWLINE);

				// Process
				Map<Integer, Integer> pointerOffsets = new HashMap<>();
				byte[] hex = hexTable.toHex(content.toString(), pointerOffsets, entry);
				if (Utils.isDebug()) {
					Utils.log(" TO OFFSET: " + Utils.intToHexString(entry.getStart(), Constants.HEX_ADDR_SIZE));
				}
				System.arraycopy(hex, 0, outFileBytes, entry.getStart(), hex.length);
				totalBytesWritten += hex.length;

				// Insert offsets
				for (Map.Entry<Integer, Integer> e : pointerOffsets.entrySet()) {
					byte[] data = Utils.intToByteArray(e.getValue());
					if (e.getKey() >= entry.getStart() && e.getKey() <= entry.getStart() + hex.length
							|| e.getKey() + data.length >= entry.getStart()
							&& e.getKey() + data.length <= entry.getStart() + hex.length) {
						Utils.log("INSERTING OFFSET " + e.getValue() + "[" + e.getKey() + ":" + e.getKey() + data.length
								+ "] TO STRING AREA [" + entry.getStart() + ":" + entry.getStart() + hex.length + "]");
					}
					System.arraycopy(data, 0, outFileBytes, e.getKey(), data.length);
				}
			}
		}
		Utils.log("TOTAL BYTES WRITTEN: " + Utils.fillLeft(valueOf(totalBytesWritten), Constants.HEX_ADDR_SIZE)
		+ " / " + Utils.intToHexString(totalBytesWritten, Constants.HEX_ADDR_SIZE) + " Hex");
		writeFileBytes(thirdFile, outFileBytes);
	}

	/**
	 * Extracts the ascii from secondFile using table firstFile to thirdFile.
	 *
	 * @param firstFile the first file
	 * @param secondFile the second file
	 * @param thirdFile the third file
	 * @param offsetsArg the offsets arg
	 * @throws IOException the exception
	 */
	public static void extractAsciiFile(String firstFile, String secondFile, String thirdFile, String offsetsArg)
			throws IOException {
		Utils.log("Extracting ascii file from \"" + secondFile + "\"\n using table \"" + firstFile + FILES_SEPARATOR
				+ thirdFile + "\".");
		extractAsciiFile(new HexTable(firstFile), Files.readAllBytes(Paths.get(secondFile)), thirdFile, offsetsArg,
				true);
	}

	/**
	 * Extracts the ascii file.
	 */
	private static void extractAsciiFile(HexTable hexTable, byte[] fileBytes, String outFile, String offsetsArg,
										 boolean showExtractions) throws IOException {
		if (offsetsArg != null && offsetsArg.length() > 0) {
			extractAsciiFile(hexTable, fileBytes, outFile, Utils.getOffsets(offsetsArg), showExtractions);
		}
	}

	/**
	 * Extracts the ascii file.
	 */
	public static void extractAsciiFile(HexTable hexTable, byte[] fileBytes, String outFile, List<OffsetEntry> offsets,
			boolean showExtractions) throws IOException {
		StringBuilder fileOut = new StringBuilder();
		if (offsets != null && !offsets.isEmpty()) {
			for (OffsetEntry entry : offsets) {
				fileOut.append(hexTable.toAscii(fileBytes, entry, showExtractions));
			}
		}
		writeFileAscii(outFile, fileOut.toString());
	}

	/**
	 * Cleans the firstFile extraction data to the secondFile as only
	 * printable chars.
	 * @param firstFile extraction file.
	 * @param secondFile ascii text.
	 * @throws IOException .
	 */
	public static void cleanAsciiFile(String firstFile, String secondFile) throws IOException {
		Utils.log("Cleaning ascii extraction \"" + firstFile + "\" to \"" + secondFile + "\".");
		writeFileAscii(secondFile,
				Utils.getLinesCleaned(getAsciiFile(firstFile).split(Constants.S_NEWLINE)).toString());
	}

	/**
	 * Generates a table file for the input string if found on the rom.
	 *
	 * @param firstFile .
	 * @param outFilePrefix .
	 * @param searchString .
	 * @throws IOException the exception
	 */
	public static void searchRelative8Bits(String firstFile, String outFilePrefix, String searchString)
			throws IOException {
		Utils.log("Searching relative string \"" + searchString + "\"\n in \"" + firstFile + "\" "
				+ "\n Generating table files from  \"" + outFilePrefix + ".001\"");
		List<TableSearchResult> hexTables = searchRelative8Bits(Files.readAllBytes(Paths.get(firstFile)), searchString);
		int tablesFound = 1;
		List<HexTable> usedTables = new ArrayList<>();
		for (TableSearchResult t : hexTables) {
			if (!usedTables.contains(t.getHexTable())) {
				writeFileAscii(outFilePrefix + "." + Utils.fillLeft(valueOf(tablesFound), 3),
						t.getHexTable().toAsciiTable());
				usedTables.add(t.getHexTable());
			}
			tablesFound++;
		}
	}

	/**
	 * Searches tables that meet the letter correlation for the target phrase.
	 *
	 * @param fileBytes the file bytes
	 * @param searchString the search string
	 * @return list of tables.
	 */
	private static List<TableSearchResult> searchRelative8Bits(byte[] fileBytes, String searchString) {
		List<TableSearchResult> res = new ArrayList<>();
		int wordLength = searchString.length();
		if (wordLength < Constants.MIN_SEARCH_WORD_LENGTH) {
			throw new IllegalArgumentException(
					"Minimal word length / Longitud minima de palabra : " + Constants.MIN_SEARCH_WORD_LENGTH);
		}
		byte[] searchBytes = searchString.getBytes(StandardCharsets.US_ASCII);
		for (int i = 0; i < fileBytes.length - wordLength; i++) {
			int displacement = searchBytes[0] - fileBytes[i] & Constants.MASK_8BIT;
			if (equivalentChars(displacement, searchBytes, Arrays.copyOfRange(fileBytes, i, i + wordLength))) {
				TableSearchResult tr = new TableSearchResult();
				HexTable ht = new HexTable(displacement);
				tr.setHexTable(ht);
				tr.setOffset(i);
				tr.setWord(searchString);
				if (!res.contains(tr)) {
					res.add(tr);
				}
				i += wordLength - 1;
			}
			if (res.size() > 999) {
				break;
			}
		}
		return res;
	}

	/**
	 * Searches relative but * can be expanded to up to expansion number of chars.
	 * @param fileBytes the file bytes
	 * @param searchString the search string
	 * @param expansion number of chars * can represent
	 * @return list of tables.
	 */
	public static List<TableSearchResult> multiSearchRelative8Bits(byte[] fileBytes, String searchString, int expansion) {
		Set<TableSearchResult>  res = new HashSet<>();
		String replacement = "";
		if(searchString.contains(Constants.STR_ASTER)) {
			for(int i = 0; i < expansion; i++) {
				replacement += Constants.STR_ASTER;
				res.addAll(searchRelative8Bits(fileBytes, searchString.replaceAll(Constants.REGEX_STR_ASTER, replacement)));
			}
		}
		else {
			res.addAll(searchRelative8Bits(fileBytes, searchString));
		}
		return new ArrayList<>(res);
	}

	/**
	 * Gets the offsets for the string on the file using the table.
	 * @param fileBytes .
	 * @param hexTable .
	 * @param searchString .
	 * @param ignoreCase .
	 * @return .
	 * @throws IllegalArgumentException .
	 */
	private static List<Integer> findString(byte[] fileBytes, HexTable hexTable, String searchString,
											boolean ignoreCase) {
		List<Integer> res = new ArrayList<>();
		int wordLength = searchString.length();
		if (ignoreCase) {
			searchString = searchString.toUpperCase();
		}
		if (wordLength < Constants.MIN_SEARCH_WORD_LENGTH) {
			throw new IllegalArgumentException(
					"Minimal word length / Longitud minima de palabra : " + Constants.MIN_SEARCH_WORD_LENGTH);
		}
		for (int i = 0; i < fileBytes.length - wordLength; i++) {
			String word = hexTable.toAscii(Arrays.copyOfRange(fileBytes, i, i + wordLength), true);
			if (ignoreCase) {
				word = word.toUpperCase();
			}
			boolean areEqual = true;
			for (int j = 0; j < wordLength; j++) {
				if (searchString.charAt(j) != Constants.CHR_ASTER && searchString.charAt(j) != word.charAt(j)) {
					areEqual = false;
				}
			}
			if (areEqual) {
				if (!res.contains(i)) {
					res.add(i);
				}
				i += wordLength - 1;
			}
			if (res.size() > 999) {
				break;
			}
		}
		return res;
	}

	/**
	 * Searches but * can be expanded to up to expansion number of chars.
	 * @param fileBytes .
	 * @param hexTable .
	 * @param searchString .
	 * @param ignoreCase .
	 * @param expansion .
	 * @return .
	 * @throws IllegalArgumentException .
	 */
	public static List<TableSearchResult> multiFindString(byte[] fileBytes, HexTable hexTable, String searchString,
			boolean ignoreCase, int expansion) {
		List<TableSearchResult> res = new ArrayList<>();
		String replacement = "";
		if (searchString.contains(Constants.STR_ASTER)) {
			for (int i = 0; i < expansion; i++) {
				replacement += Constants.STR_ASTER;
				String searchStrRep = searchString.replaceAll(Constants.REGEX_STR_ASTER, replacement);
				res.addAll(toTableResults(hexTable, searchStrRep,
						findString(fileBytes, hexTable, searchStrRep, ignoreCase)));
			}
		}
		else {
			res.addAll(
					toTableResults(hexTable, searchString, findString(fileBytes, hexTable, searchString, ignoreCase)));
		}
		return new ArrayList<>(res);
	}

	/**
	 * To table results.
	 * @return the list
	 */
	private static List<TableSearchResult> toTableResults(HexTable hexTable, String searchString,
														  List<Integer> list) {
		List<TableSearchResult> searchRes = new ArrayList<>();
		for (Integer res : list) {
			TableSearchResult tsr = new TableSearchResult();
			tsr.setHexTable(hexTable);
			tsr.setOffset(res);
			tsr.setWord(searchString);
			searchRes.add(tsr);
		}
		return searchRes;
	}

	/**
	 * Equivalent chars.
	 *
	 * @param displacement the displacement
	 * @param searchBytes the search bytes
	 * @param fileBytes the file bytes
	 * @return true, if successful
	 */
	private static boolean equivalentChars(int displacement, byte[] searchBytes, byte[] fileBytes) {
		boolean res = true;
		for (int i = 0; i < searchBytes.length; i++) {
			if (searchBytes[i] != Constants.BYTE_ASTER
					&& (searchBytes[i] & Constants.MASK_8BIT) != (fileBytes[i] + displacement & Constants.MASK_8BIT)) {
				res = false;
			}
		}
		return res;
	}

	/**
	 * Searches all the strings on the rom for the given table</br>
	 * for the default dictionary name (EngDict.txt).
	 *
	 * @param tableFile the table file
	 * @param dataFile the data file
	 * @param numIgnoredChars the num ignored chars
	 * @param endChars the end chars
	 * @throws IOException the exception
	 */
	public static void searchAllStrings(String tableFile, String dataFile, int numIgnoredChars, String endChars)
			throws IOException {
		searchAllStrings(tableFile, dataFile, numIgnoredChars, endChars, Constants.DEFAULT_DICT);
	}

	/**
	 * Searches all the strings on the rom for the given table.
	 *
	 * @param tableFile the table file
	 * @param dataFile the data file
	 * @param numIgnoredChars the num ignored chars
	 * @param endChars the end chars
	 * @param dictFile the dict file
	 * @throws IOException the exception
	 */
	public static void searchAllStrings(String tableFile, String dataFile, int numIgnoredChars, String endChars,
			String dictFile) throws IOException {
		String extractFile = dataFile + Constants.EXTRACT_EXTENSION;
		Utils.log(
				"Extracting all strings from \"" + dataFile + FILES_SEPARATOR + extractFile + "\" and \"" + extractFile
				+ Constants.OFFSET_EXTENSION + "\" \n " + "using \"" + tableFile + "\" \n numIgnoredChars: "
				+ numIgnoredChars + "\n endChars: " + endChars + "\n dictionary: " + dictFile);
		searchAllStrings(new HexTable(tableFile), Files.readAllBytes(Paths.get(dataFile)), numIgnoredChars, endChars,
				dictFile, dataFile + Constants.EXTRACT_EXTENSION);
	}

	/**
	 * Searches all the strings on the rom for the given table.
	 */
	public static void searchAllStrings(HexTable hexTable, byte[] fileBytes, int numIgnoredChars, String endChars,
										String dictFile, String extractFile) throws IOException {
		String entries = hexTable.getAllEntries(fileBytes,
				Constants.MIN_NUM_CHARS_WORD, numIgnoredChars, Arrays.asList(endChars.toUpperCase()
						.replaceAll(Constants.SPACE_STR, Constants.EMPTY).split(Constants.OFFSET_CHAR_SEPARATOR)),
				dictFile);
		if (entries != null && entries.length() > 0) {
			extractAsciiFile(hexTable, fileBytes, extractFile, entries, false);
		}
	}

	/**
	 * Extracts all the offsets of a given extraction file, useful after cleaning invalid entries of
	 * search all strings.
	 * @param extractFile file to search.
	 * @param extractFileArgs output file.
	 * @throws IOException io error.
	 */
	public static void cleanExtractedFile(String extractFile, String extractFileArgs) throws IOException {
		Utils.log("Getting offsets from \"" + extractFile + FILES_SEPARATOR + extractFileArgs + "\"");
		writeFileAscii(extractFileArgs, cleanExtractedFile(extractFile));
	}

	/**
	 * Extracts all the offsets of a given extraction file, useful after cleaning invalid entries of
	 * search all strings.
	 * @param extractFile file to search.
	 * @throws IOException io error.
	 */
	public static String cleanExtractedFile(String extractFile) throws IOException {
		Utils.log("Getting offsets from \"" + extractFile);
		StringBuilder fileArgs = new StringBuilder();
		String[] lines = getAsciiFile(extractFile).split(Constants.S_NEWLINE);
		for (String line : lines) {
			line = line.trim();
			if (line.startsWith(Constants.ADDR_STR)) {
				fileArgs.append(line.substring(Constants.ADDR_STR.length()));
				fileArgs.append(Constants.OFFSET_STR_SEPARATOR);
			}
		}
		List<OffsetEntry> entries = Utils.getOffsets(fileArgs.toString());
		Collections.sort(entries);
		fileArgs.setLength(0);
		for (OffsetEntry entry : entries) {
			if (fileArgs.length() > 0) {
				fileArgs.append(Constants.OFFSET_STR_SEPARATOR);
			}
			fileArgs.append(entry.toEntryString());
		}
		return fileArgs.toString();
	}

	/**
	 * Extracts HEX data from the inputFile to the outputFile</br>
	 * using the entries.
	 * @param inputFile .
	 * @param outputFile .
	 * @param entries .
	 * @throws IOException .
	 */
	public static void extractHexData(String inputFile, String outputFile, String entries) throws IOException {
		Utils.log("Extracting hex from \"" + inputFile + FILES_SEPARATOR + outputFile + "\"" + "\n using \"" + entries
				+ "\"");
		StringBuilder hexDataString = new StringBuilder();
		byte[] inputFileBytes = Files.readAllBytes(Paths.get(inputFile));
		for (OffsetEntry entry : Utils.getHexOffsets(entries)) {
			hexDataString.append(entry.getHexComment());
			hexDataString.append(Constants.S_NEWLINE);
			hexDataString.append(entry.getHexString(inputFileBytes));
			hexDataString.append(entry.getHexTarget());
			hexDataString.append(Constants.S_NEWLINE);
		}
		writeFileAscii(outputFile, hexDataString.toString());
	}

	/**
	 * Returns offsets as a unique line.
	 *
	 * @param fileName the file
	 * @return the clean offsets
	 * @throws FileNotFoundException the exception
	 */
	public static String getCleanOffsets(String fileName) throws FileNotFoundException {
		return getCleanOffsetsString(getAsciiFile(fileName));
	}

	/**
	 * Returns offsets as a unique line from a string.
	 *
	 * @return the clean offsets
	 */
	public static String getCleanOffsetsString(String string) {
		return string.replaceAll(Constants.S_NEWLINE, Constants.EMPTY).replaceAll(Constants.S_CRETURN, Constants.EMPTY);
	}

	/**
	 * Check if the line lengths are ok.
	 *
	 * @param toCheckFile the to check file
	 * @throws FileNotFoundException the exception
	 */
	public static void checkLineLength(String toCheckFile) throws FileNotFoundException {
		Utils.log("Checking file lines of \"" + toCheckFile);
		// 1-Read utf8 file
		String[] transFileLines = getAsciiFile(toCheckFile).split(Constants.S_NEWLINE);

		// 2-Extract "dictionary"
		Map<String, String> dictionary = Utils.extractDictionary(transFileLines);

		// 3-Check that pairs are length equal, else show a warning
		for (Map.Entry<String, String> e : dictionary.entrySet()) {
			if (!Utils.checkLineLength(e.getKey(), e.getValue())) {
				Utils.log("Error en lineas:");
				Utils.log(e.getKey());
				Utils.log(e.getValue());
			}
		}
	}

	/**
	 * Separates the string based on the table entry of the first character,
	 * adds newline after the desired chars.
	 */
	public static void separateCharLength(String file, String table, String outFile) throws IOException {
		Utils.log("Separating string from \"" + file + FILES_SEPARATOR + outFile + "\"" + "\n using table: \"" + table
				+ "\"");
		writeFileAscii(outFile, separateCharLength(getAsciiFile(file), new HexTable(table)));
	}

	/**
	 * Separates the string based on the table entry of the first character,
	 * adds newline after the desired chars.
	 *
	 * @param text the text
	 * @param table the table
	 * @return the string
	 */
	private static String separateCharLength(String text, HexTable table) {
		StringBuilder res = new StringBuilder();
		int length = text.length();
		for (int i = 0; i < length;) {
			String lenChar = text.substring(i, i + 1);
			int strLen = table.toHex(lenChar)[0];
			if (strLen == 0) {
				res.append(lenChar);
				i++;
			} else {
				int endLength = i + 1 + strLen;
				if (endLength > length) {
					endLength = length;
				}
				res.append(Constants.S_NEWLINE).append(lenChar).append(Constants.S_NEWLINE);
				res.append(text, i + 1, endLength);
				i += strLen + 1;
			}
		}
		return res.toString();
	}

	/**
	 * Checks if all files exist.
	 *
	 * @param files the args
	 * @return true, if successful
	 */
	public static boolean allFilesExist(String[] files) {
		boolean res = true;
		for (String file : files) {
			File f = new File(file);
			res &= f.exists() && !f.isDirectory();
		}
		return res;
	}

	/**
	 * Replaces bytes on baseFile starting at offset for the ones on replacementFile.
	 */
	public static void replaceFileData(String baseFile, String replacementFile, Integer offset) throws IOException {
		Utils.log("Replacing bytes on file: '" + baseFile + "' on offset (dec): " + offset + " with file: '" + replacementFile + "'");
		byte[] baseData = Files.readAllBytes(Paths.get(baseFile));
		byte[] replacementData = Files.readAllBytes(Paths.get(replacementFile));
		System.arraycopy(replacementData, 0, baseData, offset, replacementData.length);
		writeFileBytes(baseFile, baseData);
	}

	/**
	 * Outputs the file SHA1, MD5 and CRC32 (in hex), with file name and bytes
	 * FILE
	 * MD5: XXXXXXXXXXXXX
	 * SHA1: XXXXXXXXXXXXX
	 * CRC32: XXXXXXXXXXXXX
	 * XXXXXXX bytes
	 */
	public static void outputFileDigests(String file) throws IOException {
		Utils.log(getFileDigests(getFileWithDigests(file)));
	}
	
	/**
	 * Gets the file SHA1, MD5 and CRC32 (in hex), with file name and bytes
	 * FILE
	 * MD5: XXXXXXXXXXXXX
	 * SHA1: XXXXXXXXXXXXX
	 * CRC32: XXXXXXXXXXXXX
	 * XXXXXXX bytes
	 */
	private static String getFileDigests(FileWithDigests fileWithDigests) {
		StringBuilder fileDigests = new StringBuilder(fileWithDigests.getName()).append(System.lineSeparator());
		fileDigests.append("MD5: ").append(fileWithDigests.getMd5()).append(System.lineSeparator());
		fileDigests.append("SHA1: ").append(fileWithDigests.getSha1()).append(System.lineSeparator());
		fileDigests.append("CRC32: ").append(fileWithDigests.getCrc32()).append(System.lineSeparator());
		fileDigests.append(format("%d", fileWithDigests.getBytes().length)).append(" bytes");
		return fileDigests.toString();
	}

	/**
	 * Returns the file with the digests
	 */
	static FileWithDigests getFileWithDigests(String fileName) throws IOException {
		FileWithDigests fileWithDigests = new FileWithDigests();
		File file = new File(fileName);
		fileWithDigests.setName(file.getName());
		fileWithDigests.setBytes(Files.readAllBytes(Paths.get(file.getAbsolutePath())));
		fileWithDigests.setCrc32(getCrc32Hex(fileWithDigests.getBytes()));
		fileWithDigests.setMd5(getDigestHex(fileWithDigests.getBytes(), MD5_DIGEST));
		fileWithDigests.setSha1(getDigestHex(fileWithDigests.getBytes(), SHA1_DIGEST));
		return fileWithDigests;
	}

	private static String getCrc32Hex(byte[] bytes) {
		CRC32 crc32 = new CRC32();
		crc32.update(bytes);
		return format("%08X", crc32.getValue()).toLowerCase();
	}

	private static String getDigestHex(byte[] bytes, String digest) {
		String res = "";
		try {
			res = Utils.bytesToHex(MessageDigest.getInstance(digest).digest(bytes));
		} catch (NoSuchAlgorithmException e) {
			Utils.logException(e);
		}
		return res;
	}

	/**
	 * Fills the variables {GAME}, {SYSTEM} and {HASHES} from the file settings based on 
	 * the extension.
	 */
	public static void fillGameData(String emptyDataFile, String filledDataFile, String fileName) throws IOException {
		Utils.log("Filling game data from: \"" + emptyDataFile + "\""); 
		Utils.log(" to: \"" + filledDataFile + "\"");
		Utils.log(" for file: \"" + fileName + "\"");
		String readmeFile = getAsciiFile(emptyDataFile);
		FileWithDigests fileWithDigests = getFileWithDigests(fileName);
		readmeFile = readmeFile.replaceAll("\\{GAME}", getGameName(fileWithDigests.getName()));
		readmeFile = readmeFile.replaceAll("\\{SYSTEM}", getGameSystem(fileWithDigests.getName()));
		readmeFile = readmeFile.replaceAll("\\{HASHES}", getFileDigests(fileWithDigests));
		readmeFile = readmeFile.replaceAll("\\{DATE}", LocalDate.now().format(GAME_DATE_DATE_FORMAT));
		readmeFile = readmeFile.replaceAll("\\{YEAR}", LocalDate.now().format(GAME_YEAR_DATE_FORMAT));
		writeFileAscii(filledDataFile, readmeFile);
	}

	static String getGameSystem(String fileName) {
		String system = Constants.EXTENSION_TO_SYSTEM.get(getFileExtension(fileName).toLowerCase());
		if(system == null) {
			system = "XXXX";
		}
		return system;
	}

	static String getGameName(String fileName) {
		int dot = fileName.indexOf('.');
		int cut = fileName.length();
		if(dot > -1 && dot < cut) {
			cut = dot;
		}
		int parenthesis = fileName.indexOf('(');
		if(parenthesis > -1 && parenthesis < cut) {
			cut = parenthesis;
		}
		int bracket = fileName.indexOf('[');
		if(bracket > -1 && bracket < cut) {
			cut = bracket;
		}
		int comma = fileName.indexOf(COMMA_THE);
		String gameName = fileName;
		if(comma > -1 && comma < cut) {
			gameName = "The " + gameName.substring(0, comma) + gameName.substring(comma + COMMA_THE.length(), cut);
		}
		else {
			gameName = gameName.substring(0, cut);
		}
		return gameName.trim();
	}

}
