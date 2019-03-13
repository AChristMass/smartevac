package com.buildinnov.smartevac.plugin.evacplans_generation.services;

import org.tinfour.common.Vertex;

public class IndoorDistance {
    Vertex start;
    Vertex end;

    public IndoorDistance(Vertex start, Vertex end) {
        this.start = start;
        this.end = end;
    }

    public double getDistance(){
        if(this.start == null  ||  this.end == null)
            return 10;
        double x = Math.pow(this.end.getX()-this.start.getX(),2);
        double y = Math.pow(this.end.getY()-this.start.getY(),2);
        return (Math.sqrt(  x+y  ));
    }
}
