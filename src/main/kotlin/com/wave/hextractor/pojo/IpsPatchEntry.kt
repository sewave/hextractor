package com.wave.hextractor.pojo

import com.wave.hextractor.util.Constants
import com.wave.hextractor.util.Utils

/**
 * Entry of ips patch.
 * @author slcantero
 */
class IpsPatchEntry {

    var offset = 0

    var size = 0

    var rleSize = 0

    var data: ByteArray = ByteArray(0)
    val binSize: Int
        get() {
            var binSize = IPS_OFFSET_SIZE + IPS_DATA_SIZE
            if (size == IPS_RLE_MODE) {
                // Add rle size
                binSize += IPS_RLE_DATA_SIZE
            }
            binSize += data.size
            return binSize
        }

    /**
     * Converts the entry to byte[].
     *
     * @return the byte[]
     */
    fun toBin(): ByteArray {
        val binEntry = ByteArray(binSize)
        // Append OFFSET - DATA_SIZE (- RLE_DATA_SIZE) - DATA (1-N)
        var dataOff = 0
        val offsetAr = Utils.intToByteArray(offset)
        binEntry[dataOff++] = offsetAr[1]
        binEntry[dataOff++] = offsetAr[2]
        binEntry[dataOff++] = offsetAr[3]
        val sizeAr = Utils.intToByteArray(size)
        binEntry[dataOff++] = sizeAr[2]
        binEntry[dataOff++] = sizeAr[3]
        if (size == IPS_RLE_MODE) {
            // RLE entry
            val rleSizeAr = Utils.intToByteArray(rleSize)
            binEntry[dataOff++] = rleSizeAr[2]
            binEntry[dataOff++] = rleSizeAr[3]
            binEntry[dataOff] = data[0]
        } else {
            System.arraycopy(data, 0, binEntry, dataOff, data.size)
        }
        return binEntry
    }

    constructor() : super()

    override fun toString(): String {
        val sb = StringBuilder()
        val sizeData: Int = if (size == IPS_RLE_MODE) {
            sb.append("RLE ")
            rleSize
        } else {
            sb.append("PAT ")
            size
        }
        sb.append("[offset=").append(Utils.getHexFilledLeft(offset, Constants.HEX_ADDR_SIZE)).append(", size=").append(
            Utils.getHexFilledLeft(sizeData, Constants.HEX_ADDR_SIZE)
        ).append("]")
        return sb.toString()
    }

    constructor(offset: Int, size: Int, data: ByteArray) : super() {
        this.offset = offset
        this.size = size
        this.data = data
    }

    constructor(offset: Int, size: Int, rleSize: Int, data: ByteArray) : super() {
        this.offset = offset
        this.size = size
        this.rleSize = rleSize
        this.data = data
    }

    companion object {
        /** The Constant IPS_OFFSET_SIZE.  */
        const val IPS_OFFSET_SIZE = 3

        /** The Constant IPS_DATA_SIZE.  */
        const val IPS_DATA_SIZE = 2

        /** The Constant IPS_RLE_DATA_SIZE.  */
        const val IPS_RLE_DATA_SIZE = 2

        /** The Constant IPS_RLE_MODE.  */
        const val IPS_RLE_MODE = 0

        /** The Constant IPS_CHUNK_MIN_SIZE.  */
        const val IPS_CHUNK_MIN_SIZE = IPS_OFFSET_SIZE + IPS_DATA_SIZE + 1
    }
}
