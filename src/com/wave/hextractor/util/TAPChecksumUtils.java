package com.wave.hextractor.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * The Class TAPChecksumUtils.
 */
public class TAPChecksumUtils {

	/**
	 * Instantiates a new TAP checksum utils.
	 */
	private TAPChecksumUtils() {
	}

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
				Utils.logNoNL("[" + Utils.intToHexString(flag, Constants.HEXSIZE_8BIT_VALUE) + "] @0x" + Utils.intToHexString(offset, Constants.HEX_ADDR_SIZE) +
						" (Len: 0x" + Utils.intToHexString(dataBlock.length, Constants.HEX_ADDR_SIZE) + ") Checksums: 0x" +
						Utils.intToHexString(checksum & Constants.MASK_8BIT, Constants.HEXSIZE_8BIT_VALUE) +
						"/0x" +
						Utils.intToHexString(calculatedChecksum & Constants.MASK_8BIT, Constants.HEXSIZE_8BIT_VALUE));
				switch(flag) {
				case HEADER_BLOCK_FLAG:
					Utils.logNoNL(" Header \"" + getFileName() + "\" of type: ");
					switch(dataBlock[DATATYPE_OFF]) {
					case HEADER_BLOCK_BASIC:
						Utils.log("BASIC");
						break;
					case HEADER_BLOCK_ALPHAARRAY:
						Utils.log("ALPHANUMERIC ARRAY");
						break;
					case HEADER_BLOCK_CODE:
						Utils.log("CODE");
						break;
					case HEADER_BLOCK_NUMARRAY:
						Utils.log("NUMERIC ARRAY");
						break;
					default:
						Utils.log("UNKNOWN");
						break;
					}
					break;
				case DATA_BLOCK_FLAG:
					Utils.log(" Data");
					break;
				default:
					//Hay que buscar este bloque en la cinta original,
					//si ese bloque esta broken, no actualizaremos el checksum
					//y esperaremos que funcione...
					Utils.log(" Custom Data");
					if(originalBlock != null) {
						brokenData = originalBlock.getChecksum() != originalBlock.calculateChecksum();
					}
					break;
				}
				if(brokenData) {
					Utils.log("Original data is broken, checksum NOT updated.");
				}
				if(checksum != calculatedChecksum && !brokenData) {
					dataBlock[dataBlock.length - 1] = calculatedChecksum;
					Utils.log("Incorrect block checksum, checksum UPDATED to [" + Utils.intToHexString(calculatedChecksum & Constants.MASK_8BIT, Constants.HEXSIZE_8BIT_VALUE) + "].");
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
		List<ZxTapDataBlock> originalDataBlocks = new ArrayList<>();
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
		List<ZxTapDataBlock> res = new ArrayList<>();
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
		List<ZxTapDataBlock> res = new ArrayList<>();
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
					i += (fileBytes[i] & Constants.MASK_8BIT) * BYTE_SIZE_16_BITS + 1;
					break;
				case TZX_ID_DIRECT_REC:
					i += Utils.bytesToInt(fileBytes[i + TZX_ID_DIRECT_REC_LOFF + 2], fileBytes[i + TZX_ID_DIRECT_REC_LOFF + 1],
							fileBytes[i + TZX_ID_DIRECT_REC_LOFF]) + TZX_ID_DIRECT_REC_LOFF;
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
					i += (fileBytes[i] & Constants.MASK_8BIT) + 1;
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
				default:
					//No body
					break;
				}
			}
		}
		return res;
	}

	/**
	 * Updates tap checksum .
	 *
	 * @param inputFile the input file
	 * @throws IOException the exception
	 */
	public static void checkUpdateZxTapChecksum(String inputFile) throws IOException {
		checkUpdateZxTapChecksum(inputFile, null);
	}

	/**
	 * Updates tap checksum if original tap has correct checksums.
	 *
	 * @param inputFile the input file
	 * @param originalFile the original file
	 * @throws IOException the exception
	 */
	public static void checkUpdateZxTapChecksum(String inputFile, String originalFile) throws IOException {
		Utils.log("Fixing ZX TAP checksums for \"" + inputFile + "\" ");
		byte[] originalFileBytes = null;
		if(originalFile != null) {
			originalFileBytes = Files.readAllBytes(Paths.get(originalFile));
			Utils.log("With original file \"" + originalFile + "\"");
		}
		else {
			Utils.log("");
		}
		FileUtils.writeFileBytes(inputFile, checkUpdateZxTapChecksum(Files.readAllBytes(Paths.get(inputFile)), false, originalFileBytes));
	}

	/**
	 * Updates Tzx checksum.
	 *
	 * @param inputFile the input file
	 * @throws IOException the exception
	 */
	public static void checkUpdateZxTzxChecksum(String inputFile) throws IOException {
		checkUpdateZxTzxChecksum(inputFile, null);
	}

	/**
	 * Updates Tzx checksum if original file has good checksums.
	 *
	 * @param inputFile the input file
	 * @param originalFile the original file
	 * @throws IOException the exception
	 */
	public static void checkUpdateZxTzxChecksum(String inputFile, String originalFile) throws IOException {
		Utils.logNoNL("Fixing ZX TZX checksums for \"" + inputFile + "\" ");
		byte[] originalFileBytes = null;
		if(originalFile != null) {
			originalFileBytes = Files.readAllBytes(Paths.get(originalFile));
			Utils.log("With original file \"" + originalFile + "\"");
		}
		else {
			Utils.log("");
		}
		FileUtils.writeFileBytes(inputFile, checkUpdateZxTapChecksum(Files.readAllBytes(Paths.get(inputFile)),
				true, originalFileBytes));
	}
}
