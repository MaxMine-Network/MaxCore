package ru.maxmine.core.packets.proxy;

import io.netty.channel.Channel;
import ru.maxmine.core.buffer.PacketBuffer;
import ru.maxmine.core.packets.Packet;
import ru.maxmine.core.types.Proxy;

import java.io.IOException;

public class ProxyOnlinePacket extends Packet {

    private String proxy;
    private int online;

    @Override
    public void write(PacketBuffer packetBuffer) throws IOException {

    }

    @Override
    public void handle(PacketBuffer packetBuffer) throws IOException {
        this.proxy = packetBuffer.readString(48);
        this.online = packetBuffer.readIntLE();
    }

    @Override
    public void process(Channel channel) throws IOException {
        Proxy.getProxys().get(proxy).setOnline(online);
    }
}
