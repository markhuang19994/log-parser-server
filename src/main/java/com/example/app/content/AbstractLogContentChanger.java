package com.example.app.content;

import java.util.Map;

/**
 * @author MarkHuang
 * @version <ul>
 *  <li>10/29/19, MarkHuang,new
 * </ul>
 * @since 10/29/19
 */
public abstract class AbstractLogContentChanger implements LogContentChanger {
    protected Map<String, String> attrMap;

    public void setAttrMap(Map<String, String> attrMap) {
        this.attrMap = attrMap;
    }

    public void rp(String name, String str, String replacement){
        String rVal = safeStr(attrMap.get(name)).replace(str, replacement);
        attrMap.put(name, rVal);
    }

    public void rpx(String name, String regex, String replacement){
        String rVal = safeStr(attrMap.get(name)).replaceAll(regex, replacement);
        attrMap.put(name, rVal);
    }

    private String safeStr(String str) {
        return str == null ? "" : str;
    }
}
