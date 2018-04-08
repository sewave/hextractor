package com.wave.hextractor.util;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;

import com.wave.hextractor.Hextractor;

/**
 * The Class ProjectUtils.
 */
public class ProjectUtils {

	/** The Constant ECHO_OFF. */
	private static final String ECHO_OFF = "@echo off";

	/** The Constant PAUSE. */
	private static final String PAUSE = "pause";

	/** The Constant PROG_CALL. */
	private static final String PROG_CALL = "java -jar Hextractor.jar ";

	/** The Constant EXCUTEHEXVIEWER_FILE. */
	private static final String EXCUTEHEXVIEWER_FILE = "0.hexviewer.bat";

	/** The Constant SEARCHALL_FILE. */
	private static final String SEARCHALL_FILE = "1.searchAll.bat";

	/** The Constant CLEANEXTRACTED_FILE. */
	private static final String CLEANEXTRACTED_FILE = "2.cleanAutoExtract.bat";

	/** The Constant EXTRACTHEX_FILE. */
	private static final String EXTRACTHEX_FILE = "3.extractHex.bat";

	/** The Constant ORIGINALSCRIPT_FILE. */
	private static final String ORIGINALSCRIPT_FILE = "4.originalScript.bat";

	/** The Constant CLEAN_FILE. */
	private static final String CLEAN_FILE = "5.clean.bat";

	/** The Constant INSERT_FILE. */
	private static final String INSERT_FILE = "6.insert.bat";

	/** The Constant CREATEPATCH_FILE. */
	private static final String CREATEPATCH_FILE = "7.createPatch.bat";

	/** The Constant HEX_EXTENSION. */
	private static final String HEX_EXTENSION = ".hex";

	/** The Constant TR_FILENAME_PREFIX. */
	private static final String TR_FILENAME_PREFIX = "TR_";

	/** The Constant YEAR. */
	private static final String YEAR = String.valueOf(Calendar.getInstance().get(Calendar.YEAR));

	/** The Constant SCRIPTNAME_VAR. */
	private static final String SCRIPTNAME_VAR = "%SCRIPTNAME%";

	/** The Constant TFILENAMENAME_VAR. */
	private static final String TFILENAMENAME_VAR = "%T_FILENAME%";

	/** The Constant SFILENAMENAME_VAR. */
	private static final String SFILENAMENAME_VAR = "%S_FILENAME%";

	/** The Constant FILE_HEXTRACTOR. */
	private static final String FILE_HEXTRACTOR = "Hextractor.jar";

	/** The Constant FILE_README. */
	private static final String FILE_README = "_readme.txt";

	/**
	 * Gets the tfile name.
	 *
	 * @param name the name
	 * @return the tfile name
	 */
	private static final String getTfileName(String name) {
		return "set T_FILENAME=\"" + name + "\"";
	}

	/**
	 * Gets the sfile name.
	 *
	 * @param name the name
	 * @return the sfile name
	 */
	private static final String getSfileName(String name) {
		return "set S_FILENAME=\"" + name + "\"";
	}

	/**
	 * Gets the script name.
	 *
	 * @param name the name
	 * @return the script name
	 */
	private static final String getScriptName(String name) {
		return "set SCRIPTNAME=\"" + name + "\"";
	}

	/**
	 * Creates the new project.
	 *
	 * @param name the name
	 * @param fileName the file name
	 * @param fileType the file type
	 * @param projectFile the project file
	 * @throws Exception the exception
	 */
	public static final void createNewProject(String name, String fileName, String fileType, File projectFile) throws Exception {
		File projectFolder = createProjectFolder(name);
		String transfileName = TR_FILENAME_PREFIX + fileName;
		copyBaseFiles(projectFolder, name, projectFile);
		Utils.createFile(Utils.getJoinedFileName(projectFolder, EXCUTEHEXVIEWER_FILE), createHexviewerFile(name, fileName));
		Utils.createFile(Utils.getJoinedFileName(projectFolder, SEARCHALL_FILE), createSearchAllFile(name, fileName));
		Utils.createFile(Utils.getJoinedFileName(projectFolder, CLEANEXTRACTED_FILE), createCleanExtractedFile(fileName));
		Utils.createFile(Utils.getJoinedFileName(projectFolder, EXTRACTHEX_FILE), createExtractHexFile(name, fileName, transfileName));
		Utils.createFile(Utils.getJoinedFileName(projectFolder, ORIGINALSCRIPT_FILE), createOriginalScriptFile(name, fileName));
		Utils.createFile(Utils.getJoinedFileName(projectFolder, CLEAN_FILE), createCleanFile(name));
		Utils.createFile(Utils.getJoinedFileName(projectFolder, INSERT_FILE), createInsertFile(name, fileName, fileType, transfileName));
		Utils.createFile(Utils.getJoinedFileName(projectFolder, CREATEPATCH_FILE), createCreatePatchFile(name, fileName, transfileName));
		Utils.createFile(Utils.getJoinedFileName(projectFolder, name + HEX_EXTENSION), createHexFile(name));
	}

