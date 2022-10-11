package com.collectiongoals;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.game.ItemManager;
import net.runelite.client.util.AsyncBufferedImage;

import javax.inject.Inject;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static com.collectiongoals.CollectionGoalsPlugin.CONFIG_GROUP;

@Slf4j
public class CollectionGoalsDataManager
{
    private static final String CONFIG_KEY_ITEMIDS = "userLogData";

    private final CollectionGoalsPlugin plugin;
    private final ConfigManager configManager;
    private final ItemManager itemManager;
    private final Gson gson;


    private final Type userLogDataType = new TypeToken<ArrayList<CollectionGoalsLogItem>>(){}.getType();
    private List<CollectionGoalsLogItem> userLogData = new ArrayList<>();

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
        String itemsJson = configManager.getConfiguration(CONFIG_GROUP, CONFIG_KEY_ITEMIDS);

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
    }

    public void saveData()
    {
        userLogData.clear();
        for (CollectionGoalsItem item : plugin.getItems())
        {
            for (CollectionGoalsLogItem logItem : item.getUserLogData()) {
                userLogData.add(logItem);
            }
        }
        final String itemsJson = gson.toJson(userLogData);
        configManager.setConfiguration(CONFIG_GROUP, CONFIG_KEY_ITEMIDS, itemsJson);
    }

    private void convertIds()
    {
        List<CollectionGoalsLogItem> tempLogItems = new ArrayList<>();
        List<CollectionGoalsItem> collectionItems = new ArrayList<>();
        List<Integer> collectionItemIDs = new ArrayList<>();

        //build a list of the item IDs from log data
        for (CollectionGoalsLogItem logItem : userLogData) {
            if (!collectionItemIDs.contains(logItem.getId())) {
                collectionItemIDs.add(logItem.getId());
            } else {
                log.info("Data already has " + logItem.getId());//TODO remove
            }
        }

        //nested loop to build
        for (int itemID : collectionItemIDs) {
            for (CollectionGoalsLogItem logItem : userLogData) {
                if (logItem.getId() == itemID) {
                    tempLogItems.add(logItem);
                }
            }
            collectionItems.add(new CollectionGoalsItem(itemID, tempLogItems));
            tempLogItems.clear();
        }
        plugin.setItems(collectionItems);
    }
}
