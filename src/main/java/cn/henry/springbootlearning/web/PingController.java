package cn.henry.springbootlearning.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class PingController {

    @ResponseBody  //注解作用：返回结果不会被解析为跳转路径，而是直接写入 HTTP response body 中
    @RequestMapping("/ping")
    public String ping() {
        return "hello";
    }
}
