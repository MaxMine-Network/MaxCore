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
public class PlayerLoginEvent extends Event {
    private String cancelReason = "Причина не указана";
    private String name;
    private boolean cancelled;

    public PlayerLoginEvent(String name) {
        this.name = name;
    }
}
