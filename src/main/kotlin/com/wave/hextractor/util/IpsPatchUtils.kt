package com.wave.hextractor.util

import com.wave.hextractor.pojo.IpsPatchEntry
import com.wave.hextractor.util.FileUtils.outputFileDigests
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*

/**
 * The Class Patcher.
 */
object IpsPatchUtils {
    /**
     * Creates an IPS patch file patchFile from the differences between
     * originalFile to modifiedFile.
     *
     * @param originalFile original file, unmodified.
     * @param modifiedFile file modified to create patch to.
     * @param patchFile file name of the patch to create.
     * @throws IOException I/O error.
     */
    @Throws(IOException::class)
    fun createIpsPatch(originalFile: String, modifiedFile: String, patchFile: String): Boolean {
        Utils.log(
            "Creating ips patch for \"" + modifiedFile + "\"\n based on \"" + originalFile + "\" " +
                "\n to ips file \"" + patchFile + "\""
        )
        val originalFileBytes = Files.readAllBytes(Paths.get(originalFile))
        val modifiedFileBytes = Files.readAllBytes(Paths.get(modifiedFile))
        val patchEntries: MutableList<IpsPatchEntry> = ArrayList()
        var offsetStart = -1
        byteZeroPatch(originalFileBytes, modifiedFileBytes, patchEntries)
        for (i in 1 until originalFileBytes.size) {
            if (originalFileBytes[i] != modifiedFileBytes[i]) {
                if (i - offsetStart == Constants.IPS_PATCH_MAX_SIZE) {
                    patchEntries.add(createIpsEntry(modifiedFileBytes, offsetStart, i))
                    // Create entry and restart
                    offsetStart = i
                }
                if (offsetStart < 0) {
                    offsetStart = i
                }
            } else {
                // Look into the future, if there are less than 6 different
                // bytes, we continue
                if (offsetStart >= 0 && newEntryRequired(originalFileBytes, modifiedFileBytes, i)) {
                    patchEntries.add(createIpsEntry(modifiedFileBytes, offsetStart, i))
                    offsetStart = -1
                }
            }
        }
        if (offsetStart > 0) {
            patchEntries.add(createIpsEntry(modifiedFileBytes, offsetStart, originalFileBytes.size))
        }
        finalPatch(modifiedFileBytes, originalFileBytes, patchEntries)
        Files.write(Paths.get(patchFile), generatePatchFile(patchEntries))
        val valid = validateIpsPatch(originalFile, modifiedFile, patchFile)
        outputFileDigests(originalFile)
        return valid
    }

    private fun byteZeroPatch(
        originalFileBytes: ByteArray,
        modifiedFileBytes: ByteArray,
        patchEntries: MutableList<IpsPatchEntry>
    ) {
        if (originalFileBytes[0] != modifiedFileBytes[0]) {
            patchEntries.add(createIpsEntry(modifiedFileBytes, 0, 1))
        }
    }

    private fun finalPatch(
        modifiedFileBytes: ByteArray,
        originalFileBytes: ByteArray,
        patchEntries: MutableList<IpsPatchEntry>
    ) {
        if (modifiedFileBytes.size > originalFileBytes.size) {
            // Final patch, rom extended
            var i = originalFileBytes.size
            while (i < modifiedFileBytes.size) {
                var end = i + Constants.IPS_PATCH_MAX_SIZE / 64
                if (end > modifiedFileBytes.size) {
                    end = modifiedFileBytes.size
                }
                patchEntries.add(createIpsEntry(modifiedFileBytes, i, end))
                i += Constants.IPS_PATCH_MAX_SIZE / 64
            }
        }
    }

    /**
     * Returns true if a new entry is required.
     *
     * @param bytesOne the bytes one
     * @param bytesTwo the bytes two
     * @param offSet the off set
     * @return true, if successful
     */
    private fun newEntryRequired(bytesOne: ByteArray, bytesTwo: ByteArray, offSet: Int): Boolean {
        var required = true
        var i = 0
        while (i < IpsPatchEntry.IPS_CHUNK_MIN_SIZE && required) {
            if (offSet + i >= bytesOne.size) {
                required = false
            } else {
                if (bytesOne[i + offSet] != bytesTwo[i + offSet]) {
                    required = false
                }
            }
            i++
        }
        return required
    }

    /**
     * Patches originalFile with patchFile and writes it to modifiedFile.
     *
     * @param originalFile file to patch.
     * @param modifiedFile file patched to output to.
     * @param patchFile patch file.
     * @throws IOException I/O error.
     */
    @Throws(IOException::class)
    fun applyIpsPatch(originalFile: String, modifiedFile: String, patchFile: String) {
        Utils.log(
            (
                "Applying ips patch \"" + patchFile + "\"\n on file \"" + originalFile + "\" " +
                    "\n to output file  \"" + modifiedFile + "\""
                )
        )
        Files.write(Paths.get(modifiedFile), applyIpsPatch(originalFile, getPatchEntries(patchFile)))
    }

    /**
     * Patches on memory a file and compares it to the already patched file.
     *
     * @param originalFile file to patch.
     * @param modifiedFile file patched to compare to.
     * @param patchFile patch file.
     * @throws IOException I/O error.
     */
    @Throws(IOException::class)
    fun validateIpsPatch(originalFile: String, modifiedFile: String, patchFile: String): Boolean {
        val valid: Boolean
        Utils.log("Verifying ips patch \"$patchFile\"\n on file \"$originalFile\" \n to file \"$modifiedFile\"")
        if (validateIpsPatch(
                Files.readAllBytes(Paths.get(originalFile)),
                Files.readAllBytes(Paths.get(modifiedFile)),
                Files.readAllBytes(Paths.get(patchFile))
            )
        ) {
            valid = true
            Utils.log("IPS patch correct!")
        } else {
            valid = false
            Utils.log("IPS patch NOT CORRECT!")
        }
        return valid
    }

