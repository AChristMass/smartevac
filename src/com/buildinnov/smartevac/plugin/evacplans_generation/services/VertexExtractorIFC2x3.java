package com.buildinnov.smartevac.plugin.evacplans_generation.services;

import com.buildinnov.smartevac.plugin.evacplans_generation.services.models.*;
import es.usc.citius.hipster.algorithm.Hipster;
import es.usc.citius.hipster.graph.GraphBuilder;
import es.usc.citius.hipster.graph.GraphSearchProblem;
import es.usc.citius.hipster.graph.HipsterDirectedGraph;
import es.usc.citius.hipster.model.problem.SearchProblem;
import org.apache.commons.logging.impl.SLF4JLog;
import org.apache.maven.model.Build;
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

    private Map<String,IfcDoor> doorsMap = new HashMap<>();
    private Map<String,IfcSpace> spacesMap = new HashMap();
    private Map<String, IfcStair> stairsMap = new HashMap<>();

    private Map<String,SmartEvacSpace> smartEvacSpacesMap =  new HashMap<>();
    private Map<String, SmartEvacStair> alreadyCreatedSmartEvacStairMap = new HashMap<>();
    private Map<String, SmartEvacDoor> smartEvacDoorsMap = new HashMap<>();
    private Map<String,SmartEvacDoor> alreadyCreatedSmartEvacDoors = new HashMap<>();
    private Map<String, InterestPoint> bySpacesInterestPoints = new HashMap<>();

    private HipsterDirectedGraph<InterestPoint, IndoorDistance> indoorNavigationNetwork;

    private Map<String,List<InterestPoint>> indoorNavigationNetworkMap = new HashMap<>();
    private Map<String, InterestPoint> globalInterestPointsMap = new HashMap<>();

    private Map<String,List<InterestPoint>> indoorNavigationNetworkMapSpaces = new HashMap<>();
    private Map<String, InterestPoint> globalInterestPointsMapSpaces = new HashMap<>();


    public void prepareDoorsMap(IfcModelInterface model){
        List<IfcDoor> ifcDoors = model.getAll(IfcDoor.class);
        for(IfcDoor ifcDoor : ifcDoors)
            doorsMap.put(ifcDoor.getGlobalId(),ifcDoor);
    }
    public void prepareSpacesMap(IfcModelInterface model){
        List<IfcSpace> ifcSpaces = model.getAll(IfcSpace.class);
        for(IfcSpace ifcSpace : ifcSpaces)
            spacesMap.put(ifcSpace.getGlobalId(),ifcSpace);
    }
    public void prepareStairsMap(IfcModelInterface model){
        List<IfcStair> ifcStairs = model.getAll(IfcStair.class);
        for(IfcStair ifcStair : ifcStairs)
            stairsMap.put(ifcStair.getGlobalId(),ifcStair);
    }

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

    public void prepareSmartEvacDoors(IfcModelInterface model){
        List<IfcDoor> ifcDoors = model.getAll(IfcDoor.class);
        // for(IfcDor)
    }

    public void processStairs(IfcModelInterface model){
        List<IfcStair> ifcStairs = model.getAll(IfcStair.class);
        IfcLocalPlacement localPlacement;
        IfcProductRepresentation representation;
        IfcProductDefinitionShape definitionShape;
        for(IfcStair ifcStair : ifcStairs){

            if( ifcStair.getObjectPlacement() != null  &&   ifcStair.getObjectPlacement()  instanceof IfcLocalPlacement){
                IfcLocalPlacement ifcLocalPlacement = (IfcLocalPlacement) ifcStair.getObjectPlacement();
                //System.out.println(ifcLocalPlacement.getRelativePlacement().toString());
                IfcAxis2Placement3D placement3D = (IfcAxis2Placement3D) ifcLocalPlacement.getRelativePlacement();
                System.out.println(" x = " + placement3D.getLocation().getCoordinates().get(0) +" y = "+placement3D.getLocation().getCoordinates().get(1)+" z =  "+placement3D.getLocation().getCoordinates().get(2));
            }

            System.out.println("getConnectedFrom");
            for(IfcRelConnects relConnects : ifcStair.getConnectedFrom())
                System.out.println(relConnects.toString());
            System.out.println("getConnectedTo");
            for(IfcRelConnects relConnects : ifcStair.getConnectedTo())
                System.out.println(relConnects.toString());
            System.out.println("getContainedInSpatialStructure");
            for(IfcRelContainedInSpatialStructure contained:ifcStair.getContainedInStructure())
                for(IfcProduct ifcProduct : contained.getRelatedElements())
                    System.out.println(ifcProduct.toString());


            //processPlacement(ifcStair.getObjectPlacement(),ifcStair.getRepresentation());
            if( ifcStair.getObjectPlacement() != null  &&   ifcStair.getObjectPlacement()  instanceof IfcLocalPlacement){
                localPlacement = (IfcLocalPlacement)ifcStair.getObjectPlacement();
                representation = ifcStair.getRepresentation();
                System.out.println(representation.toString());
                if(representation instanceof  IfcProductDefinitionShape){
                    definitionShape = (IfcProductDefinitionShape) representation;
                    for(IfcRepresentation representation1 : definitionShape.getRepresentations()){
                        System.out.println(representation1.toString());
                    }
                }

            }

            //else return null;
        }
    }

    public  void processLevelsSpacesGraph(IfcModelInterface model) {
        FileWriter writer = null;
        try {
            writer = new FileWriter("F:\\workplace\\IFC_levels_processing_log_full.txt",false);
        } catch (IOException e) {
            e.printStackTrace();
        }
        PrintWriter print_line = new PrintWriter(writer);
        List<String> samplesGids = new ArrayList<>();
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
        SmartEvacSpace smartEvacSpace ;
        SmartEvacDoor smartEvacDoor;
        SmartEvacStair smartEvacStair;
        InterestPoint interestPoint;
        List<IfcStair> ifcStairs ;
        List<InterestPoint> spaceTinInterestPoints;
        List<InterestPoint> spaceTinOtherInterestPoints = new ArrayList<>();
        List<SmartEvacDoor> spaceSmartEvacDoors = new ArrayList<>();
        List<SmartEvacStair> spaceSmartEvacStairs = new ArrayList<>();
        List<SmartEvacStair> smartEvacStairs = new ArrayList<>();
        List<InterestPoint>  doorsInterestPoints = new ArrayList<>();
        List<InterestPoint>  stairsInterestPoints = new ArrayList<>();
        List<InterestPoint> interestPoints ;
        List<InterestPoint> temp;
        List<String> doneInterestPoints = new ArrayList<>();
        Map<String,SmartEvacSpace> smartEvacSpaces = new HashMap<>();
        boolean concatTwoGID;
        int indexO;
        InterestPoint pp;

        GraphBuilder<InterestPoint, IndoorDistance> graphBuilder = GraphBuilder.<InterestPoint, IndoorDistance>create();
        for(int i=0;i<buildingStoreys.size();i++){
            System.out.println("Story : Name :"+buildingStoreys.get(i).getName()+" GID : "+buildingStoreys.get(i).getGlobalId());
            print_line.printf("\n\n\nStory : Name :"+buildingStoreys.get(i).getName()+" GID : "+buildingStoreys.get(i).getGlobalId());
            storey = buildingStoreys.get(i);
            EList<IfcRelDecomposes> relAggregates = storey.getIsDecomposedBy();
            for(int j=0;j<relAggregates.size();j++){
                for(IfcObjectDefinition ifcObjectDefinition : relAggregates.get(j).getRelatedObjects()){
                    gIds.add(ifcObjectDefinition.getGlobalId());
                    if(ifcObjectDefinition instanceof IfcSpace){
                        spacesCount++;
                        space = (IfcSpace) ifcObjectDefinition;
                        smartEvacSpace = new SmartEvacSpace(space.getGlobalId(),space.getName());
                        //getting centroids of space
                        spaceCentroids = processSpace(space,print_line);
                        //getting doors of space
                        spaceDoors = processSpaceDoors(space,print_line);
                        //getting stairs of space
                        print_line.printf("\nGeeting space stairs :");
                        ifcStairs = getSpaceStairs(space,print_line);
                        print_line.printf("\nSpace stairs list size :  "+ifcStairs.size());
                        System.out.println("Finished the first part :");
                        //connecting goten doors to space
                        connectDoorsToSpace(space, spaceDoors, smartEvacSpace, spaceSmartEvacDoors);
                        //connecting goten stairs to space
                        ConnectStairsToSpace(smartEvacSpace, ifcStairs, spaceSmartEvacStairs);
                        this.smartEvacSpacesMap.put(space.getGlobalId(),smartEvacSpace);
                        ///generating space interest point
                        smartEvacSpaces.put(space.getGlobalId(),smartEvacSpace);
                    }
                }
            }
        }
        //smartEvacSpaces
        System.out.println("drawing generated graph");
        GraphicsLibrary graphicsLibrary = new GraphicsLibrary();
        graphicsLibrary.drawDoorsSpacesGraph("F:\\workplace\\graph_allspaces.png",smartEvacSpaces);
        print_line.close();
    }

    private void connectDoorsToSpace(IfcSpace space, List<IfcDoor> spaceDoors, SmartEvacSpace smartEvacSpace, List<SmartEvacDoor> spaceSmartEvacDoors) {
        SmartEvacDoor smartEvacDoor;
        for (IfcDoor ifcDoor : spaceDoors) {
            if (alreadyCreatedSmartEvacDoors.containsKey(ifcDoor.getGlobalId()))
                smartEvacDoor = alreadyCreatedSmartEvacDoors.get(ifcDoor.getGlobalId());
            else {
                smartEvacDoor = new SmartEvacDoor(ifcDoor.getGlobalId(), space.getName() + ":" + ifcDoor.getGlobalId());
                alreadyCreatedSmartEvacDoors.put(ifcDoor.getGlobalId(), smartEvacDoor);
            }
            smartEvacDoor.getAssociatedSpaces().add(smartEvacSpace);
            smartEvacSpace.getDoors().add(smartEvacDoor);
            spaceSmartEvacDoors.add(smartEvacDoor);
        }
    }


    public  void processLevels(IfcModelInterface model) {
        FileWriter writer = null;
        try {
            writer = new FileWriter("D:\\workplace\\IFC_levels_processing_log.txt",true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        PrintWriter print_line = new PrintWriter(writer);
        List<String> samplesGids = new ArrayList<>();
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
        SmartEvacSpace smartEvacSpace ;
        SmartEvacDoor smartEvacDoor;
        SmartEvacStair smartEvacStair;
        InterestPoint interestPoint;
        List<IfcStair> ifcStairs ;
        List<InterestPoint> spaceTinInterestPoints;
        List<InterestPoint> spaceTinOtherInterestPoints = new ArrayList<>();
        List<SmartEvacDoor> spaceSmartEvacDoors = new ArrayList<>();
        List<SmartEvacStair> spaceSmartEvacStairs = new ArrayList<>();
        List<SmartEvacStair> smartEvacStairs = new ArrayList<>();
        List<InterestPoint>  doorsInterestPoints = new ArrayList<>();
        List<InterestPoint>  stairsInterestPoints = new ArrayList<>();
        List<InterestPoint> interestPoints ;
        List<InterestPoint> temp;
        List<String> doneInterestPoints = new ArrayList<>();
        Map<String,SmartEvacSpace> smartEvacSpaces = new HashMap<>();
        boolean concatTwoGID;
        int indexO;
        InterestPoint pp;


        GraphBuilder<InterestPoint, IndoorDistance> graphBuilder = GraphBuilder.<InterestPoint, IndoorDistance>create();
        for(int i=0;i<buildingStoreys.size();i++){
            System.out.println("Story : Name :"+buildingStoreys.get(i).getName()+" GID : "+buildingStoreys.get(i).getGlobalId());
            print_line.printf("\n\n\nStory : Name :"+buildingStoreys.get(i).getName()+" GID : "+buildingStoreys.get(i).getGlobalId());
            storey = buildingStoreys.get(i);
            EList<IfcRelDecomposes> relAggregates = storey.getIsDecomposedBy();
            for(int j=0;j<relAggregates.size();j++){
                for(IfcObjectDefinition ifcObjectDefinition : relAggregates.get(j).getRelatedObjects()){
                    gIds.add(ifcObjectDefinition.getGlobalId());

                    if(ifcObjectDefinition instanceof IfcSpace && samplesGids.contains(ifcObjectDefinition.getGlobalId())  ){
                        spacesCount++;
                        space = (IfcSpace) ifcObjectDefinition;
                        smartEvacSpace = new SmartEvacSpace(space.getGlobalId(),space.getName());
                        //getting centroids of space
                        spaceCentroids = processSpace(space,print_line);
                        //getting doors of space

                        spaceDoors = processSpaceDoors(space,print_line);
                        //getting stairs of space
                        print_line.printf("\nGeeting space stairs :");
                        ifcStairs = getSpaceStairs(space,print_line);
                        print_line.printf("\nSpace stairs list size :  "+ifcStairs.size());
                        //connecting goten doors to space ///Connect Doors to Space
                        connectDoorsToSpace(space, spaceDoors, smartEvacSpace, spaceSmartEvacDoors);
                        //connecting goten stairs to space  // Connect Stairs to space
                        ConnectStairsToSpace(smartEvacSpace, ifcStairs, spaceSmartEvacStairs);

                        this.smartEvacSpacesMap.put(space.getGlobalId(),smartEvacSpace);


                        /**
                         *
                         *   connecting the current space network to global navigation network
                         *
                         * **/
                        //connecting space's centroids to doors and stairs
                        spaceTinInterestPoints = createInterestPointsFromSpaceCentroids(space, spaceCentroids);
                        doorsInterestPoints    = CreateInterestPointsFromSpaceDoors(spaceSmartEvacDoors);
                        stairsInterestPoints   = CreateInterestPointsFromSpaceStairs(spaceSmartEvacStairs);


                        //TODO implement connecting centroids method
                        //TODO implement connecting centroid to space doors
                            //TODO find the nearest centroid to each space's door and connect it
                        //TODO implement connecting centroids to space stairs
                            //TODO find the nearest centroid to each space's stair and connect it
                        //TODO connect stair to door (if possible)

                        ///generating space interest point
                        smartEvacSpaces.put(space.getGlobalId(),smartEvacSpace);
                        System.out.println("drawing generated graph");
                        GraphicsLibrary graphicsLibrary = new GraphicsLibrary();
                        graphicsLibrary.drawGraph("D:\\workplace\\"+space.getName()+"_"+space.getGlobalId()+"graph_onespace.png",indoorNavigationNetworkMap,globalInterestPointsMap);
                        indoorNavigationNetworkMap = new HashMap<>();
                        globalInterestPointsMap = new HashMap<>();
                    }
                }
            }
        }
        print_line.close();

    }

    private List<InterestPoint> CreateInterestPointsFromSpaceStairs(List<SmartEvacStair> spaceSmartEvacStairs) {
        List<InterestPoint> stairsInterestPoints;
        InterestPoint interestPoint;
        stairsInterestPoints = new ArrayList<>();
        for(int m=0;m<spaceSmartEvacStairs.size();m++){
            interestPoint = new InterestPoint(null,true);
            interestPoint.setAssociatedElement(spaceSmartEvacStairs.get(m));
            interestPoint.setType("IfcStair");
            //graphBuilder.connect(spaceTinInterestPoints.get(l)).to(interestPoint).withEdge(new IndoorDistance(spaceTinInterestPoints.get(m).getVertex(),null));
            stairsInterestPoints.add(interestPoint);
            interestPoint.setGlobalId(spaceSmartEvacStairs.get(m).getStairGlobalId());
        }
        return stairsInterestPoints;
    }

    private List<InterestPoint> CreateInterestPointsFromSpaceDoors(List<SmartEvacDoor> spaceSmartEvacDoors) {
        List<InterestPoint> doorsInterestPoints;
        InterestPoint interestPoint;
        doorsInterestPoints = new ArrayList<>();
        IfcDoor ifcDoor;
        for(int m=0;m<spaceSmartEvacDoors.size();m++){
            ifcDoor = doorsMap.get(spaceSmartEvacDoors.get(m).getDoorGlobalId());
            if(ifcDoor !=null)
                interestPoint = new InterestPoint(getDoorPosition(ifcDoor),true);
            else
                interestPoint = new InterestPoint(null,true);
            interestPoint.setAssociatedElement(spaceSmartEvacDoors.get(m));
            interestPoint.setType("IfcDoor");
            //graphBuilder.connect(spaceTinInterestPoints.get(l)).to(interestPoint).withEdge(new IndoorDistance(spaceTinInterestPoints.get(m).getVertex(),null));
            doorsInterestPoints.add(interestPoint);
            interestPoint.setGlobalId(spaceSmartEvacDoors.get(m).getDoorGlobalId());
        }
        return doorsInterestPoints;
    }

    private List<InterestPoint> createInterestPointsFromSpaceCentroids(IfcSpace space, List<Vertex> spaceCentroids) {
        List<InterestPoint> spaceTinInterestPoints;
        InterestPoint interestPoint;
        spaceTinInterestPoints = new ArrayList<>();
        for(int  l =0;l<spaceCentroids.size();l++) {
            interestPoint = new InterestPoint(space.getGlobalId()+"_cd_"+l,spaceCentroids.get(l), false);
            interestPoint.setType("Centroid");
            globalInterestPointsMap.put(interestPoint.getGlobalId(),interestPoint);
            spaceTinInterestPoints.add(interestPoint);
        }
        return spaceTinInterestPoints;
    }

    private void ConnectStairsToSpace(SmartEvacSpace smartEvacSpace, List<IfcStair> ifcStairs, List<SmartEvacStair> spaceSmartEvacStairs) {
        SmartEvacStair smartEvacStair;
        for (IfcStair ifcStair : ifcStairs) {
            if (alreadyCreatedSmartEvacStairMap.containsKey(ifcStair.getGlobalId())) {
                smartEvacStair = alreadyCreatedSmartEvacStairMap.get(ifcStair.getGlobalId());
                smartEvacStair.setUpperLevelSpace(smartEvacSpace);
            } else {
                smartEvacStair = new SmartEvacStair(ifcStair.getGlobalId(), ifcStair.getName());
                smartEvacStair.setLowerLevelSpace(smartEvacSpace);
                alreadyCreatedSmartEvacStairMap.put(ifcStair.getGlobalId(), smartEvacStair);
            }
            smartEvacSpace.getStairs().add(smartEvacStair);
            spaceSmartEvacStairs.add(smartEvacStair);
        }
    }


    public List<SmartEvacSpace> getSpaceNeighbour(SmartEvacSpace space){
        List<SmartEvacSpace> smartEvacSpaces = new ArrayList<>();
        for(SmartEvacDoor door : space.getDoors())
            for(SmartEvacSpace evacSpace : door.getAssociatedSpaces())
                if(evacSpace!=space)
                    smartEvacSpaces.add(evacSpace);
        return smartEvacSpaces;
    }


    private void generateExamplesSpaces(Map<String, SmartEvacSpace> smartEvacSpaces) {
        FileWriter writer = null;
        try {
            writer = new FileWriter("F:\\workplace\\navigation_inside_bim_example.txt",true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        PrintWriter print_line = new PrintWriter(writer);


        Iterator<Map.Entry<String, SmartEvacSpace>> spacesIterator = smartEvacSpaces.entrySet().iterator();
        Map.Entry<String, SmartEvacSpace> entry;
        SmartEvacSpace node;
        Object o;
        GraphBuilder<String, Double> graphBuilder = GraphBuilder.<String, Double>create();
        HipsterDirectedGraph<String, Double> spacesGraph;
        print_line.printf("\nCreating the navigation graph");
        List<SmartEvacSpace> spacesId = new ArrayList<>();
        while(spacesIterator.hasNext()){
            entry = spacesIterator.next();
            node = entry.getValue();
            spacesId.add(node);
            for(SmartEvacSpace space : node.getNeighbours())
                graphBuilder.connect(node.getSpaceName()).to(space.getSpaceName()).withEdge(10.0);
        }
        spacesGraph = graphBuilder.createDirectedGraph();
        System.out.println("Navigation graph created");
        print_line.printf("\nNavigation graph created");
        System.out.println("Exemple 01");
        print_line.printf("\nExemple 01");
        System.out.println("\nConnecting : "+spacesId.get(0).getSpaceName()+" to "+spacesId.get(spacesId.size()-1).getSpaceName());
        print_line.printf("\nConnecting : "+spacesId.get(0).getSpaceName()+" to "+spacesId.get(spacesId.size()-1).getSpaceName());


        SearchProblem p = GraphSearchProblem
                .startingFrom(spacesId.get(0).getSpaceName())
                .in(spacesGraph)
                .takeCostsFromEdges()
                .build();
        print_line.printf("\nPrinting result");
        System.out.println("\nPrinting result");
        print_line.printf("\n"+Hipster.createDijkstra(p).search(spacesId.get(spacesId.size()-1).getSpaceName()));


        print_line.printf("\nExemple 02");
        System.out.println("\nConnecting : "+"1E22"+" to "+"1CC1");
        print_line.printf("\nConnecting : 1E22 to 1CC1");
        p = GraphSearchProblem
                .startingFrom("1E22")
                .in(spacesGraph)
                .takeCostsFromEdges()
                .build();
        print_line.printf("\nPrinting result");
        System.out.println("\nPrinting result");
        print_line.printf("\n"+ Hipster.createDijkstra(p).search("1CC1"));
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
                //print_line.printf("\nSpace Vertex  : (X,Y) :   ( " + vertex.getX() + ", " + vertex.getY() + " )");
                spaceBounds.add(vertex.getX(),vertex.getY());
            }
            //print_line.printf("\nCreating  TIN ");
            IncrementalTin tin = new IncrementalTin();
            tin.add(spaceVertices, null);
            //print_line.printf("\nTIN Created");
            /*
            try {
                print_line.printf("\nDrawing TIN");
                TinRenderingUtility.drawTin(tin, 500, 500, new File("D:\\workplace\\"+space.getName()+"_"+space.getGlobalId()+"_TIN.png"));
            } catch (IOException e) {
                print_line.printf("\nException occured when drawing tin");
                print_line.printf(e.getMessage());
                print_line.printf("\n\n\n");
            }
            */
            //print_line.printf("\nCreating local navigation network // Cleaning up the TIN");
            TrianglesWrapper  wrapper = new TrianglesWrapper();
            TriangleCollector.visitSimpleTriangles(tin,wrapper);
            List<Vertex> centroids = wrapper.getTrianglesCentroid();
            Rectangle2D bounds = tin.getBounds();
            //print_line.printf("\nCleaning up the TIN (Removing Centroids ouside geomtery bounds)");
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



    public List<IfcStair> getSpaceStairs(IfcSpace space,PrintWriter print_line){
        List<IfcStair> ifcStairs = new ArrayList<>();
        for(IfcRelContainedInSpatialStructure contained : space.getContainsElements())
            for(IfcProduct ifcProduct : contained.getRelatedElements()){
                System.out.println("Element :: "+ifcProduct.toString());
                print_line.printf("\nElement :"+ifcProduct.toString());
                if(ifcProduct instanceof IfcStair)
                    ifcStairs.add((IfcStair)ifcProduct);
            }

        return ifcStairs;
    }



    public static Vertex getDoorPosition(IfcDoor door){
        if( door.getObjectPlacement() != null  &&   door.getObjectPlacement()  instanceof IfcLocalPlacement){
            IfcLocalPlacement ifcLocalPlacement = (IfcLocalPlacement) door.getObjectPlacement();
            //System.out.println(ifcLocalPlacement.getRelativePlacement().toString());
            IfcAxis2Placement3D placement3D = (IfcAxis2Placement3D) ifcLocalPlacement.getRelativePlacement();
            return new Vertex(placement3D.getLocation().getCoordinates().get(0),placement3D.getLocation().getCoordinates().get(1),placement3D.getLocation().getCoordinates().get(2));
        }
        else return null;

    }

    public static List<IfcDoor> processSpaceDoors(IfcSpace space,PrintWriter print_line){
        //print_line.printf( "\n\n===> IfcSpace Name  : "+space.getName()+"   Global Id : "+space.getGlobalId() );
        List<IfcRelSpaceBoundary> listBounds = space.getBoundedBy();
        List<IfcDoor> doorsList = new ArrayList<>();
        Map<String,IfcDoor> doorsMap= new HashMap<>();
        Set<IfcDoor> ifcDoorsSet = new HashSet<>();
        IfcDoor door;
        IfcLocalPlacement ifcLocalPlacement;
        IfcAxis2Placement3D placement3D;
        int ifcDoorsCount = 0;
        for(IfcRelSpaceBoundary boundary : listBounds){
            if(boundary.getRelatedBuildingElement() != null  && boundary.getRelatedBuildingElement() instanceof IfcDoor) {
                door = (IfcDoor) boundary.getRelatedBuildingElement();
                ifcDoorsCount++;
                if(!doorsMap.containsKey( door.getGlobalId())){
                    doorsMap.put(  door.getGlobalId() ,door );
                    doorsList.add( door );
                }

                if( ((IfcDoor)boundary.getRelatedBuildingElement()).getObjectPlacement() != null  &&   ((IfcDoor)boundary.getRelatedBuildingElement()).getObjectPlacement()  instanceof IfcLocalPlacement){
                    ifcLocalPlacement =  (IfcLocalPlacement)   (  ((IfcDoor)boundary.getRelatedBuildingElement()).getObjectPlacement());
                    System.out.println(ifcLocalPlacement.getRelativePlacement().toString());
                    placement3D = (IfcAxis2Placement3D)ifcLocalPlacement.getRelativePlacement();
                    System.out.println("Coordinates "+  placement3D.getLocation().getCoordinates().get(0)+"  , "+placement3D.getLocation().getCoordinates().get(1));
                }

                //((IfcDoor)boundary.getRelatedBuildingElement()).getObjectPlacement().getPlacesObject()
            }
            if(boundary.getRelatedBuildingElement() != null  && boundary.getRelatedBuildingElement() instanceof IfcWall){
                List<IfcDoor> doors = extractDoorsFromWall((IfcWall)boundary.getRelatedBuildingElement());
                for(IfcDoor mydoor:doors ){
                    if(! doorsMap.containsKey(mydoor.getGlobalId())){
                        doorsMap.put( mydoor.getGlobalId()  , mydoor);
                        doorsList.add( mydoor );
                        ifcDoorsCount++;
                    }
                }
            }
        }
        List<IfcRelContainedInSpatialStructure> containedList = space.getContainsElements();
        for(IfcRelContainedInSpatialStructure elem : containedList ){
            if(elem.getRelatedElements() instanceof IfcDoor ){
                door =  (IfcDoor) elem.getRelatedElements();
                if( ! doorsMap.containsKey(door.getGlobalId())){
                    doorsMap.put( door.getGlobalId()  , door);
                    doorsList.add( door );
                    ifcDoorsCount++;
                }
            }
        }
        print_line.printf("\n===> IfcDoors Count  :  "+ifcDoorsCount);
        return doorsList;
    }

    private static List<IfcDoor> extractDoorsFromWall(IfcWall wall) {
        List<IfcDoor > ifcDoors = new ArrayList<IfcDoor >();
        for(IfcRelVoidsElement voidsElement : wall.getHasOpenings()){
            IfcOpeningElement ifcOpeningElement = (IfcOpeningElement) voidsElement.getRelatedOpeningElement();
            for (IfcRelFillsElement filling : ifcOpeningElement.getHasFillings()) {
                IfcElement ifcRelatedBuildingElement = filling.getRelatedBuildingElement();
                if (ifcRelatedBuildingElement instanceof IfcDoor)
                    ifcDoors.add( (IfcDoor)ifcRelatedBuildingElement );
            }
        }
        return ifcDoors;
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





