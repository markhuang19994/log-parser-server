package com.example.server;

import com.example.app.MainArgs;
import com.example.app.model.LogDetail;
import com.example.app.service.ParseLogService;
import com.example.app.service.impl.LogFormatServiceImpl;
import com.example.app.service.impl.ParseLogServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.util.Assert;

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

    private static String log = "2019-10-30 13:53:06,288 [WebContainer : 4] [] | ------ |  | PCLCustomerCheckHandler      [DEBUG] receiveTokenResult:{\"receiveData>>\":\"2019-10-30 13:53:06>>\\r\\n\"}";
    private static List<String> logBlocks;
    private static ParseLogService service;
    private static MainArgs mainArgs;

    static {
        mainArgs = new MainArgs();
        mainArgs.setResultLogStructure("%time [%thread] [%sessionId] | %x | %requestPath | %className [%logStatus] %content");
        service = ParseLogServiceImpl.newInstance(log, mainArgs.getLogStructure());
        logBlocks = service.readLog(null);
    }

    private List<LogDetail> getLogDetailList() {
        return service.parseLog(logBlocks);
    }

    @Test
    void generateLogBlock() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        LogFormatServiceImpl service = new LogFormatServiceImpl(mainArgs);
        Method read = service.getClass().getDeclaredMethod("generateLogBlock", LogDetail.class);
        read.setAccessible(true);
        Object invoke = read.invoke(service, getLogDetailList().get(0));
        Assert.isTrue(invoke.equals(log));
    }
}
