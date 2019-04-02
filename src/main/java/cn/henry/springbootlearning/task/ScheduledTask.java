package cn.henry.springbootlearning.task;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @Description:定时任务
 * @Author:hang
 * @Data:2019-04-02 17:41
 **/
@Component
public class ScheduledTask {
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * @Scheduled(fixedRate = 5000) ：上一次开始执行时间点之后5秒再执行
     * @Scheduled(fixedDelay = 5000) ：上一次执行完毕时间点之后5秒再执行
     * @Scheduled(initialDelay=1000, fixedRate=5000) ：第一次延迟1秒后执行，之后按fixedRate的规则每5秒执行一次
     * @Scheduled(cron="？？？？？") 通过cron表达式定义规则
     */
    @Scheduled(fixedRate = 5000)
    public void outputTime(){
        System.out.println("now time: " + dateFormat.format(new Date()));
    }
}
