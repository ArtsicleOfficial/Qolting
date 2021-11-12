package com.qolting;

import com.google.inject.Provides;
import javax.inject.Inject;
import javax.sound.sampled.*;

import com.qolting.UI.*;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.events.*;
import net.runelite.api.kit.KitType;
import net.runelite.client.RuneLite;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.events.NpcLootReceived;
import net.runelite.client.game.ItemManager;
import net.runelite.client.game.ItemStack;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;


import java.io.*;
import java.util.ArrayList;

@Slf4j
@PluginDescriptor(
	name = "Qolting"
)
public class QoltingPlugin extends Plugin
{
	@Inject
	private Client client;
	@Inject
	private QoltingConfig config;
	@Inject
	private OverlayManager overlayManager;
	@Inject
	private ItemManager itemManager;

	private ArrayList<GroundItem> nearbyItems = new ArrayList<>();

	private QoltingAltarOverlay qoltingAltarPanel = null;
	private QoltingRSNOverlay qoltingRSNOverlay = null;
	private QoltingProfitPanel qoltingProfitPanel = null;
	private QoltingShoutOverlay qoltingShoutPanel = null;
	private QoltingNearbyPanel qoltingNearbyPanel = null;
	private QoltingSlotsLeftOverlay qoltingSlotsLeftOverlay = null;

	private Item[] lastPlayerInventory = null;

	int takingItem = 0;
	int ownLootTimer = 0;
	boolean playShardSoundNextTick = false;

	private void updateAltar() {
		if(!config.altarBar()) {
			overlayManager.remove(qoltingAltarPanel);
			return;
		}
		overlayManager.add(qoltingAltarPanel);

		qoltingAltarPanel.maxPrayer = client.getRealSkillLevel(Skill.PRAYER);
		qoltingAltarPanel.prayer = client.getBoostedSkillLevel(Skill.PRAYER);
		qoltingAltarPanel.gameWidth = client.getViewportWidth();
	}
	private void updateRSN() {
		if(!config.rsnDisplay()) {
			overlayManager.remove(qoltingRSNOverlay);
			return;
		}
		overlayManager.add(qoltingRSNOverlay);
	}
	private void updateProfit() {
		if(!config.sessionTracker()) {
			overlayManager.remove(qoltingProfitPanel);
			return;
		}
		overlayManager.add(qoltingProfitPanel);
	}
	private void updateShout() {
		boolean[] thingsToBeFussedAbout = new boolean[]{false,false,false};
		boolean any = false;
		if(config.tooAFKIndicator() && tooAFK()) {
			thingsToBeFussedAbout[QoltingShoutOverlay.TOO_AFK] = true;
			any = true;
		}
		if(config.treasureNear() && shardNear()) {
			thingsToBeFussedAbout[QoltingShoutOverlay.SHARD_NEARBY] = true;
			any = true;
		}
		if(config.goBank() && inventoryFull()) {
			thingsToBeFussedAbout[QoltingShoutOverlay.GO_BANK] = true;
			any = true;
		}
		qoltingShoutPanel.thingsToBeFussedAbout = thingsToBeFussedAbout;
		qoltingShoutPanel.viewportWidth = client.getViewportWidth();
		qoltingShoutPanel.viewportHeight = client.getViewportHeight();
		if(any) {
			overlayManager.add(qoltingShoutPanel);
		} else {
			overlayManager.remove(qoltingShoutPanel);
		}
	}
	private void updateNearby() {
		if(!config.doNearbyDrops()) {
			overlayManager.remove(qoltingNearbyPanel);
			return;
		}
		overlayManager.add(qoltingNearbyPanel);
		qoltingNearbyPanel.threshold = config.nearbyThreshold();
		qoltingNearbyPanel.nearbyItems = nearbyItems;
	}
	private void updateSlotsLeft() {
		if(!config.slotsLeft()) {
			overlayManager.remove(qoltingSlotsLeftOverlay);
			return;
		}
		overlayManager.add(qoltingSlotsLeftOverlay);
		qoltingSlotsLeftOverlay.slotsLeft = getSlotsLeft();
		qoltingSlotsLeftOverlay.fontSize = config.slotsLeftFontSize();
	}

	public String getItemName(int id) {
		return client.getItemDefinition(id).getName();
	}
	public int getItemPrice(int id) {
		return itemManager.getItemPrice(id);
	}


