package org.apache.dubbo.bootstrap.maqi.impl.wheel;

import com.alibaba.dubbo.common.URL;

import org.apache.dubbo.bootstrap.maqi.WheelMaker;

/**
 * @Author: maqi
 * @Date: 2019-12-08 20:32
 * @Version 1.0
 */
public class WheelMakerWrapper implements WheelMaker {

    private WheelMaker wheelMaker;

    public WheelMakerWrapper(WheelMaker wheelMaker) {
        this.wheelMaker = wheelMaker;
    }

    @Override
    public String makeWheel(URL url) {
        return "开始..." + wheelMaker.makeWheel(url) + "...结束";
    }
}
