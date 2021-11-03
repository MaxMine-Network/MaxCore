//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package ru.maxmine.core.api.event;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class EventHandlerMethod {
    private final Object listener;
    private final Method method;

    public Object getListener() {
        return this.listener;
    }

    public Method getMethod() {
        return this.method;
    }

    public EventHandlerMethod(Object listener, Method method) {
        this.listener = listener;
        this.method = method;
    }

    public void invoke(Object event) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        this.method.invoke(this.listener, event);
    }
}
