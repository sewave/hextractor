package com.wave.hextractor.util

import com.wave.hextractor.helper.HexTable
import com.wave.hextractor.util.FileUtils.allFilesExist
import com.wave.hextractor.util.FileUtils.checkLineLength
import com.wave.hextractor.util.FileUtils.cleanAsciiFile
import com.wave.hextractor.util.FileUtils.extractAscii3To4Data
import com.wave.hextractor.util.FileUtils.fillGameData
import com.wave.hextractor.util.FileUtils.getAsciiFile
import com.wave.hextractor.util.FileUtils.getFileExtension
import com.wave.hextractor.util.FileUtils.getFilePath
import com.wave.hextractor.util.FileUtils.getFileWithDigests
import com.wave.hextractor.util.FileUtils.getGameName
import com.wave.hextractor.util.FileUtils.getGameSystem
import com.wave.hextractor.util.FileUtils.insertAsciiAsHex
import com.wave.hextractor.util.FileUtils.insertHex4To3Data
import com.wave.hextractor.util.FileUtils.insertHexData
import com.wave.hextractor.util.FileUtils.outputFileDigests
import com.wave.hextractor.util.FileUtils.replaceFileData
import com.wave.hextractor.util.FileUtils.separateCharLength
import com.wave.hextractor.util.FileUtils.writeFileAscii
import org.junit.Assert
import org.junit.Test
import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.util.*

class FileUtilsTest {
    @Test
    fun getGameName() {
        Assert.assertEquals(
            "Crash 'n the Boys - Street Challenge", getGameName("Crash 'n the Boys - Street Challenge (U) [!].nes")
        )
        Assert.assertEquals("Fantasy Zone", getGameName("Fantasy Zone (J).nes"))
        Assert.assertEquals(
            "The King of Kings", getGameName("King of Kings, The (Wisdom Tree) (V5.0 CHR 1.3) [!].nes")
        )
        Assert.assertEquals(
            "The Adventures of Tintin - Prisoners of the .Sun",
            getGameName("Adventures of Tintin, The - Prisoners of the .Sun (E).smc")
        )
        Assert.assertEquals(
            "The Blues Brothers - Jukebox Adventure", getGameName("Blues Brothers, The - Jukebox Adventure (U).gb")
        )
        Assert.assertEquals(
            "The Blues Brothers . Jukebox Adventure", getGameName("Blues Brothers, The . Jukebox Adventure (U).gb")
        )
    }

    @Test
    fun fileExtension() {
        Assert.assertEquals("nes", getFileExtension("Crash 'n the Boys - Street Challenge (U) [!].nes"))
        Assert.assertEquals("bin", getFileExtension("Spot Goes to Hollywood (U) (REV01) .[!].bin"))
        Assert.assertEquals("smc", getFileExtension("Adventures of Tintin, The - Prisoners of the Sun (E).smc"))
        Assert.assertEquals(
            "smc", getFileExtension(File("Adventures of Tintin, The - Prisoners of the Sun (E).smc"))
        )
    }

    @Test
    @Throws(IOException::class)
    fun fillGameData() {
        val file = File.createTempFile("test", "fillGameData")
        file.deleteOnExit()
        val readme = File(Objects.requireNonNull(javaClass.classLoader.getResource("files/readme.txt")).file)
        val testFile = File(Objects.requireNonNull(javaClass.classLoader.getResource("files/test.bin")).file)
        fillGameData(readme.absolutePath, file.absolutePath, testFile.absolutePath)
        Assert.assertTrue(getAsciiFile(file.absolutePath).isNotEmpty())
    }

    @Throws(IOException::class)
    @Test
    fun fileWithDigests() {
        val testFile = File(Objects.requireNonNull(javaClass.classLoader.getResource("files/test.bin")).file)
        val (name, bytes, md5, sha1, crc32) = getFileWithDigests(testFile.absolutePath)
        Assert.assertEquals("test.bin", name)
        Assert.assertEquals("30a4e38230885e27d1bb3fd0713dfa7d", md5)
        Assert.assertEquals("4e174bbc3e0a536aa8899d1f459318f797dc325a", sha1)
        Assert.assertEquals("43549d77", crc32)
        Assert.assertEquals(11, bytes.size.toLong())
    }

