package com.wave.hextractor.util

import com.wave.hextractor.helper.HexTable
import com.wave.hextractor.pojo.OffsetEntry
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.util.*
import kotlin.experimental.and

/**
 * Utility class.
 * @author slcantero
 */
object Utils {
    /** The Constant INVALID_FILE_CHARACTERS.  */
    private val INVALID_FILE_CHARACTERS: List<String> = listOf("<", ">", ":", "\"", "\\", "/", "|", "?", "*")

    /**
     * Returns true if the file name is valid.
     *
     * @param fileName the file name
     * @return true, if is valid file name
     */
    fun isValidFileName(fileName: String): Boolean {
        var res = true
        if (fileName != null) {
            for (invalidChar in INVALID_FILE_CHARACTERS) {
                if (fileName.contains(invalidChar)) {
                    res = false
                    break
                }
            }
        }
        return res
    }

    /**
     * Copy the file using streams.
     *
     * @param source the source
     * @param dest the dest
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @Throws(IOException::class)
    fun copyFileUsingStream(source: String, dest: String) {
        FileInputStream(source).use { fileInputStream ->
            FileOutputStream(dest).use { os ->
                val buffer = ByteArray(1024)
                var length: Int
                while (fileInputStream.read(buffer).also { length = it } > 0) {
                    os.write(buffer, 0, length)
                }
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
    @Throws(IOException::class)
    fun createFile(file: String, content: String) {
        FileOutputStream(file).use { os -> os.write(content.toByteArray()) }
    }

    /**
     * Returns the path of the path + the file name.
     *
     * @param path the path
     * @param fileName the file name
     * @return the joined file name
     */
    fun getJoinedFileName(path: File, fileName: String): String {
        return (path.absolutePath + Constants.FILE_SEPARATOR) + fileName
    }

    /**
     * Pads the string from the left up to size chars with '0'.
     *
     * @param text text to pad.
     * @param size size to pad to.
     * @return the string
     */
    fun fillLeft(text: String, size: Int): String {
        val builder = StringBuilder()
        while (builder.length + text.length < size) {
            builder.append(Constants.PAD_CHAR_STRING)
        }
        builder.append(text)
        return builder.toString()
    }

    val debug: Boolean
        /**
         * Returns true if the system property logLevel is set to DEBUG.
         *
         * @return true, if is debug
         */
        get() = "DEBUG" == System.getProperty("logLevel")

    /**
     * Returns the Hex filled from the left with 0.
     * And in upper case.
     *
     * @param number the number
     * @param size the size
     * @return the hex filled left
     */
    fun getHexFilledLeft(number: Int, size: Int): String {
        return fillLeft(Integer.toHexString(number), size).uppercase(Locale.getDefault())
    }

    /**
     * Tranforms the data to a hex string.
     *
     * @param data the data
     * @return the string
     */
    fun toHexString(data: ByteArray): String {
        val sb = StringBuilder("[")
        for (dataByte in data) {
            sb.append(java.lang.String.format(Constants.HEX_16_FORMAT, dataByte)).append(Constants.SPACE_STR)
        }
        sb.append("]")
        return sb.toString()
    }

    /**
     * Transforms the hex string to a byte [].
     *
     * @param s the s
     * @return the byte[]
     */
    fun hexStringToByteArray(s: String): ByteArray = s.chunked(2).map { Integer.decode("0x$it").toByte() }.toByteArray()

    /**
     * Translates the hex string character to byte.
     *
     * @param s the s
     * @return the byte
     */
    fun hexStringCharToByte(s: String): Byte {
        return hexStringToByteArray(s)[0]
    }

    /**
     * Generates a byte array with the bytes of the int, from MSB to LSB.
     *
     * @param value the value
     * @return the byte[]
     */
    fun intToByteArray(value: Int): ByteArray {
        return byteArrayOf((value ushr 24).toByte(), (value ushr 16).toByte(), (value ushr 8).toByte(), value.toByte())
    }

    /**
     * Converts two bytes to a int.
     *
     * @param value1 MSB
     * @param value2 LSB
     * @return short v1 * 256 + v2
     */
    fun bytesToInt(value1: Byte, value2: Byte): Int {
        return value1.toInt() shl 8 and 0xFF00 or (value2.toInt() and 0xFF)
    }

