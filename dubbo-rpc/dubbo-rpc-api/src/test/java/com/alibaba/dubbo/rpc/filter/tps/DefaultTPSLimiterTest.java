package com.alibaba.dubbo.rpc.filter.tps;

import com.alibaba.dubbo.common.Constants;
import com.alibaba.dubbo.common.URL;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @Author: maqi
 * @Date: 2020-03-17 13:04
 * @Version 1.0
 */
public class DefaultTPSLimiterTest {

    @Test
    public void isAllowable() {
        URL url = new URL("dubbo", "127.0.0.1", 20880);
        url.addParameter(Constants.TPS_LIMIT_RATE_KEY, 10);// 每次发放的令牌数
        url.addParameter(Constants.TPS_LIMIT_INTERVAL_KEY, 1000);// 令牌刷新的间隔时间
        //url.addParameter();

        TPSLimiter tpsLimiter = new DefaultTPSLimiter();
        System.out.println(tpsLimiter.isAllowable(url, null));
    }
}