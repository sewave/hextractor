package com.wave.hextractor.util

import java.io.FileOutputStream
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*

/**
 * The Class SMSChecksumUtils.
 */
object SMSChecksumUtils {
    private const val SMS_HEADER_START = "TMR SEGA"
    private const val SMS_HEADER_LOCATION = 0x7FF0
    private const val SMS_HEADER_SIZE = 0x10
    private const val SMS_HEADER_COUNTRY_CHECKSUM_RANGE_OFFSET = 0xF
    private const val SMS_HEADER_CHECKSUM_OFFSET = 0xA
    private const val HIGH_NIBBLE_SHIFTS = 4
    private const val SMS_HEADER_COUNTRY_OVERSEAS = 0x4
    private const val SMS_CHECKSUM_BYTES = 2

    private fun calculatedSMSChecksum(file: ByteArray): Short {
        var checksum: Short = 0
        for (i in 0 until SMS_HEADER_LOCATION) {
            checksum = (checksum + (file[i].toInt() and Constants.MASK_8BIT).toShort()).toShort()
        }
        for (i in SMS_HEADER_LOCATION + SMS_HEADER_SIZE until file.size) {
            checksum = (checksum + (file[i].toInt() and Constants.MASK_8BIT).toShort()).toShort()
        }
        return checksum
    }

    /**
     * Calculate SMS checksum.
     *
     * @param file the file
     * @param updateChecksum the update checksum
     */
    private fun calculateSMSChecksum(file: ByteArray, updateChecksum: Boolean) {
        val isSmsOv = isSMSOverseasRom(file)
        Utils.log("Is SMS overseas ROM? $isSmsOv")
        if (isSmsOv) {
            val checksumRom = getSMSChecksum(file)
            Utils.log(String.format("SMS ROM Checksum: 0x%04X", checksumRom))
            val checksumCalculated = calculatedSMSChecksum(file)
            Utils.log(String.format("SMS SUM Checksum: 0x%04X", checksumCalculated))
            if (checksumRom != checksumCalculated) {
                Utils.log("Different SMS checksums.")
                if (updateChecksum) {
                    Utils.log(String.format("Updating SMS ROM checksum to 0x%04X", checksumCalculated))
                    val checkSumByte = Utils.shortToBytes(checksumCalculated)
                    file[SMS_HEADER_LOCATION + SMS_HEADER_CHECKSUM_OFFSET] =
                        checkSumByte[0]
                    file[SMS_HEADER_LOCATION + SMS_HEADER_CHECKSUM_OFFSET + 1] =
                        checkSumByte[1]
                }
            }
        }
    }

    @Throws(IOException::class)
    private fun calculateSMSChecksum(file: String, updateChecksum: Boolean) {
        val fileBytes = Files.readAllBytes(Paths.get(file))
        calculateSMSChecksum(fileBytes, updateChecksum)
        if (updateChecksum) {
            FileOutputStream(file).use { fos -> fos.write(fileBytes) }
        }
    }

    /**
     * Check update SMS checksum.
     *
     * @param file the file
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @Throws(IOException::class)
    fun checkUpdateSMSChecksum(file: String) {
        calculateSMSChecksum(file, true)
    }

    private fun getSMSChecksum(file: ByteArray): Short {
        return java.lang.Short.reverseBytes(
            ByteBuffer.wrap(getSMSHeader(file), SMS_HEADER_CHECKSUM_OFFSET, SMS_CHECKSUM_BYTES).short
        )
    }

    private fun getSMSHeader(file: ByteArray): ByteArray {
        return file.copyOfRange(SMS_HEADER_LOCATION, SMS_HEADER_LOCATION + SMS_HEADER_SIZE)
    }

    private fun isSMSOverseasRom(file: ByteArray): Boolean {
        val header = getSMSHeader(file)
        return (
            header[SMS_HEADER_COUNTRY_CHECKSUM_RANGE_OFFSET].toInt() shr HIGH_NIBBLE_SHIFTS ==
                SMS_HEADER_COUNTRY_OVERSEAS &&
                String(header).startsWith(SMS_HEADER_START)
            )
    }
}
