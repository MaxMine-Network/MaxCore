package ru.maxmine.core.api.groups;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class Group {
    private String name, prefix, suffix;
    private int level;
}
