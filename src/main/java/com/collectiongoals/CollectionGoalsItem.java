package com.collectiongoals;

import lombok.AllArgsConstructor;
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

    public CollectionGoalsItem(String name, int id, List<CollectionGoalsSource> sources)
    {
        this.name = name;
        this.id = id;
        this.sources = sources;
        String rateString = "";
        for (int i=0; i<sources.size(); i++) {
            if (i==0) {
                rateString = sources.get(i).getRate();
            }
            else {
                rateString += "; " + sources.get(i).getRate();
            }
        }
        this.rateString = rateString;
    }

    public CollectionGoalsItem(String name, int id, String dropSource, String dropRate)
    {
        this.name = name;
        this.id = id;
        List<CollectionGoalsSource> source =  new ArrayList<CollectionGoalsSource>() {} ;
        source.add(new CollectionGoalsSource(dropSource, dropRate));
        this.sources = source;
        this.rateString = dropRate;
    }

    public CollectionGoalsItem(int id) {
        for (CollectionGoalsItem item : ALL_ITEMS) {
            if (item.getId()==id){
                this.name = item.getName();
                this.id = item.getId();
                this.sources = item.getSources();

                if (item.getSources().size()==1) {
                    this.rateString = item.getSources().get(0).getRate();
                }

                break;
            }
        }
    }
}
