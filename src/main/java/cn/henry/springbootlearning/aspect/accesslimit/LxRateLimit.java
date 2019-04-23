package cn.henry.springbootlearning.aspect.accesslimit;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;
/**
 * @Description:guava限流；（每秒钟向桶中放入指定的令牌数，如果每秒桶中的令牌数消耗完毕，则在当秒其他请求会直接返回"服务器繁忙"(等待时间为0情况下);）
 * @Author:hang
 * @Data:2019-04-23 15:55
 **/
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LxRateLimit {

    /**
     * 每秒向桶中放入令牌的数量   默认最大即不做限流
     * @return
     */
    double perSecond() default Double.MAX_VALUE;

    /**
     * 获取令牌的等待时间  默认0
     * @return
     */
    int timeOut() default 0;

    /**
     * 超时时间单位
     * @return
     */
    TimeUnit timeOutUnit() default TimeUnit.MILLISECONDS;
}
