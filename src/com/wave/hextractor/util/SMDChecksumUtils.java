package com.wave.hextractor.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * The Class SMDChecksumUtils.
 */
public class SMDChecksumUtils {

	/** The Constant MEGADRIVE_CHECKSUM_LOCATION. */
	public static final int MEGADRIVE_CHECKSUM_LOCATION = 0x18E;

	/** The Constant MEGADRIVE_CHECKSUM_START_CALCULATION. */
	public static final int MEGADRIVE_CHECKSUM_START_CALCULATION = 0x200;

	/**
	 * Instantiates a new SMD checksum utils.
	 */
	private SMDChecksumUtils() {
	}

	/**
	 * Fixes the megadrive rom checksum.
	 *
	 * @param inputFile megadrive rom path.
	 * @throws IOException the exception
	 */
	public static void checkUpdateMegaDriveChecksum(String inputFile) throws IOException {
		byte[] fileBytes = Files.readAllBytes(Paths.get(inputFile));
		Utils.log("Fixing Megadrive checksum for \"" + inputFile + "\".");
		if (checkUpdateMegaDriveChecksum(fileBytes)) {
			FileUtils.writeFileBytes(inputFile, fileBytes);
		}
	}

	/**
	 * Fixes the megadrive rom checksum.
	 *
	 * @param fileBytes rom bytes.
	 * @return true if bytes were modified, false otherwise.
	 */
	public static boolean checkUpdateMegaDriveChecksum(byte[] fileBytes) {
		int checksum = getMegaDriveChecksum(fileBytes);
		Utils.log("Original checksum: 0x" + Integer.toHexString(checksum).toUpperCase());
		int calculatedChecksum = calculateMegaDriveChecksum(fileBytes);
		Utils.log("Calculated checksum: 0x" + Integer.toHexString(calculatedChecksum).toUpperCase());
		boolean res = false;
		if (calculatedChecksum != checksum) {
			res = true;
			Utils.log("Updating calculated checksum.");
			updateMegadriveChecksum(calculatedChecksum, fileBytes);
		} else {
			Utils.log("Checksum correct, not overwriting it.");
		}
		return res;
	}

	/**
	 * Gets the mega drive checksum.
	 *
	 * @param fileBytes the file bytes
	 * @return the mega drive checksum
	 */
	private static int getMegaDriveChecksum(byte[] fileBytes) {
		return Utils.bytesToInt(fileBytes[MEGADRIVE_CHECKSUM_LOCATION],
				fileBytes[MEGADRIVE_CHECKSUM_LOCATION + 1]) & Constants.MASK_16BIT;
	}

	/**
	 * Calculate mega drive checksum.
	 *
	 * @param fileBytes the file bytes
	 * @return the int
	 */
	private static int calculateMegaDriveChecksum(byte[] fileBytes) {
		int calculatedChecksum = 0;
		for (int i = MEGADRIVE_CHECKSUM_START_CALCULATION; i < fileBytes.length; i += 2) {
			calculatedChecksum += Utils.bytesToInt(fileBytes[i], fileBytes[i + 1]);
			calculatedChecksum &= Constants.MASK_16BIT;
		}
		return calculatedChecksum;
	}

	/**
	 * Update megadrive checksum.
	 *
	 * @param calculatedChecksum the calculated checksum
	 * @param fileBytes the file bytes
	 */
	private static void updateMegadriveChecksum(int calculatedChecksum, byte[] fileBytes) {
		byte[] bytes = Utils.intToByteArray(calculatedChecksum);
		fileBytes[MEGADRIVE_CHECKSUM_LOCATION] = bytes[2];
		fileBytes[MEGADRIVE_CHECKSUM_LOCATION + 1] = bytes[3];
	}
}
