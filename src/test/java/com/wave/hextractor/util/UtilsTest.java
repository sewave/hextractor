package com.wave.hextractor.util;

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
	}

	@Test
	public void extractDictionary() {
	}

	@Test
	public void checkLineLength() {
	}

	@Test
	public void getTextArea() {
	}

	@Test
	public void getHexArea() {
	}

	@Test
	public void getHexAreaFixedWidth() {
	}

	@Test
	public void removeCommentsAndJoin() {
	}

	@Test
	public void getCompressed4To3Data() {
	}

	@Test
	public void getExpanded3To4Data() {
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
