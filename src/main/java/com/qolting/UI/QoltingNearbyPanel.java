package com.qolting.UI;

import com.qolting.GroundItem;
import com.qolting.QoltingPlugin;
import net.runelite.api.ItemID;
import net.runelite.api.MenuAction;
import net.runelite.client.ui.overlay.OverlayMenuEntry;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LineComponent;

import java.awt.*;
import java.util.ArrayList;

public class QoltingNearbyPanel extends OverlayPanel {

    public ArrayList<GroundItem> nearbyItems = new ArrayList<>();
    public int threshold = 0;

    private QoltingPlugin plugin;

    public QoltingNearbyPanel(QoltingPlugin plugin) {
        super(plugin);
        this.plugin = plugin;
        setPosition(OverlayPosition.TOP_RIGHT);

        getMenuEntries().add(new OverlayMenuEntry(MenuAction.RUNELITE_OVERLAY, "Clear","List"));
    }

    public Dimension render(Graphics2D graphics) {

        for(GroundItem groundItem : nearbyItems) {
            if(plugin.getItemPrice(groundItem.id) >= threshold) {
                panelComponent.getChildren().add(LineComponent.builder()
                        .left("*" + groundItem.quantity)
                        .leftColor(Color.WHITE)
                        .right(plugin.getItemName(groundItem.id))
                        .rightColor(Color.GREEN)
                        .build());
            }
        }

        return super.render(graphics);
    }
}
