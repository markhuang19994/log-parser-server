package com.example.app.handler;

import com.example.app.handler.annon.IgnoreException;
import com.example.app.service.ArgumentService;
import com.example.app.service.LogService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

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
    private final LogService logService;
    private final ArgumentService argumentService;

    public InstructHandler(LogService logService, ArgumentService argumentService) {
        this.logService = logService;
        this.argumentService = argumentService;
    }

    @PostMapping("/exec/instruct/fmethod")
    public String fmethod(HttpServletRequest request) throws Exception {
        String data = request.getParameter("data");
        String[] args = argumentService.parseArgumentStr(data);
        logService.generateLogByFilterMethod(args);
        return "";
    }

    @PostMapping("/exec/instruct/cmethod")
    public String cmethod(HttpServletRequest request) throws Exception {
        String data = request.getParameter("data");
        String[] args = argumentService.parseArgumentStr(data);
        logService.generateLogByChangeContentMethod(args);
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

    @PostMapping("/exec/instruct/format")
    public String format(HttpServletRequest request) throws IOException {
        String data = request.getParameter("data");
        String[] args = argumentService.parseArgumentStr(data);
        return logService.setFormat(args);
    }

}
