package com.collectiongoals.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import net.runelite.api.ItemID;

public class CollectionGoalsDefinitionList
{
	public static List<CollectionGoalsDefinition> ALL_DEFINITIONS = new ArrayList<CollectionGoalsDefinition>()
	{
	};

	static
	{


		ALL_DEFINITIONS.add(new CollectionGoalsDefinition("Abyssal whip", ItemID.ABYSSAL_WHIP, "Abyssal demon", "1/512"));
		ALL_DEFINITIONS.add(new CollectionGoalsDefinition("Abyssal whip", ItemID.ABYSSAL_WHIP, "Greater abyssal demon", "1/512"));
		ALL_DEFINITIONS.add(new CollectionGoalsDefinition("Abyssal whip", ItemID.ABYSSAL_WHIP, "Unsired", "12/128"));


		ALL_DEFINITIONS.add(new CollectionGoalsDefinition("Pet general graardor", ItemID.PET_GENERAL_GRAARDOR, "General Graardor", "1/5000"));
		ALL_DEFINITIONS.add(new CollectionGoalsDefinition("Bandos chestplate", ItemID.BANDOS_CHESTPLATE, "General Graardor", "1/381"));
		ALL_DEFINITIONS.add(new CollectionGoalsDefinition("Staff of the dead", ItemID.STAFF_OF_THE_DEAD, "K'ril Tsutsaroth", "1/508"));
		ALL_DEFINITIONS.add(new CollectionGoalsDefinition("Zamorakian spear", ItemID.ZAMORAKIAN_SPEAR, "K'ril Tsutsaroth", "1/127"));


		ALL_DEFINITIONS.add(new CollectionGoalsDefinition("Occult necklace", ItemID.OCCULT_NECKLACE, "Nuclear smoke devil", "1/512"));
		ALL_DEFINITIONS.add(new CollectionGoalsDefinition("Occult necklace", ItemID.OCCULT_NECKLACE, "Smoke devil", "1/512"));
		ALL_DEFINITIONS.add(new CollectionGoalsDefinition("Occult necklace", ItemID.OCCULT_NECKLACE, "Thermonuclear smoke devil", "1/350"));

		ALL_DEFINITIONS.add(new CollectionGoalsDefinition("Steam battlestaff", ItemID.STEAM_BATTLESTAFF, "K'ril Tsutsaroth", "1/127"));
		ALL_DEFINITIONS.add(new CollectionGoalsDefinition("Zamorak hilt", ItemID.ZAMORAK_HILT, "K'ril Tsutsaroth", "1/508"));

		//Test an item with a single source
		ALL_DEFINITIONS.add(new CollectionGoalsDefinition("Abyssal orphan", ItemID.ABYSSAL_ORPHAN, "Unsired", "5/128"));

		//Test an item with multiple sources
		ALL_DEFINITIONS.add(new CollectionGoalsDefinition("Dragon pickaxe", ItemID.DRAGON_PICKAXE, "Callisto", "1/170.7"));
		ALL_DEFINITIONS.add(new CollectionGoalsDefinition("Dragon pickaxe", ItemID.DRAGON_PICKAXE, "Chaos Elemental", "1/256"));
		ALL_DEFINITIONS.add(new CollectionGoalsDefinition("Dragon pickaxe", ItemID.DRAGON_PICKAXE, "King Black Dragon", "1/1500"));
		ALL_DEFINITIONS.add(new CollectionGoalsDefinition("Dragon pickaxe", ItemID.DRAGON_PICKAXE, "Venenatis", "1/170.7"));
		ALL_DEFINITIONS.add(new CollectionGoalsDefinition("Dragon pickaxe", ItemID.DRAGON_PICKAXE, "Vet'ion", "1/170.7"));

	}

	public static List<CollectionGoalsItem> getCollectionGoalsAllItemList(List<CollectionGoalsDefinition> collectionGoalsDefinitionList)
	{
		List<CollectionGoalsItem> collectionGoalsItems = new ArrayList<CollectionGoalsItem>();
/*
		collectionGoalsDefinitionList.stream().collect(Collectors.groupingBy(CollectionGoalsDefinition::getId)).values().stream()
			.filter(itemsWithMultipleSources -> itemsWithMultipleSources.size() == 1)
			.forEach(i -> {
				collectionGoalsItems.add(new CollectionGoalsItem(i.get(0).getName(), i.get(0).getId(), i.get(0).getDropSource(), i.get(0).getRateString()));
			});
*/
		collectionGoalsDefinitionList.stream().collect(Collectors.groupingBy(CollectionGoalsDefinition::getId)).values().stream()
			//.filter(itemsWithMultipleSources -> itemsWithMultipleSources.size() > 1)
			.forEach(i -> {
				List<CollectionGoalsSource> tempSources = new ArrayList<CollectionGoalsSource>();
				String tempName = i.get(0).getName();
				int tempId = i.get(0).getId();
				for (CollectionGoalsDefinition definition : i)
				{
					tempSources.add(new CollectionGoalsSource(definition.getDropSource(), definition.getRateString()));
				}
				collectionGoalsItems.add(new CollectionGoalsItem(tempName, tempId, tempSources));
			});

		return collectionGoalsItems;
	}

}
