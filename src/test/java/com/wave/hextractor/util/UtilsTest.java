package com.wave.hextractor.util;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.*;

public class UtilsTest {

	@Test
	public void testIsValidFileName() {
		assertTrue(Utils.isValidFileName("abc.def"));
		assertFalse(Utils.isValidFileName("a<bc.def"));
	}

	@Test
	public void testCopyFileUsingStream() throws IOException {
		File file = File.createTempFile("test", "copyFileUsingStream");
		file.deleteOnExit();
		File file2 = File.createTempFile("test", "copyFileUsingStream2");
		file2.deleteOnExit();
		String testStr = RandomStringUtils.randomAlphabetic(64);
		FileUtils.writeFileAscii(file.getAbsolutePath(), testStr);
		Utils.copyFileUsingStream(file.getAbsolutePath(), file2.getAbsolutePath());
		assertEquals(testStr, FileUtils.getAsciiFile(file2.getAbsolutePath()));
	}

	@Test
	public void testIntToByteArray() {
		byte[] res = {7, 91, -51, 21};
		assertArrayEquals(res, Utils.intToByteArray(123456789));
	}

}
