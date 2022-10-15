package com.collectiongoals;

import static com.collectiongoals.CollectionGoalsPlugin.CONFIG_GROUP;

import java.awt.Color;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup(CONFIG_GROUP)
public interface CollectionGoalsConfig extends Config
{

	@Getter
	@RequiredArgsConstructor
	enum progressMethod
	{
		DROP_RATE("Drop Rate"),
		DROP_CHANCE("Drop Chance");
		private final String value;
	}

	@ConfigItem(
		keyName = "progressMethod",
		name = "Progress Method",
		description = "Determines how progress is calculated",
		position = 1
	)
	default progressMethod progressMethod()
	{
		return progressMethod.DROP_RATE;
	}

	@ConfigItem(
		keyName = "underRateColor",
		name = "Under Rate Color",
		description = "The color of panel items that are under drop rate",
		position = 2
	)
	default Color underRateColor()
	{
		return new Color(80, 80, 80);
	}

	@ConfigItem(
		keyName = "overRateColor",
		name = "Over Rate Color",
		description = "The color of panel items that are over drop rate",
		position = 3
	)
	default Color overRateColor()
	{
		return new Color(110, 110, 0);
	}

	@ConfigItem(
		keyName = "twiceRateColor",
		name = "Twice Rate Color",
		description = "The color of panel items that are over twice the drop rate",
		position = 4
	)
	default Color twiceRateColor()
	{
		return new Color(100, 0, 0);
	}

	@ConfigItem(
		keyName = "completeColor",
		name = "Complete Color",
		description = "The color of panel items that are complete",
		position = 5
	)
	default Color completeColor()
	{
		return new Color(10, 90, 40);
	}

}
