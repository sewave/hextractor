/*
 *
 */
package com.wave.hextractor.helper

import com.wave.hextractor.pojo.OffsetEntry
import com.wave.hextractor.util.Constants
import com.wave.hextractor.util.FileUtils
import com.wave.hextractor.util.Utils
import java.io.IOException
import java.io.Serializable
import java.util.*

/**
 * Class for the table operations.
 * @author slcantero
 */
class HexTable : Serializable {
    /** The table.  */
    private var table: MutableMap<Byte, String> = HashMap()

    /** The reversed.  */
    private var reversed: MutableMap<String, Byte> = HashMap()
    /**
     * Current search completion percent.
     * @return percent search
     */
    /** The searchPercentCompleted.  */
    var searchPercent = 0f
        private set

    /**
     * Transforms the byte into a String.
     *
     * @param aByte the byte
     * @return the string
     */
    @JvmOverloads
    fun toString(aByte: Byte, expand: Boolean, decodeUnknown: Boolean = false): String {
        var res = table[aByte]
        if (res == null || res.length > 1 && !expand) {
            res = if (decodeUnknown) {
                Constants.HEX_CHAR.toString() + Utils.intToHexString(aByte.toInt(), 2) + Constants.HEX_CHAR
            } else {
                Constants.HEX_VIEWER_UNKNOWN_CHAR
            }
        }
        return res
    }

    /**
     * Converts the table selection to a line description.
     *
     * @return the string
     */
    fun toSelectionString(): String {
        val res = StringBuilder()
        if (reversed.containsKey(TABLE_KEY_A)) {
            res.append(TABLE_KEY_A).append(Constants.OFFSET_LENGTH_SEPARATOR).append(Constants.SPACE_STR)
            res.append(Utils.intToHexString(reversed[TABLE_KEY_A]!!.toInt(), Constants.HEXSIZE_8BIT_VALUE)).append(
                Constants.SPACE_STR
            )
        }
        if (reversed.containsKey(TABLE_KEY_LOWA)) {
            res.append(TABLE_KEY_LOWA).append(Constants.OFFSET_LENGTH_SEPARATOR).append(Constants.SPACE_STR)
            res.append(Utils.intToHexString(reversed[TABLE_KEY_LOWA]!!.toInt(), Constants.HEXSIZE_8BIT_VALUE)).append(
                Constants.SPACE_STR
            )
        }
        if (reversed.containsKey(TABLE_KEY_ZERO)) {
            res.append(TABLE_KEY_ZERO).append(Constants.OFFSET_LENGTH_SEPARATOR).append(Constants.SPACE_STR)
            res.append(Utils.intToHexString(reversed[TABLE_KEY_ZERO]!!.toInt(), Constants.HEXSIZE_8BIT_VALUE)).append(
                Constants.SPACE_STR
            )
        }
        return res.toString()
    }

