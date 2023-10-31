package com.qolting.UI;

import com.qolting.GroundItem;
import com.qolting.QoltingPlugin;
import net.runelite.api.ItemID;
import net.runelite.api.MenuAction;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayMenuEntry;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LineComponent;

import java.awt.*;

public class QoltingNearbyPanel extends OverlayPanel {

    public int threshold = 0;

    private QoltingPlugin plugin;

    public QoltingNearbyPanel(QoltingPlugin plugin) {
        super(plugin);
        this.plugin = plugin;
        setPosition(OverlayPosition.TOP_RIGHT);
        setLayer(OverlayLayer.ALWAYS_ON_TOP);

        getMenuEntries().add(new OverlayMenuEntry(MenuAction.RUNELITE_OVERLAY, "Clear","List"));
    }

    public Dimension render(Graphics2D graphics) {

        for(GroundItem groundItem : plugin.nearbyItems) {
            if(plugin.getItemPrice(groundItem.id) * groundItem.quantity < threshold || plugin.ignoreItem(groundItem.id)) {
                continue;
            }
            panelComponent.getChildren().add(LineComponent.builder()
                        .left("*" + groundItem.quantity + " ")
                        .leftColor(Color.WHITE)
                        .right(plugin.getItemName(groundItem.id))
                        .rightColor(Color.GREEN)
                        .build());
        }

        return super.render(graphics);
    }
}
