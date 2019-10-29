package com.example.server;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * @author MarkHuang
 * @version <ul>
 * <li>10/26/19, MarkHuang,new
 * </ul>
 * @since 10/26/19
 */
@Configuration
@ComponentScan("com.example")
@PropertySource("classpath:config.properties")
public class SpringConfigure {
}
