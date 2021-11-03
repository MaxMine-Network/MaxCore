package ru.maxmine.core.game;

import ru.maxmine.core.MaxMineCore;
import ru.maxmine.core.api.inventory.Inventory;
import ru.maxmine.core.api.items.Item;
import ru.maxmine.core.api.items.Material;
import ru.maxmine.core.types.Player;
import ru.maxmine.core.types.Server;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GameMenu extends Inventory {

    private String branch;

    public GameMenu(String branch) {
        super("Выбор серверов", 6);

        this.branch = branch;
    }

    @Override
    public void create(Player player) {
        Item item = new Item(Material.EYE_OF_ENDER);
        item.setDisplayName("§cРандомный сервер");
        item.setLore("",
                "§7Жми, чтобы телепортироваться",
                "§7на случайную игру"
        );

        addButton(item, 10, p -> {
            Server server = MaxMineCore.getServers().values().stream()
                    .filter(srv -> srv.getStatus() == 0)
                    .filter(srv -> srv.getName().contains(branch))
                    .max(Comparator.comparing(Server::getOnline))
                    .orElse(null);

            if(server != null) p.redirect(server.getName());
            else p.sendMessage("§f[§cMaxMine Core§f] Сервера не найдены");
        });

        List<Server> servers = MaxMineCore.getServers().values().stream()
                .filter(srv -> srv.getStatus() == 0)
                .filter(srv -> srv.getName().contains(branch))
                .collect(Collectors.toList());

        int slot = 12;

        Map<String, Integer> maps = new HashMap<>();
        Map<String, Integer> onlines = new HashMap<>();

        for (Server server : servers) {
            String map = server.getMap();

            if(map == null || map.equalsIgnoreCase("null")) continue;

            maps.put(map, maps.getOrDefault(map, 0) + 1);
            onlines.put(map, onlines.getOrDefault(map, 0) + server.getOnline());
        }

        for (String map : maps.keySet()) {
            int count = maps.get(map);
            int online = onlines.get(map);

            Item sItem = new Item(Material.EMPTY_MAP);
            sItem.setDisplayName("§c" + map);
            sItem.setLore(
                    "",
                    "§7Свободно серверов: §a" + count,
                    "§7Онлайн: §a" + online,
                    "",
                    "§7Жми, чтобы войти в игру"
            );

            addButton(sItem, slot, p -> {
                Server s = MaxMineCore.getServers().values().stream()
                        .filter(srv -> srv.getStatus() == 0)
                        .filter(srv -> srv.getMap() != null)
                        .filter(srv -> srv.getName().contains(branch))
                        .filter(srv -> srv.getMap().equals(map))
                        .max(Comparator.comparing(Server::getOnline))
                        .orElse(null);

                if(s != null) p.redirect(s.getName());
                else open(p);
            });

            if(slot == 15 || slot == 24 || slot == 33 || slot == 42) slot += 6;
            else slot++;
        }

        /* for (Server server : servers) {
            Item sItem = new Item(Material.MAGMA_CREAM);
            sItem.setDisplayName("§c" + server.getName());
            sItem.setLore(
                    "",
                    "§fКарта: §c" + server.getMap(),
                    "§fОнлайн: §c" + server.getOnline()
            );

            addButton(sItem, slot, p -> p.redirect(server.getName()));

            if(slot == 15 || slot == 24 || slot == 33 || slot == 42) slot += 6;
            else slot++;
        } */
    }
}
