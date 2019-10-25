package com.wave.hextractor.util;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

/**
 * The Class SNESChecksumUtils.
 */
public class SNESChecksumUtils {

	/** The Constant SNES_CHECKSUM_BYTES. */
	public static final int SNES_CHECKSUM_BYTES = 2;

	/** The Constant SNES_CHECKSUMNOT_HEADER_OFF. */
	public static final int SNES_CHECKSUMNOT_HEADER_OFF = 44;

	/** The Constant SNES_CHECKSUM_HEADER_OFF. */
	public static final int SNES_CHECKSUM_HEADER_OFF = SNES_CHECKSUMNOT_HEADER_OFF + SNES_CHECKSUM_BYTES;

	/** The Constant SNES_ROMNAME_HEADER_OFF. */
	public static final int SNES_ROMNAME_HEADER_OFF = 16;

	/** The Constant SNES_ROMNAME_HEADER_LENGTH. */
	public static final int SNES_ROMNAME_HEADER_LENGTH = 21;

	/** The Constant SNES_ROMNAME_MAP_MODE_OFF. */
	public static final int SNES_ROMNAME_MAP_MODE_OFF = 37;

	/** The Constant SNES_ROMSIZE_HEADER_OFF. */
	public static final int SNES_ROMSIZE_HEADER_OFF = 39;

	/** The Constant SNES_LOROM_HEADER_OFF. */
	public static final int SNES_LOROM_HEADER_OFF = 0x7FB0;

	/** The Constant SNES_HIROM_OFFSET. */
	public static final int SNES_HIROM_OFFSET = 0x8000;

	/** The Constant SNES_INITIAL_CHECKSUM. */
	public static final int SNES_INITIAL_CHECKSUM = 0;

	/** The Constant SNES_INITIAL_CHECKSUMNOT. */
	public static final int SNES_INITIAL_CHECKSUMNOT = 0xFFFF;

	/** The Constant SNES_HEADER_EMPTY. */
	public static final int SNES_HEADER_EMPTY = 0;

	/** The Constant SNES_ROM_SIZE_1MBIT. */
	public static final int SNES_ROM_SIZE_1MBIT = 131072;

	/** The Constant SNES_ROM_SIZE_PAD. */
	public static final int SNES_ROM_SIZE_PAD = SNES_ROM_SIZE_1MBIT * 8;

	/** The Constant SNES_SMC_HEADER_SIZE. */
	public static final int SNES_SMC_HEADER_SIZE = 0x200;

	/** The Constant SNES_INT_HEADER_LEN. */
	public static final int SNES_INT_HEADER_LEN = 80;

	/** The Constant SNES_HIROM_BIT. */
	public static final int SNES_HIROM_BIT = 1;

	/** The Constant SNES_HEADER_SIZE_CHUNKS. */
	public static final int SNES_HEADER_SIZE_CHUNKS = 0x400;

	/** The Constant SNES_HEADER_NAME_MIN_CHAR. */
	public static final int SNES_HEADER_NAME_MIN_CHAR = 0x1F;

	/** The Constant SNES_HEADER_NAME_MAX_CHAR. */
	public static final int SNES_HEADER_NAME_MAX_CHAR = 0x7F;

	/** The Constant SNES_03_04MBIT. */
	public static final int SNES_03_04MBIT = 0x09;

	/** The Constant SNES_05_08MBIT. */
	public static final int SNES_05_08MBIT = 0x0A;

	/** The Constant SNES_09_16MBIT. */
	public static final int SNES_09_16MBIT = 0x0B;

	/** The Constant SNES_17_32MBIT. */
	public static final int SNES_17_32MBIT = 0x0C;

	/** The Constant SNES_33_64MBIT. */
	public static final int SNES_33_64MBIT = 0x0D;

	/** The Constant SNES_03_04MBIT_SIZE. */
	public static final int SNES_03_04MBIT_SIZE = 4 * SNES_ROM_SIZE_1MBIT;

	/** The Constant SNES_05_08MBIT_SIZE. */
	public static final int SNES_05_08MBIT_SIZE = 8 * SNES_ROM_SIZE_1MBIT;

	/** The Constant SNES_09_16MBIT_SIZE. */
	public static final int SNES_09_16MBIT_SIZE = 16 * SNES_ROM_SIZE_1MBIT;

	/** The Constant SNES_17_32MBIT_SIZE. */
	public static final int SNES_17_32MBIT_SIZE = 32 * SNES_ROM_SIZE_1MBIT;

	/** The Constant SNES_33_64MBIT_SIZE. */
	public static final int SNES_33_64MBIT_SIZE = 64 * SNES_ROM_SIZE_1MBIT;

