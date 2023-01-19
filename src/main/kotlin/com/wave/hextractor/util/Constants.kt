package com.wave.hextractor.util

import java.nio.charset.StandardCharsets

/**
 * Global constants.
 */
object Constants {
    const val RB_NAME = "app"
    const val HEX_VALUE_SIZE = 3
    const val CURRENT_DIR = "."
    val FILE_SEPARATOR: String = System.getProperty("file.separator")
    val PARENT_DIR = "..$FILE_SEPARATOR"
    const val CHR_DOT = '.'
    const val CHR_ASTER = '*'
    const val STR_ASTER = CHR_ASTER.toString()
    const val REGEX_STR_ASTER = "\\" + STR_ASTER
    val BYTE_ASTER = STR_ASTER.toByteArray(StandardCharsets.US_ASCII)[0]
    private const val STR_DOT = CHR_DOT.toString()
    const val HEX_VIEWER_UNKNOWN_CHAR = STR_DOT
    const val EMPTY_OFFSET_FILE = "empty.off"
    const val MIN_SEARCH_WORD_LENGTH = 3
    const val MIN_PRINTABLE_CHAR = 0x20
    const val MAX_PRINTABLE_CHAR = 0x7E
    const val OFF_EXTENSION = ".off"
    const val IPS_HEADER = "PATCH"
    const val IPS_EOF = "EOF"
    const val IPS_PATCH_MAX_SIZE = 65535
    const val MASK_16BIT = 0xFFFF
    const val MASK_8BIT = 0x00FF
    const val HEXSIZE_8BIT_VALUE = 2
    const val HEXSIZE_16BIT_VALUE = 4
    const val NEWLINE = '\n'
    const val S_NEWLINE = NEWLINE.toString()
    private const val CARRY_RETURN = '\r'
    const val S_CARRY_RETURN = CARRY_RETURN.toString()
    const val SPACE_STR = " "
    const val ADDR_CHAR = '@'
    const val ADDR_STR = ADDR_CHAR.toString()
    const val HEX_CHAR = '~'
    const val MAX_BYTES = '|'
    const val S_MAX_BYTES = MAX_BYTES.toString()
    const val ORG_STR_OPEN = '{'
    const val S_ORG_STR_OPEN = ORG_STR_OPEN.toString()
    const val ORG_STR_CLOSE = '}'
    const val S_ORG_STR_CLOSE = ORG_STR_CLOSE.toString()

    /** The Constant STR_NUM_CHARS.  */
    const val STR_NUM_CHARS = '#'

    /** The Constant S_STR_NUM_CHARS.  */
    const val S_STR_NUM_CHARS = STR_NUM_CHARS.toString()

    /** The Constant LEN_NUM_CHARS.  */
    const val LEN_NUM_CHARS = 3

    /** The Constant COMMENT_LINE.  */
    const val COMMENT_LINE = ';'

    /** The Constant S_COMMENT_LINE.  */
    const val S_COMMENT_LINE = COMMENT_LINE.toString()

    /** The Constant CODEWORD_START.  */
    const val CODEWORD_START = '{'

    /** The Constant CODEWORD_END.  */
    const val CODEWORD_END = '}'

    /** The Constant S_CODEWORD_START.  */
    const val S_CODEWORD_START = CODEWORD_START.toString()

    /** The Constant S_CODEWORD_END.  */
    const val S_CODEWORD_END = CODEWORD_END.toString()

    /** The Constant TABLE_SEPARATOR.  */
    const val TABLE_SEPARATOR = "="

    /** The Constant OFFSET_CHAR_SEPARATOR.  */
    const val OFFSET_CHAR_SEPARATOR = "-"

    /** The Constant OFFSET_LENGTH_SEPARATOR.  */
    const val OFFSET_LENGTH_SEPARATOR = ":"

    /** The Constant OFFSET_STR_SEPARATOR.  */
    const val OFFSET_STR_SEPARATOR = ","

