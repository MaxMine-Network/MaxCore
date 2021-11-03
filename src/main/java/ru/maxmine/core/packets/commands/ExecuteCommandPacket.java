package ru.maxmine.core.packets.commands;

import io.netty.channel.Channel;
import ru.maxmine.core.MaxMineCore;
import ru.maxmine.core.api.events.CommandEvent;
import ru.maxmine.core.buffer.PacketBuffer;
import ru.maxmine.core.packets.Packet;
import ru.maxmine.core.types.Player;

public class ExecuteCommandPacket extends Packet {

    private String player, command;
    private String[] args;

    @Override
    public void write(PacketBuffer packetBuffer) {

    }

    @Override
    public void handle(PacketBuffer packetBuffer) {
        this.player = packetBuffer.readString(16);
        this.command = packetBuffer.readString(256);
        int length = packetBuffer.readIntLE();
        this.args = new String[length];

        for (int i = 0; i < length; i++) {
            args[i] = packetBuffer.readString(256);
        }
    }

    @Override
    public void process(Channel channel) {
        Player p = Player.getPlayer(player);

        CommandEvent commandEvent = new CommandEvent(p, command, args);
        MaxMineCore.getInstance().getPluginManager().callEvent(commandEvent);

        MaxMineCore.getInstance().executeCommand(p, command, args);
    }
}
