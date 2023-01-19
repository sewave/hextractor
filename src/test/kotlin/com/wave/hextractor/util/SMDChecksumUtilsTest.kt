package com.wave.hextractor.util

import com.wave.hextractor.util.SMDChecksumUtils.calculateMegaDriveChecksum
import com.wave.hextractor.util.SMDChecksumUtils.checkUpdateMegaDriveChecksum
import com.wave.hextractor.util.SMDChecksumUtils.getMegaDriveChecksum
import com.wave.hextractor.util.SMDChecksumUtils.updateMegaDriveChecksum
import com.wave.hextractor.util.Utils.copyFileUsingStream
import org.junit.Assert
import org.junit.Test
import java.io.File
import java.io.IOException
import java.net.URISyntaxException
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*

class SMDChecksumUtilsTest {
    @Test
    @Throws(IOException::class, URISyntaxException::class)
    fun checkUpdateMegaDriveChecksum() {
        val incorrect =
            Paths.get(Objects.requireNonNull(javaClass.classLoader.getResource("files/incorrect_checksum.md")).toURI())
        val correct =
            Paths.get(Objects.requireNonNull(javaClass.classLoader.getResource("files/correct_checksum.md")).toURI())
        val file = File.createTempFile("test", "checkUpdateMegaDriveChecksum")
        file.deleteOnExit()
        copyFileUsingStream(incorrect.toFile().absolutePath, file.absolutePath)
        checkUpdateMegaDriveChecksum(file.absolutePath)
        val correctBytes = Files.readAllBytes(correct)
        val updatedBytes = Files.readAllBytes(file.toPath())
        Assert.assertArrayEquals(correctBytes, updatedBytes)
        copyFileUsingStream(correct.toFile().absolutePath, file.absolutePath)
        checkUpdateMegaDriveChecksum(file.absolutePath)
        val updatedBytes2 = Files.readAllBytes(file.toPath())
        Assert.assertArrayEquals(correctBytes, updatedBytes2)
    }

    @Test
    @Throws(IOException::class, URISyntaxException::class)
    fun checkUpdateMegaDriveChecksum1() {
        val incorrect =
            Paths.get(Objects.requireNonNull(javaClass.classLoader.getResource("files/incorrect_checksum.md")).toURI())
        val correct =
            Paths.get(Objects.requireNonNull(javaClass.classLoader.getResource("files/correct_checksum.md")).toURI())
        val file = File.createTempFile("test", "checkUpdateMegaDriveChecksum2")
        file.deleteOnExit()
        copyFileUsingStream(incorrect.toFile().absolutePath, file.absolutePath)
        val correctBytes = Files.readAllBytes(correct)
        val updatedBytes = Files.readAllBytes(file.toPath())
        checkUpdateMegaDriveChecksum(updatedBytes)
        Assert.assertArrayEquals(correctBytes, updatedBytes)
        copyFileUsingStream(correct.toFile().absolutePath, file.absolutePath)
        val updatedBytes2 = Files.readAllBytes(file.toPath())
        checkUpdateMegaDriveChecksum(updatedBytes2)
        Assert.assertArrayEquals(correctBytes, updatedBytes2)
    }

    @Throws(URISyntaxException::class, IOException::class)
    @Test
    fun megaDriveChecksum() {
        val path = Paths.get(
            Objects.requireNonNull(javaClass.classLoader.getResource("files/correct_checksum.md")).toURI()
        )
        val fileBytes = Files.readAllBytes(path)
        Assert.assertEquals(8192, getMegaDriveChecksum(fileBytes).toLong())
        Assert.assertEquals(
            calculateMegaDriveChecksum(fileBytes).toLong(), getMegaDriveChecksum(fileBytes).toLong()
        )
    }

    @Test
    @Throws(IOException::class, URISyntaxException::class)
    fun calculateMegaDriveChecksum() {
        val correct =
            Paths.get(Objects.requireNonNull(javaClass.classLoader.getResource("files/correct_checksum.md")).toURI())
        val fileBytes = Files.readAllBytes(correct)
        Assert.assertEquals(8192, calculateMegaDriveChecksum(fileBytes).toLong())
    }

    @Test
    @Throws(IOException::class, URISyntaxException::class)
    fun updateMegaDriveChecksum() {
        val incorrect =
            Paths.get(Objects.requireNonNull(javaClass.classLoader.getResource("files/incorrect_checksum.md")).toURI())
        val fileBytes = Files.readAllBytes(incorrect)
        val originalChecksum = getMegaDriveChecksum(fileBytes)
        val calculatedChecksum = calculateMegaDriveChecksum(fileBytes)
        updateMegaDriveChecksum(calculatedChecksum, fileBytes)
        Assert.assertNotEquals(originalChecksum.toLong(), calculatedChecksum.toLong())
        Assert.assertEquals(calculatedChecksum.toLong(), getMegaDriveChecksum(fileBytes).toLong())
    }
}