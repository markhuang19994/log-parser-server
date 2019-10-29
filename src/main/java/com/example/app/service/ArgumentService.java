package com.example.app.service;

import java.util.List;

/**
 * @author MarkHuang
 * @version <ul>
 *  <li>10/28/19, MarkHuang,new
 * </ul>
 * @since 10/28/19
 */
public interface ArgumentService {
    String[] parseArgumentStr(String str);

    List<String[]> splitArgs(String[] args, String s);
}
