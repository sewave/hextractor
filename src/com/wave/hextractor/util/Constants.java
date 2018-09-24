package com.wave.hextractor.util;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

/**
 * Global constants.
 * @author slcantero
 */
public class Constants {

	/**
	 * Instantiates a new constants.
	 */
	private Constants() {
	}

	/** The Constant RB_NAME. */
	public static final String RB_NAME= "app";

	/** The Constant HEX_VALUE_SIZE. */
	public static final int HEX_VALUE_SIZE =  3;

	/** The Constant CURRENT_DIR. */
	public static final String CURRENT_DIR = ".";

	/** The Constant FILE_SEPARATOR. */
	public static final String FILE_SEPARATOR = System.getProperty("file.separator");

	/** The Constant PARENT_DIR. */
	public static final String PARENT_DIR = ".." + FILE_SEPARATOR;

	/** The Constant CHR_DOT. */
	public static final char CHR_DOT = '.';

	/** The Constant CHR_ASTER. */
	public static final char CHR_ASTER = '*';

	/** The Constant STR_ASTER. */
	public static final String STR_ASTER = String.valueOf(CHR_ASTER);

	/** The Constant REGEX_STR_ASTER. */
	public static final String REGEX_STR_ASTER = "\\" + STR_ASTER;

	/** The Constant BYTE_ASTER. */
	public static final byte BYTE_ASTER = STR_ASTER.getBytes(StandardCharsets.US_ASCII)[0];

	/** The Constant STR_DOT. */
	public static final String STR_DOT = String.valueOf(CHR_DOT);

	/** The Constant HEX_VIEWER_UNKNOWN_CHAR. */
	public static final String HEX_VIEWER_UNKNOWN_CHAR = STR_DOT;

	/** The Constant EMPTY_OFFSET_FILE. */
	public static final String EMPTY_OFFSET_FILE = "empty.off";

	/** The Constant MIN_SEARCH_WORD_LENGTH. */
	public static final int MIN_SEARCH_WORD_LENGTH = 4;

	/** The Constant MIN_SEARCH_WORDS. */
	public static final int MIN_SEARCH_WORDS = 6;

	/** The Constant MAX_SEARCH_WORDS. */
	public static final int MAX_SEARCH_WORDS = 12;

	/** The Constant MIN_PRINTABLE_CHAR. */
	public static final int MIN_PRINTABLE_CHAR = 0x20;

	/** The Constant MAX_PRINTABLE_CHAR. */
	public static final int MAX_PRINTABLE_CHAR = 0x7E;

	/** The Constant OFF_EXTENSION. */
	public static final String OFF_EXTENSION = ".off";

	/** The Constant IPS_HEADER. */
	public static final String IPS_HEADER = "PATCH";

	/** The Constant IPS_EOF. */
	public static final String IPS_EOF = "EOF";

	/** The Constant IPS_PATCH_MAX_SIZE. */
	public static final int IPS_PATCH_MAX_SIZE = 0xFFFF;

	/** The Constant MASK_16BIT. */
	public static final int MASK_16BIT = 0xFFFF;

	/** The Constant MASK_8BIT. */
	public static final int MASK_8BIT = 0x00FF;

	/** The Constant HEXSIZE_8BIT_VALUE. */
	public static final int HEXSIZE_8BIT_VALUE = 2;

	/** The Constant HEXSIZE_16BIT_VALUE. */
	public static final int HEXSIZE_16BIT_VALUE = 4;

	/** The Constant NEWLINE. */
	public static final char NEWLINE = '\n';

	/** The Constant S_NEWLINE. */
	public static final String S_NEWLINE = String.valueOf(NEWLINE);

	/** The Constant CRETURN. */
	public static final char CRETURN = '\r';

	/** The Constant S_CRETURN. */
	public static final String S_CRETURN = String.valueOf(CRETURN);

	/** The Constant SPACE_STR. */
	public static final String SPACE_STR = " ";

