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
package com.alibaba.dubbo.common.bytecode;

import junit.framework.TestCase;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Arrays;

public class ProxyTest extends TestCase {
    public void testMain() throws Exception {
        Proxy proxy = Proxy.getProxy(ITest.class, ITest.class);
        ITest instance = (ITest) proxy.newInstance(new InvocationHandler() {
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                if ("getName".equals(method.getName())) {
                    assertEquals(args.length, 0);
                } else if ("setName".equals(method.getName())) {
                    assertEquals(args.length, 2);
                    assertEquals(args[0], "qianlei");
                    assertEquals(args[1], "hello");
                }
                return null;
            }
        });

        assertNull(instance.getName());
        instance.setName("qianlei", "hello");
    }

    @Test
    public void testCglibProxy() throws Exception {
        ITest test = (ITest) Proxy.getProxy(ITest.class).newInstance(new InvocationHandler() {

            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                System.out.println(method.getName());
                return null;
            }
        });

        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(test.getClass());
        enhancer.setCallback(new MethodInterceptor() {

            public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
                return null;
            }
        });
        try {
            enhancer.create();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void testOther() {
        Proxy testProxy = Proxy.getProxy(ITest.class);
        ITest test = (ITest)testProxy.newInstance(new InvocationHandler() {
            private String name;

            public String getName() {
                return name;
            }

            public void setName(String name, String name2) {
                this.name = name;
            }
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                //String methodName = method.getName();
                //Class<?>[] methodParamterTypes = method.getParameterTypes();
                // 拦截定义在 Object 类中的方法（未被子类重写），比如 wait/notify
                //if (method.getDeclaringClass() == Object.class) {
                //    return method.invoke(, args);
                //}
                System.out.print(String.format("method : %s, args : %s", method.getName(), args.length > 0 ? Arrays.asList(args) : "无参"));
                if ("getName".equals(method.getName())) {
                    String value = getName();
                    System.out.println(" return value : " + value);
                    return value;
                } else if ("setName".equals(method.getName())) {
                    System.out.println();
                    setName(String.valueOf(args[0]), String.valueOf(args[1]));
                }
                return null;
            }
        });
        test.setName("zhangsan", "si");
        System.out.println(test.getName());
    }

    public interface ITest {
        String getName();

        void setName(String name, String name2);
    }

    public static class ITestImpl implements ITest {
        private String name;

        @Override
        public String getName() {
            return name;
        }

        @Override
        public void setName(String name, String name2) {
            this.name = name;
            System.out.println(String.format("name = %s, name2 = %s", name, name2));
        }
    }
}