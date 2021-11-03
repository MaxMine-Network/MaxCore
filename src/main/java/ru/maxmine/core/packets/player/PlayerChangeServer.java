package ru.maxmine.core.packets.player;

import io.netty.channel.Channel;
import ru.maxmine.core.buffer.PacketBuffer;
import ru.maxmine.core.packets.Packet;
import ru.maxmine.core.types.Player;

import java.io.IOException;

public class PlayerChangeServer extends Packet {

    private String player;
    private String server;

    @Override
    public void write(PacketBuffer packetBuffer) throws IOException {

    }

    @Override
    public void handle(PacketBuffer packetBuffer) throws IOException {
        this.player = packetBuffer.readString(16).toLowerCase();
        this.server = packetBuffer.readString(48);
    }

    @Override
    public void process(Channel channel) throws IOException {
        Player player = Player.getPlayer(this.player);

        if(player != null)
            player.setServer(this.server);
    }
}
