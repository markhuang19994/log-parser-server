import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class LogCondition {
    public boolean exec() throws Exception {
        return ${auto};
    }

    public boolean bySid(String sessionId) {
        return sessionId.equals(attrMap.get("sessionId"));
    }

    public boolean findByDate(String timeStr, String startStr, String endStr) throws ParseException {
        String pattern = "yyyyMMddHHmmss";
        Date start = new SimpleDateFormat(pattern).parse(startStr);
        Date end = new SimpleDateFormat(pattern).parse(endStr);
        return betweenDate(timeStr, start, end);
    }
}
