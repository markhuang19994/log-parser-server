package com.example.app.util;

import java.util.Arrays;
import java.util.List;

/**
 * @author MarkHuang
 * @version <ul>
 * <li>10/20/19, MarkHuang,new
 * </ul>
 * @since 10/20/19
 */
public final class RegexUtil {
    private static final List<Character> KEY_WORDS = Arrays.asList(
            '[', '\\', '.', '|', '*', '+', '$', '^', '&', '(', ')'
    );

    private RegexUtil() {
        throw new AssertionError();
    }

    public static String escapeRegexSpecialChar(String str) {
        StringBuilder sb = new StringBuilder();
        char[] chars = str.toCharArray();
        for (char aChar : chars) {
            if (KEY_WORDS.contains(aChar)) {
                sb.append("\\");
            }
            sb.append(aChar);
        }
        return sb.toString();
    }
}
