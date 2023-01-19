package com.wave.hextractor.util

import com.wave.hextractor.Hextractor
import com.wave.hextractor.util.FileUtils.getFileExtension
import java.io.File
import java.io.IOException
import java.util.*

/**
 * The Class ProjectUtils.
 */
object ProjectUtils {
    private const val ECHO_OFF = "@echo off"
    private const val PAUSE = "pause"
    private const val PROGRAM_CALL = "java -jar Hextractor.jar "
    private const val ORIGINAL_SCRIPT_FILE = "1.extractScript.bat"
    private const val EXTRACT_HEX_FILE = "2.extractHex.bat"
    private const val INSERT_FILE = "3.insertAll.bat"
    private const val CREATE_PATCH_FILE = "4.createPatch.bat"
    private const val HEX_EXTENSION = ".hex"

    /** The Constant TR_FILENAME_PREFIX.  */
    private const val TR_FILENAME_PREFIX = "TR_"

    /** The Constant YEAR.  */
    private val YEAR = Calendar.getInstance()[Calendar.YEAR].toString()

    /** The Constant SCRIPTNAME_VAR.  */
    private const val SCRIPTNAME_VAR = "%SCRIPTNAME%"

    /** The Constant TFILENAMENAME_VAR.  */
    private const val TFILENAMENAME_VAR = "%T_FILENAME%"

    /** The Constant SFILENAMENAME_VAR.  */
    private const val SFILENAMENAME_VAR = "%S_FILENAME%"

    /** The Constant FILE_HEXTRACTOR.  */
    private const val FILE_HEXTRACTOR = "Hextractor.jar"

    /** The Constant FILE_README.  */
    private const val FILE_README = "_readme.txt"

    /** The Constant LOG_GENERATING.  */
    private const val LOG_GENERATING = "Generating / Generando "

    /**
     * Gets the tfile name.
     *
     * @param name the name
     * @return the tfile name
     */
    private fun getTfileName(name: String): String {
        return "set T_FILENAME=\"$name\""
    }

    /**
     * Gets the sfile name.
     *
     * @param name the name
     * @return the sfile name
     */
    private fun getSfileName(name: String): String {
        return "set S_FILENAME=\"$name\""
    }

    /**
     * Gets the script name.
     *
     * @param name the name
     * @return the script name
     */
    private fun getScriptName(name: String): String {
        return "set SCRIPTNAME=\"$name\""
    }

    /**
     * Creates the new project.
     *
     * @param name the name
     * @param fileName the file name
     * @param fileType the file type
     * @param projectFile the project file
     * @throws IOException the exception
     */
    @Throws(IOException::class)
    fun createNewProject(name: String, fileName: String, fileType: String, projectFile: File?) {
        val projectFolder = createProjectFolder(name)
        val transfileName = TR_FILENAME_PREFIX + fileName
        copyBaseFiles(projectFolder, name, projectFile)
        Utils.createFile(
            Utils.getJoinedFileName(projectFolder, ORIGINAL_SCRIPT_FILE),
            createOriginalScriptFile(name, fileName)
        )
        Utils.createFile(
            Utils.getJoinedFileName(projectFolder, EXTRACT_HEX_FILE),
            createExtractHexFile(name, transfileName)
        )
        Utils.createFile(
            Utils.getJoinedFileName(projectFolder, INSERT_FILE),
            createInsertFile(name, fileName, fileType, transfileName)
        )
        Utils.createFile(
            Utils.getJoinedFileName(projectFolder, CREATE_PATCH_FILE),
            createCreatePatchFile(name, fileName, transfileName)
        )
        Utils.createFile(Utils.getJoinedFileName(projectFolder, name + HEX_EXTENSION), createHexFile(name))
    }

    /**
     * Creates the project.
     *
     * @param file the file
     * @throws IOException the exception
     */
    @Throws(IOException::class)
    fun createProject(file: File) {
        createNewProject(getProjectName(file.name), file.name, getFileType(file), file)
    }

    /**
     * Creates the project.
     *
     * @param file the file
     * @throws IOException the exception
     */
    @Throws(IOException::class)
    fun createProject(file: String) {
        createProject(File(file))
    }

