package com.wave.hextractor.util

import com.wave.hextractor.helper.HexTable
import com.wave.hextractor.pojo.OffsetEntry
import com.wave.hextractor.util.FileUtils.getAsciiFile
import com.wave.hextractor.util.FileUtils.writeFileAscii
import com.wave.hextractor.util.Utils.allSameValue
import com.wave.hextractor.util.Utils.bytesToHex
import com.wave.hextractor.util.Utils.bytesToInt
import com.wave.hextractor.util.Utils.checkLineLength
import com.wave.hextractor.util.Utils.copyFileUsingStream
import com.wave.hextractor.util.Utils.createFile
import com.wave.hextractor.util.Utils.debug
import com.wave.hextractor.util.Utils.extractDictionary
import com.wave.hextractor.util.Utils.fillLeft
import com.wave.hextractor.util.Utils.getCleanedString
import com.wave.hextractor.util.Utils.getCompressed4To3Data
import com.wave.hextractor.util.Utils.getExpanded3To4Data
import com.wave.hextractor.util.Utils.getHexArea
import com.wave.hextractor.util.Utils.getHexAreaFixedWidth
import com.wave.hextractor.util.Utils.getHexFilledLeft
import com.wave.hextractor.util.Utils.getHexOffsets
import com.wave.hextractor.util.Utils.getJoinedFileName
import com.wave.hextractor.util.Utils.getLinesCleaned
import com.wave.hextractor.util.Utils.getOffsets
import com.wave.hextractor.util.Utils.getTextArea
import com.wave.hextractor.util.Utils.hexStringCharToByte
import com.wave.hextractor.util.Utils.hexStringListToIntList
import com.wave.hextractor.util.Utils.hexStringToByteArray
import com.wave.hextractor.util.Utils.intToByteArray
import com.wave.hextractor.util.Utils.intToHexString
import com.wave.hextractor.util.Utils.isValidFileName
import com.wave.hextractor.util.Utils.loadHex
import com.wave.hextractor.util.Utils.log
import com.wave.hextractor.util.Utils.logException
import com.wave.hextractor.util.Utils.logNoNL
import com.wave.hextractor.util.Utils.removeCommentsAndJoin
import com.wave.hextractor.util.Utils.shortToBytes
import com.wave.hextractor.util.Utils.sortByValue
import com.wave.hextractor.util.Utils.stringHasWords
import com.wave.hextractor.util.Utils.toFileString
import com.wave.hextractor.util.Utils.toHexString
import junit.framework.TestCase
import org.junit.Assert
import org.junit.Test
import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.util.*

class UtilsTest {
    @Test
    fun isValidFileName() {
        Assert.assertTrue(isValidFileName("abc.def"))
        Assert.assertFalse(isValidFileName("a<bc.def"))
    }

    @Test
    @Throws(IOException::class)
    fun copyFileUsingStream() {
        val file = File.createTempFile("test", "copyFileUsingStream")
        file.deleteOnExit()
        val file2 = File.createTempFile("test", "copyFileUsingStream2")
        file2.deleteOnExit()
        val testStr = UUID.randomUUID().toString()
        writeFileAscii(file.absolutePath, testStr)
        copyFileUsingStream(file.absolutePath, file2.absolutePath)
        TestCase.assertEquals(testStr, getAsciiFile(file2.absolutePath))
    }

    @Test
    fun intToByteArray() {
        val res = byteArrayOf(7, 91, -51, 21)
        Assert.assertArrayEquals(res, intToByteArray(123456789))
    }

    @Test
    @Throws(IOException::class)
    fun createFile() {
        val file = File.createTempFile("test", "createFile")
        file.deleteOnExit()
        val content = UUID.randomUUID().toString()
        createFile(file.absolutePath, content)
        TestCase.assertEquals(content, getAsciiFile(file.absolutePath))
    }

    @Throws(IOException::class)
    @Test
    fun joinedFileName() {
        val path = Files.createTempDirectory("getJoinedFileName").toFile()
        path.deleteOnExit()
        TestCase.assertEquals(
            path.absolutePath + Constants.FILE_SEPARATOR + "aaa", getJoinedFileName(path, "aaa")
        )
    }

    @Test
    fun fillLeft() {
        TestCase.assertEquals("000aaa", fillLeft("aaa", 6))
    }

    @Test
    fun isDebug() {
        System.setProperty("logLevel", "DEBUG")
        Assert.assertTrue(debug)
        System.setProperty("logLevel", "WARN")
        Assert.assertFalse(debug)
    }

    @Test
    fun hexFilledLeft() {
        Assert.assertEquals("0000", getHexFilledLeft(0, 4))
    }

