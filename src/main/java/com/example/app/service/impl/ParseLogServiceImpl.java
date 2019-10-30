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
    private String log;
    private String logStructure;
    private List<String> attrNames;
    private static final char[] NUMS_STR = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
    private static final int[] PREFIX_NUM_INDICES = new int[]{0, 1, 2, 3, 5, 6, 8, 9, 11, 12, 14, 15, 17, 18, 20, 21, 22};

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

    private List<String> readLog(String logLinePrefix) {
        String[] lines = log.split("\n|\r\n");
        List<String> result = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        boolean hasMatchLine = false;
        for (String line : lines) {
            if (prefixMatcher(line, logLinePrefix)) {
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

    /**
     * If user specific log line prefix pattern, then use customPrefixMatcher,
     * otherwise use defaultPrefixMatcher, cus defaultPrefixMatcher is 65% faster
     *
     * @return log blocks List
     */
    private boolean prefixMatcher(String logLine, String prefix){
        return prefix == null
                ? defaultPrefixMatcher(logLine)
                : customPrefixMatcher(logLine, prefix);
    }

    private boolean defaultPrefixMatcher(String logLine) {
        //ex: 2019-09-18 23:17:11,151
        if (logLine.length() < 23) return false;

        char c = logLine.charAt(10);
        if (c != ' ') return false;

        char c1 = logLine.charAt(19);
        if (c1 != ',') return false;

        char c2 = logLine.charAt(4);
        char c3 = logLine.charAt(7);
        if (!(c2 == c3 && c2 == '-')) return false;

        char c4 = logLine.charAt(13);
        char c5 = logLine.charAt(16);
        if (!(c4 == c5 && c4 == ':')) return false;

        for (int numIndex : PREFIX_NUM_INDICES) {
            char cn = logLine.charAt(numIndex);
            boolean isNum = false;
            for (char num : NUMS_STR) {
                if (num == cn) {
                    isNum = true;
                    break;
                }
            }
            if (!isNum) return false;
        }
        return true;
    }

    private boolean customPrefixMatcher(String logLine, String prefixPattern) {
        return logLine.matches(prefixPattern + "[\\s\\S]*");
    }

    @Override
    public List<LogDetail> parseLog() {
        List<String> logList = readLog(null);
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
