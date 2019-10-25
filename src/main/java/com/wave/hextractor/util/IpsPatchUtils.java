package com.wave.hextractor.util;

import com.wave.hextractor.pojo.IpsPatchEntry;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * The Class Patcher.
 */
public class IpsPatchUtils {

	/**
	 * Instantiates a new ips patch utils.
	 */
	private IpsPatchUtils() {
	}

	/**
	 * Creates a IPS patch file patchFile from the differences between
	 * originalFile to modifiedFile.
	 *
	 * @param originalFile original file, unmodified.
	 * @param modifiedFile file modified to create patch to.
	 * @param patchFile file name of the patch to create.
	 * @throws IOException I/O error.
	 */
	public static boolean createIpsPatch(String originalFile, String modifiedFile, String patchFile) throws IOException {
		Utils.log("Creating ips patch for \"" + modifiedFile + "\"\n based on \"" + originalFile + "\" "
				+ "\n to ips file \"" + patchFile + "\"");
		byte[] originalFileBytes = Files.readAllBytes(Paths.get(originalFile));
		byte[] modifiedFileBytes = Files.readAllBytes(Paths.get(modifiedFile));
		List<IpsPatchEntry> patchEntries = new ArrayList<>();
		int offsetStart = -1;
		byteZeroPatch(originalFileBytes, modifiedFileBytes, patchEntries);
		for (int i = 1; i < originalFileBytes.length; i++) {
			if (originalFileBytes[i] != modifiedFileBytes[i]) {
				if (i - offsetStart == Constants.IPS_PATCH_MAX_SIZE) {
					patchEntries.add(createIpsEntry(modifiedFileBytes, offsetStart, i));
					// Create entry and restart
					offsetStart = i;
				}
				if (offsetStart < 0) {
					offsetStart = i;
				}
			} else {
				// Look into the future, if there are less than 6 different
				// bytes, we continue
				if (offsetStart >= 0 && newEntryRequired(originalFileBytes, modifiedFileBytes, i)) {
					patchEntries.add(createIpsEntry(modifiedFileBytes, offsetStart, i));
					offsetStart = -1;
				}
			}
		}
		if(offsetStart > 0) {
			patchEntries.add(createIpsEntry(modifiedFileBytes, offsetStart, originalFileBytes.length));
		}
		finalPatch(modifiedFileBytes, originalFileBytes, patchEntries);
		Files.write(Paths.get(patchFile), generatePatchFile(patchEntries));
		boolean valid = validateIpsPatch(originalFile, modifiedFile, patchFile);
		FileUtils.outputFileDigests(originalFile);
		return valid;
	}

	private static void byteZeroPatch(byte[] originalFileBytes, byte[] modifiedFileBytes, List<IpsPatchEntry> patchEntries) {
		if(originalFileBytes[0] != modifiedFileBytes[0]) {
			patchEntries.add(createIpsEntry(modifiedFileBytes, 0, 1));
		}
	}

	private static void finalPatch(byte[] modifiedFileBytes, byte[] originalFileBytes, List<IpsPatchEntry> patchEntries) {
		if (modifiedFileBytes.length > originalFileBytes.length) {
			// Final patch, rom extended
			for (int i = originalFileBytes.length; i < modifiedFileBytes.length; i += Constants.IPS_PATCH_MAX_SIZE / 64) {
				int end = i + Constants.IPS_PATCH_MAX_SIZE / 64;
				if (end > modifiedFileBytes.length) {
					end = modifiedFileBytes.length;
				}
				patchEntries.add(createIpsEntry(modifiedFileBytes, i, end));
			}
		}
	}

	/**
	 * Returns true if a new entry is required.
	 *
	 * @param bytesOne the bytes one
	 * @param bytesTwo the bytes two
	 * @param offSet the off set
	 * @return true, if successful
	 */
	private static boolean newEntryRequired(byte[] bytesOne, byte[] bytesTwo, int offSet) {
		boolean required = true;
		for (int i = 0; i < IpsPatchEntry.IPS_CHUNK_MIN_SIZE && required; i++) {
			if (offSet + i >= bytesOne.length) {
				required = false;
			} else {
				if (bytesOne[i + offSet] != bytesTwo[i + offSet]) {
					required = false;
				}
			}
		}
		return required;
	}

