package com.buildinnov.smartevac.plugin.evacplans_generation.services;

import com.buildinnov.smartevac.plugin.evacplans_generation.services.models.InterestPoint;
import es.usc.citius.hipster.graph.GraphBuilder;
import es.usc.citius.hipster.graph.HipsterDirectedGraph;
import org.apache.commons.logging.impl.SLF4JLog;
import org.bimserver.emf.IfcModelInterface;
import org.bimserver.emf.OfflineGeometryGenerator;
import org.bimserver.models.ifc2x3tc1.*;
import org.bimserver.plugins.services.BimServerClientInterface;
import org.bimserver.plugins.services.Geometry;
import org.bimserver.shared.IfcDoc;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EObject;
import org.tinfour.common.*;
import org.tinfour.demo.utils.TestPalette;

import org.tinfour.demo.utils.TinRenderingUtility;
import org.tinfour.standard.IncrementalTin;
import org.tinfour.utils.TriangleCollector;

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
import java.util.*;
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
        FileWriter writer = null;
        try {
            writer = new FileWriter("D:\\workplace\\IFC_levels_processing_log.txt",true);
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


        List<IfcBuildingStorey> buildingStoreys = model.getAll(IfcBuildingStorey.class);
        IfcBuildingStorey storey;
        List<Vertex> spacesVertices= new ArrayList<Vertex>();
        List<Vertex> wallsVertices;
        IfcSpace space;
        List<String> gIds = new ArrayList<>();

        List<Vertex> spaceCentroids;
        List<IfcDoor> spaceDoors ;

        int k=0;
        int spacesCount = 0;
        int goodSpaces = 0;
        for(int i=0;i<buildingStoreys.size();i++){
            System.out.println("Story : Name :"+buildingStoreys.get(i).getName()+" GID : "+buildingStoreys.get(i).getGlobalId());
            print_line.printf("\n\n\nStory : Name :"+buildingStoreys.get(i).getName()+" GID : "+buildingStoreys.get(i).getGlobalId());
            storey = buildingStoreys.get(i);
            EList<IfcRelDecomposes> relAggregates = storey.getIsDecomposedBy();
            for(int j=0;j<relAggregates.size();j++){
                for(IfcObjectDefinition ifcObjectDefinition : relAggregates.get(j).getRelatedObjects()){
                    gIds.add(ifcObjectDefinition.getGlobalId());
                    if(ifcObjectDefinition instanceof IfcSpace && samplesGids.contains(ifcObjectDefinition.getGlobalId()) ){
                        spacesCount++;
                        space = (IfcSpace) ifcObjectDefinition;
                        spaceCentroids = processSpace(space,print_line);
                        spaceDoors = processSpaceDoors(space,print_line);
                        //getting space neibourhood relationship with other spaces
                        //from gotten doors we find the neighbour space by testing if the door is a part of it's boundaries
                        //list of neighbours
                        //getSpaceNeighbours = getSpaceNeighbours(space,spaceDoors);


                        //find also if the space contains a stair to access to other level
                        //if it does so it will be contained in the two spaces,  we have to get the other space, it's building storey (in wich level it is)


                        //A point if interest could be : a space (compound point of interest), a door,
                        // a centroid of TIN triangle of a space or a stair

                }
            }
        }
    }
        print_line.printf("\n\nSpaces with all information   : " + goodSpaces+"/"+spacesCount+"\n");
        print_line.close();

    }

    public static List<IfcSpace> getSpaceNeighbours(IfcSpace space,List<IfcDoor> doors,List<IfcSpace> spacesList){
        List<IfcSpace> spaces = new ArrayList<>();
        for(IfcSpace ifcSpace:spacesList){

        }
        return spaces;
    }

    public static List<Vertex>  processSpace(IfcSpace space,PrintWriter print_line)
    {
        List<Vertex> spaceCentroidsVertices = new ArrayList<Vertex>();
        print_line.printf("\nSpace Name :  "+space.getName()+"   Space Global Id : "+space.getGlobalId());
        print_line.printf("\nExtracting space\'s vertices  ");
        List<Vertex> spaceVertices = processPlacement(space.getObjectPlacement(), space.getRepresentation());
        if(spaceVertices.size()>0){
            Rectangle2D spaceBounds= new Rectangle2D.Double();
            print_line.printf("\nShowing space vertices");
            for(Vertex vertex:spaceVertices) {
                print_line.printf("\nSpace Vertex  : (X,Y) :   ( " + vertex.getX() + ", " + vertex.getY() + " )");
                spaceBounds.add(vertex.getX(),vertex.getY());
            }
            print_line.printf("\nCreating  TIN ");
            IncrementalTin tin = new IncrementalTin();
            tin.add(spaceVertices, null);
            print_line.printf("\nTIN Created");
            try {
                print_line.printf("\nDrawing TIN");
                TinRenderingUtility.drawTin(tin, 500, 500, new File("D:\\workplace\\"+space.getName()+"_"+space.getGlobalId()+"_TIN.png"));
            } catch (IOException e) {
                print_line.printf("\nException occured when drawing tin");
                print_line.printf(e.getMessage());
                print_line.printf("\n\n\n");
            }
            print_line.printf("\nCreating local navigation network // Cleaning up the TIN");
            TrianglesWrapper  wrapper = new TrianglesWrapper();
            TriangleCollector.visitSimpleTriangles(tin,wrapper);
            List<Vertex> centroids = wrapper.getTrianglesCentroid();
            Rectangle2D bounds = tin.getBounds();
            print_line.printf("\nCleaning up the TIN (Removing Centroids ouside geomtery bounds)");
            List<Vertex> cleanCentroids = new ArrayList<>();
            for(Vertex vertex : centroids)
                if(!  spaceBounds.contains(vertex.getX(),vertex.getY()))
                        print_line.printf("\nCentroids outside geometry  (  "+ vertex.getX()+"," + vertex.getY() + ")" );
                    else{
                        print_line.printf("\nCentroids inside geometry  (  "+ vertex.getX()+"," + vertex.getY() + ")" );
                        cleanCentroids.add(vertex);
                        spaceCentroidsVertices.add(vertex);
                    }
        }

        return spaceCentroidsVertices;
    }




    public static List<IfcDoor> processSpaceDoors(IfcSpace space,PrintWriter print_line){
        print_line.printf( "\n\n===> IfcSpace Name  : "+space.getName()+"   Global Id : "+space.getGlobalId() );
        List<IfcRelSpaceBoundary> listBounds = space.getBoundedBy();
        List<IfcDoor> doorsList = new ArrayList<>();
        IfcLocalPlacement ifcLocalPlacement;
        IfcAxis2Placement3D placement3D;
        int ifcDoorsCount = 0;
        for(IfcRelSpaceBoundary boundary : listBounds){
            if(boundary.getRelatedBuildingElement() != null  && boundary.getRelatedBuildingElement() instanceof IfcDoor) {
                System.out.println("=>Its a door");
                ifcDoorsCount++;
                doorsList.add((IfcDoor) boundary.getRelatedBuildingElement() );
                if( ((IfcDoor)boundary.getRelatedBuildingElement()).getObjectPlacement() != null  &&   ((IfcDoor)boundary.getRelatedBuildingElement()).getObjectPlacement()  instanceof IfcLocalPlacement){
                    ifcLocalPlacement =  (IfcLocalPlacement)   (  ((IfcDoor)boundary.getRelatedBuildingElement()).getObjectPlacement());
                    System.out.println(ifcLocalPlacement.getRelativePlacement().toString());
                    placement3D = (IfcAxis2Placement3D)ifcLocalPlacement.getRelativePlacement();
                    System.out.println("Coordinates "+  placement3D.getLocation().getCoordinates().get(0)+"  , "+placement3D.getLocation().getCoordinates().get(1));
                }

                //((IfcDoor)boundary.getRelatedBuildingElement()).getObjectPlacement().getPlacesObject()
        }
        }
        print_line.printf("\n===> IfcDoors Count  :  "+ifcDoorsCount);
        return doorsList;
    }


    public static HipsterDirectedGraph<InterestPoint,IndoorDistance> createSpaceNavigationGraph(List<IfcDoor> doors,List<Vertex> centroids) {

        //List<InterestPoint> CentroidsInterestPoints = new ArrayList<>()

        for(IfcDoor ifcDoor : doors){

        }
        for(Vertex vertex :  centroids){
           // CentroidsInterestPoints.add(new InterestPoint(vertex,false));
        }

        GraphBuilder<InterestPoint, IndoorDistance> graphBuilder = GraphBuilder.<InterestPoint, IndoorDistance>create();
        Double verticesDistance;
        for(int i=1;i<centroids.size();i++){
            verticesDistance = centroids.get(i-1).getDistance(centroids.get(i));
            //graphBuilder.connect(new InterestPoint(centroids.get(i-1),false)).to(new InterestPoint(centroids.get(i),false)).withEdge(verticesDistance);
        }
        return graphBuilder.createDirectedGraph();
    }


    public static HipsterDirectedGraph<InterestPoint,Double> createSpaceNavigationGraphUsingTIN(IncrementalTin tin){

        GraphBuilder<InterestPoint, Double> graphBuilder = GraphBuilder.<InterestPoint, Double>create();
        TrianglesWrapper wrapper= new TrianglesWrapper();
        TriangleCollector.visitSimpleTriangles(tin,wrapper);
        List<Vertex> spaceCentroids = wrapper.getTrianglesCentroid();
        Double verticesDistance;
        for(int i=1;i<spaceCentroids.size();i++){
            verticesDistance = spaceCentroids.get(i-1).getDistance(spaceCentroids.get(i));
            graphBuilder.connect(new InterestPoint(spaceCentroids.get(i-1),false)).to(new InterestPoint(spaceCentroids.get(i),false)).withEdge(verticesDistance);
        }
        return graphBuilder.createDirectedGraph();

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
                if (boundary.getRelatedBuildingElement() instanceof IfcWall || boundary.getRelatedBuildingElement() instanceof IfcWallStandardCase) {
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
                if(contains instanceof IfcWall || contains instanceof IfcWallStandardCase){
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





