package com.qolting.UI;

import com.qolting.QoltingPlugin;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.TitleComponent;
import net.runelite.client.util.ImageUtil;

import javax.inject.Inject;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class QoltingShoutOverlay extends Overlay {

    public static final int TOO_AFK = 0;
    public static final int SHARD_NEARBY = 1;
    public static final int GO_BANK = 2;

    private int blinker = 0;

    public int viewportWidth;
    public int viewportHeight;

    public boolean[] thingsToBeFussedAbout = new boolean[3];

    public BufferedImage shard;


    @Inject
    public QoltingShoutOverlay(QoltingPlugin plugin, int viewportWidth, int viewportHeight) {
        super(plugin);

        this.viewportHeight = viewportHeight;
        this.viewportWidth = viewportWidth;

        setPosition(OverlayPosition.DYNAMIC);
        setResizable(false);
        setDragTargetable(false);

        shard = ImageUtil.loadImageResource(getClass(),"/shard.png");
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        blinker++;
        graphics.setFont(new Font(graphics.getFont().getFontName(),Font.PLAIN,30));
        FontMetrics metrics = graphics.getFontMetrics();

        int x = viewportWidth/4;
        int y = viewportHeight/4;
        int w = viewportWidth/2;
        int h = viewportHeight/2;

        for(boolean b : thingsToBeFussedAbout) {
            if(b) {
                graphics.setColor(new Color(0,0,0,20));
                graphics.fillRect(x-5,y-5,w+10,h+10);
                graphics.setColor(new Color(250,250,250,30));
                graphics.fillRect(x,y,w,h);
                break;
            }
        }

        if(thingsToBeFussedAbout[QoltingShoutOverlay.GO_BANK]) {
            graphics.setColor(Color.BLACK);
            graphics.drawString("Go bank",x + w/2 - metrics.stringWidth("Go bank")/2,y + metrics.getAscent() + 5);
        }
        if(thingsToBeFussedAbout[QoltingShoutOverlay.TOO_AFK]) {
            graphics.setColor(Color.BLACK);
            graphics.drawString("TOO AFK (Auto-Retaliate OR Vyre Outfit)",x + w/2 - metrics.stringWidth("TOO AFK (Auto-Retaliate OR Vyre Outfit)")/2, y + h - 5);
        }
        /*graphics.setFont(new Font(graphics.getFont().getFontName(),Font.PLAIN,80));
        metrics = graphics.getFontMetrics();*/
        if(thingsToBeFussedAbout[QoltingShoutOverlay.SHARD_NEARBY] && blinker % 10 > 3) {
            int shardW = 150;
            //graphics.setColor(Color.RED);
            //graphics.drawString("SHARD!!@@@", x + w / 2 - metrics.stringWidth("SHARD!!@@@") / 2, y + h / 2 + metrics.getAscent() / 2);
            graphics.drawImage(shard,x + w / 2 - shardW/2,y + h / 2 - shardW/2,shardW,shardW,null);
        }

        return new Dimension(w,h);
    }
}
