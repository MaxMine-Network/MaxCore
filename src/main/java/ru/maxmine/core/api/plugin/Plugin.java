//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package ru.maxmine.core.api.plugin;

import lombok.Getter;
import ru.maxmine.core.MaxMineCore;
import ru.maxmine.core.api.config.Configuration;
import ru.maxmine.core.api.config.ConfigurationProvider;
import ru.maxmine.core.api.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Logger;

public class Plugin {
    private PluginDescription description;
    private File file;
    private Logger logger;
    private File configFile;
    private Configuration config;
    private YamlConfiguration yaml;

    @Getter
    private boolean state;

    @Getter
    private MaxMineCore core;

    public Plugin() {
    }

    public PluginDescription getDescription() {
        return this.description;
    }

    public File getFile() {
        return this.file;
    }

    public Logger getLogger() {
        return this.logger;
    }

    public void onLoad() {
    }

    public void onEnable() {
    }

    public void onDisable() {
    }

    public final File getDataFolder() {
        return new File(MaxMineCore.getPluginsFolder(), this.getDescription().getName());
    }

    public final InputStream getResourceAsStream(String name) {
        return this.getClass().getClassLoader().getResourceAsStream(name);
    }

    final void init(MaxMineCore core, PluginDescription description) {
        this.core = core;
        this.description = description;
        this.file = description.getFile();
        this.logger = new PluginLogger(this);
        this.loadConfig();
        this.state = true;
    }

    final void disable() {
        this.onDisable();
        this.state = false;
        saveConfig();
    }

    public Configuration getConfig() {
        return this.config;
    }

    public void loadConfig() {
        this.configFile = new File(this.getDataFolder(), "config.yml");
        this.yaml = (YamlConfiguration) ConfigurationProvider.getProvider(YamlConfiguration.class);
        if (this.configFile.exists()) {
            try {
                this.config = this.yaml.load(this.configFile);
            } catch (IOException var2) {
                var2.printStackTrace();
            }
        }

    }

    public void saveConfig() {
        try {
            this.yaml.save(this.config, this.configFile);
        } catch (IOException var2) {
            var2.printStackTrace();
            this.getLogger().info("Error on save configuration");
        }

    }

    public void saveDefaultConfig() {
        if (this.config == null) {
            try {
                if (!this.getDataFolder().exists()) {
                    this.getDataFolder().mkdir();
                }

                if (!this.configFile.exists()) {
                    InputStream is = this.getResourceAsStream("config.yml");
                    if (is == null) {
                        throw new IllegalStateException("config.yml not found in plugin jar file");
                    }

                    this.yaml.save(this.yaml.load(is), this.configFile);
                    this.loadConfig();
                    this.getLogger().info("Config loaded");
                }
            } catch (IOException var2) {
                var2.printStackTrace();
            }

        }
    }
}
