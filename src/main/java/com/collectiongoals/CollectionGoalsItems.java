package com.collectiongoals;

import java.util.ArrayList;
import java.util.List;

public class CollectionGoalsItems {

    static CollectionGoalsItem ZAMORAKIAN_SPEAR = new CollectionGoalsItem("Zamorakian spear", -1, "K'ril Tsutsaroth", 1f/127);
    static List<CollectionGoalsItem> ALL_ITEMS = new ArrayList<CollectionGoalsItem>() {};

    static{
        ALL_ITEMS.add(ZAMORAKIAN_SPEAR);

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
