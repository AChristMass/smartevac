package com.buildinnov.smartevac.plugin.evacplans_generation.services.models;

import java.util.List;

public class SmartEvacSpace {
    private String spaceGlobalId;
    private String spaceName;
    private List<SmartEvacDoor> doors;
    private List<SmartEvacSpace> neighbours;
}
