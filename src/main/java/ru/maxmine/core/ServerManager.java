package ru.maxmine.core;

import ru.maxmine.core.types.Server;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ServerManager {

    @SafeVarargs
    public static Stream<Server> findCustom(Predicate<? super Server>... predicates) {
        Stream<Server> stream = MaxMineCore.getServers().values().stream();

        for (Predicate<? super Server> predicate : predicates)
            stream = stream.filter(predicate);

        return stream;
    }

    public static List<Server> findAllGameServers() {
        return findCustom(server -> server.getMap() != null).collect(Collectors.toList());
    }

    public static List<Server> findByMap(String map, Predicate<? super Server>... predicates) {
        Stream<Server> stream = findCustom(server -> server.getMap() != null && server.getMap().equalsIgnoreCase(map));

        for (Predicate<? super Server> predicate : predicates) {
            stream = stream.filter(predicate);
        }

        return stream.collect(Collectors.toList());
    }

    public static List<Server> findByName(String name, Predicate<? super Server>... predicates) {
        Stream<Server> stream = findCustom(server -> server.getName().equalsIgnoreCase(name));

        for (Predicate<? super Server> predicate : predicates) {
            stream = stream.filter(predicate);
        }

        return stream.collect(Collectors.toList());
    }

    public static List<Server> findFreeServers(String map) {
        return findByMap(map, server -> server.getStatus() == 0);
    }

    public static int getBranchOnline(String branch) {
        int online = 0;

        for (Server server : findCustom(server -> server.getName().startsWith(branch)).collect(Collectors.toList()))
            online += server.getOnline();

        return online;
    }

}
