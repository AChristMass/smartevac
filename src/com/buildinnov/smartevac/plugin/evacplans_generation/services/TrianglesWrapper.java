package com.buildinnov.smartevac.plugin.evacplans_generation.services;




import org.tinfour.common.SimpleTriangle;
import org.tinfour.common.Vertex;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class TrianglesWrapper implements Consumer<SimpleTriangle> {

    List<SimpleTriangle> triangles = new ArrayList<SimpleTriangle>();

    @Override
    public void accept(SimpleTriangle simpleTriangle) {
        triangles.add(simpleTriangle);
        System.out.println("Triangle ");
    }

    @Override
    public Consumer<SimpleTriangle> andThen(Consumer<? super SimpleTriangle> after) {
        return null;
    }

    public List<Vertex> getTrianglesCentroid(){
        List<Vertex> vertices = new ArrayList<>();
        for(SimpleTriangle triangle : this.triangles)
            vertices.add(getTriangleCentroid(triangle));
        return vertices;
    }

    public Vertex getTriangleCentroid(SimpleTriangle triangle){
        Double x = ( triangle.getEdgeA().getA().x + triangle.getEdgeB().getA().x + triangle.getEdgeC().getA().x ) /3;
        Double y = ( triangle.getEdgeA().getA().y + triangle.getEdgeB().getA().y + triangle.getEdgeC().getA().y ) /3;
        return  new Vertex(x,y,0);
    }




}
