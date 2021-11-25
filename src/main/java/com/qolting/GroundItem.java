package com.qolting;

public class GroundItem {
    public int id;
    public int quantity;
    public GroundItem(int id, int quantity) {
        this.id = id;
        this.quantity = quantity;
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