	/**
	 * Instantiates a new SNES checksum utils.
	 */
	private SNESChecksumUtils() {
	}

	/**
	 * Update SNES rom checksum.
	 *
	 * @param inputFile the input file
	 * @throws IOException the exception
	 */
	public static void checkUpdateSnesChecksum(String inputFile) throws IOException {
		Utils.log("Fixing SNES checksum for \"" + inputFile + "\".");
		byte[] fileBytesRaw = Files.readAllBytes(Paths.get(inputFile));
		byte[] fileBytes = getNoSmcHeaderFixedLengthSnesRom(fileBytesRaw);
		boolean isHiRom = isSnesRomHiRom(fileBytes);
		byte[] internalHeader = getSnesInternalHeader(fileBytes, isHiRom);

		if (isHiRom) {
			Utils.log("Detected HIROM");
		} else {
			Utils.log("Detected LOROM");
		}

		int origChecksum = getSnesRomChecksum(internalHeader);
		int origChecksumNot = getSnesRomChecksumNot(internalHeader);
		int calcCheck = calculateSnesRomChecksum(fileBytes, isHiRom);
		int calcChecksumNot = calcCheck ^ Constants.MASK_16BIT;

		Utils.log("ROM checksum           : " + Utils.getHexFilledLeft(origChecksum, Constants.HEXSIZE_16BIT_VALUE));
		Utils.log("ROM checksum not       : " + Utils.getHexFilledLeft(origChecksumNot, Constants.HEXSIZE_16BIT_VALUE));
		Utils.log("Calculated checksum    : " + Utils.getHexFilledLeft(calcCheck, Constants.HEXSIZE_16BIT_VALUE));
		Utils.log("Calculated checksum not: " + Utils.getHexFilledLeft(calcChecksumNot, Constants.HEXSIZE_16BIT_VALUE));

		if (calcCheck != origChecksum || origChecksumNot != calcChecksumNot) {
			if (calcCheck != origChecksum) {
				Utils.log("Updating checksum.");
			}
			if (origChecksumNot != calcChecksumNot) {
				Utils.log("Updating checksumNot.");
			}
			updateSnesChecksum(internalHeader, calcCheck, calcChecksumNot);
			int off = SNES_LOROM_HEADER_OFF;
			if (isHiRom) {
				off += SNES_HIROM_OFFSET;
			}
			if (fileBytesRaw.length % SNES_ROM_SIZE_1MBIT == SNES_SMC_HEADER_SIZE) {
				off += SNES_SMC_HEADER_SIZE;
			}
			System.arraycopy(internalHeader, 0, fileBytesRaw, off, SNES_INT_HEADER_LEN);
			Files.write(Paths.get(inputFile), fileBytesRaw);
		} else {
			Utils.log("Checksum correct, not overwriting it.");
		}
	}

	//////////// PRIVATE METHODS////////////////

	/**
	 * Gets the snes internal header.
	 *
	 * @param fileBytes the file bytes
	 * @param isHiRom the is hi rom
	 * @return the snes internal header
	 */
	private static byte[] getSnesInternalHeader(byte[] fileBytes, boolean isHiRom) {
		int off = SNES_LOROM_HEADER_OFF;
		if (isHiRom) {
			off += SNES_HIROM_OFFSET;
		}
		return Arrays.copyOfRange(fileBytes, off, off + SNES_INT_HEADER_LEN);
	}

	/**
	 * Removes header and expands rom to internal definition maximum if necessary.
	 *
	 * @param fileBytes .
	 * @return cleanedrom
	 */
	private static byte[] getNoSmcHeaderFixedLengthSnesRom(byte[] fileBytes) {
		byte[] cleanedRom;
		int headerSize = fileBytes.length % SNES_ROM_SIZE_1MBIT;
		int romSize = fileBytes.length / SNES_ROM_SIZE_1MBIT * SNES_ROM_SIZE_1MBIT;

		if (headerSize != 0 && headerSize != SNES_SMC_HEADER_SIZE) {
			throw new IllegalArgumentException("Invalid header size / Tamaño header inválido: " + headerSize);
		}
		Utils.log("ROM size: " + romSize / SNES_ROM_SIZE_1MBIT + " Mbit");
		if (headerSize > 0) {
			Utils.log("WITH SMC header");
		} else {
			Utils.log("With NO SMC header");
		}
		int cleanedRomSize = getCleanedRomSize(romSize);
		cleanedRom = new byte[cleanedRomSize];
		// Copy original rom info
		System.arraycopy(fileBytes, headerSize, cleanedRom, 0, romSize);
		if (romSize != cleanedRomSize) {
			Utils.log("Expanding rom to " + cleanedRomSize / SNES_ROM_SIZE_1MBIT + " Mbit");
			// clone last chunk
			System.arraycopy(cleanedRom, romSize - (cleanedRomSize - romSize), cleanedRom, romSize,
					cleanedRomSize - romSize);
		}
		return cleanedRom;
	}

