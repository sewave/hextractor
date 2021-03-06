package com.wave.hextractor;

import com.wave.hextractor.gui.HexViewer;
import com.wave.hextractor.util.*;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Main class that routes all the options.
 * @author slcantero
 */
public class Hextractor {

	/** The Constant MODE_FIX_MEGADRIVE_CHECKSUM. */
	public static final String MODE_FIX_MEGADRIVE_CHECKSUM = "-fcm";

	/** The Constant MODE_FIX_GAMEBOY_CHECKSUM. */
	public static final String MODE_FIX_GAMEBOY_CHECKSUM = "-fcg";

	/** The Constant MODE_FIX_SNES_CHECKSUM. */
	public static final String MODE_FIX_SNES_CHECKSUM = "-fcs";

	/** The Constant MODE_FIX_ZXTAP_CHECKSUM. */
	public static final String MODE_FIX_ZXTAP_CHECKSUM = "-fctap";

	/** The Constant MODE_FIX_ZXTZX_CHECKSUM. */
	public static final String MODE_FIX_ZXTZX_CHECKSUM = "-fctzx";

	/** The Constant MODE_FIX_SMS_CHECKSUM. */
	public static final String MODE_FIX_SMS_CHECKSUM = "-fcsms";

	/** The Constant MODE_CLEAN_ASCII. */
	public static final String MODE_CLEAN_ASCII = "-ca";

	/** The Constant MODE_INSERT_HEX. */
	public static final String MODE_INSERT_HEX = "-ih";

	/** The Constant MODE_INSERT_FILE. */
	public static final String MODE_INSERT_FILE = "-if";

	/** The Constant MODE_ASCII_TO_HEX. */
	public static final String MODE_ASCII_TO_HEX = "-h";

	/** The Constant MODE_EXTRACT_ASCII. */
	public static final String MODE_EXTRACT_ASCII = "-a";

	/** The Constant MODE_SEARCH_RELATIVE_8. */
	public static final String MODE_SEARCH_RELATIVE_8 = "-sr8";

	/** The Constant MODE_SEARCH_ALL. */
	public static final String MODE_SEARCH_ALL = "-sa";

	/** The Constant MODE_CLEAN_EXTRACTED_FILE. */
	public static final String MODE_CLEAN_EXTRACTED_FILE = "-cef";

	/** The Constant CREATE_IPS_PATCH. */
	public static final String CREATE_IPS_PATCH = "-cip";

	/** The Constant MODE_VERIFY_IPS_PATCH. */
	public static final String MODE_VERIFY_IPS_PATCH = "-vip";

	/** The Constant MODE_APPLY_IPS_PATCH. */
	public static final String MODE_APPLY_IPS_PATCH = "-aip";

	/** The Constant MODE_EXTRACT_HEX. */
	public static final String MODE_EXTRACT_HEX = "-eh";

	/** The Constant MODE_HEX_VIEW. */
	public static final String MODE_HEX_VIEW = "-hv";

	/** The Constant MODE_CHECK_LINE_LENGTH. */
	public static final String MODE_CHECK_LINE_LENGTH = "-cll";

	/** The Constant MODE_EXTRACT_ASCII_3_4. */
	public static final String MODE_EXTRACT_ASCII_3_4 = "-a34";

	/** The Constant MODE_INSERT_ASCII_4_3. */
	public static final String MODE_INSERT_ASCII_4_3 = "-h43";

	/** The Constant MODE_SEPARATE_CHAR_LENGTH. */
	public static final String MODE_SEPARATE_CHAR_LENGTH = "-scl";
	
	/** The Constant MODE_GENERATE_FILE_DIGESTS. */
	public static final String MODE_GENERATE_FILE_DIGESTS = "-gd";
	
	/** The Constant MODE_FILL_READ_ME. */
	public static final String MODE_FILL_READ_ME = "-frm";

	/**
	 * Prints the usage.
	 */
	private static void printUsage(ResourceBundle rb) {
		Utils.log(rb.getString(KeyConstants.KEY_CONSOLE_HELP));
	}

	/**
	 * Main program start.
	 *
	 * @param args the arguments
	 * @throws Exception the exception
	 */
	public static void main(String[] args) throws Exception {
		ResourceBundle rb = ResourceBundle.getBundle(Constants.RB_NAME, Locale.getDefault());
		Utils.log(rb.getString(KeyConstants.KEY_CONSOLE_HEADER));
		if (args.length > 0) {
			if(FileUtils.allFilesExist(args)) {
				Utils.log("Generating projects...");
				for(String file : args) {
					try {
						ProjectUtils.createProject(file);
						Utils.log("------------------");
					} catch (Exception e) {
						Utils.logException(e);
					}
				}
				Utils.log("Projects generated!!!");
			}
			else {
				manageModes(args, rb);
			}
		} else {
			HexViewer.view();
		}
	}

