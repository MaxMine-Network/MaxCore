package ru.maxmine.core.types;

import io.netty.channel.Channel;
import lombok.Getter;
import lombok.Setter;
import ru.maxmine.core.MaxMineCore;
import ru.maxmine.core.buffer.PacketBuffer;
import ru.maxmine.core.packets.Packet;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Getter
public class Proxy {

    @Getter
    private static final Map<String, Proxy> proxys = new HashMap<>();

    private Channel channel;
    private String name;

    @Setter
    private int online;

    public Proxy(Channel ctx, String name) {
        this.channel = ctx;
        this.name = name;

        proxys.put(name, this);

        MaxMineCore.getInstance().getCommands().keySet().forEach(this::registerCommand);
        MaxMineCore.getServers().values().forEach(this::addServer);

        MaxMineCore.getInstance().getLogger().info("Proxy " + name + " connected to Core");
    }

    public void addServer(Server server) {
        Packet packet = new Packet() {
            @Override
            public void write(PacketBuffer packetBuffer) throws IOException {
                packetBuffer.writeIntLE(51);
                packetBuffer.writeString(server.getName());
                packetBuffer.writeString(server.getIp());
                packetBuffer.writeIntLE(server.getPort());
            }

            @Override
            public void handle(PacketBuffer packetBuffer) throws IOException {

            }

            @Override
            public void process(Channel channel) throws IOException {

            }
        };

        channel.writeAndFlush(packet);
    }

    public void registerCommand(String command) {
        Packet packet = new Packet() {
            @Override
            public void write(PacketBuffer packetBuffer) throws IOException {
                packetBuffer.writeIntLE(600);
                packetBuffer.writeString(command);
            }

            @Override
            public void handle(PacketBuffer packetBuffer) throws IOException {

            }

            @Override
            public void process(Channel channel) throws IOException {

            }
        };

        channel.writeAndFlush(packet);
    }

}
