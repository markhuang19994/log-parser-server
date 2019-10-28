package com.example.app.handler;

import com.example.app.handler.annon.IgnoreException;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.AbstractHandlerExceptionResolver;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;

/**
 * @author MarkHuang
 * @version <ul>
 * <li>10/27/19, MarkHuang,new
 * </ul>
 * @since 10/27/19
 */
@Component
public class RestResponseStatusExceptionResolver extends AbstractHandlerExceptionResolver {

    @Override
    protected ModelAndView doResolveException(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler,
            Exception ex) {

        if (!(handler instanceof HandlerMethod)) return null;
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        IgnoreException annotation = handlerMethod.getBeanType().getAnnotation(IgnoreException.class);
        if (annotation == null) return null;

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setView((map, httpServletRequest, httpServletResponse) -> {
            httpServletResponse.setStatus(200);
            ServletOutputStream os = httpServletResponse.getOutputStream();
            if (os == null) return;
            try (Writer writer = new OutputStreamWriter(os);) {
                if (ex != null) {
                    writer.write(ex.getMessage());
                    writer.flush();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        return modelAndView;
    }
}
