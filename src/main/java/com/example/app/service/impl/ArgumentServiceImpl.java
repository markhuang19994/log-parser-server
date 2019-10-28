package com.example.app.service.impl;

import com.example.app.service.ArgumentService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author MarkHuang
 * @version <ul>
 *  <li>10/28/19, MarkHuang,new
 * </ul>
 * @since 10/28/19
 */
@Service
public class ArgumentServiceImpl implements ArgumentService {
    @Override
    public String[] parseArgumentStr(String str) {
        char[] chars = str.toCharArray();
        List<String> args = new ArrayList<>();
        boolean escape = false;
        boolean inSq = false;
        boolean inDq = false;
        boolean onChVal = false;
        StringBuilder temp = new StringBuilder();
        for (char c : chars) {
            if (c == '\\') {
                escape = !escape;
            } else if (c == '\'') {
                if (escape) {
                    temp.append(c);
                    escape = false;
                    continue;
                }
                if (inSq) {
                    args.add(temp.toString());
                    temp = new StringBuilder();
                    inSq = false;
                    continue;
                }
                if (onChVal) {
                    onChVal = false;
                }
                if (inDq) {
                    temp.append(c);
                } else {
                    inSq = true;
                }
            } else if (c == '\"') {
                if (escape) {
                    temp.append(c);
                    escape = false;
                    continue;
                }
                if (inDq) {
                    args.add(temp.toString());
                    temp = new StringBuilder();
                    inDq = false;
                    continue;
                }
                if (onChVal) {
                    onChVal = false;
                }
                if (inSq) {
                    temp.append(c);
                } else {
                    inDq = true;
                }
            } else if (c == ' ' || c == '\t') {
                if (onChVal) {
                    continue;
                }
                if (!inSq && !inDq) {
                    args.add(temp.toString());
                    temp.setLength(0);
                    onChVal = true;
                    continue;
                }
                temp.append(c);
            } else {
                if (onChVal) {
                    onChVal = false;
                }
                temp.append(c);
            }
        }
        if (temp.length() > 0) {
            args.add(temp.toString());
        }
        return args.toArray(new String[]{});
    }
}
