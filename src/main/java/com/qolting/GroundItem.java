package com.qolting;

import net.runelite.api.coords.WorldPoint;

public class GroundItem {
    public int id;
    public int quantity;

    public WorldPoint worldPoint;

    // NOTE: This is a hack so we can remove items from the list after 5 minutes.
    // I presume that "onDespawned" is not always fully reliable (imagine we're away from the area;
    // would an event get sent? presumably not).
    // So, I can just remove them after 5 minutes
    int addedAtGameCycle;
    static int MILLISECONDS_PER_GAME_CYCLE = 20;
    static int GAME_CYCLES_BEFORE_REMOVAL = (60*5*1000) / MILLISECONDS_PER_GAME_CYCLE;

    public GroundItem(int id, int quantity, WorldPoint worldPoint, int addedAtGameCycle) {
        this.id = id;
        this.quantity = quantity;
        this.worldPoint = worldPoint;
        this.addedAtGameCycle = addedAtGameCycle;
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
