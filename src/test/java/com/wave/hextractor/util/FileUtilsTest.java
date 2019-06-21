package com.wave.hextractor.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

import com.wave.hextractor.pojo.FileWithDigests;

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
		File readme = new File(getClass().getClassLoader().getResource("files/readme.txt").getFile());
		File testFile = new File(getClass().getClassLoader().getResource("files/test.bin").getFile());
		FileUtils.fillGameData(readme.getAbsolutePath(), file.getAbsolutePath(), testFile.getAbsolutePath());
		assertTrue(FileUtils.getAsciiFile(file.getAbsolutePath()).length() > 0);
	}
	
	@Test
	public void testGetFileWithDigests() throws IOException {
		File testFile = new File(getClass().getClassLoader().getResource("files/test.bin").getFile());
		FileWithDigests testFileDigests = FileUtils.getFileWithDigests(testFile.getAbsolutePath());
		assertEquals("test.bin", testFileDigests.getName());
		assertEquals("30a4e38230885e27d1bb3fd0713dfa7d", testFileDigests.getMd5());
		assertEquals("4e174bbc3e0a536aa8899d1f459318f797dc325a", testFileDigests.getSha1());
		assertEquals("43549d77", testFileDigests.getCrc32());
		assertEquals(11, testFileDigests.getBytes().length);
	}
	
	@Test
	public void testGetGameSystem() {
		assertEquals("Game Boy", FileUtils.getGameSystem("Blues Brothers, The - Jukebox Adventure (U).gb"));
		assertEquals("NES", FileUtils.getGameSystem("Crash 'n the Boys - Street Challenge (U) [!].nes"));
		assertEquals("Super Nintendo", FileUtils.getGameSystem("Adventures of Tintin, The - Prisoners of the Sun (E).smc"));
		assertEquals("Super Nintendo", FileUtils.getGameSystem("Adventures of Tintin, The - Prisoners of the Sun (E).sfc"));
		assertEquals("Mega Drive", FileUtils.getGameSystem("Spot Goes to Hollywood (U) (REV01) [!].bin"));
		assertEquals("Mega Drive", FileUtils.getGameSystem("Spot Goes to Hollywood (U) (REV01) [!].smd"));
		assertEquals("Mega Drive", FileUtils.getGameSystem("Spot Goes to Hollywood (U) (REV01) [!].gen"));
		assertEquals("Mega Drive", FileUtils.getGameSystem("Spot Goes to Hollywood (U) (REV01) [!].md"));
		assertEquals("MSX", FileUtils.getGameSystem("test.mx1"));
		assertEquals("MSX", FileUtils.getGameSystem("test.mx2"));
		assertEquals("MSX", FileUtils.getGameSystem("test.rom"));
		assertEquals("Master System", FileUtils.getGameSystem("test.sms"));
		assertEquals("Mega Drive 32X", FileUtils.getGameSystem("test.32x"));
		assertEquals("Game Boy Color", FileUtils.getGameSystem("Blues Brothers, The - Jukebox Adventure (U).gbc"));
		assertEquals("Game Boy Advance", FileUtils.getGameSystem("Blues Brothers, The - Jukebox Adventure (U).gba"));
		assertEquals("Game Gear", FileUtils.getGameSystem("test.gg"));
		assertEquals("SG-1000", FileUtils.getGameSystem("test.sg"));
		assertEquals("Colecovision", FileUtils.getGameSystem("test.col"));
		assertEquals("PC Engine", FileUtils.getGameSystem("test.pce"));
		assertEquals("ZX Spectrum", FileUtils.getGameSystem("test.tap"));
		assertEquals("ZX Spectrum", FileUtils.getGameSystem("test.tzx"));
		assertEquals("Amstrad CPC", FileUtils.getGameSystem("test.cdt"));
		assertEquals("Neo Geo Pocket", FileUtils.getGameSystem("test.ngp"));
		assertEquals("Neo Geo Pocket Color", FileUtils.getGameSystem("test.ngc"));
	}

}
