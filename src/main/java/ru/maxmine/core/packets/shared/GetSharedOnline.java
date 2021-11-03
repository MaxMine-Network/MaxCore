package ru.maxmine.core.packets.shared;

import io.netty.channel.Channel;
import ru.maxmine.core.MaxMineCore;
import ru.maxmine.core.buffer.PacketBuffer;
import ru.maxmine.core.packets.Packet;

import java.io.IOException;

public class GetSharedOnline extends Packet {

    private int online;

    @Override
    public void write(PacketBuffer packetBuffer) throws IOException {
        packetBuffer.writeIntLE(1234);
        packetBuffer.writeIntLE(online);
    }

    @Override
    public void handle(PacketBuffer packetBuffer) throws IOException {

    }

    @Override
    public void process(Channel channel) throws IOException {
        online = MaxMineCore.getInstance().getOnline();

        channel.writeAndFlush(this);
    }
}
