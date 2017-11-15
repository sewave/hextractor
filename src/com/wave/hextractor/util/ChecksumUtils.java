package com.wave.hextractor.util;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Utility class for checksum operations.
 * @author slcantero
 */
public class ChecksumUtils {

	/** The Constant BYTE_SIZE_16_BITS. */
	private static final int BYTE_SIZE_16_BITS = 2;

	/** The Constant TZX_HEADER_SIZE. */
	private static final int TZX_HEADER_SIZE = 0x0A;

	/** The Constant TZX_ID_STANDARD_DATA. */
	private static final byte TZX_ID_STANDARD_DATA = 0x10;

	/** The Constant TZX_ID_TURBO_DATA. */
	private static final byte TZX_ID_TURBO_DATA = 0x11;

	/** The Constant TZX_ID_GEN_DATA. */
	private static final byte TZX_ID_GEN_DATA = 0x19;

	/** The Constant TZX_ID_PURE_DATA. */
	private static final byte TZX_ID_PURE_DATA = 0x14;

	/** The Constant TZX_ID_PURE_TONE. */
	private static final byte TZX_ID_PURE_TONE = 0x12;

	/** The Constant TZX_ID_SEQ_PULSES. */
	private static final byte TZX_ID_SEQ_PULSES = 0x13;

	/** The Constant TZX_ID_DIRECT_REC. */
	private static final byte TZX_ID_DIRECT_REC = 0x15;

	/** The Constant TZX_ID_CSW_REC. */
	private static final byte TZX_ID_CSW_REC = 0x18;

	/** The Constant TZX_ID_PAUSE_STOP. */
	private static final byte TZX_ID_PAUSE_STOP = 0x20;

	/** The Constant TZX_ID_GROUP_START. */
	private static final byte TZX_ID_GROUP_START = 0x21;

	/** The Constant TZX_ID_JUMP_BLOCK. */
	private static final byte TZX_ID_JUMP_BLOCK = 0x23;

	/** The Constant TZX_ID_LOOP_START. */
	private static final byte TZX_ID_LOOP_START = 0x24;

	/** The Constant TZX_ID_CALL_SEQ. */
	private static final byte TZX_ID_CALL_SEQ = 0x26;

	/** The Constant TZX_ID_SEL_BLOCK. */
	private static final byte TZX_ID_SEL_BLOCK = 0x28;

	/** The Constant TZX_ID_STOP_TAPE_48K. */
	private static final byte TZX_ID_STOP_TAPE_48K = 0x2A;

	/** The Constant TZX_ID_SET_SIG_LEV. */
	private static final byte TZX_ID_SET_SIG_LEV = 0x2B;

	/** The Constant TZX_ID_TEXT_DESC. */
	private static final byte TZX_ID_TEXT_DESC = 0x30;

	/** The Constant TZX_ID_MSG_BLOCK. */
	private static final byte TZX_ID_MSG_BLOCK = 0x31;

	/** The Constant TZX_ID_ARCHIVE_INFO. */
	private static final byte TZX_ID_ARCHIVE_INFO = 0x32;

	/** The Constant TZX_ID_HARD_TYPE. */
	private static final byte TZX_ID_HARD_TYPE = 0x33;

	/** The Constant TZX_ID_CUSTOM_INFO_BLOCK. */
	private static final byte TZX_ID_CUSTOM_INFO_BLOCK = 0x35;

	/** The Constant TZX_ID_GLUE_BLOCK. */
	private static final byte TZX_ID_GLUE_BLOCK = 0x5A;

	/** The Constant TZX_ID_PURE_TONE_LEN. */
	private static final int TZX_ID_PURE_TONE_LEN = 0x04;

	/** The Constant TZX_WORD_LEN. */
	private static final int TZX_WORD_LEN = 0x02;

	/** The Constant TZX_BYTE_LEN. */
	private static final int TZX_BYTE_LEN = 0x01;

	/** The Constant TZX_ID_DIRECT_REC_LOFF. */
	private static final int TZX_ID_DIRECT_REC_LOFF = 0x05;

	/** The Constant TZX_ID_PAUSE_STOP_LEN. */
	private static final int TZX_ID_PAUSE_STOP_LEN = 0x02;

	/** The Constant TZX_ID_STOP_TAPE_48K_LEN. */
	private static final int TZX_ID_STOP_TAPE_48K_LEN = 0x04;

	/** The Constant TZX_ID_SET_SIG_LEV_LEN. */
	private static final int TZX_ID_SET_SIG_LEV_LEN = 0x05;

	/** The Constant TZX_ID_GLUE_BLOCK_LEN. */
	private static final int TZX_ID_GLUE_BLOCK_LEN = 0x9;

	/** The Constant DATA_TYPES. */
	private static final List<Byte> DATA_TYPES = Arrays.asList(TZX_ID_STANDARD_DATA, TZX_ID_TURBO_DATA, TZX_ID_PURE_DATA);

	/** The Constant TZX_ID_STANDARD_DATA_DOFF. */
	private static final int TZX_ID_STANDARD_DATA_DOFF = 0x04;

	/** The Constant TZX_ID_STANDARD_DATA_LOFF. */
	private static final int TZX_ID_STANDARD_DATA_LOFF = 0x02;

	/** The Constant TZX_ID_TURBO_DATA_DOFF. */
	private static final int TZX_ID_TURBO_DATA_DOFF = 0x12;

	/** The Constant TZX_ID_TURBO_DATA_LOFF. */
	private static final int TZX_ID_TURBO_DATA_LOFF = 0x0F;

