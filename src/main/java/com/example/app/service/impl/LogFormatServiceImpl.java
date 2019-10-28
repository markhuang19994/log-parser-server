package com.example.app.service.impl;

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
    @Override
    public List<LogDetail> prettyFormat(List<LogDetail> logDetailList) {
        Stream<LogDetail> stream = logDetailList.stream();
        if (logDetailList.size() > 8000) {
            stream = stream.parallel();
        }
        Map<String, Integer> longVal = getMaxAttrLengthMap(logDetailList);
        return stream.peek(logDetail -> {
            Map<String, String> attr = logDetail.getAttr();
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
            Map<String, String> attr = logDetail.getAttr();
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
