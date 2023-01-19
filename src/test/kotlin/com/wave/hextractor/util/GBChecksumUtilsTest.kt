package com.wave.hextractor.util

import org.junit.Assert
import org.junit.Test
import java.io.File
import java.io.IOException
import java.net.URISyntaxException
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*

class GBChecksumUtilsTest {
    @Test
    @Throws(URISyntaxException::class, IOException::class)
    fun calculateGameBoyHeaderChecksum() {
        val correct =
            Paths.get(Objects.requireNonNull(javaClass.classLoader.getResource("files/correct_checksum.gb")).toURI())
        val correctBytes = Files.readAllBytes(correct)
        Assert.assertEquals(0xC7, GBChecksumUtils.calculateGameBoyHeaderChecksum(correctBytes).toLong())
    }

    @Test
    @Throws(URISyntaxException::class, IOException::class)
    fun calculateGameBoyRomChecksum() {
        val correct =
            Paths.get(Objects.requireNonNull(javaClass.classLoader.getResource("files/correct_checksum.gb")).toURI())
        val correctBytes = Files.readAllBytes(correct)
        Assert.assertEquals(32871, GBChecksumUtils.calculateGameBoyRomChecksum(correctBytes).toLong())
    }

    @Test
    @Throws(IOException::class, URISyntaxException::class)
    fun checkUpdateGameBoyChecksum() {
        val correct =
            Paths.get(Objects.requireNonNull(javaClass.classLoader.getResource("files/correct_checksum.gb")).toURI())
        val correctBytes = Files.readAllBytes(correct)
        val incorrectBytes = Arrays.copyOf(correctBytes, correctBytes.size)
        val file = File.createTempFile("test", "checkUpdateMegaDriveChecksum2")
        file.deleteOnExit()
        Files.write(file.toPath(), incorrectBytes)
        GBChecksumUtils.checkUpdateGameBoyChecksum(file.absolutePath)
        Assert.assertArrayEquals(correctBytes, Files.readAllBytes(file.toPath()))
        GBChecksumUtils.updateGameBoyRomChecksum(0, incorrectBytes)
        GBChecksumUtils.updateGameBoyHeaderChecksum(0, incorrectBytes)
        Files.write(file.toPath(), incorrectBytes)
        GBChecksumUtils.checkUpdateGameBoyChecksum(file.absolutePath)
        Assert.assertArrayEquals(correctBytes, Files.readAllBytes(file.toPath()))
    }

    @Throws(IOException::class, URISyntaxException::class)
    @Test
    fun gameBoyHeaderChecksum() {
        val correct = Paths.get(
            Objects.requireNonNull(javaClass.classLoader.getResource("files/correct_checksum.gb")).toURI()
        )
        val correctBytes = Files.readAllBytes(correct)
        Assert.assertEquals(0xC7, GBChecksumUtils.getGameBoyHeaderChecksum(correctBytes).toLong())
    }

    @Throws(IOException::class, URISyntaxException::class)
    @Test
    fun gameBoyRomChecksum() {
        val correct = Paths.get(
            Objects.requireNonNull(javaClass.classLoader.getResource("files/correct_checksum.gb")).toURI()
        )
        val correctBytes = Files.readAllBytes(correct)
        Assert.assertEquals(
            GBChecksumUtils.getGameBoyRomChecksum(correctBytes).toLong(),
            GBChecksumUtils.calculateGameBoyRomChecksum(correctBytes).toLong()
        )
    }

    @Test
    @Throws(IOException::class, URISyntaxException::class)
    fun updateGameBoyHeaderChecksum() {
        val correct =
            Paths.get(Objects.requireNonNull(javaClass.classLoader.getResource("files/correct_checksum.gb")).toURI())
        val correctBytes = Files.readAllBytes(correct)
        val correctBytesUpd = Files.readAllBytes(correct)
        GBChecksumUtils.updateGameBoyHeaderChecksum(
            0, correctBytes
        )
        GBChecksumUtils.updateGameBoyHeaderChecksum(
            GBChecksumUtils.calculateGameBoyHeaderChecksum(correctBytes), correctBytes
        )
        Assert.assertArrayEquals(correctBytes, correctBytesUpd)
    }

    @Test
    @Throws(IOException::class, URISyntaxException::class)
    fun updateGameBoyRomChecksum() {
        val correct =
            Paths.get(Objects.requireNonNull(javaClass.classLoader.getResource("files/correct_checksum.gb")).toURI())
        val correctBytes = Files.readAllBytes(correct)
        val correctBytesUpd = Arrays.copyOf(correctBytes, correctBytes.size)
        GBChecksumUtils.updateGameBoyRomChecksum(
            0, correctBytesUpd
        )
        GBChecksumUtils.updateGameBoyRomChecksum(
            GBChecksumUtils.calculateGameBoyRomChecksum(correctBytes), correctBytesUpd
        )
        Assert.assertArrayEquals(correctBytes, correctBytesUpd)
    }
}
