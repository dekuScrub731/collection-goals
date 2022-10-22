package com.collectiongoals.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class CollectionGoalsGroupSort
{
	//TODO - Use this to crosswalk user log data to group/sort panels
	private int id;
	private String group;
	private int sortOrder;

	public CollectionGoalsGroupSort(int id)
	{
		this.id = id;
		this.group = "";
		this.sortOrder = -1;
	}

}
