//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package ru.maxmine.core.logger;

import ru.maxmine.core.MaxMineCore;

import java.io.IOException;
import java.util.logging.Logger;
import java.util.logging.*;

public class BungeeLogger extends Logger {
    private final Formatter formatter = new ConciseFormatter();
    private final LogDispatcher dispatcher = new LogDispatcher(this);

    public BungeeLogger(MaxMineCore core) {
        super("Core", null);
        this.setLevel(Level.ALL);

        try {
            FileHandler fileHandler = new FileHandler("proxy.log", 0, 1, true);
            fileHandler.setFormatter(this.formatter);
            this.addHandler(fileHandler);
            ColouredWriter consoleHandler = new ColouredWriter(core.getConsoleReader());
            consoleHandler.setLevel(Level.INFO);
            consoleHandler.setFormatter(this.formatter);
            this.addHandler(consoleHandler);
        } catch (IOException var4) {
            System.err.println("Could not register logger!");
            var4.printStackTrace();
        }

        this.dispatcher.start();
    }

    public void log(LogRecord record) {
        this.dispatcher.queue(record);
    }

    void doLog(LogRecord record) {
        super.log(record);
    }
}
