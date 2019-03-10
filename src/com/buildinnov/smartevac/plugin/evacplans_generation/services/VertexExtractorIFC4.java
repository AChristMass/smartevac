package com.buildinnov.smartevac.plugin.evacplans_generation.services;

import com.buildinnov.smartevac.plugin.evacplans_generation.services.models.InterestPoint;
import es.usc.citius.hipster.graph.GraphBuilder;
import es.usc.citius.hipster.graph.HipsterDirectedGraph;
import org.bimserver.emf.IfcModelInterface;
import org.bimserver.models.geometry.GeometryData;
import org.bimserver.models.geometry.GeometryPackage;
import org.bimserver.models.ifc4.*;
import org.bimserver.plugins.services.BimServerClientInterface;
import org.bimserver.plugins.services.Geometry;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.tinfour.common.IConstraint;
import org.tinfour.common.LinearConstraint;
import org.tinfour.common.Vertex;
import org.tinfour.demo.utils.TinRenderingUtility;
import org.tinfour.standard.IncrementalTin;
import org.tinfour.utils.TriangleCollector;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class VertexExtractorIFC4
{
    private static final int BYTES_PER_VERTEX = 12;


    public static void processLevels(IfcModelInterface model){
        List<IfcBuildingStorey> buildingStoreys = model.getAll(IfcBuildingStorey.class);
        IfcBuildingStorey storey;
        List<Vertex> spacesVertices;
        List<IfcRelAggregates> relAggregates;
        List<Vertex> resultVertices;
        IfcSpace space;
        for(int i=0;i<buildingStoreys.size();i++){
            spacesVertices= new ArrayList<Vertex>();
            storey = buildingStoreys.get(i);
            relAggregates = storey.getIsDecomposedBy();
            for(IfcRelAggregates aggregate : relAggregates)
                for(IfcObjectDefinition objectDefinition : aggregate.getRelatedObjects())
                    if(objectDefinition instanceof IfcSpace){
                        space = (IfcSpace) objectDefinition;
                        resultVertices = processPlacement(space.getObjectPlacement(),space.getRepresentation());
                        for(int k=0;k<resultVertices.size();k++)
                            if(  !isVertexInList(resultVertices.get(k),spacesVertices))
                                spacesVertices.add(resultVertices.get(k));
                            else System.out.println("Vertex already exist");

                        //spacesVertices.addAll(   );
                    }



            for (IfcRelContainedInSpatialStructure ifcRelContainedInSpatialStructure : storey.getContainsElements())
                for (IfcProduct ifcProduct : ifcRelContainedInSpatialStructure.getRelatedElements())
                    if (ifcProduct instanceof IfcSpace){
                        space = (IfcSpace) ifcProduct;
                        resultVertices = processPlacement(space.getObjectPlacement(),space.getRepresentation());
                        for(int k=0;k<resultVertices.size();k++)
                            if(  !isVertexInList(resultVertices.get(k),spacesVertices))
                                spacesVertices.add(resultVertices.get(k));
                            else System.out.println("Vertex already exist");
                    }
            IncrementalTin tin = new IncrementalTin(1.0);
            for(int j=0;j<spacesVertices.size();j++){
                System.out.println("Vertex : "+spacesVertices.get(j).getX()+", "+spacesVertices.get(j).getY());
                tin.add(spacesVertices.get(j));
            }
            if(spacesVertices.size()>0) {
                System.out.println("Data set not empty");
                try {
                    System.out.println("drawing the TIN with constraints");
                    TinRenderingUtility.drawTin(tin, 500, 500, new File("D:\\" + storey.getName() + "_.png"));
                } catch (IOException e) {
                    e.printStackTrace();
                }


            }else{
                System.out.println("Empty dataset : spaces vertices");
            }
        }
    }


    public static boolean isVertexInList(Vertex vertex,List<Vertex> vertices){
        Vertex vertex1;
        for(int i=0;i<vertices.size();i++){
            vertex1 = vertices.get(i);
            if(vertex.getX() == vertex1.getX() && vertex.getY() == vertex1.getY())
                return true;
        }
        return false;
    }

    public static void processSpace(IfcModelInterface model)
    {
        List<Vertex> SpaceVertices = new ArrayList<>();
        List<Vertex> wallsVertices;
        List<IConstraint> constraintsList;
        LinearConstraint constraint;
        IncrementalTin tin = new IncrementalTin(1.0);
        List<TINMonitor> tinMonitors = new ArrayList<>();
        int tinMonitorsCurrentIndex = 0;
        for(IfcSpace space : model.getAll(IfcSpace.class))
        {
            if(space.getGlobalId().equals("0jdEFTa3r2xhGxvrW4$Ox5")   || space.getGlobalId().equals("0AVVlLIDbCvOgtmmM8Bw76")) {
            //System.out.println("Getting Geometry o the space    ");
            //Geometry geometry = bimServerClientInterface.getGeometry(roid, (org.bimserver.models.ifc2x3tc1.IfcProduct) space);
            //System.out.println(geometry.toString());
                System.out.println("got the space : "+"Name : "+space.getName()+" Global Id : "+space.getGlobalId());
                SpaceVertices = processPlacement(space.getObjectPlacement(), space.getRepresentation());
                wallsVertices = getSpaceWallsVertices(space);
                constraintsList = new ArrayList<>();
                constraint = new LinearConstraint();
                constraintsList.add(constraint);
                System.out.println("The size of walls's vertices list :"+wallsVertices.size());
                for(Vertex vertex : wallsVertices) {
                    constraint.add(vertex);
                    //System.out.println("Walls Vertex : (x , y)  = (" + vertex.x + ", "+vertex.y + ")" );
                }
                if(wallsVertices.size() > 0){
                    if(tinMonitorsCurrentIndex>0){
                        tinMonitors.get(tinMonitorsCurrentIndex-1).getReportingIntervalInPercent();
                    }
                    System.out.println("Creating TIN");
                    tin.clear();
                    tinMonitors.add(new TINMonitor());
                    tin.add(SpaceVertices,new TINMonitor());
                    System.out.println("TIN Added");
                    tin.addConstraints(constraintsList,true);
                    System.out.println("Constraints Added");
                    try {
                        //System.out.println("drawing the TIN");
                        //TinRenderingUtility.drawTin(tin, 500, 500, new File("D:\\"+space.getName()+"_.png"));
                        System.out.println("drawing the TIN with constraints");
                        TinRenderingUtility.drawTin(tin, 500, 500, new File("D:\\"+space.getGlobalId()+"_wall_tin_with_cons_with_const.png"));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }else{
                    System.out.println("Space Name : "+space.getName()+"   Space GID : "+space.getGlobalId());
                }
            tinMonitorsCurrentIndex++;

            }
        }
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
            if(boundary.getRelatedBuildingElement() instanceof IfcWall){
                //System.out.println("its a wall");
                vertices.addAll(getWallVertices((IfcWall) boundary.getRelatedBuildingElement()));
            }
        }
        for(IfcRelContainedInSpatialStructure  contains : space.getContainsElements()){
            for(IfcProduct ifcProduct: contains.getRelatedElements())
                if(contains instanceof IfcWall || contains instanceof IfcWallStandardCase
                ){
                    System.out.println("its a wall in contains");
                    vertices.addAll(getWallVertices((IfcWall) contains));
                }


        }



        return vertices;
    }


    private static List<Vertex > processPlacement(IfcObjectPlacement objectPlacement, IfcProductRepresentation representation2) {
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

    public static List<Vertex> getSpaceBoundariesVertices(IfcSpace space){
        for(IfcRelSpaceBoundary boundary : space.getBoundedBy()){
            if(boundary.getRelatedBuildingElement() instanceof  IfcWall){

            }
        }
        return null;
    }

    private static List<Vertex> ProcessSpacePlacementAndShape(IfcLocalPlacement localPlacement, IfcProductDefinitionShape productDefinitionShape)
    {
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
        IfcAxis2Placement3D placement = extrudedAreaSolid.getPosition();
        IfcProfileDef profile = extrudedAreaSolid.getSweptArea();
        return processProfileObjectPlacementAndLocalPlacement(profile, placement, localPlacement);
    }

    private static List<Vertex> processProfileObjectPlacementAndLocalPlacement(
            IfcProfileDef profile,
            IfcAxis2Placement3D placement,
            IfcLocalPlacement localPlacement)
    {
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