	public boolean inventoryFull() {
		ItemContainer invent = client.getItemContainer(InventoryID.INVENTORY);
		if(invent == null) {
			return false;
		}
		if(invent.getItems().length < 28) {
			return false;
		}
		boolean full = true;
		for(Item i : invent.getItems()) {
			if(i == null) {
				full = false;
				break;
			}
			if(i.getQuantity() == 0) {
				full = false;
			}
		}
		return full;
	}
	public int getSlotsLeft() {
		ItemContainer invent = client.getItemContainer(InventoryID.INVENTORY);
		if(invent == null) {
			return 28;
		}
		int itemsCounted = 0;
		for(Item i : invent.getItems()) {
			if(i == null) {
				continue;
			}
			if(i.getQuantity() == 0) {
				continue;
			}
			itemsCounted++;
		}
		return 28-itemsCounted;
	}
	public boolean shardNear() {
		for(GroundItem i : nearbyItems) {
			if(i.id == ItemID.BLOOD_SHARD) {
				return true;
			}
		}
		return false;
	}
	public boolean tooAFK() {
		Player localPlayer = client.getLocalPlayer();
		if(localPlayer == null) {
			return false;
		}
		if(localPlayer.getPlayerComposition().getEquipmentId(KitType.TORSO) == ItemID.VYRE_NOBLE_TOP || localPlayer.getPlayerComposition().getEquipmentId(KitType.LEGS) == ItemID.VYRE_NOBLE_LEGS || localPlayer.getPlayerComposition().getEquipmentId(KitType.BOOTS) == ItemID.VYRE_NOBLE_SHOES) {
			return true;
		}
		if(client.getVarpValue(172) == 1) {
			return true;
		}
		return false;
	}

	public boolean isPrayerOff() {
		for (Prayer pray : Prayer.values())	{
			if (client.isPrayerActive(pray)) {
				return false;
			}
		}
		return true;
	}
	private boolean isInDarkmeyer() {
		Player localPlayer = client.getLocalPlayer();
		if(localPlayer == null) {
			return false;
		}
		return localPlayer.getWorldLocation().getRegionID() == 14388;
	}

	public String getRSN() {
		if(client.getLocalPlayer() == null || client.getLocalPlayer().getName() == null) {
			return "?";
		}
		return client.getLocalPlayer().getName();
	}

	private void removeAllPanels() {
		overlayManager.remove(qoltingAltarPanel);
		overlayManager.remove(qoltingRSNOverlay);
		overlayManager.remove(qoltingProfitPanel);
		overlayManager.remove(qoltingShoutPanel);
		overlayManager.remove(qoltingNearbyPanel);
		overlayManager.remove(qoltingSlotsLeftOverlay);
	}

	@Override
	protected void startUp()
	{
		qoltingAltarPanel = new QoltingAltarOverlay(this,0,0,config.altarThreshold(),config.altarBackground(),config.altarForeground(),config.altarForegroundLow(),config.altarForegroundOff(),config.altarFlashing());
		qoltingRSNOverlay = new QoltingRSNOverlay(this);
		qoltingProfitPanel = new QoltingProfitPanel(this);
		qoltingShoutPanel = new QoltingShoutOverlay(this,client.getViewportWidth(),client.getViewportHeight());
		qoltingNearbyPanel = new QoltingNearbyPanel(this);
		qoltingSlotsLeftOverlay = new QoltingSlotsLeftOverlay(this);
	}

	@Override
	protected void shutDown()
	{
		removeAllPanels();
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged gameStateChanged)
	{
	}

	//If it works, it works!
	private void playSound(String path) {
		try {
			AudioInputStream soundFile;
			Clip clip = AudioSystem.getClip();

			soundFile = AudioSystem.getAudioInputStream(new File(RuneLite.RUNELITE_DIR, path));

			clip.open(soundFile);
			clip.loop(0);
		} catch (UnsupportedAudioFileException | LineUnavailableException | IOException ex) {
			ex.printStackTrace();
		}

	}

	@Subscribe
	public void onMenuOptionClicked(MenuOptionClicked clicked) {
		if(clicked.getMenuOption().toLowerCase().contains("take")) {
			takingItem = 10;
		}
	}

