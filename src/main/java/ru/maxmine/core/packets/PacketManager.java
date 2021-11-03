package ru.maxmine.core.packets;

import ru.maxmine.core.MaxMineCore;
import ru.maxmine.core.packets.commands.ExecuteCommandPacket;
import ru.maxmine.core.packets.game.ServerSelectorPacket;
import ru.maxmine.core.packets.handshake.HandshakePacket;
import ru.maxmine.core.packets.inventory.InventoryClickPacket;
import ru.maxmine.core.packets.player.*;
import ru.maxmine.core.packets.proxy.ProxyOnlinePacket;
import ru.maxmine.core.packets.server.*;
import ru.maxmine.core.packets.shared.GetSharedOnline;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

public class PacketManager {

    private static Map<Integer, Class<? extends Packet>> packets = new HashMap<>();

    static {
        packets.put(0, HandshakePacket.class);
        packets.put(50, ProxyOnlinePacket.class);
        packets.put(99, PlayerLoginPacket.class);
        packets.put(100, PlayerChangeServer.class);
        packets.put(101, PlayerJoinPacket.class);
        packets.put(102, PlayerRedirectPacket.class);
        packets.put(103, PlayerBranchRedirectPacket.class);
        packets.put(106, PlayerQuitPacket.class);
        packets.put(108, PlayerChatPacket.class);
        packets.put(120, InventoryClickPacket.class);
        packets.put(150, ServerMapPacket.class);
        packets.put(151, ServerOnlinePacket.class);
        packets.put(152, ServerStatusPacket.class);
        packets.put(153, ServerGetBranchOnline.class);
        packets.put(155, ServerDisconnectPacket.class);
        packets.put(601, ExecuteCommandPacket.class);
        packets.put(1234, GetSharedOnline.class);
        packets.put(1700, ServerSelectorPacket.class);
    }

    public static void registerPacket(int id, Class<? extends Packet> clazz) {
        packets.put(id, clazz);
    }

    public static boolean hasPacket(int id) {
        return packets.containsKey(id);
    }

    public static Packet getPacket(int id) {
        try {
            return packets.get(id).newInstance();
        } catch (Exception e) {
            MaxMineCore.getInstance().getLogger().log(Level.WARNING, "Пакета {0} не существует", id);
            return null;
        }
    }

}
