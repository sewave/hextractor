package com.wave.hextractor.util;

import com.wave.hextractor.object.HexTable;
import com.wave.hextractor.pojo.OffsetEntry;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.*;

public class UtilsTest {

	@Test
	public void isValidFileName() {
		assertTrue(Utils.isValidFileName("abc.def"));
		assertFalse(Utils.isValidFileName("a<bc.def"));
	}

	@Test
	public void copyFileUsingStream() throws IOException {
		File file = File.createTempFile("test", "copyFileUsingStream");
		file.deleteOnExit();
		File file2 = File.createTempFile("test", "copyFileUsingStream2");
		file2.deleteOnExit();
		String testStr =  UUID.randomUUID().toString();
		FileUtils.writeFileAscii(file.getAbsolutePath(), testStr);
		Utils.copyFileUsingStream(file.getAbsolutePath(), file2.getAbsolutePath());
		assertEquals(testStr, FileUtils.getAsciiFile(file2.getAbsolutePath()));
	}

	@Test
	public void intToByteArray() {
		byte[] res = {7, 91, -51, 21};
		assertArrayEquals(res, Utils.intToByteArray(123456789));
	}

	@Test
	public void createFile() throws IOException {
		File file = File.createTempFile("test", "createFile");
		file.deleteOnExit();
		String content = UUID.randomUUID().toString();
		Utils.createFile(file.getAbsolutePath(), content);
		assertEquals(content, FileUtils.getAsciiFile(file.getAbsolutePath()));
	}

	@Test
	public void getJoinedFileName() throws IOException {
		File path = Files.createTempDirectory("getJoinedFileName").toFile();
		path.deleteOnExit();
		assertEquals(path.getAbsolutePath() + Constants.FILE_SEPARATOR + "aaa",
				Utils.getJoinedFileName(path, "aaa"));
	}

	@Test
	public void fillLeft() {
		assertEquals("000aaa", Utils.fillLeft("aaa", 6));
	}

	@Test
	public void isDebug() {
		System.setProperty("logLevel", "DEBUG");
		assertTrue(Utils.isDebug());
		System.setProperty("logLevel", "WARN");
		assertFalse(Utils.isDebug());
	}

	@Test
	public void getHexFilledLeft() {
		Assert.assertEquals("0000", Utils.getHexFilledLeft(0, 4));
	}

	@Test
	public void toHexString() {
		byte[] data = {0, 1, 2, 3, 4};
		Assert.assertEquals("[00 01 02 03 04 ]", Utils.toHexString(data));
	}

	@Test
	public void hexStringToByteArray() {
		byte[] data = {0, 1, 2, 3, 4};
		Assert.assertArrayEquals(data, Utils.hexStringToByteArray("0001020304"));
	}

	@Test
	public void hexStringCharToByte() {
		Assert.assertEquals((byte) 0, Utils.hexStringCharToByte("00"));
	}

	@Test
	public void bytesToInt() {
		byte[] bytes16 = Utils.intToByteArray(258);

		assertEquals(258, Utils.bytesToInt(bytes16[2], bytes16[3]));
	}

	@Test
	public void bytesToInt1() {
		byte[] bytes24 = Utils.intToByteArray(131071);
		assertEquals(0, Utils.bytesToInt((byte) 0,(byte) 0, (byte) 0));
		assertEquals(131071, Utils.bytesToInt(bytes24[1], bytes24[2], bytes24[3]));
	}

	@Test
	public void bytesToInt2() {
		byte[] bytes32 = Utils.intToByteArray(33554431);
		assertEquals(0, Utils.bytesToInt((byte) 0, (byte) 0, (byte) 0, (byte) 0));
		assertEquals(33554431, Utils.bytesToInt(bytes32[0], bytes32[1], bytes32[2], bytes32[3]));
	}

