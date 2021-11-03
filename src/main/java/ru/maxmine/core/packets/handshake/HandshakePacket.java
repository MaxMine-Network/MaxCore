package ru.maxmine.core.packets.handshake;

import io.netty.channel.Channel;
import ru.maxmine.core.buffer.PacketBuffer;
import ru.maxmine.core.packets.Packet;
import ru.maxmine.core.types.Proxy;
import ru.maxmine.core.types.Server;

import java.io.IOException;

public class HandshakePacket extends Packet {

    private PacketBuffer buffer;

    @Override
    public void write(PacketBuffer packetBuffer) throws IOException {

    }

    @Override
    public void handle(PacketBuffer packetBuffer) throws IOException {
        this.buffer = packetBuffer;
    }

    @Override
    public void process(Channel channel) throws IOException {
        int type = buffer.readIntLE();

        switch (type) {
            case 0:
                String name = buffer.readString(48);
                new Proxy(channel, name);
                break;

            case 1:
                String serverName = buffer.readString(48);
                String ip = buffer.readString(128);
                int port = buffer.readIntLE();

                new Server(channel, serverName, ip, port);
                break;

            default:
                System.out.println("Undefined handshake type - " + type);
        }
    }
}