	/** The Constant TZX_ID_PURE_DATA_DOFF. */
	private static final int TZX_ID_PURE_DATA_DOFF = 0x0A;

	/** The Constant TZX_ID_PURE_DATA_LOFF. */
	private static final int TZX_ID_PURE_DATA_LOFF = 0x07;

	/** The Constant TZX_ID_GROUP_END. */
	private static final byte TZX_ID_GROUP_END = 0x22;

	/** The Constant TZX_ID_LOOP_END. */
	private static final byte TZX_ID_LOOP_END = 0x25;

	/** The Constant TZX_ID_RET_SEQ. */
	private static final byte TZX_ID_RET_SEQ = 0x27;

	/**
	 * Fixes the header checksum and the rom checksum of the game boy file</br>
	 * (if needed).
	 *
	 * @param inputFile file to update.
	 * @throws Exception the exception
	 */
	public static void checkUpdateGameBoyChecksum(String inputFile) throws Exception {
		byte[] fileBytes = FileUtils.getFileBytes(inputFile);
		System.out.println("Fixing Game Boy checksum for \"" + inputFile + "\".");
		boolean checksumModified = false;
		// HEADER CHECKSUM
		int headerChecksum = ChecksumUtils.getGameBoyHeaderChecksum(fileBytes);
		System.out.println("Original Header checksum: 0x" + Integer.toHexString(headerChecksum & Constants.MASK_8BIT).toUpperCase());
		int calculatedHeaderChecksum = ChecksumUtils.calculateGameBoyHeaderChecksum(fileBytes);
		System.out.println(
				"Calculated Header checksum: 0x" + Integer.toHexString(calculatedHeaderChecksum & Constants.MASK_8BIT).toUpperCase());
		if ((calculatedHeaderChecksum & Constants.MASK_8BIT) != (headerChecksum & Constants.MASK_8BIT)) {
			System.out.println("Updating calculated rom header checksum.");
			ChecksumUtils.updateGameBoyHeaderChecksum(calculatedHeaderChecksum, fileBytes);
			checksumModified = true;
		} else {
			System.out.println("Rom header Checksum correct, not overwriting it.");
		}

		// ROM CHECKSUM
		int romChecksum = ChecksumUtils.getGameBoyRomChecksum(fileBytes);
		System.out.println("Original ROM checksum: 0x" + Integer.toHexString(romChecksum).toUpperCase());
		int calculatedRomChecksum = ChecksumUtils.calculateGameBoyRomChecksum(fileBytes);
		System.out.println("Calculated ROM checksum: 0x" + Integer.toHexString(calculatedRomChecksum).toUpperCase());
		if (calculatedRomChecksum != romChecksum) {
			System.out.println("Updating calculated rom checksum.");
			ChecksumUtils.updateGameBoyRomChecksum(calculatedRomChecksum, fileBytes);
			checksumModified = true;
		} else {
			System.out.println("Rom Checksum correct, not overwriting it.");
		}
		if (checksumModified) {
			System.out.println("Writing file.");
			FileUtils.writeFileBytes(inputFile, fileBytes);
		}
	}

	/**
	 * Fixes the megadrive rom checksum.
	 *
	 * @param inputFile megadrive rom path.
	 * @throws Exception the exception
	 */
	public static void checkUpdateMegaDriveChecksum(String inputFile) throws Exception {
		byte[] fileBytes = FileUtils.getFileBytes(inputFile);
		System.out.println("Fixing Megadrive checksum for \"" + inputFile + "\".");
		if(checkUpdateMegaDriveChecksum(fileBytes)) {
			FileUtils.writeFileBytes(inputFile, fileBytes);
		}
	}

	/**
	 * Fixes the megadrive rom checksum.
	 *
	 * @param fileBytes rom bytes.
	 * @return true if bytes were modified, false otherwise.
	 * @throws Exception the exception
	 */
	public static boolean checkUpdateMegaDriveChecksum(byte[] fileBytes) throws Exception {
		int checksum = ChecksumUtils.getMegaDriveChecksum(fileBytes);
		System.out.println("Original checksum: 0x" + Integer.toHexString(checksum).toUpperCase());
		int calculatedChecksum = ChecksumUtils.calculateMegaDriveChecksum(fileBytes);
		System.out.println("Calculated checksum: 0x" + Integer.toHexString(calculatedChecksum).toUpperCase());
		boolean res = false;
		if (calculatedChecksum != checksum) {
			res = true;
			System.out.println("Updating calculated checksum.");
			ChecksumUtils.updateMegadriveChecksum(calculatedChecksum, fileBytes);
		} else {
			System.out.println("Checksum correct, not overwriting it.");
		}
		return res;
	}

