package cn.henry.springbootlearning.aspect.redislook;

import java.lang.annotation.*;

/**
 * @Description:redis锁（对于相同的key,只能锁住按顺序执行，等前一个执行完执行！！并不是接下来相同key的请求不执行了！！！）
 * @Author:hang
 * @Data:2018/11/22 10:27 AM
 **/
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RedisLock {

    /**
     * 指定 lock key ;指定value后， key\prefix 设置将无效；
     * 默认为：方法名+":"+参数hash
     * @return
     */
    String value() default "";

    /**
     * 指定业务 key ;  默认为参数hash；可以是 SPEL表达式从参数中取值:(ex: #this[0].id - 第一个参数ID... )
     * ;最终lock key 为 prefix +":"+ key
     * @return
     */
    String key() default "";

    /**
     * 指定前缀  默认为 方法名；最终lock key 为 prefix +":"+ key
     * @return
     */
    String prefix() default "";

    /**
     * 锁等待时间 默认  10 * 1000 ms
     * @return
     */
    int timeout() default 10 * 1000;

    /**
     * 锁超时时间 默认 60 * 1000 ms
     * @return
     */
    int expire() default 60 * 1000;

    /**
     * 异常信息
     * @return
     */
    String message() default "";

}
