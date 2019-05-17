package com.buildinnov.smartevac.plugin.evacplans_generation.services.models;

import java.util.ArrayList;
import java.util.List;

public class SmartEvacSpace implements SmartEvacElement{
    private String spaceGlobalId;
    private String spaceName;
    private List<SmartEvacDoor> doors = new ArrayList<>();
    private List<SmartEvacSpace> neighbours = new ArrayList<>();
    private List<SmartEvacStair> stairs = new ArrayList<>();


    public SmartEvacSpace(String spaceGlobalId, String spaceName) {
        this.spaceGlobalId = spaceGlobalId;
        this.spaceName = spaceName;
    }


    public List<SmartEvacStair> getStairs() {
        return stairs;
    }

    public void setSpaceGlobalId(String spaceGlobalId) {
        this.spaceGlobalId = spaceGlobalId;
    }

    public void setSpaceName(String spaceName) {
        this.spaceName = spaceName;
    }

    public String getSpaceGlobalId() {
        return spaceGlobalId;
    }

    public String getSpaceName() {
        return spaceName;
    }

    public List<SmartEvacDoor> getDoors() {
        return doors;
    }

    public List<SmartEvacSpace> getNeighbours() {
        List<SmartEvacSpace> smartEvacSpaces = new ArrayList<>();
        for(SmartEvacDoor door : this.getDoors())
            for(SmartEvacSpace evacSpace : door.getAssociatedSpaces())
                if(evacSpace!=this)
                    smartEvacSpaces.add(evacSpace);
        return smartEvacSpaces;
    }
}
