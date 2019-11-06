package com.example.app.service;

import com.example.app.model.LogDetail;

import java.io.IOException;
import java.util.List;

/**
 * @author MarkHuang
 * @version <ul>
 *  <li>10/26/19, MarkHuang,new
 * </ul>
 * @since 10/26/19
 */
public interface LogService {
    void init() throws Exception;

    void generateLogByFilterMethod(String[] args) throws Exception;

    void generateLogByChangeContentMethod(String[] args) throws Exception;

    void generateLogByGlobalMethod(String[] args) throws Exception;

    String setFormat(String[] args) throws IOException;

    void recoverHistory(int index) throws Exception;

    List<LogDetail> getCurrentLogDetails();

    List<LogDetail> getCurrentLogDetailsWithFormat();
}
