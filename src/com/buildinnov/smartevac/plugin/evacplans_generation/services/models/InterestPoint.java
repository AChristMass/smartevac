package com.buildinnov.smartevac.plugin.evacplans_generation.services.models;

import org.tinfour.common.Vertex;

public class InterestPoint {

    private Vertex vertex;
    boolean isOpeningElement;
    boolean isLevelExit;
    boolean isPrincipalExit;
    private String type; //IfcSpace, IfcDoor, IfcStair

    //boolean isCompound => type is space
    //Navigation graph
    //HipsterDirectedGraph<InterestPoint,IndoorDistance>




    public InterestPoint(Vertex vertex, boolean isOpeningElement) {
        this.vertex = vertex;
        this.isOpeningElement = isOpeningElement;
    }

    public Vertex getVertex() {
        return vertex;
    }

    public boolean isOpeningElement() {
        return isOpeningElement;
    }
}
