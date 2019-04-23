package cn.henry.springbootlearning.web;

import cn.henry.springbootlearning.model.commons.JsonResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * @Description:处理异常
 * @Author:hang
 * @Data:2018/11/21 11:12 AM
 **/
@ControllerAdvice
@RestController
@Slf4j
public class ExceptionController {

    @Autowired
    private HttpServletRequest httpServletRequest;

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.OK)
    public JsonResult handleGlobalException(Exception e) {
        log.error("[exception][global_exception]url={},error={}", httpServletRequest.getRequestURL(), e);
        return JsonResult.buildError();
    }
}
