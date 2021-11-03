package ru.maxmine.core.api.inventory;

import com.google.gson.Gson;
import io.netty.channel.Channel;
import lombok.Getter;
import ru.maxmine.core.api.items.Item;
import ru.maxmine.core.buffer.PacketBuffer;
import ru.maxmine.core.packets.Packet;
import ru.maxmine.core.types.Player;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Getter
public abstract class Inventory {

    private final Gson GSON = new Gson();

    private String name;
    private int slots = 9;

    private Map<Integer, Button> buttons = new HashMap<>();

    public Inventory(String name) {
        this.name = name;
    }

    public Inventory(String name, int slots) {
        this(name);
        this.slots = slots;
    }

    public abstract void create(Player player);

    public void open(Player player) {
        this.buttons.clear();
        create(player);
        player.getServer().getChannel().writeAndFlush(getPacket(player));
        player.setInventory(this);
    }

    public void addButton(Item item, int slot, ButtonClickable clickable) {
        Button button = new Button(item, slot, clickable);

        this.buttons.put(slot, button);
    }

    private Packet getPacket(Player player) {
        return new Packet() {
            @Override
            public void write(PacketBuffer packetBuffer) throws IOException {
                packetBuffer.writeIntLE(110);
                packetBuffer.writeString(name);
                packetBuffer.writeIntLE(slots);
                packetBuffer.writeIntLE(buttons.size());
                packetBuffer.writeString(player.getName());

                for (Button button : buttons.values()) {
                    String json = GSON.toJson(button.getItem());

                    packetBuffer.writeIntLE(button.getSlot());
                    packetBuffer.writeIntLE(json.length());
                    packetBuffer.writeString(json);
                }
            }

            @Override
            public void handle(PacketBuffer packetBuffer) throws IOException {

            }

            @Override
            public void process(Channel channel) throws IOException {

            }
        };
    }

    public void onClick(Player player, int slot) {
        if(!buttons.containsKey(slot))
            return;

        Button button = buttons.get(slot);

        if(button != null && button.getClickable() != null) {
            button.getClickable().onClick(player);
        }
    }
}
