package com.buildinnov.smartevac.plugin.evacplans_generation.services.models;

import org.bimserver.models.ifc4.IfcSpace;

import java.util.ArrayList;
import java.util.List;

public class SmartEvacDoor {
    private String doorGlobalId;
    private String doorName;
    private List<IfcSpace> associatedSpaces = new ArrayList<>();

    public SmartEvacDoor(String doorGlobalId, String doorName) {
        this.doorGlobalId = doorGlobalId;
        this.doorName = doorName;
    }

    public String getDoorGlobalId() {
        return doorGlobalId;
    }

    public void setDoorGlobalId(String doorGlobalId) {
        this.doorGlobalId = doorGlobalId;
    }

    public String getDoorName() {
        return doorName;
    }

    public void setDoorName(String doorName) {
        this.doorName = doorName;
    }

    public List<IfcSpace> getAssociatedSpaces() {
        return associatedSpaces;
    }


}
