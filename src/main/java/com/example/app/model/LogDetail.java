package com.example.app.model;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author MarkHuang
 * @version <ul>
 * <li>10/18/19, MarkHuang,new
 * </ul>
 * @since 10/18/19
 */
public class LogDetail implements Cloneable{
    private Map<String, String> attr = new LinkedHashMap<>();

    public LogDetail() {

    }

    public LogDetail(Map<String, String> attr) {
        this.attr = new LinkedHashMap<>(attr);
    }

    public Map<String, String> getAttr() {
        return attr;
    }

    public void putAttr(String key, String val) {
        this.attr.put(key, val);
    }

    public LogDetail clone() {
        try {
            super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return new LogDetail(this.attr);
    }
}
