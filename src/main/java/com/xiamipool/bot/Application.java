package com.xiamipool.bot;

import com.xiamipool.bot.constant.Constant;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.ApplicationPidFileWriter;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class Application {

    private static ApplicationContext context;

    public static void main(String[] args) {
        SpringApplication springApplication = new SpringApplication(Application.class);
        springApplication.addListeners(new ApplicationPidFileWriter(Constant.PID_FILE));
        context = springApplication.run(args);
    }
}
