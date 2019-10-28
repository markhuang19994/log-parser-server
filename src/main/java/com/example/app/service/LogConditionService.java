package com.example.app.service;

import com.example.app.condition.LogCondition;

/**
 * @author MarkHuang
 * @version <ul>
 *  <li>10/26/19, MarkHuang,new
 * </ul>
 * @since 10/26/19
 */
public interface LogConditionService {
    LogCondition generateLogConditionClass(String conditionJavaSource) throws Exception;

    LogCondition generateLogConditionClassAndMethod(String conditionJavaSource, String method) throws Exception;
}
