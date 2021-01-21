package com.gydx.utils;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author 拽小白
 * @createTime 2020-10-31 12:17
 * @description 通用工具
 */
public class CommonUtil {

    private static final ThreadLocal<SimpleDateFormat> SIMPLE_DATE_FORMAT_THREAD_LOCAL = ThreadLocal.withInitial(() -> new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));

    /**
     * @return 返回当前时间
     */
    public static String getNowTime() {
        return SIMPLE_DATE_FORMAT_THREAD_LOCAL.get().format(new Date());
    }

    /**
     * 将对象o序列化成二进制数组
     * @param o 要进行序列化的对象
     * @return 返回序列化后的二进制数组
     * @throws IOException 可能会抛出io异常
     */
    public static byte[] serialization(Object o) throws IOException {
        byte[] byteData;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
        // 将对象序列化成二进制流
        objectOutputStream.writeObject(o);
        // 将二进制流转成二进制数组
        byteData = byteArrayOutputStream.toByteArray();
        return byteData;
    }

    /**
     * 将二进制数组反序列化成对象
     * @param byteData 要反序列化的二进制数组
     * @param clazz 要生成的对象的class对象
     * @return 返回反序列化后的对象
     * @throws IOException 可能会抛出io异常
     * @throws ClassNotFoundException 可能会抛出要生成的对象所属的类不存在的异常
     */
    public static <T> T deserialization(byte[] byteData, Class<T> clazz) throws IOException, ClassNotFoundException {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteData);
        ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
        Object o = objectInputStream.readObject();
        if (clazz.isInstance(o)) {
            return clazz.cast(o);
        }
        return null;
    }

    /**
     * 将对象o转成json字符串
     */
    public static String objectToJson(Object o) {
        return JSONObject.toJSONString(o);
    }

    /**
     * 将json字符串转成指定类型的对象
     */
    public static <T> T jsonToObject(String json, Class<T> clazz) {
        return JSONObject.parseObject(json, clazz);
    }

}
