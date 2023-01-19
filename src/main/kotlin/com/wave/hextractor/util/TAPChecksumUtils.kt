package com.wave.hextractor.util

import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*

/**
 * The Class TAPChecksumUtils.
 */
object TAPChecksumUtils {
    /** The Constant BYTE_SIZE_16_BITS.  */
    private const val BYTE_SIZE_16_BITS = 2

    /** The Constant TZX_HEADER_SIZE.  */
    private const val TZX_HEADER_SIZE = 0x0A

    /** The Constant TZX_ID_STANDARD_DATA.  */
    private const val TZX_ID_STANDARD_DATA: Byte = 0x10

    /** The Constant TZX_ID_TURBO_DATA.  */
    private const val TZX_ID_TURBO_DATA: Byte = 0x11

    /** The Constant TZX_ID_GEN_DATA.  */
    private const val TZX_ID_GEN_DATA: Byte = 0x19

    /** The Constant TZX_ID_PURE_DATA.  */
    private const val TZX_ID_PURE_DATA: Byte = 0x14

    /** The Constant TZX_ID_PURE_TONE.  */
    private const val TZX_ID_PURE_TONE: Byte = 0x12

    /** The Constant TZX_ID_SEQ_PULSES.  */
    private const val TZX_ID_SEQ_PULSES: Byte = 0x13

    /** The Constant TZX_ID_DIRECT_REC.  */
    private const val TZX_ID_DIRECT_REC: Byte = 0x15

    /** The Constant TZX_ID_CSW_REC.  */
    private const val TZX_ID_CSW_REC: Byte = 0x18

    /** The Constant TZX_ID_PAUSE_STOP.  */
    private const val TZX_ID_PAUSE_STOP: Byte = 0x20

    /** The Constant TZX_ID_GROUP_START.  */
    private const val TZX_ID_GROUP_START: Byte = 0x21

    /** The Constant TZX_ID_JUMP_BLOCK.  */
    private const val TZX_ID_JUMP_BLOCK: Byte = 0x23

    /** The Constant TZX_ID_LOOP_START.  */
    private const val TZX_ID_LOOP_START: Byte = 0x24

    /** The Constant TZX_ID_CALL_SEQ.  */
    private const val TZX_ID_CALL_SEQ: Byte = 0x26

    /** The Constant TZX_ID_SEL_BLOCK.  */
    private const val TZX_ID_SEL_BLOCK: Byte = 0x28

    /** The Constant TZX_ID_STOP_TAPE_48K.  */
    private const val TZX_ID_STOP_TAPE_48K: Byte = 0x2A

    /** The Constant TZX_ID_SET_SIG_LEV.  */
    private const val TZX_ID_SET_SIG_LEV: Byte = 0x2B

    /** The Constant TZX_ID_TEXT_DESC.  */
    private const val TZX_ID_TEXT_DESC: Byte = 0x30

    /** The Constant TZX_ID_MSG_BLOCK.  */
    private const val TZX_ID_MSG_BLOCK: Byte = 0x31

    /** The Constant TZX_ID_ARCHIVE_INFO.  */
    private const val TZX_ID_ARCHIVE_INFO: Byte = 0x32

    /** The Constant TZX_ID_HARD_TYPE.  */
    private const val TZX_ID_HARD_TYPE: Byte = 0x33

    /** The Constant TZX_ID_CUSTOM_INFO_BLOCK.  */
    private const val TZX_ID_CUSTOM_INFO_BLOCK: Byte = 0x35

    /** The Constant TZX_ID_GLUE_BLOCK.  */
    private const val TZX_ID_GLUE_BLOCK: Byte = 0x5A

    /** The Constant TZX_ID_PURE_TONE_LEN.  */
    private const val TZX_ID_PURE_TONE_LEN = 0x04

    /** The Constant TZX_WORD_LEN.  */
    private const val TZX_WORD_LEN = 0x02

    /** The Constant TZX_BYTE_LEN.  */
    private const val TZX_BYTE_LEN = 0x01

    /** The Constant TZX_ID_DIRECT_REC_LOFF.  */
    private const val TZX_ID_DIRECT_REC_LOFF = 0x05

