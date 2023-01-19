package com.wave.hextractor.util

import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*

/**
 * The Class GBChecksumUtils.
 */
object GBChecksumUtils {
    private const val GAMEBOY_HEADER_CHECKSUM_LOCATION = 0x14D
    private const val GAMEBOY_HEADER_CHECKSUM_START_CALCULATION = 0x134
    private const val GAMEBOY_HEADER_CHECKSUM_END_CALCULATION = 0x14C
    private const val GAMEBOY_ROM_CHECKSUM_LOCATION = 0x14E
    private const val GAMEBOY_ROM_CHECKSUM_START_CALCULATION = 0x000

    /**
     * Calculate game boy header checksum.
     *
     * @param fileBytes the file bytes
     * @return the int
     */
    fun calculateGameBoyHeaderChecksum(fileBytes: ByteArray): Int {
        var calculatedHeaderChecksum: Byte = 0
        for (i in GAMEBOY_HEADER_CHECKSUM_START_CALCULATION..GAMEBOY_HEADER_CHECKSUM_END_CALCULATION) {
            calculatedHeaderChecksum = (calculatedHeaderChecksum - fileBytes[i]).toByte()
            calculatedHeaderChecksum = (calculatedHeaderChecksum - 1).toByte()
        }
        return calculatedHeaderChecksum.toInt() and Constants.MASK_8BIT
    }

    /**
     * Calculate game boy rom checksum.
     *
     * @param fileBytes the file bytes
     * @return the int
     */
    fun calculateGameBoyRomChecksum(fileBytes: ByteArray): Int {
        var calculatedRomChecksum = 0
        for (i in GAMEBOY_ROM_CHECKSUM_START_CALCULATION until fileBytes.size) {
            // Ignore checksum bytes to calculate checksum
            if (i < GAMEBOY_ROM_CHECKSUM_LOCATION || i > GAMEBOY_ROM_CHECKSUM_LOCATION + 1) {
                calculatedRomChecksum += fileBytes[i].toInt() and Constants.MASK_8BIT
                calculatedRomChecksum = calculatedRomChecksum and Constants.MASK_16BIT
            }
        }
        return calculatedRomChecksum
    }

    /**
     * Fixes the header checksum and the rom checksum of the game boy file
     * (if needed).
     *
     * @param inputFile file to update.
     * @throws IOException the exception
     */
    @Throws(IOException::class)
    fun checkUpdateGameBoyChecksum(inputFile: String) {
        val fileBytes = Files.readAllBytes(Paths.get(inputFile))
        Utils.log("Fixing Game Boy checksum for \"$inputFile\".")
        var checksumModified = false
        // HEADER CHECKSUM
        val headerChecksum = getGameBoyHeaderChecksum(fileBytes)
        Utils.log(
            "Original Header checksum: 0x" +
                Integer.toHexString(headerChecksum and Constants.MASK_8BIT).uppercase(Locale.getDefault())
        )
        val calculatedHeaderChecksum = calculateGameBoyHeaderChecksum(fileBytes)
        Utils.log(
            (
                "Calculated Header checksum: 0x" +
                    Integer.toHexString(calculatedHeaderChecksum and Constants.MASK_8BIT)
                        .uppercase(Locale.getDefault())
                )
        )
        if ((calculatedHeaderChecksum and Constants.MASK_8BIT) != (headerChecksum and Constants.MASK_8BIT)) {
            Utils.log("Updating calculated rom header checksum.")
            updateGameBoyHeaderChecksum(calculatedHeaderChecksum, fileBytes)
            checksumModified = true
        } else {
            Utils.log("Rom header Checksum correct, not overwriting it.")
        }

        // ROM CHECKSUM
        val romChecksum = getGameBoyRomChecksum(fileBytes)
        Utils.log("Original ROM checksum: 0x" + Integer.toHexString(romChecksum).uppercase(Locale.getDefault()))
        val calculatedRomChecksum = calculateGameBoyRomChecksum(fileBytes)
        Utils.log(
            "Calculated ROM checksum: 0x" + Integer.toHexString(calculatedRomChecksum).uppercase(Locale.getDefault())
        )
        if (calculatedRomChecksum != romChecksum) {
            Utils.log("Updating calculated rom checksum.")
            updateGameBoyRomChecksum(calculatedRomChecksum, fileBytes)
            checksumModified = true
        } else {
            Utils.log("Rom Checksum correct, not overwriting it.")
        }
        if (checksumModified) {
            Utils.log("Writing file.")
            Files.write(Paths.get(inputFile), fileBytes)
        }
    }

    /**
     * Gets the game boy header checksum.
     *
     * @param fileBytes the file bytes
     * @return the game boy header checksum
     */
    fun getGameBoyHeaderChecksum(fileBytes: ByteArray): Int {
        return fileBytes[GAMEBOY_HEADER_CHECKSUM_LOCATION].toInt() and Constants.MASK_8BIT
    }

    /**
     * Gets the game boy rom checksum.
     *
     * @param fileBytes the file bytes
     * @return the game boy rom checksum
     */
    fun getGameBoyRomChecksum(fileBytes: ByteArray): Int {
        return Utils.bytesToInt(
            fileBytes[GAMEBOY_ROM_CHECKSUM_LOCATION],
            fileBytes[GAMEBOY_ROM_CHECKSUM_LOCATION + 1]
        ) and Constants.MASK_16BIT
    }

    /**
     * Update game boy header checksum.
     *
     * @param calculatedHeaderChecksum the calculated header checksum
     * @param fileBytes the file bytes
     */
    fun updateGameBoyHeaderChecksum(calculatedHeaderChecksum: Int, fileBytes: ByteArray) {
        val bytes = Utils.intToByteArray(calculatedHeaderChecksum)
        fileBytes[GAMEBOY_HEADER_CHECKSUM_LOCATION] = bytes[3]
    }

    /**
     * Update game boy rom checksum.
     *
     * @param calculatedRomChecksum the calculated rom checksum
     * @param fileBytes the file bytes
     */
    fun updateGameBoyRomChecksum(calculatedRomChecksum: Int, fileBytes: ByteArray) {
        val bytes = Utils.intToByteArray(calculatedRomChecksum)
        fileBytes[GAMEBOY_ROM_CHECKSUM_LOCATION + 1] = bytes[3]
        fileBytes[GAMEBOY_ROM_CHECKSUM_LOCATION] = bytes[2]
    }
}
