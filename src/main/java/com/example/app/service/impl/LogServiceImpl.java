package com.example.app.service.impl;

import com.example.app.MainArgs;
import com.example.app.model.LogDetail;
import com.example.app.service.*;
import com.example.app.util.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

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
    private final LogFormatService formatService;
    private final LogHistoryService logHistoryService;
    private final MethodInstructService methodInstructService;
    private List<LogDetail> currentLogDetails;

    public LogServiceImpl(
            MainArgs mainArgs, LogFormatService formatService,
            LogHistoryService logHistoryService, MethodInstructService methodInstructService) {
        this.mainArgs = mainArgs;
        this.formatService = formatService;
        this.logHistoryService = logHistoryService;
        this.methodInstructService = methodInstructService;
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
        writeLogDetail(logDetails);
        currentLogDetails = logDetails;
        logHistoryService.writeHistory(currentLogDetails);
    }

    @Override
    public void recoverHistory(int index) throws Exception {
        currentLogDetails = logHistoryService.readHistory(index);
        writeLogDetail(currentLogDetails);
    }

    private void writeLogDetail(List<LogDetail> currentLogDetails) throws IOException {
        List<LogDetail> prettyDetails = formatService.prettyFormat(currentLogDetails);
        String logStr = logDetailsToLogStr(prettyDetails, mainArgs.getResultLogStructure());
        FileUtil.writeFile(mainArgs.getOutFile(), logStr);
    }

    private String logDetailsToLogStr(List<LogDetail> logDetails, String resultLogStructure) {
        StringBuilder sb = new StringBuilder();
        for (LogDetail logDetail : logDetails) {
            sb.append(generateLogBlock(logDetail, resultLogStructure));
        }
        return sb.toString();
    }

    private String generateLogBlock(LogDetail logDetail, String logStructure) {
        Map<String, String> attr = logDetail.getAttr();
        for (Map.Entry<String, String> entry : attr.entrySet()) {
            logStructure = logStructure.replace("%" + entry.getKey(), entry.getValue());
        }
        return logStructure;
    }

}
