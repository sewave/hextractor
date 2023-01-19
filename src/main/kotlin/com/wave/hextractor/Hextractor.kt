package com.wave.hextractor

import com.wave.hextractor.gui.HexViewer
import com.wave.hextractor.util.Constants
import com.wave.hextractor.util.FileUtils
import com.wave.hextractor.util.GBChecksumUtils
import com.wave.hextractor.util.IpsPatchUtils
import com.wave.hextractor.util.KeyConstants
import com.wave.hextractor.util.ProjectUtils
import com.wave.hextractor.util.SMDChecksumUtils
import com.wave.hextractor.util.SMSChecksumUtils
import com.wave.hextractor.util.SNESChecksumUtils
import com.wave.hextractor.util.TAPChecksumUtils
import com.wave.hextractor.util.Utils
import java.io.IOException
import java.util.*

/**
 * Main class that routes all the options.
 */
object Hextractor {
    /** The Constant MODE_FIX_MEGADRIVE_CHECKSUM.  */
    const val MODE_FIX_MEGA_DRIVE_CHECKSUM = "-fcm"

    /** The Constant MODE_FIX_GAMEBOY_CHECKSUM.  */
    const val MODE_FIX_GAMEBOY_CHECKSUM = "-fcg"

    /** The Constant MODE_FIX_SNES_CHECKSUM.  */
    const val MODE_FIX_SNES_CHECKSUM = "-fcs"

    /** The Constant MODE_FIX_ZXTAP_CHECKSUM.  */
    const val MODE_FIX_ZXTAP_CHECKSUM = "-fctap"

    /** The Constant MODE_FIX_ZXTZX_CHECKSUM.  */
    const val MODE_FIX_ZXTZX_CHECKSUM = "-fctzx"

    /** The Constant MODE_FIX_SMS_CHECKSUM.  */
    const val MODE_FIX_SMS_CHECKSUM = "-fcsms"

    /** The Constant MODE_CLEAN_ASCII.  */
    private const val MODE_CLEAN_ASCII = "-ca"

    /** The Constant MODE_INSERT_HEX.  */
    private const val MODE_INSERT_HEX = "-ih"

    /** The Constant MODE_INSERT_FILE.  */
    private const val MODE_INSERT_FILE = "-if"

    /** The Constant MODE_ASCII_TO_HEX.  */
    private const val MODE_ASCII_TO_HEX = "-h"

    /** The Constant MODE_EXTRACT_ASCII.  */
    private const val MODE_EXTRACT_ASCII = "-a"

    /** The Constant MODE_SEARCH_RELATIVE_8.  */
    private const val MODE_SEARCH_RELATIVE_8 = "-sr8"

    /** The Constant MODE_SEARCH_ALL.  */
    private const val MODE_SEARCH_ALL = "-sa"

    /** The Constant MODE_CLEAN_EXTRACTED_FILE.  */
    private const val MODE_CLEAN_EXTRACTED_FILE = "-cef"

    /** The Constant CREATE_IPS_PATCH.  */
    const val CREATE_IPS_PATCH = "-cip"

    /** The Constant MODE_VERIFY_IPS_PATCH.  */
    private const val MODE_VERIFY_IPS_PATCH = "-vip"

    /** The Constant MODE_APPLY_IPS_PATCH.  */
    private const val MODE_APPLY_IPS_PATCH = "-aip"

    /** The Constant MODE_EXTRACT_HEX.  */
    private const val MODE_EXTRACT_HEX = "-eh"

    /** The Constant MODE_HEX_VIEW.  */
    private const val MODE_HEX_VIEW = "-hv"

    /** The Constant MODE_CHECK_LINE_LENGTH.  */
    private const val MODE_CHECK_LINE_LENGTH = "-cll"

    /** The Constant MODE_EXTRACT_ASCII_3_4.  */
    private const val MODE_EXTRACT_ASCII_3_4 = "-a34"

    /** The Constant MODE_INSERT_ASCII_4_3.  */
    private const val MODE_INSERT_ASCII_4_3 = "-h43"

    /** The Constant MODE_SEPARATE_CHAR_LENGTH.  */
    private const val MODE_SEPARATE_CHAR_LENGTH = "-scl"

    /** The Constant MODE_GENERATE_FILE_DIGESTS.  */
    private const val MODE_GENERATE_FILE_DIGESTS = "-gd"

    /** The Constant MODE_FILL_READ_ME.  */
    const val MODE_FILL_READ_ME = "-frm"

    /**
     * Prints the usage.
     */
    private fun printUsage(rb: ResourceBundle) {
        Utils.log(rb.getString(KeyConstants.KEY_CONSOLE_HELP))
    }

    /**
     * Main program start.
     *
     * @param args the arguments
     * @throws Exception the exception
     */
    @Throws(Exception::class)
    @JvmStatic
    fun main(args: Array<String>) {
        val rb = ResourceBundle.getBundle(Constants.RB_NAME, Locale.getDefault())
        Utils.log(rb.getString(KeyConstants.KEY_CONSOLE_HEADER))
        if (args.isNotEmpty()) {
            if (FileUtils.allFilesExist(args)) {
                Utils.log("Generating projects...")
                for (file in args) {
                    try {
                        ProjectUtils.createProject(file)
                        Utils.log("------------------")
                    } catch (e: Exception) {
                        Utils.logException(e)
                    }
                }
                Utils.log("Projects generated!!!")
            } else {
                manageModes(args, rb)
            }
        } else {
            HexViewer.view()
        }
    }

