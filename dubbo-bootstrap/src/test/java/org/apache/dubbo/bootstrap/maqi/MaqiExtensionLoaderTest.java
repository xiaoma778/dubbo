package org.apache.dubbo.bootstrap.maqi;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import com.alibaba.dubbo.common.Constants;
import com.alibaba.dubbo.common.URL;
import com.alibaba.dubbo.common.compiler.support.AdaptiveCompiler;
import com.alibaba.dubbo.common.extension.Adaptive;
import com.alibaba.dubbo.common.extension.ExtensionLoader;
import com.alibaba.dubbo.registry.RegistryFactory;
import com.alibaba.dubbo.remoting.Dispatcher;
import com.alibaba.dubbo.rpc.Filter;
import com.alibaba.dubbo.rpc.Protocol;
import com.alibaba.dubbo.rpc.ProxyFactory;
import com.alibaba.dubbo.rpc.cluster.Cluster;

import org.apache.dubbo.bootstrap.maqi.impl.car.RaceCarMaker;
import org.apache.dubbo.bootstrap.maqi.impl.car.TruckCarMaker;
import org.apache.dubbo.bootstrap.maqi.impl.wheel.WheelMakerWrapper;
import org.junit.Assert;
import org.junit.Test;

/**
 * @Author: maqi
 * @Date: 2019-12-08 15:36
 * @Version 1.0
 */
public class MaqiExtensionLoaderTest {

    @Test
    public void test_getExtensionLoader() {
        ExtensionLoader<CarMaker> extensionLoader = ExtensionLoader.getExtensionLoader(CarMaker.class);
        CarMaker carMaker = extensionLoader.getExtension("racecar");
        Assert.assertTrue("error msg : carMaker is not RaceCarMaker", carMaker instanceof RaceCarMaker);

        Map<String, String> params = new HashMap<String, String>();
        params.put("Wheel", "michelin");
        params.put("Car", "racecar");
        //params.put("Wheel", "adaptive");
        URL url = new URL("", "", 0, params);
        carMaker.makeCar(url);

        carMaker = extensionLoader.getExtension("truckcar");
        Assert.assertTrue("carMaker is not TruckCarMaker", carMaker instanceof TruckCarMaker);
        carMaker.makeCar(url);
    }

    @Test
    public void test_WheelWrapper() {
        WheelMaker wheelMaker = ExtensionLoader.getExtensionLoader(WheelMaker.class).getExtension("michelin");
        Assert.assertTrue("wheelMaker is not WheelMakerWrapper", wheelMaker instanceof WheelMakerWrapper);
        HashMap<String, String> params = new HashMap();
        params.put("Car", "racecar");
        URL url = new URL("", "", 0, params);
        System.out.println(wheelMaker.makeWheel(url));
    }

    /**
     * 添加了 Adaptive 注解的类，则不会再动态创建
     */
    @Test
    public void test_compile() {
        ExtensionLoader<com.alibaba.dubbo.common.compiler.Compiler> loader = ExtensionLoader.getExtensionLoader(com.alibaba.dubbo.common.compiler.Compiler.class);
        com.alibaba.dubbo.common.compiler.Compiler compiler = loader.getAdaptiveExtension();
        Assert.assertEquals(compiler.getClass(), AdaptiveCompiler.class);
        System.out.println(compiler.getClass().getName());
    }
    
    @Test
    public void testGetActivateExtension() {
        //Map<String, String> paramter = new HashMap<String, String>();
        //paramter.put("sayHello.retries", "3");
        //paramter.put("side", "consumer");
        //paramter.put("register.ip", "192.168.3.62");
        //paramter.put("methods", "sayHello,sayBye");
        //paramter.put("qos.port", "33333");
        //paramter.put("dubbo", "2.0.2");
        //paramter.put("pid", "1110");
        //paramter.put("check", "false");
        //paramter.put("interface", "com.alibaba.dubbo.demo.DemoService");
        //paramter.put("version", "1.0");
        //paramter.put("generic", "false");
        //paramter.put("timeout", "1000000");
        //paramter.put("revision", "1.0");
        //paramter.put("application", "demo-consumer");
        //paramter.put("sayHello.executes", "15");
        //paramter.put("scope", "remote");
        //paramter.put("remote.timestamp", "1584161990626");
        //paramter.put("export", "true");
        //paramter.put("anyhost", "true");
        //paramter.put("timestamp", "1584163037305");
        URL url = new URL("dubbo", "169.254.233.101", 20880);
        // 参见 ProtocolFilterWrapper.buildInvokerChain() 方法
        List<Filter> prividerFilters = ExtensionLoader.getExtensionLoader(Filter.class).getActivateExtension(url, Constants.SERVICE_FILTER_KEY, Constants.PROVIDER);
        System.out.println(prividerFilters.size());
        //todo 对比启动服务调用方时少了一个 FutureFilter
        System.out.println("prividerFilters : ");
        for (Filter filter : prividerFilters)
            System.out.println(filter.getClass().getName());

        System.out.println("consumerfilters : ");
        List<Filter> consumerfilters = ExtensionLoader.getExtensionLoader(Filter.class).getActivateExtension(url, Constants.SERVICE_FILTER_KEY, Constants.CONSUMER);
        System.out.println(consumerfilters.size());
        for (Filter filter : consumerfilters)
            System.out.println(filter.getClass().getName());
    }

