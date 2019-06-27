package com.wave.hextractor.util;

import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

import static org.junit.Assert.*;

public class SMDChecksumUtilsTest {

    @Test
    public void checkUpdateMegaDriveChecksum() throws IOException, URISyntaxException {
        Path incorrect = Paths.get(Objects.requireNonNull(getClass().getClassLoader().getResource("files/incorrect_checksum.md")).toURI());
        Path correct = Paths.get(Objects.requireNonNull(getClass().getClassLoader().getResource("files/correct_checksum.md")).toURI());
        File file = File.createTempFile("test", "checkUpdateMegaDriveChecksum");
        file.deleteOnExit();
        Utils.copyFileUsingStream(incorrect.toFile().getAbsolutePath(), file.getAbsolutePath());
        SMDChecksumUtils.checkUpdateMegaDriveChecksum(file.getAbsolutePath());
        byte[] correctBytes = Files.readAllBytes(correct);
        byte[] updatedBytes = Files.readAllBytes(file.toPath());
        assertArrayEquals(correctBytes, updatedBytes);
        Utils.copyFileUsingStream(correct.toFile().getAbsolutePath(), file.getAbsolutePath());
        SMDChecksumUtils.checkUpdateMegaDriveChecksum(file.getAbsolutePath());
        byte[] updatedBytes2 = Files.readAllBytes(file.toPath());
        assertArrayEquals(correctBytes, updatedBytes2);
    }

    @Test
    public void checkUpdateMegaDriveChecksum1() throws IOException, URISyntaxException {
        Path incorrect = Paths.get(Objects.requireNonNull(getClass().getClassLoader().getResource("files/incorrect_checksum.md")).toURI());
        Path correct = Paths.get(Objects.requireNonNull(getClass().getClassLoader().getResource("files/correct_checksum.md")).toURI());
        File file = File.createTempFile("test", "checkUpdateMegaDriveChecksum2");
        file.deleteOnExit();
        Utils.copyFileUsingStream(incorrect.toFile().getAbsolutePath(), file.getAbsolutePath());
        byte[] correctBytes = Files.readAllBytes(correct);
        byte[] updatedBytes = Files.readAllBytes(file.toPath());
        SMDChecksumUtils.checkUpdateMegaDriveChecksum(updatedBytes);
        assertArrayEquals(correctBytes, updatedBytes);
        Utils.copyFileUsingStream(correct.toFile().getAbsolutePath(), file.getAbsolutePath());
        byte[] updatedBytes2 = Files.readAllBytes(file.toPath());
        SMDChecksumUtils.checkUpdateMegaDriveChecksum(updatedBytes2);
        assertArrayEquals(correctBytes, updatedBytes2);
    }

    @Test
    public void getMegaDriveChecksum() throws URISyntaxException, IOException {
        Path path = Paths.get(Objects.requireNonNull(getClass().getClassLoader().getResource("files/correct_checksum.md")).toURI());
        byte[] fileBytes = Files.readAllBytes(path);
        assertEquals(8192, SMDChecksumUtils.getMegaDriveChecksum(fileBytes));
        assertEquals(SMDChecksumUtils.calculateMegaDriveChecksum(fileBytes), SMDChecksumUtils.getMegaDriveChecksum(fileBytes));
    }

    @Test
    public void calculateMegaDriveChecksum() throws IOException, URISyntaxException {
        Path correct = Paths.get(Objects.requireNonNull(getClass().getClassLoader().getResource("files/correct_checksum.md")).toURI());
        byte[] fileBytes = Files.readAllBytes(correct);
       assertEquals(8192, SMDChecksumUtils.calculateMegaDriveChecksum(fileBytes));
    }

    @Test
    public void updateMegadriveChecksum() throws IOException, URISyntaxException {
        Path incorrect = Paths.get(Objects.requireNonNull(getClass().getClassLoader().getResource("files/incorrect_checksum.md")).toURI());
        byte[] fileBytes = Files.readAllBytes(incorrect);
        int originalChecksum = SMDChecksumUtils.getMegaDriveChecksum(fileBytes);
        int calculatedChecksum = SMDChecksumUtils.calculateMegaDriveChecksum(fileBytes);
        SMDChecksumUtils.updateMegadriveChecksum(calculatedChecksum, fileBytes);
        assertNotEquals(originalChecksum, calculatedChecksum);
        assertEquals(calculatedChecksum, SMDChecksumUtils.getMegaDriveChecksum(fileBytes));
    }
}