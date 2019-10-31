package com.example.app.service;

import com.example.app.method.condition.LogCondition;
import com.example.app.method.content.LogContentChanger;
import com.example.app.method.custom.GlobalLogMethod;

/**
 * @author MarkHuang
 * @version <ul>
 *  <li>10/26/19, MarkHuang,new
 * </ul>
 * @since 10/26/19
 */
public interface MethodService {
    LogCondition getLogConditionInstance(String conditionJavaSource) throws Exception;

    LogCondition getLogConditionInstance(String conditionJavaSource, String method) throws Exception;

    LogContentChanger getContentChangeMethodInstance(String javaSource) throws Exception;

    GlobalLogMethod getGlobalMethodInstance(String method) throws Exception;
}
