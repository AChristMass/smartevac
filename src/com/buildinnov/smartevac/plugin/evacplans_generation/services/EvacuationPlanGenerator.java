package com.buildinnov.smartevac.plugin.evacplans_generation.services;
import org.bimserver.emf.IfcModelInterface;
import org.bimserver.emf.OfflineGeometryGenerator;
import org.bimserver.interfaces.objects.SObjectType;
import org.bimserver.interfaces.objects.SProject;
import org.bimserver.models.ifc4.*;
import org.bimserver.plugins.PluginContext;
import org.bimserver.plugins.renderengine.RenderEngine;
import org.bimserver.plugins.renderengine.RenderEngineException;
import org.bimserver.plugins.renderengine.RenderEngineModel;
import org.bimserver.plugins.serializers.AbstractGeometrySerializer;
import org.bimserver.plugins.serializers.ProgressReporter;
import org.bimserver.plugins.serializers.SerializerException;
import org.bimserver.plugins.services.AbstractAddExtendedDataService;

import org.bimserver.plugins.services.BimServerClientInterface;
import org.bimserver.shared.exceptions.PluginException;
import org.eclipse.emf.ecore.EObject;


import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EvacuationPlanGenerator extends AbstractAddExtendedDataService {

    private static final String NAMESPACE = "http://bimserver.org/eventlog";
    public EvacuationPlanGenerator() {
        super(NAMESPACE);
    }

    Map<Integer, Map<String,IfcDoor>> doorsByLevel = new HashMap<Integer,Map<String,IfcDoor>>();
    ///used in the processBoundary method
    //to avoid to select the door twice
    private List<String> treatedDoorsGIDs = new ArrayList<String>();
    private  Integer currentLogicalLevel = 0;


    @Override
    public void init(PluginContext pluginContext) throws PluginException {
        super.init(pluginContext);
        System.out.println("initialising SmartEvacQueryPlugin");
    }

    @Override
    public void newRevision(RunningService runningService, BimServerClientInterface bimServerClientInterface, long poid, long roid, String userToken, long soid, SObjectType settings) throws Exception {
        System.out.println("new revision method invoked");
        SProject project =bimServerClientInterface.getServiceInterface().getProjectByPoid(poid);
        IfcModelInterface model =bimServerClientInterface.getModel(project,roid,false,false,true);

        System.out.println("Model gotten ");
        //processStoreys(model);
        //
        //model.getAll(IfcSpace.class);
        //List<IfcSpace> spaces = new ArrayList<>() ;
        //spaces.add((IfcSpace) model.getByGuid("0PeKSAvI59PfDaeCq6ElAJ"));

        //IfcWall ifcWall = model.getAll(IfcWall.class).get(0);
        //System.out.println("Wall found : ");
        //VertexExtractorIFC2x3.processSingleWall(ifcWall);
        //VertexExtractorIFC2x3.processSingleWall(ifcWall);
        /*List<IfcWall> walls =  model.getAll(IfcWall.class);
        for(EObject object : walls.get(0).getGeometry().getData().eContents()){
            System.out.println(object.toString());
        }
        */

        //System.out.println(walls.get(0).getGeometry().getData().eAllContents());
        //System.out.println("Wall vertices printed");
        if( project.getSchema().equals("ifc2x3tc1")){
            System.out.println("Project schema : IFC2x3tc1");
            //VertexExtractorIFC2x3.processSpace(model);
            //VertexExtractorIFC2x3.processLevels(model);
            VertexExtractorIFC2x3Tests.testManySpaces(model);

        }
        else {
            System.out.println("Project schema : IFC4");
            VertexExtractorIFC4.processSpace(model);
            //VertexExtractorIFC4.processLevels(model);
        }
        //VertexExtractorIFC2x3.processSingleWall(walls.get(0));

        /*int i;
        for(IfcSpace space : spaces){
            i=0;
            for(IfcRelSpaceBoundary boundary : space.getBoundedBy()){
                if(  boundary.getRelatedBuildingElement() instanceof IfcDoor){
                    i++;
                }
            }
            System.out.println("Number of doors associated : "+i);
        }*/
        //extractBuildingLevels(model);

    }


    private void extractBuildingLevels(IfcModelInterface model){
        List<IfcBuildingStorey> buildingStoreys = model.getAll(IfcBuildingStorey.class);
        int i = 0;
        for(IfcBuildingStorey storey : buildingStoreys ){
            i++;
            System.out.println("Etage  : "+ i);
            System.out.println("Nom de l'étage : "+storey.getName() +" Global Id : "+storey.getGlobalId());
            System.out.println("------------- Showing Building storey spaces");
            extractSpacesFromBuildingStorey(storey);
        }



    }

    private void extractSpacesFromBuildingStorey(IfcBuildingStorey storey){
        List<IfcRelAggregates> relAggregates = storey.getIsDecomposedBy();
        for(IfcRelAggregates aggregate : relAggregates)
            for(IfcObjectDefinition objectDefinition : aggregate.getRelatedObjects())
                if(objectDefinition instanceof IfcSpace)
                    System.out.println("[DecomposedBy]  Space Name  :" + objectDefinition.getName() +" Space global Id : "+objectDefinition.getGlobalId());
        for (IfcRelContainedInSpatialStructure ifcRelContainedInSpatialStructure : storey.getContainsElements())
            for (IfcProduct ifcProduct : ifcRelContainedInSpatialStructure.getRelatedElements())
                if (ifcProduct instanceof IfcSpace)
                    System.out.println("[ContainedInSpatialStructure] Space Name  :" + ifcProduct.getName() +" Space global Id : "+ifcProduct.getGlobalId());
    }


    private  void processSpaceVertices(IfcSpace space) throws SerializerException {
        System.out.println("Object Type of the space :" + space.getObjectType());
        for(IfcRelSpaceBoundary boundary : space.getBoundedBy()){
            processBoundary(boundary.getRelatedBuildingElement());
        }
        for(IfcRelContainedInSpatialStructure contains : space.getContainsElements()){
            for(IfcProduct ifcProduct: contains.getRelatedElements()){
                processBoundary(ifcProduct);
            }
        }
        //this.treatedDoorsGIDs.clear();
    }

    private  void processSpace(IfcSpace space) throws SerializerException {
        System.out.println("Object Type of the space :" + space.getObjectType());
        //Space boundry
        for(IfcRelSpaceBoundary  boundary : space.getBoundedBy()){
            processBoundary(boundary.getRelatedBuildingElement());
        }
        //Space contains
        for(IfcRelContainedInSpatialStructure  contains : space.getContainsElements()){
            for(IfcProduct ifcProduct: contains.getRelatedElements()){
                processBoundary(ifcProduct);
            }
        }
        //this.treatedDoorsGIDs.clear();
    }

    public   void processBoundary(IfcProduct ifcElement) throws SerializerException {
        if (ifcElement instanceof IfcWall) {
            IfcWall ifcWall = (IfcWall) ifcElement;
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
        } else if (ifcElement instanceof IfcDoor){
            processDoor((IfcDoor) ifcElement);
        }else if(ifcElement instanceof IfcWindow ) {

        }
    }


    private  void processDoor(IfcDoor ifcDoor){
        if(!this.treatedDoorsGIDs.contains(ifcDoor.getGlobalId())){
            this.doorsByLevel.get(this.currentLogicalLevel).put(ifcDoor.getGlobalId(),  ifcDoor);
            this.treatedDoorsGIDs.add(ifcDoor.getGlobalId());
        }
    }

    private void processSpaces(IfcModelInterface model) throws SerializerException {
        List<IfcSpace> allSpaces = model.getAll(IfcSpace.class);
        System.out.println("result size : "+ allSpaces.size());
        int i=0;
        for(IfcSpace space : allSpaces){
            System.out.println("BEGIN Processing Space ------------------------------------------------");
            System.out.println("Processing Space : "+space.getLongName()+" Global Id : "+space.getGlobalId());
            System.out.println("----------=>IfcRelSpaceBoundary ");
            System.out.println("    BEGIN GETTING ALL Space Boundaries : IfcRelSpaceBoundary ------------------------------------------------");
            for(IfcRelSpaceBoundary boundary : space.getBoundedBy()){
                System.out.println("        BEGIN Processing Boundary  ------------------------------------------------");
                System.out.println("Boundary name : "+boundary.getName()+" Global Id : "+boundary.getGlobalId());
                processBoundary(boundary.getRelatedBuildingElement());
                System.out.println("        END Processing Boundary  ------------------------------------------------");
            }
            System.out.println("    END GETTING ALL Space Boundaries : IfcRelSpaceBoundary ------------------------------------------------");

            System.out.println("    BEGIN GETTING ALL Space Boundaries : IfcRelContainedInSpatialStructure ------------------------------------------------");
            for(IfcRelContainedInSpatialStructure contains : space.getContainsElements()){
                System.out.println("Element name : "+contains.getName()+" Global Id : "+contains.getGlobalId());
            }
            System.out.println("    END GETTING ALL Space Boundaries : IfcRelContainedInSpatialStructure ------------------------------------------------");
            if(++i==3)
                break;
        }
        System.out.println("END Processing Space ------------------------------------------------");
    }

    private void processDoors(IfcModelInterface model){
        List<IfcDoor> ifcDoors = model.getAll(IfcDoor.class);
        for(IfcDoor door : ifcDoors){
            //System.out.println(door.getRe);

        }

    }

    private void processSlubs(IfcModelInterface model){
        List<IfcSlab> ifcSlabs = model.getAll(IfcSlab.class);
        for(IfcSlab slab : ifcSlabs){
            System.out.println("BEGIN Processing IfcSlab --------------------------------------------------------------------------");
            System.out.println("    Slab Name : "+slab.getName() +" Slab GID "+slab.getGlobalId());
            if (slab.getPredefinedType() == IfcSlabTypeEnum.ROOF) {
                System.out.println("    ROOF/ IfcSlabTypeEnum : "+slab.getPredefinedType());
            } else if (slab.getPredefinedType() == IfcSlabTypeEnum.FLOOR || slab.getPredefinedType() == IfcSlabTypeEnum.BASESLAB
                    || slab.getPredefinedType() == IfcSlabTypeEnum.LANDING || slab.getPredefinedType() == IfcSlabTypeEnum.NULL) {
                System.out.println("    FLOOR/ IfcSlabTypeEnum : "+slab.getPredefinedType());
            }
            System.out.println("END   Processing IfcSlab --------------------------------------------------------------------------");
        }
    }

    private void processStoreys(IfcModelInterface model )  throws SerializerException {
        List<IfcBuildingStorey> storeys = model.getAll(IfcBuildingStorey.class);
        System.out.println("All Storeys gotten");
        //init doors maps
        for(int i=0;i<storeys.size();i++)
            this.doorsByLevel.put(i,new HashMap<String,IfcDoor>());
        for(IfcBuildingStorey  storey : storeys){
            processStorey(storey);
            this.currentLogicalLevel++;
        }


        int j=0;
        for(int i=0;i<storeys.size();i++){
            System.out.println("-------------------------------------- Logical level : " + i);
            for( Map.Entry<String,IfcDoor> entry :  doorsByLevel.get(i).entrySet()){
                j++;
                System.out.println("        Door  Name : "+entry.getValue().getName()+"  Global Id : "+ entry.getValue().getGlobalId());
            }
        }
        System.out.println("Number of generated Doors : " + j);
    }

    private void processStoreyVertices(IfcBuildingStorey ifcBuildingStorey) throws SerializerException {
      /*  System.out.println("\n\nBEGIN Processing Storey     --------------------------------------------------------------------------");
        System.out.println("\n        BEGIN ifcRelContainedInSpatialStructure     --------------------------------------------------------------------------");
        System.out.println("                Storey Name :  " + ifcBuildingStorey.getLongName());
        List<IfcRelAggregates> relAggregates =   ifcBuildingStorey.getIsDecomposedBy();
        System.out.println("                Storey decomposition size : "+ relAggregates.size() );
        for(IfcRelAggregates aggregate : relAggregates){
            for(IfcObjectDefinition objectDefinition : aggregate.getRelatedObjects()){
                if(objectDefinition instanceof IfcSpace){
                    processSpaceVertices((IfcSpace) objectDefinition);
                }
                if(objectDefinition instanceof IfcBuildingStorey){
                    System.out.println("IfcBuildingStorey Name : " + objectDefinition.getName());
                }
            }
        }


        for (IfcRelContainedInSpatialStructure ifcRelContainedInSpatialStructure : ifcBuildingStorey.getContainsElements()) {
            for (IfcProduct ifcProduct : ifcRelContainedInSpatialStructure.getRelatedElements()) {
                processBoundary(ifcProduct);
                if (ifcProduct instanceof IfcSpace) {
                    IfcSpace ifcSpace = (IfcSpace) ifcProduct;
                    processSpaceVertices(ifcSpace);
                }
            }
        }


        System.out.println("        END ifcRelContainedInSpatialStructure       --------------------------------------------------------------------------");
        System.out.println("END Processing Storey     --------------------------------------------------------------------------");
        */
    }

    private void processStorey(IfcBuildingStorey ifcBuildingStorey) throws SerializerException {
       /* System.out.println("\n\nBEGIN Processing Storey     --------------------------------------------------------------------------");
        System.out.println("\n        BEGIN ifcRelContainedInSpatialStructure     --------------------------------------------------------------------------");
        System.out.println("                Storey Name :  " + ifcBuildingStorey.getLongName());
        List<IfcRelAggregates> relAggregates =   ifcBuildingStorey.getIsDecomposedBy();
        System.out.println("                Storey decomposition size : "+ relAggregates.size() );
        for(IfcRelAggregates aggregate : relAggregates){
            for(IfcObjectDefinition objectDefinition : aggregate.getRelatedObjects()){
                if(objectDefinition instanceof IfcSpace){
                    processSpace((IfcSpace) objectDefinition);
                }
                if(objectDefinition instanceof IfcBuildingStorey){
                    System.out.println("IfcBuildingStorey Name : " + objectDefinition.getName());
                }
            }
        }


        for (IfcRelContainedInSpatialStructure ifcRelContainedInSpatialStructure : ifcBuildingStorey.getContainsElements()) {
            for (IfcProduct ifcProduct : ifcRelContainedInSpatialStructure.getRelatedElements()) {
                processBoundary(ifcProduct);
                if (ifcProduct instanceof IfcSpace) {
                    IfcSpace ifcSpace = (IfcSpace) ifcProduct;
                    processSpace(ifcSpace);
                }
            }
        }

        for (IfcRelContainedInSpatialStructure spatialStructure : ifcBuildingStorey.getContainsElements()) {
            for (IfcProduct ifcProduct : spatialStructure.getRelatedElements()) {
                //System.out.println("---------------------------------> ifcProduct Name : "+ifcProduct.getName());
                if (ifcProduct instanceof IfcDoor){

                    this.processDoor((IfcDoor) ifcProduct);
                }

            }
        }

        System.out.println("        END ifcRelContainedInSpatialStructure       --------------------------------------------------------------------------");
        System.out.println("END Processing Storey     --------------------------------------------------------------------------");
        */
    }

    private void associateDoors(){
        //Map<Integer,Map<String,IfcDoor>> doorsByLevel = new HashMap<Integer,Map<String,IfcDoor>>();
        /*for(int i=0;i<storeys.size();i++){
            System.out.println("-------------------------------------- Logical level : " + i);
            for( Map.Entry<String,IfcDoor> entry :  doorsByLevel.get(i).entrySet()){
                j++;
                System.out.println("        Door  Name : "+entry.getValue().getName()+"  Global Id : "+ entry.getValue().getGlobalId());
            }
        }

        */
    }




}
