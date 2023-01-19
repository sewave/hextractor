package com.wave.hextractor.util

import com.wave.hextractor.helper.HexTable
import com.wave.hextractor.pojo.FileWithDigests
import com.wave.hextractor.pojo.OffsetEntry
import com.wave.hextractor.pojo.TableSearchResult
import java.io.File
import java.io.IOException
import java.io.PrintWriter
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Paths
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.zip.CRC32

/**
 * Utility class for files.
 * @author slcantero
 */
object FileUtils {
    /** The Constant FILES_SEPARATOR.  */
    private const val FILES_SEPARATOR = "\"\n to file \""

    /** The Constant MD5_DIGEST.  */
    private const val MD5_DIGEST = "MD5"

    /** The Constant SHA1_DIGEST.  */
    private const val SHA1_DIGEST = "SHA-1"
    private val GAME_DATE_DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy")
    private val GAME_YEAR_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy")
    private const val COMMA_THE = ", The"

    /**
     * Gets the extension of a file, i.e.: file.ext => ext
     * @param file file to look at.
     * @return the extension.
     */
    fun getFileExtension(file: File): String {
        return getFileExtension(file.name)
    }

    /**
     * Gets the extension of a file, i.e.: file.ext => ext
     * @return the extension.
     */
    fun getFileExtension(fileName: String): String {
        var extension = Constants.EMPTY
        val i = fileName.lastIndexOf(Constants.CHR_DOT)
        if (i > 0) {
            extension = fileName.substring(i + 1)
        }
        return extension
    }

    /**
     * Gets the file path without separator.
     *
     * @param file the file
     * @return the file path
     */
    fun getFilePath(file: File): String {
        return getFilePath(file.absolutePath)
    }

    /**
     * Gets the file path without separator.
     *
     * @param absolutePath the absolute path
     * @return the file path
     */
    private fun getFilePath(absolutePath: String): String {
        return absolutePath.substring(0, absolutePath.lastIndexOf(File.separator))
    }

    /**
     * Returns the ascii file with only Constants.NEWLINE as line separators.
     * @param filename .
     * @return .
     * @throws IOException .
     */
    @Throws(IOException::class)
    fun getAsciiFile(filename: String?): String {
        return java.lang.String.join(Constants.NEWLINE.toString(), Files.readAllLines(Paths.get(filename.orEmpty())))
    }

    /**
     * Extracts Ascii data on packed 3 bytes to 4 characters
     * using the entries.
     * @param inputFile .
     * @param outputFile .
     * @param entries .
     * @throws IOException .
     */
    @Throws(IOException::class)
    fun extractAscii3To4Data(table: String, inputFile: String, outputFile: String, entries: String) {
        Utils.log(
            "Extracting ascii 3 to 4 from \"$inputFile$FILES_SEPARATOR$outputFile\" \n using \"$entries\"" +
                "and table: \"$table\""
        )
        val dataString = StringBuilder()
        val inputFileBytes = Files.readAllBytes(Paths.get(inputFile))
        val hexTable = HexTable(table)
        for (entry: OffsetEntry in Utils.getOffsets(entries)) {
            val origData = Arrays.copyOfRange(inputFileBytes, entry.start, entry.end + 1)
            val expData = Utils.getExpanded3To4Data(origData)
            val compData = Utils.getCompressed4To3Data(expData)
            if (Utils.debug) {
                Utils.log(
                    ("Original data     " + entry.hexTarget + " - " + Utils.getHexArea(0, origData.size, origData))
                )
                Utils.log(
                    ("Expanded data     " + entry.hexTarget + " - " + Utils.getHexArea(0, expData.size, expData))
                )
                Utils.log(
                    ("Recompressed data " + entry.hexTarget + " - " + Utils.getHexArea(0, compData.size, compData))
                )
            }
            if (!Arrays.equals(origData, compData)) {
                Utils.log("ERROR! RECOMPRESSED DATA IS DIFFERENT!!!")
            }
            val line = hexTable.toAscii(expData, expand = false, decodeUnknown = true)
            dataString.append(Constants.COMMENT_LINE)
            dataString.append(line)
            dataString.append(Constants.S_NEWLINE)
            dataString.append(line)
            dataString.append(entry.hexTarget)
            dataString.append(Constants.S_NEWLINE)
        }
        writeFileAscii(outputFile, dataString.toString())
    }