    @Test
    fun gameSystem() {
        Assert.assertEquals(Constants.SYSTEM_GB, getGameSystem("Blues Brothers, The - Jukebox Adventure (U).gb"))
        Assert.assertEquals(Constants.SYSTEM_NES, getGameSystem("Crash 'n the Boys - Street Challenge (U) [!].nes"))
        Assert.assertEquals(
            Constants.SYSTEM_SFC, getGameSystem("Adventures of Tintin, The - Prisoners of the Sun (E).smc")
        )
        Assert.assertEquals(
            Constants.SYSTEM_SFC, getGameSystem("Adventures of Tintin, The - Prisoners of the Sun (E).sfc")
        )
        Assert.assertEquals(Constants.SYSTEM_SMD, getGameSystem("Spot Goes to Hollywood (U) (REV01) [!].bin"))
        Assert.assertEquals(Constants.SYSTEM_SMD, getGameSystem("Spot Goes to Hollywood (U) (REV01) [!].smd"))
        Assert.assertEquals(Constants.SYSTEM_SMD, getGameSystem("Spot Goes to Hollywood (U) (REV01) [!].gen"))
        Assert.assertEquals(Constants.SYSTEM_SMD, getGameSystem("Spot Goes to Hollywood (U) (REV01) [!].md"))
        Assert.assertEquals(Constants.SYSTEM_MSX, getGameSystem("test.mx1"))
        Assert.assertEquals(Constants.SYSTEM_MSX, getGameSystem("test.mx2"))
        Assert.assertEquals(Constants.SYSTEM_MSX, getGameSystem("test.rom"))
        Assert.assertEquals(Constants.SYSTEM_SMS, getGameSystem("test.sms"))
        Assert.assertEquals(Constants.SYSTEM_SMD_32X, getGameSystem("test.32x"))
        Assert.assertEquals(Constants.SYSTEM_GBC, getGameSystem("Blues Brothers, The - Jukebox Adventure (U).gbc"))
        Assert.assertEquals(Constants.SYSTEM_GBA, getGameSystem("Blues Brothers, The - Jukebox Adventure (U).gba"))
        Assert.assertEquals(Constants.SYSTEM_SGG, getGameSystem("test.gg"))
        Assert.assertEquals(Constants.SYSTEM_SG1K, getGameSystem("test.sg"))
        Assert.assertEquals(Constants.SYSTEM_COL, getGameSystem("test.col"))
        Assert.assertEquals(Constants.SYSTEM_PCE, getGameSystem("test.pce"))
        Assert.assertEquals(Constants.SYSTEM_ZXS, getGameSystem("test.tap"))
        Assert.assertEquals(Constants.SYSTEM_ZXS, getGameSystem("test.tzx"))
        Assert.assertEquals(Constants.SYSTEM_CPC, getGameSystem("test.cdt"))
        Assert.assertEquals(Constants.SYSTEM_NGP, getGameSystem("test.ngp"))
        Assert.assertEquals(Constants.SYSTEM_NGPC, getGameSystem("test.ngc"))
        Assert.assertEquals(Constants.SYSTEM_SPV, getGameSystem("watara.sv"))
    }

    @Throws(IOException::class)
    @Test
    fun fileExtension1() {
        val file = File.createTempFile("test", "getFileExtension.tst")
        file.deleteOnExit()
        Assert.assertEquals("tst", getFileExtension(file.absolutePath))
    }

    @Throws(IOException::class)
    @Test
    fun filePath() {
        val file = File.createTempFile("test", "getFilePath.tst")
        file.deleteOnExit()
        Assert.assertNotNull(getFilePath(file))
    }

    @Throws(IOException::class)
    @Test
    fun asciiFile() {
        val ascii = "aaabbbcccddd"
        val file = File.createTempFile("test", "getAsciiFile.tst")
        file.deleteOnExit()
        writeFileAscii(file.absolutePath, ascii)
        Assert.assertEquals(ascii, getAsciiFile(file.absolutePath))
    }

    @Test
    @Throws(IOException::class)
    fun extractAscii3To4Data() {
        val data = byteArrayOf(40, 41, 42, 43, 44, 45)
        val dataFile = File.createTempFile("data", "extractAscii3To4Data.tst")
        dataFile.deleteOnExit()
        Files.write(dataFile.toPath(), data)
        val tableFile = File.createTempFile("table", "extractAscii3To4Data.tbl")
        tableFile.deleteOnExit()
        val table = HexTable(0)
        writeFileAscii(tableFile.absolutePath, table.toAsciiTable())
        val extFile = File.createTempFile("result", "extractAscii3To4Data.ext")
        extFile.deleteOnExit()
        System.setProperty("logLevel", "DEBUG")
        extractAscii3To4Data(tableFile.absolutePath, dataFile.absolutePath, extFile.absolutePath, "0-6-FF")
        val decompStr = """
            ;~0A~~02~$*~0A~20-~00~~00~
            ~0A~~02~$*~0A~20-~00~~00~@00000000:00000006
            """.trimIndent()
        Assert.assertEquals(decompStr, getAsciiFile(extFile.absolutePath))
        System.setProperty("logLevel", "")
    }