    /** The Constant TZX_ID_PAUSE_STOP_LEN.  */
    private const val TZX_ID_PAUSE_STOP_LEN = 0x02

    /** The Constant TZX_ID_STOP_TAPE_48K_LEN.  */
    private const val TZX_ID_STOP_TAPE_48K_LEN = 0x04

    /** The Constant TZX_ID_SET_SIG_LEV_LEN.  */
    private const val TZX_ID_SET_SIG_LEV_LEN = 0x05

    /** The Constant TZX_ID_GLUE_BLOCK_LEN.  */
    private const val TZX_ID_GLUE_BLOCK_LEN = 0x9

    /** The Constant DATA_TYPES.  */
    private val DATA_TYPES = listOf(TZX_ID_STANDARD_DATA, TZX_ID_TURBO_DATA, TZX_ID_PURE_DATA)

    /** The Constant TZX_ID_STANDARD_DATA_DOFF.  */
    private const val TZX_ID_STANDARD_DATA_DOFF = 0x04

    /** The Constant TZX_ID_STANDARD_DATA_LOFF.  */
    private const val TZX_ID_STANDARD_DATA_LOFF = 0x02

    /** The Constant TZX_ID_TURBO_DATA_DOFF.  */
    private const val TZX_ID_TURBO_DATA_DOFF = 0x12

    /** The Constant TZX_ID_TURBO_DATA_LOFF.  */
    private const val TZX_ID_TURBO_DATA_LOFF = 0x0F

    /** The Constant TZX_ID_PURE_DATA_DOFF.  */
    private const val TZX_ID_PURE_DATA_DOFF = 0x0A

    /** The Constant TZX_ID_PURE_DATA_LOFF.  */
    private const val TZX_ID_PURE_DATA_LOFF = 0x07

    /** The Constant TZX_ID_GROUP_END.  */
    private const val TZX_ID_GROUP_END: Byte = 0x22

    /** The Constant TZX_ID_LOOP_END.  */
    private const val TZX_ID_LOOP_END: Byte = 0x25

    /** The Constant TZX_ID_RET_SEQ.  */
    private const val TZX_ID_RET_SEQ: Byte = 0x27

    /**
     * Check update zx tap checksum.
     *
     * @param fileBytes the file bytes
     * @param isTzx the is tzx
     * @param originalTapeBytes the original tape bytes
     * @return the byte[]
     */
    private fun checkUpdateZxTapChecksum(
        fileBytes: ByteArray,
        isTzx: Boolean,
        originalTapeBytes: ByteArray?
    ): ByteArray {
        val dataBlocks: List<ZxTapDataBlock>
        var originalDataBlocks: List<ZxTapDataBlock> = ArrayList()
        if (isTzx) {
            dataBlocks = getTzxTapDataBlocks(fileBytes)
            if (originalTapeBytes != null) {
                originalDataBlocks = getTzxTapDataBlocks(originalTapeBytes)
            }
        } else {
            dataBlocks = getDataBlocks(fileBytes)
            if (originalTapeBytes != null) {
                originalDataBlocks = getDataBlocks(originalTapeBytes)
            }
        }
        for (dataBlock: ZxTapDataBlock in dataBlocks) {
            val originalBlock = getOriginalBlock(originalDataBlocks, dataBlock)
            dataBlock.updateChecksum(originalBlock)
            System.arraycopy(dataBlock.dataBlock, 0, fileBytes, dataBlock.offset, dataBlock.dataBlock.size)
        }
        return fileBytes
    }

    /**
     * Gets the original block.
     *
     * @param originalDataBlocks the original data blocks
     * @param testDataBlock the test data block
     * @return the original block
     */
    private fun getOriginalBlock(
        originalDataBlocks: List<ZxTapDataBlock>,
        testDataBlock: ZxTapDataBlock
    ): ZxTapDataBlock? {
        var res: ZxTapDataBlock? = null
        for (dataBlock: ZxTapDataBlock in originalDataBlocks) {
            if (dataBlock.offset == testDataBlock.offset) {
                res = dataBlock
                break
            }
        }
        return res
    }

