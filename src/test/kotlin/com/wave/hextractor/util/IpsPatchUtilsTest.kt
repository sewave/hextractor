package com.wave.hextractor.util

import com.wave.hextractor.util.FileUtils.writeFileAscii
import com.wave.hextractor.util.IpsPatchUtils.createIpsPatch
import com.wave.hextractor.util.IpsPatchUtils.validateIpsPatch
import org.junit.Assert
import org.junit.Test
import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.security.NoSuchAlgorithmException
import java.security.SecureRandom
import java.util.*

class IpsPatchUtilsTest {
    @Test
    @Throws(IOException::class, NoSuchAlgorithmException::class)
    fun createIpsPatch() {
        val file1 = File.createTempFile("test", "createIpsPatch.rom")
        val file2 = File.createTempFile("test", "createIpsPatch2.rom")
        val file3 = File.createTempFile("test", "createIpsPatch3.ips")
        file1.deleteOnExit()
        file2.deleteOnExit()
        file3.deleteOnExit()
        val bytes1 = ByteArray(1024)
        SecureRandom.getInstanceStrong().nextBytes(bytes1)
        Files.write(file1.toPath(), bytes1)
        val bytes2 = ByteArray(1024)
        SecureRandom.getInstanceStrong().nextBytes(bytes2)
        Files.write(file2.toPath(), bytes2)
        Assert.assertTrue(createIpsPatch(file1.absolutePath, file2.absolutePath, file3.absolutePath))
    }

    @Test
    @Throws(IOException::class, NoSuchAlgorithmException::class)
    fun applyIpsPatch() {
        val file1 = File.createTempFile("test", "applyIpsPatch.rom")
        file1.deleteOnExit()
        val bytes1 = ByteArray(1024)
        SecureRandom.getInstanceStrong().nextBytes(bytes1)
        Arrays.fill(bytes1, 10, 40, 0.toByte())
        Files.write(file1.toPath(), bytes1)
        val file2 = File.createTempFile("test", "applyIpsPatch2.rom")
        val bytes2 = ByteArray(1024)
        SecureRandom.getInstanceStrong().nextBytes(bytes2)
        Arrays.fill(bytes2, 10, 20, 0.toByte())
        Arrays.fill(bytes2, 20, 30, 1.toByte())
        Arrays.fill(bytes2, 30, 40, 0.toByte())
        Files.write(file2.toPath(), bytes2)
        val file3 = File.createTempFile("test", "applyIpsPatch3.ips")
        file3.deleteOnExit()
        createIpsPatch(file1.absolutePath, file2.absolutePath, file3.absolutePath)
        val file4 = File.createTempFile("test", "applyIpsPatch4.rom")
        file4.deleteOnExit()
        IpsPatchUtils.applyIpsPatch(file1.absolutePath, file4.absolutePath, file3.absolutePath)
        Assert.assertArrayEquals(Files.readAllBytes(file2.toPath()), Files.readAllBytes(file4.toPath()))
    }

    @Test
    @Throws(IOException::class)
    fun validateIpsPatch() {
        val file1 = File.createTempFile("test", "createIpsPatch.tst")
        file1.deleteOnExit()
        writeFileAscii(file1.absolutePath, SOURCE_FILE)
        val file2 = File.createTempFile("test", "createIpsPatch2.tst")
        file2.deleteOnExit()
        writeFileAscii(file2.absolutePath, DEST_FILE)
        val file3 = File.createTempFile("test", "createIpsPatch3.tst")
        file3.deleteOnExit()
        Assert.assertTrue(createIpsPatch(file1.absolutePath, file2.absolutePath, file3.absolutePath))
        Assert.assertTrue(validateIpsPatch(file1.absolutePath, file2.absolutePath, file3.absolutePath))
    }

    @Test
    fun testValidateIpsPatch() {
    }

    companion object {
        private const val SOURCE_FILE = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaab"
        private const val DEST_FILE = "aaaaaaaaaaaabbaaaaaaaaaaaaaccccccddd"
    }
}