package ru.maxmine.core.types;

import io.netty.channel.Channel;
import lombok.Getter;
import lombok.Setter;
import ru.maxmine.core.MaxMineCore;
import ru.maxmine.core.api.events.PlayerRedirectEvent;
import ru.maxmine.core.api.groups.Group;
import ru.maxmine.core.api.groups.GroupManager;
import ru.maxmine.core.api.inventory.Inventory;
import ru.maxmine.core.buffer.PacketBuffer;
import ru.maxmine.core.packets.Packet;

import java.io.IOException;
import java.util.Map;

@Getter
@Setter
public class Player {

    private String name;
    private Proxy proxy;
    private String server;
    private Group group;
    private String ip;
    private Inventory inventory;

    private final boolean staff;

    public Player(String name) {
        this.name = name;
        this.group = GroupManager.getManager().getPlayerGroup(name);
        this.staff = group.getLevel() > 50;

        MaxMineCore.getPlayers().put(name.toLowerCase(), this);
        MaxMineCore.getInstance().getLogger().info("Player " + name + " connected to Core");
    }

    public Server getServer() {
        return MaxMineCore.getServer(server);
    }

    public static Map<String, Player> getPlayers() {
        return MaxMineCore.getPlayers();
    }

    public static Player getPlayer(String name) {
        return MaxMineCore.getPlayer(name);
    }

    public void sendTitle(String title, String subtitle) {
        Packet packet = new Packet() {
            @Override
            public void write(PacketBuffer packetBuffer) throws IOException {
                packetBuffer.writeIntLE(111);
                packetBuffer.writeString(name);
                packetBuffer.writeString(title);
                packetBuffer.writeString(subtitle);
            }

            @Override
            public void handle(PacketBuffer packetBuffer) throws IOException {

            }

            @Override
            public void process(Channel channel) throws IOException {

            }
        };

        proxy.getChannel().writeAndFlush(packet);
    }

    public void kick(String reason) {
        Packet packet = new Packet() {
            @Override
            public void write(PacketBuffer packetBuffer) throws IOException {
                packetBuffer.writeIntLE(107);
                packetBuffer.writeString(name);
                packetBuffer.writeString(reason);
            }

            @Override
            public void handle(PacketBuffer packetBuffer) throws IOException {

            }

            @Override
            public void process(Channel channel) throws IOException {

            }
        };

        proxy.getChannel().writeAndFlush(packet);
    }

    public void redirect(String server) {
        PlayerRedirectEvent event = new PlayerRedirectEvent(this, server);
        MaxMineCore.getInstance().getPluginManager().callEvent(event);

        Packet packet = new Packet() {
            @Override
            public void write(PacketBuffer packetBuffer) throws IOException {
                packetBuffer.writeIntLE(105);
                packetBuffer.writeString(name);
                packetBuffer.writeString(event.getServer());
            }

            @Override
            public void handle(PacketBuffer packetBuffer) throws IOException {

            }

            @Override
            public void process(Channel channel) throws IOException {

            }
        };

        proxy.getChannel().writeAndFlush(packet);
    }

    public void sendMessage(String message) {
        Packet packet = new Packet() {
            @Override
            public void write(PacketBuffer packetBuffer) throws IOException {
                packetBuffer.writeIntLE(104);
                packetBuffer.writeString(name);
                packetBuffer.writeString(message);
            }

            @Override
            public void handle(PacketBuffer packetBuffer) throws IOException {

            }

            @Override
            public void process(Channel channel) throws IOException {

            }
        };

        proxy.getChannel().writeAndFlush(packet);
    }

    public void sendAchievemt(String background, String icon, String title, String desc) {
        Packet packet = new Packet() {
            @Override
            public void write(PacketBuffer packetBuffer) throws IOException {
                packetBuffer.writeIntLE(1506);
                packetBuffer.writeString(name);
                packetBuffer.writeString(background);
                packetBuffer.writeString(icon);
                packetBuffer.writeString(title);
                packetBuffer.writeString(desc);
            }

            @Override
            public void handle(PacketBuffer packetBuffer) throws IOException {

            }

            @Override
            public void process(Channel channel) throws IOException {

            }
        };

        Server server = getServer();

        if(server != null)
            server.getChannel().writeAndFlush(packet);
    }

}
