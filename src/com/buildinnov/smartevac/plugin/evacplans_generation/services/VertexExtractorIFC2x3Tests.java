package com.buildinnov.smartevac.plugin.evacplans_generation.services;

import org.bimserver.emf.IfcModelInterface;
import org.bimserver.models.ifc2x3tc1.*;
import org.eclipse.emf.common.util.EList;
import org.jdelaunay.delaunay.ConstrainedMesh;
import org.jdelaunay.delaunay.error.DelaunayError;
import org.jdelaunay.delaunay.geometries.DEdge;
import org.jdelaunay.delaunay.geometries.DPoint;
import org.tinfour.common.*;
import org.tinfour.demo.utils.TinRenderingUtility;
import org.tinfour.standard.IncrementalTin;
import org.tinfour.utils.TriangleCollector;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import static com.buildinnov.smartevac.plugin.evacplans_generation.services.GraphicsLibrary.drawPolyLine;
import static com.buildinnov.smartevac.plugin.evacplans_generation.services.GraphicsLibrary.drawPolygon;
import static com.buildinnov.smartevac.plugin.evacplans_generation.services.VertexExtractorIFC2x3.getSpaceWallsVertices;
import static com.buildinnov.smartevac.plugin.evacplans_generation.services.VertexExtractorIFC2x3.processPlacement;


public class VertexExtractorIFC2x3Tests
{

