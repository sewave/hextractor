package com.wave.hextractor.util;

import com.wave.hextractor.pojo.FileWithDigests;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class FileUtilsTest {
	@Test
	public void testGetGameName() {
		assertEquals("Crash 'n the Boys - Street Challenge",
				FileUtils.getGameName("Crash 'n the Boys - Street Challenge (U) [!].nes"));
		assertEquals("Fantasy Zone", FileUtils.getGameName("Fantasy Zone (J).nes"));
		assertEquals("The King of Kings",
				FileUtils.getGameName("King of Kings, The (Wisdom Tree) (V5.0 CHR 1.3) [!].nes"));
		assertEquals("The Adventures of Tintin - Prisoners of the Sun",
				FileUtils.getGameName("Adventures of Tintin, The - Prisoners of the Sun (E).smc"));
		assertEquals("The Blues Brothers - Jukebox Adventure",
				FileUtils.getGameName("Blues Brothers, The - Jukebox Adventure (U).gb"));
	}

	@Test
	public void testGetFileExtension() {
		assertEquals("nes", FileUtils.getFileExtension("Crash 'n the Boys - Street Challenge (U) [!].nes"));
		assertEquals("bin", FileUtils.getFileExtension("Spot Goes to Hollywood (U) (REV01) [!].bin"));
		assertEquals("smc", FileUtils.getFileExtension("Adventures of Tintin, The - Prisoners of the Sun (E).smc"));
	}

	@Test
	public void testFillGameData() throws IOException {
		File file = File.createTempFile("test", "fillGameData");
		file.deleteOnExit();
		File readme = new File(Objects.requireNonNull(getClass().getClassLoader().getResource("files/readme.txt")).getFile());
		File testFile = new File(Objects.requireNonNull(getClass().getClassLoader().getResource("files/test.bin")).getFile());
		FileUtils.fillGameData(readme.getAbsolutePath(), file.getAbsolutePath(), testFile.getAbsolutePath());
		assertTrue(FileUtils.getAsciiFile(file.getAbsolutePath()).length() > 0);
	}
	
	@Test
	public void testGetFileWithDigests() throws IOException {
		File testFile = new File(Objects.requireNonNull(getClass().getClassLoader().getResource("files/test.bin")).getFile());
		FileWithDigests testFileDigests = FileUtils.getFileWithDigests(testFile.getAbsolutePath());
		assertEquals("test.bin", testFileDigests.getName());
		assertEquals("30a4e38230885e27d1bb3fd0713dfa7d", testFileDigests.getMd5());
		assertEquals("4e174bbc3e0a536aa8899d1f459318f797dc325a", testFileDigests.getSha1());
		assertEquals("43549d77", testFileDigests.getCrc32());
		assertEquals(11, testFileDigests.getBytes().length);
	}
	
	@Test
	public void testGetGameSystem() {
		assertEquals(Constants.SYSTEM_GB, FileUtils.getGameSystem("Blues Brothers, The - Jukebox Adventure (U).gb"));
		assertEquals(Constants.SYSTEM_NES, FileUtils.getGameSystem("Crash 'n the Boys - Street Challenge (U) [!].nes"));
		assertEquals(Constants.SYSTEM_SFC, FileUtils.getGameSystem("Adventures of Tintin, The - Prisoners of the Sun (E).smc"));
		assertEquals(Constants.SYSTEM_SFC, FileUtils.getGameSystem("Adventures of Tintin, The - Prisoners of the Sun (E).sfc"));
		assertEquals(Constants.SYSTEM_SMD, FileUtils.getGameSystem("Spot Goes to Hollywood (U) (REV01) [!].bin"));
		assertEquals(Constants.SYSTEM_SMD, FileUtils.getGameSystem("Spot Goes to Hollywood (U) (REV01) [!].smd"));
		assertEquals(Constants.SYSTEM_SMD, FileUtils.getGameSystem("Spot Goes to Hollywood (U) (REV01) [!].gen"));
		assertEquals(Constants.SYSTEM_SMD, FileUtils.getGameSystem("Spot Goes to Hollywood (U) (REV01) [!].md"));
		assertEquals(Constants.SYSTEM_MSX, FileUtils.getGameSystem("test.mx1"));
		assertEquals(Constants.SYSTEM_MSX, FileUtils.getGameSystem("test.mx2"));
		assertEquals(Constants.SYSTEM_MSX, FileUtils.getGameSystem("test.rom"));
		assertEquals(Constants.SYSTEM_SMS, FileUtils.getGameSystem("test.sms"));
		assertEquals(Constants.SYSTEM_SMD_32X, FileUtils.getGameSystem("test.32x"));
		assertEquals(Constants.SYSTEM_GBC, FileUtils.getGameSystem("Blues Brothers, The - Jukebox Adventure (U).gbc"));
		assertEquals(Constants.SYSTEM_GBA, FileUtils.getGameSystem("Blues Brothers, The - Jukebox Adventure (U).gba"));
		assertEquals(Constants.SYSTEM_SGG, FileUtils.getGameSystem("test.gg"));
		assertEquals(Constants.SYSTEM_SG1K, FileUtils.getGameSystem("test.sg"));
		assertEquals(Constants.SYSTEM_COL, FileUtils.getGameSystem("test.col"));
		assertEquals(Constants.SYSTEM_PCE, FileUtils.getGameSystem("test.pce"));
		assertEquals(Constants.SYSTEM_ZXS, FileUtils.getGameSystem("test.tap"));
		assertEquals(Constants.SYSTEM_ZXS, FileUtils.getGameSystem("test.tzx"));
		assertEquals(Constants.SYSTEM_CPC, FileUtils.getGameSystem("test.cdt"));
		assertEquals(Constants.SYSTEM_NGP, FileUtils.getGameSystem("test.ngp"));
		assertEquals(Constants.SYSTEM_NGPC, FileUtils.getGameSystem("test.ngc"));
	}

}
