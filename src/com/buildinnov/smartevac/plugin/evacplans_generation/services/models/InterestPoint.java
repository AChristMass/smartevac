package com.buildinnov.smartevac.plugin.evacplans_generation.services.models;


import org.tinfour.common.Vertex;

public class InterestPoint {


    private Vertex vertex;
    boolean isOpeningElement;
    boolean isLevelExit;
    boolean isPrincipalExit;
    private String globalId;
    private String type; //IfcSpace, IfcDoor, IfcStair
    private SmartEvacElement associatedElement;

    //boolean isCompound => type is space
    //Navigation graph
    //HipsterDirectedGraph<InterestPoint,IndoorDistance>


    public void setGlobalId(String globalId) {
        this.globalId = globalId;
    }

    public void setVertex(Vertex vertex) {
        this.vertex = vertex;
    }

    public void setOpeningElement(boolean openingElement) {
        isOpeningElement = openingElement;
    }

    public boolean isLevelExit() {
        return isLevelExit;
    }

    public void setLevelExit(boolean levelExit) {
        isLevelExit = levelExit;
    }

    public boolean isPrincipalExit() {
        return isPrincipalExit;
    }

    public void setPrincipalExit(boolean principalExit) {
        isPrincipalExit = principalExit;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public SmartEvacElement getAssociatedElement() {
        return associatedElement;
    }

    public void setAssociatedElement(SmartEvacElement associatedElement) {
        this.associatedElement = associatedElement;
    }


    public InterestPoint(String globalId, Vertex vertex, boolean isOpeningElement) {
        this.globalId = globalId;
        this.vertex = vertex;
        this.isOpeningElement = isOpeningElement;
    }

    public InterestPoint(Vertex vertex, boolean isOpeningElement) {
        this.vertex = vertex;
        this.isOpeningElement = isOpeningElement;
    }

    public String getGlobalId() {
        return globalId;
    }

    public Vertex getVertex() {
        return vertex;
    }

    public boolean isOpeningElement() {
        return isOpeningElement;
    }


    @Override
    public boolean equals(Object obj) {
        return this.getGlobalId().equals(  ((InterestPoint)obj).globalId );
    }

    @Override
    public int hashCode() {
        return 100;
    }
}
