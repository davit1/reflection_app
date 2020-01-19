package Executor;

import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;


class MethodFinder {

    private List<Method> methodList = new LinkedList<Method>();

    private void getAllMethods(Class klass) {
        Method[] methods = klass.getMethods();
        for (Method method : methods) {
            if (method.isAnnotationPresent(Task.class)) {
                methodList.add(method);
            }
        }
    }

    List<Method> getMethodList(Class klass) {
        getAllMethods(klass);
        return methodList;
    }
}