	/**
	 * Patches originalFile with patchFile and writes it to modifiedFile.
	 *
	 * @param originalFile file to patch.
	 * @param modifiedFile file patched to output to.
	 * @param patchFile patch file.
	 * @throws IOException I/O error.
	 */
	public static void applyIpsPatch(String originalFile, String modifiedFile, String patchFile) throws IOException {
		Utils.log("Applying ips patch \"" + patchFile + "\"\n on file \"" + originalFile + "\" "
				+ "\n to output file  \"" + modifiedFile + "\"");
		Files.write(Paths.get(modifiedFile), applyIpsPatch(originalFile, getPatchEntries(patchFile)));
	}

	/**
	 * Patches on memory a file and compares it to the already patched file.
	 *
	 * @param originalFile file to patch.
	 * @param modifiedFile file patched to compare to.
	 * @param patchFile patch file.
	 * @throws IOException I/O error.
	 */
	public static boolean validateIpsPatch(String originalFile, String modifiedFile, String patchFile) throws IOException {
		boolean valid;
		Utils.log("Verifying ips patch \"" + patchFile + "\"\n on file \"" + originalFile + "\" " + "\n to file \""
				+ modifiedFile + "\"");
		if (validateIpsPatch(Files.readAllBytes(Paths.get(originalFile)), Files.readAllBytes(Paths.get(modifiedFile)),
				Files.readAllBytes(Paths.get(patchFile)))) {
			valid = true;
			Utils.log("IPS patch correct!");
		} else {
			valid = false;
			Utils.log("IPS patch NOT CORRECT!");
		}
		return valid;
	}

	/**
	 * Validate ips patch.
	 *
	 * @param originalFile the original file
	 * @param modifiedFile the modified file
	 * @param patchFile the patch file
	 * @return true, if successful
	 */
	public static boolean validateIpsPatch(byte[] originalFile, byte[] modifiedFile, byte[] patchFile) {
		return Arrays.equals(modifiedFile, applyIpsPatch(originalFile, getPatchEntries(patchFile)));
	}

	/**
	 * Creates the ips entry.
	 *
	 * @param modifiedFileBytes the modified file bytes
	 * @param offsetStart the offset start
	 * @param offsetEnd the offset end
	 * @return the ips patch entry
	 */
	private static IpsPatchEntry createIpsEntry(byte[] modifiedFileBytes, int offsetStart, int offsetEnd) {
		byte[] entryData = Arrays.copyOfRange(modifiedFileBytes, offsetStart, offsetEnd);
		IpsPatchEntry entry;
		if (Utils.allSameValue(entryData) && entryData.length > IpsPatchEntry.IPS_RLE_DATA_SIZE) {
			// RLE encoded entry
			entry = new IpsPatchEntry(offsetStart, (short) 0, (short) (offsetEnd - offsetStart),
					new byte[] { entryData[0] });
		} else {
			entry = new IpsPatchEntry(offsetStart, (short) (offsetEnd - offsetStart), entryData);
		}
		return entry;
	}

	/**
	 * Generate patch file.
	 *
	 * @param patchEntries the patch entries
	 * @return the byte[]
	 */
	private static byte[] generatePatchFile(List<IpsPatchEntry> patchEntries) {
		int patchFileSize = Constants.IPS_HEADER.length() + Constants.IPS_EOF.length();
		for (IpsPatchEntry entry : patchEntries) {
			patchFileSize += entry.getBinSize();
		}
		byte[] fileBytes = new byte[patchFileSize];
		// Copy header
		System.arraycopy(Constants.IPS_HEADER.getBytes(), 0, fileBytes, 0, Constants.IPS_HEADER.length());
		int offset = Constants.IPS_HEADER.length();
		for (IpsPatchEntry entry : patchEntries) {
			byte[] binEntryData = entry.toBin();
			System.arraycopy(binEntryData, 0, fileBytes, offset, binEntryData.length);
			offset += binEntryData.length;
		}
		// Copy end of file
		System.arraycopy(Constants.IPS_EOF.getBytes(), 0, fileBytes, offset, Constants.IPS_EOF.length());
		return fileBytes;
	}

