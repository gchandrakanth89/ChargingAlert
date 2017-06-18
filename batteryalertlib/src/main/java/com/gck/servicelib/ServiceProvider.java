package com.gck.servicelib;

/**
 * Created by Pervacio on 13-06-2017.
 */

public class ServiceProvider {
    public static IService getServiceProvider() {
        return new ServiceImpl();
    }
}
