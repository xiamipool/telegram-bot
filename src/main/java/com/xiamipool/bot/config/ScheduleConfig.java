package com.xiamipool.bot.config;

import com.xiamipool.bot.service.CommandService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.telegram.telegrambots.meta.api.objects.Message;

import javax.annotation.Resource;

/**
 * 跑批任务
 */
@Configuration
@EnableScheduling
@Slf4j
public class ScheduleConfig {

    @Resource
    private CommandService commandService;

    @Scheduled(cron = "0 0 * * * ?")
    public void sendDocTask() {
        log.info("send doc start ...");
        commandService.sendDoc("/doc");
        log.info("send doc finish ...");
    }

    @Scheduled(cron = "0 15,45 * * * ?")
    public void sendTokenTask() {
        log.info("send token start ...");
        commandService.sendDoc("/token");
        log.info("send token finish ...");
    }
}