	/** The Constant ADDR_CHAR. */
	public static final char ADDR_CHAR = '@';

	/** The Constant ADDR_STR. */
	public static final String ADDR_STR = String.valueOf(ADDR_CHAR);

	/** The Constant HEX_CHAR. */
	public static final char HEX_CHAR = '~';

	/** The Constant MAX_BYTES. */
	public static final char MAX_BYTES = '|';

	/** The Constant S_MAX_BYTES. */
	public static final String S_MAX_BYTES = String.valueOf(MAX_BYTES);

	/** The Constant ORG_STR_OPEN. */
	public static final char ORG_STR_OPEN = '{';

	/** The Constant S_ORG_STR_OPEN. */
	public static final String S_ORG_STR_OPEN = String.valueOf(ORG_STR_OPEN);

	/** The Constant ORG_STR_CLOSE. */
	public static final char ORG_STR_CLOSE = '}';

	/** The Constant S_ORG_STR_CLOSE. */
	public static final String S_ORG_STR_CLOSE = String.valueOf(ORG_STR_CLOSE);

	/** The Constant STR_NUM_CHARS. */
	public static final char STR_NUM_CHARS = '#';

	/** The Constant S_STR_NUM_CHARS. */
	public static final String S_STR_NUM_CHARS = String.valueOf(STR_NUM_CHARS);

	/** The Constant LEN_NUM_CHARS. */
	public static final int LEN_NUM_CHARS = 3;

	/** The Constant COMMENT_LINE. */
	public static final char COMMENT_LINE = ';';

	/** The Constant S_COMMENT_LINE. */
	public static final String S_COMMENT_LINE = String.valueOf(COMMENT_LINE);

	/** The Constant CODEWORD_START. */
	public static final char CODEWORD_START = '{';

	/** The Constant CODEWORD_END. */
	public static final char CODEWORD_END = '}';

	/** The Constant S_CODEWORD_START. */
	public static final String S_CODEWORD_START = String.valueOf(CODEWORD_START);

	/** The Constant S_CODEWORD_END. */
	public static final String S_CODEWORD_END = String.valueOf(CODEWORD_END);

	/** The Constant TABLE_SEPARATOR. */
	public static final String TABLE_SEPARATOR = "=";

	/** The Constant OFFSET_CHAR_SEPARATOR. */
	public static final String OFFSET_CHAR_SEPARATOR = "-";

	/** The Constant OFFSET_LENGTH_SEPARATOR. */
	public static final String OFFSET_LENGTH_SEPARATOR = ":";

	/** The Constant OFFSET_STR_SEPARATOR. */
	public static final String OFFSET_STR_SEPARATOR = ",";

	/** The Constant EXTRACT_EXTENSION. */
	public static final String EXTRACT_EXTENSION = ".ext";

	/** The Constant EXTRACT_EXTENSION_NODOT. */
	public static final String EXTRACT_EXTENSION_NODOT = "ext";

	/** The Constant OFFSET_EXTENSION. */
	public static final String OFFSET_EXTENSION = ".off";

	/** The Constant TBL_EXTENSION_REGEX. */
	public static final String TBL_EXTENSION_REGEX = "[.]tbl";

	/** The Constant PAD_CHAR. */
	public static final byte PAD_CHAR = 0;

	/** The Constant PAD_CHAR_STRING. */
	public static final char PAD_CHAR_STRING = '0';

	/** The Constant EMPTY. */
	public static final String EMPTY = "";

	/** The Constant HEXCHARS. */
	public static final String HEXCHARS = "0123456789ABCDEFabcdef";

	/** The Constant HEX_16_FORMAT. */
	public static final String HEX_16_FORMAT = "%02X";

	/** The Constant UTF8_ENCODING. */
	public static final String UTF8_ENCODING = "UTF8";

	/** The Constant HEX_ADDR_SIZE. */
	public static final int HEX_ADDR_SIZE = 8;

	/** The Constant CHAR_SIZE. */
	public static final int CHAR_SIZE = 1;

