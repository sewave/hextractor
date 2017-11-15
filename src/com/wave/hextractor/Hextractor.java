package com.wave.hextractor;

import com.wave.hextractor.gui.HexViewer;
import com.wave.hextractor.util.ChecksumUtils;
import com.wave.hextractor.util.FileUtils;
import com.wave.hextractor.util.IpsPatchUtils;

/**
 * Main class that routes all the options.
 * @author slcantero
 */
public class Hextractor {

	/** The Constant HEADER. */
	private static final String HEADER = "HEXTRACTOR v0.9 (c) Wave 10/11/2017 \n"
			+ " Type -? for help. / Escribe -? para ayuda";

	/** The Constant USAGE. */
	private static final String USAGE = "EXTRACT ASCII FILE FROM HEX / EXTRAER ARCHIVO ASCII DE HEXADECIMAL \n"
			+ "-a tableFile file scriptAsciiFile offsetsList (START-END-STRING_END_CHAR(1+))\n"
			+ "INSERT ASCII AS HEX / INSERTAR ASCII COMO HEX\n" + "-h scriptAsciiFile tableFile targetFile \n"
			+ "INSERT DIRECT HEX VALUES / INSERTAR HEXADECIMAL DIRECTO \n" + "-ih scriptHexFile targetFile \n"
			+ "EXTRACT DIRECT HEX VALUES / EXTRAER HEXADECIMAL \n"
			+ "-eh srcFile destFile ((INIT-END,)|(INIT:LENGTH,))1+\n" + "INTERLEAVE FILES / INTERCALAR ARCHIVOS \n"
			+ "-i evenLinesFile oddLinesFile \n" + "FIX MEGADRIVE CHECKSUM / REPARAR CHECKSUM MEGADRIVE \n"
			+ "-fcm rom \n" + "FIX GAME BOY CHECKSUM / REPARAR CHECKSUM GAME BOY \n" + "-fcg rom \n"
			+ "FIX SNES CHECKSUM / REPARAR CHECKSUM SNES \n" + "-fcs rom \n"
			+ "FIX ZX TAP CHECKSUM / REPARAR CHECKSUM ZX TAP \n" + "-fctap tap \n"
			+ "FIX ZX TAP CHECKSUM / REPARAR CHECKSUM ZX TAP \n" + "-fctap tap originalTap\n"
			+ "FIX ZX TZX OR CPC CDT CHECKSUM / REPARAR CHECKSUM ZX TZX O CPC CDT \n" + "-fctap tzx/cdt  \n"
			+ "FIX ZX TZX OR CPC CDT CHECKSUM / REPARAR CHECKSUM ZX TZX O CPC CDT \n" + "-fctap tzx/cdt originalTzx/Cdt \n"
			+ "CLEAN EXTRACTED TEXT FILE / LIMPIAR FICHERO DE TEXTO EXTRAIDO \n" + "-ca file fileCleaned \n"
			+ "SEARCH ALL STRINGS / BUSCAR TODAS LAS CADENAS \n"
			+ "-sa table file maxIgnoredUnknownChars lineEndChars dictFile (optional) \n"
			+ "CLEAN EXTRACTED FILE / LIMPIAR ARCHIVO EXTRACCION  \n" + "-cef extractFile fileOut \n"
			+ "TRANSLATE SIMILAR / TRADUCIR SIMILAR  \n" + "-trs toTransFile transFile outputFile\n"
			+ "FIND RELATIVE 8 bits / BUSCAR RELATIVO 8 bits   \n" + "-sr8 file baseTable word \n"
			+ "Hex Viewer / Visor Hexadecimal   \n" + "-hv file (optional) table (optional) \n"
			+ "CREATE IPS PATCH / CREAR PARCHE IPS\n" + "-cip originalFile modifiedFile patchFile \n"
			+ "VERIFY IPS PATCH / VERIFICAR PATCH IPS \n" + "-vip originalFile modifiedFile patchFile \n"
			+ "APPLY IPS PATCH / APLICAR PARCHE IPS \n" + "-aip originalFile modifiedFile patchFile \n"
			+ "CHECK LINE LENGTHS / VERIFICAR TAMAÑO LINEA \n" + "-cll extFile \n";

	/** The Constant MODE_TRANSLATE_SIMILAR. */
	public static final String MODE_TRANSLATE_SIMILAR = "-trs";

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

	/** The Constant MODE_INTERLEAVE_FILES. */
	public static final String MODE_INTERLEAVE_FILES = "-i";

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

	/**
	 * Prints the usage.
	 */
	private static void printUsage() {
		System.out.println(USAGE);
	}

	/**
	 * Main program start.
	 *
	 * @param args the arguments
	 * @throws Exception the exception
	 */
	public static void main(String[] args) throws Exception {
		System.out.println(HEADER);
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
											printUsage();
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
										printUsage();
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
										printUsage();
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
						printUsage();
					}
				}
				break;
			case 6:
				if (MODE_SEARCH_ALL.equals(mode)) {
					FileUtils.searchAllStrings(args[1], args[2], Integer.parseInt(args[3]), args[4], args[5]);
				} else {
					printUsage();
				}
				break;
			default:
			case 1:
				if (MODE_HEX_VIEW.equals(mode)) {
					HexViewer.view();
				} else {
					printUsage();
				}
				break;
			}
		} else {
			HexViewer.view();
		}
	}

}
