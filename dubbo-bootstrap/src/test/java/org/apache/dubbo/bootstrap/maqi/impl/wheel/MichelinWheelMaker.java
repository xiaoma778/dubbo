package org.apache.dubbo.bootstrap.maqi.impl.wheel;

import com.alibaba.dubbo.common.URL;

import org.apache.dubbo.bootstrap.maqi.CarMaker;
import org.apache.dubbo.bootstrap.maqi.WheelMaker;

/**
 * @Author: maqi
 * @Date: 2019-12-08 15:32
 * @Version 1.0
 */
public class MichelinWheelMaker implements WheelMaker {
    private CarMaker carMaker;
    public void setCarMaker(CarMaker carMaker) {
        this.carMaker = carMaker;
    }

    @Override
    public String makeWheel(URL url) {
        carMaker.printCar(url);
        return "米其林轮胎";
    }
}
