//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package ru.maxmine.core.api.events;

import lombok.Getter;
import lombok.Setter;
import ru.maxmine.core.api.plugin.Event;

@Getter
@Setter
public class PlayerChatEvent extends Event {
    private String message;
    private String name;
    private boolean cancelled;

    public PlayerChatEvent(String name, String message) {
        this.name = name;
        this.message = message;
    }
}
