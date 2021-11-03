package ru.maxmine.core.packets.server;

import io.netty.channel.Channel;
import ru.maxmine.core.MaxMineCore;
import ru.maxmine.core.buffer.PacketBuffer;
import ru.maxmine.core.packets.Packet;
import ru.maxmine.core.types.Server;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ServerGetBranchOnline extends Packet {

    private String server, branch;

    private int online = 0;

    @Override
    public void write(PacketBuffer packetBuffer) throws IOException {
        packetBuffer.writeIntLE(153);
        packetBuffer.writeIntLE(online);
    }

    @Override
    public void handle(PacketBuffer packetBuffer) throws IOException {
        this.branch = packetBuffer.readString(16);
    }

    @Override
    public void process(Channel channel) throws IOException {
        List<Server> serverList = MaxMineCore.getServers().values().stream()
                .filter(srv -> srv.getName().contains(branch))
                .collect(Collectors.toList());

        for (Server s : serverList) {
            online += s.getOnline();
        }

        channel.writeAndFlush(this);
    }
}
