package org.tangbean.proxypatterndemo.handler;

import javafx.beans.binding.ObjectExpression;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class RequestHandler implements InvocationHandler {
    private Object target;

    public RequestHandler(Object target) {
        this.target = target;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        System.out.println("代理类类名: " + proxy.getClass().getName());
        System.out.println("当前调用的方法名：" + method.getName());
        System.out.println("方法传入的参数：" + Arrays.toString(args));

        if (method.getName().matches("request.*")) {
            beforeInvoke();
        } else {
            preAnotherNameMethod();
        }

        Object res = method.invoke(target, args);  // 被代理的对象 target 才是真正去干活的

        if (method.getName().matches("request.*")) {
            afterInvoke();
        } else {
            postAnotherNameMethod();
        }

        return res;
    }

    public void beforeInvoke() {
        System.out.println("---- Invoke time: " + new Date() + " ----");
    }

    public void afterInvoke() {
        System.out.println("---- After invoke ----");
    }

    private void preAnotherNameMethod() {
        System.out.println("---- Before another name method ----");
    }

    private void postAnotherNameMethod() {
        System.out.println("---- After another name method ----");
    }
}