	/**
	 * Update SNES rom checksum.
	 *
	 * @param inputFile the input file
	 * @throws Exception the exception
	 */
	public static void checkUpdateSnesChecksum(String inputFile) throws Exception {
		System.out.println("Fixing SNES checksum for \"" + inputFile + "\".");
		byte[] fileBytesRaw = FileUtils.getFileBytes(inputFile);
		byte[] fileBytes = getNoSmcHeaderFixedLengthSnesRom(fileBytesRaw);
		boolean isHiRom = isSnesRomHiRom(fileBytes);
		byte[] internalHeader = getSnesInternalHeader(fileBytes, isHiRom);

		if(isHiRom) {
			System.out.println("Detected HIROM");
		}
		else {
			System.out.println("Detected LOROM");
		}

		int origChecksum = getSnesRomChecksum(internalHeader);
		int origChecksumNot = getSnesRomChecksumNot(internalHeader);
		int calcCheck = calculateSnesRomChecksum(fileBytes, isHiRom);
		int calcChecksumNot = calcCheck ^ Constants.MASK_16BIT;

		System.out.println("ROM checksum           : " + Utils.getHexFilledLeft(origChecksum, Constants.HEXSIZE_16BIT_VALUE));
		System.out.println("ROM checksum not       : " + Utils.getHexFilledLeft(origChecksumNot, Constants.HEXSIZE_16BIT_VALUE));
		System.out.println("Calculated checksum    : " + Utils.getHexFilledLeft(calcCheck, Constants.HEXSIZE_16BIT_VALUE));
		System.out.println("Calculated checksum not: " + Utils.getHexFilledLeft(calcChecksumNot, Constants.HEXSIZE_16BIT_VALUE));

		if (calcCheck != origChecksum || origChecksumNot != calcChecksumNot) {
			if(calcCheck != origChecksum) {
				System.out.println("Updating checksum.");
			}
			if(origChecksumNot != calcChecksumNot) {
				System.out.println("Updating checksumNot.");
			}
			updateSnesChecksum(internalHeader, calcCheck, calcChecksumNot);
			int off = Constants.SNES_LOROM_HEADER_OFF;
			if(isHiRom) {
				off += Constants.SNES_HIROM_OFFSET;
			}
			if(fileBytesRaw.length % Constants.SNES_ROM_SIZE_1MBIT == Constants.SNES_SMC_HEADER_SIZE) {
				off += Constants.SNES_SMC_HEADER_SIZE;
			}
			System.arraycopy(internalHeader, 0, fileBytesRaw, off, Constants.SNES_INT_HEADER_LEN);
			FileUtils.writeFileBytes(inputFile, fileBytesRaw);
		} else {
			System.out.println("Checksum correct, not overwriting it.");
		}
	}

	/**
	 * Updates tap checksum .
	 *
	 * @param inputFile the input file
	 * @throws Exception the exception
	 */
	public static void checkUpdateZxTapChecksum(String inputFile) throws Exception {
		checkUpdateZxTapChecksum(inputFile, null);
	}

	/**
	 * Updates tap checksum if original tap has correct checksums.
	 *
	 * @param inputFile the input file
	 * @param originalFile the original file
	 * @throws Exception the exception
	 */
	public static void checkUpdateZxTapChecksum(String inputFile, String originalFile) throws Exception {
		System.out.println("Fixing ZX TAP checksums for \"" + inputFile + "\" ");
		byte[] originalFileBytes = null;
		if(originalFile != null) {
			originalFileBytes = FileUtils.getFileBytes(originalFile);
			System.out.println("With original file \"" + originalFile + "\"");
		}
		else {
			System.out.println();
		}
		FileUtils.writeFileBytes(inputFile, checkUpdateZxTapChecksum(FileUtils.getFileBytes(inputFile), false, originalFileBytes));
	}

	/**
	 * Updates Tzx checksum.
	 *
	 * @param inputFile the input file
	 * @throws Exception the exception
	 */
	public static void checkUpdateZxTzxChecksum(String inputFile) throws Exception {
		checkUpdateZxTzxChecksum(inputFile, null);
	}

	/**
	 * Updates Tzx checksum if original file has good checksums.
	 *
	 * @param inputFile the input file
	 * @param originalFile the original file
	 * @throws Exception the exception
	 */
	public static void checkUpdateZxTzxChecksum(String inputFile, String originalFile) throws Exception {
		System.out.print("Fixing ZX TZX checksums for \"" + inputFile + "\" ");
		byte[] originalFileBytes = null;
		if(originalFile != null) {
			originalFileBytes = FileUtils.getFileBytes(originalFile);
			System.out.println("With original file \"" + originalFile + "\"");
		}
		else {
			System.out.println();
		}
		FileUtils.writeFileBytes(inputFile, checkUpdateZxTapChecksum(FileUtils.getFileBytes(inputFile), true, originalFileBytes));
	}

	////////////PRIVATE METHODS////////////////

	/**
	 * Gets the game boy rom checksum.
	 *
	 * @param fileBytes the file bytes
	 * @return the game boy rom checksum
	 */
	private static int getGameBoyRomChecksum(byte[] fileBytes) {
		return Utils.bytesToInt(fileBytes[Constants.GAMEBOY_ROM_CHECKSUM_LOCATION],
				fileBytes[Constants.GAMEBOY_ROM_CHECKSUM_LOCATION+1]) & Constants.MASK_16BIT;
	}

	/**
	 * Gets the mega drive checksum.
	 *
	 * @param fileBytes the file bytes
	 * @return the mega drive checksum
	 */
	private static int getMegaDriveChecksum(byte[] fileBytes) {
		return Utils.bytesToInt(fileBytes[Constants.MEGADRIVE_CHECKSUM_LOCATION],
				fileBytes[Constants.MEGADRIVE_CHECKSUM_LOCATION+1]) & Constants.MASK_16BIT;
	}