    @Test
    @Throws(IOException::class)
    fun insertHex4To3Data() {
        val data = byteArrayOf(0x28, 0xA2.toByte(), 0x8A.toByte())
        val dest = ByteArray(6)
        val dataFile = File.createTempFile("data", "insertHex4To3Data.tst")
        dataFile.deleteOnExit()
        Files.write(dataFile.toPath(), data)
        val tableFile = File.createTempFile("table", "insertHex4To3Data.tbl")
        tableFile.deleteOnExit()
        val table = HexTable(0)
        table.addToTable(0x0A.toByte(), "¿")
        table.addToTable(0x00.toByte(), "·")
        writeFileAscii(tableFile.absolutePath, table.toAsciiTable())
        val extFile = File.createTempFile("result", "insertHex4To3Data.ext")
        extFile.deleteOnExit()
        System.setProperty("logLevel", "DEBUG")
        extractAscii3To4Data(
            tableFile.absolutePath, dataFile.absolutePath, extFile.absolutePath, "0-3-FF"
        )
        val outFile = File.createTempFile("out", "insertHex4To3Data.ext")
        outFile.deleteOnExit()
        Files.write(outFile.toPath(), dest)
        insertHex4To3Data(tableFile.absolutePath, extFile.absolutePath, outFile.absolutePath)
        Assert.assertArrayEquals(data, Arrays.copyOfRange(Files.readAllBytes(outFile.toPath()), 0, data.size))
    }

    @Test
    @Throws(IOException::class)
    fun writeFileAscii() {
        val ascii = "aaabbbcccddd"
        val file = File.createTempFile("test", "getAsciiFile.tst")
        file.deleteOnExit()
        writeFileAscii(file.absolutePath, ascii)
        Assert.assertEquals(ascii, getAsciiFile(file.absolutePath))
    }

    @Test
    @Throws(IOException::class)
    fun insertHexData() {
        val data = byteArrayOf(1, 2, 3, 4)
        val dataOrig = byteArrayOf(0, 0, 0, 0)
        val hexDataFile = File.createTempFile("origin", "insertHexData.tst")
        hexDataFile.deleteOnExit()
        writeFileAscii(hexDataFile.absolutePath, "01 02 03 04@00000000:00000003")
        val hexFile = File.createTempFile("dest", "insertHexData.tst")
        hexFile.deleteOnExit()
        Files.write(hexFile.toPath(), dataOrig)
        insertHexData(hexDataFile.absolutePath, hexFile.absolutePath)
        Assert.assertArrayEquals(data, Files.readAllBytes(hexFile.toPath()))
    }

    @Test
    @Throws(IOException::class)
    fun insertAsciiAsHex() {
        val tableFile = File.createTempFile("table", "insertAsciiAsHex.tbl")
        tableFile.deleteOnExit()
        val table = HexTable(0)
        writeFileAscii(tableFile.absolutePath, table.toAsciiTable())
        val asciiFile = """
            @00000000-00000004-FF
            ;00008BB4{abcd~FF~}#028#022
            abcd~FF~#005
            |5
            """.trimIndent()
        val asciiDataFile = File.createTempFile("origin", "insertAsciiAsHex.tst")
        asciiDataFile.deleteOnExit()
        writeFileAscii(asciiDataFile.absolutePath, asciiFile)
        val hexFile = File.createTempFile("dest", "insertAsciiAsHex.tst")
        hexFile.deleteOnExit()
        val empty = ByteArray(5)
        Files.write(hexFile.toPath(), empty)
        insertAsciiAsHex(tableFile.absolutePath, asciiDataFile.absolutePath, hexFile.absolutePath)
        val data = byteArrayOf(0x61, 0x62, 0x63, 0x64, 0xFF.toByte())
        Assert.assertArrayEquals(data, Files.readAllBytes(hexFile.toPath()))
    }

