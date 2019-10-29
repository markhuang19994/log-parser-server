import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class LogConditionDemo extends LogCondition {
    public boolean exec(Map<String, String> attrMap) throws Exception {
        return ${auto};
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
