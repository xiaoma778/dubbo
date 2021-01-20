package org.apache.dubbo.bootstrap.maqi.impl.car;

import com.alibaba.dubbo.common.URL;
import org.apache.dubbo.bootstrap.maqi.CarMaker;

/**
 * @Author: maqi
 * @Date: 2019-12-08 15:40
 * @Version 1.0
 */
public class TruckCarMaker implements CarMaker {
    @Override
    public String makeCar(URL url) {
        System.out.println("卡车制造商");
        return "卡车制造商";
    }

    @Override
    public void printCar(URL url) {
        System.out.println(url);
    }
}
