package com.collectiongoals.utils;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CollectionGoalsLogItem
{
	private int id;
	private String source;
	private boolean obtained;
	private int numberObtained;
	private int killCount;
	private int alternateKillCount;//challenge mode, etc.

	public CollectionGoalsLogItem(int id, String source)
	{
		this.id = id;
		this.source = source;
		this.obtained = false;
		this.numberObtained = -1;
		this.killCount = -1;
		this.alternateKillCount = -1;
	}

	public CollectionGoalsLogItem(int id, String source, int numberObtained, int killCount)
	{
		this.id = id;
		this.source = source;
		this.obtained = (numberObtained > 0) ? true : false;
		this.numberObtained = numberObtained;
		this.killCount = killCount;
		this.alternateKillCount = -1;
	}

	public CollectionGoalsLogItem(int id, String source, int numberObtained, int killCount, int alternateKillCount)
	{
		this.id = id;
		this.source = source;
		this.obtained = (numberObtained > 0) ? true : false;
		this.numberObtained = numberObtained;
		this.killCount = killCount;
		this.alternateKillCount = alternateKillCount;
	}
}
