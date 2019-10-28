package com.example.app.analysis;

import java.util.*;

/**
 * @author MarkHuang
 * @version <ul>
 * <li>2018/10/29, MarkHuang,new
 * </ul>
 * @since 2018/10/29
 */
public class ActionAnalysis {
    private Map<String, List<String>> actionMap = new HashMap<>();

    private ActionAnalysis(String[] args) {
        List<String> tempParams = new ArrayList<>();
        for (String arg : args) {
            if (arg.charAt(0) == '-') {
                tempParams = new ArrayList<>();
                actionMap.put(arg, tempParams);
            } else {
                tempParams.add(arg);
            }
        }
    }

    public static List<String> flags(String ...flags){
        return Arrays.asList(flags);
    }

    public static ActionAnalysis getInstance(String[] args) {
        return new ActionAnalysis(args);
    }

    public Map<String, List<String>> getActionMap() {
        return actionMap;
    }

    public Optional<String> getActionByFlag(String action) {
        if (!this.actionMap.containsKey(action)) return Optional.empty();
        List<String> params = this.actionMap.get(action);
        if (params == null || params.isEmpty()) return Optional.empty();
        return Optional.of(params.get(0));
    }

    public Optional<String> getActionByFlags(List<String> actions) {
        for (String action : actions) {
            Optional<String> actionByFlagOpt = getActionByFlag(action);
            String actionByFlag = actionByFlagOpt.orElse(null);
            if (actionByFlag != null) {
                return actionByFlagOpt;
            }
        }
        return Optional.empty();
    }
}