    /**
     * Load lines.
     *
     * @param tableLines the table lines
     */
    private fun loadLines(tableLines: List<String>) {
        table = HashMap()
        reversed = HashMap()
        for (s: String in tableLines) {
            if (s.length >= 4 && s.contains(Constants.TABLE_SEPARATOR)) {
                val isEquals = s.contains(Constants.TABLE_SEPARATOR + Constants.TABLE_SEPARATOR)
                var tablechar: String
                val items = s.split(Constants.TABLE_SEPARATOR.toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                tablechar = if (isEquals) {
                    s.substring(s.indexOf(Constants.TABLE_SEPARATOR) + 1)
                } else {
                    items[1]
                }
                // Remove line breaks
                tablechar = tablechar.replace(Constants.S_NEWLINE.toRegex(), Constants.EMPTY)
                    .replace(Constants.S_CARRY_RETURN.toRegex(), Constants.EMPTY)
                if (Constants.RESERVED_CHARS.contains(tablechar)) {
                    Utils.log("WARNING - Table char \"$tablechar\" will not be used because it is reserved.")
                } else {
                    addToTable(
                        Utils.hexStringCharToByte(items[0].uppercase(Locale.getDefault()).trim { it <= ' ' }),
                        tablechar
                    )
                }
            } else {
                Utils.log("ERROR - Line not valid: '$s'")
            }
        }
    }

    /**
     * Based in a displacement, reconstruct full table from $20 to $7E (included).
     *
     * @param displacement the displacement
     */
    constructor(displacement: Int) {
        val tableLines: MutableList<String> = ArrayList()
        val sb = StringBuilder()
        var currChar = ((Constants.MIN_PRINTABLE_CHAR - displacement) and Constants.MASK_8BIT).toByte()
        for (i in Constants.MIN_PRINTABLE_CHAR..Constants.MAX_PRINTABLE_CHAR) {
            if (!Constants.RESERVED_CHARS.contains(i.toChar().toString())) {
                sb.setLength(0)
                sb.append(String.format(Constants.HEX_16_FORMAT, currChar))
                sb.append(Constants.TABLE_SEPARATOR)
                sb.append(i.toChar())
                sb.append(Constants.S_NEWLINE)
                tableLines.add(sb.toString())
            }
            currChar++
        }
        loadLines(tableLines)
    }

    /**
     * Loads the table lines.
     *
     * @param tableLines the table lines
     */
    constructor(tableLines: List<String>) {
        loadLines(tableLines)
    }

    /**
     * Empty table.
     */
    constructor()

    /**
     * Loads a table lines file.
     *
     * @param tableFile the table file
     */
    constructor(tableFile: String?) {
        loadLines(
            FileUtils.getAsciiFile(tableFile).replace(Constants.UTF_8_BOM_BE, Constants.EMPTY).replace(
                Constants.UTF_8_BOM_LE,
                Constants.EMPTY
            ).split(Constants.NEWLINE.toString().toRegex()).dropLastWhile { it.isEmpty() }.toList()
        )
    }


    /**
     * Translates a hex string to ascii.
     */
    /**
     * Translates a hex string to ascii.
     */
    @JvmOverloads
    fun toAscii(hexString: ByteArray, expand: Boolean, decodeUnknown: Boolean = false): String {
        val sb = StringBuilder()
        for (b: Byte in hexString) {
            sb.append(toString(b, expand, decodeUnknown))
        }
        return sb.toString()
    }

    /**
     * Simple string to hex using table.
     */
    fun toHex(aString: String): ByteArray {
        val res = ByteArray(aString.length)
        val hexSpace: Byte = if (reversed.containsKey(Constants.SPACE_STR)) {
            reversed[Constants.SPACE_STR]!!
        } else {
            0
        }
        for ((i, c: Char) in aString.toCharArray().withIndex()) {
            val b = reversed[c.toString()]
            if (b == null) {
                res[i] = hexSpace
            } else {
                res[i] = b
            }
        }
        return res
    }

    /**
     * Adds the to table.
     *
     * @param entry the entry
     * @param theChar the char
     */
    fun addToTable(entry: Byte, theChar: String) {
        table[entry] = theChar
        reversed[theChar] = entry
    }

    /**
     * Translates to ascii entry to a hex string.
     *
     * @param hexString the hex string
     * @param entry the entry
     * @param showExtracting shows current extraction
     * @return the string
     */
    fun toAscii(hexString: ByteArray, entry: OffsetEntry, showExtracting: Boolean): String {
        val sb = StringBuilder()
        var bytesreaded = 0
        var bytesreadedStart = 0
        val line = StringBuilder()
        if (showExtracting) {
            Utils.log(
                "Extracting [" + Utils.fillLeft(Integer.toHexString(entry.start), Constants.HEX_ADDR_SIZE)
                    .uppercase(Locale.getDefault()) + ":" + Utils.fillLeft(
                    Integer.toHexString(entry.end),
                    Constants.HEX_ADDR_SIZE
                ).uppercase(Locale.getDefault()) + "]"
            )
        }
        sb.append(entry.toString()).append(Constants.NEWLINE)
        for (i in entry.start..entry.end) {
            val hex = hexString[i]
            bytesreaded++
            if (table.containsKey(hex)) {
                val value = table[hex]
                if (value!!.length > 1) {
                    line.append(Constants.S_CODEWORD_START)
                    line.append(value)
                    line.append(Constants.S_CODEWORD_END)
                } else {
                    line.append(value)
                }
            }
            if (!table.containsKey(hex) || i == entry.end) {
                val hexStr = String.format(Constants.HEX_16_FORMAT, hexString[i])
                if (!table.containsKey(hex)) {
                    line.append(Constants.HEX_CHAR).append(hexStr).append(Constants.HEX_CHAR)
                }
                if (entry.endChars?.contains(hexStr) == true || i == entry.end) {
                    val originalLine = line.toString()
                    val numChars = Utils.fillLeft(originalLine.length.toString(), Constants.LEN_NUM_CHARS)
                    val numCharsHex =
                        Utils.fillLeft((bytesreaded - bytesreadedStart).toString(), Constants.LEN_NUM_CHARS)
                    sb.append(Constants.COMMENT_LINE).append(
                        Utils.fillLeft(
                            Integer.toHexString(entry.start + bytesreadedStart),
                            Constants.HEX_ADDR_SIZE
                        ).uppercase(Locale.getDefault())
                    )
                    sb.append(Constants.ORG_STR_OPEN).append(originalLine).append(Constants.ORG_STR_CLOSE)
                    sb.append(Constants.STR_NUM_CHARS).append(numChars).append(Constants.STR_NUM_CHARS)
                        .append(numCharsHex)
                    sb.append(Constants.NEWLINE)
                    sb.append(line).append(Constants.STR_NUM_CHARS).append(numCharsHex)
                    sb.append(Constants.NEWLINE)
                    line.setLength(0)
                    bytesreadedStart = bytesreaded
                }
            }
        }
        sb.append(Constants.MAX_BYTES).append(bytesreaded).append(Constants.NEWLINE)
        if (showExtracting) {
            Utils.log("TOTAL BYTES TO ASCII: $bytesreaded")
        }
        return sb.toString()
    }

    /**
     * Transforms the ascii string to hex byte[].
     *
     * @param string the string
     * @param entry the entry
     * @return the byte[]
     */
    fun toHex(string: String, entry: OffsetEntry): ByteArray {
        var offset = 0
        var offsetStart = 0
        val hex = ByteArray(string.length * 16)
        var maxsize = 0
        var end = false
        var next: Char
        var incomment = false
        val hexSpace: Byte = if (reversed.containsKey(Constants.SPACE_STR)) {
            (reversed[Constants.SPACE_STR])!!
        } else {
            0
        }
        var stringStart = 0
        var i = 0
        while (i < string.length && !end) {
            next = string.substring(i, i + 1)[0]
            if (incomment) {
                if (Constants.NEWLINE == next) {
                    incomment = false
                }
            } else {
                when (next) {
                    Constants.COMMENT_LINE -> incomment = true
                    Constants.MAX_BYTES -> {
                        maxsize = string.substring(i + 1, string.length - 1).toInt()
                        end = true
                    }

                    Constants.HEX_CHAR -> {
                        val hexChar = string.substring(i + 1, i + 3)
                        i += 3
                        if (Constants.HEX_CHAR != string.substring(i, i + 1)[0]) {
                            var j = i - 100
                            if (j < 0) {
                                j = 0
                            }
                            Utils.log("ERROR! HEX CHAR NOT CLOSED AT: " + i + " -> " + string.substring(j, i + 1))
                        }
                        if (entry.endChars?.contains(hexChar) == true) {
                            var nextchar = string.substring(i + 1, i + 2)[0]
                            while (Constants.ADDR_CHAR == nextchar) {
                                i++
                                val hexTo = string.substring(i + 1, i + 1 + 8)
                                Utils.log(
                                    (
                                            "INSERTING OFFSET " + Utils.fillLeft(
                                                Integer.toHexString(offsetStart),
                                                Constants.HEX_ADDR_SIZE
                                            ) + " TO " + hexTo
                                            )
                                )
                                i += 8
                                nextchar = string.substring(i + 1, i + 2)[0]
                            }
                            // Check size
                            if (Constants.STR_NUM_CHARS == nextchar) {
                                i++
                                // Search end char
                                var j = i
                                var testEnd = nextchar
                                while (testEnd != Constants.NEWLINE) {
                                    j++
                                    testEnd = string.substring(j, j + 1)[0]
                                }
                                val length = string.substring(i + 1, j).toInt()
                                if (offset - offsetStart > length - 1) {
                                    Utils.log(
                                        (
                                                "ERROR!!! STRING TOO LARGE (" + Utils.fillLeft(
                                                    (offset - offsetStart + 1).toString(),
                                                    4
                                                ) + " - " + Utils.fillLeft(length.toString(), 4) + ")!!!"
                                                )
                                    )
                                    Utils.log(string.substring(stringStart, i))
                                } else {
                                    if (offset - offsetStart < length - 1) {
                                        Utils.log(
                                            (
                                                    "WARNING!!! STRING TOO SMALL (" + Utils.fillLeft(
                                                        (offset - offsetStart + 1).toString(),
                                                        4
                                                    ) + " - " + Utils.fillLeft(length.toString(), 4) + ")!!!"
                                                    )
                                        )
                                        Utils.log(string.substring(stringStart, i))
                                        while (offset - offsetStart < length - 1) {
                                            hex[offset++] = hexSpace
                                        }
                                    }
                                }
                                i += j - i - 1
                                stringStart = i + 2
                            }
                            hex[offset++] = Utils.hexStringCharToByte(hexChar)
                            offsetStart = offset
                        } else {
                            hex[offset++] = Utils.hexStringCharToByte(hexChar)
                        }
                    }

                    Constants.STR_NUM_CHARS -> {
                        // Search end char
                        var j = i
                        var testEnd = next
                        while (testEnd != Constants.NEWLINE) {
                            j++
                            testEnd = string.substring(j, j + 1)[0]
                        }
                        val length = string.substring(i + 1, j).toInt()
                        if (offset - offsetStart - 1 > length - 1) {
                            Utils.log(
                                (
                                        "ERROR!!! NOENDED STRING TOO LARGE (" + Utils.fillLeft(
                                            (offset - offsetStart).toString(),
                                            4
                                        ) + " - " + Utils.fillLeft(length.toString(), 4) + ")!!!"
                                        )
                            )
                            Utils.log(string.substring(stringStart, i))
                        } else {
                            if (offset - offsetStart - 1 < length - 1) {
                                while (offset - offsetStart - 1 < length - 1) {
                                    hex[offset++] = hexSpace
                                }
                            }
                        }
                        i += j - i - 1
                        stringStart = i + 2
                    }

                    Constants.NEWLINE -> {}
                    Constants.CODEWORD_START -> {
                        var k = i
                        // Search CODEWORD_END if not end, space char
                        var foundCodeWord = false
                        while (!foundCodeWord && k < string.length - 2) {
                            k++
                            foundCodeWord = (Constants.S_CODEWORD_END == string.substring(k, k + 1))
                        }
                        var codeWordValue = hexSpace
                        if (foundCodeWord) {
                            // Get Key/value
                            val key = string.substring(i + 1, k)
                            if (reversed.containsKey(key)) {
                                codeWordValue = (reversed[key])!!
                            } else {
                                Utils.log("WARNING!!! CODE WORD NOT IN TABLE: '$key'")
                            }
                            i = k
                        }
                        hex[offset++] = codeWordValue
                    }

                    else -> {
                        val nextString = next.toString()
                        var value = hexSpace
                        if (reversed.containsKey(nextString)) {
                            value = (reversed[nextString])!!
                        } else {
                            Utils.log("WARNING!!! CHARACTER NOT IN TABLE: '$nextString'")
                            Utils.log(string.substring(stringStart, i))
                        }
                        hex[offset++] = value
                    }
                }
            }
            i++
        }
        if (offset > maxsize) {
            offset = maxsize
        }
        // No dejemos que la siguiente cadena empiece tarde
        if (offset < maxsize) {
            Utils.log("WARNING!!! STRING TOO SMALL")
            Utils.log(string.substring(stringStart))
            for (j in offset until maxsize) {
                hex[i] = Constants.PAD_CHAR
            }
        }
        if (maxsize == 0) {
            maxsize = offset
        }
        if (Utils.debug) {
            Utils.logNoNL(
                "BYTES TO HEX: " + Utils.fillLeft(
                    offset.toString(),
                    5
                ) + " / " + Utils.fillLeft(maxsize.toString(), 5)
            )
        }
        return hex.copyOf(maxsize)
    }

    /**
     * The Enum ENTRIES_STATUS.
     */
    internal enum class EntriesStatus {
        /** The searching start of string.  */
        SEARCHING_START_OF_STRING,

        /** The searching end of string.  */
        SEARCHING_END_OF_STRING,

        /** The skipping chars.  */
        SKIPPING_CHARS
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
    @Throws(IOException::class)
    fun getAllEntries(
        secondFileBytes: ByteArray,
        numMinChars: Int,
        numIgnoredChars: Int,
        endCharsList: List<String>,
        dictFile: String?
    ): String {
        searchPercent = 0f
        val offsetEntryList: MutableList<OffsetEntry> = ArrayList()
        val dict = FileUtils.getAsciiFile(dictFile).split(Constants.S_NEWLINE.toRegex()).dropLastWhile { it.isEmpty() }
            .toSet()
        var entryStart = 0
        var validString = false
        val word = StringBuilder()
        val sentence = StringBuilder()
        var dataChar: String?
        val skippedChars: MutableList<String> = ArrayList()
        var status: EntriesStatus = EntriesStatus.SEARCHING_START_OF_STRING
        var lastTime = System.currentTimeMillis()
        var i = 0
        while (i < secondFileBytes.size - numMinChars && !Thread.currentThread().isInterrupted) {
            searchPercent = i * 100f / secondFileBytes.size
            if (System.currentTimeMillis() - lastTime > 1000) {
                lastTime = System.currentTimeMillis()
                Utils.log("$searchPercent% completed.")
            }
            val readByteObj = secondFileBytes[i]
            val dataCharHex = String.format(Constants.HEX_16_FORMAT, readByteObj)
            dataChar = table.getOrDefault(readByteObj, null)
            when (status) {
                EntriesStatus.SEARCHING_START_OF_STRING -> if (dataChar != null) {
                    entryStart = i
                    word.setLength(0)
                    sentence.setLength(0)
                    sentence.append(dataChar)
                    word.append(dataChar)
                    validString = false
                    status = EntriesStatus.SEARCHING_END_OF_STRING
                }

                EntriesStatus.SEARCHING_END_OF_STRING -> if (dataChar != null) {
                    sentence.append(dataChar)
                    word.append(dataChar)
                } else {
                    if (Utils.getCleanedString(word.toString()).length > 1) {
                        if (!validString) {
                            validString = Utils.stringHasWords(dict, word.toString())
                        }
                        sentence.append(Constants.SPACE_STR)
                        word.append(Constants.SPACE_STR)
                        skippedChars.clear()
                        skippedChars.add(dataCharHex)
                        status = EntriesStatus.SKIPPING_CHARS
                    } else {
                        if (validString) {
                            offsetEntryList.add(OffsetEntry(entryStart, i, endCharsList.toMutableList()))
                        }
                        entryStart = 0
                        status = EntriesStatus.SEARCHING_START_OF_STRING
                    }
                }

                EntriesStatus.SKIPPING_CHARS -> if (dataChar != null) {
                    word.setLength(0)
                    sentence.append(dataChar)
                    word.append(dataChar)
                    status = EntriesStatus.SEARCHING_END_OF_STRING
                } else {
                    skippedChars.add(dataCharHex)
                    val skippedAreEndings = endCharsList.any { o: String ->
                        o in skippedChars
                    }
                    if (skippedChars.size > numIgnoredChars) {
                        if (sentence.length > numMinChars) {
                            if (Utils.stringHasWords(dict, word.toString()) || validString && skippedAreEndings) {
                                offsetEntryList.add(OffsetEntry(entryStart, i, endCharsList.toMutableList()))
                            } else {
                                if (validString) {
                                    offsetEntryList.add(OffsetEntry(entryStart, i, endCharsList.toMutableList()))
                                }
                            }
                        }
                        entryStart = 0
                        status = EntriesStatus.SEARCHING_START_OF_STRING
                    }
                }
            }
            i++
        }
        if (entryStart > 0) {
            offsetEntryList.add(OffsetEntry(entryStart, secondFileBytes.size - 1, endCharsList.toMutableList()))
        }
        word.setLength(0)
        offsetEntryList.forEach { offsetEntry: OffsetEntry ->
            word.append(offsetEntry.toEntryString()).append(Constants.OFFSET_STR_SEPARATOR)
        }
        return word.toString()
    }

    /**
     * Transforms the table into ascii.
     *
     * @return the string
     */
    fun toAsciiTable(): String {
        val sb = StringBuilder()
        Utils.sortByValue(table).forEach { (key: Byte, value: String) ->
            if (SPANISH_CHARS.containsKey(value)) {
                val spaChar = SPANISH_CHARS[value]
                sb.append(String.format(Constants.HEX_16_FORMAT, key)).append(Constants.TABLE_SEPARATOR).append(spaChar)
                sb.append(Constants.S_NEWLINE)
            }
            sb.append(String.format(Constants.HEX_16_FORMAT, key)).append(Constants.TABLE_SEPARATOR).append(value)
            sb.append(Constants.S_NEWLINE)
        }
        return sb.toString()
    }

    /* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
    override fun hashCode(): Int {
        val prime = 31
        var result = 1
        result = prime * result + (table.hashCode())
        return result
    }

    /**
     * Equals.
     *
     * @param other the obj
     * @return true, if successful
     */
    override fun equals(other: Any?): Boolean {
        if (other !is HexTable) {
            return false
        }
        return (table == other.table)
    }

    companion object {
        /** The Constant serialVersionUID.  */
        private const val serialVersionUID = -2128637206318658102L

        /** The Constant TABLE_KEY_A.  */
        private const val TABLE_KEY_A = "A"

        /** The Constant TABLE_KEY_LOWA.  */
        private const val TABLE_KEY_LOWA = "a"

        /** The Constant TABLE_KEY_ZERO.  */
        private const val TABLE_KEY_ZERO = "0"

        /** The Constant SPANISH_CHARS.  */
        private val SPANISH_CHARS: Map<String, String> = mapOf(
            "a" to "á",
            "e" to "é",
            "i" to "í",
            "o" to "ó",
            "u" to "ú",
            "n" to "ñ",
            "!" to "¡",
            "?" to "¿",
            "A" to "Á",
            "E" to "É",
            "I" to "Í",
            "O" to "Ó",
            "U" to "Ú",
            "N" to "Ñ"
        )
    }
}
