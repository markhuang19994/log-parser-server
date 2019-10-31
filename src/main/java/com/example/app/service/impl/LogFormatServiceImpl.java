package com.example.app.service.impl;

import com.example.app.MainArgs;
import com.example.app.model.LogDetail;
import com.example.app.service.LogFormatService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author MarkHuang
 * @version <ul>
 * <li>10/26/19, MarkHuang,new
 * </ul>
 * @since 10/26/19
 */
@Service
public class LogFormatServiceImpl implements LogFormatService {

    private final MainArgs mainArgs;

    public LogFormatServiceImpl(MainArgs mainArgs) {
        this.mainArgs = mainArgs;
    }

    @Override
    public String generateFormatLogStr(List<LogDetail> logDetails) {
        List<LogDetail> logDetailList = logDetails
                .parallelStream()
                .map(LogDetail::clone)
                .collect(Collectors.toList());
        if (mainArgs.isPretty()) {
            logDetailList = prettyFormat(logDetailList);
        }
        StringBuilder sb = new StringBuilder();
        for (LogDetail logDetail : logDetailList) {
            sb.append(generateLogBlock(logDetail));
        }
        return sb.toString();
    }

    private String generateLogBlock(LogDetail logDetail) {
        String result = mainArgs.getResultLogStructure();
        Map<String, String> attr = logDetail.getAttributeMap();
        for (Map.Entry<String, String> entry : attr.entrySet()) {
            result = result.replace("%" + entry.getKey(), entry.getValue());
        }
        return result;
    }

    @Override
    public List<LogDetail> prettyFormat(List<LogDetail> logDetailList) {
        Stream<LogDetail> stream = logDetailList.stream();
        if (logDetailList.size() > 8000) {
            stream = stream.parallel();
        }
        Map<String, Integer> longVal = getMaxAttrLengthMap(logDetailList);
        return stream.peek(logDetail -> {
            Map<String, String> attr = logDetail.getAttributeMap();
            for (Map.Entry<String, String> entry : attr.entrySet()) {
                String key = entry.getKey();
                if (key.equals("content")) continue;
                String val = entry.getValue();
                Integer i = longVal.get(key);
                attr.put(key, String.format("%-" + i + "s", val));
            }
        }).collect(Collectors.toList());
    }

    private Map<String, Integer> getMaxAttrLengthMap(List<LogDetail> logDetailList) {
        Map<String, Integer> maxLengthMap = new ConcurrentHashMap<>();
        Stream<LogDetail> stream = logDetailList.stream();
        if (logDetailList.size() > 8000) {
            stream = stream.parallel();
        }
        stream.forEach(logDetail -> {
            Map<String, String> attr = logDetail.getAttributeMap();
            for (Map.Entry<String, String> entry : attr.entrySet()) {
                String key = entry.getKey();
                if (key.equals("content")) continue;
                int valLength = entry.getValue().length();
                Integer i = maxLengthMap.get(key);
                if (i == null || i < valLength) {
                    maxLengthMap.put(key, valLength);
                }
            }
        });
        return maxLengthMap;
    }


}
