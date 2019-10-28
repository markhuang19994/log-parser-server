package com.example.app.service.impl;

import com.example.app.MainArgs;
import com.example.app.condition.LogCondition;
import com.example.app.model.LogDetail;
import com.example.app.service.*;
import com.example.app.util.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
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
    private final LogConditionService conditionService;
    private final LogHistoryService logHistoryService;
    private List<LogDetail> currentLogDetails;

    public LogServiceImpl(
            MainArgs mainArgs, LogFormatService formatService,
            LogConditionService conditionService, LogHistoryService logHistoryService) {
        this.mainArgs = mainArgs;
        this.formatService = formatService;
        this.conditionService = conditionService;
        this.logHistoryService = logHistoryService;
    }

    @Override
    public void init() throws Exception {
        String log = FileUtil.readFileAsString(mainArgs.getLogFile());
        ParseLogService parseLogService = new ParseLogService(log, mainArgs.getLogStructure());
        this.currentLogDetails = parseLogService.parseLog();
        logHistoryService.writeHistory(this.currentLogDetails);
    }

    @Override
    public void generateLogByConditionMethod(String[] args) throws Exception {
        List<LogDetail> logDetails = new ArrayList<>(currentLogDetails);
        List<MethodInstruct> instructList = parseMethodExecInstruct(args);
        for (MethodInstruct instruct : instructList) {
            String methodStr = instruct.getMethodStr();
            LOGGER.debug("Exec method Instruct >>> {}", methodStr);
            LogCondition logCondition = conditionService
                    .generateLogConditionClassAndMethod(mainArgs.getConditionJavaSource(), methodStr);
            logDetails = filterByLogCondition(logDetails, logCondition);

            writeLogDetail(logDetails);
            currentLogDetails = logDetails;
            logHistoryService.writeHistory(currentLogDetails);
        }
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

    private List<LogDetail> filterByLogCondition(List<LogDetail> logDetails, LogCondition logCondition) {
        List<LogDetail> result = new ArrayList<>();
        for (LogDetail logDetail : logDetails) {
            Map<String, String> attr = logDetail.getAttr();
            try {
                logCondition.setAttrMap(attr);
                if (logCondition.exec()) {
                    result.add(logDetail);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
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

    private List<MethodInstruct> parseMethodExecInstruct(String[] args) {
        List<MethodInstruct> instructList = new ArrayList<>();
        List<String> instructParts = new ArrayList<>();
        for (String arg : args) {
            if (arg.equals("|")) {
                instructList.add(new MethodInstruct(instructParts));
                instructParts = new ArrayList<>();
            }
            instructParts.add(arg);
        }
        return instructList;
    }

    private static class MethodInstruct {
        private List<String> parts;

        private MethodInstruct(List<String> parts) {
            this.parts = parts;
        }

        private String getMethodStr() {

            StringBuilder method = new StringBuilder(parts.get(0) + "(");
            for (int i = 1; i < parts.size(); i++) {
                method.append(String.format("\"%s\"", parts.get(i)));
                if (i < parts.size() - 1) {
                    method.append(",");
                }
            }
            method.append(")");
            return method.toString();
        }
    }
}
