package com.qolting;

import lombok.NoArgsConstructor;
import net.runelite.client.config.*;

import java.awt.*;

@ConfigGroup("qolting")
public interface QoltingConfig extends Config
{
	@ConfigSection(
				name = "General",
				description = "Everything general",
				position = -1
	)
	String general = "general";

	@ConfigSection(
				name = "Altar",
				description = "Related to the Altar",
				position = 1
	)
	String altar = "altar";

	@ConfigSection(
			name = "Blackout",
			description = "Related to the Blackout Overlay",
			position = 0
	)
	String blackout = "blackout";

	@ConfigSection(
				name = "Screen Blasters",
				description = "you're gonna have to figure out what that one means ;)",
				position = 2
	)
	String screenBlasters = "screenBlasters";

	@ConfigSection(
				name = "Ear Blasters",
				description = "Blasts your ears with pertinent info",
				position = 3
	)
	String earBlasters = "earBlasters";

	@ConfigSection(
				name = "Nearby Drops",
				description = "Nearby drops pointer-outer",
				position = 3
	)
	String nearbyDrops = "nearbyDrops";

	@ConfigSection(
				name = "Tracker",
				description = "Tracks all accounts using the plugin at once",
				position = 4
	)
	String tracker = "tracker";

	@ConfigItem(
			keyName = "rsnDisplay",
			name = "RSN Display",
			description = "Whether or not to display the account's RSN",
			section = general
	)
	default boolean rsnDisplay() {
		return true;
	}

	@ConfigItem(
			keyName = "onlyInDarkmeyer",
			name = "Only use Qolting in Darkmeyer",
			description = "Some people wanted to use the plugin outside Darkmeyer. Here you go.",
			section = general
	)
	default boolean onlyInDarkmeyer() { return true; }

	@ConfigItem(
			keyName = "blackoutOverlay",
			name = "Blackout Overlay",
			description = "Whether or not to display the blackout overlay",
			section = blackout
	)
	default boolean blackoutOverlay() {
		return false;
	}

	@ConfigItem(
			keyName = "blackoutFPS",
			name = "Lock to FPS",
			description = "Unsure whether this can lower FPS across many many clients",
			section = blackout
	)
	default boolean blackoutFPS() {
		return true;
	}

	@Alpha
	@ConfigItem(
			keyName = "blackoutColor",
			name = "Blackout Color",
			description = "Color and transparency of the blackout overlay",
			section = blackout
	)
	default Color blackoutColor() {
		return new Color(0,0,0,234);
	}

	@ConfigItem(
			keyName = "blackoutGlobalDisplayAltar",
			name = "Global Display Altar",
			description = "When any account has low prayer, all accounts will show the altar (Requires 'Use Account Manager' on each client)",
			section = blackout
	)
	default boolean blackoutGlobalDisplayAltar() {
		return true;
	}

	@Range(
			min = 0,
			max = 100
	)
	@ConfigItem(
			keyName = "blackoutPadding",
			name = "Padding",
			description = "Give me some space!",
			section = blackout
	)
	default int blackoutPadding() {
		return 5;
	}

	@Range(
			min=1
	)
	@ConfigItem(
			keyName = "rsnFontSize",
			name = "RSN Font Size",
			description = "The size of the RSN",
			section = general
	)
	default int rsnFontSize() {
		return 50;
	}

	@ConfigItem(
			keyName = "flashInterval",
			name = "Flashing Interval",
			description = "How many frames it should be before a blood shard flasher or altar prayer bar flasher flashes. (Higher = less flashy, avoid seizures.) At 50 FPS, if a flashing interval of 50 will result in one cycle of flashing per second.",
			section = general
	)
	default int flashyInterval() { return 20; }

	@ConfigItem(
			keyName = "slotsLeft",
			name = "Show Slots Left",
			description = "Show number of item slots open",
			section = general
	)
	default boolean slotsLeft() {
		return true;
	}

