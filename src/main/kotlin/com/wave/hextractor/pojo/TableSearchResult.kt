package com.wave.hextractor.pojo

import com.wave.hextractor.helper.HexTable
import com.wave.hextractor.util.Utils

/**
 * The Class TableSearchResult.
 */
data class TableSearchResult(
    val hexTable: HexTable,
    val offset: Int,
    val word: String
) {

    override fun toString(): String {
        return word + " @ 0x" + Utils.intToHexString(offset, 6) + " Table=" + hexTable.toSelectionString()
    }

    override fun hashCode(): Int {
        val prime = 31
        var result = 1
        result = prime * result + offset.hashCode()
        result = prime * result + word.hashCode()
        return result
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        if (other == null) {
            return false
        }
        if (javaClass != other.javaClass) {
            return false
        }
        val otherClass = other as TableSearchResult
        if (offset != otherClass.offset) {
            return false
        }
        return word == otherClass.word
    }
}
