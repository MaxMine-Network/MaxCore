package ru.maxmine.core.packets.player;

import io.netty.channel.Channel;
import ru.maxmine.core.MaxMineCore;
import ru.maxmine.core.buffer.PacketBuffer;
import ru.maxmine.core.packets.Packet;
import ru.maxmine.core.types.Player;
import ru.maxmine.core.types.Server;

import java.io.IOException;
import java.util.Comparator;

public class PlayerBranchRedirectPacket extends Packet {

    private String player;
    private String branch;

    @Override
    public void write(PacketBuffer packetBuffer) throws IOException {

    }

    @Override
    public void handle(PacketBuffer packetBuffer) throws IOException {
        this.player = packetBuffer.readString(16);
        this.branch = packetBuffer.readString(32);
    }

    @Override
    public void process(Channel channel) throws IOException {
        Server server = MaxMineCore.getServers().values().stream()
                .filter(srv -> srv.getStatus() == 0)
                .filter(srv -> srv.getName().contains(branch))
                .max(Comparator.comparing(Server::getOnline))
                .orElse(MaxMineCore.getServer("Login-1"));

        Player p = Player.getPlayer(player);

        p.redirect(server.getName());
    }
}
