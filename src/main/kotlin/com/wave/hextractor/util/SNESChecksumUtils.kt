package com.wave.hextractor.util

import java.io.IOException
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*

/**
 * The Class SNESChecksumUtils.
 */
object SNESChecksumUtils {
    /** The Constant SNES_CHECKSUM_BYTES.  */
    private const val SNES_CHECKSUM_BYTES = 2

    /** The Constant SNES_CHECKSUMNOT_HEADER_OFF.  */
    private const val SNES_CHECKSUMNOT_HEADER_OFF = 44

    /** The Constant SNES_CHECKSUM_HEADER_OFF.  */
    private const val SNES_CHECKSUM_HEADER_OFF = SNES_CHECKSUMNOT_HEADER_OFF + SNES_CHECKSUM_BYTES

    /** The Constant SNES_ROMNAME_HEADER_OFF.  */
    const val SNES_ROMNAME_HEADER_OFF = 16

    /** The Constant SNES_ROMNAME_HEADER_LENGTH.  */
    const val SNES_ROMNAME_HEADER_LENGTH = 21

    /** The Constant SNES_ROMNAME_MAP_MODE_OFF.  */
    const val SNES_ROMNAME_MAP_MODE_OFF = 37

    /** The Constant SNES_ROMSIZE_HEADER_OFF.  */
    const val SNES_ROMSIZE_HEADER_OFF = 39

    /** The Constant SNES_LOROM_HEADER_OFF.  */
    const val SNES_LOROM_HEADER_OFF = 0x7FB0

    /** The Constant SNES_HIROM_OFFSET.  */
    const val SNES_HIROM_OFFSET = 0x8000

    /** The Constant SNES_INITIAL_CHECKSUM.  */
    const val SNES_INITIAL_CHECKSUM = 0

    /** The Constant SNES_INITIAL_CHECKSUMNOT.  */
    const val SNES_INITIAL_CHECKSUMNOT = 0xFFFF

    /** The Constant SNES_HEADER_EMPTY.  */
    const val SNES_HEADER_EMPTY = 0

    /** The Constant SNES_ROM_SIZE_1MBIT.  */
    const val SNES_ROM_SIZE_1MBIT = 131072

    /** The Constant SNES_ROM_SIZE_PAD.  */
    const val SNES_ROM_SIZE_PAD = SNES_ROM_SIZE_1MBIT * 8

    /** The Constant SNES_SMC_HEADER_SIZE.  */
    const val SNES_SMC_HEADER_SIZE = 0x200

    /** The Constant SNES_INT_HEADER_LEN.  */
    const val SNES_INT_HEADER_LEN = 80

    /** The Constant SNES_HIROM_BIT.  */
    const val SNES_HIROM_BIT = 1

    /** The Constant SNES_HEADER_SIZE_CHUNKS.  */
    const val SNES_HEADER_SIZE_CHUNKS = 0x400

    /** The Constant SNES_HEADER_NAME_MIN_CHAR.  */
    const val SNES_HEADER_NAME_MIN_CHAR = 0x1F

    /** The Constant SNES_HEADER_NAME_MAX_CHAR.  */
    const val SNES_HEADER_NAME_MAX_CHAR = 0x7F

    /** The Constant SNES_03_04MBIT.  */
    const val SNES_03_04MBIT = 0x09

    /** The Constant SNES_05_08MBIT.  */
    const val SNES_05_08MBIT = 0x0A

    /** The Constant SNES_09_16MBIT.  */
    const val SNES_09_16MBIT = 0x0B

    /** The Constant SNES_17_32MBIT.  */
    const val SNES_17_32MBIT = 0x0C

    /** The Constant SNES_33_64MBIT.  */
    const val SNES_33_64MBIT = 0x0D

    /** The Constant SNES_03_04MBIT_SIZE.  */
    const val SNES_03_04MBIT_SIZE = 4 * SNES_ROM_SIZE_1MBIT

    /** The Constant SNES_05_08MBIT_SIZE.  */
    const val SNES_05_08MBIT_SIZE = 8 * SNES_ROM_SIZE_1MBIT

    /** The Constant SNES_09_16MBIT_SIZE.  */
    const val SNES_09_16MBIT_SIZE = 16 * SNES_ROM_SIZE_1MBIT

    /** The Constant SNES_17_32MBIT_SIZE.  */
    const val SNES_17_32MBIT_SIZE = 32 * SNES_ROM_SIZE_1MBIT

