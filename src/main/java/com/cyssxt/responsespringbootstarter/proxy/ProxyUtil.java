package com.cyssxt.responsespringbootstarter.proxy;

import java.lang.reflect.*;

public class ProxyUtil {

    public static void main(String[] args) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Class clazz = Proxy.getProxyClass(Person.class.getClassLoader(),Person.class);
        Constructor<Person> constructor  =  clazz.getConstructor(InvocationHandler.class);
        Person person = constructor.newInstance(new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                return "11111";
            }
        });
        System.out.println(person.say());
    }
}
