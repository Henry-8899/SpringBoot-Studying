package cn.henry.springbootlearning.config.threadpool;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * @Description:线程池
 * @Author:hang
 * @Data:2018/12/25 8:18 PM
 **/
@Configuration
public class ThreadPoolConfig {

    @Bean(name = "auditThreadPool")
    public ThreadPoolTaskExecutor threadPoolExecutor() {
        ThreadPoolTaskExecutor threadPool  = new ThreadPoolTaskExecutor();
        threadPool.setCorePoolSize(10);
        threadPool.setMaxPoolSize(10);
        threadPool.setQueueCapacity(Integer.MAX_VALUE);
        threadPool.setKeepAliveSeconds(5);
        threadPool.setWaitForTasksToCompleteOnShutdown(true);
        threadPool.setThreadNamePrefix("audit-thread-");
        threadPool.initialize();
        return threadPool;
    }
}
