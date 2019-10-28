package com.example.app.service.impl;

import com.example.app.MainArgs;
import com.example.app.MainArgsParser;
import com.example.app.service.MainService;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * @author MarkHuang
 * @version <ul>
 * <li>10/27/19, MarkHuang,new
 * </ul>
 * @since 10/27/19
 */
@Service
public class MainServiceImpl implements MainService {

    @Autowired
    private ApplicationContext ctx;

    @Autowired
    private LogServiceImpl logService;

    private ObjectMapper om = new ObjectMapper();

    @Override
    public String init(String mainConfigPath) throws Exception {

        if (mainConfigPath == null) {
            throw new IllegalArgumentException("Main configure path can't be null.");
        }

        File mainConfig = new File(mainConfigPath);
        if (!mainConfig.exists()) {
            throw new IllegalArgumentException("Main configure not found, path:" + mainConfigPath);
        }

        Properties prop = new Properties();
        prop.load(new FileInputStream(mainConfig));

        String logPath = prop.getProperty("log.path");
        String outPath = prop.getProperty("log.out.path");
        String logStructure = prop.getProperty("log.structure");
        String logCondition = prop.getProperty("log.condition");

        MainArgs mainArgs = MainArgsParser.parseMainArgs(new String[]{
                "-l", logPath,
                "-o", outPath,
                "-s", logStructure,
                "-c", logCondition
        });

        MainArgs mainArgsBean = ctx.getBean(MainArgs.class);
        mainArgsBean.setLogFile(mainArgs.getLogFile());
        mainArgsBean.setOutFile(mainArgs.getOutFile());
        mainArgsBean.setLogStructure(mainArgs.getLogStructure());
        mainArgsBean.setResultLogStructure(mainArgs.getResultLogStructure());
        mainArgsBean.setConditionJavaSource(mainArgs.getConditionJavaSource());

        runMain(mainArgs);
        return String.format("Set main config:\n%s",
                om.setDefaultPrettyPrinter(new DefaultPrettyPrinter()).writeValueAsString(mainArgsBean));
    }

    @Override
    public void runMain(MainArgs mainArgs) throws Exception {
        logService.init();
    }
}
