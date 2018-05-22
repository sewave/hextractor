package com.wave.hextractor;

import java.util.Locale;
import java.util.ResourceBundle;

import com.wave.hextractor.gui.HexViewer;
import com.wave.hextractor.util.ChecksumUtils;
import com.wave.hextractor.util.Constants;
import com.wave.hextractor.util.FileUtils;
import com.wave.hextractor.util.IpsPatchUtils;
import com.wave.hextractor.util.KeyConstants;

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

	/** The Constant MODE_CLEAN_ASCII. */
	public static final String MODE_CLEAN_ASCII = "-ca";

	/** The Constant MODE_INSERT_HEX. */
	public static final String MODE_INSERT_HEX = "-ih";

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

	/**
	 * Prints the usage.
	 */
	private static void printUsage(ResourceBundle rb) {
		System.out.println(rb.getString(KeyConstants.KEY_CONSOLE_HELP));
	}

	/**
	 * Main program start.
	 *
	 * @param args the arguments
	 * @throws Exception the exception
	 */
	public static void main(String[] args) throws Exception {
		ResourceBundle rb = ResourceBundle.getBundle(Constants.RB_NAME, Locale.getDefault());
		System.out.println(rb.getString(KeyConstants.KEY_CONSOLE_HEADER));
		String mode = "";
		if (args.length > 0) {
			mode = args[0];
			switch (args.length) {
			case 2:
				if (MODE_FIX_MEGADRIVE_CHECKSUM.equals(mode)) {
					ChecksumUtils.checkUpdateMegaDriveChecksum(args[1]);
				} else {
					if (MODE_FIX_GAMEBOY_CHECKSUM.equals(mode)) {
						ChecksumUtils.checkUpdateGameBoyChecksum(args[1]);
					} else {
						if (MODE_FIX_SNES_CHECKSUM.equals(mode)) {
							ChecksumUtils.checkUpdateSnesChecksum(args[1]);
						} else {
							if (MODE_HEX_VIEW.equals(mode)) {
								HexViewer.view(args[1]);
							} else {
								if (MODE_FIX_ZXTAP_CHECKSUM.equals(mode)) {
									ChecksumUtils.checkUpdateZxTapChecksum(args[1]);
								} else {
									if (MODE_FIX_ZXTZX_CHECKSUM.equals(mode)) {
										ChecksumUtils.checkUpdateZxTzxChecksum(args[1]);
									} else {
										if (MODE_CHECK_LINE_LENGTH.equals(mode)) {
											FileUtils.checkLineLength(args[1]);
										} else {
											printUsage(rb);
										}
									}
								}
							}
						}
					}
				}
				break;
			case 3:
				if (MODE_CLEAN_ASCII.equals(mode)) {
					FileUtils.cleanAsciiFile(args[1], args[2]);
				} else {
					if (MODE_INSERT_HEX.equals(mode)) {
						FileUtils.insertHexData(args[1], args[2]);
					} else {
						if (MODE_CLEAN_EXTRACTED_FILE.equals(mode)) {
							FileUtils.cleanExtractedFile(args[1], args[2]);
						} else {
							if (MODE_HEX_VIEW.equals(mode)) {
								HexViewer.view(args[1], args[2]);
							} else {
								if (MODE_FIX_ZXTAP_CHECKSUM.equals(mode)) {
									ChecksumUtils.checkUpdateZxTapChecksum(args[1], args[2]);
								} else {
									if (MODE_FIX_ZXTZX_CHECKSUM.equals(mode)) {
										ChecksumUtils.checkUpdateZxTzxChecksum(args[1], args[2]);
									} else {
										printUsage(rb);
									}
								}
							}
						}
					}
				}
				break;
			case 4:
				if (MODE_ASCII_TO_HEX.equals(mode)) {
					FileUtils.insertAsciiAsHex(args[1], args[2], args[3]);
				} else {
					if (MODE_SEARCH_RELATIVE_8.equals(mode)) {
						FileUtils.searchRelative8Bits(args[1], args[2], args[3]);
					} else {
						if (CREATE_IPS_PATCH.equals(mode)) {
							IpsPatchUtils.createIpsPatch(args[1], args[2], args[3]);
						} else {
							if (MODE_APPLY_IPS_PATCH.equals(mode)) {
								IpsPatchUtils.applyIpsPatch(args[1], args[2], args[3]);
							} else {
								if (MODE_VERIFY_IPS_PATCH.equals(mode)) {
									IpsPatchUtils.validateIpsPatch(args[1], args[2], args[3]);
								} else {
									if (MODE_EXTRACT_HEX.equals(mode)) {
										FileUtils.extractHexData(args[1], args[2], args[3]);
									} else {
										if (MODE_INSERT_ASCII_4_3.equals(mode)) {
											FileUtils.insertHex4To3Data(args[1], args[2], args[3]);
										} else {
											if (MODE_SEPARATE_CHAR_LENGTH.equals(mode)) {
												FileUtils.separateCharLength(args[1], args[2], args[3]);
											} else {
												printUsage(rb);
											}
										}
									}
								}
							}
						}
					}
				}
				break;
			case 5:
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
		} else {
			HexViewer.view();
		}
	}

}
