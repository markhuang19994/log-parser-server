package com.example.app.handler;

import com.example.app.model.LogDetail;
import com.example.app.service.LogService;
import com.example.app.service.MainService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author MarkHuang
 * @version <ul>
 * <li>10/26/19, MarkHuang,new
 * </ul>
 * @since 10/26/19
 */
@RestController
public class BaseHandler {

    private final MainService mainService;
    private final Environment env;
    private final LogService logService;

    public BaseHandler(MainService mainService, Environment env, LogService logService) {
        this.mainService = mainService;
        this.env = env;
        this.logService = logService;
    }

    @GetMapping(path = {"/", "/index"})
    public ModelAndView hello() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("index");
        return modelAndView;
    }

    @PostMapping("/set/main_args")
    public String setMainArgs(HttpServletRequest request) throws Exception {
        String mainConfigPath = request.getParameter("data");
        return mainService.init(mainConfigPath);
    }

    @GetMapping("/get/main_args")
    public Map<String, String> getMainConfigs(HttpServletRequest request) throws Exception {
        String configDirPath = env.getProperty("main.config.dir");
        if (configDirPath == null)
            throw new RuntimeException("Config dir path is not define.");

        File configDir = new File(configDirPath);
        if (!configDir.exists())
            throw new RuntimeException("Config dir is not exist:" + configDirPath);

        File[] mainConfigs = configDir.listFiles(file -> file.getName().contains("properties"));
        if (mainConfigs == null || mainConfigs.length == 0)
            throw new RuntimeException("Main config not found at:" + configDirPath);

        Map<String, String> result = new LinkedHashMap<>();
        for (File mainConfig : mainConfigs) {
            result.put(mainConfig.getName(), mainConfig.getAbsolutePath());
        }
        return result;
    }

    @PostMapping("/get/current-log-details")
    public ResponseEntity<?> getCurrentLogDetails(HttpServletRequest request) {
        String p = env.getProperty("browser-log-detail-limit");
        int limit = Integer.parseInt(p == null ? "5000" : p);
        String startStr = request.getParameter("start");
        String endStr = request.getParameter("end");

        List<LogDetail> currentLogDetails = logService.getCurrentLogDetailsWithFormat();

        int start = "".equals(startStr) ? 0 : Integer.parseInt(startStr);
        int end = "".equals(endStr) ? start + limit : Integer.parseInt(endStr) + 1;
        end = Math.min(end, Math.min(start + limit, currentLogDetails.size()));

        List<LogDetail> logDetails = currentLogDetails.subList(start, end);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("logDetails", logDetails);
        result.put("start", start);
        result.put("end", end);
        result.put("limit", limit);
        result.put("total", currentLogDetails.size());
        return ResponseEntity.ok(result);
    }

    @PostMapping("/get/current-log-details-size")
    public ResponseEntity<?> getCurrentLogDetailsSize(HttpServletRequest request) {
        List<LogDetail> currentLogDetails = logService.getCurrentLogDetailsWithFormat();
        return ResponseEntity.ok(Collections.singletonMap("size", currentLogDetails.size()));
    }
}