    /**
     * Inserts ascii as hex from a 4 to 3 data.
     */
    @Throws(IOException::class)
    fun insertHex4To3Data(table: String, inputFile: String, outputFile: String) {
        Utils.log(
            ("Inserting ascii as hex 4 to 3 from \"$inputFile$FILES_SEPARATOR$outputFile\"\n using table: \"$table\"")
        )
        val outFileBytes = Files.readAllBytes(Paths.get(outputFile))
        val hexTable = HexTable(table)
        for (entry: String in Utils.removeCommentsAndJoin(getAsciiFile(inputFile))) {
            val entryDataAndOffset =
                entry.split(Constants.ADDR_STR.toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            val offEntry = OffsetEntry.fromHexRange(entryDataAndOffset[1])
            val compData = Utils.getCompressed4To3Data(hexTable.toHex(entryDataAndOffset[0]))
            System.arraycopy(compData, 0, outFileBytes, offEntry.start, compData.size)
        }
        Files.write(Paths.get(outputFile), outFileBytes)
    }

    /**
     * Writes a string as UTF8 in the destination file.
     * **Overwrites the file if exists.**
     *
     * @param filename file to write.
     * @param ascii string containing the text.
     * @throws IOException the exception
     */
    @Throws(IOException::class)
    fun writeFileAscii(filename: String?, ascii: String?) {
        PrintWriter(filename.orEmpty(), Constants.UTF8_ENCODING).use { out -> out.print(ascii) }
    }

    /**
     * Insert hex data.
     *
     * @param firstFile the first file
     * @param secondFile the second file
     * @throws IOException the exception
     */
    @Throws(IOException::class)
    fun insertHexData(firstFile: String, secondFile: String) {
        Utils.log("Inserting hex file \"$firstFile$FILES_SEPARATOR$secondFile\".")
        val b = Files.readAllBytes(Paths.get(secondFile))
        Utils.loadHex(getAsciiFile(firstFile), b)
        Files.write(Paths.get(secondFile), b)
    }

    /**
     * Insert ascii as hex.
     *
     * @param firstFile the first file
     * @param secondFile the second file
     * @param thirdFile the third file
     * @throws IOException the exception
     */
    @Throws(IOException::class)
    fun insertAsciiAsHex(firstFile: String, secondFile: String, thirdFile: String) {
        Utils.log(
            "Inserting ascii file \"$secondFile\"\n using table \"$firstFile$FILES_SEPARATOR$thirdFile\"."
        )
        val hexTable = HexTable(firstFile)
        val input = getAsciiFile(secondFile)
        val outFileBytes = Files.readAllBytes(Paths.get(thirdFile))
        val lines: Array<String?> =
            input.split(Constants.S_NEWLINE.toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        var totalBytesWritten = 0
        var line = 0
        while (line < lines.size) {
            if (lines[line] != null && lines[line]!!.contains(Constants.ADDR_STR)) {
                // Read entry
                val entry = OffsetEntry(lines[line]!!)
                line++
                // Read content (including end)
                val content = StringBuilder()
                // Put lines not starting with |
                while (!lines[line]!!.contains(Constants.S_MAX_BYTES)) {
                    if ((lines[line] != null) && (lines[line]!!.isNotEmpty()) && !lines[line]!!.contains(
                            Constants.S_COMMENT_LINE
                        )
                    ) {
                        content.append(lines[line])
                        if (lines[line]!!.contains(Constants.S_STR_NUM_CHARS)) {
                            content.append(Constants.S_NEWLINE)
                        }
                    }
                    line++
                }
                // End line
                content.append(lines[line]).append(Constants.S_NEWLINE)

                // Process
                val hex = hexTable.toHex(content.toString(), entry)
                if (Utils.debug) {
                    Utils.log(" TO OFFSET: " + Utils.intToHexString(entry.start, Constants.HEX_ADDR_SIZE))
                }
                System.arraycopy(hex, 0, outFileBytes, entry.start, hex.size)
                totalBytesWritten += hex.size
            }
            line++
        }
        Utils.log(
            (
                "TOTAL BYTES WRITTEN: " + Utils.fillLeft(
                    totalBytesWritten.toString(),
                    Constants.HEX_ADDR_SIZE
                ) + " / " + Utils.intToHexString(totalBytesWritten, Constants.HEX_ADDR_SIZE) + " Hex"
                )
        )
        Files.write(Paths.get(thirdFile), outFileBytes)
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
    @Throws(IOException::class)
    fun extractAsciiFile(firstFile: String, secondFile: String, thirdFile: String, offsetsArg: String?) {
        Utils.log(
            "Extracting ascii file from \"$secondFile\"\n using table \"$firstFile$FILES_SEPARATOR$thirdFile\"."
        )
        extractAsciiFile(
            HexTable(firstFile),
            Files.readAllBytes(Paths.get(secondFile)),
            thirdFile,
            offsetsArg,
            true
        )
    }

    /**
     * Extracts the ascii file.
     */
    @Throws(IOException::class)
    private fun extractAsciiFile(
        hexTable: HexTable,
        fileBytes: ByteArray,
        outFile: String,
        offsetsArg: String?,
        showExtractions: Boolean
    ) {
        if (!offsetsArg.isNullOrEmpty()) {
            extractAsciiFile(hexTable, fileBytes, outFile, Utils.getOffsets(offsetsArg), showExtractions)
        }
    }

    /**
     * Extracts the ascii file.
     */
    @Throws(IOException::class)
    fun extractAsciiFile(
        hexTable: HexTable,
        fileBytes: ByteArray?,
        outFile: String?,
        offsets: List<OffsetEntry?>?,
        showExtractions: Boolean
    ) {
        val fileOut = StringBuilder()
        if (!offsets.isNullOrEmpty()) {
            for (entry: OffsetEntry? in offsets) {
                fileOut.append(hexTable.toAscii((fileBytes)!!, (entry)!!, showExtractions))
            }
        }
        writeFileAscii(outFile, fileOut.toString())
    }

    /**
     * Cleans the firstFile extraction data to the secondFile as only
     * printable chars.
     * @param firstFile extraction file.
     * @param secondFile ascii text.
     * @throws IOException .
     */
    @Throws(IOException::class)
    fun cleanAsciiFile(firstFile: String, secondFile: String) {
        Utils.log("Cleaning ascii extraction \"$firstFile\" to \"$secondFile\".")
        writeFileAscii(
            secondFile,
            Utils.getLinesCleaned(
                getAsciiFile(firstFile).split(Constants.S_NEWLINE.toRegex()).dropLastWhile { it.isEmpty() }
                    .toTypedArray()
            ).toString()
        )
    }

    /**
     * Generates a table file for the input string if found on the rom.
     *
     * @param firstFile .
     * @param outFilePrefix .
     * @param searchString .
     * @throws IOException the exception
     */
    @Throws(IOException::class)
    fun searchRelative8Bits(firstFile: String, outFilePrefix: String, searchString: String) {
        Utils.log(
            "Searching relative string \"$searchString\"\n in \"$firstFile\" \n Generating table files from  \"$outFilePrefix.001\""
        )
        val hexTables = searchRelative8Bits(Files.readAllBytes(Paths.get(firstFile)), searchString)
        var tablesFound = 1
        val usedTables: MutableList<HexTable> = ArrayList()
        for (t: TableSearchResult in hexTables) {
            if (!usedTables.contains(t.hexTable)) {
                writeFileAscii(
                    outFilePrefix + "." + Utils.fillLeft(tablesFound.toString(), 3),
                    t.hexTable.toAsciiTable()
                )
                usedTables.add(t.hexTable)
            }
            tablesFound++
        }
    }

    /**
     * Searches tables that meet the letter correlation for the target phrase.
     *
     * @param fileBytes the file bytes
     * @param searchString the search string
     * @return list of tables.
     */
    private fun searchRelative8Bits(fileBytes: ByteArray, searchString: String): List<TableSearchResult> {
        val res: MutableList<TableSearchResult> = ArrayList()
        val wordLength = searchString.length
        if (wordLength < Constants.MIN_SEARCH_WORD_LENGTH) {
            throw IllegalArgumentException(
                "Minimal word length / Longitud minima de palabra : " + Constants.MIN_SEARCH_WORD_LENGTH
            )
        }
        val searchBytes = searchString.toByteArray(StandardCharsets.US_ASCII)
        var i = 0
        while (i < fileBytes.size - wordLength) {
            val displacement = (searchBytes[0] - fileBytes[i]) and Constants.MASK_8BIT
            if (equivalentChars(displacement, searchBytes, fileBytes.copyOfRange(i, i + wordLength))) {
                val tr = TableSearchResult(HexTable(displacement), i, searchString)
                if (!res.contains(tr)) {
                    res.add(tr)
                }
                i += wordLength - 1
            }
            if (res.size > 999) {
                break
            }
            i++
        }
        return res
    }

    /**
     * Searches relative but * can be expanded to up to expansion number of chars.
     * @param fileBytes the file bytes
     * @param searchString the search string
     * @param expansion number of chars * can represent
     * @return list of tables.
     */
    fun multiSearchRelative8Bits(fileBytes: ByteArray, searchString: String, expansion: Int): List<TableSearchResult> {
        val res: MutableSet<TableSearchResult> = HashSet()
        val replacement = StringBuilder()
        if (searchString.contains(Constants.STR_ASTER)) {
            for (i in 0 until expansion) {
                replacement.append(Constants.STR_ASTER)
                res.addAll(
                    searchRelative8Bits(
                        fileBytes,
                        searchString.replace(Constants.REGEX_STR_ASTER.toRegex(), replacement.toString())
                    )
                )
            }
        } else {
            res.addAll(searchRelative8Bits(fileBytes, searchString))
        }
        return ArrayList(res)
    }

    /**
     * Gets the offsets for the string on the file using the table.
     * @param fileBytes .
     * @param hexTable .
     * @param searchStringFun .
     * @param ignoreCase .
     * @return .
     * @throws IllegalArgumentException .
     */
    private fun findString(
        fileBytes: ByteArray,
        hexTable: HexTable,
        searchStringFun: String,
        ignoreCase: Boolean
    ): List<Int> {
        var searchString = searchStringFun
        val res: MutableList<Int> = ArrayList()
        val wordLength = searchString.length
        if (ignoreCase) {
            searchString = searchString.uppercase(Locale.getDefault())
        }
        if (wordLength < Constants.MIN_SEARCH_WORD_LENGTH) {
            throw IllegalArgumentException(
                "Minimal word length / Longitud minima de palabra : " + Constants.MIN_SEARCH_WORD_LENGTH
            )
        }
        var i = 0
        while (i < fileBytes.size - wordLength) {
            var word = hexTable.toAscii(fileBytes.copyOfRange(i, i + wordLength), true)
            if (ignoreCase) {
                word = word.uppercase(Locale.getDefault())
            }
            if (areEqual(searchString, wordLength, word)) {
                if (!res.contains(i)) {
                    res.add(i)
                }
                i += wordLength - 1
            }
            if (res.size > 999) {
                break
            }
            i++
        }
        return res
    }

    private fun areEqual(searchString: String, wordLength: Int, word: String): Boolean {
        var areEqual = true
        for (j in 0 until wordLength) {
            if (searchString[j] != Constants.CHR_ASTER && searchString[j] != word[j]) {
                areEqual = false
                break
            }
        }
        return areEqual
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
    fun multiFindString(
        fileBytes: ByteArray,
        hexTable: HexTable,
        searchString: String,
        ignoreCase: Boolean,
        expansion: Int
    ): List<TableSearchResult> {
        val res: MutableList<TableSearchResult> = ArrayList()
        if (searchString.contains(Constants.STR_ASTER)) {
            val replacement = StringBuilder()
            for (i in 0 until expansion) {
                replacement.append(Constants.STR_ASTER)
                val searchStrRep = searchString.replace(Constants.REGEX_STR_ASTER.toRegex(), replacement.toString())
                res.addAll(
                    toTableResults(
                        hexTable,
                        searchStrRep,
                        findString(fileBytes, hexTable, searchStrRep, ignoreCase)
                    )
                )
            }
        } else {
            res.addAll(
                toTableResults(hexTable, searchString, findString(fileBytes, hexTable, searchString, ignoreCase))
            )
        }
        return ArrayList(res)
    }

    /**
     * To table results.
     * @return the list
     */
    private fun toTableResults(
        hexTable: HexTable,
        searchString: String,
        list: List<Int>
    ): List<TableSearchResult> = list.map {
        TableSearchResult(
            hexTable,
            it,
            searchString
        )
    }

    /**
     * Equivalent chars.
     *
     * @param displacement the displacement
     * @param searchBytes the search bytes
     * @param fileBytes the file bytes
     * @return true, if successful
     */
    private fun equivalentChars(displacement: Int, searchBytes: ByteArray, fileBytes: ByteArray): Boolean {
        for (i in searchBytes.indices) {
            if ((searchBytes[i] != Constants.BYTE_ASTER && (searchBytes[i].toInt() and Constants.MASK_8BIT) !=
                        (fileBytes[i] + displacement and Constants.MASK_8BIT))) {
                return false
            }
        }
        return true
    }

    /**
     * Searches all the strings on the rom for the given table
     * for the default dictionary name (EngDict.txt).
     *
     * @param tableFile the table file
     * @param dataFile the data file
     * @param numIgnoredChars the num ignored chars
     * @param endChars the end chars
     * @throws IOException the exception
     */
    @JvmOverloads
    @Throws(IOException::class)
    fun searchAllStrings(
        tableFile: String,
        dataFile: String,
        numIgnoredChars: Int,
        endChars: String,
        dictFile: String = Constants.DEFAULT_DICT
    ) {
        val extractFile = dataFile + Constants.EXTRACT_EXTENSION
        Utils.log(
            ("Extracting all strings from \"" + dataFile + FILES_SEPARATOR + extractFile + "\" and \"" + extractFile + Constants.OFFSET_EXTENSION + "\" \n " + "using \"" + tableFile + "\" \n numIgnoredChars: " + numIgnoredChars + "\n endChars: " + endChars + "\n dictionary: " + dictFile)
        )
        searchAllStrings(
            HexTable(tableFile),
            Files.readAllBytes(Paths.get(dataFile)),
            numIgnoredChars,
            endChars,
            dictFile,
            dataFile + Constants.EXTRACT_EXTENSION
        )
    }

    /**
     * Searches all the strings on the rom for the given table.
     */
    @Throws(IOException::class)
    fun searchAllStrings(
        hexTable: HexTable,
        fileBytes: ByteArray,
        numIgnoredChars: Int,
        endChars: String,
        dictFile: String?,
        extractFile: String
    ) {
        val entries: String = hexTable.getAllEntries(
            fileBytes,
            Constants.MIN_NUM_CHARS_WORD,
            numIgnoredChars,
            endChars.uppercase(Locale.getDefault()).replace(Constants.SPACE_STR, Constants.EMPTY)
                    .split(Constants.OFFSET_CHAR_SEPARATOR.toRegex()).dropLastWhile { it.isEmpty() },
            dictFile
        )
        if (entries.isNotEmpty()) {
            extractAsciiFile(hexTable, fileBytes, extractFile, entries, false)
        }
    }

    /**
     * Extracts all the offsets of a given extraction file, useful after cleaning invalid entries of
     * search all strings.
     * @param extractFile file to search.
     * @param extractFileArgs output file.
     * @throws IOException io error.
     */
    @Throws(IOException::class)
    fun cleanExtractedFile(extractFile: String, extractFileArgs: String) {
        Utils.log("Getting offsets from \"$extractFile$FILES_SEPARATOR$extractFileArgs\"")
        writeFileAscii(extractFileArgs, cleanExtractedFile(extractFile))
    }

    /**
     * Extracts all the offsets of a given extraction file, useful after cleaning invalid entries of
     * search all strings.
     * @param extractFile file to search.
     * @throws IOException io error.
     */
    @Throws(IOException::class)
    fun cleanExtractedFile(extractFile: String): String {
        Utils.log("Getting offsets from \"$extractFile")
        val fileArgs = StringBuilder()
        val lines =
            getAsciiFile(extractFile).split(Constants.S_NEWLINE.toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        for (line: String in lines) {
            val trimLine = line.trim { it <= ' ' }
            if (trimLine.startsWith(Constants.ADDR_STR)) {
                fileArgs.append(trimLine.substring(Constants.ADDR_STR.length))
                fileArgs.append(Constants.OFFSET_STR_SEPARATOR)
            }
        }
        val entries = Utils.getOffsets(fileArgs.toString()).sorted()
        fileArgs.setLength(0)
        for (entry: OffsetEntry in entries) {
            if (fileArgs.isNotEmpty()) {
                fileArgs.append(Constants.OFFSET_STR_SEPARATOR)
            }
            fileArgs.append(entry.toEntryString())
        }
        return fileArgs.toString()
    }

    /**
     * Extracts HEX data from the inputFile to the outputFile
     * using the entries.
     * @param inputFile .
     * @param outputFile .
     * @param entries .
     * @throws IOException .
     */
    @Throws(IOException::class)
    fun extractHexData(inputFile: String, outputFile: String, entries: String) {
        Utils.log(
            ("Extracting hex from \"$inputFile$FILES_SEPARATOR$outputFile\"\n using \"$entries\"")
        )
        val hexDataString = StringBuilder()
        val inputFileBytes = Files.readAllBytes(Paths.get(inputFile))
        for (entry: OffsetEntry in Utils.getHexOffsets(entries)) {
            hexDataString.append(entry.hexComment)
            hexDataString.append(Constants.S_NEWLINE)
            hexDataString.append(entry.getHexString(inputFileBytes))
            hexDataString.append(entry.hexTarget)
            hexDataString.append(Constants.S_NEWLINE)
        }
        writeFileAscii(outputFile, hexDataString.toString())
    }

    /**
     * Returns offsets as a unique line.
     *
     * @param fileName the file
     * @return the clean offsets
     * @throws IOException the exception
     */
    @Throws(IOException::class)
    fun getCleanOffsets(fileName: String?): String {
        return getCleanOffsetsString(getAsciiFile(fileName))
    }

    /**
     * Returns offsets as a unique line from a string.
     *
     * @return the clean offsets
     */
    fun getCleanOffsetsString(string: String): String {
        return string.replace(Constants.S_NEWLINE.toRegex(), Constants.EMPTY)
            .replace(Constants.S_CARRY_RETURN.toRegex(), Constants.EMPTY)
    }

    /**
     * Check if the line lengths are ok.
     *
     * @param toCheckFile the to check file
     * @throws IOException the exception
     */
    @Throws(IOException::class)
    fun checkLineLength(toCheckFile: String) {
        Utils.log("Checking file lines of \"$toCheckFile")
        val dictionary = Utils.extractDictionary(Files.readAllLines(Paths.get(toCheckFile)))
        dictionary.entries.stream().filter { x: Map.Entry<String?, String> ->
            !Utils.checkLineLength(
                x.key.orEmpty(),
                x.value
            )
        }.forEach { e: Map.Entry<String?, String> ->
            Utils.log("Error in lines:")
            Utils.log(e.key)
            Utils.log(e.value)
        }
    }

    /**
     * Separates the string based on the table entry of the first character,
     * adds newline after the desired chars.
     */
    @Throws(IOException::class)
    fun separateCharLength(file: String, table: String, outFile: String) {
        Utils.log("Separating string from \"$file$FILES_SEPARATOR$outFile\"\n using table: \"$table\"")
        writeFileAscii(outFile, separateCharLength(getAsciiFile(file), HexTable(table)))
    }

    /**
     * Separates the string based on the table entry of the first character,
     * adds newline after the desired chars.
     *
     * @param text the text
     * @param table the table
     * @return the string
     */
    private fun separateCharLength(text: String, table: HexTable): String {
        val res = StringBuilder()
        var i = 0
        while (i < text.length) {
            val lenChar = text.substring(i, i + 1)
            val strLen = table.toHex(lenChar)[0].toInt()
            if (strLen == 0) {
                res.append(lenChar)
                i++
            } else {
                res.append(Constants.S_NEWLINE).append(lenChar).append(Constants.S_NEWLINE)
                res.append(text, i + 1, (i + 1 + strLen).coerceAtMost(text.length))
                i += strLen + 1
            }
        }
        return res.toString()
    }

    /**
     * Checks if all files exist.
     *
     * @param files the args
     * @return true, if successful
     */
    fun allFilesExist(files: Array<String>?): Boolean {
        return Arrays.stream(files).map { pathname: String? ->
            File(
                pathname.orEmpty()
            )
        }.allMatch { x: File -> x.exists() && !x.isDirectory }
    }

    /**
     * Replaces bytes on baseFile starting at offset for the ones on replacementFile.
     */
    @Throws(IOException::class)
    fun replaceFileData(baseFile: String, replacementFile: String, offset: Int) {
        Utils.log("Replacing bytes on file: '$baseFile' on offset (dec): $offset with file: '$replacementFile'")
        val baseData = Files.readAllBytes(Paths.get(baseFile))
        val replacementData = Files.readAllBytes(Paths.get(replacementFile))
        System.arraycopy(replacementData, 0, baseData, offset, replacementData.size)
        Files.write(Paths.get(baseFile), baseData)
    }

    /**
     * Outputs the file SHA1, MD5 and CRC32 (in hex), with file name and bytes
     * FILE
     * MD5: XXXXXXXXXXXXX
     * SHA1: XXXXXXXXXXXXX
     * CRC32: XXXXXXXXXXXXX
     * XXXXXXX bytes
     */
    @Throws(IOException::class)
    fun outputFileDigests(file: String) {
        Utils.log(getFileDigests(getFileWithDigests(file)))
    }

    /**
     * Gets the file SHA1, MD5 and CRC32 (in hex), with file name and bytes
     * FILE
     * MD5: XXXXXXXXXXXXX
     * SHA1: XXXXXXXXXXXXX
     * CRC32: XXXXXXXXXXXXX
     * XXXXXXX bytes
     */
    private fun getFileDigests(fileWithDigests: FileWithDigests): String {
        val fileDigests = StringBuilder(fileWithDigests.name).append(System.lineSeparator())
        fileDigests.append("MD5: ").append(fileWithDigests.md5).append(System.lineSeparator())
        fileDigests.append("SHA1: ").append(fileWithDigests.sha1).append(System.lineSeparator())
        fileDigests.append("CRC32: ").append(fileWithDigests.crc32).append(System.lineSeparator())
        fileDigests.append(String.format("%d", fileWithDigests.bytes.size)).append(" bytes")
        return fileDigests.toString()
    }

    /**
     * Returns the file with the digests
     */
    @Throws(IOException::class)
    fun getFileWithDigests(fileName: String): FileWithDigests {
        val file = File(fileName)
        with(Files.readAllBytes(Paths.get(file.absolutePath))) {
            return FileWithDigests(
                name = file.name,
                bytes = this,
                md5 = getDigestHex(this, MD5_DIGEST),
                sha1 = getDigestHex(this, SHA1_DIGEST),
                crc32 = getCrc32Hex(this)
            )
        }
    }

    private fun getCrc32Hex(bytes: ByteArray): String {
        val crc32 = CRC32()
        crc32.update(bytes)
        return String.format("%08X", crc32.value).lowercase(Locale.getDefault())
    }

    private fun getDigestHex(bytes: ByteArray, digest: String): String {
        var res = ""
        try {
            res = Utils.bytesToHex(MessageDigest.getInstance(digest).digest(bytes))
        } catch (e: NoSuchAlgorithmException) {
            Utils.logException(e)
        }
        return res
    }

    /**
     * Fills the variables {GAME}, {SYSTEM} and {HASHES} from the file settings based on
     * the extension.
     */
    @Throws(IOException::class)
    fun fillGameData(emptyDataFile: String, filledDataFile: String, fileName: String) {
        Utils.log("Filling game data from: \"$emptyDataFile\"")
        Utils.log(" to: \"$filledDataFile\"")
        Utils.log(" for file: \"$fileName\"")
        var readmeFile = getAsciiFile(emptyDataFile)
        val fileWithDigests = getFileWithDigests(fileName)
        readmeFile = readmeFile.replace("\\{GAME}".toRegex(), getGameName(fileWithDigests.name))
        readmeFile = readmeFile.replace("\\{SYSTEM}".toRegex(), getGameSystem(fileWithDigests.name))
        readmeFile = readmeFile.replace("\\{HASHES}".toRegex(), getFileDigests(fileWithDigests))
        readmeFile = readmeFile.replace("\\{DATE}".toRegex(), LocalDate.now().format(GAME_DATE_DATE_FORMAT))
        readmeFile = readmeFile.replace("\\{YEAR}".toRegex(), LocalDate.now().format(GAME_YEAR_DATE_FORMAT))
        writeFileAscii(filledDataFile, readmeFile)
    }

    fun getGameSystem(fileName: String): String {
        var system = Constants.EXTENSION_TO_SYSTEM[getFileExtension(fileName).lowercase(Locale.getDefault())]
        if (system == null) {
            system = "XXXX"
        }
        return system
    }

    fun getGameName(fileName: String): String {
        var cleanFileName = fileName.replace("\\[.*]".toRegex(), "").replace("\\(.*\\)".toRegex(), "")
        val dot = cleanFileName.lastIndexOf('.')
        var cut = cleanFileName.length
        if (dot > -1 && dot < cut) {
            cut = dot
        }
        val comma = cleanFileName.indexOf(COMMA_THE)
        if (comma > -1 && comma < cut) {
            cleanFileName =
                "The " + cleanFileName.substring(0, comma) + cleanFileName.substring(comma + COMMA_THE.length, cut)
        } else {
            cleanFileName = cleanFileName.substring(0, cut)
        }
        return cleanFileName.trim { it <= ' ' }
    }
}
