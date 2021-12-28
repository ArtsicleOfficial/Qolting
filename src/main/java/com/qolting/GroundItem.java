package com.qolting;

import net.runelite.api.Tile;

public class GroundItem {
    public int id;
    public int quantity;

    public Tile tile;

    public GroundItem(int id, int quantity) {
        this.id = id;
        this.quantity = quantity;
    }

    public GroundItem(int id, int quantity, Tile tile) {
        this.id = id;
        this.quantity = quantity;
        this.tile = tile;
    }

    public GroundItem(String str) {
        fromString(str);
    }

    @Override
    public String toString() {
        return id + "=" + quantity;
    }

    public void fromString(String str) {
        String[] split = str.split("=");
        this.id = Integer.parseInt(split[0]);
        this.quantity = Integer.parseInt(split[1]);
    }
}
