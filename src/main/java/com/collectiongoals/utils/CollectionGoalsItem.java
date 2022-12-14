package com.collectiongoals.utils;


import static com.collectiongoals.CollectionGoalsPlugin.ALL_ITEMS;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CollectionGoalsItem
{
	private String name;
	private int id;
	private List<CollectionGoalsSource> sources;
	private String rateString;
	private List<CollectionGoalsLogItem> userLogData;

	// Added from plugin (single source)
	public CollectionGoalsItem(String name, int id, String dropSource, String dropRate)
	{
		this.name = name;
		this.id = id;
		List<CollectionGoalsSource> source = new ArrayList<CollectionGoalsSource>()
		{
		};
		source.add(new CollectionGoalsSource(dropSource, dropRate));
		this.sources = source;
		this.rateString = dropRate;
		List<CollectionGoalsLogItem> userData = new ArrayList<CollectionGoalsLogItem>()
		{
		};
		userData.add(new CollectionGoalsLogItem(id, dropSource));
		this.userLogData = userData;
	}

	// Added from plugin (multiple sources)
	public CollectionGoalsItem(String name, int id, List<CollectionGoalsSource> sources)
	{
		this.name = name;
		this.id = id;
		this.sources = sources;

		if (sources.size() == 1)
		{
			this.rateString = sources.get(0).getRate();
		}
		else
		{
			this.rateString = "Multiple Sources";
		}


		List<CollectionGoalsLogItem> userData = new ArrayList<CollectionGoalsLogItem>()
		{
		};
		for (CollectionGoalsSource source : sources)
		{
			userData.add(new CollectionGoalsLogItem(id, source.getName()));
		}
		this.userLogData = userData;
	}

	// Loaded from Data Manager
	public CollectionGoalsItem(int id, List<CollectionGoalsLogItem> userLogData)
	{
		this.id = id;
		this.userLogData = userLogData;
		for (CollectionGoalsItem item : ALL_ITEMS)
		{
			if (item.getId() == id)
			{
				this.name = item.getName();
				this.sources = item.getSources();
				if (item.getSources().size() == 1)
				{
					this.rateString = item.getSources().get(0).getRate();
				}
				else
				{
					this.rateString = "Multiple Sources";
				}
				break;
			}
		}
	}


	public boolean isObtained()
	{
		for (CollectionGoalsLogItem logItem : this.userLogData)
		{
			if (logItem.isObtained())
			{
				return true;
			}
		}
		return false;
	}


}
