package ru.maxmine.core.api.items;

import lombok.Getter;
import lombok.Setter;

import java.util.*;

@Getter
@Setter
public class Item {

    private Material material;
    private String displayName, headKey;
    private short data;
    private int amount = 1;
    private boolean glowing;

    private List<String> lore = new ArrayList<>();
    private final Map<String, Integer> enchantments = new HashMap<>();

    public Item(Material material) {
        this.material = material;
    }

    public Item(Material material, int amount) {
        this(material);
        this.amount = 1;
    }

    public Item(Material material, short data) {
        this(material);
        this.data = data;
    }

    public Item(Material material, String headKey) {
        this.material = material;
        this.headKey = headKey;
    }

    public void setLore(String... lores) {
        this.lore = Arrays.asList(lores);
    }

    public void addEnchantment(Enchantment enchantment) {
        enchantments.put(enchantment.name(), 1);
    }

    public void addEnchantment(Enchantment enchantment, int level) {
        enchantments.put(enchantment.name(), level);
    }

}
