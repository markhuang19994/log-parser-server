package com.example.server;

import com.example.app.service.ParseLogService;
import com.example.app.service.impl.ParseLogServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.Assert;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.util.List;

public class ParseLogServiceTest {

    static String log = "";
    private static final int ANS_LOG_BLOCK_SIZE = 353050;

    static {
        try {
            File f = new ClassPathResource("log/CLM_WebLog_RCE2.log.1").getFile();
            StringBuilder logB = new StringBuilder();
            for (int i = 0; i < 10; i++) {
                logB.append(new String(Files.readAllBytes(f.toPath())));
            }
            log = logB.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    void readLog1() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        ParseLogService service = ParseLogServiceImpl.newInstance(log, "%time");
        Method read = service.getClass().getDeclaredMethod("readLog", String.class);
        read.setAccessible(true);
        long l = System.currentTimeMillis();
        Object invoke = read.invoke(service, "(\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2},\\d{3})");
        long useTime = System.currentTimeMillis() - l;
        System.out.println("Use time:" + useTime + "ms");

        Assert.isTrue(useTime < 2000, "method perform is too slow");
        Assert.isInstanceOf(List.class, invoke);
        Assert.isTrue(((List) invoke).size() == ANS_LOG_BLOCK_SIZE, () -> "Log block size not correct");
    }

    @Test
    void readLog2() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        ParseLogService service = ParseLogServiceImpl.newInstance(log, "%time");
        Method read = service.getClass().getDeclaredMethod("readLog", String.class);
        read.setAccessible(true);
        long l = System.currentTimeMillis();
        Object invoke = read.invoke(service, new Object[]{ null });
        long useTime = System.currentTimeMillis() - l;
        System.out.println("Use time:" + useTime + "ms");

        Assert.isTrue(useTime < 2000, "method perform is too slow");
        Assert.isInstanceOf(List.class, invoke);
        Assert.isTrue(((List) invoke).size() == ANS_LOG_BLOCK_SIZE, () -> "Log block size not correct");
    }

}
