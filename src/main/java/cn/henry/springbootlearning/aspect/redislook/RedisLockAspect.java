package cn.henry.springbootlearning.aspect.redislook;

import cn.henry.springbootlearning.exception.ResubmitException;
import cn.henry.springbootlearning.utils.JsonUtil;
import cn.henry.springbootlearning.utils.MD5Util;
import cn.henry.springbootlearning.utils.RedisLockHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionException;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * @author: liuwei
 * @Description: redis锁实现
 * @Date: 下午4:53 2017/10/10
 */
@Component
@Aspect
@Slf4j
public class RedisLockAspect {

    @Autowired
    @Qualifier("stringRedisTemplate")
    private RedisTemplate<String, String> stringRedisTemplate;

    @Pointcut("@annotation(cn.henry.springbootlearning.aspect.redislook.RedisLock)")
    private void redisLockPointCut () {
    }

    /**
     * @param pjp
     * @return
     * @throws Throwable
     */
    @Around("redisLockPointCut()")
    private Object preventDuplicateSubmit(ProceedingJoinPoint pjp) throws Throwable {
        MethodSignature methodSignature = (MethodSignature) pjp.getSignature();
        Method method = methodSignature.getMethod();
        RedisLock annotation = method.getAnnotation(RedisLock.class);
        Object[] args = pjp.getArgs();
        String lockKey = null;
        int timeout = annotation.timeout();
        String message = annotation.message();
        int expire = annotation.expire();
        String prefix = annotation.prefix();
        if(StringUtils.isBlank(prefix)){
            prefix = method.getName();
        }
        if(StringUtils.isNotBlank(annotation.value())){
            //指定了lockKey
            lockKey = annotation.value();
        }else if(StringUtils.isNotBlank(annotation.key())){
            String keySPEL = annotation.key();
            try {
                if(keySPEL.startsWith("#this")){//判断是否是spel表达式
                    Expression expression = new SpelExpressionParser().parseExpression(keySPEL);
                    String value = expression.getValue(args, String.class);
                    lockKey = prefix.concat(":").concat(value);
                }else{
                    lockKey = prefix.concat(":").concat(keySPEL);
                }
            }catch (ExpressionException e){
                log.error("key表达式“"+keySPEL+"”错误：{}",e);
                throw e;
            }
        }else{
            if (args != null && args.length > 1) {
                lockKey = prefix.concat(":").concat(MD5Util.build(JsonUtil.toJson(args).orElse("")));
            }else{
                return pjp.proceed();
            }
        }
        RedisLockHelper lock = new RedisLockHelper(stringRedisTemplate,lockKey,timeout,expire);
        try {
            if(lock.lock()){
                return pjp.proceed();
            }else{
                log.error("调用方法失败，已有数据在处理中 key：{}", lock.getLockKey());
                throw new ResubmitException(message);
            }
        } catch (InterruptedException e) {
            log.error("获取锁失败：{},{}",lock.getLockKey(),e.getMessage());
            throw new ResubmitException(message);
        }finally {
            lock.unlock();
        }
    }
}