	private static int getCleanedRomSize(int romSize) {
		int cleanedRomSize;
		if (romSize > SNES_03_04MBIT_SIZE) {
			if (romSize > SNES_05_08MBIT_SIZE) {
				cleanedRomSize = getCleanedRomSizeLarge(romSize);
			} else {
				cleanedRomSize = SNES_05_08MBIT_SIZE;
			}
		} else {
			cleanedRomSize = SNES_03_04MBIT_SIZE;
		}
		return cleanedRomSize;
	}

	private static int getCleanedRomSizeLarge(int romSize) {
		int cleanedRomSize;
		if (romSize > SNES_09_16MBIT_SIZE) {
			if (romSize > SNES_17_32MBIT_SIZE) {
				if (romSize > SNES_33_64MBIT_SIZE) {
					throw new IllegalArgumentException("Rom size too large!!!!");
				} else {
					cleanedRomSize = SNES_33_64MBIT_SIZE;
				}
			} else {
				cleanedRomSize = SNES_17_32MBIT_SIZE;
			}
		} else {
			cleanedRomSize = SNES_09_16MBIT_SIZE;
		}
		return cleanedRomSize;
	}

	/**
	 * Update snes checksum.
	 *
	 * @param header the header
	 * @param check the check
	 * @param checksumNot the checksum not
	 */
	private static void updateSnesChecksum(byte[] header, int check, int checksumNot) {
		byte[] bytesNot = Utils.intToByteArray(checksumNot);
		header[SNES_CHECKSUMNOT_HEADER_OFF] = bytesNot[3];
		header[SNES_CHECKSUMNOT_HEADER_OFF + 1] = bytesNot[2];
		byte[] bytes = Utils.intToByteArray(check);
		header[SNES_CHECKSUM_HEADER_OFF] = bytes[3];
		header[SNES_CHECKSUM_HEADER_OFF + 1] = bytes[2];
	}

	/**
	 * Gets the snes rom checksum not.
	 *
	 * @param header the header
	 * @return the snes rom checksum not
	 */
	private static int getSnesRomChecksumNot(byte[] header) {
		return Utils.bytesToInt(header[SNES_CHECKSUMNOT_HEADER_OFF + 1],
				header[SNES_CHECKSUMNOT_HEADER_OFF]) & Constants.MASK_16BIT;
	}

	/**
	 * Calculate snes rom checksum.
	 *
	 * @param fileBytes the file bytes
	 * @param isHiRom the is hi rom
	 * @return the int
	 */
	private static int calculateSnesRomChecksum(byte[] fileBytes, boolean isHiRom) {
		byte[] checkRom = Arrays.copyOf(fileBytes, fileBytes.length);
		// Limpiamos checksum y negado
		byte[] header = getSnesInternalHeader(checkRom, isHiRom);
		updateSnesChecksum(header, SNES_INITIAL_CHECKSUM, SNES_INITIAL_CHECKSUMNOT);
		int destPos = SNES_LOROM_HEADER_OFF;
		if (isHiRom) {
			destPos += SNES_HIROM_OFFSET;
		}
		System.arraycopy(header, 0, checkRom, destPos, header.length);

		// Calcular checksum
		int checksum = 0;
		for (byte b : checkRom) {
			checksum += b & Constants.MASK_8BIT;
		}
		return checksum & Constants.MASK_16BIT;
	}

	/**
	 * Checks if is snes rom hi rom.
	 *
	 * @param fileBytes the file bytes
	 * @return true, if is snes rom hi rom
	 */
	private static boolean isSnesRomHiRom(byte[] fileBytes) {
		boolean isLoRom ;
		boolean isHiRom = false;

		byte[] header = Arrays.copyOfRange(fileBytes, SNES_LOROM_HEADER_OFF,
				SNES_LOROM_HEADER_OFF + SNES_INT_HEADER_LEN);
		isLoRom = validateSnesHeader(header, fileBytes.length, false);
		if (!isLoRom) {
			header = Arrays.copyOfRange(fileBytes, SNES_LOROM_HEADER_OFF + SNES_HIROM_OFFSET,
					SNES_LOROM_HEADER_OFF + SNES_HIROM_OFFSET + SNES_INT_HEADER_LEN);
			isHiRom = validateSnesHeader(header, fileBytes.length, true);
		}
		if (!isLoRom && !isHiRom) {
			throw new IllegalArgumentException("Could not determine ROM type / No puedo determinarse el tipo de ROM");
		}
		return isHiRom;
	}

