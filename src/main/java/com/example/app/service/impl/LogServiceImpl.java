package com.example.app.service.impl;

import com.example.app.MainArgs;
import com.example.app.model.LogDetail;
import com.example.app.service.*;
import com.example.app.util.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author MarkHuang
 * @version <ul>
 * <li>10/25/19, MarkHuang,new
 * </ul>
 * @since 10/25/19
 */
@Service
public class LogServiceImpl implements LogService {

    private static final Logger LOGGER = LoggerFactory.getLogger(LogServiceImpl.class);
    private final MainArgs mainArgs;
    private final LogFormatService logFormatService;
    private final ArgumentService argumentService;
    private final LogHistoryService logHistoryService;
    private final MethodInstructService methodInstructService;
    private List<LogDetail> currentLogDetails;


    public LogServiceImpl(
            MainArgs mainArgs, LogFormatService logFormatService,
            LogHistoryService logHistoryService, MethodInstructService methodInstructService,
            ArgumentService argumentService) {
        this.mainArgs = mainArgs;
        this.logFormatService = logFormatService;
        this.logHistoryService = logHistoryService;
        this.methodInstructService = methodInstructService;
        this.argumentService = argumentService;
    }

    @Override
    public void init() throws Exception {
        String log = FileUtil.readFileAsString(mainArgs.getLogFile());
        ParseLogService parseLogService = ParseLogServiceImpl.newInstance(log, mainArgs.getLogStructure());
        this.currentLogDetails = parseLogService.parseLog();
        logHistoryService.writeHistory(this.currentLogDetails);
    }

    @Override
    public void generateLogByConditionMethod(String[] args) throws Exception {
        List<LogDetail> logDetails = methodInstructService.execInstruct(this.currentLogDetails, args);
        String logStr = logFormatService.generateFormatLogStr(logDetails);
        writeLog(logStr);
        currentLogDetails = logDetails;
        logHistoryService.writeHistory(currentLogDetails);
    }

    @Override
    public String setFormat(String[] args) throws IOException {
        List<String> messages = new ArrayList<>();
        List<String[]> pipeArgsList = argumentService.splitArgs(args, "|");
        for (String[] pipeArgs : pipeArgsList) {
            String arg0 = String.valueOf(pipeArgs[0]);
            if (arg0.matches("^p|pretty$")) {
                boolean pretty = Boolean.parseBoolean(pipeArgs[1]);
                mainArgs.setPretty(pretty);
                messages.add(String.format("Set pretty:%s", pretty));
            }
            if (arg0.matches("^s|structure$")) {
                String structure = String.valueOf(pipeArgs[1]);
                mainArgs.setResultLogStructure(structure);
                messages.add(String.format("Set result structure:%s", structure));
            }
            if (arg0.matches("^r|reset$")) {
                String resultLogStructure = mainArgs.resetResultLogStructure();
                messages.add(String.format("Reset result structure:%s", resultLogStructure));
            }
        }
        String logStr = logFormatService.generateFormatLogStr(currentLogDetails);
        writeLog(logStr);
        messages.add("Result log is update.");
        for (String message : messages) {
            LOGGER.debug(message);
        }
        return String.join("\n", messages);
    }

    @Override
    public void recoverHistory(int index) throws Exception {
        currentLogDetails = logHistoryService.readHistory(index);
        String logStr = logFormatService.generateFormatLogStr(currentLogDetails);
        writeLog(logStr);
    }

    private void writeLog(String logStr) throws IOException {
        FileUtil.writeFile(mainArgs.getOutFile(), logStr);
    }

}