	/**
	 * Creates the project.
	 *
	 * @param file the file
	 * @throws Exception the exception
	 */
	public static final void createProject(File file) throws Exception {
		ProjectUtils.createNewProject(getProjectName(file.getName()), file.getName(), getFileType(file), file);
	}

	/**
	 * Gets the file type.
	 *
	 * @param file the file
	 * @return the file type
	 */
	private static final String getFileType(File file) {
		String res = Constants.FILE_TYPE_OTHER;
		if(file != null) {
			String extension =  FileUtils.getFileExtension(file);
			if(Constants.EXTENSIONS_MEGADRIVE.contains(extension)) {
				res = Constants.FILE_TYPE_MEGADRIVE;
			}
			else {
				if(Constants.EXTENSIONS_SNES.contains(extension)) {
					res = Constants.FILE_TYPE_SNES;
				}
				else {
					if(Constants.EXTENSIONS_GB.contains(extension)) {
						res = Constants.FILE_TYPE_NGB;
					}
					else {
						if(Constants.EXTENSIONS_TAP.contains(extension)) {
							res = Constants.FILE_TYPE_ZXTAP;
						}
						else {
							if(Constants.EXTENSIONS_TZX_CDT.contains(extension)) {
								res = Constants.FILE_TYPE_TZX;
							}
						}
					}
				}
			}
		}
		return res;
	}

	/**
	 * Creates the hexviewer file.
	 *
	 * @param name the name
	 * @param fileName the file name
	 * @return the string
	 */
	private static final String createHexviewerFile(String name, String fileName) {
		StringBuilder fileContent = new StringBuilder();
		System.out.println("Generating / Generando " + EXCUTEHEXVIEWER_FILE + "...");
		fileContent.append(ECHO_OFF).append(Constants.NEWLINE);
		fileContent.append(PROG_CALL).append(Constants.NEWLINE);
		fileContent.append(PAUSE).append(Constants.NEWLINE);
		return fileContent.toString();
	}

	/**
	 * Creates the clean file.
	 *
	 * @param name the name
	 * @return the string
	 */
	private static final String createCleanFile(String name) {
		StringBuilder fileContent = new StringBuilder();
		System.out.println("Generating / Generando " + CLEAN_FILE + "...");
		fileContent.append(ECHO_OFF).append(Constants.NEWLINE);
		fileContent.append(getScriptName(name)).append(Constants.NEWLINE);
		fileContent.append(PROG_CALL).append("-ca tr_"+ SCRIPTNAME_VAR +".ext tr_"+ SCRIPTNAME_VAR +"_clean.ext").append(Constants.NEWLINE);
		fileContent.append(PAUSE).append(Constants.NEWLINE);
		return fileContent.toString();
	}

	/**
	 * Creates the original script file.
	 *
	 * @param name the name
	 * @param fileName the file name
	 * @return the string
	 */
	private static final String createOriginalScriptFile(String name, String fileName) {
		StringBuilder fileContent = new StringBuilder();
		System.out.println("Generating / Generando " + ORIGINALSCRIPT_FILE + "...");
		fileContent.append(ECHO_OFF).append(Constants.NEWLINE);
		fileContent.append(getTfileName(fileName)).append(Constants.NEWLINE);
		fileContent.append(getScriptName(name)).append(Constants.NEWLINE);
		fileContent.append(PROG_CALL).append("-a "+ SCRIPTNAME_VAR +".tbl "+ TFILENAMENAME_VAR +" "+ SCRIPTNAME_VAR +".ext "+ SCRIPTNAME_VAR +".off").append(Constants.NEWLINE);
		fileContent.append(PAUSE).append(Constants.NEWLINE);
		return fileContent.toString();
	}

