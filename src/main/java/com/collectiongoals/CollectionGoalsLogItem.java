package com.collectiongoals;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
public class CollectionGoalsLogItem {
    private final int id;
    private final boolean obtained;
    private final int numberObtained;
    private final int killCount;
    private final int alternateKillCount;//challenge mode, etc.

    public CollectionGoalsLogItem(int id) {
        this.id = id;
        this.obtained = false;
        this.numberObtained = -1;
        this.killCount = -1;
        this.alternateKillCount = -1;
    }

    public CollectionGoalsLogItem(int id, int numberObtained, int killCount) {
        this.id = id;
        this.obtained = (numberObtained>0) ? true : false;
        this.numberObtained = numberObtained;
        this.killCount = killCount;
        this.alternateKillCount = -1;
    }

    public CollectionGoalsLogItem(int id, int numberObtained, int killCount, int alternateKillCount) {
        this.id = id;
        this.obtained = (numberObtained>0) ? true : false;
        this.numberObtained = numberObtained;
        this.killCount = killCount;
        this.alternateKillCount = alternateKillCount;
    }


}
