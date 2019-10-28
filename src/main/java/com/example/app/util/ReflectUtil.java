package com.example.app.util;

import com.example.app.tool.cache.Cache;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author MarkHuang
 * @version <ul>
 * <li>10/29/19, MarkHuang,new
 * </ul>
 * @since 10/29/19
 */
public final class ReflectUtil {

    private static Cache<String, Method> cache = new Cache<>();

    private ReflectUtil() {
        throw new AssertionError();
    }

    @SuppressWarnings("unchecked")
    public static <T> T executeMethodWithStringArgs(
            Object instance, String methodName, List<String> args) throws InvocationTargetException, IllegalAccessException {
        Class<?> cls = instance.getClass();
        Method method = null;
        String key = null;
        try {
            key = EncryptUtil.encryptMd5(cls.getName() + methodName + String.join("", args));
            method = cache.get(key);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        Method[] methods = cls.getMethods();
        Method[] declaredMethods = cls.getDeclaredMethods();

        if (method == null) {
            for (Method declaredMethod : declaredMethods) {
                if (methodName.equals(declaredMethod.getName())) {
                    method = declaredMethod;
                    method.setAccessible(true);
                    break;
                }
            }
        }

        if (method == null) {
            for (Method m : methods) {
                if (methodName.equals(m.getName())) {
                    method = m;
                    method.setAccessible(true);
                    break;
                }
            }
        }

        if (method == null) {
            throw new IllegalArgumentException(cls.getSimpleName() + " Method not found:" + methodName);
        }

        Object result = method.invoke(instance, args);
        if (key != null) {
            cache.put(key, method, 1, TimeUnit.DAYS);
        }

        return ((T) result);
    }

}