    /** The Constant SNES_33_64MBIT_SIZE.  */
    const val SNES_33_64MBIT_SIZE = 64 * SNES_ROM_SIZE_1MBIT

    /**
     * Update SNES rom checksum.
     *
     * @param inputFile the input file
     * @throws IOException the exception
     */
    @Throws(IOException::class)
    fun checkUpdateSnesChecksum(inputFile: String) {
        Utils.log("Fixing SNES checksum for \"$inputFile\".")
        val fileBytesRaw = Files.readAllBytes(Paths.get(inputFile))
        val fileBytes = getNoSmcHeaderFixedLengthSnesRom(fileBytesRaw)
        val isHiRom = isSnesRomHiRom(fileBytes)
        val internalHeader = getSnesInternalHeader(fileBytes, isHiRom)
        if (isHiRom) {
            Utils.log("Detected HIROM")
        } else {
            Utils.log("Detected LOROM")
        }
        val origChecksum = getSnesRomChecksum(internalHeader)
        val origChecksumNot = getSnesRomChecksumNot(internalHeader)
        val calcCheck = calculateSnesRomChecksum(fileBytes, isHiRom)
        val calcChecksumNot = calcCheck xor Constants.MASK_16BIT
        Utils.log("ROM checksum           : " + Utils.getHexFilledLeft(origChecksum, Constants.HEXSIZE_16BIT_VALUE))
        Utils.log("ROM checksum not       : " + Utils.getHexFilledLeft(origChecksumNot, Constants.HEXSIZE_16BIT_VALUE))
        Utils.log("Calculated checksum    : " + Utils.getHexFilledLeft(calcCheck, Constants.HEXSIZE_16BIT_VALUE))
        Utils.log("Calculated checksum not: " + Utils.getHexFilledLeft(calcChecksumNot, Constants.HEXSIZE_16BIT_VALUE))
        if (calcCheck != origChecksum || origChecksumNot != calcChecksumNot) {
            if (calcCheck != origChecksum) {
                Utils.log("Updating checksum.")
            }
            if (origChecksumNot != calcChecksumNot) {
                Utils.log("Updating checksumNot.")
            }
            updateSnesChecksum(internalHeader, calcCheck, calcChecksumNot)
            var off = SNES_LOROM_HEADER_OFF
            if (isHiRom) {
                off += SNES_HIROM_OFFSET
            }
            if (fileBytesRaw.size % SNES_ROM_SIZE_1MBIT == SNES_SMC_HEADER_SIZE) {
                off += SNES_SMC_HEADER_SIZE
            }
            System.arraycopy(internalHeader, 0, fileBytesRaw, off, SNES_INT_HEADER_LEN)
            Files.write(Paths.get(inputFile), fileBytesRaw)
        } else {
            Utils.log("Checksum correct, not overwriting it.")
        }
    }
    // ////////// PRIVATE METHODS////////////////
    /**
     * Gets the snes internal header.
     *
     * @param fileBytes the file bytes
     * @param isHiRom the is hi rom
     * @return the snes internal header
     */
    private fun getSnesInternalHeader(fileBytes: ByteArray, isHiRom: Boolean): ByteArray {
        var off = SNES_LOROM_HEADER_OFF
        if (isHiRom) {
            off += SNES_HIROM_OFFSET
        }
        return Arrays.copyOfRange(fileBytes, off, off + SNES_INT_HEADER_LEN)
    }

    /**
     * Removes header and expands rom to internal definition maximum if necessary.
     *
     * @param fileBytes .
     * @return cleanedrom
     */
    private fun getNoSmcHeaderFixedLengthSnesRom(fileBytes: ByteArray): ByteArray {
        val cleanedRom: ByteArray
        val headerSize = fileBytes.size % SNES_ROM_SIZE_1MBIT
        val romSize = fileBytes.size / SNES_ROM_SIZE_1MBIT * SNES_ROM_SIZE_1MBIT
        require(!(headerSize != 0 && headerSize != SNES_SMC_HEADER_SIZE)) { "Invalid header size / Tamaño header inválido: $headerSize" }
        Utils.log("ROM size: " + romSize / SNES_ROM_SIZE_1MBIT + " Mbit")
        if (headerSize > 0) {
            Utils.log("WITH SMC header")
        } else {
            Utils.log("With NO SMC header")
        }
        val cleanedRomSize = getCleanedRomSize(romSize)
        cleanedRom = ByteArray(cleanedRomSize)
        // Copy original rom info
        System.arraycopy(fileBytes, headerSize, cleanedRom, 0, romSize)
        if (romSize != cleanedRomSize) {
            Utils.log("Expanding rom to " + cleanedRomSize / SNES_ROM_SIZE_1MBIT + " Mbit")
            // clone last chunk
            System.arraycopy(
                cleanedRom, romSize - (cleanedRomSize - romSize), cleanedRom, romSize, cleanedRomSize - romSize
            )
        }
        return cleanedRom
    }

