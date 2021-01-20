package org.apache.dubbo.bootstrap.maqi.impl.car;

import com.alibaba.dubbo.common.URL;

import org.apache.dubbo.bootstrap.maqi.CarMaker;
import org.apache.dubbo.bootstrap.maqi.WheelMaker;

/**
 * @Author: maqi
 * @Date: 2019-12-08 15:30
 * @Version 1.0
 */
public class RaceCarMaker implements CarMaker {

    private WheelMaker wheelMaker;

    /**
     * 设置 WheelMaker
     *
     * @param wheelMaker 通过 SPI 机制 会帮我们注入 WheelMaker$Adaptive 类，该类内容如下：
     *                   package com.alibaba.dubbo.common.extensionloader.maqi;
     *                   import com.alibaba.dubbo.common.extension.ExtensionLoader;
     *
     *                   public class WheelMaker$Adaptive implements com.alibaba.dubbo.common.extensionloader.maqi.WheelMaker {
     *
     *                   public java.lang.String makeWheel(com.alibaba.dubbo.common.URL arg0) {
     *                   if (arg0 == null) throw new IllegalArgumentException("url == null");
     *                   com.alibaba.dubbo.common.URL url = arg0;
     *                   String extName = url.getParameter("Wheel");
     *                   if(extName == null)
     *                   throw new IllegalStateException("Fail to get extension(com.alibaba.dubbo.common.extensionloader.maqi.WheelMaker) name from url(" + url.toString() + ") use keys([Wheel])");
     *                   com.alibaba.dubbo.common.extensionloader.maqi.WheelMaker extension = (com.alibaba.dubbo.common.extensionloader.maqi.WheelMaker)ExtensionLoader.getExtensionLoader(com.alibaba.dubbo.common.extensionloader.maqi.WheelMaker.class).getExtension(extName);
     *                   return extension.makeWheel(arg0);
     *                   }
     *                   }
     */
    public void setWheelMaker(WheelMaker wheelMaker) {
        this.wheelMaker = wheelMaker;
    }

    @Override
    public String makeCar(URL url) {
        System.out.println(String.format("赛车制造商，我的轮胎是：%s", wheelMaker.makeWheel(url)));
        //System.out.println("赛车制造商");
        return "我是赛车制造商";
    }

    @Override
    public void printCar(URL url) {
        System.out.println("printCar has called! url : " + url);
    }
}
