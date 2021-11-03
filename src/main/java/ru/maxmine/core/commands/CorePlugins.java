package ru.maxmine.core.commands;

import ru.maxmine.core.MaxMineCore;
import ru.maxmine.core.api.Command;
import ru.maxmine.core.api.plugin.Plugin;
import ru.maxmine.core.types.Player;

public class CorePlugins extends Command {
    public CorePlugins() {
        super("coreplugins");
    }

    @Override
    public void execute(Player player, String[] args) {
        if(player.getGroup().getLevel() < 100) {
            player.sendMessage("§f[§cMaxMine§f] Данная команда Вам недоступна");
            return;
        }

        if(args.length == 0) {
            player.sendMessage("§f[§cMaxMine Core§f] Подкоманды:");
            player.sendMessage("Список плагинов - §c/coreplugins list");
            player.sendMessage("Переключить плагин - §c/coreplugins toggle [имя]");
            return;
        }

        String action = args[0];

        switch (action) {
            case "list": {
                player.sendMessage("§f[§cMaxMine Core§f] Список плагинов:");

                for (Plugin plugin : MaxMineCore.getInstance().getPluginManager().getPlugins()) {
                    player.sendMessage("- §c" + plugin.getDescription().getName());
                }

                break;
            }

            case "toggle": {
                break;
            }
        }

    }
}
