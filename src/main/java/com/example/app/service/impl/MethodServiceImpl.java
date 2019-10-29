package com.example.app.service.impl;

import com.example.app.condition.LogCondition;
import com.example.app.content.LogContentChanger;
import com.example.app.service.MethodService;
import com.example.app.util.FileUtil;
import org.mdkt.compiler.InMemoryJavaCompiler;
import org.springframework.stereotype.Service;

import java.io.File;

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
    public LogCondition generateLogConditionClass(String conditionJavaSource) throws Exception {
        try {
            String className = "LogCondition_" + count++;
            String pattern = "^([\\s\\S]*?public[ \t]+class[ \t]+)([\\s\\S]*?)(\\{[\\s\\S]*)$";
            conditionJavaSource = conditionJavaSource.replaceAll(pattern, "$1" + className + " extends AbstractLogCondition " + "$3");

            Class<?> resultClass = InMemoryJavaCompiler.newInstance().useParentClassLoader(LogCondition.class.getClassLoader()).compile(className, "import com.example.app.condition.AbstractLogCondition;\n" + conditionJavaSource);
            return (LogCondition) resultClass.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new Exception(e.getMessage() + "\n" + "source:\n" + conditionJavaSource);
        }
    }

    @Override
    public LogCondition generateLogConditionClassAndMethod(String conditionJavaSource, String method) throws Exception {
        conditionJavaSource = conditionJavaSource.replace("${auto}", method);
        return generateLogConditionClass(conditionJavaSource);
    }


    @Override
    public LogContentChanger generateContentChangeMethodClass(String javaSource) throws Exception {
        try {
            String className = "ContentChange_" + count++;
            javaSource = javaSource.replaceAll(classPattern, "$1" + className + " extends AbstractLogContentChanger " + "$3");

            Class<?> resultClass = InMemoryJavaCompiler.newInstance()
                    .useParentClassLoader(LogContentChanger.class.getClassLoader())
                    .compile(className, "import com.example.app.content.AbstractLogContentChanger;\n" + javaSource);
            return (LogContentChanger) resultClass.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new Exception(e.getMessage() + "\n" + "source:\n" + javaSource);
        }
    }

}
