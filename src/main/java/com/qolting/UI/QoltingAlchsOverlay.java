package com.qolting.UI;

import com.qolting.QoltingPlugin;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayPosition;

import javax.inject.Inject;
import java.awt.*;

public class QoltingAlchsOverlay extends Overlay {

    public int count = 0;
    public int fontSize = 50;

    @Inject
    public QoltingAlchsOverlay(QoltingPlugin qoltingPlugin) {
        super(qoltingPlugin);

        setPosition(OverlayPosition.BOTTOM_RIGHT);
        setDragTargetable(true);
        setResizable(false);
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        graphics.setFont(new Font(graphics.getFont().getFontName(),Font.PLAIN,fontSize));
        FontMetrics metrics = graphics.getFontMetrics();

        if(count > 0) {
            String str = count + "";

            graphics.setColor(Color.GREEN);
            graphics.drawString(str, 0, metrics.getAscent());

            return new Dimension(metrics.stringWidth(str), metrics.getAscent());
        } else {
            return new Dimension(0,0);
        }
    }
}
