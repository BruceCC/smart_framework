package org.leave.framework.bean;

import org.leave.framework.util.CastUtil;
import org.leave.framework.util.CollectionUtil;

import java.util.Map;

/**
 * 请求参数对象
 */
public class Param {
    private Map<String, Object> paramMap;

    public Param(Map<String, Object> paramMap) {
        this.paramMap = paramMap;
    }

    public long getLong(String name){
        return CastUtil.castLong(paramMap.get(name));
    }

    public boolean isEmpty(){
        return CollectionUtil.isEmpty(paramMap);
    }

    /**
     * 获取所有字段信息
     */
    public Map<String, Object> getMap(){
        return paramMap;
    }
}
