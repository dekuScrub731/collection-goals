package com.collectiongoals;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class CollectionGoalsItem {

private String name;
private int id;
private List<CollectionGoalsSource> sources;

    public CollectionGoalsItem(String name, int id, String dropSource, double dropRate)
    {
        this.name = name;
        this.id = id;
        List<CollectionGoalsSource> source =  new ArrayList<CollectionGoalsSource>() {} ;
        source.add(new CollectionGoalsSource(dropSource, dropRate));
        this.sources = source;
    }

}