    private fun getCleanedRomSize(romSize: Int) = if (romSize > SNES_03_04MBIT_SIZE) {
        if (romSize > SNES_05_08MBIT_SIZE) {
            getCleanedRomSizeLarge(romSize)
        } else {
            SNES_05_08MBIT_SIZE
        }
    } else {
        SNES_03_04MBIT_SIZE
    }

    private fun getCleanedRomSizeLarge(romSize: Int) = if (romSize > SNES_09_16MBIT_SIZE) {
        if (romSize > SNES_17_32MBIT_SIZE) {
            require(romSize <= SNES_33_64MBIT_SIZE) { "Rom size too large!!!!" }
            SNES_33_64MBIT_SIZE
        } else {
            SNES_17_32MBIT_SIZE
        }
    } else {
        SNES_09_16MBIT_SIZE
    }

    /**
     * Update snes checksum.
     *
     * @param header the header
     * @param check the check
     * @param checksumNot the checksum not
     */
    private fun updateSnesChecksum(header: ByteArray, check: Int, checksumNot: Int) {
        val bytesNot = Utils.intToByteArray(checksumNot)
        header[SNES_CHECKSUMNOT_HEADER_OFF] = bytesNot[3]
        header[SNES_CHECKSUMNOT_HEADER_OFF + 1] = bytesNot[2]
        val bytes = Utils.intToByteArray(check)
        header[SNES_CHECKSUM_HEADER_OFF] = bytes[3]
        header[SNES_CHECKSUM_HEADER_OFF + 1] = bytes[2]
    }

    /**
     * Gets the snes rom checksum not.
     *
     * @param header the header
     * @return the snes rom checksum not
     */
    private fun getSnesRomChecksumNot(header: ByteArray): Int {
        return Utils.bytesToInt(
            header[SNES_CHECKSUMNOT_HEADER_OFF + 1], header[SNES_CHECKSUMNOT_HEADER_OFF]
        ) and Constants.MASK_16BIT
    }

    /**
     * Calculate snes rom checksum.
     *
     * @param fileBytes the file bytes
     * @param isHiRom the is hi rom
     * @return the int
     */
    private fun calculateSnesRomChecksum(fileBytes: ByteArray, isHiRom: Boolean): Int {
        val checkRom = Arrays.copyOf(fileBytes, fileBytes.size)
        // Limpiamos checksum y negado
        val header = getSnesInternalHeader(checkRom, isHiRom)
        updateSnesChecksum(header, SNES_INITIAL_CHECKSUM, SNES_INITIAL_CHECKSUMNOT)
        var destPos = SNES_LOROM_HEADER_OFF
        if (isHiRom) {
            destPos += SNES_HIROM_OFFSET
        }
        System.arraycopy(header, 0, checkRom, destPos, header.size)

        // Calcular checksum
        var checksum = 0
        for (b in checkRom) {
            checksum += b.toInt() and Constants.MASK_8BIT
        }
        return checksum and Constants.MASK_16BIT
    }

    /**
     * Checks if is snes rom hi rom.
     *
     * @param fileBytes the file bytes
     * @return true, if is snes rom hi rom
     */
    private fun isSnesRomHiRom(fileBytes: ByteArray): Boolean {
        val isLoRom: Boolean
        var isHiRom = false
        var header = Arrays.copyOfRange(
            fileBytes, SNES_LOROM_HEADER_OFF, SNES_LOROM_HEADER_OFF + SNES_INT_HEADER_LEN
        )
        isLoRom = validateSnesHeader(header, fileBytes.size, false)
        if (!isLoRom) {
            header = Arrays.copyOfRange(
                fileBytes,
                SNES_LOROM_HEADER_OFF + SNES_HIROM_OFFSET,
                SNES_LOROM_HEADER_OFF + SNES_HIROM_OFFSET + SNES_INT_HEADER_LEN
            )
            isHiRom = validateSnesHeader(header, fileBytes.size, true)
        }
        require(!(!isLoRom && !isHiRom)) { "Could not determine ROM type / No puedo determinarse el tipo de ROM" }
        return isHiRom
    }

