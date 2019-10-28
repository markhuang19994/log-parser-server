package com.example.app.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.UUID;

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

    public static void writeFile(String filePath, String text) throws IOException {
        writeFile(new File(filePath), text);
    }

    public static void writeFile(File file, String text) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(text.getBytes());
            fos.flush();
        }
    }

    public static File newRandomTempDir() {
        File tempDir = new File(System.getProperty(
                "java.io.tmpdir"), UUID.randomUUID().toString().replace("-", ""));
        tempDir.mkdirs();
        return tempDir;
    }

}
