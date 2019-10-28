package com.example.app.condition;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LogConditionDemo extends AbstractLogCondition {
    public boolean exec() throws Exception {
//        return attrMap.get("aId").equals("d0dfd");
        return findByDate(attrMap.get("time"), "20191018183806", "20191018183809");
    }

    public boolean findByDate(String timeStr, String startStr, String endStr) throws ParseException {
        String pattern = "yyyyMMddHHmmss";
        Date start = new SimpleDateFormat(pattern).parse(timeStr);
        Date end = new SimpleDateFormat(pattern).parse(endStr);
        return betweenDate(timeStr, start, end);
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

}