    /**
     * Gets the data blocks.
     *
     * @param fileBytes the file bytes
     * @return the data blocks
     */
    private fun getDataBlocks(fileBytes: ByteArray): List<ZxTapDataBlock> {
        val res: MutableList<ZxTapDataBlock> = ArrayList()
        var i = 0
        while (i < fileBytes.size) {
            val zxTapDataBlock = ZxTapDataBlock()
            res.add(zxTapDataBlock)
            val dataBlockLength = Utils.bytesToInt(fileBytes[i + 1], fileBytes[i])
            i += BYTE_SIZE_16_BITS
            zxTapDataBlock.offset = i
            zxTapDataBlock.dataBlock = fileBytes.copyOfRange(i, i + dataBlockLength)
            i += dataBlockLength
        }
        return res
    }

    /**
     * Gets the tzx tap data blocks.
     *
     * @param fileBytes the file bytes
     * @return the tzx tap data blocks
     */
    private fun getTzxTapDataBlocks(fileBytes: ByteArray): List<ZxTapDataBlock> {
        val res: MutableList<ZxTapDataBlock> = ArrayList()
        var i = TZX_HEADER_SIZE
        while (i < fileBytes.size) {
            val tzxBlockType = fileBytes[i]
            var bytesLength = 1
            i++
            if (DATA_TYPES.contains(tzxBlockType)) {
                val zxTzxDataBlock = ZxTapDataBlock()
                res.add(zxTzxDataBlock)
                when (tzxBlockType) {
                    TZX_ID_STANDARD_DATA -> {
                        bytesLength = Utils.bytesToInt(
                            fileBytes[i + 1 + TZX_ID_STANDARD_DATA_LOFF],
                            fileBytes[i + TZX_ID_STANDARD_DATA_LOFF]
                        )
                        i += TZX_ID_STANDARD_DATA_DOFF
                    }

                    TZX_ID_TURBO_DATA -> {
                        bytesLength = Utils.bytesToInt(
                            fileBytes[i + 2 + TZX_ID_TURBO_DATA_LOFF],
                            fileBytes[i + 1 + TZX_ID_TURBO_DATA_LOFF],
                            fileBytes[i + TZX_ID_TURBO_DATA_LOFF]
                        )
                        i += TZX_ID_TURBO_DATA_DOFF
                    }

                    TZX_ID_PURE_DATA -> {
                        bytesLength = Utils.bytesToInt(
                            fileBytes[i + 2 + TZX_ID_PURE_DATA_LOFF],
                            fileBytes[i + 1 + TZX_ID_PURE_DATA_LOFF],
                            fileBytes[i + TZX_ID_PURE_DATA_LOFF]
                        )
                        i += TZX_ID_PURE_DATA_DOFF
                    }

                    else -> {}
                }
                // Set i to data offset
                zxTzxDataBlock.offset = i
                zxTzxDataBlock.dataBlock = fileBytes.copyOfRange(i, i + bytesLength)
                i += bytesLength
            } else {
                // Skip y bytes
                when (tzxBlockType) {
                    TZX_ID_PURE_TONE -> i += TZX_ID_PURE_TONE_LEN
                    TZX_ID_SEQ_PULSES -> i += (fileBytes[i].toInt() and Constants.MASK_8BIT) * BYTE_SIZE_16_BITS + 1
                    TZX_ID_DIRECT_REC -> i += Utils.bytesToInt(
                        fileBytes[i + TZX_ID_DIRECT_REC_LOFF + 2],
                        fileBytes[i + TZX_ID_DIRECT_REC_LOFF + 1],
                        fileBytes[i + TZX_ID_DIRECT_REC_LOFF]
                    ) + TZX_ID_DIRECT_REC_LOFF

                    TZX_ID_CSW_REC, TZX_ID_GEN_DATA -> i += 4 + Utils.bytesToInt(
                        fileBytes[i + 3],
                        fileBytes[i + 2],
                        fileBytes[i + 1],
                        fileBytes[i]
                    )

                    TZX_ID_PAUSE_STOP -> i += TZX_ID_PAUSE_STOP_LEN
                    TZX_ID_GROUP_START -> i += (fileBytes[i].toInt() and Constants.MASK_8BIT) + 1
                    TZX_ID_JUMP_BLOCK, TZX_ID_LOOP_START -> i += TZX_WORD_LEN
                    TZX_ID_CALL_SEQ -> i += Utils.bytesToInt(
                        fileBytes[i + 1],
                        fileBytes[i]
                    ) * TZX_WORD_LEN + TZX_WORD_LEN

                    TZX_ID_SEL_BLOCK, TZX_ID_ARCHIVE_INFO -> i += Utils.bytesToInt(
                        fileBytes[i + 1],
                        fileBytes[i]
                    ) + TZX_WORD_LEN

                    TZX_ID_STOP_TAPE_48K -> i += TZX_ID_STOP_TAPE_48K_LEN
                    TZX_ID_SET_SIG_LEV -> i += TZX_ID_SET_SIG_LEV_LEN
                    TZX_ID_TEXT_DESC -> i += (fileBytes[i].toInt() and Constants.MASK_8BIT) + TZX_BYTE_LEN
                    TZX_ID_MSG_BLOCK -> i += (fileBytes[i + 1].toInt() and Constants.MASK_8BIT) + 2 * TZX_BYTE_LEN
                    TZX_ID_HARD_TYPE -> i += 3 * TZX_BYTE_LEN + (fileBytes[i + 1].toInt() and Constants.MASK_8BIT)
                    TZX_ID_CUSTOM_INFO_BLOCK -> i += 20 * TZX_BYTE_LEN + Utils.bytesToInt(
                        fileBytes[i + 4],
                        fileBytes[i + 3],
                        fileBytes[i + 2],
                        fileBytes[i + 1]
                    )

                    TZX_ID_GLUE_BLOCK -> i += TZX_ID_GLUE_BLOCK_LEN
                    TZX_ID_GROUP_END, TZX_ID_LOOP_END, TZX_ID_RET_SEQ -> {}
                    else -> {}
                }
            }
        }
        return res
    }

