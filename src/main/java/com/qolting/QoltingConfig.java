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
				position = 0
	)
	String general = "general";

	@ConfigSection(
				name = "Altar",
				description = "Related to the Altar",
				position = 1
	)
	String altar = "altar";

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

	@Range(
			max=99
	)
	@ConfigItem(
			keyName = "altarThreshold",
			name = "Threshold",
			description = "The threshold for the altar bar",
			section = altar,
			position = 1
	)
	default int altarThreshold() {
		return 5;
	}

	@ConfigItem(
			keyName = "altarSize",
			name = "Size",
			description = "The size for the altar bar",
			section = altar,
			position = 2
	)
	default int altarSize() {
		return 25;
	}

	@ConfigItem(
			keyName = "altarOutline",
			name = "Outline",
			description = "Whether or not to display the white outline",
			section = altar,
			position = 3
	)
	default boolean altarOutline() {
		return true;
	}

	@ConfigItem(
			keyName = "altarPrayer",
			name = "Display Prayer",
			description = "Whether or not to display prayer",
			section = altar,
			position = 4
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
		return new Color(0,0,0,0);
	}

	@Alpha
	@ConfigItem(
			keyName="altarForeground",
			name="Foreground",
			description = "The foreground for the altar bar",
			section = altar
	)
	default Color altarForeground() {
		return new Color(38,63,62,0);
	}

	@Alpha
	@ConfigItem(
			keyName="altarForegroundOff",
			name="Foreground Off",
			description = "The foreground for the altar bar when off",
			section = altar
	)
	default Color altarForegroundOff() {
		return new Color(0, 5, 10,0);
	}


	@Alpha
	@ConfigItem(
			keyName="altarForegroundLow",
			name="Foreground Low",
			description = "The foreground for the altar bar when below threshold",
			section = altar
	)
	default Color altarForegroundLow() {
		return new Color(83,55,29,0);
	}

	@Alpha
	@ConfigItem(
			keyName = "altarFlashing",
			name="Flashing",
			description = "The flashing color for the altar bar",
			section = altar
	)
	default Color altarFlashing() {
		return new Color(192, 157,69,0);
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
			name = "Threshold",
			description = "Threshold for nearby drops in traded value",
			section = nearbyDrops
	)
	default int nearbyThreshold() {
		return 12000;
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
			name = "Custom Blushard EarBlaster",
			description = "Place file in '%userprofile%\\.runelite\\blushard.wav' on windows",
			section = earBlasters
	)
	default boolean customBlushard() {
		return true;
	}

	@ConfigItem(
			keyName = "customOnItsBoltTips",
			name = "Custom OnIts Bolt Tip EarBlaster",
			description = "Place file in '%userprofile%\\.runelite\\onitsbolttips.wav' on windows",
			section = earBlasters
	)
	default boolean customOnItsBoltTips() {
		return true;
	}

	@ConfigItem(
			keyName = "customYoink",
			name = "Custom Yoink EarBlaster",
			description = "Place file in '%userprofile%\\.runelite\\yoink.wav' on windows",
			section = earBlasters
	)
	default boolean customYoink() {
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
			section = earBlasters
	)
	default int loopBlasters() { return 1; }


	@ConfigItem(
			keyName = "useAccountTracker",
			name = "Use Account Tracker",
			description = "Whether or not to save info to the account tracker at %userprofile%/.runelite/qolting/",
			section = tracker
	)
	default boolean useAccountTracker() { return false; }

	@ConfigItem(
			keyName = "launchAccountTracker",
			name = "Launch Account Tracker",
			description = "Opens the account tracker window with this client as its parent",
			section = tracker
	)
	default boolean launchAccountTracker() { return false; }
}
