package ru.maxmine.core.packets.game;

import io.netty.channel.Channel;
import ru.maxmine.core.buffer.PacketBuffer;
import ru.maxmine.core.game.GameMenu;
import ru.maxmine.core.packets.Packet;
import ru.maxmine.core.types.Player;

import java.io.IOException;

public class ServerSelectorPacket extends Packet {

    private String player, branch;

    @Override
    public void write(PacketBuffer packetBuffer) throws IOException {

    }

    @Override
    public void handle(PacketBuffer packetBuffer) throws IOException {
        this.player = packetBuffer.readString(16);
        this.branch = packetBuffer.readString(48);
    }

    @Override
    public void process(Channel channel) throws IOException {
        Player player = Player.getPlayer(this.player);

        if(player != null)
            new GameMenu(branch).open(player);
    }
}
