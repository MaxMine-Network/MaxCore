package ru.maxmine.core.packets.server;

import io.netty.channel.Channel;
import ru.maxmine.core.MaxMineCore;
import ru.maxmine.core.buffer.PacketBuffer;
import ru.maxmine.core.packets.Packet;
import ru.maxmine.core.types.Server;

import java.io.IOException;

public class ServerOnlinePacket extends Packet {

    private String server;
    private int online;

    @Override
    public void write(PacketBuffer packetBuffer) throws IOException {

    }

    @Override
    public void handle(PacketBuffer packetBuffer) throws IOException {
        this.server = packetBuffer.readString(48);
        this.online = packetBuffer.readIntLE();
    }

    @Override
    public void process(Channel channel) throws IOException {
        Server server = MaxMineCore.getServer(this.server);

        if(server != null)
            server.setOnline(online);
    }
}
