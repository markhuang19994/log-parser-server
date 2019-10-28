package com.example.app.service;

import com.example.app.model.LogDetail;

import java.io.IOException;
import java.util.List;

/**
 * @author MarkHuang
 * @version <ul>
 *  <li>10/27/19, MarkHuang,new
 * </ul>
 * @since 10/27/19
 */
public interface LogHistoryService {
    void writeHistory(List<LogDetail> logDetails) throws IOException;

    List<LogDetail> readHistory(int index) throws IOException;
}