    @Test
    fun toHexString() {
        val data = byteArrayOf(0, 1, 2, 3, 4)
        Assert.assertEquals("[00 01 02 03 04 ]", toHexString(data))
    }

    @Test
    fun hexStringToByteArray() {
        val data = byteArrayOf(0, 1, 2, 3, 4)
        Assert.assertArrayEquals(data, hexStringToByteArray("0001020304"))
    }

    @Test
    fun hexStringCharToByte() {
        Assert.assertEquals(0.toByte().toLong(), hexStringCharToByte("00").toLong())
    }

    @Test
    fun bytesToInt() {
        val bytes16 = intToByteArray(258)
        TestCase.assertEquals(258, bytesToInt(bytes16[2], bytes16[3]))
    }

    @Test
    fun bytesToInt1() {
        val bytes24 = intToByteArray(131071)
        TestCase.assertEquals(0, bytesToInt(0.toByte(), 0.toByte(), 0.toByte()))
        TestCase.assertEquals(131071, bytesToInt(bytes24[1], bytes24[2], bytes24[3]))
    }

    @Test
    fun bytesToInt2() {
        val bytes32 = intToByteArray(33554431)
        TestCase.assertEquals(0, bytesToInt(0.toByte(), 0.toByte(), 0.toByte(), 0.toByte()))
        TestCase.assertEquals(
            33554431, bytesToInt(
                bytes32[0], bytes32[1], bytes32[2], bytes32[3]
            )
        )
    }

    @Test
    fun loadHex() {
        val input = """
            ;Traducciones Wave 2019
            00 01 02 03@00000000:00000003
            """.trimIndent()
        val data = ByteArray(4)
        loadHex(input, data)
        val dataEnd = byteArrayOf(0, 1, 2, 3)
        Assert.assertArrayEquals(dataEnd, data)
    }

    @Throws(IOException::class)
    @Test
    fun offsets() {
        val file = File.createTempFile("test", "getOffsets.off")
        file.deleteOnExit()
        val offsets = "00000000-00000003-FF"
        writeFileAscii(file.absolutePath, offsets)
        val entry = OffsetEntry(0, 3, mutableListOf("FF"))
        val resEntries = listOf(entry)
        Assert.assertEquals(resEntries, getOffsets(file.absolutePath))
        Assert.assertEquals(resEntries, getOffsets(offsets))
    }

    @Test
    fun linesCleaned() {
        val lines = arrayOf("aaa~01~", "b~02~bb@", "CC~03~C^", "DDD~04~¨")
        Assert.assertEquals("aaa\nb bb\nCC C^\nDDD ¨\n", getLinesCleaned(lines).toString())
    }

    @Test
    fun stringHasWords() {
        val words: MutableList<String?> = ArrayList()
        Assert.assertFalse(
            stringHasWords(words, "many words")
        )
        words.add("many")
        words.add("words")
        Assert.assertTrue(
            stringHasWords(words, "many words")
        )
    }

    @Test
    fun cleanedString() {
        Assert.assertEquals("abcd x'yz", getCleanedString("´Abcd    X'YZ"))
    }

    @Test
    fun allSameValue() {
        val eq = byteArrayOf(1, 1, 1, 1)
        val neq = byteArrayOf(2, 1, 4, 1)
        Assert.assertTrue(allSameValue(eq))
        Assert.assertFalse(allSameValue(neq))
    }

    @Test
    fun intToHexString() {
        Assert.assertEquals("0001", intToHexString(1, 4))
    }

    @Test
    fun hexStringListToIntList() {
        val hexValues = arrayOf("01", "02", "03")
        val intValues = intArrayOf(1, 2, 3)
        Assert.assertArrayEquals(intValues, hexStringListToIntList(hexValues))
    }

    @Test
    fun hexOffsets() {
        val entries = "0-1,5:6"
        val offEntries: MutableList<OffsetEntry> = ArrayList()
        offEntries.add(OffsetEntry(0, 1, null))
        offEntries.add(OffsetEntry(5, 10, null))
        Assert.assertEquals(offEntries, getHexOffsets(entries))
    }

    @Test
    fun sortByValue() {
        var initMap: Map<String, String> = mapOf(
            "01" to "ZZ", "02" to "AA"
        )
        val valuesInit: List<String?> = sortByValue(initMap).map { it.second }
        val valuesSorted: List<String> = mutableListOf("AA", "ZZ")
        Assert.assertEquals(valuesSorted, valuesInit)
    }

    @Test
    fun toFileString() {
        val offEntries: MutableList<OffsetEntry?> = ArrayList()
        offEntries.add(OffsetEntry(0, 1, mutableListOf("FF", "00")))
        offEntries.add(OffsetEntry(5, 10, mutableListOf("01", "00")))
        Assert.assertEquals("00000000-00000001-FF-00,00000005-0000000A-01-00", toFileString(offEntries))
    }