	/**
	 * Manage app modes.
	 *
	 * @param args the args
	 * @param rb the rb
	 * @throws IOException the exception
	 */
	private static void manageModes(String[] args, ResourceBundle rb) throws IOException {
		String mode = args[0];
		switch (args.length) {
		case 2:
			manageModes2Args(args, rb, mode);
			break;
		case 3:
			manageModes3Args(args, rb, mode);
			break;
		case 4:
			manageModes4Args(args, rb, mode);
			break;
		case 5:
			manageModes5Args(args, rb, mode);
			break;
		case 6:
			if (MODE_SEARCH_ALL.equals(mode)) {
				FileUtils.searchAllStrings(args[1], args[2], Integer.parseInt(args[3]), args[4], args[5]);
			} else {
				printUsage(rb);
			}
			break;
		default:
		case 1:
			if (MODE_HEX_VIEW.equals(mode)) {
				HexViewer.view();
			} else {
				printUsage(rb);
			}
			break;
		}
	}

	private static void manageModes5Args(String[] args, ResourceBundle rb, String mode) throws IOException {
		if (MODE_EXTRACT_ASCII.equals(mode)) {
			FileUtils.extractAsciiFile(args[1], args[2], args[3], args[4]);
		} else {
			if (MODE_SEARCH_ALL.equals(mode)) {
				FileUtils.searchAllStrings(args[1], args[2], Integer.parseInt(args[3]), args[4]);
			} else {
				if (MODE_EXTRACT_ASCII_3_4.equals(mode)) {
					FileUtils.extractAscii3To4Data(args[1], args[2], args[3], args[4]);
				} else {
					printUsage(rb);
				}
			}
		}
	}

	private static void manageModes4Args(String[] args, ResourceBundle rb, String mode) throws IOException {
		switch (mode) {
			case MODE_ASCII_TO_HEX:
				FileUtils.insertAsciiAsHex(args[1], args[2], args[3]);
				break;
			case MODE_SEARCH_RELATIVE_8:
				FileUtils.searchRelative8Bits(args[1], args[2], args[3]);
				break;
			case CREATE_IPS_PATCH:
				IpsPatchUtils.createIpsPatch(args[1], args[2], args[3]);
				break;
			case MODE_APPLY_IPS_PATCH:
				IpsPatchUtils.applyIpsPatch(args[1], args[2], args[3]);
				break;
			case MODE_VERIFY_IPS_PATCH:
				IpsPatchUtils.validateIpsPatch(args[1], args[2], args[3]);
				break;
			case MODE_EXTRACT_HEX:
				FileUtils.extractHexData(args[1], args[2], args[3]);
				break;
			case MODE_INSERT_ASCII_4_3:
				FileUtils.insertHex4To3Data(args[1], args[2], args[3]);
				break;
			case MODE_SEPARATE_CHAR_LENGTH:
				FileUtils.separateCharLength(args[1], args[2], args[3]);
				break;
			case MODE_INSERT_FILE:
				FileUtils.replaceFileData(args[1], args[2],
						Integer.valueOf(args[3], Constants.HEX_RADIX));
				break;
			case MODE_FILL_READ_ME:
				FileUtils.fillGameData(args[1], args[2], args[3]);
				break;
			default:
				printUsage(rb);
				break;
		}
	}

	private static void manageModes3Args(String[] args, ResourceBundle rb, String mode) throws IOException {
		switch (mode) {
			case MODE_CLEAN_ASCII:
				FileUtils.cleanAsciiFile(args[1], args[2]);
				break;
			case MODE_INSERT_HEX:
				FileUtils.insertHexData(args[1], args[2]);
				break;
			case MODE_CLEAN_EXTRACTED_FILE:
				FileUtils.cleanExtractedFile(args[1], args[2]);
				break;
			case MODE_HEX_VIEW:
				HexViewer.view(args[1], args[2]);
				break;
			case MODE_FIX_ZXTAP_CHECKSUM:
				TAPChecksumUtils.checkUpdateZxTapChecksum(args[1], args[2]);
				break;
			case MODE_FIX_ZXTZX_CHECKSUM:
				TAPChecksumUtils.checkUpdateZxTzxChecksum(args[1], args[2]);
				break;
			default:
				printUsage(rb);
				break;
		}
	}

	private static void manageModes2Args(String[] args, ResourceBundle rb, String mode) throws IOException {
		switch (mode) {
			case MODE_FIX_MEGADRIVE_CHECKSUM:
				SMDChecksumUtils.checkUpdateMegaDriveChecksum(args[1]);
				break;
			case MODE_FIX_GAMEBOY_CHECKSUM:
				GBChecksumUtils.checkUpdateGameBoyChecksum(args[1]);
				break;
			case MODE_FIX_SNES_CHECKSUM:
				SNESChecksumUtils.checkUpdateSnesChecksum(args[1]);
				break;
			case MODE_HEX_VIEW:
				HexViewer.view(args[1]);
				break;
			case MODE_FIX_ZXTAP_CHECKSUM:
				TAPChecksumUtils.checkUpdateZxTapChecksum(args[1]);
				break;
			case MODE_FIX_ZXTZX_CHECKSUM:
				TAPChecksumUtils.checkUpdateZxTzxChecksum(args[1]);
				break;
			case MODE_FIX_SMS_CHECKSUM:
				SMSChecksumUtils.checkUpdateSMSChecksum(args[1]);
				break;
			case MODE_CHECK_LINE_LENGTH:
				FileUtils.checkLineLength(args[1]);
				break;
			case MODE_GENERATE_FILE_DIGESTS:
				FileUtils.outputFileDigests(args[1]);
				break;
			default:
				printUsage(rb);
				break;
		}
	}

}