    private fun getFileType(file: File?): String {
        var res = Constants.FILE_TYPE_OTHER
        if (file != null) {
            val extension = getFileExtension(file)
            if (Constants.EXTENSIONS_MEGADRIVE.contains(extension)) {
                res = Constants.FILE_TYPE_MEGA_DRIVE
            } else {
                if (Constants.EXTENSIONS_SNES.contains(extension)) {
                    res = Constants.FILE_TYPE_SNES
                } else {
                    if (Constants.EXTENSIONS_GB.contains(extension)) {
                        res = Constants.FILE_TYPE_NGB
                    } else {
                        if (Constants.EXTENSIONS_TAP.contains(extension)) {
                            res = Constants.FILE_TYPE_ZXTAP
                        } else {
                            if (Constants.EXTENSIONS_TZX_CDT.contains(extension)) {
                                res = Constants.FILE_TYPE_TZX
                            } else {
                                if (Constants.EXTENSIONS_SMS.contains(extension)) {
                                    res = Constants.FILE_TYPE_MASTER_SYSTEM
                                }
                            }
                        }
                    }
                }
            }
        }
        return res
    }

    private fun createOriginalScriptFile(name: String, fileName: String): String {
        val fileContent = StringBuilder()
        Utils.log("$LOG_GENERATING$ORIGINAL_SCRIPT_FILE...")
        fileContent.append(ECHO_OFF).append(Constants.NEWLINE)
        fileContent.append(getTfileName(fileName)).append(Constants.NEWLINE)
        fileContent.append(getScriptName(name)).append(Constants.NEWLINE)
        fileContent.append(PROGRAM_CALL)
            .append("-a $SCRIPTNAME_VAR.tbl $TFILENAMENAME_VAR $SCRIPTNAME_VAR.ext $SCRIPTNAME_VAR.off")
            .append(
                Constants.NEWLINE
            )
        fileContent.append(PAUSE).append(Constants.NEWLINE)
        return fileContent.toString()
    }

    private fun createInsertFile(name: String, fileName: String, fileType: String, transFileName: String): String {
        val fileContent = StringBuilder()
        Utils.log("$LOG_GENERATING$INSERT_FILE...")
        fileContent.append(ECHO_OFF).append(Constants.NEWLINE)
        fileContent.append(getTfileName(transFileName)).append(Constants.NEWLINE)
        fileContent.append(getSfileName(fileName)).append(Constants.NEWLINE)
        fileContent.append(getScriptName(name)).append(Constants.NEWLINE)
        fileContent.append("del $TFILENAMENAME_VAR").append(Constants.NEWLINE)
        fileContent.append("copy $SFILENAMENAME_VAR $TFILENAMENAME_VAR").append(Constants.NEWLINE)
        fileContent.append(PROGRAM_CALL).append("-ih $SCRIPTNAME_VAR.hex $TFILENAMENAME_VAR")
            .append(Constants.NEWLINE)
        fileContent.append(PROGRAM_CALL)
            .append("-h $SCRIPTNAME_VAR.tbl tr_$SCRIPTNAME_VAR.ext $TFILENAMENAME_VAR").append(
                Constants.NEWLINE
            )
        val checksumMode = getChecksumMode(fileName, fileType)
        if (checksumMode.isNotEmpty()) {
            fileContent.append(PROGRAM_CALL).append(checksumMode).append(" ").append(TFILENAMENAME_VAR)
                .append(Constants.NEWLINE)
        }
        fileContent.append(PAUSE).append(Constants.NEWLINE)
        return fileContent.toString()
    }

    private fun getChecksumMode(fileName: String, fileType: String): String {
        var checksumMode = Constants.EMPTY
        if (Constants.FILE_TYPE_MEGA_DRIVE == fileType || fileName.endsWith(".32x")) {
            checksumMode = Hextractor.MODE_FIX_MEGA_DRIVE_CHECKSUM
        } else {
            if (Constants.FILE_TYPE_NGB == fileType) {
                checksumMode = Hextractor.MODE_FIX_GAMEBOY_CHECKSUM
            } else {
                if (Constants.FILE_TYPE_SNES == fileType) {
                    checksumMode = Hextractor.MODE_FIX_SNES_CHECKSUM
                } else {
                    if (Constants.FILE_TYPE_ZXTAP == fileType) {
                        checksumMode = Hextractor.MODE_FIX_ZXTAP_CHECKSUM
                    } else {
                        if (Constants.FILE_TYPE_TZX == fileType) {
                            checksumMode = Hextractor.MODE_FIX_ZXTZX_CHECKSUM
                        } else {
                            if (Constants.FILE_TYPE_MASTER_SYSTEM == fileType) {
                                checksumMode = Hextractor.MODE_FIX_SMS_CHECKSUM
                            }
                        }
                    }
                }
            }
        }
        return checksumMode
    }

