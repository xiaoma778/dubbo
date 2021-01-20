package org.apache.dubbo.bootstrap.maqi;

import com.alibaba.dubbo.common.URL;
import com.alibaba.dubbo.common.extension.Adaptive;
import com.alibaba.dubbo.common.extension.SPI;

/**
 * @Author: maqi
 * @Date: 2019-12-08 15:29
 * @Version 1.0
 */
@SPI
public interface CarMaker {

    String makeCar(URL url);

    @Adaptive("Car")
    void printCar(URL url);
}
