package com.collectiongoals.utils;

import com.collectiongoals.CollectionGoalsPlugin;
import static com.collectiongoals.CollectionGoalsPlugin.CONFIG_GROUP;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.game.ItemManager;

@Slf4j
public class CollectionGoalsDataManager
{
	private static final String USER_LOG_DATA = "userLogData";
	private static final String GROUP_SORT_DATA = "groupSortData";

	private final CollectionGoalsPlugin plugin;
	private final ConfigManager configManager;
	private final ItemManager itemManager;
	private final Gson gson;


	private final Type userLogDataType = new TypeToken<ArrayList<CollectionGoalsLogItem>>()	{}.getType();
	private List<CollectionGoalsLogItem> userLogData = new ArrayList<>();

	private final Type groupSortDataType = new TypeToken<ArrayList<CollectionGoalsGroupSort>>()	{}.getType();
	private List<CollectionGoalsGroupSort> groupSortData = new ArrayList<>();

	@Inject
	public CollectionGoalsDataManager(CollectionGoalsPlugin plugin, ConfigManager configManager, ItemManager itemManager, Gson gson)
	{
		this.plugin = plugin;
		this.configManager = configManager;
		this.itemManager = itemManager;
		this.gson = gson;
	}

	public void loadData()
	{
		userLogData.clear();
		String itemsJson = configManager.getConfiguration(CONFIG_GROUP, USER_LOG_DATA);

		if (itemsJson == null || itemsJson.equals("[]"))
		{
			plugin.setItems(new ArrayList<>());
		}
		else
		{
			try
			{
				userLogData = (gson.fromJson(itemsJson, userLogDataType));
				convertIds();
			}
			catch (Exception e)
			{
				log.error("Exception occurred while loading purchase progress data", e);
				plugin.setItems(new ArrayList<>());
			}
		}

		groupSortData.clear();
		String groupJson = configManager.getConfiguration(CONFIG_GROUP, GROUP_SORT_DATA);

		if (groupJson == null || groupJson.equals("[]"))
		{
			plugin.setGroupSort(new ArrayList<>());
		}
		else
		{
			try
			{
				groupSortData = (gson.fromJson(groupJson, groupSortDataType));
				plugin.setGroupSort(groupSortData);
			}
			catch (Exception e)
			{
				log.error("Exception occurred while loading purchase progress data", e);
				plugin.setGroupSort(new ArrayList<>());
			}
		}

		logData();
	}

	public void saveData()
	{
		//User Log Data
		userLogData.clear();
		for (CollectionGoalsItem item : plugin.getItems())
		{
			for (CollectionGoalsLogItem logItem : item.getUserLogData())
			{
				userLogData.add(logItem);
			}
		}
		final String itemsJson = gson.toJson(userLogData);
		configManager.setConfiguration(CONFIG_GROUP, USER_LOG_DATA, itemsJson);

		//Group Data
		groupSortData.clear();
		for (CollectionGoalsGroupSort item : plugin.getGroupSort())
		{
			groupSortData.add(item);
		}
		final String groupJson = gson.toJson(groupSortData);
		configManager.setConfiguration(CONFIG_GROUP, GROUP_SORT_DATA, groupJson);
	}

	private void convertIds()
	{
		List<CollectionGoalsItem> collectionItems = new ArrayList<>();
		List<Integer> collectionItemIDs = new ArrayList<>();

		//build a list of the item IDs from log data
		for (CollectionGoalsLogItem logItem : userLogData)
		{
			if (!collectionItemIDs.contains(logItem.getId()))
			{
				collectionItemIDs.add(logItem.getId());
			}
			else
			{
				log.debug("Data already has " + logItem.getId());
			}
		}

		//add the actual items
		for (int itemID : collectionItemIDs)
		{
			collectionItems.add(new CollectionGoalsItem(itemID, buildCollectionGoalsLog(itemID)));
		}
		plugin.setItems(collectionItems);
	}

	private List<CollectionGoalsLogItem> buildCollectionGoalsLog(int itemID)
	{
		List<CollectionGoalsLogItem> tempLogItems = new ArrayList<>();

		for (CollectionGoalsLogItem logItem : userLogData)
		{
			if (logItem.getId() == itemID)
			{
				tempLogItems.add(logItem);
			}
		}

		return updateNumberObtained(tempLogItems);
	}

	//for items that are shared between sources, use the greatest number obtained to prevent stale data
	//This assumes everything is of the same ID
	private List<CollectionGoalsLogItem> updateNumberObtained(List<CollectionGoalsLogItem> originalLogItems)
	{
		int itemID = originalLogItems.get(0).getId();

		List<CollectionGoalsLogItem> tempLogItems = new ArrayList<>();

		int maxQuantity = -1;
		for (CollectionGoalsLogItem item : originalLogItems)
		{
			if (item.getId() != itemID) {
				return originalLogItems;//this method should only be used when the list is of the same ID
			}
			if (item.getNumberObtained() > maxQuantity)
			{
				maxQuantity = item.getNumberObtained();
			}
		}
		for (CollectionGoalsLogItem item : originalLogItems)
		{
			item.setNumberObtained(maxQuantity);
			if (maxQuantity>0) {
				item.setObtained(true);
			}
			tempLogItems.add(item);
		}
		return tempLogItems;
	}

	public void logData() {
		log.info(gson.toJson(userLogData));
		log.info(gson.toJson(groupSortData));
	}

}