    private fun validateIpsPatch(originalFile: ByteArray, modifiedFile: ByteArray?, patchFile: ByteArray) =
        Arrays.equals(modifiedFile, applyIpsPatch(originalFile, getPatchEntries(patchFile)))

    private fun createIpsEntry(modifiedFileBytes: ByteArray, offsetStart: Int, offsetEnd: Int): IpsPatchEntry {
        val entryData = modifiedFileBytes.copyOfRange(offsetStart, offsetEnd)
        return if (Utils.allSameValue(entryData) && entryData.size > IpsPatchEntry.IPS_RLE_DATA_SIZE) {
            // RLE encoded entry
            IpsPatchEntry(
                offsetStart,
                (0.toShort()).toInt(),
                ((offsetEnd - offsetStart).toShort()).toInt(),
                byteArrayOf(
                    entryData[0]
                )
            )
        } else {
            IpsPatchEntry(offsetStart, ((offsetEnd - offsetStart).toShort()).toInt(), entryData)
        }
    }

    private fun generatePatchFile(patchEntries: List<IpsPatchEntry>): ByteArray {
        var patchFileSize = Constants.IPS_HEADER.length + Constants.IPS_EOF.length
        for (entry: IpsPatchEntry in patchEntries) {
            patchFileSize += entry.binSize
        }
        val fileBytes = ByteArray(patchFileSize)
        // Copy header
        System.arraycopy(Constants.IPS_HEADER.toByteArray(), 0, fileBytes, 0, Constants.IPS_HEADER.length)
        var offset = Constants.IPS_HEADER.length
        for (entry: IpsPatchEntry in patchEntries) {
            val binEntryData = entry.toBin()
            System.arraycopy(binEntryData, 0, fileBytes, offset, binEntryData.size)
            offset += binEntryData.size
        }
        // Copy end of file
        System.arraycopy(Constants.IPS_EOF.toByteArray(), 0, fileBytes, offset, Constants.IPS_EOF.length)
        return fileBytes
    }

    @Throws(IOException::class)
    private fun applyIpsPatch(originalFile: String, patchEntries: List<IpsPatchEntry>): ByteArray {
        return applyIpsPatch(Files.readAllBytes(Paths.get(originalFile)), patchEntries)
    }

    private fun applyIpsPatch(originalFileBytes: ByteArray, patchEntries: List<IpsPatchEntry>): ByteArray {
        var outputFileSize = originalFileBytes.size
        val maxEntryMod = getMaxEntryOffset(patchEntries)
        if (maxEntryMod > outputFileSize) {
            outputFileSize = maxEntryMod
        }
        val modifiedFileBytes = ByteArray(outputFileSize)
        System.arraycopy(originalFileBytes, 0, modifiedFileBytes, 0, originalFileBytes.size)
        for (entry: IpsPatchEntry in patchEntries) {
            Utils.log("Applying Patch: $entry")
            if (entry.size == IpsPatchEntry.IPS_RLE_MODE) {
                Arrays.fill(
                    modifiedFileBytes,
                    entry.offset,
                    entry.offset + entry.rleSize,
                    entry.data[0]
                )
            } else {
                System.arraycopy(entry.data, 0, modifiedFileBytes, entry.offset, entry.size)
            }
        }
        return modifiedFileBytes
    }

    private fun getMaxEntryOffset(patchEntries: List<IpsPatchEntry>): Int {
        var maxEntryOffset = 0
        for (entry: IpsPatchEntry in patchEntries) {
            if (entry.size == IpsPatchEntry.IPS_RLE_MODE) {
                if (entry.offset + entry.rleSize > maxEntryOffset) {
                    maxEntryOffset = entry.offset + entry.rleSize
                }
            } else {
                if (entry.offset + entry.size > maxEntryOffset) {
                    maxEntryOffset = entry.offset + entry.size
                }
            }
        }
        return maxEntryOffset
    }

    @Throws(IOException::class)
    private fun getPatchEntries(patchFile: String): List<IpsPatchEntry> {
        return getPatchEntries(Files.readAllBytes(Paths.get(patchFile)))
    }

    private fun getPatchEntries(patchBytes: ByteArray): List<IpsPatchEntry> {
        val patchEntries: MutableList<IpsPatchEntry> = ArrayList()
        var i = Constants.IPS_HEADER.length
        while (i < patchBytes.size - Constants.IPS_EOF.length) {
            val entry = IpsPatchEntry()
            entry.offset = Utils.bytesToInt(
                patchBytes[i],
                patchBytes[i + 1],
                patchBytes[i + 2]
            )
            i += IpsPatchEntry.IPS_OFFSET_SIZE
            entry.size = Utils.bytesToInt(patchBytes[i], patchBytes[i + 1])
            i += IpsPatchEntry.IPS_DATA_SIZE
            if (entry.size == IpsPatchEntry.IPS_RLE_MODE) {
                entry.rleSize = Utils.bytesToInt(patchBytes[i], patchBytes[i + 1])
                i += IpsPatchEntry.IPS_RLE_DATA_SIZE
                entry.data = byteArrayOf(patchBytes[i])
            } else {
                entry.data = patchBytes.copyOfRange(i, i + entry.size)
            }
            i += entry.data.size
            patchEntries.add(entry)
        }
        return patchEntries
    }
}
