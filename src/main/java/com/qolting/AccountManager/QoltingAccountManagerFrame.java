package com.qolting.AccountManager;

import com.qolting.LootItem;
import com.qolting.QoltingPlugin;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.RuneLite;

import javax.swing.*;
import javax.swing.text.AttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
@Slf4j
public class QoltingAccountManagerFrame {

    public JFrame frame;

    private volatile boolean running = true;

    public JTextPane lowPrayer;
    public JTextPane lootNearby;
    public JTextPane lowBackpack;
    public JTextPane atAltarBank;

    public JButton toggleBlackout;
    public JTextPane GPhr;
    public JTextPane totalProfit;
    public JPanel pageEnd;

    public QoltingPlugin plugin;


    public volatile QoltingAccountManager manager;

    public QoltingAccountManagerFrame(QoltingAccountManager manager,boolean alwaysOnTop,QoltingPlugin plugin) {
        this.manager = manager;

        createWindow();

        this.frame.setAlwaysOnTop(alwaysOnTop);

        this.plugin = plugin;
    }

    public void close() {
        frame.dispose();
    }

    public void createWindow() {
        frame = new JFrame("Qolting Account Manager");
        frame.setSize(500,300);

        frame.setLayout(new BorderLayout());

        JPanel panel = new JPanel(new GridLayout(1,4));

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

        toggleBlackout = new JButton("Force Disable Blackout");

        File lock = new File(RuneLite.RUNELITE_DIR,QoltingPlugin.LOCK_FILE);
        if(lock.exists()) {
            lock.delete();
        }
        toggleBlackout.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                File file = new File(RuneLite.RUNELITE_DIR, QoltingPlugin.LOCK_FILE);
                if(file.exists()) {
                    file.delete();
                    toggleBlackout.setText("Force Disable Blackout");
                } else {
                    try {
                        file.createNewFile();
                        toggleBlackout.setText("Un-force-disable Blackout");
                    } catch (IOException ex) {
                        ex.printStackTrace();
                        toggleBlackout.setText("Could not disable blackout (try again)");
                    }
                }
            }
        });

        panel.add(lowPrayer);
        panel.add(lootNearby);
        panel.add(lowBackpack);
        panel.add(atAltarBank);

        GPhr = new JTextPane();
        GPhr.setContentType("text/html");
        GPhr.setEditable(false);

        totalProfit = new JTextPane();
        totalProfit.setContentType("text/html");
        totalProfit.setEditable(false);


        pageEnd = new JPanel(new GridLayout(1,2));
        pageEnd.setBackground(new Color(5,5,5));
        frame.add(pageEnd,BorderLayout.PAGE_END);

        pageEnd.add(GPhr);
        pageEnd.add(totalProfit);

        frame.add(toggleBlackout,BorderLayout.PAGE_START);
        frame.add(panel,BorderLayout.CENTER);

        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        frame.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {

            }

            @Override
            public void windowClosing(WindowEvent e) {
                File file = new File(RuneLite.RUNELITE_DIR, QoltingPlugin.LOCK_FILE);
                if(file.exists()) {
                    file.delete();
                }
            }

            @Override
            public void windowClosed(WindowEvent e) {

            }

            @Override
            public void windowIconified(WindowEvent e) {

            }

            @Override
            public void windowDeiconified(WindowEvent e) {

            }

            @Override
            public void windowActivated(WindowEvent e) {

            }

            @Override
            public void windowDeactivated(WindowEvent e) {

            }
        });

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
        int totalGPhr = 0;
        int totalledProfit = 0;
        for(QoltingAccountInfo info : data) {
            totalGPhr += info.GPhr;
            totalledProfit += info.profit;
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
                    if(plugin.ignoreItem(item.name)) {
                        continue;
                    }
                    lootNearbyT += getSpan((value/1000) + "k: " + item.name.substring(0,1).toUpperCase() + item.name.substring(1).toLowerCase() + " * " + item.quantity + " (" + info.username + ")",interpolateColors(Color.LIGHT_GRAY,Color.GREEN,Math.max(0,Math.min(1,value/40000.0f))));
                }
            }
            if(info.atAltarOrBank) {
                atAltarBankT += getSpan(info.username + "",new Color(100,0,0));
            }
        }
        totalledProfit /= 1000;

        GPhr.setText("<html>" + getSpan(totalGPhr + "k GP/hr (" + (totalGPhr/data.size()) + "k per account)",totalGPhr > 10000 ? new Color(192,220,192) : Color.lightGray));
        totalProfit.setText("<html><style>body { text-align:right; } </style><body>" + getSpan("+" + totalledProfit + "k GP (" + (totalledProfit/data.size()) + "k per account)</body>",totalledProfit > 10000 ? new Color(192,220,192) : Color.lightGray));



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
