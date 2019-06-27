package com.wave.hextractor.util;

import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Objects;

public class GBChecksumUtilsTest {

    @Test
    public void GBChecksumUtils() {
        boolean ok;
        try {
            new GBChecksumUtils();
            ok = true;
        } catch(Exception e) {
            ok = false;
        }
        Assert.assertTrue(ok);
    }

    @Test
    public void calculateGameBoyHeaderChecksum() throws URISyntaxException, IOException {
        Path correct = Paths.get(Objects.requireNonNull(getClass().getClassLoader().getResource("files/correct_checksum.gb")).toURI());
        byte[] correctBytes = Files.readAllBytes(correct);
        Assert.assertEquals(0xC7, GBChecksumUtils.calculateGameBoyHeaderChecksum(correctBytes));
    }

    @Test
    public void calculateGameBoyRomChecksum() throws URISyntaxException, IOException {
        Path correct = Paths.get(Objects.requireNonNull(getClass().getClassLoader().getResource("files/correct_checksum.gb")).toURI());
        byte[] correctBytes = Files.readAllBytes(correct);
        Assert.assertEquals(32871, GBChecksumUtils.calculateGameBoyRomChecksum(correctBytes));
    }

    @Test
    public void checkUpdateGameBoyChecksum() throws IOException, URISyntaxException {
        Path correct = Paths.get(Objects.requireNonNull(getClass().getClassLoader().getResource("files/correct_checksum.gb")).toURI());
        byte[] correctBytes = Files.readAllBytes(correct);
        byte[] incorrectBytes = Arrays.copyOf(correctBytes, correctBytes.length);
        File file = File.createTempFile("test", "checkUpdateMegaDriveChecksum2");
        file.deleteOnExit();
        Files.write(file.toPath(), incorrectBytes);
        GBChecksumUtils.checkUpdateGameBoyChecksum(file.getAbsolutePath());
        Assert.assertArrayEquals(correctBytes, Files.readAllBytes(file.toPath()));
        GBChecksumUtils.updateGameBoyRomChecksum(0, incorrectBytes);
        GBChecksumUtils.updateGameBoyHeaderChecksum(0, incorrectBytes);
        Files.write(file.toPath(), incorrectBytes);
        GBChecksumUtils.checkUpdateGameBoyChecksum(file.getAbsolutePath());
       Assert.assertArrayEquals(correctBytes, Files.readAllBytes(file.toPath()));
    }

    @Test
    public void getGameBoyHeaderChecksum() throws IOException, URISyntaxException {
        Path correct = Paths.get(Objects.requireNonNull(getClass().getClassLoader().getResource("files/correct_checksum.gb")).toURI());
        byte[] correctBytes = Files.readAllBytes(correct);
        Assert.assertEquals(0xC7, GBChecksumUtils.getGameBoyHeaderChecksum(correctBytes));
    }

    @Test
    public void getGameBoyRomChecksum() throws IOException, URISyntaxException {
        Path correct = Paths.get(Objects.requireNonNull(getClass().getClassLoader().getResource("files/correct_checksum.gb")).toURI());
        byte[] correctBytes = Files.readAllBytes(correct);
        Assert.assertEquals(GBChecksumUtils.getGameBoyRomChecksum(correctBytes), GBChecksumUtils.calculateGameBoyRomChecksum(correctBytes));
    }

    @Test
    public void updateGameBoyHeaderChecksum() throws IOException, URISyntaxException {
        Path correct = Paths.get(Objects.requireNonNull(getClass().getClassLoader().getResource("files/correct_checksum.gb")).toURI());
        byte[] correctBytes = Files.readAllBytes(correct);
        byte[] correctBytesUpd = Files.readAllBytes(correct);
        GBChecksumUtils.updateGameBoyHeaderChecksum(0,
                correctBytes);
        GBChecksumUtils.updateGameBoyHeaderChecksum(GBChecksumUtils.calculateGameBoyHeaderChecksum(correctBytes),
                correctBytes);
        Assert.assertArrayEquals(correctBytes, correctBytesUpd);
    }

    @Test
    public void updateGameBoyRomChecksum() throws IOException, URISyntaxException {
        Path correct = Paths.get(Objects.requireNonNull(getClass().getClassLoader().getResource("files/correct_checksum.gb")).toURI());
        byte[] correctBytes = Files.readAllBytes(correct);
        byte[] correctBytesUpd = Arrays.copyOf(correctBytes, correctBytes.length);
        GBChecksumUtils.updateGameBoyRomChecksum(0,
                correctBytesUpd);
        GBChecksumUtils.updateGameBoyRomChecksum(GBChecksumUtils.calculateGameBoyRomChecksum(correctBytes),
                correctBytesUpd);
        Assert.assertArrayEquals(correctBytes, correctBytesUpd);
    }
}