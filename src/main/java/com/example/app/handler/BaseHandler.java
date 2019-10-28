package com.example.app.handler;

import com.example.app.service.MainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
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
public class BaseHandler {

    @Autowired
    private MainService mainService;

    @GetMapping("/hello")
    public String hello() {
        return "Hello";
    }

    @PostMapping("/set/main_args")
    public String setMainArgs(HttpServletRequest request) throws Exception {
        String mainConfigPath = request.getParameter("data");
        return mainService.init(mainConfigPath);
    }

}
