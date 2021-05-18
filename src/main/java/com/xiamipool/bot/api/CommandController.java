package com.xiamipool.bot.api;

import com.xiamipool.bot.service.CommandService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

@Api(tags = "电报机器人")
@RestController
@RequestMapping("api/app/telegram/bot")
@Slf4j
public class CommandController {

    @Resource
    private CommandService commandService;

    @ApiOperation(value = "指令回调")
    @PostMapping(value = "hook")
    public synchronized boolean hook(HttpServletRequest request) throws Exception {
        StringBuilder sb = new StringBuilder();
        try {
            ServletInputStream servletInputStream = request.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(servletInputStream, StandardCharsets.UTF_8);
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = "";
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            log.info("hello:{}", sb.toString());
            return commandService.hook(sb.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }
}
