package com.wave.hextractor.object;

import com.wave.hextractor.pojo.OffsetEntry;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class HexTableTest {

    private static final List<String> LINES = Collections.unmodifiableList(Arrays.asList(
            "00=a", "01=b", "02=c", "03=ab" ,"ad"
    ));

    private static final String LINESRES = "00=á\n" +
            "00=a\n" +
            "03=ab\n" +
            "01=b\n" +
            "02=c\n";

    private static final List<String> LINES2 = Collections.unmodifiableList(Arrays.asList(
            "00=a", "01=A", "02=c", "03=ab" ,"ad"
    ));

    private static final List<String> LINES3 = Collections.unmodifiableList(Arrays.asList(
            "00=a", "01=A", "02=0", "03=ab" ,"ad"
    ));

    @Test
    public void constructors() {
        boolean ok;
        try {
            HexTable test = new HexTable();
            Assert.assertNotNull(test.toString());
            test = new HexTable(LINES);
            Assert.assertNotNull(test.toString());
            ok = true;
        }
        catch(Exception e) {
            ok = false;
        }
        Assert.assertTrue(ok);
    }

    @Test
    public void toString1() {
        HexTable table = new HexTable(LINES);
        Assert.assertEquals("b", table.toString((byte)1, true, true));
        Assert.assertEquals(".", table.toString((byte)3, false, false));
        Assert.assertEquals("ab", table.toString((byte)3, true, false));
        Assert.assertEquals("~04~", table.toString((byte)4, false, true));
    }

    @Test
    public void toString2() {
        HexTable table = new HexTable(LINES);
        Assert.assertEquals(".", table.toString((byte)3, false));
        Assert.assertEquals("ab", table.toString((byte)3, true));
    }

    @Test
    public void toSelectionString() {
        HexTable table = new HexTable(LINES);
        HexTable table2 = new HexTable(LINES2);
        HexTable table3 = new HexTable(LINES3);
        Assert.assertEquals("a: 00 ", table.toSelectionString());
        Assert.assertEquals("A: 01 a: 00 ", table2.toSelectionString());
        Assert.assertEquals("A: 01 a: 00 0: 02 ", table3.toSelectionString());
    }

    @Test
    public void toAscii() {
        HexTable table = new HexTable(LINES);
        Assert.assertEquals("abc..", table.toAscii(new byte[] {0,1,2,3,4}, false, false));
        Assert.assertEquals("abcab.", table.toAscii(new byte[] {0,1,2,3,4}, true, false));
        Assert.assertEquals("abcab~04~", table.toAscii(new byte[] {0,1,2,3,4}, true, true));
        Assert.assertEquals("abc~03~~04~", table.toAscii(new byte[] {0,1,2,3,4}, false, true));
    }

    @Test
    public void toAscii1() {
        HexTable table = new HexTable(LINES);
        Assert.assertEquals("abc..", table.toAscii(new byte[]{0, 1, 2, 3, 4}, false));
        Assert.assertEquals("abcab.", table.toAscii(new byte[]{0, 1, 2, 3, 4}, true));
    }

    @Test
    public void toHex() {
        HexTable table = new HexTable(LINES);
        byte[] res = {0, 1, 2, 0, 0, 1, 0};
        Assert.assertArrayEquals(res, table.toHex("abc{ab}"));
    }

    @Test
    public void addToTable() {
        HexTable table = new HexTable();
        byte[] data = {5, 0};
        Assert.assertNotEquals("Ka", table.toAscii(data, false));
        table.addToTable((byte) 5, "K");
        table.addToTable((byte) 0, "a");
        Assert.assertEquals("Ka", table.toAscii(data, false));
    }

    @Test
    public void toAscii2() {
        HexTable table = new HexTable(LINES);
        byte[] data = new byte[]{0, 1, 2, 3, 4};
        OffsetEntry entry = new OffsetEntry(0, 4, Arrays.asList("FF"));
        String ascii = "@00000000-00000004-FF\n" +
                ";00000000{abc{ab}~04~}#011#005\n" +
                "abc{ab}~04~#005\n" +
                "|5\n";
        Assert.assertEquals(ascii, table.toAscii(data, entry,true));
        Assert.assertEquals(ascii, table.toAscii(data, entry,false));
    }

    @Test
    public void toHex1() {
        HexTable table = new HexTable(LINES);
        byte[] data = new byte[]{0, 1, 2, 3, 4};
        OffsetEntry entry = new OffsetEntry(0, 4, Arrays.asList("FF"));
        Assert.assertArrayEquals(data, table.toHex("abc{ab}~04~#005\n|5\n", entry));
    }

    @Test
    public void getAllEntries() throws IOException {
        HexTable table = new HexTable(0);
        File searchAll = new File(Objects.requireNonNull(getClass().getClassLoader().getResource("files/searchAll.txt")).getFile());
        byte[] secondFileBytes = Files.readAllBytes(searchAll.toPath());
        File dictFile = new File(Objects.requireNonNull(getClass().getClassLoader().getResource("files/TestDict.txt")).getFile());
        Assert.assertEquals("00000000-0000000A-00-FF,0000000C-00000015-00-FF,00000022-0000002B-00-FF,",
                table.getAllEntries(secondFileBytes, 4, 1,
        Arrays.asList("00", "FF"), dictFile.getAbsolutePath()));
    }

    @Test
    public void toAsciiTable() {
        HexTable table = new HexTable(LINES);
        Assert.assertEquals(LINESRES, table.toAsciiTable());
    }

    @Test
    public void hashCode1() {
        HexTable table1 = new HexTable();
        HexTable table2 = new HexTable();
        Assert.assertEquals(table1.hashCode(), table2.hashCode());
        HexTable table3 = new HexTable(LINES);
        HexTable table4 = new HexTable(LINES);
        Assert.assertEquals(table3.hashCode(), table4.hashCode());
    }

    @Test
    public void equals1() {
        HexTable table1 = new HexTable();
        HexTable table2 = new HexTable();
        Assert.assertEquals(table1, table2);
        HexTable table3 = new HexTable(LINES);
        HexTable table4 = new HexTable(LINES);
        Assert.assertEquals(table3, table4);
        Assert.assertNotEquals(table3, new Object());
    }

    @Test
    public void getSearchPercent() {
        HexTable table = new HexTable(LINES);
        Assert.assertTrue(table.getSearchPercent() == 0);
    }
}