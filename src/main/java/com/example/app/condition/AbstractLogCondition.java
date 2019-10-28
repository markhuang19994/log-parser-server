package com.example.app.condition;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public abstract class AbstractLogCondition implements LogCondition {
    protected Map<String, String> attrMap;

    public void setAttrMap(Map<String, String> attrMap) {
        this.attrMap = attrMap;
    }

    public boolean eq(String name, String val) {
        return safeStr(attrMap.get(name)).equals(val);
    }

    public boolean ct(String name, String val) {
        return safeStr(attrMap.get(name)).contains(val);
    }

    public boolean reg(String name, String reg) {
        return safeStr(attrMap.get(name)).matches(reg);
    }

    public boolean betweenDate(String timeStr, Date start, Date end) {
        return betweenDate(timeStr, "yyyy-MM-dd HH:mm:ss,S", start, end);
    }

    public boolean betweenDate(String timeStr, String pattern, Date start, Date end) {
        try {
            Date time = new SimpleDateFormat(pattern).parse(timeStr);
            return time.getTime() >= start.getTime() && time.getTime() <= end.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    private String safeStr(String str) {
        return str == null ? "" : str;
    }
}
