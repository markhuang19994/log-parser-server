package com.example.app.method.custom;

import com.example.app.model.LogDetail;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author MarkHuang
 * @version <ul>
 * <li>10/29/19, MarkHuang,new
 * </ul>
 * @since 10/29/19
 */
public abstract class AbstractGlobalLogMethod implements GlobalLogMethod {
    protected List<LogDetail> logDetailList;

    public void setLogDetailList(List<LogDetail> logDetailList) {
        this.logDetailList = logDetailList;
    }

    public List<LogDetail> gb(String... attrNames) {
        List<LogDetail> result = new ArrayList<>(logDetailList);
        Map<String, List<String>> orderMap = new LinkedHashMap<>();
        long l = 0;
        for (LogDetail logDetail : logDetailList) {
            Map<String, String> attributeMap = logDetail.getAttributeMap();
            for (String attrName : attrNames) {
                String val = attributeMap.get(attrName);
                List<String> orderList = orderMap.get(attrName);
                if (orderList == null) orderList = new ArrayList<>();
                if (val == null || "".equals(val)) {
                    val = String.format("@empty%d@", l++);
                    attributeMap.put(attrName, val);
                }
                if (!orderList.contains(val)) {
                    orderList.add(val);
                    orderMap.put(attrName, orderList);
                }
            }
        }

        result.sort((ld1, ld2) -> {
            Map<String, String> m1 = ld1.getAttributeMap();
            Map<String, String> m2 = ld2.getAttributeMap();

            for (Map.Entry<String, List<String>> entry : orderMap.entrySet()) {
                String attrName = entry.getKey();
                List<String> valList = orderMap.get(attrName);
                String v1 = m1.get(attrName);
                String v2 = m2.get(attrName);

                int i1 = valList.indexOf(v1);
                int i2 = valList.indexOf(v2);
                if (i1 != i2) {
                    return i1 > i2 ? 1 : -1;
                }
            }

            return 0;
        });

        return result.stream().peek(ld -> {
//            if (ld == null) return;
//            Map<String, String> m = ld.getAttributeMap();
//            for (Map.Entry<String, String> entry : m.entrySet()) {
//                String val = entry.getValue();
//                if (val.matches("@empty.*?@")) {
//                    val = "";
//                }
//                entry.setValue(val);
//            }
        }).collect(Collectors.toList());
    }

    private String safeStr(String str) {
        return str == null ? "" : str;
    }
}
