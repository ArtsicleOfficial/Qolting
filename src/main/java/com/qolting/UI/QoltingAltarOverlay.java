package com.qolting.UI;

import com.qolting.QoltingConfig;
import com.qolting.QoltingPlugin;
import net.runelite.client.ui.overlay.*;

import javax.inject.Inject;
import java.awt.*;

public class QoltingAltarOverlay extends Overlay {

    public int prayer;
    public int maxPrayer;
    public int threshold;

    public Color backgroundColor;
    public Color foregroundColor;
    public Color flashingColor;
    public Color foregroundLowColor;
    public Color foregroundOffColor;

    private int counter = 0;

    public int gameWidth = 100;

    public int barHeight = 25;

    private final QoltingPlugin plugin;

    @Inject
    public QoltingAltarOverlay(QoltingPlugin plugin, int prayer, int maxPrayer, int threshold, Color backgroundColor, Color foregroundColor, Color foregroundLowColor, Color foregroundOffColor, Color flashingColor) {
        super(plugin);
        this.prayer = prayer;
        this.maxPrayer = maxPrayer;
        this.foregroundColor = foregroundColor;
        this.backgroundColor = backgroundColor;
        this.flashingColor = flashingColor;
        this.foregroundLowColor = foregroundLowColor;
        this.foregroundOffColor = foregroundOffColor;
        this.threshold = threshold;
        this.plugin = plugin;

        setPosition(OverlayPosition.DYNAMIC);
        setDragTargetable(false);
        setBounds(new Rectangle(0,0,gameWidth,barHeight));
        setPriority(OverlayPriority.HIGH);
        setLayer(OverlayLayer.UNDER_WIDGETS);
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        int rightSideDisplayWidth = 50;
        counter++;

        graphics.setColor(backgroundColor);
        if(prayer <= threshold && counter % 20 <= 10) {
            graphics.setColor(flashingColor);
        }
        graphics.fillRect(0,0,gameWidth,barHeight);

        graphics.setColor(foregroundColor);

        if(plugin.isPrayerOff()) {
            graphics.setColor(foregroundOffColor);
        } else if(prayer <= threshold) {
            graphics.setColor(foregroundLowColor);
        }
        graphics.fillRect(0,0,(gameWidth - rightSideDisplayWidth-1) * prayer / maxPrayer ,barHeight);

        graphics.setColor(foregroundColor);
        graphics.fillRect(gameWidth-rightSideDisplayWidth,0,rightSideDisplayWidth,barHeight);

        String letsWrite = prayer + "/" + maxPrayer;
        FontMetrics metrics = graphics.getFontMetrics();
        graphics.setColor(backgroundColor);
        graphics.drawString(letsWrite,gameWidth-(rightSideDisplayWidth/2) - metrics.stringWidth(letsWrite)/2,barHeight/2 + metrics.getAscent()/2);

        graphics.setColor(Color.WHITE);
        graphics.fillRect(0,barHeight-2,gameWidth,2);


        return new Dimension(gameWidth,barHeight);
    }

}