	@Range(
			min=1
	)
	@ConfigItem(
			keyName = "slotsLeftFontSize",
			name = "Slots Left Font Size",
			description = "Font size for slots left display",
			section = general
	)
	default int slotsLeftFontSize() {
		return 50;
	}

	@ConfigItem(
			keyName = "sessionTracker",
			name = "Session Tracker",
			description = "Displays profit from the session",
			section = general
	)
	default boolean sessionTracker() {
		return true;
	}


	@ConfigItem(
			keyName = "altarBar",
			name = "Bar",
			description = "Whether to show the altar bar",
			section = altar,
			position = 0
	)
	default boolean altarBar() {
		return true;
	}

	@ConfigItem(
			keyName = "altarBarOnBottom",
			name = "Bar On Bottom",
			description = "Whether or not to display the altar bar on the bottom",
			section = altar,
			position = 1
	)
	default boolean altarBarOnBottom() {
		return false;
	}

	@Range(
			max=99
	)
	@ConfigItem(
			keyName = "altarThreshold",
			name = "Threshold",
			description = "The threshold for the altar bar",
			section = altar,
			position = 2
	)
	default int altarThreshold() {
		return 5;
	}

	@ConfigItem(
			keyName = "altarSize",
			name = "Size",
			description = "The size for the altar bar",
			section = altar,
			position = 3
	)
	default int altarSize() {
		return 25;
	}


	@ConfigItem(
			keyName = "altarPrayer",
			name = "Display Prayer",
			description = "Whether or not to display prayer",
			section = altar,
			position = 5
	)
	default boolean altarPrayer() {
		return true;
	}

	@Alpha
	@ConfigItem(
			keyName="altarBackground",
			name="Background",
			description = "The background for the altar bar",
			section = altar
	)
	default Color altarBackground() {
		return new Color(0,0,0,255);
	}

	@Alpha
	@ConfigItem(
			keyName="altarOutline",
			name="Outline",
			description = "The outline for the altar bar",
			section = altar
	)
	default Color altarOutline() {
		return new Color(255,255,255,0);
	}

	@Alpha
	@ConfigItem(
			keyName="altarForeground",
			name="Foreground",
			description = "The foreground for the altar bar",
			section = altar
	)
	default Color altarForeground() {
		return new Color(38,63,62,255);
	}

	@Alpha
	@ConfigItem(
			keyName="altarForegroundOff",
			name="Foreground Off",
			description = "The foreground for the altar bar when off",
			section = altar
	)
	default Color altarForegroundOff() {
		return new Color(0, 5, 10,255);
	}


	@Alpha
	@ConfigItem(
			keyName="altarForegroundLow",
			name="Foreground Low",
			description = "The foreground for the altar bar when below threshold",
			section = altar
	)
	default Color altarForegroundLow() {
		return new Color(83,55,29,255);
	}

	@Alpha
	@ConfigItem(
			keyName = "altarFlashing",
			name="Flashing",
			description = "The flashing color for the altar bar",
			section = altar
	)
	default Color altarFlashing() {
		return new Color(192, 157,69,255);
	}


	@ConfigItem(
			keyName = "goBank",
			name="Go Bank",
			description = "Whether or not the inventory is full",
			section = screenBlasters
	)
	default boolean goBank() {
		return true;
	}

	@ConfigItem(
			keyName = "treasureNear",
			name="Treasure Near",
			description = "Whether or not there is red treasure near",
			section = screenBlasters
	)
	default boolean treasureNear() {
		return true;
	}

	@ConfigItem(
			keyName = "tooAFKIndicator",
			name = "Too AFK Indicator",
			description = "Tells if the account is not retaliating",
			section = screenBlasters
	)
	default boolean tooAFKIndicator() {
		return true;
	}


	@ConfigItem(
			keyName = "doNearbyDrops",
			name="Nearby Drops",
			description = "Whether or not to list nearby drops",
			section = nearbyDrops
	)
	default boolean doNearbyDrops() {
		return true;
	}

