//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package ru.maxmine.core.logger;

import java.util.Iterator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.LogRecord;

public class LogDispatcher extends Thread {
    private final BungeeLogger logger;
    private final BlockingQueue<LogRecord> queue = new LinkedBlockingQueue();

    public LogDispatcher(BungeeLogger logger) {
        super("BungeeCord Logger Thread");
        this.logger = logger;
    }

    public void run() {
        while(!this.isInterrupted()) {
            LogRecord record;
            try {
                record = this.queue.take();
            } catch (InterruptedException var3) {
                continue;
            }

            this.logger.doLog(record);
        }

        Iterator var4 = this.queue.iterator();

        while(var4.hasNext()) {
            LogRecord record = (LogRecord)var4.next();
            this.logger.doLog(record);
        }

    }

    public void queue(LogRecord record) {
        if (!this.isInterrupted()) {
            this.queue.add(record);
        }

    }
}
