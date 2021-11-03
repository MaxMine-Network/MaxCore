package ru.maxmine.core.types;

import io.netty.channel.Channel;
import lombok.Getter;
import lombok.Setter;
import ru.maxmine.core.MaxMineCore;

@Getter
@Setter
public class Server {

    private Channel channel;

    private String name;
    private String ip;
    private int port;
    private int online;

    private String map;
    private int status;

    public Server(Channel channel, String name, String ip, int port) {
        this.channel = channel;
        this.name = name;
        this.ip = ip;
        this.port = port;

        for (Proxy proxy : Proxy.getProxys().values()) {
            proxy.addServer(this);
        }

        MaxMineCore.getInstance().registerServer(this);
        MaxMineCore.getInstance().getLogger().info("Server " + name + " connected to Core");
    }

}
