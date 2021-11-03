package ru.maxmine.core;

import jline.console.ConsoleReader;
import lombok.Getter;
import lombok.NonNull;
import ru.maxmine.core.api.Command;
import ru.maxmine.core.api.groups.GroupManager;
import ru.maxmine.core.api.plugin.PluginManager;
import ru.maxmine.core.commands.CorePlugins;
import ru.maxmine.core.database.MySQL;
import ru.maxmine.core.logger.BungeeLogger;
import ru.maxmine.core.packets.PacketManager;
import ru.maxmine.core.types.Player;
import ru.maxmine.core.types.Proxy;
import ru.maxmine.core.types.Server;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

@Getter
public class MaxMineCore {

    @Getter
    private final static MySQL database = MySQL.newBuilder()
            .host("localhost")
            .user("root")
            .password("")
            .database("Groups")
            .create();

    @Getter
    private static MaxMineCore instance;

    @Getter
    private static final Map<String, Player> players = new HashMap<>();

    @Getter
    private static final Map<String, Server> servers = new HashMap<>();

    @Getter
    private final static File pluginsFolder = new File("plugins/");

    private ConsoleReader consoleReader;
    private Logger logger;
    private PluginManager pluginManager;
    private final Map<String, Command> commands = new ConcurrentHashMap<>();

    public MaxMineCore() throws Exception {
        instance = this;
        new PacketManager();
        long start = System.currentTimeMillis();
        new CoreServer();
        load();

        getLogger().info(String.format("Core started (%d ms)", System.currentTimeMillis() - start));
        startConsoleReader();
    }

    private void load() throws IOException {
        consoleReader = new ConsoleReader();
        this.consoleReader.setExpandEvents(false);
        logger = new BungeeLogger(this);
        if(!pluginsFolder.exists()) pluginsFolder.mkdir();
        pluginManager = new PluginManager(this);

        this.pluginManager.detectPlugins(getPluginsFolder());
        this.pluginManager.loadPlugins();

        new GroupManager();

        this.pluginManager.enablePlugins();
        new CorePlugins();
        //new ServerBalancer().start();
    }

    public static Player getPlayer(String name) {
        return players.getOrDefault(name.toLowerCase(), null);
    }

    public static Server getServer(String name) {
        return servers.getOrDefault(name.toLowerCase(), null);
    }

    public void registerServer(Server server) {
        servers.put(server.getName().toLowerCase(), server);
    }

    public void removeServer(String server) {
        servers.remove(server.toLowerCase());
    }

    private void startConsoleReader() {
        new Thread(() -> {
           do {
               try {
                   String line = this.consoleReader.readLine(">");
                   String[] args = line.split(" ");
                   String command = args[0];

                   switch (command.toLowerCase()) {
                       case "exit":
                           Runtime.getRuntime().exit(0);
                           break;

                       case "servers": {
                           for (Server server : servers.values()) {
                               System.out.println(server.getName() + ":");
                               System.out.println("  Online: " + server.getOnline());
                               System.out.println("  Status: " + server.getStatus());
                           }

                           break;
                       }

                       case "proxys": {
                           for (Proxy proxy : Proxy.getProxys().values()) {
                               System.out.println(proxy.getName() + ":");
                               System.out.println(" Online: " + proxy.getOnline());
                           }

                           break;
                       }

                       default:
                           System.out.println("Command not found");
                           break;
                   }

               } catch (Exception ignored) { }
           } while (true);
        }).start();
    }

    public int getOnline() {
        return Player.getPlayers().size();
    }

    public void executeCommand(@NonNull Player player, @NonNull String command, String[] args) {
        this.commands.get(command.toLowerCase())
            .execute(player, args);
    }

    public void registerCommand(String name, Command cmd) {
        this.commands.put(name, cmd);

        for (Proxy proxy : Proxy.getProxys().values())
            proxy.registerCommand(name);
    }

}
