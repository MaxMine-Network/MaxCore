package ru.maxmine.core.logger;

import io.netty.handler.logging.LogLevel;

public class Logger {

    public void log(LogLevel level, String info, Object... objects) {

        String string =
                "[" +
                level.toString() +
                "]" +
                " " +
                String.format(info, objects);


        System.out.println(string);
    }

}
