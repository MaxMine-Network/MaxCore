package ru.maxmine.core.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import ru.maxmine.core.MaxMineCore;
import ru.maxmine.core.buffer.PacketBuffer;
import ru.maxmine.core.packets.Packet;
import ru.maxmine.core.packets.PacketManager;

import java.util.List;

public class Decoder extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        if(byteBuf.readableBytes() != 0) {
            PacketBuffer packetBuffer = new PacketBuffer(byteBuf);

            int packetID = packetBuffer.readIntLE();

            if(PacketManager.hasPacket(packetID) && packetID >= 0) {
                Packet packet = PacketManager.getPacket(packetID);

                packet.handle(packetBuffer);

                list.add(packet);
            } else {
                MaxMineCore.getInstance().getLogger().info("Packet " + packetID + " invalid");
            }
        }

    }
}