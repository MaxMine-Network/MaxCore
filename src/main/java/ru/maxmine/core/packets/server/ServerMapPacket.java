package ru.maxmine.core.packets.server;

import io.netty.channel.Channel;
import ru.maxmine.core.MaxMineCore;
import ru.maxmine.core.buffer.PacketBuffer;
import ru.maxmine.core.packets.Packet;
import ru.maxmine.core.types.Server;

import java.io.IOException;

public class ServerMapPacket extends Packet {

    private String server;
    private String map;

    @Override
    public void write(PacketBuffer packetBuffer) throws IOException {

    }

    @Override
    public void handle(PacketBuffer packetBuffer) throws IOException {
        this.server = packetBuffer.readString(48);
        this.map = packetBuffer.readString(128);
    }

    @Override
    public void process(Channel channel) throws IOException {
        Server server = MaxMineCore.getServer(this.server);

        if(server != null)
            server.setMap(map);
    }
}
