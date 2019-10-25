package com.wave.hextractor.util;

import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;

public class IpsPatchUtilsTest {

    private static final String SOURCE_FILE = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaab";
    private static final String DEST_FILE = "aaaaaaaaaaaabbaaaaaaaaaaaaaccccccddd";

    @Test
    public void createIpsPatch() throws IOException, NoSuchAlgorithmException {
        File file1 = File.createTempFile("test", "createIpsPatch.rom");
        File file2 = File.createTempFile("test", "createIpsPatch2.rom");
        File file3 = File.createTempFile("test", "createIpsPatch3.ips");
        file1.deleteOnExit();
        file2.deleteOnExit();
        file3.deleteOnExit();
        byte[] bytes1 = new byte[1024];
        SecureRandom.getInstanceStrong().nextBytes(bytes1);
        Files.write(file1.toPath(), bytes1);
        byte[] bytes2 = new byte[1024];
        SecureRandom.getInstanceStrong().nextBytes(bytes2);
        Files.write(file2.toPath(), bytes2);
        Assert.assertTrue(IpsPatchUtils.createIpsPatch(file1.getAbsolutePath(), file2.getAbsolutePath(), file3.getAbsolutePath()));
    }

    @Test
    public void applyIpsPatch() throws IOException, NoSuchAlgorithmException {
        File file1 = File.createTempFile("test", "applyIpsPatch.rom");
        file1.deleteOnExit();
        byte[] bytes1 = new byte[1024];
        SecureRandom.getInstanceStrong().nextBytes(bytes1);
        Arrays.fill(bytes1,10, 40, (byte) 0);

        Files.write(file1.toPath(), bytes1);
        File file2 = File.createTempFile("test", "applyIpsPatch2.rom");
        byte[] bytes2 = new byte[1024];
        SecureRandom.getInstanceStrong().nextBytes(bytes2);
        Arrays.fill(bytes2,10, 20, (byte) 0);
        Arrays.fill(bytes2,20, 30, (byte) 1);
        Arrays.fill(bytes2,30, 40, (byte) 0);
        Files.write(file2.toPath(), bytes2);
        File file3 = File.createTempFile("test", "applyIpsPatch3.ips");
        file3.deleteOnExit();
        IpsPatchUtils.createIpsPatch(file1.getAbsolutePath(), file2.getAbsolutePath(), file3.getAbsolutePath());
        File file4 = File.createTempFile("test", "applyIpsPatch4.rom");
        file4.deleteOnExit();
        IpsPatchUtils.applyIpsPatch(file1.getAbsolutePath(), file4.getAbsolutePath(), file3.getAbsolutePath());
        Assert.assertArrayEquals(Files.readAllBytes(file2.toPath()), Files.readAllBytes(file4.toPath()));
    }

    @Test
    public void validateIpsPatch() throws IOException {
        File file1 = File.createTempFile("test", "createIpsPatch.tst");
        file1.deleteOnExit();
        FileUtils.writeFileAscii(file1.getAbsolutePath(), SOURCE_FILE);
        File file2 = File.createTempFile("test", "createIpsPatch2.tst");
        file2.deleteOnExit();
        FileUtils.writeFileAscii(file2.getAbsolutePath(), DEST_FILE);
        File file3 = File.createTempFile("test", "createIpsPatch3.tst");
        file3.deleteOnExit();
        Assert.assertTrue(IpsPatchUtils.createIpsPatch(file1.getAbsolutePath(), file2.getAbsolutePath(), file3.getAbsolutePath()));
        Assert.assertTrue(IpsPatchUtils.validateIpsPatch(file1.getAbsolutePath(), file2.getAbsolutePath(), file3.getAbsolutePath()));
    }

    @Test
    public void testValidateIpsPatch() {
    }
}