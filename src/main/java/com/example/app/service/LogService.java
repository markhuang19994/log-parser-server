package com.example.app.service;

import java.io.IOException;

/**
 * @author MarkHuang
 * @version <ul>
 *  <li>10/26/19, MarkHuang,new
 * </ul>
 * @since 10/26/19
 */
public interface LogService {
    void init() throws Exception;

    void generateLogByConditionMethod(String[] args) throws Exception;

    String setFormat(String[] args) throws IOException;

    void recoverHistory(int index) throws Exception;
}
