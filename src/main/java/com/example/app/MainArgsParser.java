package com.example.app;

import com.example.app.analysis.ActionAnalysis;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author MarkHuang* @version
 * <ul>
 *  <li>10/14/19, MarkHuang,new
 * </ul>
 * @since 10/14/19
 */
public class MainArgsParser {
    public static MainArgs parseMainArgs(String[] args) throws IOException {
        ActionAnalysis actionAnalysis = ActionAnalysis.getInstance(args);
        List<String> errorMessageList = new ArrayList<>();

        String log = actionAnalysis
                .getActionByFlags(ActionAnalysis.flags("-log", "-l"))
                .orElseGet(() -> {
                    errorMessageList.add("-log not found");
                    return null;
                });

        String structure = actionAnalysis
                .getActionByFlags(ActionAnalysis.flags("-structure", "-s"))
                .orElseGet(() -> {
                    errorMessageList.add("-structure not found");
                    return null;
                });

        String resultStructure = actionAnalysis
                .getActionByFlags(ActionAnalysis.flags("-resultStructure", "-rs"))
                .orElse(null);

        String condition = actionAnalysis
                .getActionByFlags(ActionAnalysis.flags("-condition", "-c"))
                .orElseGet(() -> {
                    errorMessageList.add("-condition not found");
                    return null;
                });

        String out = actionAnalysis
                .getActionByFlags(ActionAnalysis.flags("-out", "-o"))
                .orElse(null);

        if (errorMessageList.size() > 0) {
            throw new RuntimeException("\n" + String.join("\n", errorMessageList));
        }

        return new MainArgs(log, structure, resultStructure, condition, out);
    }
}
