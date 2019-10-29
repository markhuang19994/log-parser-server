package com.example.app.service;

import com.example.app.model.LogDetail;

import java.util.List;

/**
 * @author MarkHuang
 * @version <ul>
 *  <li>10/26/19, MarkHuang,new
 * </ul>
 * @since 10/26/19
 */
public interface LogFormatService {
    String generateFormatLogStr(List<LogDetail> logDetails);

    List<LogDetail> prettyFormat(List<LogDetail> logDetailList);
}