	/**
	 * Apply ips patch.
	 *
	 * @param originalFile the original file
	 * @param patchEntries the patch entries
	 * @return the byte[]
	 * @throws IOException the exception
	 */
	private static byte[] applyIpsPatch(String originalFile, List<IpsPatchEntry> patchEntries) throws IOException {
		return applyIpsPatch(Files.readAllBytes(Paths.get(originalFile)), patchEntries);
	}

	/**
	 * Apply ips patch.
	 *
	 * @param originalFileBytes the original file bytes
	 * @param patchEntries the patch entries
	 * @return the byte[]
	 */
	private static byte[] applyIpsPatch(byte[] originalFileBytes, List<IpsPatchEntry> patchEntries) {
		int outputFileSize = originalFileBytes.length;
		int maxEntryMod = getMaxEntryOffset(patchEntries);
		if (maxEntryMod > outputFileSize) {
			outputFileSize = maxEntryMod;
		}
		byte[] modifiedFileBytes = new byte[outputFileSize];
		System.arraycopy(originalFileBytes, 0, modifiedFileBytes, 0, originalFileBytes.length);
		for (IpsPatchEntry entry : patchEntries) {
			Utils.log("Applying Patch: " + entry.toString());
			if (entry.getSize() == IpsPatchEntry.IPS_RLE_MODE) {
				Arrays.fill(modifiedFileBytes, entry.getOffset(), entry.getOffset() + entry.getRleSize(),
						entry.getData()[0]);
			} else {
				System.arraycopy(entry.getData(), 0, modifiedFileBytes, entry.getOffset(), entry.getSize());
			}
		}
		return modifiedFileBytes;
	}

	/**
	 * Returns the last offset modified (not zero based).
	 *
	 * @param patchEntries the patch entries
	 * @return the max entry offset
	 */
	private static int getMaxEntryOffset(List<IpsPatchEntry> patchEntries) {
		int maxEntryOffset = 0;
		for (IpsPatchEntry entry : patchEntries) {
			if (entry.getSize() == IpsPatchEntry.IPS_RLE_MODE) {
				if (entry.getOffset() + entry.getRleSize() > maxEntryOffset) {
					maxEntryOffset = entry.getOffset() + entry.getRleSize();
				}
			} else {
				if (entry.getOffset() + entry.getSize() > maxEntryOffset) {
					maxEntryOffset = entry.getOffset() + entry.getSize();
				}
			}
		}
		return maxEntryOffset;
	}

	/**
	 * Gets the patch entries.
	 *
	 * @param patchFile the patch file
	 * @return the patch entries
	 * @throws IOException the exception
	 */
	private static List<IpsPatchEntry> getPatchEntries(String patchFile) throws IOException {
		return getPatchEntries(Files.readAllBytes(Paths.get(patchFile)));
	}

	/**
	 * Gets the patch entries.
	 *
	 * @param patchBytes the patch bytes
	 * @return the patch entries
	 */
	private static List<IpsPatchEntry> getPatchEntries(byte[] patchBytes) {
		List<IpsPatchEntry> patchEntries = new ArrayList<>();
		int i = Constants.IPS_HEADER.length();
		while (i < patchBytes.length - Constants.IPS_EOF.length()) {
			IpsPatchEntry entry = new IpsPatchEntry();
			entry.setOffset(Utils.bytesToInt(patchBytes[i], patchBytes[i + 1], patchBytes[i + 2]));
			i += IpsPatchEntry.IPS_OFFSET_SIZE;
			entry.setSize(Utils.bytesToInt(patchBytes[i], patchBytes[i + 1]));
			i += IpsPatchEntry.IPS_DATA_SIZE;
			if(entry.getSize() == IpsPatchEntry.IPS_RLE_MODE) {
				entry.setRleSize(Utils.bytesToInt(patchBytes[i], patchBytes[i + 1]));
				i += IpsPatchEntry.IPS_RLE_DATA_SIZE;
				entry.setData(new byte[] { patchBytes[i] });
			}
			else {
				entry.setData(Arrays.copyOfRange(patchBytes, i, i + entry.getSize()));
			}
			i += entry.getData().length;
			patchEntries.add(entry);
		}
		return patchEntries;
	}

}
