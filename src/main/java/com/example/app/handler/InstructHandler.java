package com.example.app.handler;

import com.example.app.handler.annon.IgnoreException;
import com.example.app.service.ArgumentService;
import com.example.app.service.impl.LogServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * @author MarkHuang
 * @version <ul>
 * <li>10/26/19, MarkHuang,new
 * </ul>
 * @since 10/26/19
 */
@RestController
@IgnoreException
public class InstructHandler {
    private final LogServiceImpl logService;
    private final ArgumentService argumentService;

    private ObjectMapper om = new ObjectMapper();

    public InstructHandler(LogServiceImpl logService, ArgumentService argumentService) {
        this.logService = logService;
        this.argumentService = argumentService;
    }

    @PostMapping("/exec/instruct/method")
    public String method(HttpServletRequest request) throws Exception {
        //todo group by method
        //todo change content method
        //todo cache origin log detail
        //todo logback
        //todo use reflection instead of compile new class
        String data = request.getParameter("data");
        String[] args = argumentService.parseArgumentStr(data);
        logService.generateLogByConditionMethod(args);
        return "";
    }

    @PostMapping("/exec/instruct/life")
    public String life(HttpServletRequest request) throws Exception {
        String data = request.getParameter("data");
        String[] args = argumentService.parseArgumentStr(data);
        if ("his".equals(args[0]) || "history".equals(args[0])) {
            logService.recoverHistory(Integer.parseInt(args[1]));
        }
        return "";
    }

}