	@Subscribe
	public void onItemContainerChanged(ItemContainerChanged containerChanged) {

		if(containerChanged.getContainerId() == InventoryID.INVENTORY.getId()) {
			if(takingItem == 0) {
				lastPlayerInventory = containerChanged.getItemContainer().getItems();
				return;
			}
			if(lastPlayerInventory == null) {
				lastPlayerInventory = containerChanged.getItemContainer().getItems();
				return;
			}
			mainLoop: for(int count = 0; count < containerChanged.getItemContainer().getItems().length; count++) {
				Item i = containerChanged.getItemContainer().getItems()[count];
				if(i == null) {
					continue;
				}
				//Make sure this item is unique and we aren't checking RUNE PLATEBODIES and things multiple times
				for(int count2 = 0; count2 < count; count2++) {
					if(containerChanged.getItemContainer().getItems()[count2].getId() == containerChanged.getItemContainer().getItems()[count].getId()) {
						continue mainLoop;
					}
				}

				int lastAmount = 0;
				int newAmount = 0;
				for(Item j : lastPlayerInventory) {
					if(j.getId() == i.getId()) {
						lastAmount += j.getQuantity();
					}
				}
				for(Item j : containerChanged.getItemContainer().getItems()) {
					if(j.getId() == i.getId()) {
						newAmount += j.getQuantity();
					}
				}
				if(newAmount - lastAmount > 0) {
					qoltingProfitPanel.profit += itemManager.getItemPrice(i.getId()) * (newAmount-lastAmount);
				}
			}
			lastPlayerInventory = containerChanged.getItemContainer().getItems();
		}
	}

	@Subscribe
	public void onNpcLootReceived(NpcLootReceived npcLootReceived) {
		for(ItemStack i : npcLootReceived.getItems()) {
			if(i.getId() == ItemID.BLOOD_SHARD) {
				ownLootTimer = 10;
				break;
			}
		}
	}

	@Subscribe
	public void onItemSpawned(ItemSpawned itemSpawned) {
		TileItem item = itemSpawned.getItem();
		if(item.getId() == ItemID.BLOOD_SHARD) {
			playShardSoundNextTick = true;
		} else if(item.getId() == ItemID.ONYX_BOLT_TIPS && config.customOnItsBoltTips()) {
			playSound("onitsbolttips.wav");
		}

		boolean existing = false;
		for(GroundItem i : nearbyItems) {
			if(i.id == item.getId()) {
				i.quantity += item.getQuantity();
				existing = true;
				break;
			}
		}
		if(!existing) {
			nearbyItems.add(new GroundItem(item.getId(),item.getQuantity()));
		}
	}

	@Subscribe
	public void onItemDespawned(ItemDespawned itemDespawned) {
		TileItem item = itemDespawned.getItem();

		for(GroundItem i : nearbyItems) {
			if(i.id == item.getId()) {
				i.quantity -= item.getQuantity();
				if(i.quantity == 0) {
					nearbyItems.remove(i);
				}
				break;
			}
		}

	}

	@Subscribe
	public void onItemQuantityChanged(ItemQuantityChanged itemQuantityChanged) {
		TileItem item = itemQuantityChanged.getItem();

		for(GroundItem i : nearbyItems) {
			if(i.id == item.getId()) {
				i.quantity = itemQuantityChanged.getNewQuantity();
				break;
			}
		}
	}

	@Subscribe
	public void onConfigChanged(ConfigChanged configChanged) {
		qoltingAltarPanel.backgroundColor = config.altarBackground();
		qoltingAltarPanel.foregroundColor = config.altarForeground();
		qoltingAltarPanel.flashingColor = config.altarFlashing();
		qoltingAltarPanel.foregroundLowColor = config.altarForegroundLow();
		qoltingAltarPanel.foregroundOffColor = config.altarForegroundOff();
		qoltingAltarPanel.threshold = config.altarThreshold();
		qoltingRSNOverlay.fontSize = config.rsnFontSize();
	}

	@Subscribe
	public void onGameTick(GameTick gameTick) {
		if(!isInDarkmeyer()) {
			removeAllPanels();
			return;
		}
		if(playShardSoundNextTick) {
			if(ownLootTimer == 0 && config.customYoink()) {
				playSound("yoink.wav");
			} else if(config.customBlushard()) {
				playSound("blushard.wav");
			}
			playShardSoundNextTick = false;
		}
		updateAltar();
		updateRSN();
		updateProfit();
		updateShout();
		updateNearby();
		updateSlotsLeft();

		takingItem = Math.max(0,takingItem-1);
		ownLootTimer = Math.max(0,ownLootTimer-1);
	}

	@Provides
	QoltingConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(QoltingConfig.class);
	}
}
