package ru.maxmine.core.serverbalancer;

import org.apache.commons.io.FileUtils;
import ru.maxmine.core.MaxMineCore;
import ru.maxmine.core.ServerManager;
import ru.maxmine.core.types.Server;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class ServerBalancer extends Thread {

    private ThreadLocalRandom random = ThreadLocalRandom.current();

    private Map<String, Integer> started = new HashMap<>();
    private Map<String, String> toStart = new HashMap<>();

    public void run() {
        while (true) {
            try {
                Thread.sleep(30000);
                checkServers();
                startServers();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void checkServers() {
        if(getCount() == 0) return;
        List<Server> gameServers = ServerManager.findAllGameServers();
        if(gameServers.size() == 0) return;
        List<String> filtered = new ArrayList<>();

        for (Server server : gameServers) {
            if(filtered.contains(server.getMap())) continue;
            if(ServerManager.findFreeServers(server.getMap()).size() == 0) {
                MaxMineCore.getInstance().getLogger().info("Find need server with map " + server.getMap());
                toStart.put(server.getName().split("-")[0], server.getMap());
                filtered.add(server.getMap());
            }
        }
    }

    private void startServers() {
        Runtime runtime = Runtime.getRuntime();

        toStart.forEach((server, map) -> {
            String name = server + "-" + getServers(server);

            try {

                /* ProcessBuilder builder = new ProcessBuilder();
                builder.directory(new File("/"))
                        .command("cd home/Servers/" + server + "/Files",
                                "mkdir /home/Run/" + name,
                                "cp -R ../Maps/" + map + " /home/Run/" + name,
                                "cp -R ../Configs/" + map + " /home/Run/" + name + "/plugins",
                                "cp -R * /home/Run/" + name + " && cd",
                                "cp -R * /home/Plugins/ /home/Run/" + name + "/plugins",
                                "sed -i -e 's/server-name=0/server-name='"+ name +"'/g' server.properties",
                                "sed -i -e 's/server-port=0/server-port='"+ getPort() +"'/g' server.properties",
                                "screen -dmS " + name + " java -jar spigot.jar")
                        .redirectOutput(new File("/home/", "test.log"))
                        .start(); */

                String directory = "/home/Servers/" + server + "/";
                File serverDir = new File("/home/Run/" + name);
                File mapFile = new File(serverDir, map);
                File configServerDir = new File(serverDir, "plugins");
                if(!serverDir.exists()) serverDir.mkdir();
                else FileUtils.cleanDirectory(serverDir);
                if(!mapFile.exists()) mapFile.mkdir();
                if(!configServerDir.exists()) configServerDir.mkdir();

                File filesDir = new File("/home/Servers/" + server + "/Files");
                File mapDir = new File("/home/Servers/" + server + "/Maps/" + map);
                File configDir = new File("/home/Servers/" + server + "/Configs/" + map);

                FileUtils.copyDirectory(filesDir, serverDir);
                FileUtils.copyDirectory(mapDir, mapFile);
                FileUtils.copyDirectory(configDir, configServerDir);
                FileUtils.copyDirectory(new File("/home/Plugins"), configServerDir);

                String[] commands = new String[]{
                        /* "mkdir /home/Run/" + name,
                        "cp -R " + directory + "Maps/" + map + " /home/Run/" + name,
                        "cp -R " + directory + "Configs/" + map + "/* /home/Run/" + name + "/plugins",
                        "cp -R " + directory + "/Files /home/Run/" + name + "",
                        "cp -R * /home/Plugins/ /home/Run/" + name + "/plugins", */
                        "/bin/bash -c sed -i -e 's/server-name=0/server-name='"+ name +"'/g' server.properties",
                        "/bin/bash -c sed -i -e 's/server-port=0/server-port='"+ getPort() +"'/g' server.properties",
                        "/bin/bash -c screen -dmS " + name + " java -jar spigot.jar"
                };

                startProcess("/bin/bash", "-c", "cd", "/home/Run" + server);
                startProcess("/bin/bash", "-c", "sed", "-i", "-e", "'s/server-name=0/server-name='"+ name +"'/g'", "server.properties");
                startProcess("/bin/bash", "-c", "echo", "123321");

                //File executor = new File("", "executor.sh");
                //if(!executor.exists()) executor.createNewFile();
                //PrintWriter writer = new PrintWriter(executor, "UTF-8");
                //writer.println("#!/bin/bash");
                //writer.println();
                //writer.println("cd /home/Run/" + server);
                //writer.println("sed -i -e 's/server-name=0/server-name='"+ name +"'/g' server.properties");
                //writer.println("sed -i -e 's/server-port=0/server-port='"+ getPort() +"'/g' server.properties");
                //writer.println("screen -dmS " + name + " java -jar spigot.jar");
                //writer.close();
                //executor.setExecutable(true);

                //runtime.exec(executor.getPath());

                //Process process = runtime.exec(executor.getPath());

                started.put(name, getServers(server) + 1);
                toStart.remove(server);
                MaxMineCore.getInstance().getLogger().info("Started server " + name);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void startProcess(String... command) throws Exception {
        Process process = Runtime.getRuntime().exec(command, null, new File("/bin"));

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String s = null;

        while ((s = bufferedReader.readLine()) != null) {
            MaxMineCore.getInstance().getLogger().info(s);
        }

        process.waitFor();
        bufferedReader.close();
        process.destroy();
    }

    public int getPort() {
        int port = 0;

        while (isPortAvailable((port = random.nextInt(65530)))) return port;

        return 0;
    }

    public static boolean isPortAvailable(int port) {
        try (ServerSocket serverSocket = new ServerSocket()) {

            serverSocket.setReuseAddress(false);
            serverSocket.bind(new InetSocketAddress(InetAddress.getByName("localhost"), port), 1);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    public int getServers(String branch) {
        return started.getOrDefault(branch, 2);
    }

    private int getCount() {
        int players = MaxMineCore.getPlayers().size();

        if(players == 0) return 0;
        else if (players > 20 && players < 40) return 1;
        else if (players > 100 && players < 200) return 2;
        else if (players > 200 && players < 400) return 4;

        return 5;
    }
}
