package com.example.app.service.impl;

import com.example.app.MainArgs;
import com.example.app.model.LogDetail;
import com.example.app.service.*;
import com.example.app.util.EncryptUtil;
import com.example.app.util.FileUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
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
    private final Environment env;
    private List<LogDetail> currentLogDetails;


    public LogServiceImpl(
            MainArgs mainArgs, LogFormatService logFormatService,
            LogHistoryService logHistoryService, MethodInstructService methodInstructService,
            ArgumentService argumentService, Environment env) {
        this.mainArgs = mainArgs;
        this.logFormatService = logFormatService;
        this.logHistoryService = logHistoryService;
        this.methodInstructService = methodInstructService;
        this.argumentService = argumentService;
        this.env = env;
    }

    @Override
    public void init() throws Exception {
        String log = FileUtil.readFileAsString(mainArgs.getLogFile());
        this.currentLogDetails = getInitLogInCacheDir(log);
        if (this.currentLogDetails == null) {
            ParseLogService parseLogService = ParseLogServiceImpl.newInstance(log, mainArgs.getLogStructure());
            this.currentLogDetails = parseLogService.parseLog();
        } else {
            LOGGER.debug("Init log detail from cache");
        }

        logHistoryService.writeHistory(this.currentLogDetails);
        cacheInitLog(log, this.currentLogDetails);
    }

    private void cacheInitLog(String log, List<LogDetail> logDetails) {
        try {
            String key = getInitLogCacheKey(log);
            String cacheDir = env.getProperty("init.cache.dir");
            FileUtil.writeGZipFile(new File(cacheDir, key), new ObjectMapper().writeValueAsString(logDetails));
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    private List<LogDetail> getInitLogInCacheDir(String log) {
        try {
            String key = getInitLogCacheKey(log);
            String cacheDir = env.getProperty("init.cache.dir");
            File f = new File(cacheDir, key);
            if (!f.exists()) return null;
            String logDetailJson = FileUtil.readGzipFileAsString(f);
            return new ObjectMapper().readValue(logDetailJson, new TypeReference<List<LogDetail>>() {
            });
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return null;
    }

    private String getInitLogCacheKey(String log) throws NoSuchAlgorithmException {
        return EncryptUtil.encryptMd5(String.format("%s%s", log, mainArgs.getLogStructure())) + ".init.gz";
    }

    @Override
    public void generateLogByFilterMethod(String[] args) throws Exception {
        List<LogDetail> logDetails = methodInstructService.execFilterInstruct(this.currentLogDetails, args);
        String logStr = logFormatService.generateFormatLogStr(logDetails);
        writeLog(logStr);
        currentLogDetails = logDetails;
        logHistoryService.writeHistory(currentLogDetails);
    }

    @Override
    public void generateLogByChangeContentMethod(String[] args) throws Exception {
        List<LogDetail> logDetails = methodInstructService.execContentChangeInstruct(this.currentLogDetails, args);
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
                String structure = pipeArgs[1];
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
