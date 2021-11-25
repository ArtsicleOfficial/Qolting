package com.qolting.AccountManager;

import com.qolting.LootItem;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import javax.swing.text.AttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
@Slf4j
public class QoltingAccountManagerFrame {

    public JFrame frame;

    private volatile boolean running = true;

    public JTextPane lowPrayer;
    public JTextPane lootNearby;
    public JTextPane lowBackpack;
    public JTextPane atAltarBank;


    public volatile QoltingAccountManager manager;

    public QoltingAccountManagerFrame(QoltingAccountManager manager) {
        this.manager = manager;

        createWindow();
    }

    public void close() {
        frame.dispose();
    }

    public void createWindow() {
        frame = new JFrame("Qolting Account Manager");
        frame.setSize(500,300);

        frame.setLayout(new GridLayout(1,4));

        lowPrayer = new JTextPane();
        lootNearby = new JTextPane();
        lowBackpack = new JTextPane();
        atAltarBank = new JTextPane();

        lowPrayer.setEditable(false);
        lootNearby.setEditable(false);
        lowBackpack.setEditable(false);
        atAltarBank.setEditable(false);

        lowPrayer.setContentType("text/html");
        lowBackpack.setContentType("text/html");
        lootNearby.setContentType("text/html");
        atAltarBank.setContentType("text/html");

        frame.add(lowPrayer);
        frame.add(lootNearby);
        frame.add(lowBackpack);
        frame.add(atAltarBank);

        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        frame.setVisible(true);
    }

    public void update(int prayerThreshold, int nearbyThreshold) {
        if(!frame.isVisible()) {
            return;
        }

        ArrayList<QoltingAccountInfo> data = manager.getAllAccountInfo();

        String lowPrayerT = "<HTML>";
        String lowBackpackT = "<HTML>";
        String lootNearbyT = "<HTML>";
        String atAltarBankT = "<HTML>";


        lowPrayerT += getSpan("Low Prayer: <br/>",Color.LIGHT_GRAY);
        lowBackpackT += getSpan("Low Backpack: <br/>",Color.LIGHT_GRAY);
        lootNearbyT += getSpan("Loot Nearby: <br/>",Color.LIGHT_GRAY);
        atAltarBankT += getSpan("Stuck Altar/Bank: <br/>",Color.LIGHT_GRAY);
        for(QoltingAccountInfo info : data) {
            if(info.prayer <= prayerThreshold) {
                lowPrayerT += getSpan(info.username + " (" + info.prayer + ")",Color.CYAN);
            }
            if(info.backpackSpaces <= 2) {
                lowBackpackT += getSpan(info.username + " (" + info.backpackSpaces + ")",new Color(40,20,0));
            }
            if(info.items.size() > 0) {
                for(LootItem item : info.items) {
                    int value = item.value * item.quantity;
                    if(value < nearbyThreshold) {
                        continue;
                    }
                    lootNearbyT += getSpan((value/1000) + "k: " + item.name.substring(0,1).toUpperCase() + item.name.substring(1).toLowerCase() + " * " + item.quantity + " (" + info.username + ")",interpolateColors(Color.LIGHT_GRAY,Color.GREEN,Math.max(0,Math.min(1,value/40000.0f))));
                }
            }
            if(info.atAltarOrBank) {
                atAltarBankT += getSpan(info.username + "",new Color(100,0,0));
            }
        }



        lowPrayer.setText(lowPrayerT);
        lowBackpack.setText(lowBackpackT);
        lootNearby.setText(lootNearbyT);
        atAltarBank.setText(atAltarBankT);
    }

    private int lerp(int x, int y, float a) {
        return (int)Math.floor(x + (y-x)*a);
    }

    private Color interpolateColors(Color c1, Color c2, float amt) {
        return new Color(lerp(c1.getRed(),c2.getRed(),amt),lerp(c1.getGreen(),c2.getGreen(),amt),lerp(c1.getBlue(),c2.getBlue(),amt));
    }

    private String getSpan(String s, Color c)
    {
        //https://stackoverflow.com/questions/3607858/convert-a-rgb-color-value-to-a-hexadecimal-string
        String hex = "#"+Integer.toHexString(c.getRGB()).substring(2);
        return " <span style=\"color: rgb(" + c.getRed() + "," + c.getGreen() + "," + c.getBlue() + ")\">" + s + "</span><br/>";
    }

}
