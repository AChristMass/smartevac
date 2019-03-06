package com.buildinnov.smartevac.plugin.evacplans_generation.services;

import org.bimserver.emf.IfcModelInterface;
import org.bimserver.emf.OfflineGeometryGenerator;
import org.bimserver.models.ifc2x3tc1.*;
import org.bimserver.plugins.services.BimServerClientInterface;
import org.bimserver.plugins.services.Geometry;
import org.eclipse.emf.common.util.EList;
import org.tinfour.common.*;
import org.tinfour.demo.utils.TestPalette;

import org.tinfour.demo.utils.TinRenderingUtility;
import org.tinfour.standard.IncrementalTin;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.DoubleBuffer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.buildinnov.smartevac.plugin.evacplans_generation.services.GraphicsLibrary.drawPolygon;
import static org.bimserver.models.ifc2x3tc1.IfcInternalOrExternalEnum.INTERNAL;


public class VertexExtractorIFC2x3
{
    private static final int BYTES_PER_VERTEX = 12;


    public static void processSamplesSpaces(IfcModelInterface model){

        FileWriter writer = null;
        try {
            writer = new FileWriter("D:\\IFC_samples_log.txt",true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        PrintWriter print_line = new PrintWriter(writer);

        /*
        for(IfcSpace space : ifcSpaces)
            if(samplesGids.contains(space.getGlobalId())){
                System.out.println("Space : "+space.getName()+" GID : "+space.getGlobalId());
                print_line.printf("\n\nProcessing space :" +space.getName() + "  GID :  "+space.getGlobalId());
                if(space.getIsDecomposedBy().size()>0){
                    print_line.printf("\nProcessing decomposed space : ");
                    for(IfcRelDecomposes decomposes : space.getIsDecomposedBy())
                        print_line.printf("\nDecomposition object :   GID : "+decomposes.getGlobalId()+"  instance :"+decomposes.toString());

                }
                spaceVertices = new ArrayList<>();
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
                        drawPolygon(spaceXes,spaceYes,"D:\\"+space.getName()+"_"+space.getGlobalId()+"_POLY.png");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

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
                            drawPolygon(wallXes,wallYes,"D:\\"+space.getName()+"_"+space.getGlobalId()+"_WALLS_POLY.png");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                }
            }

        */
        print_line.close();
    }

    public static void processLevels(IfcModelInterface model) {
        List<IfcBuildingStorey> buildingStoreys = model.getAll(IfcBuildingStorey.class);
        System.out.println("Storey available");
        IfcBuildingStorey storey;
        List<Vertex> spacesVertices= new ArrayList<Vertex>();
        List<Vertex> wallsVertices;
        IfcSpace space;
        List<String> gIds = new ArrayList<>();
        List<Integer> xes;
        List<Integer> yes;


        int k=0;
        FileWriter writer = null;
        try {
            writer = new FileWriter("D:\\IFC_log.txt",true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        PrintWriter print_line = new PrintWriter(writer);
        int noVerticesSpaces = 0;
        int spacesCount = 0;
        int goodSpaces = 0;


        //print_line.printf("Spaces count :  %d" + spacesCount+"\n");

        for(int i=0;i<buildingStoreys.size();i++){
            System.out.println("Story : Name :"+buildingStoreys.get(i).getName()+" GID : "+buildingStoreys.get(i).getGlobalId());
            storey = buildingStoreys.get(i);
            EList<IfcRelDecomposes> relAggregates = storey.getIsDecomposedBy();
            for(int j=0;j<relAggregates.size();j++){
                for(IfcObjectDefinition ifcObjectDefinition : relAggregates.get(j).getRelatedObjects()){
                    //System.out.println("[DecomposedBy]" +ifcObjectDefinition.toString());
                    gIds.add(ifcObjectDefinition.getGlobalId());
                    if(ifcObjectDefinition instanceof IfcSpace){
                        spacesCount++;
                        space = (IfcSpace) ifcObjectDefinition;
                        System.out.println("[DecomposedBy] space vertices generated   : Name "+space.getName()+"  GID : "+space.getGlobalId());
                        getSpaceDoors(space);
                        spacesVertices = processPlacement(space.getObjectPlacement(),space.getRepresentation());


                        xes = new ArrayList<>();
                        yes = new ArrayList<>();
                        for(Vertex vertex : spacesVertices){
                            xes.add((int) Math.round(vertex.getX()));
                            yes.add((int) Math.round(vertex.getY()));

                        }

                        //wallsVertices = getSpaceWallsVertices(space);

                            //&& wallsVertices!=null && wallsVertices.size()>0
                            if(spacesVertices.size()>0 ) {
                                System.out.println("drawing the polygon");
                                print_line.printf("Wall vertices : Space Name %s Space GID %s\n",space.getName(),space.getGlobalId());
                                try {
                                    drawPolygon(xes,yes,"D:\\"+storey.getName() + "_" + space.getName() + "_POLY.png");
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                goodSpaces++;
                                //System.out.println("Creating TIN");
                               /* IncrementalTin tin = new IncrementalTin(1.0);
                                tin.add(spacesVertices, null);

                                ArrayList<IConstraint> constraintsList = new ArrayList<>();
                                IConstraint constraint = new LinearConstraint();
                                constraintsList.add(constraint);
                                for(Vertex vertex : wallsVertices)
                                    constraint.add(vertex);
                                tin.addConstraints(constraintsList,false);
                                //System.out.println("TIN Added");
                                try {
                                    System.out.println("drawing the TIN");
                                    TinRenderingUtility.drawTin(tin, 500, 500, new File("D:\\TIN_" + storey.getName() + "_" + space.getName() + "_C_NC.png"));
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                */


                            }else{
                                //if(wallsVertices ==null || wallsVertices.size() ==0)
                                 //   print_line.printf("Wall vertices : Space Name %s Space GID %s\n",space.getName(),space.getGlobalId());

                                noVerticesSpaces++;
                                print_line.printf("Space Name %s Space GID %s\n",space.getName(),space.getGlobalId());
                            }
                    }
                }
                print_line.printf("\n\nSpaces with all information   : " + goodSpaces+"/"+spacesCount+"\n");
                print_line.printf("Spaces no vertices count " + noVerticesSpaces+"\n");
                print_line.close();
            }
        }
    }


    public static List<IfcDoor> getSpaceDoors(IfcSpace ifcSpace){
        List<IfcDoor> ifcDoors = new ArrayList<>();
        for(IfcRelSpaceBoundary boundary : ifcSpace.getBoundedBy()){
            if(boundary.getRelatedBuildingElement() instanceof IfcWall){
                IfcWall ifcWall = (IfcWall) boundary.getRelatedBuildingElement();
                for (IfcRelVoidsElement ifcRelVoidsElement : ifcWall.getHasOpenings()) {
                    IfcOpeningElement ifcOpeningElement = (IfcOpeningElement) ifcRelVoidsElement.getRelatedOpeningElement();
                    for (IfcRelFillsElement filling : ifcOpeningElement.getHasFillings()) {
                        IfcElement ifcRelatedBuildingElement = filling.getRelatedBuildingElement();
                        if (ifcRelatedBuildingElement instanceof IfcDoor ) {

                            processDoor((IfcDoor) ifcRelatedBuildingElement);
                        } else if (ifcRelatedBuildingElement instanceof IfcWindow) {
                            // Do something
                        }
                    }
                }
            }else if(boundary.getRelatedBuildingElement() instanceof IfcDoor){
                System.out.println("a Door in boundary");
                processDoor((IfcDoor) boundary.getRelatedBuildingElement());
            }
        }

        return ifcDoors;
    }



    private static void processDoor(IfcDoor door){
            processPlacement(door.getObjectPlacement(),door.getRepresentation());
    }



    public static void processSpace(IfcModelInterface model)
    {
        List<Vertex> SpaceVertices = new ArrayList<>();
        List<de.alsclo.voronoi.graph.Point> spacePoints = new ArrayList<>();


        List<Vertex> wallsVertices;
        List<IConstraint> constraintsList;
        LinearConstraint constraint;
        for(IfcSpace space : model.getAll(IfcSpace.class))
        {
            //if(space.getGlobalId().equals("0PeKSAvI59PfDaeCq6ElAH")) {
            System.out.println("got the space : "+"Name : "+space.getName()+" Global Id : "+space.getGlobalId());
            SpaceVertices = processPlacement(space.getObjectPlacement(), space.getRepresentation());
            /*
            wallsVertices = getSpaceWallsVertices(space);
            constraintsList = new ArrayList<>();
            constraint = new LinearConstraint();
            constraintsList.add(constraint);
            System.out.println("The size of walls's vertices list :"+wallsVertices.size());
            for(Vertex vertex : wallsVertices){
                constraint.add(vertex);
                System.out.println("Walls Vertex : (x , y)  = (" + vertex.x + ", "+vertex.y + ")" );
            }
            */
            //System.out.println("Creating TIN");
            //IncrementalTin tin = new IncrementalTin(1.0);
            //tin.add(wallsVertices, null);
            //System.out.println("TIN Added");
            //tin.addConstraints(constraintsList,false);
            //System.out.println("Constraints Added");
            /*
                try {
                    System.out.println("drawing the TIN");
                    TinRenderingUtility.drawTin(tin, 500, 500, new File("D:\\wall_tin_with_cons_.png"));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                */


            //}
        }
    }


    public static  List<Vertex>  getWallVertices(IfcWall ifcWall){
        int polylines = 0;
        int faceted = 0;
        int clipped = 0;
        List<Vertex> vertices = new ArrayList<>();
        IfcProductRepresentation representation = ifcWall.getRepresentation();
        for(IfcRepresentation ifcRepresentation : representation.getRepresentations()){
            for(IfcRepresentationItem representationItem : ifcRepresentation.getItems()){
               /* if(representationItem instanceof IfcGeometricRepresentationItem)
                    if(representationItem  instanceof IfcPolyline)
                        for(IfcCartesianPoint point : ((IfcPolyline)representationItem).getPoints() )
                           vertices.add(new Vertex(point.getCoordinates().get(0).doubleValue(),point.getCoordinates().get(1).doubleValue(),0)); */
                //System.out.println("Yes  : "+representationItem.toString());

                if(representationItem instanceof  IfcFacetedBrep  ){
                    faceted++;
                    for(IfcFace face: ((IfcFacetedBrep) representationItem).getOuter().getCfsFaces() ){
                        for(IfcFaceBound faceBound : face.getBounds()){
                            IfcLoop loop = faceBound.getBound();
                            //if(loop instanceof IfcPolyLoop == false)
                            //    continue;
                            for(IfcCartesianPoint point : ((IfcPolyLoop)loop).getPolygon()){
                                vertices.add(new Vertex(point.getCoordinates().get(0).doubleValue(),point.getCoordinates().get(1).doubleValue(),0));
                            }
                        }
                    }
                }else {
                    if(representationItem instanceof IfcPolyline) {
                        polylines++;
                        for (IfcCartesianPoint point : ((IfcPolyline) representationItem).getPoints())
                            vertices.add(new Vertex(point.getCoordinates().get(0).doubleValue(), point.getCoordinates().get(1).doubleValue(), 0));
                    }
                    if(representationItem instanceof IfcBooleanClippingResult){
                        clipped++;
                        System.out.println(representationItem.toString());

                    }

                }

            }
        }
        //System.out.println("Polylines cout : "+polylines);
        //System.out.println("IfcFacetedBrep coutn "+faceted);
        //System.out.println("IfcBooleanClippingResult count :"+clipped);
        return vertices;
    }

    public static List<Vertex> getSpaceWallsVertices(IfcSpace space){
        List<Vertex> vertices = new ArrayList<>();

        for(IfcRelSpaceBoundary boundary : space.getBoundedBy()){
            //if(boundary.getInternalOrExternalBoundary() == IfcInternalOrExternalEnum.INTERNAL) {
                if (boundary.getRelatedBuildingElement() instanceof IfcWall) {
                    IfcWall wall = (IfcWall) boundary.getRelatedBuildingElement();
                    //System.out.println("its a wall");
                    vertices.addAll(getWallVertices((IfcWall) boundary.getRelatedBuildingElement()));
                }
            /*}else {
                System.out.println("EXternal boundary");
            }*/
        }
        for(IfcRelContainedInSpatialStructure  contains : space.getContainsElements()){
            for(IfcProduct ifcProduct: contains.getRelatedElements())
                if(contains instanceof IfcWall){
                    System.out.println("its a wall in contains");
                    vertices.addAll(getWallVertices((IfcWall) contains));
                }
        }
        return vertices;
    }

    public static List<Vertex> getSpaceBoundariesVertices(IfcSpace space){
        for(IfcRelSpaceBoundary boundary : space.getBoundedBy()){
            if(boundary.getRelatedBuildingElement() instanceof  IfcWall){

            }
        }
        return null;
    }





    /*******
     * Space processing section to get vertices
     * ***/


    public static List<Vertex > processPlacement(IfcObjectPlacement objectPlacement, IfcProductRepresentation representation2) {
        System.out.println("processPlacement");
        List<Vertex > vertices = new ArrayList<>();
        IfcObjectPlacement placement = objectPlacement;
        if (placement instanceof IfcLocalPlacement) {
            IfcLocalPlacement localPlacement = (IfcLocalPlacement) placement;
            IfcProductRepresentation representation = representation2;
            if (representation instanceof IfcProductDefinitionShape) {
                IfcProductDefinitionShape productDefinitionShape = (IfcProductDefinitionShape) representation;
                vertices = ProcessSpacePlacementAndShape(localPlacement, productDefinitionShape);
            }
        }
        return  vertices;
    }



    private static List<Vertex> ProcessSpacePlacementAndShape(IfcLocalPlacement localPlacement, IfcProductDefinitionShape productDefinitionShape)
    {
        System.out.println("ProcessSpacePlacementAndShape");
        List<Vertex>  vertices = new ArrayList<>();
        for (IfcRepresentation representation : productDefinitionShape.getRepresentations())
        {
            if(representation instanceof IfcShapeRepresentation)
            {
                IfcShapeRepresentation shapeRepresentation = (IfcShapeRepresentation) representation;
                for(IfcRepresentationItem representationItem : shapeRepresentation.getItems())
                {
                    if(representationItem instanceof IfcExtrudedAreaSolid)
                    {
                        IfcExtrudedAreaSolid extrudedAreaSolid =
                                (IfcExtrudedAreaSolid) representationItem;
                        vertices = processExtrudedAreaSolid(localPlacement, extrudedAreaSolid);
                    }
                }
            }
        }
        return vertices;
    }

    private static List<Vertex> processExtrudedAreaSolid(IfcLocalPlacement localPlacement, IfcExtrudedAreaSolid extrudedAreaSolid)
    {
        System.out.println("processExtrudedAreaSolid");
        IfcAxis2Placement3D placement = extrudedAreaSolid.getPosition();
        IfcProfileDef profile = extrudedAreaSolid.getSweptArea();
        return processProfileObjectPlacementAndLocalPlacement(profile, placement, localPlacement);
    }

    private static List<Vertex> processProfileObjectPlacementAndLocalPlacement(
            IfcProfileDef profile,
            IfcAxis2Placement3D placement,
            IfcLocalPlacement localPlacement)
    {
        System.out.println("processProfileObjectPlacementAndLocalPlacement");
        List<Vertex> vertices = new ArrayList<>();
        if(profile instanceof IfcArbitraryClosedProfileDef)
        {
            vertices = handleArbitraryClosedProfileDef((IfcArbitraryClosedProfileDef) profile, placement, localPlacement);
        }

        return vertices;

    }

    private static List<Vertex> handleArbitraryClosedProfileDef(
            IfcArbitraryClosedProfileDef profile,
            IfcAxis2Placement3D placement,
            IfcLocalPlacement localPlacement)
    {

        System.out.println("handleArbitraryClosedProfileDef");
        List<Vertex> vertexList = new ArrayList<Vertex>();
        Vertex vertex;
        if (profile.getProfileType() == IfcProfileTypeEnum.AREA && profile.getOuterCurve() instanceof IfcPolyline)
        {
            IfcPolyline polyLine =
                    (IfcPolyline) profile.getOuterCurve();
            for(IfcCartesianPoint cartesianPoint : polyLine.getPoints())
            {
                vertex = new Vertex(cartesianPoint.getCoordinates().get(0),cartesianPoint.getCoordinates().get(1),0);
                vertexList.add(vertex);
            }
        }
        return vertexList;
    }


}





