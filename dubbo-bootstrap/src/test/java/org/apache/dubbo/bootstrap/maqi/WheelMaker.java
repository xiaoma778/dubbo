package org.apache.dubbo.bootstrap.maqi;

import com.alibaba.dubbo.common.URL;
import com.alibaba.dubbo.common.extension.Adaptive;
import com.alibaba.dubbo.common.extension.SPI;

/**
 * @Author: maqi
 * @Date: 2019-12-08 15:31
 * @Version 1.0
 */
@SPI
public interface WheelMaker {

    /**
     * 这里的 @Adaptive("Wheel") 注解表示当调用通过动态创建的 WheelMaker 自适应扩展类（WheelMaker$Adaptive）的 makeWheel(URL url) 方法时，自适应扩展类中会先通过
     * url.getParameter("Wheel") 获取对应的字符串，然后在通过 ExtensionLoader.getExtensionLoader(WheelMaker.class).getExtension(url.getParameter("Wheel")) 方法
     * 获取到最终要调用的 WheelMaker 实现类，来调用真正的 makeWheel(URL url) 方法
     * 注：如果 Adaptive 注解里没有填写默认值的话，那么在动态创建自适应扩展点的代码片段时，会解析当前的类名来作为获取自适应扩展点的key
     * 具体获取过程就是循环遍历类名的 char[] 数组，在每个大写字母（首字母除外）前加上小数点后，将所有字符都变成小写，如当前类就是：url.getParameter("heel.maker")
     * @param url
     * @return
     */
    @Adaptive
    String makeWheel(URL url);
}