    /** The Constant EXTRACT_EXTENSION.  */
    const val EXTRACT_EXTENSION = ".ext"

    /** The Constant EXTRACT_EXTENSION_NODOT.  */
    const val EXTRACT_EXTENSION_NODOT = "ext"

    /** The Constant OFFSET_EXTENSION.  */
    const val OFFSET_EXTENSION = ".off"

    /** The Constant TBL_EXTENSION_REGEX.  */
    const val TBL_EXTENSION_REGEX = "[.]tbl"

    /** The Constant PAD_CHAR.  */
    const val PAD_CHAR: Byte = 0

    /** The Constant PAD_CHAR_STRING.  */
    const val PAD_CHAR_STRING = '0'

    /** The Constant EMPTY.  */
    const val EMPTY = ""

    /** The Constant HEXCHARS.  */
    const val HEXCHARS = "0123456789ABCDEFabcdef"

    /** The Constant HEX_16_FORMAT.  */
    const val HEX_16_FORMAT = "%02X"

    /** The Constant UTF8_ENCODING.  */
    const val UTF8_ENCODING = "UTF8"

    /** The Constant HEX_ADDR_SIZE.  */
    const val HEX_ADDR_SIZE = 8

    /** The Constant CHAR_SIZE.  */
    const val CHAR_SIZE = 1

    /** The Constant HEX_SIZE.  */
    const val HEX_SIZE = 2

    /** The Constant HEX_RADIX.  */
    const val HEX_RADIX = 16

    /** The Constant REGEX_CLEAN_TEXT.  */
    const val REGEX_CLEAN_TEXT = "((\\||@|;).*|~.{2}~|\\#.*)"

    /** The Constant REGEX_CLEAN_SPACES.  */
    const val REGEX_CLEAN_SPACES = "[^\\S\n]{2,}"

    /** The Constant REGEX_MULTI_SPACES.  */
    const val REGEX_MULTI_SPACES = "\\s{2,}"

    /** The Constant REGEX_NOT_LETTER_DIGIT.  */
    const val REGEX_NOT_LETTER_DIGIT = "[^a-zA-z1-9'\\s]"

    /** The Constant REGEX_DICTIONARY_CHARS.  */
    const val REGEX_DICTIONARY_CHARS = "(\\{|\\})"

    /** The Constant MIN_NUM_CHARS_WORD.  */
    const val MIN_NUM_CHARS_WORD = 3

    /** The Constant UTF_8_BOM_BE.  */
    const val UTF_8_BOM_BE = "\uFEFF"

    /** The Constant UTF_8_BOM_LE.  */
    const val UTF_8_BOM_LE = "\uFFFE"

    /** The Constant RESERVED_CHARS.  */
    val RESERVED_CHARS =
        listOf(
            HEX_CHAR.toString(),
            ADDR_STR,
            S_MAX_BYTES,
            STR_NUM_CHARS.toString(),
            S_COMMENT_LINE,
            S_CODEWORD_START,
            S_CODEWORD_END
        )

    /** The Constant DEFAULT_DICT.  */
    const val DEFAULT_DICT = "EngDict.txt"

    /** The Constant EXTENSIONS_MEGADRIVE.  */
    val EXTENSIONS_MEGADRIVE = listOf("smd", "gen", "bin", "md", "32x")

    /** The Constant EXTENSIONS_SNES.  */
    val EXTENSIONS_SNES = listOf("sfc", "smc")

    /** The Constant EXTENSIONS_GB.  */
    val EXTENSIONS_GB = listOf("gb", "gbc")

    /** The Constant EXTENSIONS_TAP.  */
    val EXTENSIONS_TAP = listOf("tap")

    /** The Constant EXTENSIONS_TZX_CDT.  */
    val EXTENSIONS_TZX_CDT = listOf("tzx", "cdt")

    /** The Constant EXTENSIONS_SMS.  */
    val EXTENSIONS_SMS = listOf("sms")

