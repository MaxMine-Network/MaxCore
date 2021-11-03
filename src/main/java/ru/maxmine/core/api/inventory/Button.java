package ru.maxmine.core.api.inventory;

import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.maxmine.core.api.items.Item;

@AllArgsConstructor
@Getter
public class Button {

    private Item item;
    private int slot;
    private ButtonClickable clickable;

}
