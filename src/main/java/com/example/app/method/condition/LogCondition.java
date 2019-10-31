package com.example.app.method.condition;

import java.util.Map;

public interface LogCondition {
    boolean exec() throws Exception;
    void setAttrMap(Map<String, String> attrMap);
}
