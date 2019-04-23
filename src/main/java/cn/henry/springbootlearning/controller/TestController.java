package cn.henry.springbootlearning.controller;

import cn.henry.springbootlearning.aspect.accesslimit.LxRateLimit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class TestController {


    @RequestMapping("/testlog")
    @LxRateLimit(perSecond = 1.0)
    public String testLog(String param) {
        return "result = " + param;
    }
}

