package com.example.app.service.impl;

import com.example.app.MainArgs;
import com.example.app.condition.LogCondition;
import com.example.app.model.LogDetail;
import com.example.app.service.ArgumentService;
import com.example.app.service.LogConditionService;
import com.example.app.service.MethodInstructService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author MarkHuang
 * @version <ul>
 * <li>10/28/19, MarkHuang,new
 * </ul>
 * @since 10/28/19
 */
@Service
public class MethodInstructServiceImpl implements MethodInstructService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodInstructServiceImpl.class);

    private final MainArgs mainArgs;
    private final LogConditionService conditionService;
    @Autowired
    private ArgumentService argumentService;


    public MethodInstructServiceImpl(LogConditionService conditionService, MainArgs mainArgs) {
        this.conditionService = conditionService;
        this.mainArgs = mainArgs;
    }

    @Override
    public List<LogDetail> execInstruct(List<LogDetail> logDetails, String[] instructArgs) throws Exception {
        List<MethodInstruct> instructList = parseMethodExecInstruct(instructArgs);
        List<LogDetail> result = new ArrayList<>(logDetails);
        for (MethodInstruct instruct : instructList) {
            String methodStr = instruct.getMethodStr();
            LOGGER.debug("Exec method Instruct >>> {}", methodStr);
            LogCondition logCondition = conditionService
                    .generateLogConditionClassAndMethod(mainArgs.getConditionJavaSource(), methodStr);
            result = filterByLogCondition(result, logCondition);
        }
        return result;
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

    private List<MethodInstruct> parseMethodExecInstruct(String[] args) {
        return argumentService.splitArgs(args, "|")
                .stream()
                .map(_args -> new MethodInstruct(Arrays.asList(_args)))
                .collect(Collectors.toList());
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
