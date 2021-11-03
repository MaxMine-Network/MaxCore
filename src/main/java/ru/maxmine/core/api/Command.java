//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package ru.maxmine.core.api;

import ru.maxmine.core.MaxMineCore;
import ru.maxmine.core.types.Player;

public abstract class Command {
    private final String name;

    public String getName() {
        return this.name;
    }

    public Command(String name) {
        this.name = name;
        MaxMineCore.getInstance().registerCommand(name.toLowerCase(), this);
    }

    public Command(String name, String... aliases) {
        this.name = name;
        MaxMineCore.getInstance().registerCommand(name.toLowerCase(), this);

        for (String s : aliases) {
            if(!s.equals(""))
                MaxMineCore.getInstance().registerCommand(s.toLowerCase(), this);
        }

    }

    public abstract void execute(Player player, String[] args);
}
