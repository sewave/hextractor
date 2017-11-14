package com.wave.hextractor;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

public class Constants {
	public static final String FILE_SEPARATOR = System.getProperty("file.separator");
	public static final char CHR_DOT = '.';
	public static final char CHR_ASTER = '*';
	public static final byte BYTE_ASTER = "*".getBytes(StandardCharsets.US_ASCII)[0];
	public static final String STR_DOT = String.valueOf(CHR_DOT);
	public static final String HEX_VIEWER_UNKNOWN_CHAR = STR_DOT;
	public static final String EMPTY_OFFSET_FILE = "empty.off";
	public static final int MIN_SEARCH_WORD_LENGTH = 4;
	public static final int MIN_PRINTABLE_CHAR = 0x20;
	public static final int MAX_PRINTABLE_CHAR = 0x7E;
	public static final String OFF_EXTENSION = ".off";

	public static final String IPS_HEADER = "PATCH";
	public static final String IPS_EOF = "EOF";
	public static final int IPS_PATCH_MAX_SIZE = 0xFFFF;
	public static final int MEGADRIVE_CHECKSUM_LOCATION = 0x18E;
	public static final int MEGADRIVE_CHECKSUM_START_CALCULATION = 0x200;
	public static final int MASK_16BIT = 0xFFFF;
	public static final int MASK_8BIT = 0x00FF;
	public static final int GAMEBOY_HEADER_CHECKSUM_LOCATION = 0x14D;
	public static final int GAMEBOY_HEADER_CHECKSUM_START_CALCULATION = 0x134;
	public static final int GAMEBOY_HEADER_CHECKSUM_END_CALCULATION = 0x14C;
	public static final int GAMEBOY_ROM_CHECKSUM_LOCATION = 0x14E;

	public static final int SNES_CHECKSUM_BYTES = 2;
	public static final int SNES_CHECKSUMNOT_HEADER_OFF = 44;
	public static final int SNES_CHECKSUM_HEADER_OFF = SNES_CHECKSUMNOT_HEADER_OFF + SNES_CHECKSUM_BYTES;
	public static final int SNES_ROMNAME_HEADER_OFF = 16;
	public static final int SNES_ROMNAME_HEADER_LENGTH = 21;
	public static final int SNES_ROMNAME_MAP_MODE_OFF = 37;
	public static final int SNES_ROMSIZE_HEADER_OFF = 39;
	public static final int SNES_LOROM_HEADER_OFF = 0x7FB0;
	public static final int SNES_HIROM_OFFSET = 0x8000;
	public static final int SNES_INITIAL_CHECKSUM = 0;
	public static final int SNES_INITIAL_CHECKSUMNOT = 0xFFFF;
	public static final int SNES_HEADER_EMPTY = 0;
	public static final int HEXSIZE_8BIT_VALUE = 2;
	public static final int HEXSIZE_16BIT_VALUE = 4;
	public static final int SNES_ROM_SIZE_1MBIT = 131072;
	public static final int SNES_ROM_SIZE_PAD = SNES_ROM_SIZE_1MBIT * 8;
	public static final int SNES_SMC_HEADER_SIZE = 0x200;
	public static final int SNES_INT_HEADER_LEN = 80;
	public static final int SNES_HIROM_BIT = 1;
	public static final int SNES_HEADER_SIZE_CHUNKS = 0x400;
	public static final int SNES_HEADER_NAME_MIN_CHAR = 0x1F;
	public static final int SNES_HEADER_NAME_MAX_CHAR = 0x7F;
	public static final int SNES_03_04MBIT = 0x09;
	public static final int SNES_05_08MBIT = 0x0A;
	public static final int SNES_09_16MBIT = 0x0B;
	public static final int SNES_17_32MBIT = 0x0C;
	public static final int SNES_33_64MBIT = 0x0D;
	public static final int SNES_03_04MBIT_SIZE = 4 * SNES_ROM_SIZE_1MBIT;
	public static final int SNES_05_08MBIT_SIZE = 8 * SNES_ROM_SIZE_1MBIT;
	public static final int SNES_09_16MBIT_SIZE = 16 * SNES_ROM_SIZE_1MBIT;
	public static final int SNES_17_32MBIT_SIZE = 32 * SNES_ROM_SIZE_1MBIT;
	public static final int SNES_33_64MBIT_SIZE = 64 * SNES_ROM_SIZE_1MBIT;

	public static final int GAMEBOY_ROM_CHECKSUM_START_CALCULATION = 0x000;
	public static final char NEWLINE = '\n';
	public static final String S_NEWLINE = String.valueOf(NEWLINE);
	public static final char CRETURN = '\r';
	public static final String S_CRETURN = String.valueOf(CRETURN);
	public static final String SPACE_STR = " ";
	public static final char ADDR_CHAR = '@';
	public static final String ADDR_STR = String.valueOf(ADDR_CHAR);
	public static final char HEX_CHAR = '~';
	public static final char MAX_BYTES = '|';
	public static final String S_MAX_BYTES = String.valueOf(MAX_BYTES);
	public static final char ORG_STR_OPEN = '{';
	public static final String S_ORG_STR_OPEN = String.valueOf(ORG_STR_OPEN);
	public static final char ORG_STR_CLOSE = '}';
	public static final String S_ORG_STR_CLOSE = String.valueOf(ORG_STR_CLOSE);
	public static final char STR_NUM_CHARS = '#';
	public static final String S_STR_NUM_CHARS = String.valueOf(STR_NUM_CHARS);
	public static final int LEN_NUM_CHARS = 3;
	public static final char COMMENT_LINE = ';';
	public static final String S_COMMENT_LINE = String.valueOf(COMMENT_LINE);
	public static final String TABLE_SEPARATOR = "=";
	public static final String OFFSET_CHAR_SEPARATOR = "-";
	public static final String OFFSET_LENGTH_SEPARATOR = ":";
	public static final String OFFSET_STR_SEPARATOR = ",";
	public static final String EXTRACT_EXTENSION = ".ext";
	public static final String OFFSET_EXTENSION = ".off";
	public static final byte PAD_CHAR = 0;
	public static final char PAD_CHAR_STRING = '0';
	public static final String EMPTY = "";
	public static final String HEXCHARS = "0123456789ABCDEFabcdef";
	public static final String HEX_16_FORMAT = "%02X";
	public static final String UTF8_ENCODING = "UTF8";
	public static final int HEX_ADDR_SIZE = 8;
	public static final int CHAR_SIZE = 1;
	public static final int HEX_SIZE = 2;
	public static final int HEX_RADIX = 16;
	public static final String REGEX_CLEAN_TEXT = "((\\||@|;).*|~.{2}~|\\#.*)";
	public static final String REGEX_CLEAN_SPACES = "[^\\S\n]{2,}";
	public static final String REGEX_MULTI_SPACES = "\\s{2,}";
	public static final String REGEX_NOT_LETTER_DIGIT = "[^a-zA-z1-9'\\s]";
	public static final int COMMENT_SIZE_MADDR_START = 1;
	public static final int COMMENT_SIZE_MADDR_END = 9;
	public static final int MIN_NUM_CHARS_WORD = 3;
	public static final String UTF_8_BOM_BE = "\uFEFF";
	public static final String UTF_8_BOM_LE = "\uFFFE";
	public static final List<String> RESERVED_CHARS = Arrays.asList(
			new String[]{
					String.valueOf(Constants.HEX_CHAR),
					ADDR_STR,
					S_MAX_BYTES,
					String.valueOf(Constants.STR_NUM_CHARS),
					S_COMMENT_LINE});

	public static final String DEFAULT_DICT = "EngDict.txt";

}
