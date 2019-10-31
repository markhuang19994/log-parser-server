package com.example.app.service.impl;

import com.example.app.MainArgs;
import com.example.app.method.condition.LogCondition;
import com.example.app.method.content.LogContentChanger;
import com.example.app.method.custom.GlobalLogMethod;
import com.example.app.model.LogDetail;
import com.example.app.service.ArgumentService;
import com.example.app.service.MethodService;
import com.example.app.service.MethodInstructService;
import com.example.app.util.ReflectUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private final MethodService methodService;
    private final ArgumentService argumentService;


    public MethodInstructServiceImpl(MethodService methodService, MainArgs mainArgs, ArgumentService argumentService) {
        this.methodService = methodService;
        this.mainArgs = mainArgs;
        this.argumentService = argumentService;
    }

    @Override
    public List<LogDetail> execFilterInstruct(List<LogDetail> logDetails, String[] instructArgs) throws Exception {
        List<MethodInstruct> instructList = parseMethodExecInstruct(instructArgs);
        List<LogDetail> result = new ArrayList<>(logDetails);
        for (MethodInstruct instruct : instructList) {
            String methodStr = instruct.getMethodStr();
            LOGGER.debug("Exec method Instruct >>> {}", methodStr);
            LogCondition logCondition = methodService
                    .getLogConditionInstance(mainArgs.getConditionJavaSource(), methodStr);
            result = filterLogByCondition(result, logCondition);
        }
        return result;
    }

    private List<LogDetail> filterLogByCondition(List<LogDetail> logDetails, LogCondition logCondition) {
        List<LogDetail> result = new ArrayList<>();
        for (LogDetail logDetail : logDetails) {
            Map<String, String> attr = logDetail.getAttributeMap();
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

    @Override
    public List<LogDetail> execContentChangeInstruct(List<LogDetail> logDetails, String[] instructArgs) throws Exception {
        List<MethodInstruct> instructList = parseMethodExecInstruct(instructArgs);
        List<LogDetail> result = new ArrayList<>(logDetails);
        for (MethodInstruct instruct : instructList) {
            String methodStr = instruct.getMethodStr();
            LOGGER.debug("Exec method Instruct >>> {}", methodStr);
            LogContentChanger logContentChanger = methodService
                    .getContentChangeMethodInstance(mainArgs.getContentChangeJavaSource());
            result = changeLogContent(result, logContentChanger, instruct.getMethod());
        }
        return result;
    }

    private List<LogDetail> changeLogContent(
            List<LogDetail> logDetails, LogContentChanger logContentChanger, Method method) {
        List<LogDetail> result = new ArrayList<>();
        for (LogDetail logDetail : logDetails) {
            Map<String, String> attr = logDetail.getAttributeMap();
            try {
                logContentChanger.setAttrMap(attr);
                ReflectUtil.executeMethodWithStringArgs(logContentChanger, method.name, method.args);
                result.add(logDetail);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    @Override
    public List<LogDetail> execGlobalInstruct(List<LogDetail> logDetails, String[] instructArgs) throws Exception {
        List<MethodInstruct> instructList = parseMethodExecInstruct(instructArgs);
        List<LogDetail> result = new ArrayList<>(logDetails);
        for (MethodInstruct instruct : instructList) {
            String methodStr = instruct.getMethodStr();
            LOGGER.debug("Exec method Instruct >>> {}", methodStr);
            GlobalLogMethod globalLogMethod = methodService.getGlobalMethodInstance(methodStr);
            try {
                globalLogMethod.setLogDetailList(logDetails);
                Method method = instruct.getMethod();
                result = globalLogMethod.exec();
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

        private Method getMethod() {
            return new Method(parts.get(0), parts.subList(1, parts.size()));
        }
    }

    private static class Method {
        private String name;
        private List<String> args;

        private Method(String name, List<String> args) {
            this.name = name;
            this.args = args;
        }
    }
}