    public static void testManySpaces(IfcModelInterface model){
        FileWriter writer = null;
        try {
            writer = new FileWriter("D:\\workplace\\IFC_samples_log.txt",true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        PrintWriter print_line = new PrintWriter(writer);
        List<String> samplesGids = new ArrayList<>();




        samplesGids.add("1hS0l0psT3ZP0d5DO1Dqbw");
        samplesGids.add("1hS0l0psT3ZP0d5DO1Dqby");
        samplesGids.add("1hS0l0psT3ZP0d5DO1Dqbu");
        samplesGids.add("1hS0l0psT3ZP0d5DO1Dqc5");
        samplesGids.add("1hS0l0psT3ZP0d5DO1Dqbw");
        samplesGids.add("1hS0l0psT3ZP0d5DO1Dqbv");
        samplesGids.add("1hS0l0psT3ZP0d5DO1Dqbx");
        samplesGids.add("1hS0l0psT3ZP0d5DO1DqcM");
        samplesGids.add("1hS0l0psT3ZP0d5DO1DqcG");
        samplesGids.add("1hS0l0psT3ZP0d5DO1DqcJ");
        samplesGids.add("1hS0l0psT3ZP0d5DO1DqcT");
        List<IfcSpace> ifcSpaces = model.getAll(IfcSpace.class);
        List<Vertex> spaceVertices;
        List<Vertex> wallsVertices;
        List<Integer> spaceXes;
        List<Integer> spaceYes;
        List<Integer> wallXes;
        List<Integer> wallYes;

        List<IConstraint> constraintsList;
        LinearConstraint constraint;

        LinearConstraint constraintSpace;

        for(IfcSpace space : ifcSpaces)
            if(samplesGids.contains(space.getGlobalId())){
                System.out.println("Space : "+space.getName()+" GID : "+space.getGlobalId());
                print_line.printf("\n\nProcessing space :" +space.getName() + "  GID :  "+space.getGlobalId());
                if(space.getIsDecomposedBy().size()>0){
                    print_line.printf("\nProcessing decomposed space : ");
                    for(IfcRelDecomposes decomposes : space.getIsDecomposedBy())
                        print_line.printf("\nDecomposition object :   GID : "+decomposes.getGlobalId()+"  instance :"+decomposes.toString());
                }
                spaceVertices = new ArrayList<Vertex>();
                spaceVertices = processPlacement(space.getObjectPlacement(),space.getRepresentation());
                print_line.printf("\nNumber of space\'s vertices  : "+spaceVertices.size() );
                if(spaceVertices.size()>0){
                    constraintSpace = new LinearConstraint();
                    print_line.printf("\nDrawing space");
                    spaceXes = new ArrayList<>();
                    spaceYes = new ArrayList<>();

                    wallsVertices = new ArrayList<>();
                    print_line.printf("\nExtracting space\'s vertices  ");
                    wallsVertices = getSpaceWallsVertices(space);
                    //wallsVertices.addAll(spaceVertices);




                    print_line.printf("\nNumber of extracted wall vertices :"+wallsVertices.size());
                    if(wallsVertices.size()>0){
                        print_line.printf("\nDrawing space walls");
                        wallXes = new ArrayList<>();
                        wallYes = new ArrayList<>();
                        for(Vertex vertex:wallsVertices){
                            wallXes.add( (int)Math.round(vertex.getX()) );
                            wallYes.add( (int)Math.round(vertex.getY()) );
                            print_line.printf("\nWall Vertex  : (X,Y) :   ( "+ vertex.getX()+", " + vertex.getY() + " )" );
                        }

                        constraintsList = new ArrayList<>();
                        constraint = new LinearConstraint();


                        //System.out.println("The size of walls's vertices list :"+wallsVertices.size());

                        //constraintsList.add(constraint);

                        print_line.printf("\nCreating TIN");
                        IncrementalTin tin = new IncrementalTin(1.0);
                        tin.add(spaceVertices, null);
                        print_line.printf("TIN Added");


                        /*
                        for(Vertex vertex : spaceVertices){
                            spaceXes.add(  (int)Math.round(vertex.getX())  );
                            spaceYes.add(  (int)Math.round(vertex.getY()) );
                            print_line.printf("\nSpace Vertex  : (X,Y) :   ( "+ vertex.getX()+", " + vertex.getY() + " )" );
                            if(tin.isPointInsideTin(vertex.getX(),vertex.getY()))
                                constraintSpace.add(vertex);
                        }
                        */


                        for(Vertex vertex : tin.getVertices()){
                            if(tin.getBounds().contains(vertex.getX(),vertex.getY()))
                                constraintSpace.add(vertex);
                        }

                        //constraintsList.add(constraint);

                        //for(int i =0;i+j<wallsVertices.size()-1;i++)
                        /*
                        while (i+j<wallsVertices.size()-1){
                            boolean c1 = wallsVertices.get(i).getX() != 0 || wallsVertices.get(i).getY() !=0  && tin.isPointInsideTin(wallsVertices.get(i).getX(),wallsVertices.get(i).getY()) ;
                            boolean c2 = wallsVertices.get(i+j).getX() != 0 || wallsVertices.get(i+j).getY() !=0  && tin.isPointInsideTin(wallsVertices.get(i+j).getX(),wallsVertices.get(i+j).getY()) ;
                            if( c1 && c2 ){
                                    constraintsList.add(new LinearConstraint(wallsVertices.get(i),wallsVertices.get(i+j)));
                                    //constraint.add(wallsVertices.get(i));
                                    System.out.println("Sans zero and inside tin");
                                    //print_line.printf("\nVertex NOT NULL  Inside TIN " + "Walls Vertex : (x , y)  = (" + wallsVertices.get(i).x + ", "+wallsVertices.get(i).y + ")");
                                print_line.printf("\nVertex NOT NULL  Inside TIN"  +  "Walls Vertices : (x , y)  = (" + wallsVertices.get(i).x + ", "+wallsVertices.get(i).y + ")  "+"  (x , y)  = ("+ wallsVertices.get(i+j).x + ", "+wallsVertices.get(i+j).y);
                                    //print_line.printf("\nOutside TIN " + "Walls Vertex : (x , y)  = (" + wallsVertices.get(i).x + ", "+wallsVertices.get(i).y + ")");
                                    i++;

                            }
                            else {
                                if(!c1) i++;
                                if(!c2) j++;
                                System.out.println("Avec zero");
                                print_line.printf("\nVertex NULL // Outside TIN"  +  "Walls Vertices : (x , y)  = (" + wallsVertices.get(i).x + ", "+wallsVertices.get(i).y + ")  "+"  (x , y)  = ("+ wallsVertices.get(i+j).x + ", "+wallsVertices.get(i+j).y);
                            }
                            System.out.println("Walls Vertex : (x , y)  = (" + wallsVertices.get(i).x + ", "+wallsVertices.get(i).y + ")" );
                        }
                        */
                        constraintsList.add(constraintSpace);


/*
                        ConstrainedMesh mesh = new ConstrainedMesh();
                        ArrayList<DEdge> dEdges = new ArrayList<DEdge>();
                        DEdge edge;
                        try {



                        for(int i=0;i<spaceVertices.size()-1;i++){
                            mesh.addPoint(new DPoint(spaceVertices.get(i).getX(),spaceVertices.get(i).getY(),0));
                            edge = new DEdge();
                            edge.setStartPoint(new DPoint(spaceVertices.get(i).getX(),spaceVertices.get(i).getY(),0));
                            edge.setEndPoint(new DPoint(spaceVertices.get(i+1).getX(),spaceVertices.get(i+1).getY(),0));
                            dEdges.add(edge);
                        }
                        mesh.addPoint(new DPoint(spaceVertices.get(spaceVertices.size()-1).getX(),spaceVertices.get(spaceVertices.size()-1).getY(),0));
                        mesh.setConstraintEdges(dEdges);
                        mesh.processDelaunay();

                        } catch (DelaunayError delaunayError) {
                            delaunayError.printStackTrace();
                            print_line.printf("\nDelaunay exception");
                            print_line.printf(delaunayError.getMessage());
                        }

                        */
                        print_line.printf("\nCleaning up the TIN");


                        double mx;
                        double my;
                        List<IQuadEdge> edges = tin.getEdges();
                        List<Vertex> listToRemove = new ArrayList<>();
                        ListIterator iterator = edges.listIterator();
                        IQuadEdge edge = null;
                        while(iterator.hasNext()){
                             edge = (IQuadEdge) iterator.next();
                            if(edge !=null){
                            if(edge.getA()!=null && edge.getB() !=null){
                            System.out.println("Edge :"+ "( " + edge.getA().getX()+", " + edge.getA().getY() + " )"   +  "( " + edge.getB().getX()+", " + edge.getB().getY() + " )"  );
                            mx = edge.getA().getX() + edge.getB().getX() /2;
                            my = edge.getA().getY() + edge.getB().getY() /2;
                            if(  ! tin.getBounds().contains (mx,my)){
                                //listToRemove.add(edge.getA());
                                //listToRemove.add(edge.getB());
                                //print_line.printf("\nRemoved points");
                                //print_line.printf("\nRemoved points");
                                print_line.printf("\nEdge center outside :(  "+ mx+"," + my + ")");
                            }else{
                                print_line.printf("\nEdge center inside :(  "+ mx+"," + my + ")");
                            }

                            }else{

                                        ///print_line.printf("\nNULL points");
                                System.out.println("NULL POINTS");
                            }}else{
                                    print_line.printf("\nNULL edge");
                                System.out.println("NULL edge");
                            }

                        }


                    TrianglesWrapper  wrapper = new TrianglesWrapper();
                    TriangleCollector.visitSimpleTriangles(tin,wrapper);
                    List<Vertex> centroids = wrapper.getTrianglesCentroid();
                    Rectangle2D bounds = tin.getBounds();
                    for(Vertex vertex : centroids)
                        if(!  bounds.contains(vertex.getX(),vertex.getY()))
                            print_line.printf("\nCentroids outside geometry  (  "+ vertex.getX()+"," + vertex.getY() + ")" );
                        else
                            print_line.printf("\nCentroids inside geometry  (  "+ vertex.getX()+"," + vertex.getY() + ")" );





                        try {
                            print_line.printf("\nDrawing TIN");
                                TinRenderingUtility.drawTin(tin, 500, 500, new File("D:\\workplace\\"+space.getName()+"_"+space.getGlobalId()+"_TIN.png"));
                                tin.addConstraints(constraintsList, true);
                                print_line.printf("Constraints Added");
                                TinDrawingUtility.drawTin(tin, 500, 500, new File("D:\\workplace\\" + space.getName() + "_" + space.getGlobalId() + "_TIN_CONSTR.png"));
                            //drawPolyLine(wallXes,wallYes,"D:\\workplace\\"+space.getName()+"_"+space.getGlobalId()+"_WALLS_POLY.png");
                        } catch (IOException e) {
                            print_line.printf("\nException occured ");
                            print_line.printf(e.getMessage());
                            print_line.printf("\n\n\n");
                            e.printStackTrace();
                        }
                    }



                }
            }
        print_line.close();
    }

    public static void testOneSpace(IfcModelInterface model){
        FileWriter writer = null;
        try {
            writer = new FileWriter("D:\\IFC_samples_log.txt",true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        PrintWriter print_line = new PrintWriter(writer);

        List<Integer> spaceXes;
        List<Integer> spaceYes;
        List<Integer> wallXes;
        List<Integer> wallYes;

        List<Vertex> listVerticesSamples = new ArrayList<Vertex>();
        listVerticesSamples.add(new Vertex(-2101.670076752294,-3236.127112027889,0));
        listVerticesSamples.add(new Vertex(1788.329923247809,-3236.127112027889,0));
        listVerticesSamples.add(new Vertex(1788.329923247809,-831.1271120278991,0));
        listVerticesSamples.add(new Vertex(1833.329923247807,-831.1271120278991,0));
        listVerticesSamples.add(new Vertex(1833.329923247816,2753.897887972146,0));
        listVerticesSamples.add(new Vertex(1163.360614017834,2753.897887972146,0));
        listVerticesSamples.add(new Vertex(-2101.670076752294,2753.897887972146,0));
        listVerticesSamples.add(new Vertex(-2101.670076752294,423.9073920976326,0));
        listVerticesSamples.add(new Vertex(-2101.670076752294,-551.092607902371,0));
        listVerticesSamples.add(new Vertex(-2101.670076752294,-3236.127112027889,0));
        spaceXes = new ArrayList<>();
        spaceYes = new ArrayList<>();
        for(Vertex vertex: listVerticesSamples){
            spaceXes.add( (int)(Math.round(vertex.getX())));
            spaceYes.add( (int)(Math.round(vertex.getY())));
        }




        try {
            drawPolygon(spaceXes,spaceYes,"D:\\SpceD.png");
        } catch (IOException e) {
            e.printStackTrace();
        }
        print_line.close();
    }





}





