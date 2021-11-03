package ru.maxmine.core.packets.inventory;

import io.netty.channel.Channel;
import ru.maxmine.core.buffer.PacketBuffer;
import ru.maxmine.core.packets.Packet;
import ru.maxmine.core.types.Player;

public class InventoryClickPacket extends Packet {

    private String playerName;
    private int slot;

    @Override
    public void write(PacketBuffer packetBuffer) {

    }

    @Override
    public void handle(PacketBuffer packetBuffer) {
        this.playerName = packetBuffer.readString(16);
        this.slot = packetBuffer.readIntLE();
    }

    @Override
    public void process(Channel channel) {
        Player player = Player.getPlayer(playerName.toLowerCase());

        if(player != null && player.getInventory() != null) {
            player.getInventory().onClick(player, slot);
        }
    }
}
