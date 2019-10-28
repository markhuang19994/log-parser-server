package com.example.app.service;

import com.example.app.model.LogDetail;

import java.util.List;

/**
 * @author MarkHuang
 * @version <ul>
 *  <li>10/28/19, MarkHuang,new
 * </ul>
 * @since 10/28/19
 */
public interface MethodInstructService {
    List<LogDetail> execInstruct(List<LogDetail> logDetails, String[] instructArgs) throws Exception;
}