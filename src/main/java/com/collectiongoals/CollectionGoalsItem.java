package com.collectiongoals;


import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

import static com.collectiongoals.CollectionGoalsItems.ALL_ITEMS;

@Getter
@Setter
public class CollectionGoalsItem {

private String name;
private int id;
private List<CollectionGoalsSource> sources;
private String rateString;
private List<CollectionGoalsLogItem> userLogData;

    // This constructor is used when adding a new item (single source)
    // Log data will be blank until the user loads the collection log
    public CollectionGoalsItem(String name, int id, String dropSource, String dropRate)
    {
        this.name = name;
        this.id = id;
        List<CollectionGoalsSource> source =  new ArrayList<CollectionGoalsSource>() {} ;
        source.add(new CollectionGoalsSource(dropSource, dropRate));
        this.sources = source;
        this.rateString = dropRate;
        List<CollectionGoalsLogItem> userData = new ArrayList<CollectionGoalsLogItem>() {} ;
        userData.add(new CollectionGoalsLogItem(id, dropSource));
        this.userLogData = userData;
    }

    // This constructor is used when adding a new item (multiple sources)
    // Log data will be blank until the user loads the collection log
    public CollectionGoalsItem(String name, int id, List<CollectionGoalsSource> sources)
    {
        this.name = name;
        this.id = id;
        this.sources = sources;
        this.rateString = "Multiple";

        List<CollectionGoalsLogItem> userData = new ArrayList<CollectionGoalsLogItem>() {} ;
        for (CollectionGoalsSource source : sources) {
            userData.add(new CollectionGoalsLogItem(id, source.getName()));
        }
        this.userLogData = userData;
    }










    // This method is used to load data on startup/launch
    // (including any log data that may have been saved)
    public CollectionGoalsItem(int id, List<CollectionGoalsLogItem> userLogData) {
        this.id = id;
        this.userLogData = userLogData;
        for (CollectionGoalsItem item : ALL_ITEMS) {
            if (item.getId() == id) {
                this.name = item.getName();
                this.sources = item.getSources();
                if (item.getSources().size()==1) {
                    this.rateString = item.getSources().get(0).getRate();
                }
                else {
                    this.rateString = "Multiple Sources";
                }
                break;
            }
        }
    }








}
