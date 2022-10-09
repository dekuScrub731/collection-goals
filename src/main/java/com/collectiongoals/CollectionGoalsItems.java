package com.collectiongoals;

import net.runelite.api.ItemID;

import java.util.ArrayList;
import java.util.List;


public class CollectionGoalsItems {


    static List<CollectionGoalsItem> ALL_ITEMS = new ArrayList<CollectionGoalsItem>() {};

    static {
        ALL_ITEMS.add(new CollectionGoalsItem("Zamorakian spear", ItemID.ZAMORAKIAN_SPEAR, "K'ril Tsutsaroth", "1/20"));
        ALL_ITEMS.add(new CollectionGoalsItem("Bandos tassets", ItemID.BANDOS_TASSETS, "General Graardor", "1/381"));

    }



    public static CollectionGoalsItem getItemByName(String name) {
        for (CollectionGoalsItem item : ALL_ITEMS) {
            if (item.getName().equals(name)) {
                return item;
            }
        }
        return null;
    }




}