	@ConfigItem(
			keyName = "nearbyThreshold",
			name = "Valuable Threshold",
			description = "Threshold in traded value for nearby drops to be considered valuable",
			section = general
	)
	default int nearbyThreshold() {
		return 6000;
	}

	@ConfigItem(
			keyName = "nearbyBlacklist",
			name = "Valuable Blacklist",
			description = "Items to ignore when their stack value is over the Nearby Threshold (comma separated list of item names, not case sensitive, don't put spaces between commas)",
			section = general
	)
	default String nearbyBlacklist() {
		return "rune dagger,vampyre dust";
	}

	@Range(
			min=0
	)
	@ConfigItem(
			keyName = "nearbyRange",
			name = "Range",
			description = "The range, in tiles, to search for new nearby items in (maximum distance along an axis)",
			section = nearbyDrops
	)
	default int nearbyRange() { return 100; }

	@ConfigItem(
			keyName = "customBlushard",
			name = "Shard EarBlaster",
			description = "Place file in '%userprofile%\\.runelite\\qolting\\shard.wav' on windows",
			section = earBlasters
	)
	default boolean customBlushard() {
		return true;
	}

	@ConfigItem(
			keyName = "customOnItsBoltTips",
			name = "Onyx EarBlaster",
			description = "Place file in '%userprofile%\\.runelite\\qolting\\onyx.wav' on windows",
			section = earBlasters
	)
	default boolean customOnItsBoltTips() {
		return true;
	}

	@ConfigItem(
			keyName = "customYoink",
			name = "Yoink EarBlaster",
			description = "Place file in '%userprofile%\\.runelite\\qolting\\yoink.wav' on windows",
			section = earBlasters
	)
	default boolean customYoink() {
		return true;
	}

	@ConfigItem(
			keyName = "customPrayer",
			name = "Prayer EarBlaster",
			description = "Place file in '%userprofile%\\.runelite\\qolting\\prayer.wav' on windows",
			section = earBlasters
	)
	default boolean customPrayer() {
		return true;
	}

	@ConfigItem(
			keyName = "customLowHP",
			name = "Low HP Blaster",
			description = "Place file in '%userprofile%\\.runelite\\qolting\\health.wav' on windows",
			section = earBlasters
	)
	default boolean customLowHP() {
		return true;
	}


	@ConfigItem(
			keyName = "customRegularDrops",
			name = "Regular Drop EarBlaster",
			description = "Place file in '%userprofile%\\.runelite\\qolting\\regularDrop.wav' on windows, only works for items over 'Valuable Threshold' threshold set in General!",
			section = earBlasters
	)
	default boolean customRegularDrops() {
		return true;
	}

	@Range(
			min = 1,
			max = 100
	)
	@ConfigItem(
			keyName = "numLoops",
			name = "Play Blasters (#)",
			description = "How many times to play an Ear Blaster",
			section = earBlasters,
			position = -3
	)
	default int loopBlasters() { return 1; }

	@ConfigItem(
			keyName = "infLoop",
			name = "Loop Shard/Onyx",
			description = "Loop the ear blaster until a very valuable item has been picked up [overrides 'Play Blasters (#)']",
			section = earBlasters,
			position = -2
	)
	default boolean loopUntil() { return false; }

	@ConfigItem(
			keyName = "useAccountTracker",
			name = "Use Account Tracker",
			description = "Whether or not to save info to the account tracker at %userprofile%/.runelite/qolting/ (for many functionalities of the plugin this is recommended)",
			section = tracker
	)
	default boolean useAccountTracker() { return true; }

	@ConfigItem(
			keyName = "launchAccountTracker",
			name = "Launch Account Tracker",
			description = "Opens the account tracker window with this client as its parent",
			section = tracker,
			position = -2
	)
	default boolean launchAccountTracker() { return false; }


	@ConfigItem(
			keyName = "alwaysOnTopTracker",
			name = "Always On Top",
			description = "Makes the account tracker always-on-top",
			section = tracker
	)
	default boolean alwaysOnTopTracker() { return false; }
}