	/**
	 * Validate snes header.
	 *
	 * @param header the header
	 * @param length the length
	 * @param isHiRom the is hi rom
	 * @return true, if successful
	 */
	private static boolean validateSnesHeader(byte[] header, int length, boolean isHiRom) {
		boolean res = false;
		if (isValidSnesName(Arrays.copyOfRange(header, SNES_ROMNAME_HEADER_OFF,
				SNES_ROMNAME_HEADER_OFF + SNES_ROMNAME_HEADER_LENGTH))) {
			// 2.Verificamos que el map mode marque lo que toca
			int romType = header[SNES_ROMNAME_MAP_MODE_OFF] & Constants.MASK_8BIT;
			if ((romType & SNES_HIROM_BIT) == 0 && !isHiRom
					|| (romType & SNES_HIROM_BIT) == SNES_HIROM_BIT && isHiRom) {
				res = true;
				// 3.Verificamos que el tamaño especificado sea correcto
				int snesBanks = header[SNES_ROMSIZE_HEADER_OFF] & Constants.MASK_8BIT;
				int snesSize = SNES_HEADER_SIZE_CHUNKS << snesBanks;

				int minSize = 0;
				int maxSize = 0;
				switch (snesBanks) {
				case SNES_03_04MBIT:
					minSize = SNES_03_04MBIT_SIZE - SNES_ROM_SIZE_1MBIT;
					maxSize = SNES_03_04MBIT_SIZE;
					break;
				case SNES_05_08MBIT:
					minSize = SNES_03_04MBIT_SIZE + SNES_ROM_SIZE_1MBIT;
					maxSize = SNES_05_08MBIT_SIZE;
					break;
				case SNES_09_16MBIT:
					minSize = SNES_05_08MBIT_SIZE + SNES_ROM_SIZE_1MBIT;
					maxSize = SNES_09_16MBIT_SIZE;
					break;
				case SNES_17_32MBIT:
					minSize = SNES_09_16MBIT_SIZE + SNES_ROM_SIZE_1MBIT;
					maxSize = SNES_17_32MBIT_SIZE;
					break;
				case SNES_33_64MBIT:
					minSize = SNES_17_32MBIT_SIZE + SNES_ROM_SIZE_1MBIT;
					maxSize = SNES_33_64MBIT_SIZE;
					break;
				default:
					Utils.log("Invalid ROM size: " + snesSize);
					res = false;
				}
				logRomResults(length, minSize, maxSize, res);
			}
		}
		return res;
	}

	private static void logRomResults(int length, int minSize, int maxSize, boolean res) {
		if (length < minSize || length > maxSize) {
			Utils.log("Size mismatch, File (Expanded): " + length / SNES_ROM_SIZE_1MBIT
					+ " MBit, ROM info: " + minSize / SNES_ROM_SIZE_1MBIT + " - "
					+ maxSize / SNES_ROM_SIZE_1MBIT + " MBit");
		}
		if (res) {
			Utils.log("Size correct, File (Expanded): " + length / SNES_ROM_SIZE_1MBIT
					+ " MBit, ROM info: " + minSize / SNES_ROM_SIZE_1MBIT + " - "
					+ maxSize / SNES_ROM_SIZE_1MBIT + " MBit");
		}
	}

	/**
	 * Checks if is valid snes name.
	 *
	 * @param asciiName the ascii name
	 * @return true, if is valid snes name
	 */
	private static boolean isValidSnesName(byte[] asciiName) {
		// Verificamos que su nombre cumple que usa bytes van de (1F-7F?)
		boolean res = true;
		for (byte b : asciiName) {
			int num = b & Constants.MASK_8BIT;
			if (num <= SNES_HEADER_NAME_MIN_CHAR /* || num >= Constants.SNES_HEADER_NAME_MAX_CHAR */) {
				res = false;
				break;
			}
		}
		if (res) {
			Utils.log("ROM NAME: \"" + new String(asciiName, StandardCharsets.US_ASCII) + "\"");
		}
		return res;
	}

	/**
	 * Gets the snes rom checksum.
	 *
	 * @param header the header
	 * @return the snes rom checksum
	 */
	private static int getSnesRomChecksum(byte[] header) {
		return Utils.bytesToInt(header[SNES_CHECKSUM_HEADER_OFF + 1],
				header[SNES_CHECKSUM_HEADER_OFF]) & Constants.MASK_16BIT;
	}
}
