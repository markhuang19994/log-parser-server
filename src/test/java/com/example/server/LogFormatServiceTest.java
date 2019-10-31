package com.example.server;

import com.example.app.MainArgs;
import com.example.app.model.LogDetail;
import com.example.app.service.ParseLogService;
import com.example.app.service.impl.LogFormatServiceImpl;
import com.example.app.service.impl.ParseLogServiceImpl;
import com.example.app.util.FileUtil;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.Assert;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

/**
 * @author MarkHuang
 * @version <ul>
 * <li>10/31/19, MarkHuang,new
 * </ul>
 * @since 10/31/19
 */
public class LogFormatServiceTest {
    private static String log;
    private static List<String> logBlocks;
    private static ParseLogService service;
    private static MainArgs mainArgs;

    static {
        try {
            log = FileUtil.readFileAsString(new ClassPathResource("/log/format_test.log").getFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
        mainArgs = new MainArgs();
        String s = "%time [%thread] [%sessionId] | %x | %requestPath | %className [%logStatus] %content";
        mainArgs.setLogStructure(s);
        mainArgs.setResultLogStructure(s);
        service = ParseLogServiceImpl.newInstance(log, mainArgs.getLogStructure());
        logBlocks = service.readLog(null);
    }

    private List<LogDetail> getLogDetailList() {
        return service.parseLog(logBlocks);
    }

    @Test
    void generateLogBlock() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        LogFormatServiceImpl service = new LogFormatServiceImpl(mainArgs);
        Method generateLogBlock = service.getClass().getDeclaredMethod("generateLogBlock", LogDetail.class);
        generateLogBlock.setAccessible(true);
        Object invoke = generateLogBlock.invoke(service, getLogDetailList().get(0));
        Assert.isTrue(invoke.equals(log));
    }

//    @Test
//    void groupBy() {
//        LogFormatServiceImpl service = new LogFormatServiceImpl(mainArgs);
//        List<LogDetail> logDetails = service.groupBy(getLogDetailList(), new String[]{"sessionId", "thread"});
//        String s = service.generateFormatLogStr(logDetails);
//        System.out.println(s);
//    }
}
