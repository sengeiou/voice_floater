package com.gydx.utils;

import org.springframework.context.ApplicationContext;

/**
 * @author 拽小白
 * @createTime 2020-11-15 13:53
 * @description
 */
public class SpringContextUtil {

    private static ApplicationContext applicationContext;

    private SpringContextUtil() {
        //spring 上下文辅助类
    }

    /**
     * 获取上下文
     * @return
     */
    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    /**
     * 设置上下文
     * @param applicationContext
     */
    public static void setApplicationContext(ApplicationContext applicationContext) {
        SpringContextUtil.applicationContext = applicationContext;
    }

    /**
     * 通过名字获取上下文中的bean
     * @param name
     * @return
     */
    public static Object getBean(String name){
        return applicationContext.getBean(name);
    }

    /**
     * 通过类型获取上下文中的bean
     * @param requiredType
     * @return
     */
    public static Object getBean(Class<?> requiredType){
        return applicationContext.getBean(requiredType);
    }


}