    /**
     * Updates tap checksum if original tap has correct checksums.
     *
     * @param inputFile the input file
     * @param originalFile the original file
     * @throws IOException the exception
     */
    @JvmOverloads
    @Throws(IOException::class)
    fun checkUpdateZxTapChecksum(inputFile: String, originalFile: String? = null) {
        Utils.log("Fixing ZX TAP checksums for \"$inputFile\" ")
        var originalFileBytes: ByteArray? = null
        if (originalFile != null) {
            originalFileBytes = Files.readAllBytes(Paths.get(originalFile))
            Utils.log("With original file \"$originalFile\"")
        } else {
            Utils.log("")
        }
        Files.write(
            Paths.get(inputFile),
            checkUpdateZxTapChecksum(Files.readAllBytes(Paths.get(inputFile)), false, originalFileBytes)
        )
    }

    /**
     * Updates Tzx checksum if original file has good checksums.
     *
     * @param inputFile the input file
     * @param originalFile the original file
     * @throws IOException the exception
     */
    @JvmOverloads
    @Throws(IOException::class)
    fun checkUpdateZxTzxChecksum(inputFile: String, originalFile: String? = null) {
        Utils.logNoNL("Fixing ZX TZX checksums for \"$inputFile\" ")
        var originalFileBytes: ByteArray? = null
        if (originalFile != null) {
            originalFileBytes = Files.readAllBytes(Paths.get(originalFile))
            Utils.log("With original file \"$originalFile\"")
        } else {
            Utils.log("")
        }
        Files.write(
            Paths.get(inputFile),
            checkUpdateZxTapChecksum(
                Files.readAllBytes(Paths.get(inputFile)),
                true,
                originalFileBytes
            )
        )
    }

    /**
     * The Class ZxTapDataBlock.
     */
    class ZxTapDataBlock {
        var offset = 0
        var dataBlock = ByteArray(0)

