package com.example.app.service.impl;

import com.example.app.model.LogDetail;
import com.example.app.service.ParseLogService;
import com.example.app.util.RegexUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author MarkHuang
 * @version <ul>
 * <li>10/18/19, MarkHuang,new
 * </ul>
 * @since 10/18/19
 */
public class ParseLogServiceImpl implements ParseLogService {
    private String prefixPattern = "(\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2},\\d{3})";
    private String log;
    private String logStructure;
    private List<String> attrNames;

    public static ParseLogService newInstance(String log, String logStructure) {
        return new ParseLogServiceImpl(log, logStructure);
    }

    private ParseLogServiceImpl(String log, String logStructure) {
        this.log = log;
        this.logStructure = logStructure;
        this.attrNames = getAttrNamesByLogStructure(logStructure);
    }

    @Override
    public List<String> getAttrNames() {
        return attrNames;
    }

    @Override
    public String getLogStructure() {
        return logStructure;
    }

    private List<String> getAttrNamesByLogStructure(String logStructure) {
        List<String> attrNames = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        boolean attrStart = false;
        for (char c : logStructure.toCharArray()) {
            if (c == '%') {
                attrStart = true;
            } else {
                String s = String.valueOf(c);
                boolean match = s.matches("[a-zA-Z0-9]");
                if (!match && attrStart) {
                    attrNames.add(sb.toString());
                    sb.setLength(0);
                    attrStart = false;
                }
                if (attrStart) {
                    sb.append(c);
                }
            }
        }

        if (sb.length() > 0) {
            attrNames.add(sb.toString());
        }
        return attrNames;
    }

    private List<String> readLog() {
        String[] lines = log.split("\n|\r\n");
        List<String> result = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        boolean hasMatchLine = false;
        for (String line : lines) {
            if (line.matches(prefixPattern + "[\\s\\S]*")) {
                hasMatchLine = true;
                if (sb.length() > 0) {
                    result.add(sb.toString());
                    sb.setLength(0);
                }
                sb.append(line).append(System.lineSeparator());
            } else {
                if (hasMatchLine) {
                    sb.append(line).append(System.lineSeparator());
                }
            }
        }
        if (sb.length() > 0) {
            result.add(sb.toString());
        }
        return result;
    }

    @Override
    public List<LogDetail> parseLog() {
        List<String> logList = readLog();
        int size = logList.size();
        Stream<String> stream = size > 80000 ? logList.parallelStream() : logList.stream();
        String patternStr = getAttrPattern();
        return stream
                .map(log -> {
                    Pattern pattern = Pattern.compile(patternStr);
                    Matcher matcher = pattern.matcher(log);
                    boolean isFind = matcher.find();
                    if (isFind) {
                        try {
                            LogDetail logDetail = new LogDetail();
                            for (int i = 1; i <= attrNames.size(); i++) {
                                logDetail.putAttr(attrNames.get(i - 1), matcher.group(i));
                            }
                            return logDetail;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private String getAttrPattern() {
        String pattern = RegexUtil.escapeRegexSpecialChar(logStructure);
        pattern = pattern.replaceAll(" ", "[ \t]+");
        for (String attrName : attrNames) {
            String key = "%" + attrName;
            if (attrName.equalsIgnoreCase("time")) {
                pattern = pattern.replace(key, "(\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2},\\d{3})");
            } else if (attrName.equalsIgnoreCase("content")) {
                pattern = pattern.replace(key, "([\\s\\S]*)");
            } else {
                pattern = pattern.replace(key, "([\\s\\S]*?)");
            }
        }

        return String.format("^%s$", pattern);
    }
}
