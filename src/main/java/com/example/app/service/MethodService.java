package com.example.app.service;

import com.example.app.condition.LogCondition;
import com.example.app.content.LogContentChanger;

/**
 * @author MarkHuang
 * @version <ul>
 *  <li>10/26/19, MarkHuang,new
 * </ul>
 * @since 10/26/19
 */
public interface MethodService {
    LogCondition generateLogConditionClass(String conditionJavaSource) throws Exception;

    LogCondition generateLogConditionClassAndMethod(String conditionJavaSource, String method) throws Exception;

    LogContentChanger generateContentChangeMethodClass(String javaSource) throws Exception;
}
