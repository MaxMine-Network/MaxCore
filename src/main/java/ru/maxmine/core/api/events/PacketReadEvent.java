//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package ru.maxmine.core.api.events;

import com.google.common.io.ByteArrayDataInput;
import io.netty.channel.ChannelHandlerContext;
import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.maxmine.core.api.plugin.Event;

@Getter
@AllArgsConstructor
public class PacketReadEvent extends Event {
    private int packetID;
    private ChannelHandlerContext ctx;
    private ByteArrayDataInput in;
}