	@Test
	public void loadHex() {
		String input = ";Traducciones Wave 2019\n" +
				"00 01 02 03@00000000:00000003";
		byte[] data = new byte[4];
		Utils.loadHex(input, data);
		byte[] dataEnd = {0, 1, 2, 3};
		assertArrayEquals(dataEnd, data);
	}

	@Test
	public void getOffsets() throws IOException {
		File file = File.createTempFile("test", "getOffsets.off");
		file.deleteOnExit();
		String offsets = "00000000-00000003-FF";
		FileUtils.writeFileAscii(file.getAbsolutePath(), offsets);
		OffsetEntry entry = new OffsetEntry(0, 3, Collections.singletonList("FF"));
		List<OffsetEntry> resEntries = Collections.singletonList(entry);
		Assert.assertEquals(resEntries, Utils.getOffsets(file.getAbsolutePath()));
		Assert.assertEquals(resEntries, Utils.getOffsets(offsets));
	}

	@Test
	public void getLinesCleaned() {
		String[] lines = {"aaa~~", "bbb@", "CCC^", "DDD¨"};
		Utils.getLinesCleaned(lines);
	}

	@Test
	public void stringHasWords() {
		List<String> words = new ArrayList<>();
		Assert.assertFalse(
				Utils.stringHasWords(words,"many words"));
		words.add("many");
		words.add("words");
		Assert.assertTrue(
				Utils.stringHasWords(words,"many words"));
	}

	@Test
	public void getCleanedString() {
		Assert.assertEquals("abcd x'yz", Utils.getCleanedString("´Abcd    X'YZ"));
	}

	@Test
	public void allSameValue() {
		byte[] eq = {1, 1, 1, 1};
		byte[] neq = {2, 1, 4, 1};
		Assert.assertTrue(Utils.allSameValue(eq));
		Assert.assertFalse(Utils.allSameValue(neq));
	}

	@Test
	public void intToHexString() {
		Assert.assertEquals("0001", Utils.intToHexString(1, 4));
	}

	@Test
	public void hexStringListToIntList() {
		String[] hexValues = {"01", "02", "03"};
		int[] intValues = {1, 2, 3};
		Assert.assertArrayEquals(intValues, Utils.hexStringListToIntList(hexValues));
	}

	@Test
	public void getHexOffsets() {
		String entries = "0-1,5:6";
		List<OffsetEntry> offEntries = new ArrayList<>();
		offEntries.add(new OffsetEntry(0,1, null));
		offEntries.add(new OffsetEntry(5,10, null));
		Assert.assertEquals(offEntries, Utils.getHexOffsets(entries));
	}

	@Test
	public void sortByValue() {
		Map<String, String> initMap = new HashMap<>();
		initMap.put("01", "ZZ");
		initMap.put("02", "AA");
		initMap = Utils.sortByValue(initMap);
		List<String> valuesInit = new ArrayList<>(initMap.values());
		List<String> valuesSorted = Arrays.asList("AA", "ZZ");
		Assert.assertEquals(valuesSorted, valuesInit);
	}

	@Test
	public void toFileString() {
		List<OffsetEntry> offEntries = new ArrayList<>();
		offEntries.add(new OffsetEntry(0,1, Arrays.asList("FF", "00")));
		offEntries.add(new OffsetEntry(5,10, Arrays.asList("01", "00")));
		Assert.assertEquals("00000000-00000001-FF-00,00000005-0000000A-01-00", Utils.toFileString(offEntries));
	}

	@Test
	public void extractDictionary() {
		String[] lines = new String[4];
		lines[0] = ";000000DE{text1}#117#105";
		lines[1] = "texto1#105";
		lines[2] = ";000000DE{text2}#117#105";
		lines[3] = "texto2#105";
		Map<String, String> resMap = new HashMap<>();
		resMap.put("text1#105", "texto1#105");
		resMap.put("text2#105", "texto2#105");
		Assert.assertEquals(resMap, Utils.extractDictionary(lines));
	}