	/**
	 * Creates the insert file.
	 *
	 * @param name the name
	 * @param fileName the file name
	 * @param fileType the file type
	 * @param transfileName the transfile name
	 * @return the string
	 */
	private static final String createInsertFile(String name, String fileName, String fileType, String transfileName) {
		StringBuilder fileContent = new StringBuilder();
		System.out.println("Generating / Generando " + INSERT_FILE + "...");
		fileContent.append(ECHO_OFF).append(Constants.NEWLINE);
		fileContent.append(getTfileName(transfileName)).append(Constants.NEWLINE);
		fileContent.append(getSfileName(fileName)).append(Constants.NEWLINE);
		fileContent.append(getScriptName(name)).append(Constants.NEWLINE);
		fileContent.append("del " + TFILENAMENAME_VAR).append(Constants.NEWLINE);
		fileContent.append("copy " + SFILENAMENAME_VAR + " " + TFILENAMENAME_VAR).append(Constants.NEWLINE);
		fileContent.append(PROG_CALL).append("-ih "+ SCRIPTNAME_VAR +".hex " + TFILENAMENAME_VAR).append(Constants.NEWLINE);
		fileContent.append(PROG_CALL).append("-h "+ SCRIPTNAME_VAR +".tbl tr_"+ SCRIPTNAME_VAR +".ext " + TFILENAMENAME_VAR).append(Constants.NEWLINE);
		String checksumMode = Constants.EMPTY;
		if(Constants.FILE_TYPE_MEGADRIVE.equals(fileType) || fileName.endsWith(".32x")) {
			checksumMode = Hextractor.MODE_FIX_MEGADRIVE_CHECKSUM;
		}
		else {
			if(Constants.FILE_TYPE_NGB.equals(fileType)) {
				checksumMode = Hextractor.MODE_FIX_GAMEBOY_CHECKSUM;
			}
			else {
				if(Constants.FILE_TYPE_SNES.equals(fileType)) {
					checksumMode = Hextractor.MODE_FIX_SNES_CHECKSUM;
				}
				else {
					if(Constants.FILE_TYPE_ZXTAP.equals(fileType)) {
						checksumMode = Hextractor.MODE_FIX_ZXTAP_CHECKSUM;
					}
					else {
						if(Constants.FILE_TYPE_TZX.equals(fileType)) {
							checksumMode = Hextractor.MODE_FIX_ZXTZX_CHECKSUM;
						}
					}
				}
			}
		}
		if(checksumMode.length() > 0) {
			fileContent.append(PROG_CALL).append(checksumMode + " " + TFILENAMENAME_VAR).append(Constants.NEWLINE);
		}
		fileContent.append(PAUSE).append(Constants.NEWLINE);
		return fileContent.toString();
	}

	/**
	 * Creates the create patch file.
	 *
	 * @param name the name
	 * @param fileName the file name
	 * @param transFileName the trans file name
	 * @return the string
	 */
	private static final String createCreatePatchFile(String name, String fileName, String transFileName) {
		StringBuilder fileContent = new StringBuilder();
		System.out.println("Generating / Generando " + CREATEPATCH_FILE + "...");
		fileContent.append(ECHO_OFF).append(Constants.NEWLINE);
		fileContent.append(getTfileName(transFileName)).append(Constants.NEWLINE);
		fileContent.append(getSfileName(fileName)).append(Constants.NEWLINE);
		fileContent.append(getScriptName(name)).append(Constants.NEWLINE);
		fileContent.append(PROG_CALL).append("-cip " + SFILENAMENAME_VAR + " " + TFILENAMENAME_VAR + " " + SCRIPTNAME_VAR +".ips").append(Constants.NEWLINE);
		fileContent.append(PAUSE).append(Constants.NEWLINE);
		return fileContent.toString();
	}

	/**
	 * Creates the search all file.
	 *
	 * @param name the name
	 * @param fileName the file name
	 * @return the string
	 */
	private static final String createSearchAllFile(String name, String fileName) {
		StringBuilder fileContent = new StringBuilder();
		System.out.println("Generating / Generando " + SEARCHALL_FILE + "...");
		fileContent.append(ECHO_OFF).append(Constants.NEWLINE);
		fileContent.append(getTfileName(fileName)).append(Constants.NEWLINE);
		fileContent.append(getScriptName(name)).append(Constants.NEWLINE);
		fileContent.append(PROG_CALL).append("-sa "+SCRIPTNAME_VAR+".tbl "+TFILENAMENAME_VAR+" 4 FF \"..\\EngDict.txt\"").append(Constants.NEWLINE);
		fileContent.append(PAUSE).append(Constants.NEWLINE);
		return fileContent.toString();
	}

