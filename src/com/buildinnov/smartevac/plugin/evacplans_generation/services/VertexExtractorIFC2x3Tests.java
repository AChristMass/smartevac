package com.buildinnov.smartevac.plugin.evacplans_generation.services;

import org.bimserver.emf.IfcModelInterface;
import org.bimserver.models.ifc2x3tc1.*;
import org.eclipse.emf.common.util.EList;
import org.tinfour.common.IConstraint;
import org.tinfour.common.LinearConstraint;
import org.tinfour.common.Vertex;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

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
                    print_line.printf("\nDrawing space");
                    spaceXes = new ArrayList<>();
                    spaceYes = new ArrayList<>();
                    for(Vertex vertex : spaceVertices){
                        spaceXes.add(  (int)Math.round(vertex.getX())  );
                        spaceYes.add(  (int)Math.round(vertex.getY()) );
                        print_line.printf("\nSpace Vertex  : (X,Y) :   ( "+ vertex.getX()+", " + vertex.getY() + " )" );
                    }
                    try {
                        print_line.printf("\nDrawing space geometry");
                        drawPolygon(spaceXes,spaceYes,"D:\\"+space.getName()+"_"+space.getGlobalId()+"_POLY.png");
                    } catch (IOException e) {
                        print_line.printf("\nException occured ");
                        print_line.printf(e.getMessage());
                        print_line.printf("\n\n\n");
                        e.printStackTrace();
                    }
                    /*
                    wallsVertices = new ArrayList<>();
                    print_line.printf("\nExtracting space\'s vertices  ");
                    wallsVertices = getSpaceWallsVertices(space);
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
                        try {
                            drawPolyLine(wallXes,wallYes,"D:\\workplace\\"+space.getName()+"_"+space.getGlobalId()+"_WALLS_POLY.png");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    */
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





