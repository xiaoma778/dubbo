package org.apache.dubbo.bootstrap.maqi;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.alibaba.dubbo.common.Constants;
import com.alibaba.dubbo.common.URL;
import com.alibaba.dubbo.common.bytecode.Wrapper;
import com.alibaba.dubbo.common.extension.ExtensionLoader;
import com.alibaba.dubbo.rpc.Invocation;
import com.alibaba.dubbo.rpc.Invoker;
import com.alibaba.dubbo.rpc.ProxyFactory;
import com.alibaba.dubbo.rpc.Result;
import com.alibaba.dubbo.rpc.RpcInvocation;
import com.alibaba.dubbo.rpc.proxy.AbstractProxyInvoker;

import org.apache.dubbo.bootstrap.maqi.impl.car.RaceCarMaker;
import org.apache.dubbo.bootstrap.maqi.impl.wheel.MichelinWheelMaker;
import org.junit.Test;

/**
 * @Author: maqi
 * @Date: 2020-03-13 12:38
 * @Version 1.0
 */
public class InvokeTest {

    private URL createURL() {
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("registry", "zookeeper");
        parameters.put("backup", "zoo2:2181,zoo3:2181");
        parameters.put("application", "demo-provider");
        parameters.put("qos.port", "22222");
        parameters.put("dubbo", "2.0.2");
        parameters.put("pid", "1344");
        parameters.put("export", "dubbo%3A%2F%2F192.168.3.62%3A20880%2Fcom.alibaba.dubbo.demo.DemoService%3Fanyhost%3Dtrue%26application%3Ddemo-provider%26bind.ip%3D192.168.3.62%26bind.port%3D20880%26dubbo%3D2.0.2%26export%3Dtrue%26generic%3Dfalse%26interface%3Dcom.alibaba.dubbo.demo.DemoService%26methods%3DsayHello%26pid%3D1344%26qos.port%3D22222%26revision%3D1.0%26sayHello.executes%3D15%26sayHello.retries%3D3%26scope%3Dremote%26side%3Dprovider%26timeout%3D100000%26timestamp%3D1584074227777%26version%3D1.0");
        parameters.put("timestamp", "1584074227736");
        return new URL("registry", "zoo1", 2181, "com.alibaba.dubbo.registry.RegistryService", parameters);
    }

    @Test
    public void testWrapper() throws InvocationTargetException {
        final Wrapper wrapper = Wrapper.getWrapper(CarMaker.class);
        //wrapper.
        wrapper.invokeMethod(new RaceCarMaker(), "printCar", new Class[]{URL.class}, new Object[]{createURL()});
    }

    @Test
    public void testAbstractProxyInvoker() {
        // 该步骤会在服务端根据配置信息先生成好
        Provider provider = new Provider(new Impl1());
        //这里会在客户端调用，告知要调哪一个服务的哪一个方法，及其相关参数是啥
        Result result = provider.invoke("hello", new Object[]{"ha"});
        System.out.println(result);

        Consumer consumer = new Consumer(provider);
        consumer.hello("sss");
    }

    private class Provider {
        private Object proxy;
        private Invoker<?> invoker;

        public Provider() {}

        public <T> Provider(T proxy) {
            this.proxy = proxy;
            invoker = getInvoke(proxy);
        }

        public <T> void setProxy(T proxy) {
            this.proxy = proxy;
        }

        private <T> Invoker<T> getInvoke(T proxy) {
            final Wrapper wrapper = Wrapper.getWrapper(proxy.getClass());
            return new AbstractProxyInvoker<T>(proxy, (Class<T>)proxy.getClass(), createURL()) {
                @Override
                protected Object doInvoke(T proxy, String methodName,
                                          Class<?>[] parameterTypes,
                                          Object[] arguments) throws Throwable {
                    // 调用 Wrapper 的 invokeMethod 方法，invokeMethod 最终会调用目标方法
                    return wrapper.invokeMethod(proxy, methodName, parameterTypes, arguments);
                }
            };
            //abstractProxyInvoker.invoke((Invocation)Proxy.newProxyInstance(Invocation.class.getClassLoader(), new Class[] {Invocation.class}, new InvocationHandler() {
            //    @Override
            //    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            //        System.out.println(method.getName());
            //        return null;
            //    }
            //}));
        }

