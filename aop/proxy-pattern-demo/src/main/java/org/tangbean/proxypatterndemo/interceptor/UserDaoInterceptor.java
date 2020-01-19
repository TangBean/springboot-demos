package org.tangbean.proxypatterndemo.interceptor;

import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;
import java.util.Date;

public class UserDaoInterceptor implements MethodInterceptor {

    /**
     * @param o "this", the enhanced object
     * @param method intercepted Method
     * @param objects argument array; primitive types are wrapped
     * @param methodProxy used to invoke super (non-intercepted method); may be called as many times as needed
     * @return any value compatible with the signature of the proxied method. Method returning void will ignore this value.
     * @throws Throwable any exception may be thrown; if so, super method will not be invoked
     */
    @Override
    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
        beforeInvoke();
        Object resObj = methodProxy.invokeSuper(o, objects);
        afterInvoke();
        return resObj;
    }

    public void beforeInvoke() {
        System.out.println("---- Invoke time: " + new Date() + " ----");
    }

    public void afterInvoke() {
        System.out.println("---- After invoke ----");
    }
}
