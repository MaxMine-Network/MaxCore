package ru.maxmine.core.packets.player;

import io.netty.channel.Channel;
import lombok.Getter;
import ru.maxmine.core.MaxMineCore;
import ru.maxmine.core.api.events.PlayerChatEvent;
import ru.maxmine.core.buffer.PacketBuffer;
import ru.maxmine.core.packets.Packet;

import java.io.IOException;

@Getter
public class PlayerChatPacket extends Packet {

    private String player, message;

    private boolean canceled;

    public PlayerChatPacket() {}

    @Override
    public void write(PacketBuffer packetBuffer) throws IOException {
        packetBuffer.writeIntLE(108);
        packetBuffer.writeString(player);
        packetBuffer.writeBoolean(canceled);
        packetBuffer.writeString(message);
    }

    @Override
    public void handle(PacketBuffer packetBuffer) throws IOException {
        this.player = packetBuffer.readString(16);
        this.message = packetBuffer.readString(32767);
    }

    @Override
    public void process(Channel channel) throws IOException {
        PlayerChatEvent event = new PlayerChatEvent(player, message);
        MaxMineCore.getInstance().getPluginManager().callEvent(event);

        this.message = event.getMessage();
        this.canceled = event.isCancelled();

        channel.writeAndFlush(this);
    }
}
