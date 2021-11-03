package ru.maxmine.core.packets.server;

import io.netty.channel.Channel;
import ru.maxmine.core.MaxMineCore;
import ru.maxmine.core.buffer.PacketBuffer;
import ru.maxmine.core.packets.Packet;
import ru.maxmine.core.types.Player;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class ServerDisconnectPacket extends Packet {

    private String server;

    @Override
    public void write(PacketBuffer packetBuffer) throws IOException {

    }

    @Override
    public void handle(PacketBuffer packetBuffer) throws IOException {
        this.server = packetBuffer.readString(48);
    }

    @Override
    public void process(Channel channel) throws IOException {
        List<Player> players = Player.getPlayers().values().stream()
                .filter(player -> player.getServer().getName().equalsIgnoreCase(server))
                .collect(Collectors.toList());

        players.forEach(player -> {
            player.sendMessage("§сСервер, на котором вы находились, был выключен. Вы перемещены в лимбо.");
            player.sendMessage("§сДля выхода напишите /hub");
        });

        MaxMineCore.getInstance().removeServer(this.server);
        MaxMineCore.getInstance().getLogger().info("Server " + this.server + " disconnected from core");
    }
}