        private fun calculateChecksum(): Byte {
            var calculatedChecksum: Byte = 0
            for (i in 0 until dataBlock.size - 1) {
                calculatedChecksum = (calculatedChecksum.toInt() xor dataBlock[i].toInt()).toByte()
            }
            return calculatedChecksum
        }

        private val checksum: Byte
            get() = dataBlock[dataBlock.size - 1]

        /**
         * Update checksum.
         *
         * @param originalBlock the original block
         */
        fun updateChecksum(originalBlock: ZxTapDataBlock?) {
            // fields with dataLength < 2 have no checksum (2 bytes for size)
            if (dataBlock.size >= MIN_BLOCK_LENGTH) {
                val checksum = checksum
                val calculatedChecksum = calculateChecksum()
                var brokenData = false
                val flag = dataBlock[FLAG_OFF]
                Utils.logNoNL(
                    "[" + Utils.intToHexString(
                        flag.toInt(),
                        Constants.HEXSIZE_8BIT_VALUE
                    ) + "] @0x" + Utils.intToHexString(offset, Constants.HEX_ADDR_SIZE) +
                        " (Len: 0x" + Utils.intToHexString(
                        dataBlock.size,
                        Constants.HEX_ADDR_SIZE
                    ) + ") Checksums: 0x" +
                        Utils.intToHexString(
                            checksum.toInt() and Constants.MASK_8BIT,
                            Constants.HEXSIZE_8BIT_VALUE
                        ) +
                        "/0x" +
                        Utils.intToHexString(
                            calculatedChecksum.toInt() and Constants.MASK_8BIT,
                            Constants.HEXSIZE_8BIT_VALUE
                        )
                )
                when (flag) {
                    HEADER_BLOCK_FLAG -> {
                        Utils.logNoNL(" Header \"$fileName\" of type: ")
                        when (dataBlock[DATATYPE_OFF]) {
                            HEADER_BLOCK_BASIC -> Utils.log("BASIC")
                            HEADER_BLOCK_ALPHA_ARRAY -> Utils.log("ALPHANUMERIC ARRAY")
                            HEADER_BLOCK_CODE -> Utils.log("CODE")
                            HEADER_BLOCK_NUM_ARRAY -> Utils.log("NUMERIC ARRAY")
                            else -> Utils.log("UNKNOWN")
                        }
                    }

                    DATA_BLOCK_FLAG -> Utils.log(" Data")
                    else -> {
                        // We have to search this block on the original tape,
                        // if that block is broken, we don't update the checksum
                        // and hope it works...
                        Utils.log(" Custom Data")
                        if (originalBlock != null) {
                            brokenData = originalBlock.checksum != originalBlock.calculateChecksum()
                        }
                    }
                }
                if (brokenData) {
                    Utils.log("Original data is broken, checksum NOT updated.")
                }
                if (checksum != calculatedChecksum && !brokenData) {
                    dataBlock[dataBlock.size - 1] = calculatedChecksum
                    Utils.log(
                        "Incorrect block checksum, checksum UPDATED to [" + Utils.intToHexString(
                            calculatedChecksum.toInt() and Constants.MASK_8BIT,
                            Constants.HEXSIZE_8BIT_VALUE
                        ) + "]."
                    )
                }
            }
        }

        private val fileName: String
            get() = String(
                dataBlock.copyOfRange(HEADER_FILE_NAME_OFF, HEADER_FILE_NAME_OFF + HEADER_FILE_NAME_LEN)
            )

        companion object {
            private const val DATA_BLOCK_FLAG = 0xFF.toByte()
            private const val HEADER_BLOCK_FLAG: Byte = 0
            private const val MIN_BLOCK_LENGTH = 4
            private const val HEADER_FILE_NAME_LEN = 10
            private const val HEADER_FILE_NAME_OFF = 2
            private const val HEADER_BLOCK_BASIC: Byte = 0
            private const val HEADER_BLOCK_NUM_ARRAY: Byte = 1
            private const val HEADER_BLOCK_ALPHA_ARRAY: Byte = 2
            private const val HEADER_BLOCK_CODE: Byte = 3
            private const val FLAG_OFF = 0
            private const val DATATYPE_OFF = 1
        }
    }
}
