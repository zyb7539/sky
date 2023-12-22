package com.sky.task;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.Date;

/**
 * 自定义定时任务类
 * */
// @Component
@Slf4j
public class MyTask {

    @Scheduled(cron = "0/5 * * * * ?")
    public void excuteTask(){
        log.info("定时任务开始执行：{}",new Date());
    }


}
