package com.example.app.service;

import com.example.app.MainArgs;

import java.io.IOException;

/**
 * @author MarkHuang
 * @version <ul>
 *  <li>10/27/19, MarkHuang,new
 * </ul>
 * @since 10/27/19
 */
public interface MainService {
    String init(String mainConfigPath) throws Exception;

    void runMain(MainArgs mainArgs) throws Exception;
}
