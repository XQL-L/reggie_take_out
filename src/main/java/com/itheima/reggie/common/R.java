package com.itheima.reggie.common;

import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 通用返回结果
 * @param <T>
 */
@Data
public class R<T>  implements Serializable {

    private Integer code;//编码 ： 1成功，其他失败

    private  String msg;//错误信息

    private T data;//数据

    private Map map = new HashMap();

    public static <T> R<T> success(T object){ //返回成功R
        R<T> r = new R<>();
        r.code = 1;
        r.data = object;
        return r;
    }

    public static <T> R<T> error(String msg){ //返回失败R，附带错误信息
        R<T> r = new R<>();
        r.code = 0;
        r.msg = msg;
        return r;
    }

    public R<T> add(String key, Object value){
        this.map.put(key,value);
        return this;
    }




}
