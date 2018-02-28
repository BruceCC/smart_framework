package org.leave.framework.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class JsonUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(JsonUtil.class);

    private static  final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    /**
     * 将pojo转为json
     */
    public static <T> String toJson(T obj){
        String json;
        try {
            json = OBJECT_MAPPER.writeValueAsString(obj);
        }catch (Exception e){
            LOGGER.error("convert pojo to json failure", e);
            throw new RuntimeException();
        }
        return json;
    }

    /**
     * 将json转为pojo
     */
    public static <T> T fromJson(String json, Class<T> type){
        T pojo;
        try {
            pojo = OBJECT_MAPPER.readValue(json, type);
        }catch (Exception e){
            LOGGER.error("convert json to pojo failure", e);
            throw new RuntimeException();
        }
        return pojo;
    }
 }
