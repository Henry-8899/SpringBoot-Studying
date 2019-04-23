package cn.henry.springbootlearning.aspect.accesslimit;

import com.google.common.util.concurrent.RateLimiter;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Aspect
@Component
@Slf4j
public class LxRateLimitAspect {

    private RateLimiter rateLimiter = RateLimiter.create(Double.MAX_VALUE);

    @Pointcut("@annotation(cn.henry.springbootlearning.aspect.accesslimit.LxRateLimit)")
    public void checkPointcut() {
    }

    @Around(value = "checkPointcut()")
    public Object aroundNotice(ProceedingJoinPoint pjp) throws Throwable {
        log.info("拦截到了{}方法...", pjp.getSignature().getName());
        MethodSignature methodSignature = (MethodSignature) pjp.getSignature();
        Method targetMethod = methodSignature.getMethod();
        if (targetMethod.isAnnotationPresent(LxRateLimit.class)) {
            LxRateLimit lxRateLimit = targetMethod.getAnnotation(LxRateLimit.class);
            rateLimiter.setRate(lxRateLimit.perSecond());
            if (!rateLimiter.tryAcquire(lxRateLimit.timeOut(), lxRateLimit.timeOutUnit()))
                return "服务器繁忙，请稍后再试!";

        }
        return pjp.proceed();
    }
}
