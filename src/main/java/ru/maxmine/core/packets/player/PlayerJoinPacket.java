package ru.maxmine.core.packets.player;

import io.netty.channel.Channel;
import ru.maxmine.core.MaxMineCore;
import ru.maxmine.core.api.events.PlayerJoinEvent;
import ru.maxmine.core.buffer.PacketBuffer;
import ru.maxmine.core.packets.Packet;
import ru.maxmine.core.types.Player;
import ru.maxmine.core.types.Proxy;

import java.io.IOException;

public class PlayerJoinPacket extends Packet {

    private String proxy;
    private String name;
    private String ip;

    @Override
    public void write(PacketBuffer packetBuffer) throws IOException {

    }

    @Override
    public void handle(PacketBuffer packetBuffer) throws IOException {
        this.proxy = packetBuffer.readString(48);
        this.name = packetBuffer.readString(16);
        this.ip = packetBuffer.readString(128);
    }

    @Override
    public void process(Channel channel) throws IOException {
        Player player = new Player(name);

        player.setProxy(Proxy.getProxys().get(proxy));
        player.setIp(ip);

        PlayerJoinEvent event = new PlayerJoinEvent(player);
        MaxMineCore.getInstance().getPluginManager().callEvent(event);
    }
}
