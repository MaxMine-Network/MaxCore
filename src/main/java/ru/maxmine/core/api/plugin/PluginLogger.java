//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package ru.maxmine.core.api.plugin;

import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class PluginLogger extends Logger {
    private final String pluginName;

    protected PluginLogger(Plugin plugin) {
        super(plugin.getClass().getCanonicalName(), null);
        this.pluginName = "[" + plugin.getDescription().getName() + "] ";
        this.setParent(plugin.getCore().getLogger());
    }

    public void log(LogRecord logRecord) {
        logRecord.setMessage(this.pluginName + logRecord.getMessage());
        super.log(logRecord);
    }
}