    /**
     * 没有添加 Adaptive 注解的类会动态创建一个适配器类
     */
    @Test
    public void test_createAdaptiveExtensionClassCode() throws NoSuchMethodException {
        Method method = WheelMaker.class.getMethod("makeWheel", URL.class);
        Adaptive adaptiveAnnotation = method.getAnnotation(Adaptive.class);
        String[] value = adaptiveAnnotation.value();
        System.out.println(value.length);

        createAdaptiveExtensionClassCode(Dispatcher.class);
        createAdaptiveExtensionClassCode(Cluster.class);
        createAdaptiveExtensionClassCode(ProxyFactory.class);
        createAdaptiveExtensionClassCode(RegistryFactory.class);
        createAdaptiveExtensionClassCode(WheelMaker.class);
        createAdaptiveExtensionClassCode(CarMaker.class);
        createAdaptiveExtensionClassCode(Protocol.class);
    }

    @Test
    public void testProxyFactory() {
        ProxyFactory proxyFactory = ExtensionLoader.getExtensionLoader(ProxyFactory.class).getAdaptiveExtension();
        System.out.println(proxyFactory);
    }

    @Test
    public void test_protocol() {
        //ExtensionLoader<Protocol> loader = ExtensionLoader.getExtensionLoader(Protocol.class);
        //Protocol protocol = loader.getAdaptiveExtension();//Protocol$Adaptive
        System.out.println(Protocol.class.getName());

        ExtensionLoader<Protocol> loader = ExtensionLoader.getExtensionLoader(Protocol.class);
        Protocol protocol = loader.getExtension("registry");
        iterateProtocol(protocol);
        iterateProtocol(ExtensionLoader.getExtensionLoader(Protocol.class).getAdaptiveExtension());

        // TODO 这里可以自定义几个 Filter 测试一下
        URL url = new URL("dubbo", "192.168.3.62", 20880);
        List<Filter> filters = ExtensionLoader.getExtensionLoader(Filter.class).getActivateExtension(url, Constants.SERVICE_FILTER_KEY, Constants.PROVIDER_PROTOCOL);
        System.out.println("filters.size = " + filters.size());
    }

    /**
     * 打印嵌套的 protocol 信息
     * @param protocol
     */
    void iterateProtocol(Protocol protocol) {
        System.out.println(protocol.getClass().getName());
        Class clazz = protocol.getClass();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            if (Protocol.class.isAssignableFrom(field.getDeclaringClass()) && field.getGenericType() == Protocol.class) {
                try {
                    field.setAccessible(true);
                    iterateProtocol((Protocol)field.get(protocol));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 动态创建并打印适配器类(Adaptive)信息
     * @param clazz
     * @param <T>
     */
    <T> String createAdaptiveExtensionClassCode(Class<T> clazz)  {
        ExtensionLoader<T> loader = ExtensionLoader.getExtensionLoader(clazz);
        loader.getAdaptiveExtension();// 先调用加载一下 clazz
        try {
            Method method = loader.getClass().getDeclaredMethod("createAdaptiveExtensionClassCode", null);
            method.setAccessible(true);
            String code = (String)method.invoke(loader, null);

            System.out.println(String.format("\n\n%s$Adaptive:\n%s", clazz.getSimpleName(), code));
            return code;
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    <T> String createAdaptiveExtensionClassCode(ExtensionLoader<T> loader)  {
        try {
            Method method = loader.getClass().getDeclaredMethod("createAdaptiveExtensionClassCode", null);
            method.setAccessible(true);
            String code = (String)method.invoke(loader, null);
            System.out.println(String.format("\n\n%s$Adaptive:\n%s", loader.getDefaultExtension(), code));
            return code;
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }
}
