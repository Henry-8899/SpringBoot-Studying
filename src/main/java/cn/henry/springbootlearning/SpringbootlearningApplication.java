package cn.henry.springbootlearning;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling //scheduling 定时任务
public class SpringbootlearningApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringbootlearningApplication.class, args);
    }

}
