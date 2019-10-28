package com.example.app.handler.annon;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author MarkHuang
 * @version <ul>
 *  <li>10/27/19, MarkHuang,new
 * </ul>
 * @since 10/27/19
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface IgnoreException {

}
