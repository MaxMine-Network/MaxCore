package ru.maxmine.core.packets.player;

import io.netty.channel.Channel;
import lombok.Getter;
import lombok.Setter;
import ru.maxmine.core.MaxMineCore;
import ru.maxmine.core.api.events.PlayerLoginEvent;
import ru.maxmine.core.buffer.PacketBuffer;
import ru.maxmine.core.packets.Packet;

import java.io.IOException;

@Getter
public class PlayerLoginPacket extends Packet {

    private String name;

    @Setter
    private boolean cancelled;

    @Setter
    private String reason;

    @Override
    public void write(PacketBuffer packetBuffer) throws IOException {
        packetBuffer.writeIntLE(99);
        packetBuffer.writeString(name);
        packetBuffer.writeBoolean(cancelled);
        packetBuffer.writeString(reason);
    }

    @Override
    public void handle(PacketBuffer packetBuffer) throws IOException {
        this.name = packetBuffer.readString(16);
    }

    @Override
    public void process(Channel channel) throws IOException {
        PlayerLoginEvent playerLoginEvent = new PlayerLoginEvent(name);
        MaxMineCore.getInstance().getPluginManager().callEvent(playerLoginEvent);

        this.cancelled = playerLoginEvent.isCancelled();
        this.reason = playerLoginEvent.getCancelReason();

        channel.writeAndFlush(this);
    }
}
