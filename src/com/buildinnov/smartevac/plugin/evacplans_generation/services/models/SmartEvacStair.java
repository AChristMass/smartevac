package com.buildinnov.smartevac.plugin.evacplans_generation.services.models;

public class SmartEvacStair implements SmartEvacElement {

    private String stairGlobalId;
    private  String stairName;
    private SmartEvacSpace upperLevelSpace;
    private SmartEvacSpace lowerLevelSpace;

    public SmartEvacStair(String stairGlobalId, String stairName) {
        this.stairGlobalId = stairGlobalId;
        this.stairName = stairName;
    }

    public String getStairGlobalId() {
        return stairGlobalId;
    }

    public void setStairGlobalId(String stairGlobalId) {
        this.stairGlobalId = stairGlobalId;
    }

    public String getStairName() {
        return stairName;
    }

    public void setStairName(String stairName) {
        this.stairName = stairName;
    }

    public SmartEvacSpace getUpperLevelSpace() {
        return upperLevelSpace;
    }

    public void setUpperLevelSpace(SmartEvacSpace upperLevelSpace) {
        this.upperLevelSpace = upperLevelSpace;
    }

    public SmartEvacSpace getLowerLevelSpace() {
        return lowerLevelSpace;
    }

    public void setLowerLevelSpace(SmartEvacSpace lowerLevelSpace) {
        this.lowerLevelSpace = lowerLevelSpace;
    }
}
