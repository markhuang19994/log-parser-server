package com.example.app.service.impl;

import com.example.app.condition.LogCondition;
import com.example.app.service.LogConditionService;
import org.mdkt.compiler.InMemoryJavaCompiler;
import org.springframework.stereotype.Service;

/**
 * @author MarkHuang
 * @version <ul>
 * <li>10/20/19, MarkHuang,new
 * </ul>
 * @since 10/20/19
 */
@Service
public class LogConditionServiceImpl implements LogConditionService {

    private static int count = 0;

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

}
