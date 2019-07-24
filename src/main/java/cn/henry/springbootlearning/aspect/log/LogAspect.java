package cn.henry.springbootlearning.aspect.log;

import cn.henry.springbootlearning.utils.HttpRequestHelper;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.UUID;

/**
 * @Description:记录日志  （参考：https://blog.csdn.net/zhengchao1991/article/details/53391244）
 * @Author:hang
 * @Data:2019-04-03 11:02
 **/
@Aspect
@Component
@Slf4j
public class LogAspect {

    @Pointcut(value = "@annotation(org.springframework.web.bind.annotation.RequestMapping)")
    public void logAspect() {

    }

    @Around("logAspect()")
    public Object controllerLogAround(ProceedingJoinPoint joinPoint) throws Throwable {
        /**
         * 获取 request 中包含的请求参数
         */
        String uuid = UUID.randomUUID().toString();
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        /**
         * 获取切点请求参数(class,method)
         */
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        StringBuilder params = new StringBuilder();
        ObjectMapper mapper = new ObjectMapper();
        if ("POST".equalsIgnoreCase(request.getMethod())) {
            Object[] objects = joinPoint.getArgs();
            for (Object arg : objects) {
                params.append(mapper.writeValueAsString(arg));
            }
        }
        if ("GET".equalsIgnoreCase(request.getMethod())) {
            params.append(request.getQueryString());
        }
        /**
         * 入参日志
         */
        log.info("[AOP-LOG-START]\n\trequestMark: {}\n\trequestIP: {}\n\tcontentType:{}\n\trequestUrl: {}\n\t" +
                        "requestMethod: {}\n\trequestParams: {}\n\ttargetClassAndMethod: {}#{}", uuid, HttpRequestHelper.storeIp(request),
                request.getHeader("Content-Type"),request.getRequestURL(), request.getMethod(), params.toString(),
                method.getDeclaringClass().getName(), method.getName());
        /**
         * 出参日志
         */
        Object result = joinPoint.proceed();
        log.info("[AOP-LOG-END]\n\t{}", result);
        return result;
    }


}
