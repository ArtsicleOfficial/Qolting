package com.qolting.UI;

import com.qolting.Blackout.BlackoutQuad;
import com.qolting.Blackout.BlackoutVector;
import com.qolting.QoltingPlugin;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;

import java.awt.*;
import java.awt.geom.Area;
import java.util.ArrayList;

public class QoltingBlackoutOverlay extends Overlay {

    public int gameWidth;
    public int gameHeight;
    public int padding;
    public Color color;

    public ArrayList<BlackoutQuad> quads = new ArrayList<>();

    public QoltingBlackoutOverlay(QoltingPlugin plugin, int gameWidth, int gameHeight, int padding, Color color) {
        super(plugin);

        this.gameHeight = gameHeight;
        this.gameWidth = gameWidth;
        this.padding = padding;
        this.color = color;

        setPosition(OverlayPosition.DYNAMIC);
        setDragTargetable(false);
        setBounds(new Rectangle(0,0,gameWidth,gameHeight));
        setPriority(OverlayPriority.HIGHEST);
        setLayer(OverlayLayer.ABOVE_WIDGETS);
    }

    public void addQuad(Polygon gon) {
        if(gon == null || gon.xpoints == null) {
            return;
        }
        quads.add(new BlackoutQuad(
                new BlackoutVector(gon.xpoints[0], gon.ypoints[0] ),
                new BlackoutVector(gon.xpoints[1], gon.ypoints[1]),
                new BlackoutVector(gon.xpoints[2], gon.ypoints[2]),
                new BlackoutVector(gon.xpoints[3], gon.ypoints[3]))
        );
    }

    @Override
    public Dimension render(Graphics2D graphics2D) {
        Area clippingArea = new Area(new Rectangle(0,0,gameWidth,gameHeight));
        for(BlackoutQuad quad : quads) {
            clippingArea.subtract(new Area(BlackoutQuad.expandRectangle(quad.getBounds(),padding)));
        }
        graphics2D.setClip(clippingArea);

        graphics2D.setColor(color);
        graphics2D.fillRect(0,0,gameWidth,gameHeight);

        return new Dimension(gameWidth,gameHeight);
    }
}
