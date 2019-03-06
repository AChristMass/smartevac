package com.buildinnov.smartevac.plugin.evacplans_generation.services.models;

import org.tinfour.common.Vertex;

public class InterestPoint {
    private Vertex vertex;
    boolean isOpeningElement;

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
