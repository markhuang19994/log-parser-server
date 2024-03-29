package com.example.app;

import com.example.app.util.FileUtil;
import org.springframework.core.io.ClassPathResource;

import javax.validation.constraints.NotNull;
import java.io.File;
import java.io.IOException;

/**
 * @author MarkHuang* @version
 * <ul>
 *  <li>10/14/19, MarkHuang,new
 * </ul>
 * @since 10/14/19
 */
public class MainArgs {
    private File logFile;
    private String logStructure;
    private String resultLogStructure;
    private String oriResultLogStructure;
    private String conditionJavaSource;
    private String contentChangeJavaSource;
    private File outFile;
    private boolean isPretty = false;

    public MainArgs() {
    }

    public MainArgs(@NotNull String log, String logStructure,
                    String resultLogStructure, String conditionJavaPath,
                    String contentChangeJavaPath, String out) throws IOException {
        File logFile = new File(log);
        if (!logFile.exists()) {
            throw new RuntimeException("Log file not found at:" + log);
        }
        this.logFile = logFile;
        this.logStructure = logStructure;
        this.resultLogStructure = resultLogStructure == null ? logStructure : resultLogStructure;
        this.oriResultLogStructure = this.resultLogStructure;
        File conditionJavaFile = new File(conditionJavaPath);

        if (!conditionJavaFile.exists()) {
            this.conditionJavaSource = FileUtil.readFileAsString(new ClassPathResource("method/condition/LogConditionDemo.java").getFile());
        } else {
            this.conditionJavaSource = FileUtil.readFileAsString(conditionJavaFile);
        }

        File contentChangeJavaFile = new File(contentChangeJavaPath);
        if (!contentChangeJavaFile.exists()) {
            this.contentChangeJavaSource = FileUtil.readFileAsString(new ClassPathResource("method/content/LogContentChangerDemo.java").getFile());
        } else {
            this.contentChangeJavaSource = FileUtil.readFileAsString(contentChangeJavaFile);
        }

        if (out == null) {
            this.outFile = new File(this.logFile.getParentFile(), "out.log");
        } else {
            File outFile = new File(out);
            if (!outFile.getParentFile().exists()) {
                outFile.getParentFile().mkdirs();
            }
            this.outFile = outFile;
        }
    }

    public File getLogFile() {
        return logFile;
    }

    public void setLogFile(File logFile) {
        this.logFile = logFile;
    }

    public String getLogStructure() {
        return logStructure;
    }

    public void setLogStructure(String logStructure) {
        this.logStructure = logStructure;
    }

    public String getResultLogStructure() {
        return resultLogStructure;
    }

    public void setResultLogStructure(String resultLogStructure) {
        this.resultLogStructure = resultLogStructure;
    }

    public String getConditionJavaSource() {
        return conditionJavaSource;
    }

    public void setConditionJavaSource(String conditionJavaSource) {
        this.conditionJavaSource = conditionJavaSource;
    }

    public File getOutFile() {
        return outFile;
    }

    public void setOutFile(File outFile) {
        this.outFile = outFile;
    }

    public void setContentChangeJavaSource(String contentChangeJavaSource) {
        this.contentChangeJavaSource = contentChangeJavaSource;
    }

    public String getContentChangeJavaSource() {
        return contentChangeJavaSource;
    }

    public void setPretty(boolean pretty) {
        isPretty = pretty;
    }

    public boolean isPretty() {
        return isPretty;
    }

    public void setOriResultLogStructure(String oriResultLogStructure) {
        this.oriResultLogStructure = oriResultLogStructure;
    }

    public String getOriResultLogStructure() {
        return oriResultLogStructure;
    }

    public String resetResultLogStructure() {
        this.resultLogStructure = oriResultLogStructure;
        return this.resultLogStructure;
    }

}
