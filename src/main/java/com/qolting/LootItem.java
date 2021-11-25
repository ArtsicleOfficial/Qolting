package com.qolting;

public class LootItem {
    public String name;
    public int value;
    public int quantity;

    public LootItem(String name, int value, int quantity) {
        this.name = name;
        this.value = value;
        this.quantity = quantity;
    }

    public LootItem(GroundItem item, QoltingPlugin plugin) {
        this.name = plugin.getItemName(item.id);
        this.value = plugin.getItemPrice(item.id);
        this.quantity = item.quantity;
    }

    public LootItem(String dataStr) {
        fromString(dataStr);
    }

    @Override
    public String toString() {
        return value + "=" + name + "=" + quantity;
    }

    public void fromString(String str) {
        String[] split = str.split("=");
        this.value = Integer.parseInt(split[0]);
        this.name = split[1];
        this.quantity = Integer.parseInt(split[2]);
    }
}
