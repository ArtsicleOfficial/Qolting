package com.qolting.UI;

import com.qolting.QoltingPlugin;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;

import javax.inject.Inject;
import java.awt.*;

public class QoltingRSNOverlay extends Overlay {
    public String rsn;
    public QoltingPlugin plugin;
    public int fontSize = 50;


    int padding = 5;
    @Inject
    public QoltingRSNOverlay(QoltingPlugin plugin) {
        super(plugin);

        this.plugin = plugin;

        rsn = plugin.getRSN();

        setDragTargetable(true);
        setLayer(OverlayLayer.ABOVE_WIDGETS);
        setPosition(OverlayPosition.ABOVE_CHATBOX_RIGHT);
        setResizable(false);
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        this.rsn = plugin.getRSN();
        graphics.setFont(new Font(graphics.getFont().getFontName(), Font.PLAIN, fontSize));
        FontMetrics metrics = graphics.getFontMetrics();

        graphics.setColor(Color.white);
        graphics.drawString(rsn,padding/2,metrics.getAscent());

        return new Dimension(metrics.stringWidth(rsn) + padding*2,Math.max(5,fontSize));
    }
}
