package com.example.server;

import com.example.app.MainArgs;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * @author MarkHuang
 * @version <ul>
 *  <li>10/27/19, MarkHuang,new
 * </ul>
 * @since 10/27/19
 */
@Component
public class Beans {

    @Bean
    public MainArgs mainArgs(){
        return new MainArgs();
    }

}