    /**
     * Validate snes header.
     *
     * @param header the header
     * @param length the length
     * @param isHiRom the is hi rom
     * @return true, if successful
     */
    private fun validateSnesHeader(header: ByteArray, length: Int, isHiRom: Boolean): Boolean {
        var res = false
        if (isValidSnesName(
                Arrays.copyOfRange(
                    header, SNES_ROMNAME_HEADER_OFF, SNES_ROMNAME_HEADER_OFF + SNES_ROMNAME_HEADER_LENGTH
                )
            )
        ) {
            // 2.Verificamos que el map mode marque lo que toca
            val romType = header[SNES_ROMNAME_MAP_MODE_OFF].toInt() and Constants.MASK_8BIT
            if (romType and SNES_HIROM_BIT == 0 && !isHiRom || romType and SNES_HIROM_BIT == SNES_HIROM_BIT && isHiRom) {
                res = true
                // 3.Verificamos que el tamaño especificado sea correcto
                val snesBanks = header[SNES_ROMSIZE_HEADER_OFF].toInt() and Constants.MASK_8BIT
                val snesSize = SNES_HEADER_SIZE_CHUNKS shl snesBanks
                var minSize = 0
                var maxSize = 0
                when (snesBanks) {
                    SNES_03_04MBIT -> {
                        minSize = SNES_03_04MBIT_SIZE - SNES_ROM_SIZE_1MBIT
                        maxSize = SNES_03_04MBIT_SIZE
                    }

                    SNES_05_08MBIT -> {
                        minSize = SNES_03_04MBIT_SIZE + SNES_ROM_SIZE_1MBIT
                        maxSize = SNES_05_08MBIT_SIZE
                    }

                    SNES_09_16MBIT -> {
                        minSize = SNES_05_08MBIT_SIZE + SNES_ROM_SIZE_1MBIT
                        maxSize = SNES_09_16MBIT_SIZE
                    }

                    SNES_17_32MBIT -> {
                        minSize = SNES_09_16MBIT_SIZE + SNES_ROM_SIZE_1MBIT
                        maxSize = SNES_17_32MBIT_SIZE
                    }

                    SNES_33_64MBIT -> {
                        minSize = SNES_17_32MBIT_SIZE + SNES_ROM_SIZE_1MBIT
                        maxSize = SNES_33_64MBIT_SIZE
                    }

                    else -> {
                        Utils.log("Invalid ROM size: $snesSize")
                        res = false
                    }
                }
                logRomResults(length, minSize, maxSize, res)
            }
        }
        return res
    }

    private fun logRomResults(length: Int, minSize: Int, maxSize: Int, res: Boolean) {
        if (length < minSize || length > maxSize) {
            Utils.log(
                "Size mismatch, File (Expanded): " + length / SNES_ROM_SIZE_1MBIT + " MBit, ROM info: " + minSize / SNES_ROM_SIZE_1MBIT + " - " + maxSize / SNES_ROM_SIZE_1MBIT + " MBit"
            )
        }
        if (res) {
            Utils.log(
                "Size correct, File (Expanded): " + length / SNES_ROM_SIZE_1MBIT + " MBit, ROM info: " + minSize / SNES_ROM_SIZE_1MBIT + " - " + maxSize / SNES_ROM_SIZE_1MBIT + " MBit"
            )
        }
    }

    /**
     * Checks if is valid snes name.
     *
     * @param asciiName the ascii name
     * @return true, if is valid snes name
     */
    private fun isValidSnesName(asciiName: ByteArray): Boolean {
        // Verificamos que su nombre cumple que usa bytes van de (1F-7F?)
        var res = true
        for (b in asciiName) {
            val num = b.toInt() and Constants.MASK_8BIT
            if (num <= SNES_HEADER_NAME_MIN_CHAR /* || num >= Constants.SNES_HEADER_NAME_MAX_CHAR */) {
                res = false
                break
            }
        }
        if (res) {
            Utils.log("ROM NAME: \"" + String(asciiName, StandardCharsets.US_ASCII) + "\"")
        }
        return res
    }

    /**
     * Gets the snes rom checksum.
     *
     * @param header the header
     * @return the snes rom checksum
     */
    private fun getSnesRomChecksum(header: ByteArray): Int {
        return Utils.bytesToInt(
            header[SNES_CHECKSUM_HEADER_OFF + 1], header[SNES_CHECKSUM_HEADER_OFF]
        ) and Constants.MASK_16BIT
    }
}
