package com.example.app.condition;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public interface LogCondition {
    boolean exec() throws Exception;
    void setAttrMap(Map<String, String> attrMap);
}
