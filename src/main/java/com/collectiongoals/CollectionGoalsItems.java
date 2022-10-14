package com.collectiongoals;

import net.runelite.api.ItemID;

import java.util.ArrayList;
import java.util.List;


public class CollectionGoalsItems {


    static List<CollectionGoalsItem> ALL_ITEMS = new ArrayList<CollectionGoalsItem>() {};

    static {

        // GOD WARS DUNGEON
        ALL_ITEMS.add(new CollectionGoalsItem("Pet zilyana", ItemID.PET_ZILYANA, "Commander Zilyana", "1/5000"));
        ALL_ITEMS.add(new CollectionGoalsItem("Armadyl crossbow", ItemID.ARMADYL_CROSSBOW, "Commander Zilyana", "1/508"));
        ALL_ITEMS.add(new CollectionGoalsItem("Saradomin hilt", ItemID.SARADOMIN_HILT, "Commander Zilyana", "1/508"));
        ALL_ITEMS.add(new CollectionGoalsItem("Saradomin sword", ItemID.SARADOMIN_SWORD, "Commander Zilyana", "1/127"));
        ALL_ITEMS.add(new CollectionGoalsItem("Saradomin's light", ItemID.SARADOMINS_LIGHT, "Commander Zilyana", "1/254"));

        ALL_ITEMS.add(new CollectionGoalsItem("Pet general graardor", ItemID.PET_GENERAL_GRAARDOR, "General Graardor", "1/5000"));
        ALL_ITEMS.add(new CollectionGoalsItem("Bandos chestplate", ItemID.BANDOS_CHESTPLATE, "General Graardor", "1/381"));
        ALL_ITEMS.add(new CollectionGoalsItem("Bandos tassets", ItemID.BANDOS_TASSETS, "General Graardor", "1/381"));
        ALL_ITEMS.add(new CollectionGoalsItem("Bandos boots", ItemID.BANDOS_BOOTS, "General Graardor", "1/381"));
        ALL_ITEMS.add(new CollectionGoalsItem("Bandos hilt", ItemID.BANDOS_HILT, "General Graardor", "1/508"));

        ALL_ITEMS.add(new CollectionGoalsItem("Pet kree'arra", ItemID.PET_KREEARRA, "Kree'arra", "1/5000"));
        ALL_ITEMS.add(new CollectionGoalsItem("Armadyl helmet", ItemID.ARMADYL_HELMET, "Kree'arra", "1/381"));
        ALL_ITEMS.add(new CollectionGoalsItem("Armadyl chestplate", ItemID.ARMADYL_CHESTPLATE, "Kree'arra", "1/381"));
        ALL_ITEMS.add(new CollectionGoalsItem("Armadyl chainskirt", ItemID.ARMADYL_CHAINSKIRT, "Kree'arra", "1/381"));
        ALL_ITEMS.add(new CollectionGoalsItem("Armadyl hilt", ItemID.ARMADYL_HILT, "Kree'arra", "1/508"));

        ALL_ITEMS.add(new CollectionGoalsItem("Pet k'ril tsutsaroth", ItemID.PET_KRIL_TSUTSAROTH, "K'ril Tsutsaroth", "1/5000"));
        ALL_ITEMS.add(new CollectionGoalsItem("Staff of the dead", ItemID.STAFF_OF_THE_DEAD, "K'ril Tsutsaroth", "1/508"));
        ALL_ITEMS.add(new CollectionGoalsItem("Zamorakian spear", ItemID.ZAMORAKIAN_SPEAR, "K'ril Tsutsaroth", "1/127"));
        ALL_ITEMS.add(new CollectionGoalsItem("Steam battlestaff", ItemID.STEAM_BATTLESTAFF, "K'ril Tsutsaroth", "1/127"));
        ALL_ITEMS.add(new CollectionGoalsItem("Zamorak hilt", ItemID.ZAMORAK_HILT, "K'ril Tsutsaroth", "1/508"));






        // MULTIPLE SOURCES
        //ALL_ITEMS.add(new CollectionGoalsItem("Dragon pickaxe", ItemID.DRAGON_PICKAXE, "Multiple", "Multiple Sources"));



        ALL_ITEMS.add(new CollectionGoalsItem("Dragon pickaxe", ItemID.DRAGON_PICKAXE, new ArrayList<CollectionGoalsSource>() {{
            add(new CollectionGoalsSource("Callisto", "1/170.7"));
            add(new CollectionGoalsSource("Venenatis", "1/170.7"));
            add(new CollectionGoalsSource("Vet'ion", "1/170.7"));
            add(new CollectionGoalsSource("Chaos Elemental", "1/256"));
            add(new CollectionGoalsSource("King Black Dragon", "1/1500"));
        }}));


    }



    public static CollectionGoalsItem getBaseItemByName(String name) {
        for (CollectionGoalsItem item : ALL_ITEMS) {
            if (item.getName().equals(name)) {
                return item;
            }
        }
        return null;
    }




}