    /**
     * Creates the patch creation file.
     *
     * @param name the name
     * @param fileName the file name
     * @param transFileName the trans file name
     * @return the string
     */
    private fun createCreatePatchFile(name: String, fileName: String, transFileName: String): String {
        val fileContent = StringBuilder()
        Utils.log("$LOG_GENERATING$CREATE_PATCH_FILE...")
        fileContent.append(ECHO_OFF).append(Constants.NEWLINE)
        fileContent.append(getTfileName(transFileName)).append(Constants.NEWLINE)
        fileContent.append(getSfileName(fileName)).append(Constants.NEWLINE)
        fileContent.append(getScriptName(name)).append(Constants.NEWLINE)
        fileContent.append(PROGRAM_CALL)
            .append("${Hextractor.CREATE_IPS_PATCH} $SFILENAMENAME_VAR $TFILENAMENAME_VAR $SCRIPTNAME_VAR.ips")
            .append(Constants.NEWLINE)
        fileContent.append(PROGRAM_CALL)
            .append(
                "${Hextractor.MODE_FILL_READ_ME} ${SCRIPTNAME_VAR}_readme.txt  ${SCRIPTNAME_VAR}_readme.txt $SFILENAMENAME_VAR"
            )
            .append(Constants.NEWLINE)
        fileContent.append(PAUSE).append(Constants.NEWLINE)
        return fileContent.toString()
    }

    private fun createHexFile(name: String): String {
        val fileContent = StringBuilder()
        Utils.log("$LOG_GENERATING$name$HEX_EXTENSION...")
        fileContent.append(";Traducciones Wave ").append(YEAR).append(Constants.NEWLINE)
        fileContent.append(";54 72 61 64 75 63 63 69 6F 6E 65 73 20 57 61 76 65 20 3")
        fileContent.append(YEAR, 0, 1).append(" 3").append(YEAR, 1, 2).append(" 3").append(YEAR, 2, 3).append(" 3")
            .append(YEAR, 3, 4).append("@000000E0:000000F5").append(Constants.NEWLINE)
        return fileContent.toString()
    }

    private fun createExtractHexFile(name: String, translatedFileName: String): String {
        val fileContent = StringBuilder()
        Utils.log("$LOG_GENERATING$EXTRACT_HEX_FILE...")
        fileContent.append(ECHO_OFF).append(Constants.NEWLINE)
        fileContent.append(getTfileName(translatedFileName)).append(Constants.NEWLINE)
        fileContent.append(getScriptName(name)).append(Constants.NEWLINE)
        fileContent.append(PROGRAM_CALL).append("-eh $TFILENAMENAME_VAR $SCRIPTNAME_VAR.ext.hex").append(
            Constants.NEWLINE
        )
        fileContent.append(PAUSE).append(Constants.NEWLINE)
        return fileContent.toString()
    }

    @Throws(IOException::class)
    private fun copyBaseFiles(projectFolder: File, name: String, projectFile: File?) {
        Utils.copyFileUsingStream(FILE_HEXTRACTOR, Utils.getJoinedFileName(projectFolder, FILE_HEXTRACTOR))
        Utils.copyFileUsingStream(FILE_README, Utils.getJoinedFileName(projectFolder, name + FILE_README))
        if (projectFile != null) {
            Utils.copyFileUsingStream(
                projectFile.absolutePath,
                Utils.getJoinedFileName(projectFolder, projectFile.name)
            )
            Utils.copyFileUsingStream(
                projectFile.absolutePath,
                Utils.getJoinedFileName(projectFolder, TR_FILENAME_PREFIX + projectFile.name)
            )
        }
    }

    @Throws(IOException::class)
    private fun createProjectFolder(name: String): File {
        val projectFolder = File(name)
        if (!projectFolder.exists() && !projectFolder.mkdir()) {
            throw IOException("Error generating: $name directory.")
        }
        return projectFolder
    }

    fun getProjectName(fileName: String): String {
        var projectName = fileName
        for (ext in Constants.EXTENSIONS_MEGADRIVE) {
            if ("32x" != ext) {
                projectName = projectName.replace(".$ext", "smd")
            }
        }
        for (ext in Constants.EXTENSIONS_SNES) {
            projectName = projectName.replace(".$ext", "sfc")
        }
        projectName = projectName.replace(" ", "")
        projectName = projectName.replace("(\\(.*\\))".toRegex(), "")
        projectName = projectName.replace("(\\[.*])".toRegex(), "")
        projectName = projectName.replace("[^A-Za-z0-9]".toRegex(), "")
        return projectName.lowercase(Locale.getDefault())
    }

    val projectName: String
        /**
         * Gets the project name (based on the current directory).
         *
         * @return the project name
         */
        get() = try {
            File(Constants.CURRENT_DIR).canonicalFile.name
        } catch (e: IOException) {
            Utils.logException(e)
            Constants.EMPTY
        }
}
