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
import java.util.HashMap;
import java.util.List;

import static com.collectiongoals.CollectionGoalsPlugin.CONFIG_GROUP;

@Slf4j
public class CollectionGoalsDataManager
{
    private static final String CONFIG_KEY_VALUE = "value";
    private static final String CONFIG_KEY_ITEMIDS = "itemIds";

    private final CollectionGoalsPlugin plugin;
    private final ConfigManager configManager;
    private final ItemManager itemManager;
    private final Gson gson;

    private final Type itemsType = new TypeToken<ArrayList<Integer>>(){}.getType();
    private final Type itemsType2 = new TypeToken<HashMap<Integer, Integer>>(){}.getType();

    private List<Integer> itemIds = new ArrayList<>();
    private HashMap<Integer, Integer> collectionProgress = new HashMap<Integer, Integer>();




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
        //String value = configManager.getConfiguration(CONFIG_GROUP, CONFIG_KEY_VALUE);
        //plugin.setValue(Long.parseLong(value));

        collectionProgress.clear();
        itemIds.clear();

        String itemsJson = configManager.getConfiguration(CONFIG_GROUP, CONFIG_KEY_ITEMIDS);
        String itemsJson2 = configManager.getConfiguration(CONFIG_GROUP, CONFIG_KEY_ITEMIDS+"2");


//original block
        if (itemsJson == null || itemsJson.equals("[]"))
        {
            plugin.setItems(new ArrayList<>());
        }
        else
        {
            try
            {
                itemIds = (gson.fromJson(itemsJson, itemsType));
                convertIds();
            }
            catch (Exception e)
            {
                log.error("Exception occurred while loading purchase progress data", e);
                plugin.setItems(new ArrayList<>());
            }
        }

//new block TODO



    }

    public void saveData()
    {
        //configManager.setConfiguration(CONFIG_GROUP, CONFIG_KEY_VALUE, String.valueOf(plugin.getValue()));

        collectionProgress.clear();
        itemIds.clear();

        for (CollectionGoalsItem item : plugin.getItems())
        {
            itemIds.add(item.getId());
            collectionProgress.put(item.getId(), 0);//TODO: actual number received
        }

        final String itemsJson = gson.toJson(itemIds);
        final String itemsJson2 = gson.toJson(collectionProgress);

        configManager.setConfiguration(CONFIG_GROUP, CONFIG_KEY_ITEMIDS, itemsJson);
        configManager.setConfiguration(CONFIG_GROUP, CONFIG_KEY_ITEMIDS+"2", itemsJson2);
    }

    private void convertIds()
    {
        List<CollectionGoalsItem> collectionItems = new ArrayList<>();

        for (Integer itemId : itemIds)
        {
            collectionItems.add(new CollectionGoalsItem(itemId));
        }

        plugin.setItems(collectionItems);
    }
}
