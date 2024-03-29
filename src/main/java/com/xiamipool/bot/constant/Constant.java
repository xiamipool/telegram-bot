package com.xiamipool.bot.constant;

import java.io.File;

/**
 * 常量
 */
public class Constant {

    public static final String ARRAYS_SEPARATOR = ",";
    public static final String CONNECTOR = "_";
    public static final String SLASH = "/";
    public static final String CHARSET = "UTF-8";
    public static final File PID_FILE = new File("flag.pid");

    public static class Command {
        /**
         * 状态：帮助、空投
         */
        public static final String HELP = "/help";
        public static final String AIRDROP = "/airdrop";
    }

}
