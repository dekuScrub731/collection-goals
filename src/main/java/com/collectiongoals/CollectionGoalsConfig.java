package com.collectiongoals;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

import static com.collectiongoals.CollectionGoalsPlugin.CONFIG_GROUP;

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



}
