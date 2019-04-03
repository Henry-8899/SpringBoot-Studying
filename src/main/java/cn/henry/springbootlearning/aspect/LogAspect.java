package cn.henry.springbootlearning.aspect;

import cn.henry.springbootlearning.utils.HttpRequestHelper;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * @Description:记录日志
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

    @Before("logAspect()")
    public void doBefore(JoinPoint jp) {
        //获得请求（https://www.cnblogs.com/xdp-gacl/p/3798347.html）
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();

        //记录请求内容
        log.info("URL : {} ;HTTP_METHOD : {} ;IP : {} ;ARGS : {}",
                request.getRequestURL().toString(), request.getMethod(), HttpRequestHelper.getIpAddr(request), request.getQueryString());
    }

    @AfterReturning(returning = "ret", pointcut = "logAspect()")
    public void doAfterReturning(Object ret) {
        log.info("response : " + ret);
    }

}