	/** The Constant HEX_SIZE. */
	public static final int HEX_SIZE = 2;

	/** The Constant HEX_RADIX. */
	public static final int HEX_RADIX = 16;

	/** The Constant REGEX_CLEAN_TEXT. */
	public static final String REGEX_CLEAN_TEXT = "((\\||@|;).*|~.{2}~|\\#.*)";

	/** The Constant REGEX_CLEAN_SPACES. */
	public static final String REGEX_CLEAN_SPACES = "[^\\S\n]{2,}";

	/** The Constant REGEX_MULTI_SPACES. */
	public static final String REGEX_MULTI_SPACES = "\\s{2,}";

	/** The Constant REGEX_NOT_LETTER_DIGIT. */
	public static final String REGEX_NOT_LETTER_DIGIT = "[^a-zA-z1-9'\\s]";

	/** The Constant REGEX_DICTIONARY_CHARS. */
	public static final String REGEX_DICTIONARY_CHARS = "(\\{|\\})";

	/** The Constant COMMENT_SIZE_MADDR_START. */
	public static final int COMMENT_SIZE_MADDR_START = 1;

	/** The Constant COMMENT_SIZE_MADDR_END. */
	public static final int COMMENT_SIZE_MADDR_END = 9;

	/** The Constant MIN_NUM_CHARS_WORD. */
	public static final int MIN_NUM_CHARS_WORD = 3;

	/** The Constant UTF_8_BOM_BE. */
	public static final String UTF_8_BOM_BE = "\uFEFF";

	/** The Constant UTF_8_BOM_LE. */
	public static final String UTF_8_BOM_LE = "\uFFFE";

	/** The Constant RESERVED_CHARS. */
	public static final List<String> RESERVED_CHARS = Arrays.asList(String.valueOf(Constants.HEX_CHAR), ADDR_STR,
			S_MAX_BYTES, String.valueOf(Constants.STR_NUM_CHARS), S_COMMENT_LINE, S_CODEWORD_START, S_CODEWORD_END);

	/** The Constant DEFAULT_DICT. */
	public static final String DEFAULT_DICT = "EngDict.txt";

	/** The Constant EXTENSIONS_MEGADRIVE. */
	public static final List<String> EXTENSIONS_MEGADRIVE = Arrays.asList("smd", "gen", "bin", "md", "32x");

	/** The Constant EXTENSIONS_SNES. */
	public static final List<String> EXTENSIONS_SNES = Arrays.asList("sfc", "smc");

	/** The Constant EXTENSIONS_GB. */
	public static final List<String> EXTENSIONS_GB = Arrays.asList("gb", "gbc");

	/** The Constant EXTENSIONS_TAP. */
	public static final List<String> EXTENSIONS_TAP = Arrays.asList("tap");

	/** The Constant EXTENSIONS_TZX_CDT. */
	public static final List<String> EXTENSIONS_TZX_CDT = Arrays.asList("tzx", "cdt");

	/** The Constant EXTENSIONS_SMS. */
	public static final List<String> EXTENSIONS_SMS = Arrays.asList("sms");

	/** The Constant FILE_TYPE_OTHER. */
	public static final String FILE_TYPE_OTHER =  "0";

	/** The Constant FILE_TYPE_MEGADRIVE. */
	public static final String FILE_TYPE_MEGADRIVE =  "1";

	/** The Constant FILE_TYPE_MASTERSYSTEM. */
	public static final String FILE_TYPE_MASTERSYSTEM =  "2";

	/** The Constant FILE_TYPE_NGB. */
	public static final String FILE_TYPE_NGB =  "3";

	/** The Constant FILE_TYPE_SNES. */
	public static final String FILE_TYPE_SNES =  "4";

	/** The Constant FILE_TYPE_ZXTAP. */
	public static final String FILE_TYPE_ZXTAP =  "5";

	/** The Constant FILE_TYPE_TZX. */
	public static final String FILE_TYPE_TZX =  "6";

}
