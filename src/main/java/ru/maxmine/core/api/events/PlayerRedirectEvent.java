//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package ru.maxmine.core.api.events;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import ru.maxmine.core.api.plugin.Event;
import ru.maxmine.core.types.Player;

@Getter
@Setter
@AllArgsConstructor
public class PlayerRedirectEvent extends Event {
    private Player player;
    private String server;
}
