# SpringBoot-Studying
* templates运用 (delete)
* freemark运用
* redis/mysql配置文件
* schedule 定时任务
   * @Scheduled(fixedRate = 5000) ：上一次开始执行时间点之后5秒再执行
   * @Scheduled(fixedDelay = 5000) ：上一次执行完毕时间点之后5秒再执行
   * @Scheduled(initialDelay=1000, fixedRate=5000) ：第一次延迟1秒后执行，之后按fixedRate的规则每5秒执行一次
   * @Scheduled(cron="*/5 * * * * *") ：通过cron表达式定义规则
* 日志输出文件（logback.xml）
* 线程池
* 日志切面打印
* guava限流
* redis 分布式锁
* java8 Collectors api
    > groupBy、mapping、counting、averagingInt、summarizingInt、joining、maxBy、minBy用法