        public Result invoke(String methodName, Object[] arguments) {
            return invoker.invoke(createRpcInvocation(proxy.getClass(), methodName, arguments));
        }
    }

    private class Consumer {
        private Provider provider;

        public Consumer(Provider provider) {
            this.provider = provider;
        }

        public void hello(String str) {
            provider.invoke("hello", new Object[]{str});
        }
    }

    @Test
    public void testProxy() {
        URL url = createURL();
        ProxyFactory proxyFactory = ExtensionLoader.getExtensionLoader(ProxyFactory.class).getAdaptiveExtension();
        RpcInvocation invocation = new RpcInvocation();
        Invoker<?> invoker = proxyFactory.getInvoker(new MichelinWheelMaker(), WheelMaker.class, url.addParameterAndEncoded(Constants.EXPORT_KEY, url.toFullString()));
        //invoker.invoke();
    }

    private RpcInvocation createRpcInvocation(Class clazz, String methodName, Object[] arguments) {
        try {
            Method method = clazz.getDeclaredMethod(methodName, String.class);
            return new RpcInvocation(method.getName(), method.getParameterTypes(), arguments);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return null;
    }

    private class InvocationImpl {
        private String methodName;
        private Class<?>[] parameterTypes;
        private Object[] arguments;
        private Map<String, String> attachments;
        private Invoker<?> invoker;
    }

    private Invocation createInvocation(final String name) {
        return new Invocation() {
            /**
             * get method name.
             *
             * @return method name.
             * @serial
             */
            @Override
            public String getMethodName() {
                return name;
            }

            /**
             * get parameter types.
             *
             * @return parameter types.
             * @serial
             */
            @Override
            public Class<?>[] getParameterTypes() {
                return new Class[0];
            }

            /**
             * get arguments.
             *
             * @return arguments.
             * @serial
             */
            @Override
            public Object[] getArguments() {
                return new Object[0];
            }

            /**
             * get attachments.
             *
             * @return attachments.
             * @serial
             */
            @Override
            public Map<String, String> getAttachments() {
                return null;
            }

            /**
             * get attachment by key.
             *
             * @param key
             * @return attachment value.
             * @serial
             */
            @Override
            public String getAttachment(String key) {
                return null;
            }

            /**
             * get attachment by key with default value.
             *
             * @param key
             * @param defaultValue
             * @return attachment value.
             * @serial
             */
            @Override
            public String getAttachment(String key, String defaultValue) {
                return null;
            }

            /**
             * get the invoker in current context.
             *
             * @return invoker.
             * @transient
             */
            @Override
            public Invoker<?> getInvoker() {
                return null;
            }
        };
    }

    @Test
    public void testProxy1() {
        ProxyFactory proxyFactory = ExtensionLoader.getExtensionLoader(ProxyFactory.class).getDefaultExtension();
        Invoker<I1> invoker = proxyFactory.getInvoker(new Impl1(), I1.class, createURL());
        System.out.println(invoker.getInterface());
        //invoker.invoke();
    }

    private interface I0 {
        String getName();
    }

    private interface I1 extends I0 {
        void setName(String name);

        void hello(String name);

        int showInt(int v);

        float getFloat();

        void setFloat(float f);
    }

    private class Impl1 implements I1 {
        private String name = "your name";

        private float fv = 0;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void hello(String name) {
            System.out.println("hello " + name);
        }

        public int showInt(int v) {
            return v;
        }

        public float getFloat() {
            return fv;
        }

        public void setFloat(float f) {
            fv = f;
        }
    }
}
