package cn.henry.springbootlearning.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.Optional;

/**
 * Created by liqing on 2017/10/16 0016.
 */
@Slf4j
public class JsonUtil {
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final ObjectMapper ORDERED_MAPPER = new ObjectMapper();
    private static final ObjectMapper IGNORE_UNKNOWN_MAPPER = new ObjectMapper();

    static {
        MAPPER.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        ORDERED_MAPPER.setSerializationInclusion(JsonInclude.Include.NON_NULL)
                .configure(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, true);
        IGNORE_UNKNOWN_MAPPER.setSerializationInclusion(JsonInclude.Include.NON_NULL)
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    /**
     * 按照约定格式进行类型转换
     *
     * @param json 被转换对象
     * @param clazz 需要转换的类型
     * @param <T>
     * @return
     */
    public static <T> T fromJson(String json, Class<T> clazz) {
        return fromJson(json, clazz, false);
    }

    /**
     * 按照约定格式进行类型转换
     * @param json 被转换对象
     * @param clazz 需要转换的类型
     * @param ignoreUnknown 是否忽略未知字段
     * @param <T>
     * @return
     */
    public static <T> T fromJson(String json, Class<T> clazz, boolean ignoreUnknown) {
        try {
            if (StringUtils.isBlank(json)) {
                return null;
            }
            if (!ignoreUnknown){
                return MAPPER.readValue(json, clazz);
            }else {
                return IGNORE_UNKNOWN_MAPPER.readValue(json, clazz);
            }
        } catch (Exception e) {
            log.error("[JsonUtil.fromJson][clazz]json转换成对象出错,json={},error={}", json, e);
            return null;
        }
    }

    /**
     * 按照约定格式进行类型转换
     * @param json 被转换对象
     * @param typeReference 需要转换的类型
     * @param <T>
     * @return
     */
    public static <T> T fromJson(String json, TypeReference<T> typeReference) {
        return fromJson(json, typeReference, false);
    }

    /**
     * 按照约定格式进行类型转换
     * @param json 被转换对象
     * @param typeReference 需要转换的类型
     * @param ignoreUnknown 是否忽略未知字段
     * @param <T>
     * @return
     */
    public static <T> T fromJson(String json, TypeReference<T> typeReference, boolean ignoreUnknown) {
        try {
            if (StringUtils.isBlank(json)) {
                return null;
            }
            if (!ignoreUnknown){
                return MAPPER.readValue(json, typeReference);
            }else {
                return IGNORE_UNKNOWN_MAPPER.readValue(json, typeReference);
            }
        } catch (Exception e) {
            log.error("[JsonUtil.fromJson][typeReference]json转换成对象出错,json={},error={}", json, e);
            return null;
        }
    }

    /**
     * 将对象转换成json串
     * @param object
     * @return
     */
    public static Optional<String> toJson(Object object) {
        try {
            if (object == null){
                return Optional.empty();
            }
            String ret = MAPPER.writeValueAsString(object);
            return Optional.of(ret);
        } catch (Exception e) {
            log.error("[JsonUtil.toJson]对象转换成json出错,error={}", e);
            return Optional.empty();
        }
    }

    /**
     * 将对象转换成json串,key按字典序升序,不支持集合类型,只支持java bean
     * @param object
     * @return
     */
    public static String toOrderedJson(Object object) {
        try {
            if (object == null){
                return null;
            }
            return ORDERED_MAPPER.writeValueAsString(object);
        } catch (Exception e) {
            log.error("[JsonUtil.toOrderedJson]对象转换成json出错,error={}", e);
            return null;
        }
    }

}
