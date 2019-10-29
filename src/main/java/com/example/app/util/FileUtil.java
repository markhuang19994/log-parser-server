package com.example.app.util;

import java.io.*;
import java.nio.file.Files;
import java.util.UUID;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * @author MarkHuang
 * @version <ul>
 * <li>10/20/19, MarkHuang,new
 * </ul>
 * @since 10/20/19
 */
public final class FileUtil {

    private FileUtil() {
        throw new AssertionError();
    }

    public static String readFileAsString(String filePath) throws IOException {
        return readFileAsString(new File(filePath));
    }

    public static String readFileAsString(File file) throws IOException {
        byte[] bytes = Files.readAllBytes(file.toPath());
        return new String(bytes);
    }

    public static String readGzipFileAsString(File file) throws IOException {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             GZIPInputStream gis = new GZIPInputStream(new FileInputStream(file))) {
            byte[] buffer = new byte[4096];
            int n;
            while ((n = gis.read(buffer)) >= 0) {
                baos.write(buffer, 0, n);
            }
            return new String(baos.toByteArray());
        }
    }

    public static void writeFile(String filePath, String text) throws IOException {
        writeFile(new File(filePath), text);
    }

    public static void writeFile(File file, String text) throws IOException {
        write(new FileOutputStream(file), text);
    }

    public static void writeGZipFile(File file, String text) throws IOException {
        write(new GZIPOutputStream(new FileOutputStream(file)), text);
    }

    private static void write(OutputStream os, String text) throws IOException {
        try {
            os.write(text.getBytes());
            os.flush();
        } finally {
            if (os != null) {
                os.close();
            }
        }
    }

    public static File newRandomTempDir(String suffix) {
        File tempDir = new File(System.getProperty(
                "java.io.tmpdir"), UUID.randomUUID().toString().replace("-", "") + "_" + suffix);
        tempDir.mkdirs();
        return tempDir;
    }

}
