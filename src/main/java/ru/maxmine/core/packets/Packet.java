package ru.maxmine.core.packets;

import io.netty.channel.Channel;
import ru.maxmine.core.buffer.PacketBuffer;

import java.io.IOException;

public abstract class Packet {

    public abstract void write(PacketBuffer packetBuffer) throws IOException;

    public abstract void handle(PacketBuffer packetBuffer) throws IOException;

    public abstract void process(Channel channel) throws IOException;
}