    /** The Constant FILE_TYPE_OTHER.  */
    const val FILE_TYPE_OTHER = "0"
    const val FILE_TYPE_MEGA_DRIVE = "1"
    const val FILE_TYPE_MASTER_SYSTEM = "2"
    const val FILE_TYPE_NGB = "3"
    const val FILE_TYPE_SNES = "4"
    const val FILE_TYPE_ZXTAP = "5"
    const val FILE_TYPE_TZX = "6"
    const val SYSTEM_SMD = "Mega Drive"
    const val SYSTEM_SMS = "Master System"
    const val SYSTEM_SMD_32X = "Mega Drive 32X"
    const val SYSTEM_SFC = "Super Nintendo"
    const val SYSTEM_NES = "NES"
    const val SYSTEM_GB = "Game Boy"
    const val SYSTEM_GBC = "Game Boy Color"
    const val SYSTEM_PCE = "PC Engine"
    const val SYSTEM_MSX = "MSX"
    const val SYSTEM_ZXS = "ZX Spectrum"
    const val SYSTEM_CPC = "Amstrad CPC"
    const val SYSTEM_GBA = "Game Boy Advance"
    const val SYSTEM_SGG = "Game Gear"
    const val SYSTEM_SG1K = "SG-1000"
    const val SYSTEM_COL = "Colecovision"
    const val SYSTEM_NGP = "Neo Geo Pocket"
    const val SYSTEM_NGPC = "Neo Geo Pocket Color"
    const val SYSTEM_SPV = "Supervision"
    val EXTENSION_TO_SYSTEM = HashMap<String, String>()

    init {
        // Micropcs
        EXTENSION_TO_SYSTEM["mx1"] = SYSTEM_MSX
        EXTENSION_TO_SYSTEM["mx2"] = SYSTEM_MSX
        EXTENSION_TO_SYSTEM["rom"] = SYSTEM_MSX
        EXTENSION_TO_SYSTEM["tap"] = SYSTEM_ZXS
        EXTENSION_TO_SYSTEM["tzx"] = SYSTEM_ZXS
        EXTENSION_TO_SYSTEM["cdt"] = SYSTEM_CPC

        // Watara
        EXTENSION_TO_SYSTEM["sv"] = SYSTEM_SPV

        // Sega
        EXTENSION_TO_SYSTEM["smd"] = SYSTEM_SMD
        EXTENSION_TO_SYSTEM["gen"] = SYSTEM_SMD
        EXTENSION_TO_SYSTEM["bin"] = SYSTEM_SMD
        EXTENSION_TO_SYSTEM["md"] = SYSTEM_SMD
        EXTENSION_TO_SYSTEM["32x"] = SYSTEM_SMD_32X
        EXTENSION_TO_SYSTEM["sms"] = SYSTEM_SMS
        EXTENSION_TO_SYSTEM["gg"] = SYSTEM_SGG
        EXTENSION_TO_SYSTEM["sg"] = SYSTEM_SG1K

        // Nintendo
        EXTENSION_TO_SYSTEM["sfc"] = SYSTEM_SFC
        EXTENSION_TO_SYSTEM["smc"] = SYSTEM_SFC
        EXTENSION_TO_SYSTEM["nes"] = SYSTEM_NES
        EXTENSION_TO_SYSTEM["gb"] = SYSTEM_GB
        EXTENSION_TO_SYSTEM["gbc"] = SYSTEM_GBC
        EXTENSION_TO_SYSTEM["gba"] = SYSTEM_GBA

        // Hudson
        EXTENSION_TO_SYSTEM["pce"] = SYSTEM_PCE

        // SNK
        EXTENSION_TO_SYSTEM["ngp"] = SYSTEM_NGP
        EXTENSION_TO_SYSTEM["ngc"] = SYSTEM_NGPC

        // Coleco
        EXTENSION_TO_SYSTEM["col"] = SYSTEM_COL
    }
}