	/**
	 * Creates the hex file.
	 *
	 * @param name the name
	 * @return the string
	 */
	private static final String createHexFile(String name) {
		StringBuilder fileContent = new StringBuilder();
		System.out.println("Generating / Generando " + name + HEX_EXTENSION + "...");
		fileContent.append(";Traducciones Wave " + YEAR).append(Constants.NEWLINE);
		fileContent.append(";54 72 61 64 75 63 63 69 6F 6E 65 73 20 57 61 76 65 20 3");
		fileContent.append(YEAR.substring(0, 1) + " 3" + YEAR.substring(1, 2) + " 3" + YEAR.substring(2, 3) + " 3" + YEAR.substring(3, 4) + "@000000E0:000000F5").append(Constants.NEWLINE);
		return fileContent.toString();
	}

	/**
	 * Creates the clean extracted file.
	 *
	 * @param fileName the file name
	 * @return the string
	 */
	private static final String createCleanExtractedFile(String fileName) {
		StringBuilder fileContent = new StringBuilder();
		System.out.println("Generating / Generando " + CLEANEXTRACTED_FILE + "...");
		fileContent.append(ECHO_OFF).append(Constants.NEWLINE);
		fileContent.append(getTfileName(fileName)).append(Constants.NEWLINE);
		fileContent.append(PROG_CALL).append("-cef "+ TFILENAMENAME_VAR +".ext "+ TFILENAMENAME_VAR +".ext.off").append(Constants.NEWLINE);
		fileContent.append(PAUSE).append(Constants.NEWLINE);
		return fileContent.toString();
	}

	/**
	 * Creates the extract hex file.
	 *
	 * @param name the name
	 * @param fileName the file name
	 * @param transfileName the transfile name
	 * @return the string
	 */
	private static final String createExtractHexFile(String name, String fileName, String transfileName) {
		StringBuilder fileContent = new StringBuilder();
		System.out.println("Generating / Generando " + EXTRACTHEX_FILE + "...");
		fileContent.append(ECHO_OFF).append(Constants.NEWLINE);
		fileContent.append(getTfileName(transfileName)).append(Constants.NEWLINE);
		fileContent.append(getScriptName(name)).append(Constants.NEWLINE);
		fileContent.append(PROG_CALL).append("-eh "+ TFILENAMENAME_VAR +" "+ SCRIPTNAME_VAR +".ext.hex").append(Constants.NEWLINE);
		fileContent.append(PAUSE).append(Constants.NEWLINE);
		return fileContent.toString();
	}

	/**
	 * Copy base files.
	 *
	 * @param projectFolder the project folder
	 * @param name the name
	 * @param projectFile the project file
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private static final void copyBaseFiles(File projectFolder, String name, File projectFile) throws IOException {
		Utils.copyFileUsingStream(FILE_HEXTRACTOR, Utils.getJoinedFileName(projectFolder, FILE_HEXTRACTOR));
		Utils.copyFileUsingStream(FILE_README, Utils.getJoinedFileName(projectFolder, name + FILE_README));
		if(projectFile != null) {
			Utils.copyFileUsingStream(projectFile.getAbsolutePath(), Utils.getJoinedFileName(projectFolder, projectFile.getName()));
			Utils.copyFileUsingStream(projectFile.getAbsolutePath(), Utils.getJoinedFileName(projectFolder, TR_FILENAME_PREFIX + projectFile.getName()));
		}
	}

	/**
	 * Creates the project folder.
	 *
	 * @param name the name
	 * @return the file
	 * @throws Exception the exception
	 */
	private static final File createProjectFolder(String name) throws Exception {
		File projectFolder = new File(name);
		if(!projectFolder.exists() && !projectFolder.mkdir()) {
			throw new Exception("Error generating: " + name + " directory." );
		}
		return projectFolder;
	}

	/**
	 * Gets the project name.
	 *
	 * @param fileName the file name
	 * @return the project name
	 */
	public static final String getProjectName(String fileName) {
		String projectName = fileName;
		for(String ext : Constants.EXTENSIONS_MEGADRIVE) {
			if(!"32x".equals(ext)) {
				projectName = projectName.replace("."+ext, "smd");
			}
		}
		for(String ext : Constants.EXTENSIONS_SNES) {
			projectName = projectName.replace("."+ext, "sfc");
		}
		projectName = projectName.replaceAll(" ", "");
		projectName = projectName.replaceAll("(\\(.*\\))", "");
		projectName = projectName.replaceAll("(\\[.*\\])", "");
		projectName = projectName.replaceAll("[^A-Za-z0-9]", "");
		return projectName.toLowerCase();
	}

	/**
	 * Gets the project name (based on the current directory).
	 *
	 * @return the project name
	 */
	public static final String getProjectName() {
		try {
			return new File(Constants.CURRENT_DIR).getCanonicalFile().getName();
		} catch (IOException e) {
			e.printStackTrace();
			return Constants.EMPTY;
		}
	}

}
