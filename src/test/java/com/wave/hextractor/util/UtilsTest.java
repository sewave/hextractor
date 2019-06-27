package com.wave.hextractor.util;

import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.UUID;

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
	}

	@Test
	public void toHexString() {
	}

	@Test
	public void hexStringToByteArray() {
	}

	@Test
	public void hexStringCharToByte() {
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
	}

	@Test
	public void getOffsets() {
	}

	@Test
	public void getLinesCleaned() {
	}

	@Test
	public void stringHasWords() {
	}

	@Test
	public void getCleanedString() {
	}

	@Test
	public void allSameValue() {
	}

	@Test
	public void intToHexString() {
	}

	@Test
	public void hexStringListToIntList() {
	}

	@Test
	public void getHexOffsets() {
	}

	@Test
	public void sortByValue() {
	}

	@Test
	public void toFileString() {
	}

	@Test
	public void extractDictionary() {
	}

	@Test
	public void translateDictionary() {
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
	public void getHexAreaFixedWidthHtml() {
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
	}

	@Test
	public void logException() {
	}

	@Test
	public void shortToBytes() {
	}

	@Test
	public void bytesToHex() {
	}
}
