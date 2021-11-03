package ru.maxmine.core.api.groups;

import lombok.Getter;
import ru.maxmine.core.MaxMineCore;
import ru.maxmine.core.database.MySQL;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GroupManager {

    @Getter
    private static GroupManager manager;

    private Group DEFAULT;

    private final MySQL database = MaxMineCore.getDatabase();
    private final Map<String, Group> groups = new HashMap<>();

    public GroupManager() {
        manager = this;
        database.executeQuery("SELECT * FROM `Groups`.`Groups`", rs -> {

            while (rs.next()) {
                String group = rs.getString("Name");
                String prefix = rs.getString("Prefix").replace("&", "§");
                String suffix = rs.getString("Suffix").replace("&", "§");
                int level = rs.getInt("Level");

                Group g = new Group(group, prefix, suffix, level);

                groups.put(group, g);
            }

            return Void.TYPE;
        });

        DEFAULT = groups.get("default");
    }

    public Group getPlayerGroup(String player) {
        return database.executeQuery("SELECT * FROM `Groups`.`Users` WHERE `Name` = ?", rs -> {

            if(rs.next()) {
                return groups.get(rs.getString("Group"));
            }

            return DEFAULT;
        }, player.toLowerCase());
    }

    public Group getGroupByLevel(int level) {
        return getGroupsByLevel(level).get(0);
    }

    public List<Group> getGroupsByLevel(int level) {
        return groups.values().stream()
                .filter(group -> group.getLevel() >= level)
                .collect(Collectors.toList());
    }
}
