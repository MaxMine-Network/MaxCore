//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package ru.maxmine.core.api.events;

import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.maxmine.core.api.plugin.Event;
import ru.maxmine.core.types.Player;

@AllArgsConstructor
@Getter
public class PlayerQuitEvent extends Event {
    private Player player;
}