    /**
     * Converts two bytes to a int.
     *
     * @param value1 MSB
     * @param value2 Med
     * @param value3 LSB
     * @return short v1 + v2 *256 8
     */
    fun bytesToInt(value1: Byte, value2: Byte, value3: Byte): Int {
        return value1.toInt() shl 16 and 0xFF0000 or (value2.toInt() shl 8 and 0xFF00) or (value3.toInt() and 0xFF)
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
    fun bytesToInt(value1: Byte, value2: Byte, value3: Byte, value4: Byte): Int {
        return value1.toInt() shl 24 and -0x1000000 or (value2.toInt() shl 16 and 0xFF0000) or
                (value3.toInt() shl 8 and 0xFF00) or (value4.toInt() and 0xFF)
    }

    /**
     * Loads the input hex string into the b[].
     *
     * @param inputParam the input
     * @param b the b
     */
    fun loadHex(inputParam: String, b: ByteArray) {
        var input = inputParam
        input = input.uppercase(Locale.getDefault())
        var incomment = false
        val hexLine = StringBuilder()
        var i = 0
        while (i < input.length) {
            val charString: String = input.substring(i, i + 1)
            var charGot = charString[0]
            if (incomment) {
                if (Constants.NEWLINE == charGot) {
                    incomment = false
                }
            } else {
                if (Constants.HEXCHARS.contains(charString)) {
                    hexLine.append(input, i, i + 2)
                    i++
                } else {
                    when (charGot) {
                        Constants.COMMENT_LINE -> incomment = true
                        Constants.ADDR_CHAR -> {
                            val hexString: String = hexLine.toString()
                            val line = hexStringToByteArray(hexString)
                            while (charGot == Constants.ADDR_CHAR) {
                                i += Constants.CHAR_SIZE // Jump ADDR_CHAR
                                // Read start
                                val adrrStartString: String = input.substring(i, i + Constants.HEX_ADDR_SIZE)
                                val addrStart: Int = adrrStartString.toInt(Constants.HEX_RADIX)
                                i += Constants.HEX_ADDR_SIZE + Constants.CHAR_SIZE
                                // Read end
                                val adrrEndString: String = input.substring(i, i + Constants.HEX_ADDR_SIZE)
                                val addrEnd: Int = adrrEndString.toInt(Constants.HEX_RADIX)
                                i += Constants.HEX_ADDR_SIZE
                                // Dump buffer if it has hex and address
                                log("DUMPING $hexString")
                                log("TO @$adrrStartString:$adrrEndString ${addrEnd - addrStart + 1} BYTES")
                                log("")
                                System.arraycopy(line, 0, b, addrStart, line.size)
                                // pad with zeroes if needed
                                var j = addrStart + line.size
                                while (j <= addrEnd) {
                                    b[j] = Constants.PAD_CHAR
                                    j++
                                }
                                charGot = if (i < input.length - 1) {
                                    input.substring(i, i + 1)[0]
                                } else {
                                    Constants.MAX_BYTES
                                }
                            }
                            // Buffer clear
                            hexLine.setLength(0)
                            i-- // For increment will adjust it
                        }

                        else -> {}
                    }
                }
            }
            i++
        }
    }

    /**
     * Gets the offsets of the string offsets.
     *
     * @param inputString the string
     * @return the offsets
     * @throws IOException the exception
     */
    @Throws(IOException::class)
    fun getOffsets(inputString: String): List<OffsetEntry> {
        var offsetsString = inputString
        if (offsetsString.endsWith(Constants.OFF_EXTENSION)) {
            offsetsString = FileUtils.getCleanOffsets(offsetsString)
        }
        offsetsString = offsetsString.uppercase(Locale.getDefault())
        val offsets: MutableList<OffsetEntry> = mutableListOf()
        for (offsetString in offsetsString.split(Constants.OFFSET_STR_SEPARATOR.toRegex())
            .dropLastWhile { it.isEmpty() }.toTypedArray()) {
            val entry = OffsetEntry()
            val values: Array<String> =
                offsetString.split(Constants.OFFSET_CHAR_SEPARATOR.toRegex()).dropLastWhile { it.isEmpty() }
                    .toTypedArray()
            entry.start = values[0].toInt(Constants.HEX_RADIX)
            if (values.size > 1) {
                entry.end = values[1].toInt(Constants.HEX_RADIX)
                if (values.size > 2) {
                    for (i in 2 until values.size) {
                        entry.endChars?.add(values[i])
                    }
                }
            }
            offsets.add(entry)
        }
        return offsets
    }

    /**
     * Gets the lines cleaned (Only printable chars).
     *
     * @param lines the lines
     * @return the lines cleaned
     */
    fun getLinesCleaned(lines: Array<String>): StringBuilder {
        val cleanedLines = StringBuilder()
        for (forLine in lines) {
            var line = forLine
            line = line.replace(Constants.REGEX_CLEAN_TEXT.toRegex(), Constants.SPACE_STR)
            line = line.replace(Constants.REGEX_CLEAN_SPACES.toRegex(), Constants.SPACE_STR)
            line = line.replace(Constants.REGEX_DICTIONARY_CHARS.toRegex(), Constants.EMPTY)
            line = line.trim { it <= ' ' }
            if (line.isNotEmpty()) {
                cleanedLines.append(line).append(Constants.S_NEWLINE)
            }
        }
        return cleanedLines
    }

    /**
     * Returns true if the string has words from the dict.
     *
     * @param dict .
     * @param sentence .
     * @return true, if successful
     */
    fun stringHasWords(dict: Collection<String?>, sentence: String): Boolean {
        return getCleanedString(sentence).lowercase(Locale.getDefault()).split(Constants.SPACE_STR.toRegex())
            .dropLastWhile(CharSequence::isEmpty).any { word: String? ->
                word != null && word.trim { it <= ' ' }.length > 3 && word in dict
            }
    }

    /**
     * Cleans a string from multiple spaces, to lower case,
     * and deletes the non digit characters.
     *
     * @param toClean the string
     * @return the cleaned string
     */
    fun getCleanedString(toClean: String): String {
        return toClean.replace(
            Constants.REGEX_MULTI_SPACES.toRegex(),
            Constants.SPACE_STR
        ).lowercase(Locale.getDefault()).replace(
            Constants.REGEX_NOT_LETTER_DIGIT.toRegex(),
            Constants.EMPTY
        )
    }

    /**
     * Returns true if all bytes are equal.
     *
     * @param entryData the entry data
     * @return true, if successful
     */
    fun allSameValue(entryData: ByteArray): Boolean = entryData.distinct().size < 2

    /**
     * Generates a hex string of at least length size, padded with zeroes on
     * the left, uppercase.
     *
     * @param value the value
     * @param length the length
     * @return the string
     */
    fun intToHexString(value: Int, length: Int): String {
        var num: String = fillLeft(Integer.toHexString(value), length).uppercase(Locale.getDefault())
        if (num.length > length) {
            num = num.substring(num.length - length)
        }
        return num
    }

    /**
     * Translates an array of hex values to int values.
     *
     * @param hexValues the hex values
     * @return the int[]
     */
    fun hexStringListToIntList(hexValues: Array<String>): IntArray {
        val numbers = IntArray(hexValues.size)
        for (i in hexValues.indices) {
            numbers[i] = hexValues[i].toInt(Constants.HEX_RADIX)
        }
        return numbers
    }

    /**
     * Get the OffsetEntries of the string.
     *
     * @param entries the entries
     * @return the hex offsets
     */
    fun getHexOffsets(entries: String): List<OffsetEntry> {
        val entryList: MutableList<OffsetEntry> = ArrayList<OffsetEntry>()
        for (entryStr in entries.replace(Constants.SPACE_STR, Constants.EMPTY)
            .split(Constants.OFFSET_STR_SEPARATOR.toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()) {
            var entry: OffsetEntry? = null
            if (entryStr.contains(Constants.OFFSET_CHAR_SEPARATOR)) {
                // 00001-00003 type init-end (inclusive)
                val numbers = hexStringListToIntList(
                    entryStr.split(Constants.OFFSET_CHAR_SEPARATOR.toRegex()).dropLastWhile { it.isEmpty() }
                        .toTypedArray()
                )
                entry = OffsetEntry(numbers[0], numbers[1], null)
            } else {
                if (entryStr.contains(Constants.OFFSET_LENGTH_SEPARATOR)) {
                    // 00001:00003 type init:length
                    val numbers = hexStringListToIntList(
                        entryStr.split(Constants.OFFSET_LENGTH_SEPARATOR.toRegex()).dropLastWhile { it.isEmpty() }
                            .toTypedArray()
                    )
                    entry = OffsetEntry(numbers[0], numbers[0] + numbers[1] - 1, null)
                } else {
                    log("Invalid Offset entry!!!!! : $entryStr")
                }
            }
            if (entry != null) {
                entryList.add(entry)
            }
        }
        return entryList
    }

    fun <K, V : Comparable<V>> sortByValue(map: Map<K, V>): List<Pair<K, V>> =
        map.entries.sortedBy { it.value }.map { Pair(it.key, it.value) }

    /**
     * Transforms the list of entries to the format:
     * (start-end-(endchar)+)*etc.
     *
     * @param offEntries the off entries
     * @return the string
     */
    fun toFileString(offEntries: List<OffsetEntry?>): String {
        val entriesStr = StringBuilder()
        for (entry in offEntries) {
            if (entriesStr.isNotEmpty()) {
                entriesStr.append(Constants.OFFSET_STR_SEPARATOR)
            }
            entriesStr.append(entry?.toEntryString())
        }
        return entriesStr.toString()
    }

    /**
     * Gets the dict key.
     *
     * @param line the line
     * @return the dict key
     */
    private fun getDictKey(line: String): String? {
        // ;000000DE{text}#117#105
        var res: String? = null
        if (line.startsWith(Constants.S_COMMENT_LINE)) {
            res = line.substring(
                line.lastIndexOf(Constants.S_ORG_STR_OPEN) + Constants.S_ORG_STR_OPEN.length,
                line.lastIndexOf(Constants.S_ORG_STR_CLOSE)
            ) + line.substring(
                line.lastIndexOf(Constants.STR_NUM_CHARS),
                line.lastIndexOf(Constants.STR_NUM_CHARS) + Constants.LEN_NUM_CHARS + Constants.S_STR_NUM_CHARS.length
            )
        }
        return res
    }

    /**
     * Checks if is dict value.
     *
     * @param line the line
     * @return true, if is dict value
     */
    private fun isDictValue(line: String): Boolean {
        return !(
            line.startsWith(Constants.ADDR_STR) || line.startsWith(Constants.S_COMMENT_LINE) || line.startsWith(
                Constants.S_MAX_BYTES
            )
            )
    }

    /**
     * Extracts the dictionary of the file Lines.
     *
     * @param transFileLines the trans file lines
     * @return the map
     */
    fun extractDictionary(transFileLines: List<String>): Map<String?, String> {
        val dict = HashMap<String?, String>()
        var currKey: String? = null
        for (line in transFileLines) {
            val aKey = getDictKey(line)
            if (aKey != null) {
                currKey = aKey
            } else {
                if (isDictValue(line)) {
                    dict[currKey] = line
                }
            }
        }
        return dict
    }

    /**
     * Devuelve true si key y value tienen la misma longitud.
     *
     * @param key the key
     * @param value the value
     * @return true, if successful
     */
    fun checkLineLength(key: String, value: String): Boolean {
        return getLineLength(key) == getLineLength(value)
    }

    /**
     * Gets the line length.
     *
     * @param value the value
     * @return the line length
     */
    private fun getLineLength(value: String): String {
        return value.substring(value.lastIndexOf(Constants.S_STR_NUM_CHARS))
    }

    /**
     * Gets the text area using the table.
     */
    fun getTextArea(offset: Int, length: Int, data: ByteArray, hexTable: HexTable): String {
        val sb = StringBuilder(length)
        var end = offset + length
        if (end > data.size) {
            end = data.size
        }
        for (i in offset until end) {
            sb.append(hexTable.toString(data[i], false))
        }
        return sb.toString()
    }

    /**
     * Gets the hex area as string 00 01 02 etc.
     *
     * @param offset the offset
     * @param length the length
     * @param data the data.
     * @return the hex area
     */
    fun getHexArea(offset: Int, length: Int, data: ByteArray): String {
        val sb = StringBuilder(length * Constants.HEX_VALUE_SIZE)
        var end = offset + length
        if (end > data.size) {
            end = data.size
        }
        for (i in offset until end) {
            sb.append(java.lang.String.format(Constants.HEX_16_FORMAT, data[i]))
            sb.append(Constants.SPACE_STR)
        }
        return sb.toString()
    }

    /**
     * Gets the hex area as string 00 01 02 etc.
     *
     * @param offset the offset
     * @param length the length
     * @param data the data.
     * @return the hex area
     */
    fun getHexAreaFixedWidth(offset: Int, length: Int, data: ByteArray, cols: Int): String {
        val sb = StringBuilder(length * Constants.HEX_VALUE_SIZE)
        var end = offset + length
        if (end > data.size) {
            end = data.size
        }
        var curCol = 0
        for (i in offset until end) {
            sb.append(java.lang.String.format(Constants.HEX_16_FORMAT, data[i]))
            if (curCol == cols - 1 && i < end - 1) {
                sb.append(Constants.S_NEWLINE)
                curCol = 0
            } else {
                sb.append(Constants.SPACE_STR)
                curCol++
            }
        }
        return sb.toString()
    }

    /**
     * Removes comment lines and joins nonaddress lines togheter
     */
    fun removeCommentsAndJoin(asciiFile: String): Array<String> {
        val sb = StringBuilder()
        for (line in asciiFile.split(Constants.S_NEWLINE.toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()) {
            if (!line.contains(Constants.S_COMMENT_LINE)) {
                sb.append(line)
                if (line.contains(Constants.ADDR_STR)) {
                    sb.append(Constants.S_NEWLINE)
                }
            }
        }
        return sb.toString().split(Constants.S_NEWLINE.toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
    }

    /**
     * Compresses 4 6 bit bytes to 3 8 bit bytes
     */
    fun getCompressed4To3Data(bytes: ByteArray): ByteArray {
        val res = ByteArray(bytes.size)
        var j = 0
        var i = 0
        while (i <= bytes.size - 4) {
            res[j++] = (bytes[i].toInt() shl 2 and 0xFF or (bytes[i + 1].toInt() shr 4 and 0xFF)).toByte()
            res[j++] = (bytes[i + 1].toInt() shl 4 and 0xFF or (bytes[i + 2].toInt() shr 2 and 0xFF)).toByte()
            res[j++] = (bytes[i + 3].toInt() or (bytes[i + 2].toInt() and 0x3 shl 6 and 0xFF) and 0xFF).toByte()
            i += 4
        }
        when (bytes.size % 4) {
            1 -> // No debería poder pasar nunca
                res[j++] = bytes[i]

            2 -> // 2->1
                res[j++] = (bytes[i].toInt() shl 2 and 0xFF or (bytes[i + 1].toInt() shr 4 and 0xFF)).toByte()

            0, 3 -> {
                // 3->2
                res[j++] = (bytes[i].toInt() shl 2 and 0xFF or (bytes[i + 1].toInt() shr 4 and 0xFF)).toByte()
                res[j++] = (bytes[i + 1].toInt() shl 4 and 0xFF or (bytes[i + 2].toInt() shr 2 and 0xFF)).toByte()
            }

            else -> {
                res[j++] = (bytes[i].toInt() shl 2 and 0xFF or (bytes[i + 1].toInt() shr 4 and 0xFF)).toByte()
                res[j++] = (bytes[i + 1].toInt() shl 4 and 0xFF or (bytes[i + 2].toInt() shr 2 and 0xFF)).toByte()
            }
        }
        return res.copyOfRange(0, j)
    }

    /**
     * Expands 3 bytes to 4 6 bits bytes
     * @param bytes .
     * @return expanded data
     */
    fun getExpanded3To4Data(bytes: ByteArray): ByteArray {
        val res = ByteArray(bytes.size * 2)
        var j = 0
        var i = 0
        while (i <= bytes.size - 3) {
            res[j++] = (bytes[i].toInt() shr 2 and 0x3F).toByte()
            res[j++] = (bytes[i].toInt() and 0x3 shl 4 or (bytes[i + 1].toInt() and 0xFF shr 4) and 0x3F).toByte()
            res[j++] = (bytes[i + 2].toInt() and 0xFF shr 6 or (bytes[i + 1].toInt() and 0xFF shl 2) and 0x3F).toByte()
            res[j++] = (bytes[i + 2].toInt() and 0x3F).toByte()
            i += 3
        }
        when (bytes.size % 3) {
            1 -> {
                res[j++] = (bytes[i].toInt() shr 2 and 0x3F).toByte()
                res[j++] = (bytes[i].toInt() and 0x03 shl 4 and 0x30).toByte()
            }

            2 -> {
                res[j++] = (bytes[i].toInt() shr 2 and 0x3F).toByte()
                res[j++] =
                    (bytes[i].toInt() and 0x3 shl 4 and 0x30 or (bytes[i + 1].toInt() shr 4 and 0x0F) and 0x3F).toByte()
                res[j++] = (bytes[i + 1].toInt() shl 2 and 0x3F).toByte()
            }

            0 -> {}
            else -> {}
        }
        return res.copyOfRange(0, j)
    }

    /**
     * Log.
     *
     * @param msg the msg
     */
    fun log(msg: String?) {
        println(msg)
    }

    /**
     * LogNoNL.
     *
     * @param msg the msg
     */
    fun logNoNL(msg: String?) {
        print(msg)
    }

    /**
     * Log exception.
     *
     * @param e the e
     */
    fun logException(e: Throwable) {
        e.printStackTrace()
    }

    /**
     * Short to bytes.
     *
     * @param value the value
     * @return the byte[]
     */
    fun shortToBytes(value: Short): ByteArray {
        val returnByteArray = ByteArray(2)
        returnByteArray[0] = (value and Constants.MASK_8BIT.toShort()).toByte()
        returnByteArray[1] = (value.toInt() ushr 8 and Constants.MASK_8BIT).toByte()
        return returnByteArray
    }

    fun bytesToHex(hashInBytes: ByteArray): String {
        val sb = StringBuilder()
        for (b in hashInBytes) {
            sb.append(String.format("%02x", b))
        }
        return sb.toString().lowercase(Locale.getDefault())
    }
}