    @Test
    fun extractDictionary() {
        val lines: MutableList<String> = ArrayList()
        lines.add(";000000DE{text1}#117#105")
        lines.add("texto1#105")
        lines.add(";000000DE{text2}#117#105")
        lines.add("texto2#105")
        val resMap: MutableMap<String, String> = HashMap()
        resMap["text1#105"] = "texto1#105"
        resMap["text2#105"] = "texto2#105"
        Assert.assertEquals(resMap, extractDictionary(lines))
    }

    @Test
    fun checkLineLength() {
        Assert.assertTrue(checkLineLength("text1#105", "texto1#105"))
        Assert.assertFalse(checkLineLength("text1#105", "texto1#106"))
    }

    @Test
    fun textArea() {
        val table = HexTable(0)
        val bytes = byteArrayOf(0x40, 0x41, 0x42, 0x43, 0x44, 0x45)
        Assert.assertEquals("ABCD", getTextArea(1, 4, bytes, table))
    }

    @Test
    fun hexArea() {
        val bytes = byteArrayOf(0x40, 0x41, 0x42, 0x43, 0x44, 0x45)
        Assert.assertEquals("41 42 43 44 ", getHexArea(1, 4, bytes))
    }

    @Test
    fun hexAreaFixedWidth() {
        val bytes = byteArrayOf(0x40, 0x41, 0x42, 0x43, 0x44, 0x45)
        Assert.assertEquals(
            """
                    41 42
                    43 44 
                    """.trimIndent(), getHexAreaFixedWidth(1, 4, bytes, 2)
        )
    }

    @Test
    fun removeCommentsAndJoin() {
        val file = ";comment\n@addr\ntext\n;comment2\n@addr2\ntext2"
        val res = arrayOf("@addr", "text@addr2", "text2")
        Assert.assertArrayEquals(res, removeCommentsAndJoin(file))
    }

    @Test
    fun compressed4To3Data() {
        val bytes = byteArrayOf(0, 1, 2, 3, 4)
        val bytesComp = byteArrayOf(0, 16, -125, 4)
        Assert.assertArrayEquals(bytesComp, getCompressed4To3Data(bytes))
        val bytes2 = byteArrayOf(0, 1, 2, 3, 4, 5)
        val bytesComp2 = byteArrayOf(0, 16, -125, 16)
        Assert.assertArrayEquals(bytesComp2, getCompressed4To3Data(bytes2))
        val bytes3 = byteArrayOf(0, 1, 2, 3, 4, 5, 6)
        val bytesComp3 = byteArrayOf(0, 16, -125, 16, 81)
        Assert.assertArrayEquals(bytesComp3, getCompressed4To3Data(bytes3))
    }

    @Test
    fun expanded3To4Data() {
        val bytesComp = byteArrayOf(0, 16, -125, 4)
        val bytesDecomp = byteArrayOf(0, 1, 2, 3, 1, 0)
        Assert.assertArrayEquals(bytesDecomp, getExpanded3To4Data(bytesComp))
        val bytesComp2 = byteArrayOf(0, 16, -125, 16)
        val bytesDecomp2 = byteArrayOf(0, 1, 2, 3, 4, 0)
        Assert.assertArrayEquals(bytesDecomp2, getExpanded3To4Data(bytesComp2))
        val bytesComp3 = byteArrayOf(0, 16, -125, 16, 81)
        val bytesDecomp3 = byteArrayOf(0, 1, 2, 3, 4, 5, 4)
        Assert.assertArrayEquals(bytesDecomp3, getExpanded3To4Data(bytesComp3))
    }

    @Test
    fun log() {
        var ok = true
        try {
            log("test")
        } catch (e: Exception) {
            ok = false
        }
        Assert.assertTrue(ok)
    }

    @Test
    fun logNoNL() {
        var ok = true
        try {
            logNoNL("test")
        } catch (e: Exception) {
            ok = false
        }
        Assert.assertTrue(ok)
    }

    @Test
    fun logException() {
        var ok = true
        try {
            logException(Exception())
        } catch (e: Exception) {
            ok = false
        }
        Assert.assertTrue(ok)
    }

    @Test
    fun shortToBytes() {
        val bytes16 = byteArrayOf(2, 1)
        Assert.assertArrayEquals(bytes16, shortToBytes(258.toShort()))
    }

    @Test
    fun bytesToHex() {
        val bytes = byteArrayOf(1, 2, 3, 4)
        Assert.assertEquals("01020304", bytesToHex(bytes))
    }
}