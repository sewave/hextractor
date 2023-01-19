package com.wave.hextractor.pojo

import com.wave.hextractor.util.Constants
import com.wave.hextractor.util.Utils
import java.io.Serializable
import java.util.*

/**
 * Offset pair entry.
 */
class OffsetEntry : Comparable<OffsetEntry?>, Serializable {

    var start: Int = 0

    var end: Int = 0

    /** The end chars.  */
    var endChars: MutableList<String>? = ArrayList()

    /**
     * Instantiates a new offset entry.
     */
    constructor()

    /**
     * Instantiates a new offset entry.
     *
     * @param start the start
     * @param end the end
     * @param endChars the end chars
     */
    constructor(start: Int, end: Int, endChars: MutableList<String>?) {
        this.start = start
        this.end = end
        this.endChars = endChars
    }

    /**
     * Instantiates a new offset entry.
     *
     * @param asciiString the ascii string
     */
    constructor(asciiString: String) {
        val values: Array<String> =
            asciiString.substring(1).split(Constants.OFFSET_CHAR_SEPARATOR.toRegex()).dropLastWhile { it.isEmpty() }
                .toTypedArray()
        start = values[0].toInt(Constants.HEX_RADIX)
        if (values.size > 1) {
            end = values[1].toInt(Constants.HEX_RADIX)
            if (values.size > 2) {
                endChars?.addAll(values.toList().subList(2, values.size))
            }
        }
    }

    /*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#toString()
	 */
    override fun toString(): String {
        return Constants.ADDR_CHAR.toString() + toEntryString()
    }

    /**
     * To entry string.
     *
     * @return the string
     */
    fun toEntryString(): String {
        val sb: StringBuilder = StringBuilder()
        sb.append(Utils.fillLeft(Integer.toHexString(start), Constants.HEX_ADDR_SIZE).uppercase(Locale.getDefault()))
            .append(Constants.OFFSET_CHAR_SEPARATOR).append(Utils.intToHexString(end, Constants.HEX_ADDR_SIZE))
        for (endChar: String in endChars.orEmpty()) {
            sb.append(Constants.OFFSET_CHAR_SEPARATOR)
            sb.append(Utils.fillLeft(endChar, Constants.HEX_SIZE).uppercase(Locale.getDefault()))
        }
        return sb.toString()
    }

    val hexComment: String
        /**
         * Comment in style: START-END:BYTES.
         *
         * @return the hex comment
         */
        get() = (
            Constants.COMMENT_LINE.toString() +
                Utils.intToHexString(start, Constants.HEX_ADDR_SIZE) +
                Constants.OFFSET_CHAR_SEPARATOR +
                Utils.intToHexString(end, Constants.HEX_ADDR_SIZE) +
                Constants.OFFSET_LENGTH_SEPARATOR +
                Utils.intToHexString(end - start + 1, Constants.HEX_ADDR_SIZE)
            )

    /**
     * Gets the hex string.
     *
     * @param bytes the bytes
     * @return the hex string
     */
    fun getHexString(bytes: ByteArray): String {
        val sb: StringBuilder = StringBuilder()
        for (i in start..end) {
            sb.append(String.format(Constants.HEX_16_FORMAT, bytes[i])).append(Constants.SPACE_STR)
        }
        return sb.toString()
    }

    /**
     * Split the entry into minMaxLength chunks.
     *
     * @param minMaxLength the min max length
     * @param bytes the bytes
     * @return the list
     */
    fun split(minMaxLength: Int, bytes: ByteArray): List<OffsetEntry> {
        val res: MutableList<OffsetEntry> = ArrayList()
        var readBytes = 0
        var pointerStart: Int = start
        for (pointer in start..end) {
            if ((
                readBytes >= minMaxLength &&
                    endChars.orEmpty().contains(String.format(Constants.HEX_16_FORMAT, bytes[pointer]))
                )
            ) {
                res.add(OffsetEntry(pointerStart, pointer, endChars?.toMutableList()))
                pointerStart = pointer + 1
                readBytes = 0
            }
            readBytes++
        }
        if (pointerStart < end) {
            res.add(OffsetEntry(pointerStart, end, endChars))
        }
        return res
    }

    val hexTarget: String
        /**
         * Gets the hex target.
         *
         * @return the hex target
         */
        get() {
            return (
                Constants.ADDR_STR + Utils.intToHexString(start, Constants.HEX_ADDR_SIZE) +
                    Constants.OFFSET_LENGTH_SEPARATOR +
                    Utils.intToHexString(end, Constants.HEX_ADDR_SIZE)
                )
        }

    /*
    * (non-Javadoc)
    *
    * @see java.lang.Comparable#compareTo(java.lang.Object)
    */
    override operator fun compareTo(other: OffsetEntry?): Int {
        require(other != null)
        var res = 0
        // Returns a negative integer, zero, or a positive integer as this object
        // is less than, equal to, or greater than the specified object.
        if (start < other.start) {
            res = -1
        }
        if (start > other.start) {
            res = 1
        }
        return res
    }

    /**
     * The inserted block can't touch others, so if they are totally inside
     * it eats them and if they are mid inside mid outside it expands.
     *
     * @param offEntries the off entries
     */
    fun mergeInto(offEntries: MutableList<OffsetEntry>) {
        val result: MutableList<OffsetEntry> = ArrayList()
        for (oEntry: OffsetEntry in offEntries) {
            resultMerge(
                result,
                oEntry,
                oEntry.start in start..end,
                oEntry.end in start..end
            )
        }
        result.add(this)
        // As we iterate on it, it's better to update it at the end
        offEntries.clear()
        offEntries.addAll(result)
    }

    private fun resultMerge(
        result: MutableList<OffsetEntry>,
        oEntry: OffsetEntry,
        startInside: Boolean,
        endInside: Boolean
    ) {
        // If out of range, included in the result
        if (!startInside && !endInside) {
            result.add(oEntry)
        } else {
            // One out and one in
            if (!startInside) {
                start = oEntry.start
            }
            if (!endInside) {
                end = oEntry.end
            }
        }
        // If both are inside, the offset is lost
    }

    val length: Int
        /**
         * Gets the offset length.
         *
         * @return the length
         */
        get() {
            return end - start
        }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val that: OffsetEntry = other as OffsetEntry
        return (
            (start == that.start) && (
                end == that.end
                ) &&
                Objects.equals(endChars, that.endChars)
            )
    }

    override fun hashCode(): Int {
        return Objects.hash(start, end, endChars)
    }

    companion object {
        /** The Constant serialVersionUID.  */
        private const val serialVersionUID: Long = -2209881475541068244L

        /**
         * Creates an offset entry from a hex range (FF as ending char).
         *
         * @param string the string
         * @return the offset entry
         */
        fun fromHexRange(string: String): OffsetEntry {
            val numbers: IntArray = Utils.hexStringListToIntList(
                string.split(Constants.OFFSET_LENGTH_SEPARATOR.toRegex()).dropLastWhile { it.isEmpty() }
                    .toTypedArray()
            )
            return OffsetEntry(numbers[0], numbers[1], mutableListOf("FF"))
        }
    }
}
