/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.dubbo.rpc.proxy.javassist;

import com.alibaba.dubbo.common.URL;
import com.alibaba.dubbo.common.bytecode.Proxy;
import com.alibaba.dubbo.common.bytecode.Wrapper;
import com.alibaba.dubbo.common.extension.ExtensionLoader;
import com.alibaba.dubbo.rpc.Invoker;
import com.alibaba.dubbo.rpc.ProxyFactory;
import com.alibaba.dubbo.rpc.proxy.AbstractProxyFactory;
import com.alibaba.dubbo.rpc.proxy.AbstractProxyInvoker;
import com.alibaba.dubbo.rpc.proxy.InvokerInvocationHandler;

/**
 * JavaassistRpcProxyFactory
 */
public class JavassistProxyFactory extends AbstractProxyFactory {

    //Proxy.getProxy()方法首先会创建类似下面这样的代理类信息，而后再通过 newInstance(new InvokerInvocationHandler(invoker)) 方法来实例化这个代理对象
    //package org.apache.dubbo.common.bytecode;
    //
    //public class proxy0 implements org.apache.dubbo.demo.DemoService {
    //    public static java.lang.reflect.Method[] methods;
    //    private java.lang.reflect.InvocationHandler handler;
    //
    //    public proxy0() {
    //    }
    //
    //    public proxy0(java.lang.reflect.InvocationHandler arg0) {
    //        handler = arg0;
    //    }
    //
    //    public java.lang.String sayHello(java.lang.String arg0) {
    //        Object[] args = new Object[1];
    //        args[0] = ($w)arg0;//最后调用 InvocationHandler.invoke() 方法
    //        Object ret = handler.invoke(this, methods[0], args);
    //        return (java.lang.String)ret;
    //    }
    //}
    @Override
    @SuppressWarnings("unchecked")
    public <T> T getProxy(Invoker<T> invoker, Class<?>[] interfaces) {
        // 生成 Proxy 子类（Proxy 是抽象类）。并调用 Proxy 子类的 newInstance 方法创建 Proxy 实例
        // 这里 newInstance(new InvokerInvocationHandler(invoker)) = new proxy0(InvokerInvocationHandler)!!!!!
        return (T) Proxy.getProxy(interfaces).newInstance(new InvokerInvocationHandler(invoker));
    }

    /**
     * 创建 Invoke 对象（注：以下参数解释以导出 DemoService 时为例来说明）
     * @param proxy DemoServiceImple的实例化对象ref
     * @param type  DemoService.class
     * @param url Registry URL
     * @param <T>
     * @return
     */
    @Override
    public <T> Invoker<T> getInvoker(T proxy, Class<T> type, URL url) {
        // 为目标类创建 Wrapper
        // TODO Wrapper cannot handle this scenario correctly: the classname contains '$'
        // Wrapper 类不能正确处理带 $ 的类名
        final Wrapper wrapper = Wrapper.getWrapper(proxy.getClass().getName().indexOf('$') < 0 ? proxy.getClass() : type);
        // 创建匿名 Invoker 类对象，并实现 doInvoke 方法。
        return new AbstractProxyInvoker<T>(proxy, type, url) {
            @Override
            protected Object doInvoke(T proxy, String methodName,
                                      Class<?>[] parameterTypes,
                                      Object[] arguments) throws Throwable {
                // 调用 Wrapper 的 invokeMethod 方法，invokeMethod 最终会调用目标方法
                return wrapper.invokeMethod(proxy, methodName, parameterTypes, arguments);
            }
        };
    }
}