    /**
     * Manage app modes.
     *
     * @param args the args
     * @param rb the rb
     * @throws IOException the exception
     */
    @Throws(IOException::class)
    private fun manageModes(args: Array<String>, rb: ResourceBundle) {
        val mode = args[0]
        when (args.size) {
            2 -> manageModes2Args(args, rb, mode)
            3 -> manageModes3Args(args, rb, mode)
            4 -> manageModes4Args(args, rb, mode)
            5 -> manageModes5Args(args, rb, mode)
            6 -> if (MODE_SEARCH_ALL == mode) {
                FileUtils.searchAllStrings(args[1], args[2], args[3].toInt(), args[4], args[5])
            } else {
                printUsage(rb)
            }

            1 -> if (MODE_HEX_VIEW == mode) {
                HexViewer.view()
            } else {
                printUsage(rb)
            }

            else -> if (MODE_HEX_VIEW == mode) {
                HexViewer.view()
            } else {
                printUsage(rb)
            }
        }
    }

    @Throws(IOException::class)
    private fun manageModes5Args(args: Array<String>, rb: ResourceBundle, mode: String) {
        if (MODE_EXTRACT_ASCII == mode) {
            FileUtils.extractAsciiFile(args[1], args[2], args[3], args[4])
        } else {
            if (MODE_SEARCH_ALL == mode) {
                FileUtils.searchAllStrings(args[1], args[2], args[3].toInt(), args[4])
            } else {
                if (MODE_EXTRACT_ASCII_3_4 == mode) {
                    FileUtils.extractAscii3To4Data(args[1], args[2], args[3], args[4])
                } else {
                    printUsage(rb)
                }
            }
        }
    }

    @Throws(IOException::class)
    private fun manageModes4Args(args: Array<String>, rb: ResourceBundle, mode: String) {
        when (mode) {
            MODE_ASCII_TO_HEX -> FileUtils.insertAsciiAsHex(args[1], args[2], args[3])
            MODE_SEARCH_RELATIVE_8 -> FileUtils.searchRelative8Bits(args[1], args[2], args[3])
            CREATE_IPS_PATCH -> IpsPatchUtils.createIpsPatch(args[1], args[2], args[3])
            MODE_APPLY_IPS_PATCH -> IpsPatchUtils.applyIpsPatch(args[1], args[2], args[3])
            MODE_VERIFY_IPS_PATCH -> IpsPatchUtils.validateIpsPatch(args[1], args[2], args[3])
            MODE_EXTRACT_HEX -> FileUtils.extractHexData(args[1], args[2], args[3])
            MODE_INSERT_ASCII_4_3 -> FileUtils.insertHex4To3Data(args[1], args[2], args[3])
            MODE_SEPARATE_CHAR_LENGTH -> FileUtils.separateCharLength(args[1], args[2], args[3])
            MODE_INSERT_FILE -> FileUtils.replaceFileData(
                args[1],
                args[2],
                Integer.valueOf(args[3], Constants.HEX_RADIX)
            )
            MODE_FILL_READ_ME -> FileUtils.fillGameData(args[1], args[2], args[3])
            else -> printUsage(rb)
        }
    }

    @Throws(IOException::class)
    private fun manageModes3Args(args: Array<String>, rb: ResourceBundle, mode: String) {
        when (mode) {
            MODE_CLEAN_ASCII -> FileUtils.cleanAsciiFile(args[1], args[2])
            MODE_INSERT_HEX -> FileUtils.insertHexData(args[1], args[2])
            MODE_CLEAN_EXTRACTED_FILE -> FileUtils.cleanExtractedFile(args[1], args[2])
            MODE_HEX_VIEW -> HexViewer.view(args[1], args[2])
            MODE_FIX_ZXTAP_CHECKSUM -> TAPChecksumUtils.checkUpdateZxTapChecksum(args[1], args[2])
            MODE_FIX_ZXTZX_CHECKSUM -> TAPChecksumUtils.checkUpdateZxTzxChecksum(args[1], args[2])
            else -> printUsage(rb)
        }
    }

    @Throws(IOException::class)
    private fun manageModes2Args(args: Array<String>, rb: ResourceBundle, mode: String) {
        when (mode) {
            MODE_FIX_MEGA_DRIVE_CHECKSUM -> SMDChecksumUtils.checkUpdateMegaDriveChecksum(args[1])
            MODE_FIX_GAMEBOY_CHECKSUM -> GBChecksumUtils.checkUpdateGameBoyChecksum(args[1])
            MODE_FIX_SNES_CHECKSUM -> SNESChecksumUtils.checkUpdateSnesChecksum(args[1])
            MODE_HEX_VIEW -> HexViewer.view(args[1])
            MODE_FIX_ZXTAP_CHECKSUM -> TAPChecksumUtils.checkUpdateZxTapChecksum(args[1])
            MODE_FIX_ZXTZX_CHECKSUM -> TAPChecksumUtils.checkUpdateZxTzxChecksum(args[1])
            MODE_FIX_SMS_CHECKSUM -> SMSChecksumUtils.checkUpdateSMSChecksum(args[1])
            MODE_CHECK_LINE_LENGTH -> FileUtils.checkLineLength(args[1])
            MODE_GENERATE_FILE_DIGESTS -> FileUtils.outputFileDigests(args[1])
            else -> printUsage(rb)
        }
    }
}
