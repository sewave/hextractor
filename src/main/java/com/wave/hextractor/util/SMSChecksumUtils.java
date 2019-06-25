package com.wave.hextractor.util;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

/**
 * The Class SMSChecksumUtils.
 */
public class SMSChecksumUtils {

	/** The Constant SMS_HEADER_START. */
	private static final String SMS_HEADER_START = "TMR SEGA";

	/** The Constant SMS_HEADER_LOCATION. */
	private static final int SMS_HEADER_LOCATION = 0x7FF0;

	/** The Constant SMS_HEADER_SIZE. */
	private static final int SMS_HEADER_SIZE = 0x10;

	/** The Constant SMS_HEADER_COUNTRY_CHECKSUMRANGE_OFFSET. */
	private static final int SMS_HEADER_COUNTRY_CHECKSUMRANGE_OFFSET = 0xF;

	/** The Constant SMS_HEADER_CHECKSUM_OFFSET. */
	private static final int SMS_HEADER_CHECKSUM_OFFSET = 0xA;

	/** The Constant HIGH_NIBBLE_SHIFTS. */
	private static final int HIGH_NIBBLE_SHIFTS = 4;

	/** The Constant SMS_HEADER_COUNTRY_OVERSEAS. */
	private static final int SMS_HEADER_COUNTRY_OVERSEAS = 0x4;

	/** The Constant SMS_CHECKSUM_BYTES. */
	private static final int SMS_CHECKSUM_BYTES = 2;

	/**
	 * Calculated SMS checksum.
	 *
	 * @param file the file
	 * @return the short
	 */
	private static short calculatedSMSChecksum(byte[] file) {
		short checksum = 0;
		for (int i = 0; i < SMS_HEADER_LOCATION; i++) {
			checksum += file[i] & Constants.MASK_8BIT;
		}
		for (int i = SMS_HEADER_LOCATION + SMS_HEADER_SIZE; i < file.length; i++) {
			checksum += file[i] & Constants.MASK_8BIT;
		}
		return checksum;
	}

	/**
	 * Calculate SMS checksum.
	 *
	 * @param file the file
	 * @param updateChecksum the update checksum
	 */
	private static void calculateSMSChecksum(byte[] file, boolean updateChecksum) {
		boolean isSmsOv = isSMSOverseasRom(file);
		Utils.log("Is SMS overseas ROM? " + isSmsOv);
		if (isSmsOv) {
			short checksumRom = getSMSChecksum(file);
			Utils.log(String.format("SMS ROM Checksum: 0x%04X", checksumRom));
			short checksumCalulated = calculatedSMSChecksum(file);
			Utils.log(String.format("SMS SUM Checksum: 0x%04X", checksumCalulated));
			if (checksumRom != checksumCalulated) {
				Utils.log("Different SMS checksums.");
				if (updateChecksum) {
					Utils.log(String.format("Updating SMS ROM checksum to 0x%04X", checksumCalulated));
					byte[] checkSumByte = Utils.shortToBytes(checksumCalulated);
					file[SMS_HEADER_LOCATION + SMS_HEADER_CHECKSUM_OFFSET] = checkSumByte[0];
					file[SMS_HEADER_LOCATION + SMS_HEADER_CHECKSUM_OFFSET + 1] = checkSumByte[1];
				}
			}
		}
	}

	/**
	 * Calculate SMS checksum.
	 *
	 * @param file the file
	 * @param updateChecksum the update checksum
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private static void calculateSMSChecksum(String file, boolean updateChecksum) throws IOException {
		byte[] fileBytes = Files.readAllBytes(Paths.get(file));
		calculateSMSChecksum(fileBytes, updateChecksum);
		if (updateChecksum) {
			try (FileOutputStream fos = new FileOutputStream(file)) {
				fos.write(fileBytes);
			}
		}
	}

	/**
	 * Check SMS checksum.
	 *
	 * @param file the file
	 */
	public static void checkSMSChecksum(byte[] file) {
		calculateSMSChecksum(file, false);
	}

	/**
	 * Check SMS checksum.
	 *
	 * @param file the file
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static void checkSMSChecksum(String file) throws IOException {
		calculateSMSChecksum(file, false);
	}

	/**
	 * Check update SMS checksum.
	 *
	 * @param file the file
	 */
	public static void checkUpdateSMSChecksum(byte[] file) {
		calculateSMSChecksum(file, true);
	}

	/**
	 * Check update SMS checksum.
	 *
	 * @param file the file
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static void checkUpdateSMSChecksum(String file) throws IOException {
		calculateSMSChecksum(file, true);
	}

	/**
	 * Gets the SMS checksum.
	 *
	 * @param file the file
	 * @return the SMS checksum
	 */
	private static short getSMSChecksum(byte[] file) {
		return Short.reverseBytes(
				ByteBuffer.wrap(getSMSHeader(file), SMS_HEADER_CHECKSUM_OFFSET, SMS_CHECKSUM_BYTES).getShort());
	}

	/**
	 * Gets the SMS header.
	 *
	 * @param file the file
	 * @return the SMS header
	 */
	private static byte[] getSMSHeader(byte[] file) {
		return Arrays.copyOfRange(file, SMS_HEADER_LOCATION, SMS_HEADER_LOCATION + SMS_HEADER_SIZE);
	}

	/**
	 * Checks if is SMS overseas rom.
	 *
	 * @param file the file
	 * @return true, if is SMS overseas rom
	 */
	private static boolean isSMSOverseasRom(byte[] file) {
		byte[] header = getSMSHeader(file);
		return header[SMS_HEADER_COUNTRY_CHECKSUMRANGE_OFFSET] >> HIGH_NIBBLE_SHIFTS == SMS_HEADER_COUNTRY_OVERSEAS
				&& new String(header).startsWith(SMS_HEADER_START);
	}

	/**
	 * Instantiates a new SMS checksum utils.
	 */
	private SMSChecksumUtils() {
	}
}
