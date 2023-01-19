package com.wave.hextractor.util

import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*

/**
 * The Class SMDChecksumUtils.
 */
object SMDChecksumUtils {
    private const val MEGA_DRIVE_CHECKSUM_LOCATION = 0x18E
    private const val MEGA_DRIVE_CHECKSUM_START_CALCULATION = 0x200

    /**
     * Fixes the mega drive rom checksum.
     *
     * @param inputFile mega drive rom path.
     * @throws IOException the exception
     */
    @Throws(IOException::class)
    fun checkUpdateMegaDriveChecksum(inputFile: String) {
        val fileBytes = Files.readAllBytes(Paths.get(inputFile))
        Utils.log("Fixing Mega Drive checksum for \"$inputFile\".")
        if (checkUpdateMegaDriveChecksum(fileBytes)) {
            Files.write(Paths.get(inputFile), fileBytes)
        }
    }

    /**
     * Fixes the mega drive rom checksum.
     *
     * @param fileBytes rom bytes.
     * @return true if bytes were modified, false otherwise.
     */
    fun checkUpdateMegaDriveChecksum(fileBytes: ByteArray): Boolean {
        val checksum = getMegaDriveChecksum(fileBytes)
        Utils.log("Original checksum: 0x" + Integer.toHexString(checksum).uppercase(Locale.getDefault()))
        val calculatedChecksum = calculateMegaDriveChecksum(fileBytes)
        Utils.log("Calculated checksum: 0x" + Integer.toHexString(calculatedChecksum).uppercase(Locale.getDefault()))
        var res = false
        if (calculatedChecksum != checksum) {
            res = true
            Utils.log("Updating calculated checksum.")
            updateMegaDriveChecksum(calculatedChecksum, fileBytes)
        } else {
            Utils.log("Checksum correct, not overwriting it.")
        }
        return res
    }

    /**
     * Gets the mega drive checksum.
     *
     * @param fileBytes the file bytes
     * @return the mega drive checksum
     */
    fun getMegaDriveChecksum(fileBytes: ByteArray): Int {
        return Utils.bytesToInt(
            fileBytes[MEGA_DRIVE_CHECKSUM_LOCATION],
            fileBytes[MEGA_DRIVE_CHECKSUM_LOCATION + 1]
        ) and Constants.MASK_16BIT
    }

    /**
     * Calculate mega drive checksum.
     *
     * @param fileBytes the file bytes
     * @return the int
     */
    fun calculateMegaDriveChecksum(fileBytes: ByteArray): Int {
        var calculatedChecksum = 0
        var i = MEGA_DRIVE_CHECKSUM_START_CALCULATION
        while (i < fileBytes.size) {
            calculatedChecksum += Utils.bytesToInt(fileBytes[i], fileBytes[i + 1])
            calculatedChecksum = calculatedChecksum and Constants.MASK_16BIT
            i += 2
        }
        return calculatedChecksum
    }

    /**
     * Update mega drive checksum.
     *
     * @param calculatedChecksum the calculated checksum
     * @param fileBytes the file bytes
     */
    fun updateMegaDriveChecksum(calculatedChecksum: Int, fileBytes: ByteArray) {
        val bytes = Utils.intToByteArray(calculatedChecksum)
        fileBytes[MEGA_DRIVE_CHECKSUM_LOCATION] = bytes[2]
        fileBytes[MEGA_DRIVE_CHECKSUM_LOCATION + 1] = bytes[3]
    }
}