	/**
	 * Calculate mega drive checksum.
	 *
	 * @param fileBytes the file bytes
	 * @return the int
	 */
	private static int calculateMegaDriveChecksum(byte[] fileBytes) {
		int calculatedChecksum = 0;
		for(int i = Constants.MEGADRIVE_CHECKSUM_START_CALCULATION; i < fileBytes.length; i+=2) {
			calculatedChecksum += Utils.bytesToInt(fileBytes[i],
					fileBytes[i+1]);
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
		fileBytes[Constants.MEGADRIVE_CHECKSUM_LOCATION] = bytes[2];
		fileBytes[Constants.MEGADRIVE_CHECKSUM_LOCATION+1] = bytes[3];
	}

	/**
	 * Calculate game boy rom checksum.
	 *
	 * @param fileBytes the file bytes
	 * @return the int
	 */
	private static int calculateGameBoyRomChecksum(byte[] fileBytes) {
		int calculatedRomChecksum = 0;
		for(int i = Constants.GAMEBOY_ROM_CHECKSUM_START_CALCULATION; i < fileBytes.length; i++) {
			//Ignore checksum bytes to calculate checksum
			if(i < Constants.GAMEBOY_ROM_CHECKSUM_LOCATION || i > Constants.GAMEBOY_ROM_CHECKSUM_LOCATION +1) {
				calculatedRomChecksum += (fileBytes[i] & Constants.MASK_8BIT);
				calculatedRomChecksum &= Constants.MASK_16BIT;
			}
		}
		return calculatedRomChecksum;
	}

	/**
	 * Update game boy rom checksum.
	 *
	 * @param calculatedRomChecksum the calculated rom checksum
	 * @param fileBytes the file bytes
	 */
	private static void updateGameBoyRomChecksum(int calculatedRomChecksum, byte[] fileBytes) {
		byte[] bytes = Utils.intToByteArray(calculatedRomChecksum);
		fileBytes[Constants.GAMEBOY_ROM_CHECKSUM_LOCATION+1] = bytes[3];
		fileBytes[Constants.GAMEBOY_ROM_CHECKSUM_LOCATION] = bytes[2];
	}

	/**
	 * Gets the game boy header checksum.
	 *
	 * @param fileBytes the file bytes
	 * @return the game boy header checksum
	 */
	private static int getGameBoyHeaderChecksum(byte[] fileBytes) {
		return (fileBytes[Constants.GAMEBOY_HEADER_CHECKSUM_LOCATION] & Constants.MASK_8BIT);
	}

	/**
	 * Calculate game boy header checksum.
	 *
	 * @param fileBytes the file bytes
	 * @return the int
	 */
	private static int calculateGameBoyHeaderChecksum(byte[] fileBytes) {
		byte calculatedHeaderChecksum = 0;
		for(int i = Constants.GAMEBOY_HEADER_CHECKSUM_START_CALCULATION; i <= Constants.GAMEBOY_HEADER_CHECKSUM_END_CALCULATION; i++) {
			calculatedHeaderChecksum -= fileBytes[i];
			calculatedHeaderChecksum -= 1;
		}
		return calculatedHeaderChecksum;
	}

	/**
	 * Update game boy header checksum.
	 *
	 * @param calculatedHeaderChecksum the calculated header checksum
	 * @param fileBytes the file bytes
	 */
	private static void updateGameBoyHeaderChecksum(int calculatedHeaderChecksum, byte[] fileBytes) {
		byte[] bytes = Utils.intToByteArray(calculatedHeaderChecksum);
		fileBytes[Constants.GAMEBOY_HEADER_CHECKSUM_LOCATION] = bytes[3];
	}

	/**
	 * Gets the snes internal header.
	 *
	 * @param fileBytes the file bytes
	 * @param isHiRom the is hi rom
	 * @return the snes internal header
	 */
	private static byte[] getSnesInternalHeader(byte[] fileBytes, boolean isHiRom) {
		int off = Constants.SNES_LOROM_HEADER_OFF;
		if(isHiRom) {
			off += Constants.SNES_HIROM_OFFSET;
		}
		return Arrays.copyOfRange(fileBytes, off, off + Constants.SNES_INT_HEADER_LEN);
	}

	/**
	 * Removes header and expands rom to internal definition maximum if necessary.
	 *
	 * @param fileBytes .
	 * @return cleanedrom
	 * @throws Exception the exception
	 */
	private static byte[] getNoSmcHeaderFixedLengthSnesRom(byte[] fileBytes) throws Exception {
		byte[] cleanedRom;
		int headerSize = fileBytes.length % Constants.SNES_ROM_SIZE_1MBIT;
		int romSize = (fileBytes.length / Constants.SNES_ROM_SIZE_1MBIT) * Constants.SNES_ROM_SIZE_1MBIT;

		if(headerSize != 0 && headerSize != Constants.SNES_SMC_HEADER_SIZE) {
			throw new Exception("Invalid header size / Tama�o header invalido: " +  headerSize);
		}
		System.out.println("ROM size: " + romSize / Constants.SNES_ROM_SIZE_1MBIT + " Mbit");
		if(headerSize > 0) {
			System.out.println("WITH SMC header");
		}
		else {
			System.out.println("With NO SMC header");
		}

		int cleanedRomSize = romSize;

		if(romSize > Constants.SNES_03_04MBIT_SIZE) {
			if(romSize > Constants.SNES_05_08MBIT_SIZE) {
				if(romSize > Constants.SNES_09_16MBIT_SIZE) {
					if(romSize > Constants.SNES_17_32MBIT_SIZE) {
						if(romSize > Constants.SNES_33_64MBIT_SIZE) {
							throw new Exception("Rom size too large!!!!");
						}
						else {
							cleanedRomSize = Constants.SNES_33_64MBIT_SIZE;
						}
					}
					else {
						cleanedRomSize = Constants.SNES_17_32MBIT_SIZE;
					}
				}
				else {
					cleanedRomSize = Constants.SNES_09_16MBIT_SIZE;
				}
			}
			else {
				cleanedRomSize = Constants.SNES_05_08MBIT_SIZE;
			}
		}
		else {
			cleanedRomSize = Constants.SNES_03_04MBIT_SIZE;
		}

		cleanedRom = new byte[cleanedRomSize];
		//Copy original rom info
		System.arraycopy(fileBytes, headerSize, cleanedRom, 0, romSize);

		if(romSize != cleanedRomSize) {
			System.out.println("Expanding rom to " + (cleanedRomSize / Constants.SNES_ROM_SIZE_1MBIT) + " Mbit");
			//clone last chunk
			System.arraycopy(cleanedRom, romSize - (cleanedRomSize - romSize), cleanedRom, romSize, (cleanedRomSize - romSize));
		}

		return cleanedRom;
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
		header[Constants.SNES_CHECKSUMNOT_HEADER_OFF] = bytesNot[3];
		header[Constants.SNES_CHECKSUMNOT_HEADER_OFF + 1] = bytesNot[2];
		byte[] bytes = Utils.intToByteArray(check);
		header[Constants.SNES_CHECKSUM_HEADER_OFF] = bytes[3];
		header[Constants.SNES_CHECKSUM_HEADER_OFF + 1] = bytes[2];
	}

	/**
	 * Gets the snes rom checksum not.
	 *
	 * @param header the header
	 * @return the snes rom checksum not
	 */
	private static int getSnesRomChecksumNot(byte[] header) {
		return Utils.bytesToInt(header[Constants.SNES_CHECKSUMNOT_HEADER_OFF + 1],
				header[Constants.SNES_CHECKSUMNOT_HEADER_OFF]) & Constants.MASK_16BIT;
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
		//Limpiamos checksum y negado
		byte[] header = getSnesInternalHeader(checkRom, isHiRom);
		updateSnesChecksum(header, Constants.SNES_INITIAL_CHECKSUM, Constants.SNES_INITIAL_CHECKSUMNOT);
		int destPos = Constants.SNES_LOROM_HEADER_OFF;
		if(isHiRom) {
			destPos += Constants.SNES_HIROM_OFFSET;
		}
		System.arraycopy(header, 0, checkRom, destPos, header.length);

		//Calcular checksum
		int checksum = 0;
		for(byte b : checkRom) {
			checksum += (b & Constants.MASK_8BIT);
		}
		return (checksum & Constants.MASK_16BIT);
	}

	/**
	 * Checks if is snes rom hi rom.
	 *
	 * @param fileBytes the file bytes
	 * @return true, if is snes rom hi rom
	 * @throws Exception the exception
	 */
	private static boolean isSnesRomHiRom(byte[] fileBytes) throws Exception {
		boolean isLoRom = false;
		boolean isHiRom = false;

		byte[] header = Arrays.copyOfRange(fileBytes, Constants.SNES_LOROM_HEADER_OFF, Constants.SNES_LOROM_HEADER_OFF + Constants.SNES_INT_HEADER_LEN);
		isLoRom = validateSnesHeader(header, fileBytes.length, false);
		if(!isLoRom) {
			header = Arrays.copyOfRange(fileBytes, Constants.SNES_LOROM_HEADER_OFF + Constants.SNES_HIROM_OFFSET,
					Constants.SNES_LOROM_HEADER_OFF + Constants.SNES_HIROM_OFFSET + Constants.SNES_INT_HEADER_LEN);
			isHiRom = validateSnesHeader(header, fileBytes.length, true);
		}
		if(!isLoRom && !isHiRom) {
			throw new Exception("Could not determine ROM type / No puedo determinarse el tipo de ROM");
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
		if(isValidSnesName(Arrays.copyOfRange(header, Constants.SNES_ROMNAME_HEADER_OFF,
				Constants.SNES_ROMNAME_HEADER_OFF + Constants.SNES_ROMNAME_HEADER_LENGTH))) {
			//2.Verificamos que el map mode marque lo que toca
			int romType = header[Constants.SNES_ROMNAME_MAP_MODE_OFF] & Constants.MASK_8BIT;
			if(((romType & Constants.SNES_HIROM_BIT) == 0 && !isHiRom) ||
					((romType & Constants.SNES_HIROM_BIT) == Constants.SNES_HIROM_BIT && isHiRom)) {
				res = true;
				//3.Verificamos que el tama�o especificado sea correcto
				int snesBanks = ((header[Constants.SNES_ROMSIZE_HEADER_OFF]) & Constants.MASK_8BIT);
				int snesSize = Constants.SNES_HEADER_SIZE_CHUNKS << snesBanks;

				int minSize = 0;
				int maxSize = 0;
				switch(snesBanks) {
				case Constants.SNES_03_04MBIT:
					minSize = Constants.SNES_03_04MBIT_SIZE - Constants.SNES_ROM_SIZE_1MBIT;
					maxSize = Constants.SNES_03_04MBIT_SIZE;
					break;
				case Constants.SNES_05_08MBIT:
					minSize = Constants.SNES_03_04MBIT_SIZE + Constants.SNES_ROM_SIZE_1MBIT;
					maxSize = Constants.SNES_05_08MBIT_SIZE;
					break;
				case Constants.SNES_09_16MBIT:
					minSize = Constants.SNES_05_08MBIT_SIZE + Constants.SNES_ROM_SIZE_1MBIT;
					maxSize = Constants.SNES_09_16MBIT_SIZE;
					break;
				case Constants.SNES_17_32MBIT:
					minSize = Constants.SNES_09_16MBIT_SIZE + Constants.SNES_ROM_SIZE_1MBIT;
					maxSize = Constants.SNES_17_32MBIT_SIZE;
					break;
				case Constants.SNES_33_64MBIT:
					minSize = Constants.SNES_17_32MBIT_SIZE + Constants.SNES_ROM_SIZE_1MBIT;
					maxSize = Constants.SNES_33_64MBIT_SIZE;
					break;
				default:
					System.out.println("Invalid ROM size: " + snesSize);
					res = false;
				}
				if(length < minSize || length >  maxSize) {
					System.out.println("Size mismatch, File (Expanded): " + (length /Constants.SNES_ROM_SIZE_1MBIT)  +
							" MBit, ROM info: " + (minSize / Constants.SNES_ROM_SIZE_1MBIT) + " - " + (maxSize / Constants.SNES_ROM_SIZE_1MBIT) + " MBit");
				}
				if(res) {
					System.out.println("Size correct, File (Expanded): " + (length /Constants.SNES_ROM_SIZE_1MBIT)  +
							" MBit, ROM info: " + (minSize / Constants.SNES_ROM_SIZE_1MBIT) + " - " + (maxSize / Constants.SNES_ROM_SIZE_1MBIT) + " MBit");
				}
			}
		}
		return res;
	}

	/**
	 * Checks if is valid snes name.
	 *
	 * @param asciiName the ascii name
	 * @return true, if is valid snes name
	 */
	private static boolean isValidSnesName(byte[] asciiName) {
		//Verificamos que su nombre cumple que usa bytes van de (1F-7F?)
		boolean res = true;
		for(byte b : asciiName) {
			int num = b & Constants.MASK_8BIT;
			if(num <= Constants.SNES_HEADER_NAME_MIN_CHAR /*|| num >= Constants.SNES_HEADER_NAME_MAX_CHAR*/) {
				res = false;
				break;
			}
		}
		if(res) {
			System.out.println("ROM NAME: \"" + new String(asciiName, StandardCharsets.US_ASCII) + "\"");
		}
		return res;
	}

	/**
	 * The Class ZxTapDataBlock.
	 */
	protected static class ZxTapDataBlock {

		/** The Constant DATA_BLOCK_FLAG. */
		private static final byte DATA_BLOCK_FLAG = (byte) 0xFF;

		/** The Constant HEADER_BLOCK_FLAG. */
		private static final byte HEADER_BLOCK_FLAG = 0;

		/** The Constant MIN_BLOCK_LENGTH. */
		private static final int MIN_BLOCK_LENGTH = 4;

		/** The Constant HEADER_FILE_NAME_LEN. */
		private static final int HEADER_FILE_NAME_LEN = 10;

		/** The Constant HEADER_FILE_NAME_OFF. */
		private static final int HEADER_FILE_NAME_OFF = 2;

		/** The Constant HEADER_BLOCK_BASIC. */
		private static final byte HEADER_BLOCK_BASIC = 0;

		/** The Constant HEADER_BLOCK_NUMARRAY. */
		private static final byte HEADER_BLOCK_NUMARRAY = 1;

		/** The Constant HEADER_BLOCK_ALPHAARRAY. */
		private static final byte HEADER_BLOCK_ALPHAARRAY = 2;

		/** The Constant HEADER_BLOCK_CODE. */
		private static final byte HEADER_BLOCK_CODE = 3;

		/** The Constant FLAG_OFF. */
		private static final int FLAG_OFF = 0;

		/** The Constant DATATYPE_OFF. */
		private static final int DATATYPE_OFF = 1;

		/** The offset. */
		private int offset;

		/**
		 * Gets the offset.
		 *
		 * @return the offset
		 */
		public int getOffset() {
			return offset;
		}

		/**
		 * Sets the offset.
		 *
		 * @param start the new offset
		 */
		public void setOffset(int start) {
			this.offset = start;
		}

		/** The data block. */
		private byte[] dataBlock;

		/**
		 * Gets the data block.
		 *
		 * @return the data block
		 */
		public byte[] getDataBlock() {
			return dataBlock;
		}

		/**
		 * Sets the data block.
		 *
		 * @param dataBlock the new data block
		 */
		public void setDataBlock(byte[] dataBlock) {
			this.dataBlock = dataBlock;
		}

		/**
		 * Calculate checksum.
		 *
		 * @return the byte
		 */
		public byte calculateChecksum() {
			byte calculatedChecksum = 0;
			for(int i = 0; i < dataBlock.length - 1; i++) {
				calculatedChecksum ^= dataBlock[i];
			}
			return calculatedChecksum;
		}

		/**
		 * Sets the checksum.
		 *
		 * @param checksum the new checksum
		 */
		public void setChecksum(byte checksum) {
			dataBlock[dataBlock.length - 1] = checksum;
		}

		/**
		 * Gets the checksum.
		 *
		 * @return the checksum
		 */
		public byte getChecksum() {
			return dataBlock[dataBlock.length - 1];
		}

		/**
		 * Update checksum.
		 *
		 * @param originalBlock the original block
		 */
		public void updateChecksum(ZxTapDataBlock originalBlock) {
			//fields with dataLength < 2 have no checksum (2 bytes for size)
			if(dataBlock.length >= MIN_BLOCK_LENGTH) {
				byte checksum = getChecksum();
				byte calculatedChecksum = calculateChecksum();
				boolean brokenData = false;
				byte flag = dataBlock[FLAG_OFF];
				System.out.print("[" + Utils.intToHexString(flag, Constants.HEXSIZE_8BIT_VALUE) + "] @0x" + Utils.intToHexString(offset, Constants.HEX_ADDR_SIZE) +
						" (Len: 0x" + Utils.intToHexString(dataBlock.length, Constants.HEX_ADDR_SIZE) + ") Checksums: 0x" +
						Utils.intToHexString(checksum & Constants.MASK_8BIT, Constants.HEXSIZE_8BIT_VALUE) +
						"/0x" +
						Utils.intToHexString(calculatedChecksum & Constants.MASK_8BIT, Constants.HEXSIZE_8BIT_VALUE));
				switch(flag) {
				case HEADER_BLOCK_FLAG:
					System.out.print(" Header \"" + getFileName() + "\" of type: ");
					switch(dataBlock[DATATYPE_OFF]) {
					case HEADER_BLOCK_BASIC:
						System.out.println("BASIC");
						break;
					case HEADER_BLOCK_ALPHAARRAY:
						System.out.println("ALPHANUMERIC ARRAY");
						break;
					case HEADER_BLOCK_CODE:
						System.out.println("CODE");
						break;
					case HEADER_BLOCK_NUMARRAY:
						System.out.println("NUMERIC ARRAY");
						break;
					default:
						System.out.println("UNKNOWN");
						break;
					}
					break;
				default:
					//Hay que buscar este bloque en la cinta original,
					//si ese bloque esta broken, no actualizaremos el checksum
					//y esperaremos que funcione...
					System.out.println(" Custom Data");
					if(originalBlock != null) {
						brokenData = originalBlock.getChecksum() != originalBlock.calculateChecksum();
					}
					break;
				case DATA_BLOCK_FLAG:
					System.out.println(" Data");
					break;
				}
				if(brokenData) {
					System.out.println("Original data is broken, checksum NOT updated.");
				}
				if(checksum != calculatedChecksum && !brokenData) {
					dataBlock[dataBlock.length - 1] = calculatedChecksum;
					System.out.println("Incorrect block checksum, checksum UPDATED to [" + Utils.intToHexString(calculatedChecksum & Constants.MASK_8BIT, Constants.HEXSIZE_8BIT_VALUE) + "].");
				}
			}
		}

		/**
		 * Gets the file name.
		 *
		 * @return the file name
		 */
		private String getFileName() {
			return new String(Arrays.copyOfRange(dataBlock, HEADER_FILE_NAME_OFF, HEADER_FILE_NAME_OFF + HEADER_FILE_NAME_LEN));
		}
	}

	/**
	 * Check update zx tap checksum.
	 *
	 * @param fileBytes the file bytes
	 * @param isTzx the is tzx
	 * @param originalTapeBytes the original tape bytes
	 * @return the byte[]
	 */
	private static byte[] checkUpdateZxTapChecksum(byte[] fileBytes, boolean isTzx, byte[] originalTapeBytes) {
		List<ZxTapDataBlock> dataBlocks;
		List<ZxTapDataBlock> originalDataBlocks = new ArrayList<ZxTapDataBlock>();
		if(isTzx) {
			dataBlocks = getTzxTapDataBlocks(fileBytes);
			if(originalTapeBytes != null) {
				originalDataBlocks = getTzxTapDataBlocks(originalTapeBytes);
			}
		}
		else {
			dataBlocks = getDataBlocks(fileBytes);
			if(originalTapeBytes != null) {
				originalDataBlocks = getDataBlocks(originalTapeBytes);
			}
		}
		for(ZxTapDataBlock dataBlock : dataBlocks) {
			ZxTapDataBlock originalBlock = getOriginalBlock(originalDataBlocks, dataBlock);
			dataBlock.updateChecksum(originalBlock);
			System.arraycopy(dataBlock.getDataBlock(), 0, fileBytes, dataBlock.getOffset(), dataBlock.getDataBlock().length);
		}
		return fileBytes;
	}

	/**
	 * Gets the original block.
	 *
	 * @param originalDataBlocks the original data blocks
	 * @param testDataBlock the test data block
	 * @return the original block
	 */
	private static ZxTapDataBlock getOriginalBlock(List<ZxTapDataBlock> originalDataBlocks, ZxTapDataBlock testDataBlock) {
		ZxTapDataBlock res = null;
		for(ZxTapDataBlock dataBlock : originalDataBlocks) {
			if(dataBlock.getOffset() == testDataBlock.getOffset()) {
				res = dataBlock;
				break;
			}
		}
		return res;
	}

	/**
	 * Gets the data blocks.
	 *
	 * @param fileBytes the file bytes
	 * @return the data blocks
	 */
	private static List<ZxTapDataBlock> getDataBlocks(byte[] fileBytes) {
		List<ZxTapDataBlock> res = new ArrayList<ZxTapDataBlock>();
		for(int i = 0; i < fileBytes.length;) {
			ZxTapDataBlock zxTapDataBlock = new ZxTapDataBlock();
			res.add(zxTapDataBlock);
			int dataBlockLength = Utils.bytesToInt(fileBytes[i + 1], fileBytes[i]);
			i += BYTE_SIZE_16_BITS;
			zxTapDataBlock.setOffset(i);
			zxTapDataBlock.setDataBlock(Arrays.copyOfRange(fileBytes, i, i + dataBlockLength));
			i += dataBlockLength;
		}
		return res;
	}

	/**
	 * Gets the tzx tap data blocks.
	 *
	 * @param fileBytes the file bytes
	 * @return the tzx tap data blocks
	 */
	private static List<ZxTapDataBlock> getTzxTapDataBlocks(byte[] fileBytes) {
		List<ZxTapDataBlock> res = new ArrayList<ZxTapDataBlock>();
		for(int i = TZX_HEADER_SIZE; i < fileBytes.length;) {
			byte tzxBlockType = fileBytes[i];
			int bytesLength = 1;
			i++;
			if(DATA_TYPES.contains(tzxBlockType)) {
				ZxTapDataBlock zxTzxDataBlock = new ZxTapDataBlock();
				res.add(zxTzxDataBlock);
				//Search data offset
				//Set bytesLength as the data to skip
				switch(tzxBlockType) {
				case TZX_ID_STANDARD_DATA:
					bytesLength = Utils.bytesToInt(fileBytes[i + 1 + TZX_ID_STANDARD_DATA_LOFF], fileBytes[i + TZX_ID_STANDARD_DATA_LOFF]);
					i += TZX_ID_STANDARD_DATA_DOFF;
					break;
				case TZX_ID_TURBO_DATA:
					bytesLength = Utils.bytesToInt(fileBytes[i + 2 + TZX_ID_TURBO_DATA_LOFF], fileBytes[i + 1 + TZX_ID_TURBO_DATA_LOFF], fileBytes[i + TZX_ID_TURBO_DATA_LOFF]);
					i += TZX_ID_TURBO_DATA_DOFF;
					break;
				case TZX_ID_PURE_DATA:
					bytesLength = Utils.bytesToInt(fileBytes[i + 2 + TZX_ID_PURE_DATA_LOFF], fileBytes[i + 1 + TZX_ID_PURE_DATA_LOFF], fileBytes[i + TZX_ID_PURE_DATA_LOFF]);
					i += TZX_ID_PURE_DATA_DOFF;
					break;
				default:
					break;
				}
				//Set i to data offset
				zxTzxDataBlock.setOffset(i);
				zxTzxDataBlock.setDataBlock(Arrays.copyOfRange(fileBytes, i, i + bytesLength));
				i += bytesLength;
			}
			else {
				//Skip y bytes
				switch(tzxBlockType) {
				case TZX_ID_PURE_TONE:
					i += TZX_ID_PURE_TONE_LEN;
					break;
				case TZX_ID_SEQ_PULSES:
					i += ((fileBytes[i] & Constants.MASK_8BIT) * BYTE_SIZE_16_BITS + 1);
					break;
				case TZX_ID_DIRECT_REC:
					i += (Utils.bytesToInt(fileBytes[i + TZX_ID_DIRECT_REC_LOFF + 2], fileBytes[i + TZX_ID_DIRECT_REC_LOFF + 1],
							fileBytes[i + TZX_ID_DIRECT_REC_LOFF]) + TZX_ID_DIRECT_REC_LOFF);
					break;
				case TZX_ID_CSW_REC:
				case TZX_ID_GEN_DATA:
					i += 4 + Utils.bytesToInt(fileBytes[i + 3], fileBytes[i + 2], fileBytes[i + 1],
							fileBytes[i]);
					break;
				case TZX_ID_PAUSE_STOP:
					i += TZX_ID_PAUSE_STOP_LEN;
					break;
				case TZX_ID_GROUP_START:
					i += ((fileBytes[i] & Constants.MASK_8BIT) + 1);
					break;
				case TZX_ID_JUMP_BLOCK:
				case TZX_ID_LOOP_START:
					i += TZX_WORD_LEN;
					break;
				case TZX_ID_CALL_SEQ:
					i +=  Utils.bytesToInt(fileBytes[i + 1], fileBytes[i]) * TZX_WORD_LEN + TZX_WORD_LEN;
					break;
				case TZX_ID_SEL_BLOCK:
				case TZX_ID_ARCHIVE_INFO:
					i +=  Utils.bytesToInt(fileBytes[i + 1], fileBytes[i]) + TZX_WORD_LEN;
					break;
				case TZX_ID_STOP_TAPE_48K:
					i +=  TZX_ID_STOP_TAPE_48K_LEN;
					break;
				case TZX_ID_SET_SIG_LEV:
					i +=  TZX_ID_SET_SIG_LEV_LEN;
					break;
				case TZX_ID_TEXT_DESC:
					i += (fileBytes[i] & Constants.MASK_8BIT) + TZX_BYTE_LEN;
					break;
				case TZX_ID_MSG_BLOCK:
					i += (fileBytes[i + 1] & Constants.MASK_8BIT) + 2 * TZX_BYTE_LEN;
					break;
				case TZX_ID_HARD_TYPE:
					i += 3 * TZX_BYTE_LEN + (fileBytes[i + 1] & Constants.MASK_8BIT);
					break;
				case TZX_ID_CUSTOM_INFO_BLOCK:
					i += 20 * TZX_BYTE_LEN + Utils.bytesToInt(fileBytes[i + 4], fileBytes[i + 3], fileBytes[i + 2], fileBytes[i+1]);
					break;
				case TZX_ID_GLUE_BLOCK:
					i += TZX_ID_GLUE_BLOCK_LEN;
					break;
				case TZX_ID_GROUP_END:
				case TZX_ID_LOOP_END:
				case TZX_ID_RET_SEQ:
					//No body
					break;
				}
			}
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
		return Utils.bytesToInt(header[Constants.SNES_CHECKSUM_HEADER_OFF + 1], header[Constants.SNES_CHECKSUM_HEADER_OFF]) & Constants.MASK_16BIT;
	}

}
