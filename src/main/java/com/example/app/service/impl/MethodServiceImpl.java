package com.example.app.service.impl;

import com.example.app.method.condition.LogCondition;
import com.example.app.method.content.LogContentChanger;
import com.example.app.method.custom.GlobalLogMethod;
import com.example.app.service.MethodService;
import com.example.app.util.FileUtil;
import org.mdkt.compiler.InMemoryJavaCompiler;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

/**
 * @author MarkHuang
 * @version <ul>
 * <li>10/20/19, MarkHuang,new
 * </ul>
 * @since 10/20/19
 */
@Service
public class MethodServiceImpl implements MethodService {

    private static int count = 0;
    private String classPattern = "^([\\s\\S]*?public[ \t]+class[ \t]+)([\\s\\S]*?)(\\{[\\s\\S]*)$";

    @Override
    public LogCondition getLogConditionInstance(String conditionJavaSource) throws Exception {
        try {
            String className = "LogCondition_" + count++;
            String pattern = "^([\\s\\S]*?public[ \t]+class[ \t]+)([\\s\\S]*?)(\\{[\\s\\S]*)$";
            conditionJavaSource = conditionJavaSource.replaceAll(pattern, "$1" + className + " extends AbstractLogCondition " + "$3");

            Class<?> resultClass = InMemoryJavaCompiler.newInstance().useParentClassLoader(LogCondition.class.getClassLoader()).compile(className, "import com.example.app.method.condition.AbstractLogCondition;\n" + conditionJavaSource);
            return (LogCondition) resultClass.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new Exception(e.getMessage() + "\n" + "source:\n" + conditionJavaSource);
        }
    }

    @Override
    public LogCondition getLogConditionInstance(String conditionJavaSource, String method) throws Exception {
        conditionJavaSource = conditionJavaSource.replace("${auto}", method);
        return getLogConditionInstance(conditionJavaSource);
    }


    @Override
    public LogContentChanger getContentChangeMethodInstance(String javaSource) throws Exception {
        try {
            String className = "ContentChange_" + count++;
            javaSource = javaSource.replaceAll(classPattern, "$1" + className + " extends AbstractLogContentChanger " + "$3");

            Class<?> resultClass = InMemoryJavaCompiler.newInstance()
                    .useParentClassLoader(LogContentChanger.class.getClassLoader())
                    .compile(className, "import com.example.app.method.content.AbstractLogContentChanger;\n" + javaSource);
            return (LogContentChanger) resultClass.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new Exception(e.getMessage() + "\n" + "source:\n" + javaSource);
        }
    }

    @Override
    public GlobalLogMethod getGlobalMethodInstance(String method) throws Exception {
        String javaSource = FileUtil.readFileAsString(new ClassPathResource("/method/global/GlobalLogMethodImpl.java").getFile());
        javaSource = javaSource.replace("${auto}", method);
        try {
            String className = "Global_" + count++;
            javaSource = javaSource.replaceAll(classPattern, "$1" + className + " extends AbstractGlobalLogMethod " + "$3");

            Class<?> resultClass = InMemoryJavaCompiler.newInstance()
                    .useParentClassLoader(LogContentChanger.class.getClassLoader())
                    .compile(className, "import com.example.app.method.custom.AbstractGlobalLogMethod;\n" + javaSource);
            return (GlobalLogMethod) resultClass.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new Exception(e.getMessage() + "\n" + "source:\n" + javaSource);
        }
    }

}
