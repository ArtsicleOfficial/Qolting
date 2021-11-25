package com.qolting;

import com.google.inject.Provides;
import javax.inject.Inject;
import javax.sound.sampled.*;

import com.qolting.AccountManager.QoltingAccountInfo;
import com.qolting.AccountManager.QoltingAccountManager;
import com.qolting.AccountManager.QoltingAccountManagerFrame;
import com.qolting.UI.*;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.events.*;
import net.runelite.api.kit.KitType;
import net.runelite.client.RuneLite;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ClientShutdown;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.events.NpcLootReceived;
import net.runelite.client.events.OverlayMenuClicked;
import net.runelite.client.game.ItemManager;
import net.runelite.client.game.ItemStack;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import org.apache.commons.lang3.ArrayUtils;


import java.io.*;
import java.util.ArrayList;

@Slf4j
@PluginDescriptor(
		name = "Qolting dev"
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
	@Inject
	private ConfigManager configManager;

	private QoltingAccountManager qoltingAccountManager;

	private QoltingAccountManagerFrame currentManager = null;

	private File qoltingDirectory = new File(RuneLite.RUNELITE_DIR,"qolting");

	private ArrayList<GroundItem> nearbyItems = new ArrayList<>();

	private QoltingAltarOverlay qoltingAltarPanel = null;
	private QoltingRSNOverlay qoltingRSNOverlay = null;
	private QoltingProfitPanel qoltingProfitPanel = null;
	private QoltingShoutOverlay qoltingShoutPanel = null;
	private QoltingNearbyPanel qoltingNearbyPanel = null;
	private QoltingSlotsLeftOverlay qoltingSlotsLeftOverlay = null;

	private Item[] lastPlayerInventory = null;

	private static final long CLIP_MTIME_UNLOADED = -2;
	private static final long CLIP_MTIME_BUILTIN = -1;


	public final File[] files = {
			new File(RuneLite.RUNELITE_DIR, "yoink.wav"),
			new File(RuneLite.RUNELITE_DIR, "blushard.wav"),
			new File(RuneLite.RUNELITE_DIR, "onitsbolttips.wav")
	};
	public final Clip[] clips = {
			null,
			null,
			null
	};
	private long[] lastClipMTime = {
			CLIP_MTIME_UNLOADED,
			CLIP_MTIME_UNLOADED,
			CLIP_MTIME_UNLOADED
	};
	private final byte YOINK = 0;
	private final byte SHARD = 1;
	private final byte ONYX = 2;

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
		qoltingNearbyPanel.nearbyItems = nearbyItems;
	}
	private void updateSlotsLeft() {
		if(!config.slotsLeft()) {
			overlayManager.remove(qoltingSlotsLeftOverlay);
			return;
		}
		overlayManager.add(qoltingSlotsLeftOverlay);
		qoltingSlotsLeftOverlay.slotsLeft = getSlotsLeft();
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
		qoltingAccountManager = new QoltingAccountManager(qoltingDirectory);

		qoltingAltarPanel = new QoltingAltarOverlay(this,0,0,config.altarThreshold(),config.altarBackground(),config.altarForeground(),config.altarForegroundLow(),config.altarForegroundOff(),config.altarFlashing());
		qoltingRSNOverlay = new QoltingRSNOverlay(this);
		qoltingProfitPanel = new QoltingProfitPanel(this);
		qoltingShoutPanel = new QoltingShoutOverlay(this,client.getViewportWidth(),client.getViewportHeight());
		qoltingNearbyPanel = new QoltingNearbyPanel(this);
		qoltingSlotsLeftOverlay = new QoltingSlotsLeftOverlay(this);

		updateConfig();

	}

	@Override
	protected void shutDown()
	{
		removeAllPanels();
	}

	private synchronized void playCustomSound(byte index)
	{
		File file = files[index];
		long currentMTime = file.exists() ? file.lastModified() : CLIP_MTIME_BUILTIN;
		if (clips[index] == null || currentMTime != lastClipMTime[index] || !clips[index].isOpen())
		{
			if (clips[index] != null)
			{
				clips[index].close();
			}

			try
			{
				clips[index] = AudioSystem.getClip();
			}
			catch (LineUnavailableException e)
			{
				lastClipMTime[index] = CLIP_MTIME_UNLOADED;
				log.warn("Unable to play notification", e);
				return;
			}

			lastClipMTime[index] = currentMTime;

			if (!tryLoadNotification(index))
			{
				return;
			}
		}
		// Using loop instead of start + setFramePosition prevents a the clip
		// from not being played sometimes, presumably a race condition in the
		// underlying line driver
		clips[index].loop(Math.min(Math.max(0,config.loopBlasters()-1),100));
	}

	private boolean tryLoadNotification(byte index)
	{
		File file = files[index];
		if (file.exists())
		{
			try (InputStream fileStream = new BufferedInputStream(new FileInputStream(file));
				 AudioInputStream sound = AudioSystem.getAudioInputStream(fileStream))
			{
				clips[index].open(sound);
				return true;
			}
			catch (UnsupportedAudioFileException | IOException | LineUnavailableException e)
			{
				log.warn("Unable to load notification sound", e);
			}
		}

		return false;
	}

	@Subscribe
	public void onMenuOptionClicked(MenuOptionClicked clicked) {
		if(clicked.getMenuOption().toLowerCase().contains("take")) {
			takingItem = 10;
		}
	}

	private void clearNearbyItems() {
		nearbyItems.clear();
		qoltingNearbyPanel.nearbyItems.clear();
	}

	@Subscribe
	public void onOverlayMenuClicked(OverlayMenuClicked event) {
		if(event.getEntry().getMenuAction() == MenuAction.RUNELITE_OVERLAY &&
				event.getEntry().getTarget().equals("List") &&
				event.getEntry().getOption().equals("Clear")) {
			clearNearbyItems();
		}
	}

	private Item[] getInventoryList(ItemContainerChanged changed) {
		return ArrayUtils.addAll(changed.getItemContainer().getItems(),client.getItemContainer(InventoryID.EQUIPMENT).getItems());
	}

	@Subscribe
	public void onItemContainerChanged(ItemContainerChanged containerChanged) {
		if(containerChanged.getContainerId() == InventoryID.INVENTORY.getId()) {
			if(takingItem == 0) {
				lastPlayerInventory = getInventoryList(containerChanged);
				return;
			}
			if(lastPlayerInventory == null) {
				lastPlayerInventory = getInventoryList(containerChanged);
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
			lastPlayerInventory = getInventoryList(containerChanged);
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
			playCustomSound(ONYX);
		}

		if(client.getLocalPlayer() == null) {
			return;
		}

		if(Math.abs(itemSpawned.getTile().getWorldLocation().getX() - client.getLocalPlayer().getWorldLocation().getX()) > config.nearbyRange() ||
				Math.abs(itemSpawned.getTile().getWorldLocation().getY() - client.getLocalPlayer().getWorldLocation().getY()) > config.nearbyRange()) {
			//item is outside of the range of the config
			return;
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

	public void updateConfig() {
		qoltingAltarPanel.backgroundColor = config.altarBackground();
		qoltingAltarPanel.foregroundColor = config.altarForeground();
		qoltingAltarPanel.flashingColor = config.altarFlashing();
		qoltingAltarPanel.foregroundLowColor = config.altarForegroundLow();
		qoltingAltarPanel.foregroundOffColor = config.altarForegroundOff();
		qoltingAltarPanel.threshold = config.altarThreshold();
		qoltingAltarPanel.barHeight = config.altarSize();
		qoltingAltarPanel.displayOutline = config.altarOutline();
		qoltingAltarPanel.displayPrayer = config.altarPrayer();
		qoltingAltarPanel.flashInterval = config.flashyInterval();

		qoltingRSNOverlay.fontSize = config.rsnFontSize();
		qoltingSlotsLeftOverlay.fontSize = config.slotsLeftFontSize();
		qoltingNearbyPanel.threshold = config.nearbyThreshold();

		qoltingShoutPanel.flashInterval = config.flashyInterval();
	}

	@Subscribe
	public void onConfigChanged(ConfigChanged configChanged) {
		updateConfig();

		if(configChanged.getKey().equals("launchAccountTracker")) {
			if(currentManager != null) {
				currentManager.close();
			}
			currentManager = new QoltingAccountManagerFrame(qoltingAccountManager);
			currentManager.update(config.altarThreshold(),config.nearbyThreshold());
		}
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged gameStateChanged) {
		int state = gameStateChanged.getGameState().getState();
		if(state == GameState.HOPPING.getState() || state == GameState.LOGGING_IN.getState() || state == GameState.STARTING.getState()) {
			clearNearbyItems();
		}
	}

	@Subscribe
	public void onGameTick(GameTick gameTick) {
		if(config.useAccountTracker()) {
			ArrayList<LootItem> items = new ArrayList<>();
			for(GroundItem item : nearbyItems) {
				items.add(new LootItem(item,this));
			}
			qoltingAccountManager.saveAccountInfo(getRSN(),client.getBoostedSkillLevel(Skill.PRAYER),client.getBoostedSkillLevel(Skill.HITPOINTS),getSlotsLeft(),items,client.getLocalPlayer().getWorldLocation());
		}
		if(currentManager != null) {
			currentManager.update(config.altarThreshold(),config.nearbyThreshold());
		}
		if(!isInDarkmeyer()) {
			removeAllPanels();
			return;
		}
		if(playShardSoundNextTick) {
			if(ownLootTimer == 0 && config.customYoink()) {
				playCustomSound(YOINK);
			} else if(config.customBlushard()) {
				playCustomSound(SHARD);
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
