package com.wave.hextractor.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * The Class GBChecksumUtils.
 */
public class GBChecksumUtils {

	/** The Constant GAMEBOY_HEADER_CHECKSUM_LOCATION. */
	private static final int GAMEBOY_HEADER_CHECKSUM_LOCATION = 0x14D;

	/** The Constant GAMEBOY_HEADER_CHECKSUM_START_CALCULATION. */
	private static final int GAMEBOY_HEADER_CHECKSUM_START_CALCULATION = 0x134;

	/** The Constant GAMEBOY_HEADER_CHECKSUM_END_CALCULATION. */
	private static final int GAMEBOY_HEADER_CHECKSUM_END_CALCULATION = 0x14C;

	/** The Constant GAMEBOY_ROM_CHECKSUM_LOCATION. */
	private static final int GAMEBOY_ROM_CHECKSUM_LOCATION = 0x14E;

	/** The Constant GAMEBOY_ROM_CHECKSUM_START_CALCULATION. */
	private static final int GAMEBOY_ROM_CHECKSUM_START_CALCULATION = 0x000;

	/**
	 * Calculate game boy header checksum.
	 *
	 * @param fileBytes the file bytes
	 * @return the int
	 */
	public static int calculateGameBoyHeaderChecksum(byte[] fileBytes) {
		byte calculatedHeaderChecksum = 0;
		for (int i = GAMEBOY_HEADER_CHECKSUM_START_CALCULATION; i <= GAMEBOY_HEADER_CHECKSUM_END_CALCULATION; i++) {
			calculatedHeaderChecksum -= fileBytes[i];
			calculatedHeaderChecksum -= 1;
		}
		return calculatedHeaderChecksum & Constants.MASK_8BIT;
	}

	/**
	 * Calculate game boy rom checksum.
	 *
	 * @param fileBytes the file bytes
	 * @return the int
	 */
	public static int calculateGameBoyRomChecksum(byte[] fileBytes) {
		int calculatedRomChecksum = 0;
		for (int i = GAMEBOY_ROM_CHECKSUM_START_CALCULATION; i < fileBytes.length; i++) {
			// Ignore checksum bytes to calculate checksum
			if (i < GAMEBOY_ROM_CHECKSUM_LOCATION || i > GAMEBOY_ROM_CHECKSUM_LOCATION + 1) {
				calculatedRomChecksum += fileBytes[i] & Constants.MASK_8BIT;
				calculatedRomChecksum &= Constants.MASK_16BIT;
			}
		}
		return calculatedRomChecksum;
	}

	/**
	 * Fixes the header checksum and the rom checksum of the game boy file</br>
	 * (if needed).
	 *
	 * @param inputFile file to update.
	 * @throws IOException the exception
	 */
	public static void checkUpdateGameBoyChecksum(String inputFile) throws IOException {
		byte[] fileBytes = Files.readAllBytes(Paths.get(inputFile));
		Utils.log("Fixing Game Boy checksum for \"" + inputFile + "\".");
		boolean checksumModified = false;
		// HEADER CHECKSUM
		int headerChecksum = getGameBoyHeaderChecksum(fileBytes);
		Utils.log("Original Header checksum: 0x"
				+ Integer.toHexString(headerChecksum & Constants.MASK_8BIT).toUpperCase());
		int calculatedHeaderChecksum = calculateGameBoyHeaderChecksum(fileBytes);
		Utils.log("Calculated Header checksum: 0x"
				+ Integer.toHexString(calculatedHeaderChecksum & Constants.MASK_8BIT).toUpperCase());
		if ((calculatedHeaderChecksum & Constants.MASK_8BIT) != (headerChecksum & Constants.MASK_8BIT)) {
			Utils.log("Updating calculated rom header checksum.");
			updateGameBoyHeaderChecksum(calculatedHeaderChecksum, fileBytes);
			checksumModified = true;
		} else {
			Utils.log("Rom header Checksum correct, not overwriting it.");
		}

		// ROM CHECKSUM
		int romChecksum = getGameBoyRomChecksum(fileBytes);
		Utils.log("Original ROM checksum: 0x" + Integer.toHexString(romChecksum).toUpperCase());
		int calculatedRomChecksum = calculateGameBoyRomChecksum(fileBytes);
		Utils.log("Calculated ROM checksum: 0x" + Integer.toHexString(calculatedRomChecksum).toUpperCase());
		if (calculatedRomChecksum != romChecksum) {
			Utils.log("Updating calculated rom checksum.");
			updateGameBoyRomChecksum(calculatedRomChecksum, fileBytes);
			checksumModified = true;
		} else {
			Utils.log("Rom Checksum correct, not overwriting it.");
		}
		if (checksumModified) {
			Utils.log("Writing file.");
			Files.write(Paths.get(inputFile), fileBytes);
		}
	}

	/**
	 * Gets the game boy header checksum.
	 *
	 * @param fileBytes the file bytes
	 * @return the game boy header checksum
	 */
	public static int getGameBoyHeaderChecksum(byte[] fileBytes) {
		return fileBytes[GAMEBOY_HEADER_CHECKSUM_LOCATION] & Constants.MASK_8BIT;
	}

	/**
	 * Gets the game boy rom checksum.
	 *
	 * @param fileBytes the file bytes
	 * @return the game boy rom checksum
	 */
	public static int getGameBoyRomChecksum(byte[] fileBytes) {
		return Utils.bytesToInt(fileBytes[GAMEBOY_ROM_CHECKSUM_LOCATION],
				fileBytes[GAMEBOY_ROM_CHECKSUM_LOCATION + 1]) & Constants.MASK_16BIT;
	}

	/**
	 * Update game boy header checksum.
	 *
	 * @param calculatedHeaderChecksum the calculated header checksum
	 * @param fileBytes the file bytes
	 */
	public static void updateGameBoyHeaderChecksum(int calculatedHeaderChecksum, byte[] fileBytes) {
		byte[] bytes = Utils.intToByteArray(calculatedHeaderChecksum);
		fileBytes[GAMEBOY_HEADER_CHECKSUM_LOCATION] = bytes[3];
	}

	/**
	 * Update game boy rom checksum.
	 *
	 * @param calculatedRomChecksum the calculated rom checksum
	 * @param fileBytes the file bytes
	 */
	public static void updateGameBoyRomChecksum(int calculatedRomChecksum, byte[] fileBytes) {
		byte[] bytes = Utils.intToByteArray(calculatedRomChecksum);
		fileBytes[GAMEBOY_ROM_CHECKSUM_LOCATION + 1] = bytes[3];
		fileBytes[GAMEBOY_ROM_CHECKSUM_LOCATION] = bytes[2];
	}

	/**
	 * Instantiates a new GB checksum utils.
	 */
	protected GBChecksumUtils() {
	}
}