	@Test
	public void checkLineLength() {
		Assert.assertTrue(Utils.checkLineLength("text1#105", "texto1#105"));
		Assert.assertFalse(Utils.checkLineLength("text1#105", "texto1#106"));
	}

	@Test
	public void getTextArea() {
		HexTable table = new HexTable(0);
		byte[] bytes = {0x40, 0x41, 0x42, 0x43, 0x44, 0x45};
		Assert.assertEquals("ABCD", Utils.getTextArea(1, 4, bytes, table));
	}

	@Test
	public void getHexArea() {
		byte[] bytes = {0x40, 0x41, 0x42, 0x43, 0x44, 0x45};
		Assert.assertEquals("41 42 43 44 ", Utils.getHexArea(1, 4, bytes));
	}

	@Test
	public void getHexAreaFixedWidth() {
		byte[] bytes = {0x40, 0x41, 0x42, 0x43, 0x44, 0x45};
		Assert.assertEquals("41 42\n" + "43 44 ", Utils.getHexAreaFixedWidth(1, 4, bytes, 2));
	}

	@Test
	public void removeCommentsAndJoin() {
		String file = ";comment\n@addr\ntext\n;comment2\n@addr2\ntext2";
		String[] res = {"@addr", "text@addr2", "text2"};
		Assert.assertArrayEquals(res, Utils.removeCommentsAndJoin(file));
	}

	@Test
	public void getCompressed4To3Data() {
		byte[] bytes = {0, 1, 2, 3, 4};
		byte[] bytesComp = {0, 16, -125, 4};
		Assert.assertArrayEquals(bytesComp, Utils.getCompressed4To3Data(bytes));

		byte[] bytes2 = {0, 1, 2, 3, 4, 5};
		byte[] bytesComp2 = {0, 16, -125, 16};
		Assert.assertArrayEquals(bytesComp2, Utils.getCompressed4To3Data(bytes2));

		byte[] bytes3 = {0, 1, 2, 3, 4, 5, 6};
		byte[] bytesComp3 = {0, 16, -125, 16, 81};
		Assert.assertArrayEquals(bytesComp3, Utils.getCompressed4To3Data(bytes3));
	}

	@Test
	public void getExpanded3To4Data() {
		byte[] bytesComp = {0, 16, -125, 4};
		byte[] bytesDecomp = {0, 1, 2, 3, 1, 0};
		Assert.assertArrayEquals(bytesDecomp, Utils.getExpanded3To4Data(bytesComp));

		byte[] bytesComp2 = {0, 16, -125, 16};
		byte[] bytesDecomp2 = {0, 1, 2, 3, 4, 0};
		Assert.assertArrayEquals(bytesDecomp2, Utils.getExpanded3To4Data(bytesComp2));

		byte[] bytesComp3 = {0, 16, -125, 16, 81};
		byte[] bytesDecomp3 = {0, 1, 2, 3, 4, 5, 4};
		Assert.assertArrayEquals(bytesDecomp3, Utils.getExpanded3To4Data(bytesComp3));
	}

	@Test
	public void log() {
		boolean ok = true;
		try {
			Utils.log("test");
		} catch(Exception e) {
			ok = false;
		}
		assertTrue(ok);
	}

	@Test
	public void logNoNL() {
		boolean ok = true;
		try {
			Utils.logNoNL("test");
		} catch(Exception e) {
			ok = false;
		}
		assertTrue(ok);
	}

	@Test
	public void logException() {
		boolean ok = true;
		try {
			Utils.logException(new Exception());
		} catch(Exception e) {
			ok = false;
		}
		assertTrue(ok);
	}

	@Test
	public void shortToBytes() {
		byte[] bytes16 = {2, 1};
		assertArrayEquals(bytes16, Utils.shortToBytes((short) 258));
	}

	@Test
	public void bytesToHex() {
		byte[] bytes = {1, 2, 3, 4};
		Assert.assertEquals("01020304", Utils.bytesToHex(bytes));
	}
}
