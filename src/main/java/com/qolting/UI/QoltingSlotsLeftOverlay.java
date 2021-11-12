package com.qolting.UI;

import com.qolting.QoltingPlugin;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayPosition;

import javax.inject.Inject;
import java.awt.*;

public class QoltingSlotsLeftOverlay extends Overlay {

    public int slotsLeft = 28;
    public int fontSize = 50;

    @Inject
    public QoltingSlotsLeftOverlay(QoltingPlugin qoltingPlugin) {
        super(qoltingPlugin);

        setPosition(OverlayPosition.BOTTOM_RIGHT);
        setDragTargetable(true);
        setResizable(false);
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        graphics.setFont(new Font(graphics.getFont().getFontName(),Font.PLAIN,fontSize));
        FontMetrics metrics = graphics.getFontMetrics();
        String str = slotsLeft + "";

        graphics.setColor(Color.LIGHT_GRAY);
        graphics.drawString(str,0,metrics.getAscent());

        return new Dimension(metrics.stringWidth(str),metrics.getAscent());
    }
}
