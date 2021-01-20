package org.apache.dubbo.bootstrap.maqi.impl.wheel;

import com.alibaba.dubbo.common.URL;
import com.alibaba.dubbo.common.extension.ExtensionLoader;

import org.apache.dubbo.bootstrap.maqi.WheelMaker;

/**
 * @Author: maqi
 * @Date: 2019-12-09 02:30
 * @Version 1.0
 */
public class AdaptiveWheelMaker implements WheelMaker {
    @Override
    public String makeWheel(URL url) {
        if (url == null)
            throw new IllegalArgumentException("url == null");

        String wheelMakerName = url.getParameter("Wheel");
        if (wheelMakerName == null)
            throw new IllegalArgumentException("wheelMakerName == null");

        WheelMaker wheelMaker = ExtensionLoader.getExtensionLoader(WheelMaker.class).getExtension(wheelMakerName);
        return wheelMaker.makeWheel(url);
    }
}
