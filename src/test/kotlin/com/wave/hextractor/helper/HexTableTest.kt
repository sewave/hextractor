package com.wave.hextractor.helper

import com.wave.hextractor.pojo.OffsetEntry
import org.junit.Assert
import org.junit.Test
import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.util.*

class HexTableTest {
    @Test
    fun constructors() {
        var ok: Boolean
        try {
            var test = HexTable()
            Assert.assertNotNull(test.toString())
            test = HexTable(LINES)
            Assert.assertNotNull(test.toString())
            ok = true
        } catch (e: Exception) {
            ok = false
        }
        Assert.assertTrue(ok)
    }

    @Test
    fun toString1() {
        val table = HexTable(LINES)
        Assert.assertEquals("b", table.toString(1.toByte(), expand = true, decodeUnknown = true))
        Assert.assertEquals(".", table.toString(3.toByte(), expand = false, decodeUnknown = false))
        Assert.assertEquals("ab", table.toString(3.toByte(), expand = true, decodeUnknown = false))
        Assert.assertEquals("~04~", table.toString(4.toByte(), expand = false, decodeUnknown = true))
    }

    @Test
    fun toString2() {
        val table = HexTable(LINES)
        Assert.assertEquals(".", table.toString(3.toByte(), false))
        Assert.assertEquals("ab", table.toString(3.toByte(), true))
    }

    @Test
    fun toSelectionString() {
        val table = HexTable(LINES)
        val table2 = HexTable(LINES2)
        val table3 = HexTable(LINES3)
        Assert.assertEquals("a: 00 ", table.toSelectionString())
        Assert.assertEquals("A: 01 a: 00 ", table2.toSelectionString())
        Assert.assertEquals("A: 01 a: 00 0: 02 ", table3.toSelectionString())
    }

    @Test
    fun toAscii() {
        val table = HexTable(LINES)
        Assert.assertEquals("abc..", table.toAscii(byteArrayOf(0, 1, 2, 3, 4), expand = false, false))
        Assert.assertEquals("abcab.", table.toAscii(byteArrayOf(0, 1, 2, 3, 4), expand = true, decodeUnknown = false))
        Assert.assertEquals("abcab~04~", table.toAscii(byteArrayOf(0, 1, 2, 3, 4), expand = true, decodeUnknown = true))
        Assert.assertEquals("abc~03~~04~", table.toAscii(byteArrayOf(0, 1, 2, 3, 4),
            expand = false,
            decodeUnknown = true
        ))
    }

    @Test
    fun toAscii1() {
        val table = HexTable(LINES)
        Assert.assertEquals("abc..", table.toAscii(byteArrayOf(0, 1, 2, 3, 4), false))
        Assert.assertEquals("abcab.", table.toAscii(byteArrayOf(0, 1, 2, 3, 4), true))
    }

    @Test
    fun toHex() {
        val table = HexTable(LINES)
        val res = byteArrayOf(0, 1, 2, 0, 0, 1, 0)
        Assert.assertArrayEquals(res, table.toHex("abc{ab}"))
    }

    @Test
    fun addToTable() {
        val table = HexTable()
        val data = byteArrayOf(5, 0)
        Assert.assertNotEquals("Ka", table.toAscii(data, false))
        table.addToTable(5.toByte(), "K")
        table.addToTable(0.toByte(), "a")
        Assert.assertEquals("Ka", table.toAscii(data, false))
    }

    @Test
    fun toAscii2() {
        val table = HexTable(LINES)
        val data = byteArrayOf(0, 1, 2, 3, 4)
        val entry = OffsetEntry(0, 4, mutableListOf("FF"))
        val ascii = """
            @00000000-00000004-FF
            ;00000000{abc{ab}~04~}#011#005
            abc{ab}~04~#005
            |5
            
        """.trimIndent()
        Assert.assertEquals(ascii, table.toAscii(data, entry, true))
        Assert.assertEquals(ascii, table.toAscii(data, entry, false))
    }

    @Test
    fun toHex1() {
        val table = HexTable(LINES)
        val data = byteArrayOf(0, 1, 2, 3, 4)
        val entry = OffsetEntry(0, 4, mutableListOf("FF"))
        Assert.assertArrayEquals(data, table.toHex("abc{ab}~04~#005\n|5\n", entry))
    }

    @Throws(IOException::class)
    @Test
    fun allEntries() {
        val table = HexTable(0)
        val searchAll = File(Objects.requireNonNull(javaClass.classLoader.getResource("files/searchAll.txt")).file)
        val secondFileBytes = Files.readAllBytes(searchAll.toPath())
        val dictFile = File(Objects.requireNonNull(javaClass.classLoader.getResource("files/TestDict.txt")).file)
        Assert.assertEquals(
            "00000000-0000000A-00-FF,0000000C-00000015-00-FF,00000022-0000002B-00-FF,",
            table.getAllEntries(secondFileBytes, 4, 1, mutableListOf("00", "FF"), dictFile.absolutePath)
        )
    }

    @Test
    fun toAsciiTable() {
        val table = HexTable(LINES)
        Assert.assertEquals(LINESRES, table.toAsciiTable())
    }

    @Test
    fun hashCode1() {
        val table1 = HexTable()
        val table2 = HexTable()
        Assert.assertEquals(table1.hashCode().toLong(), table2.hashCode().toLong())
        val table3 = HexTable(LINES)
        val table4 = HexTable(LINES)
        Assert.assertEquals(table3.hashCode().toLong(), table4.hashCode().toLong())
    }

    @Test
    fun equals1() {
        val table1 = HexTable()
        val table2 = HexTable()
        Assert.assertEquals(table1, table2)
        val table3 = HexTable(LINES)
        val table4 = HexTable(LINES)
        Assert.assertEquals(table3, table4)
        Assert.assertNotEquals(table3, Any())
    }

    @Test
    fun searchPercent() {
        val table = HexTable(LINES)
        Assert.assertEquals(0.0, table.searchPercent.toDouble(), 0.0)
    }

    companion object {
        private val LINES = Collections.unmodifiableList(
            mutableListOf(
                "00=a",
                "01=b",
                "02=c",
                "03=ab",
                "ad"
            )
        )
        private const val LINESRES = "00=á\n" + "00=a\n" + "03=ab\n" + "01=b\n" + "02=c\n"
        private val LINES2 = Collections.unmodifiableList(
            mutableListOf(
                "00=a",
                "01=A",
                "02=c",
                "03=ab",
                "ad"
            )
        )
        private val LINES3 = Collections.unmodifiableList(
            mutableListOf(
                "00=a",
                "01=A",
                "02=0",
                "03=ab",
                "ad"
            )
        )
    }
}