    @Test
    fun extractAsciiFile() {
    }

    @Test
    fun extractAsciiFile1() {
    }

    @Test
    @Throws(IOException::class)
    fun cleanAsciiFile() {
        val extFile = File.createTempFile("table", ".ext")
        extFile.deleteOnExit()
        val asciiFile = """
            @00000000-00000004-FF
            ;00008BB4{abcd~FF~}#028#022
            abcd~FF~#005
            |5
            ;otro comment
            """.trimIndent()
        writeFileAscii(extFile.absolutePath, asciiFile)
        val extFileClean = File.createTempFile("extFileClean", ".ext")
        extFileClean.deleteOnExit()
        cleanAsciiFile(extFile.absolutePath, extFileClean.absolutePath)
        Assert.assertEquals("abcd\n", String(Files.readAllBytes(extFileClean.toPath())))
    }

    @Test
    fun searchRelative8Bits() {
    }

    @Test
    fun multiSearchRelative8Bits() {
    }

    @Test
    fun multiFindString() {
    }

    @Test
    fun searchAllStrings() {
    }

    @Test
    fun searchAllStrings1() {
    }

    @Test
    fun searchAllStrings2() {
    }

    @Test
    fun cleanExtractedFile() {
    }

    @Test
    fun cleanExtractedFile1() {
    }

    @Test
    fun extractHexData() {
    }

    @Test
    fun cleanOffsets() {}

    @Test
    fun cleanOffsetsString() {}

    @Test
    @Throws(IOException::class)
    fun checkLineLength() {
        val asciiFile = """
            @00000000-00000004-FF
            ;00008BB4{abcd~FF~}#028#022
            abcd~FF~#005
            |5
            """.trimIndent()
        val checkFile = File.createTempFile("origin", "insertAsciiAsHex.tst")
        checkFile.deleteOnExit()
        writeFileAscii(checkFile.absolutePath, asciiFile)
        val ok: Boolean = try {
            checkLineLength(checkFile.absolutePath)
            true
        } catch (e: Exception) {
            false
        }
        Assert.assertTrue(ok)
    }

    @Test
    @Throws(IOException::class)
    fun separateCharLength() {
        val tableFile = File.createTempFile("table", "insertAsciiAsHex.tbl")
        tableFile.deleteOnExit()
        val table = HexTable(0)
        writeFileAscii(tableFile.absolutePath, table.toAsciiTable())
        val asciiFile = "abcd~FF~"
        val asciiDataFile = File.createTempFile("origin", "insertAsciiAsHex.tst")
        asciiDataFile.deleteOnExit()
        writeFileAscii(asciiDataFile.absolutePath, asciiFile)
        val asciiFileOut = File.createTempFile("dest", "insertAsciiAsHex.tst")
        asciiFileOut.deleteOnExit()
        separateCharLength(asciiDataFile.absolutePath, tableFile.absolutePath, asciiFileOut.absolutePath)
        Assert.assertEquals("\na\nbcd~FF~", getAsciiFile(asciiFileOut.absolutePath))
    }

    @Test
    @Throws(IOException::class)
    fun allFilesExist() {
        val files = arrayOf("0", "1")
        Assert.assertFalse(allFilesExist(files))
        val file = File.createTempFile("test", "allFilesExist.tst")
        file.deleteOnExit()
        val file2 = File.createTempFile("test2", "allFilesExist.tst")
        file2.deleteOnExit()
        files[0] = file.absolutePath
        files[1] = file2.absolutePath
        Assert.assertTrue(allFilesExist(files))
    }

    @Test
    @Throws(IOException::class)
    fun replaceFileData() {
        val file = File.createTempFile("test", "replaceFileData.tst")
        file.deleteOnExit()
        writeFileAscii(file.absolutePath, "0000000000")
        val file2 = File.createTempFile("test2", "replaceFileData.tst")
        file2.deleteOnExit()
        writeFileAscii(file2.absolutePath, "11111")
        replaceFileData(file.absolutePath, file2.absolutePath, 2)
        Assert.assertEquals("0011111000", getAsciiFile(file.absolutePath))
    }

    @Test
    @Throws(IOException::class)
    fun outputFileDigests() {
        val file = File.createTempFile("test", "outputFileDigests.tst")
        file.deleteOnExit()
        var ok = true
        try {
            outputFileDigests(file.absolutePath)
        } catch (ex: Exception) {
            ok = false
        }
        Assert.assertTrue(ok)
    }
}