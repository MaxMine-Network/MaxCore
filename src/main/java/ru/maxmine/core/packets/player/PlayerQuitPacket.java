package ru.maxmine.core.packets.player;

import io.netty.channel.Channel;
import ru.maxmine.core.MaxMineCore;
import ru.maxmine.core.api.events.PlayerQuitEvent;
import ru.maxmine.core.buffer.PacketBuffer;
import ru.maxmine.core.packets.Packet;
import ru.maxmine.core.types.Player;

import java.io.IOException;

public class PlayerQuitPacket extends Packet {

    private String player;

    @Override
    public void write(PacketBuffer packetBuffer) throws IOException {

    }

    @Override
    public void handle(PacketBuffer packetBuffer) throws IOException {
        this.player = packetBuffer.readString(16);
    }

    @Override
    public void process(Channel channel) throws IOException {
        Player player = Player.getPlayer(this.player);

        if(player != null) {
            PlayerQuitEvent event = new PlayerQuitEvent(player);
            MaxMineCore.getInstance().getPluginManager().callEvent(event);

            Player.getPlayers().remove(player.getName().toLowerCase());

            MaxMineCore.getInstance().getLogger().info("Player " + this.player + " disconnected");
        }
    }
}
