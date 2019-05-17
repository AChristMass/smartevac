package com.buildinnov.smartevac.plugin.evacplans_generation.services;

import com.buildinnov.smartevac.plugin.evacplans_generation.services.models.InterestPoint;
import com.buildinnov.smartevac.plugin.evacplans_generation.services.models.SmartEvacDoor;
import com.buildinnov.smartevac.plugin.evacplans_generation.services.models.SmartEvacSpace;
import com.buildinnov.smartevac.plugin.evacplans_generation.services.models.SmartEvacStair;
import es.usc.citius.hipster.algorithm.Hipster;
import es.usc.citius.hipster.graph.GraphBuilder;
import es.usc.citius.hipster.graph.GraphSearchProblem;
import es.usc.citius.hipster.graph.HipsterDirectedGraph;
import es.usc.citius.hipster.model.problem.SearchProblem;
import org.bimserver.emf.IfcModelInterface;
import org.bimserver.models.ifc4.*;
import org.eclipse.emf.common.util.EList;
import org.tinfour.common.Vertex;
import org.tinfour.standard.IncrementalTin;
import org.tinfour.utils.TriangleCollector;

import java.awt.geom.Rectangle2D;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;


public class VertexExtractorIFC4
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
        for(IfcDoor ifcDoor : ifcDoors){
            doorsMap.put(ifcDoor.getGlobalId(),ifcDoor);
        }



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

    public void processSample(IfcModelInterface model){
        this.prepareDoorsMap(model);
        this.prepareSpacesMap(model);
        this.prepareStairsMap(model);

        SmartEvacDoor d1 =  new SmartEvacDoor("32L5gmIu547Q9m_2tav9ym",this.doorsMap.get("32L5gmIu547Q9m_2tav9ym").getName());
        SmartEvacSpace s1 = new SmartEvacSpace("1hS0l0psT3ZP0d5DO1Dqbf",this.spacesMap.get("1hS0l0psT3ZP0d5DO1Dqbf").getName());
        s1.getDoors().add(d1);
        d1.getAssociatedSpaces().add(s1);
        InterestPoint ip1 = new InterestPoint(null,true);
        ip1.setAssociatedElement(d1);
        ip1.setLevelExit(false);
        ip1.setPrincipalExit(true);
        ip1.setType("IfcDoor");


        SmartEvacDoor d1p1 =  new SmartEvacDoor("2Ozc7DO$n3xg8r$nUPe3lI",this.doorsMap.get("2Ozc7DO$n3xg8r$nUPe3lI").getName());
        s1.getDoors().add(d1p1);
        d1p1.getAssociatedSpaces().add(s1);
        InterestPoint ip2 = new InterestPoint(null,true);
        ip2.setAssociatedElement(d1p1);
        ip2.setLevelExit(false);
        ip2.setPrincipalExit(true);
        ip2.setType("IfcDoor");


        SmartEvacDoor d2 =  new SmartEvacDoor("32L5gmIu547Q9m_2tav9pv",this.doorsMap.get("32L5gmIu547Q9m_2tav9pv").getName());
        SmartEvacSpace s2 = new SmartEvacSpace("1hS0l0psT3ZP0d5DO1Dqbe",this.spacesMap.get("1hS0l0psT3ZP0d5DO1Dqbe").getName());
        s1.getDoors().add(d1);
        d1.getAssociatedSpaces().add(s1);
        d1p1.getAssociatedSpaces().add(s2);
        s2.getDoors().add(d1p1);
        InterestPoint ip3 = new InterestPoint(null,true);
        ip3.setAssociatedElement(d2);
        ip3.setLevelExit(false);
        ip3.setPrincipalExit(true);
        ip3.setType("IfcDoor");


        SmartEvacStair st1 = new SmartEvacStair("2Ozc7DO$n3xg8r$nUPeAvl",this.stairsMap.get("2Ozc7DO$n3xg8r$nUPeAvl").getName());
        st1.setLowerLevelSpace(s2);
        InterestPoint ip4 = new InterestPoint(null,true);
        ip4.setAssociatedElement(st1);
        ip4.setLevelExit(false);
        ip4.setPrincipalExit(true);
        ip4.setType("IfcStair");
        SmartEvacSpace s3 = new SmartEvacSpace("1hS0l0psT3ZP0d5DO1DqWd",this.spacesMap.get("1hS0l0psT3ZP0d5DO1DqWd").getName());
        st1.setUpperLevelSpace(s3);


        SmartEvacDoor d3 =  new SmartEvacDoor("2AdidhgFj5CuQm2tLAXBLP",this.doorsMap.get("2AdidhgFj5CuQm2tLAXBLP").getName());
        s3.getDoors().add(d3);
        d3.getAssociatedSpaces().add(s3);
        InterestPoint ip5 = new InterestPoint(null,true);
        ip5.setAssociatedElement(d3);
        ip5.setLevelExit(false);
        ip5.setPrincipalExit(true);
        ip5.setType("IfcDoor");
        SmartEvacSpace s4 = new SmartEvacSpace("1hS0l0psT3ZP0d5DO1DqWO",this.spacesMap.get("1hS0l0psT3ZP0d5DO1DqWO").getName());
        d3.getAssociatedSpaces().add(s4);
        s4.getDoors().add(d3);


        SmartEvacDoor d4 =  new SmartEvacDoor("2AdidhgFj5CuQm2tLAXBGB",this.doorsMap.get("2AdidhgFj5CuQm2tLAXBGB").getName());
        d4.getAssociatedSpaces().add(s4);
        s4.getDoors().add(d4);
        InterestPoint ip6 = new InterestPoint(null,true);
        ip6.setAssociatedElement(d4);
        ip6.setLevelExit(false);
        ip6.setPrincipalExit(true);
        ip6.setType("IfcDoor");

        SmartEvacDoor d5 =  new SmartEvacDoor("2AdidhgFj5CuQm2tLAXB5F",this.doorsMap.get("2AdidhgFj5CuQm2tLAXB5F").getName());
        d5.getAssociatedSpaces().add(s4);
        s4.getDoors().add(d5);
        InterestPoint ip7 = new InterestPoint(null,true);
        ip7.setAssociatedElement(d5);
        ip7.setLevelExit(false);
        ip7.setPrincipalExit(true);
        ip7.setType("IfcDoor");

        SmartEvacDoor d6 =  new SmartEvacDoor("2AdidhgFj5CuQm2tLAXBBH",this.doorsMap.get("2AdidhgFj5CuQm2tLAXBBH").getName());
        d6.getAssociatedSpaces().add(s4);
        s4.getDoors().add(d6);
        InterestPoint ip8 = new InterestPoint(null,true);
        ip8.setAssociatedElement(d6);
        ip8.setLevelExit(false);
        ip8.setPrincipalExit(true);
        ip8.setType("IfcDoor");

        SmartEvacDoor d7 =  new SmartEvacDoor("2AdidhgFj5CuQm2tLAXBAH",this.doorsMap.get("2AdidhgFj5CuQm2tLAXBAH").getName());
        d7.getAssociatedSpaces().add(s4);
        s4.getDoors().add(d7);
        InterestPoint ip9 = new InterestPoint(null,true);
        ip9.setAssociatedElement(d7);
        ip9.setLevelExit(false);
        ip9.setPrincipalExit(true);
        ip9.setType("IfcDoor");

        SmartEvacSpace s5 = new SmartEvacSpace("1hS0l0psT3ZP0d5DO1DqWR",this.spacesMap.get("1hS0l0psT3ZP0d5DO1DqWR").getName());
        d4.getAssociatedSpaces().add(s5);
        s5.getDoors().add(d4);

        SmartEvacDoor d8 =  new SmartEvacDoor("2AdidhgFj5CuQm2tLAXB61",this.doorsMap.get("2AdidhgFj5CuQm2tLAXB61").getName());
        d8.getAssociatedSpaces().add(s5);
        s5.getDoors().add(d8);
        InterestPoint ip10 = new InterestPoint(null,true);
        ip10.setAssociatedElement(d8);
        ip10.setLevelExit(false);
        ip10.setPrincipalExit(true);
        ip10.setType("IfcDoor");

        SmartEvacDoor d9 =  new SmartEvacDoor("2AdidhgFj5CuQm2tLAXB7N",this.doorsMap.get("2AdidhgFj5CuQm2tLAXB7N").getName());
        d9.getAssociatedSpaces().add(s5);
        s5.getDoors().add(d9);
        InterestPoint ip11 = new InterestPoint(null,true);
        ip11.setAssociatedElement(d9);
        ip11.setLevelExit(false);
        ip11.setPrincipalExit(true);
        ip11.setType("IfcDoor");

        SmartEvacDoor d10 =  new SmartEvacDoor("2AdidhgFj5CuQm2tLAXB0v",this.doorsMap.get("2AdidhgFj5CuQm2tLAXB0v").getName());
        d10.getAssociatedSpaces().add(s5);
        s5.getDoors().add(d10);
        InterestPoint ip12 = new InterestPoint(null,true);
        ip12.setAssociatedElement(d10);
        ip12.setLevelExit(false);
        ip12.setPrincipalExit(true);
        ip12.setType("IfcDoor");

        SmartEvacDoor d11 =  new SmartEvacDoor("2AdidhgFj5CuQm2tLAXBZF",this.doorsMap.get("2AdidhgFj5CuQm2tLAXBZF").getName());
        d11.getAssociatedSpaces().add(s5);
        s5.getDoors().add(d11);
        InterestPoint ip13 = new InterestPoint(null,true);
        ip13.setAssociatedElement(d11);
        ip13.setLevelExit(false);
        ip13.setPrincipalExit(true);
        ip13.setType("IfcDoor");

        SmartEvacDoor d12 =  new SmartEvacDoor("2AdidhgFj5CuQm2tLAXBYx",this.doorsMap.get("2AdidhgFj5CuQm2tLAXBYx").getName());
        d12.getAssociatedSpaces().add(s5);
        s5.getDoors().add(d12);
        InterestPoint ip14 = new InterestPoint(null,true);
        ip14.setAssociatedElement(d12);
        ip14.setLevelExit(false);
        ip14.setPrincipalExit(true);
        ip14.setType("IfcDoor");


        SmartEvacSpace s6 = new SmartEvacSpace("1hS0l0psT3ZP0d5DO1DqWQ",this.spacesMap.get("1hS0l0psT3ZP0d5DO1DqWQ").getName());

        SmartEvacDoor d13 =  new SmartEvacDoor("2AdidhgFj5CuQm2tLAXBXV",this.doorsMap.get("2AdidhgFj5CuQm2tLAXBXV").getName());
        d13.getAssociatedSpaces().add(s6);
        s6.getDoors().add(d13);
        InterestPoint ip15 = new InterestPoint(null,true);
        ip15.setAssociatedElement(d13);
        ip15.setLevelExit(false);
        ip15.setPrincipalExit(true);
        ip15.setType("IfcDoor");
        SmartEvacSpace s7 = new SmartEvacSpace("1hS0l0psT3ZP0d5DO1DqYB",this.spacesMap.get("1hS0l0psT3ZP0d5DO1DqYB").getName());
        d13.getAssociatedSpaces().add(s7);
        s7.getDoors().add(d13);


        SmartEvacDoor d14 =  new SmartEvacDoor("2AdidhgFj5CuQm2tLAXBWp",this.doorsMap.get("2AdidhgFj5CuQm2tLAXBWp").getName());
        d14.getAssociatedSpaces().add(s6);
        s6.getDoors().add(d14);
        InterestPoint ip16 = new InterestPoint(null,true);
        ip16.setAssociatedElement(d14);
        ip16.setLevelExit(false);
        ip16.setPrincipalExit(true);
        ip16.setType("IfcDoor");
        SmartEvacSpace s8 = new SmartEvacSpace("1hS0l0psT3ZP0d5DO1DqY8",this.spacesMap.get("1hS0l0psT3ZP0d5DO1DqY8").getName());
        d14.getAssociatedSpaces().add(s8);
        s8.getDoors().add(d14);



        SmartEvacDoor d15 =  new SmartEvacDoor("2AdidhgFj5CuQm2tLAXB1B",this.doorsMap.get("2AdidhgFj5CuQm2tLAXB1B").getName());
        d15.getAssociatedSpaces().add(s6);
        s6.getDoors().add(d15);
        InterestPoint ip17 = new InterestPoint(null,true);
        ip17.setAssociatedElement(d15);
        ip17.setLevelExit(false);
        ip17.setPrincipalExit(true);
        ip17.setType("IfcDoor");
        SmartEvacSpace s9 = new SmartEvacSpace("1hS0l0psT3ZP0d5DO1DqWT",this.spacesMap.get("1hS0l0psT3ZP0d5DO1DqWT").getName());
        d15.getAssociatedSpaces().add(s9);
        s9.getDoors().add(d15);




        SmartEvacDoor d16 =  new SmartEvacDoor("2AdidhgFj5CuQm2tLAXB2B",this.doorsMap.get("2AdidhgFj5CuQm2tLAXBZF").getName());
        d16.getAssociatedSpaces().add(s6);
        s6.getDoors().add(d16);
        InterestPoint ip18 = new InterestPoint(null,true);
        ip18.setAssociatedElement(d16);
        ip18.setLevelExit(false);
        ip18.setPrincipalExit(true);
        ip18.setType("IfcDoor");
        SmartEvacSpace s10 = new SmartEvacSpace("1hS0l0psT3ZP0d5DO1DqWS",this.spacesMap.get("1hS0l0psT3ZP0d5DO1DqWS").getName());
        d16.getAssociatedSpaces().add(s10);
        s10.getDoors().add(d16);


        SmartEvacDoor d17 =  new SmartEvacDoor("2AdidhgFj5CuQm2tLAX8y1",this.doorsMap.get("2AdidhgFj5CuQm2tLAXBYx").getName());
        d17.getAssociatedSpaces().add(s6);
        s6.getDoors().add(d17);
        InterestPoint ip19 = new InterestPoint(null,true);
        ip19.setAssociatedElement(d17);
        ip19.setLevelExit(false);
        ip19.setPrincipalExit(true);
        ip19.setType("IfcDoor");
        SmartEvacSpace s11 = new SmartEvacSpace("1hS0l0psT3ZP0d5DO1DqY9",this.spacesMap.get("1hS0l0psT3ZP0d5DO1DqY9").getName());
        d17.getAssociatedSpaces().add(s11);
        s11.getDoors().add(d17);



        SmartEvacDoor d18 =  new SmartEvacDoor("2AdidhgFj5CuQm2tLAXB3$",this.doorsMap.get("2AdidhgFj5CuQm2tLAXB3$").getName());
        d18.getAssociatedSpaces().add(s6);
        s6.getDoors().add(d18);
        InterestPoint ip20 = new InterestPoint(null,true);
        ip20.setAssociatedElement(d18);
        ip20.setLevelExit(false);
        ip20.setPrincipalExit(true);
        ip20.setType("IfcDoor");
        SmartEvacSpace s12 = new SmartEvacSpace("1hS0l0psT3ZP0d5DO1DqWV",this.spacesMap.get("1hS0l0psT3ZP0d5DO1DqWV").getName());
        d18.getAssociatedSpaces().add(s12);
        s12.getDoors().add(d18);



        SmartEvacDoor d19 =  new SmartEvacDoor("2AdidhgFj5CuQm2tLAX8zx",this.doorsMap.get("2AdidhgFj5CuQm2tLAX8zx").getName());
        d19.getAssociatedSpaces().add(s6);
        s6.getDoors().add(d19);
        InterestPoint ip21 = new InterestPoint(null,true);
        ip21.setAssociatedElement(d19);
        ip21.setLevelExit(false);
        ip21.setPrincipalExit(true);
        ip21.setType("IfcDoor");
        SmartEvacSpace s13 = new SmartEvacSpace("1hS0l0psT3ZP0d5DO1DqWV",this.spacesMap.get("1hS0l0psT3ZP0d5DO1DqWV").getName());
        d19.getAssociatedSpaces().add(s13);
        s13.getDoors().add(d19);



        SmartEvacDoor d20 =  new SmartEvacDoor("2AdidhgFj5CuQm2tLAX8_p",this.doorsMap.get("2AdidhgFj5CuQm2tLAX8_p").getName());
        d20.getAssociatedSpaces().add(s6);
        s6.getDoors().add(d20);
        InterestPoint ip22 = new InterestPoint(null,true);
        ip22.setAssociatedElement(d20);
        ip22.setLevelExit(false);
        ip22.setPrincipalExit(true);
        ip22.setType("IfcDoor");
        d20.getAssociatedSpaces().add(s6);
        s6.getDoors().add(d20);


        SmartEvacDoor d21 =  new SmartEvacDoor("2AdidhgFj5CuQm2tLAX8_R",this.doorsMap.get("2AdidhgFj5CuQm2tLAX8_R").getName());
        d21.getAssociatedSpaces().add(s6);
        s6.getDoors().add(d21);
        InterestPoint ip23 = new InterestPoint(null,true);
        ip23.setAssociatedElement(d21);
        ip23.setLevelExit(false);
        ip23.setPrincipalExit(true);
        ip23.setType("IfcDoor");
        SmartEvacSpace s14 = new SmartEvacSpace("1hS0l0psT3ZP0d5DO1DqYE",this.spacesMap.get("1hS0l0psT3ZP0d5DO1DqYE").getName());
        d21.getAssociatedSpaces().add(s14);
        s14.getDoors().add(d21);


        SmartEvacSpace s15 = new SmartEvacSpace("1hS0l0psT3ZP0d5DO1DqWb",this.spacesMap.get("1hS0l0psT3ZP0d5DO1DqWb").getName());
        d20.getAssociatedSpaces().add(s15);
        s15.getDoors().add(d20);


        SmartEvacDoor d22 =  new SmartEvacDoor("2AdidhgFj5CuQm2tLAX8vd",this.doorsMap.get("2AdidhgFj5CuQm2tLAX8vd").getName());
        d22.getAssociatedSpaces().add(s15);
        s15.getDoors().add(d22);
        InterestPoint ip24 = new InterestPoint(null,true);
        ip24.setAssociatedElement(d22);
        ip24.setLevelExit(false);
        ip24.setPrincipalExit(true);
        ip24.setType("IfcDoor");
        SmartEvacSpace s16 = new SmartEvacSpace("1hS0l0psT3ZP0d5DO1DqX_",this.spacesMap.get("1hS0l0psT3ZP0d5DO1DqX_").getName());
        d22.getAssociatedSpaces().add(s16);
        s16.getDoors().add(d22);

        SmartEvacDoor d23 =  new SmartEvacDoor("2AdidhgFj5CuQm2tLAX8up",this.doorsMap.get("2AdidhgFj5CuQm2tLAX8up").getName());
        d23.getAssociatedSpaces().add(s15);
        s15.getDoors().add(d23);
        InterestPoint ip25 = new InterestPoint(null,true);
        ip25.setAssociatedElement(d23);
        ip25.setLevelExit(false);
        ip25.setPrincipalExit(true);
        ip25.setType("IfcDoor");
        SmartEvacSpace s17 = new SmartEvacSpace("1hS0l0psT3ZP0d5DO1DqX$",this.spacesMap.get("1hS0l0psT3ZP0d5DO1DqX$").getName());
        d23.getAssociatedSpaces().add(s17);
        s17.getDoors().add(d23);

        SmartEvacDoor d24 =  new SmartEvacDoor("2AdidhgFj5CuQm2tLAX8nX",this.doorsMap.get("2AdidhgFj5CuQm2tLAX8nX").getName());
        d24.getAssociatedSpaces().add(s15);
        s15.getDoors().add(d24);
        InterestPoint ip26 = new InterestPoint(null,true);
        ip26.setAssociatedElement(d24);
        ip26.setLevelExit(false);
        ip26.setPrincipalExit(true);
        ip26.setType("IfcDoor");
        SmartEvacSpace s18 = new SmartEvacSpace("1hS0l0psT3ZP0d5DO1DqXo",this.spacesMap.get("1hS0l0psT3ZP0d5DO1DqXo").getName());
        d24.getAssociatedSpaces().add(s18);
        s18.getDoors().add(d24);


        SmartEvacDoor d25 =  new SmartEvacDoor("2AdidhgFj5CuQm2tLAX8sx",this.doorsMap.get("2AdidhgFj5CuQm2tLAX8sx").getName());
        d25.getAssociatedSpaces().add(s15);
        s15.getDoors().add(d25);
        InterestPoint ip27 = new InterestPoint(null,true);
        ip27.setAssociatedElement(d25);
        ip27.setLevelExit(false);
        ip27.setPrincipalExit(true);
        ip27.setType("IfcDoor");
        SmartEvacSpace s19 = new SmartEvacSpace("1hS0l0psT3ZP0d5DO1DqXz",this.spacesMap.get("1hS0l0psT3ZP0d5DO1DqXz").getName());
        d25.getAssociatedSpaces().add(s19);
        s19.getDoors().add(d25);

        SmartEvacDoor d26 =  new SmartEvacDoor("2AdidhgFj5CuQm2tLAX8vx",this.doorsMap.get("2AdidhgFj5CuQm2tLAX8vx").getName());
        d26.getAssociatedSpaces().add(s15);
        s15.getDoors().add(d26);
        InterestPoint ip28 = new InterestPoint(null,true);
        ip28.setAssociatedElement(d26);
        ip28.setLevelExit(false);
        ip28.setPrincipalExit(true);
        ip28.setType("IfcDoor");
        SmartEvacSpace s20 = new SmartEvacSpace("1hS0l0psT3ZP0d5DO1DqXv",this.spacesMap.get("1hS0l0psT3ZP0d5DO1DqXv").getName());
        d26.getAssociatedSpaces().add(s20);
        s20.getDoors().add(d26);

        SmartEvacDoor d27 =  new SmartEvacDoor("2AdidhgFj5CuQm2tLAX8rD",this.doorsMap.get("2AdidhgFj5CuQm2tLAX8rD").getName());
        d27.getAssociatedSpaces().add(s15);
        s15.getDoors().add(d27);
        InterestPoint ip29 = new InterestPoint(null,true);
        ip29.setAssociatedElement(d27);
        ip29.setLevelExit(false);
        ip29.setPrincipalExit(true);
        ip29.setType("IfcDoor");
        SmartEvacSpace s21 = new SmartEvacSpace("1hS0l0psT3ZP0d5DO1DqYE",this.spacesMap.get("1hS0l0psT3ZP0d5DO1DqYE").getName());
        d27.getAssociatedSpaces().add(s21);
        s21.getDoors().add(d27);

        SmartEvacDoor d28 =  new SmartEvacDoor("2AdidhgFj5CuQm2tLAX8wZ",this.doorsMap.get("2AdidhgFj5CuQm2tLAX8wZ").getName());
        d28.getAssociatedSpaces().add(s15);
        s15.getDoors().add(d28);
        InterestPoint ip30 = new InterestPoint(null,true);
        ip30.setAssociatedElement(d21);
        ip30.setLevelExit(false);
        ip30.setPrincipalExit(true);
        ip30.setType("IfcDoor");
        SmartEvacSpace s22 = new SmartEvacSpace("1hS0l0psT3ZP0d5DO1DqYF",this.spacesMap.get("1hS0l0psT3ZP0d5DO1DqYF").getName());
        d28.getAssociatedSpaces().add(s22);
        s22.getDoors().add(d28);

        SmartEvacDoor d29 =  new SmartEvacDoor("2AdidhgFj5CuQm2tLAX8qN",this.doorsMap.get("2AdidhgFj5CuQm2tLAX8qN").getName());
        d29.getAssociatedSpaces().add(s15);
        s15.getDoors().add(d29);
        InterestPoint ip31 = new InterestPoint(null,true);
        ip31.setAssociatedElement(d21);
        ip31.setLevelExit(false);
        ip31.setPrincipalExit(true);
        ip31.setType("IfcDoor");
        SmartEvacSpace s23 = new SmartEvacSpace("1hS0l0psT3ZP0d5DO1DqXu",this.spacesMap.get("1hS0l0psT3ZP0d5DO1DqXu").getName());
        d29.getAssociatedSpaces().add(s23);
        s23.getDoors().add(d29);

        SmartEvacDoor d30 =  new SmartEvacDoor("2AdidhgFj5CuQm2tLAX8xH",this.doorsMap.get("2AdidhgFj5CuQm2tLAX8xH").getName());
        d30.getAssociatedSpaces().add(s15);
        s15.getDoors().add(d30);
        InterestPoint ip32 = new InterestPoint(null,true);
        ip32.setAssociatedElement(d30);
        ip32.setLevelExit(false);
        ip32.setPrincipalExit(true);
        ip32.setType("IfcDoor");


        SmartEvacSpace s24 = new SmartEvacSpace("1hS0l0psT3ZP0d5DO1DqWa",this.spacesMap.get("1hS0l0psT3ZP0d5DO1DqWa").getName());
        d30.getAssociatedSpaces().add(s24);
        s24.getDoors().add(d30);



        SmartEvacDoor d31 =  new SmartEvacDoor("2AdidhgFj5CuQm2tLAXBSN",this.doorsMap.get("2AdidhgFj5CuQm2tLAXBSN").getName());
        d31.getAssociatedSpaces().add(s24);
        s24.getDoors().add(d31);
        InterestPoint ip33 = new InterestPoint(null,true);
        ip32.setAssociatedElement(d31);
        ip32.setLevelExit(false);
        ip32.setPrincipalExit(true);
        ip32.setType("IfcDoor");

        SmartEvacSpace s25 = new SmartEvacSpace("1hS0l0psT3ZP0d5DO1DqWP",this.spacesMap.get("1hS0l0psT3ZP0d5DO1DqWP").getName());
        d31.getAssociatedSpaces().add(s25);
        s25.getDoors().add(d31);

        /*
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

        */


    }


    public static void processSamplesSpaces(IfcModelInterface model){

        FileWriter writer = null;
        try {
            writer = new FileWriter("F:\\IFC_samples_log.txt",true);
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



    public  void processLevelsFull(IfcModelInterface model) {
        FileWriter writer = null;
        try {
            writer = new FileWriter("F:\\workplace\\IFC_levels_processing_log_full.txt",false);
        } catch (IOException e) {
            e.printStackTrace();
        }
        PrintWriter print_line = new PrintWriter(writer);
        List<String> samplesGids = new ArrayList<>();
        /*
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
        */
        samplesGids.add("1hS0l0psT3ZP0d5DO1DqWd");
        //samplesGids.add("1hS0l0psT3ZP0d5DO1Dqbe");
        samplesGids.add("1hS0l0psT3ZP0d5DO1Dqbf");
        samplesGids.add("1hS0l0psT3ZP0d5DO1DqWO");
        samplesGids.add("1hS0l0psT3ZP0d5DO1DqWR");
        samplesGids.add("1hS0l0psT3ZP0d5DO1DqYL");
        samplesGids.add("1hS0l0psT3ZP0d5DO1DqYA");
        samplesGids.add("1hS0l0psT3ZP0d5DO1DqWQ");
        samplesGids.add("1hS0l0psT3ZP0d5DO1DqYB");
        samplesGids.add("1hS0l0psT3ZP0d5DO1DqY8");
        samplesGids.add("1hS0l0psT3ZP0d5DO1DqWT");
        samplesGids.add("1hS0l0psT3ZP0d5DO1DqWS");
        samplesGids.add("1hS0l0psT3ZP0d5DO1DqWT");
        samplesGids.add("1hS0l0psT3ZP0d5DO1DqWS");
        samplesGids.add("1hS0l0psT3ZP0d5DO1DqWV");
        samplesGids.add("1hS0l0psT3ZP0d5DO1DqWb");
        samplesGids.add("1hS0l0psT3ZP0d5DO1DqX_");
        samplesGids.add("1hS0l0psT3ZP0d5DO1DqX$");
        samplesGids.add("1hS0l0psT3ZP0d5DO1DqXo");
        samplesGids.add("1hS0l0psT3ZP0d5DO1DqXz");
        samplesGids.add("1hS0l0psT3ZP0d5DO1DqXy");
        samplesGids.add("1hS0l0psT3ZP0d5DO1DqXv");
        samplesGids.add("1hS0l0psT3ZP0d5DO1DqXu");
        samplesGids.add("1hS0l0psT3ZP0d5DO1DqYF");
        samplesGids.add("1hS0l0psT3ZP0d5DO1DqWa");
        samplesGids.add("1hS0l0psT3ZP0d5DO1DqXx");
        samplesGids.add("1hS0l0psT3ZP0d5DO1DqWP");


        /*
        samplesGids.add("1hS0l0psT3ZP0d5DO1Dqbe");
        samplesGids.add("1hS0l0psT3ZP0d5DO1DqWd");
*/


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
            EList<IfcRelAggregates> relAggregates = storey.getIsDecomposedBy();
            for(int j=0;j<relAggregates.size();j++){
                for(IfcObjectDefinition ifcObjectDefinition : relAggregates.get(j).getRelatedObjects()){
                    gIds.add(ifcObjectDefinition.getGlobalId());
                    //&& samplesGids.contains(ifcObjectDefinition.getGlobalId())
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
                        /**
                         *
                         * Connecting goten doors and stairs to space
                         *
                         *In
                         * */

                        //connecting goten doors to space
                        for(IfcDoor  ifcDoor : spaceDoors ){
                            if(alreadyCreatedSmartEvacDoors.containsKey(ifcDoor.getGlobalId()))
                                smartEvacDoor = alreadyCreatedSmartEvacDoors.get(ifcDoor.getGlobalId());
                            else {
                                smartEvacDoor = new SmartEvacDoor(ifcDoor.getGlobalId(), space.getName()+":"+ifcDoor.getGlobalId());
                                alreadyCreatedSmartEvacDoors.put(ifcDoor.getGlobalId(),smartEvacDoor);

                            }
                            smartEvacDoor.getAssociatedSpaces().add(smartEvacSpace);
                            smartEvacSpace.getDoors().add(smartEvacDoor);
                            spaceSmartEvacDoors.add(smartEvacDoor);
                        }
                        //connecting goten stairs to space
                        /*
                        for(IfcStair ifcStair : ifcStairs){
                            if(alreadyCreatedSmartEvacStairMap.containsKey(ifcStair.getGlobalId())) {
                                smartEvacStair = alreadyCreatedSmartEvacStairMap.get(ifcStair.getGlobalId());
                                smartEvacStair.setUpperLevelSpace(smartEvacSpace);
                            }
                            else {
                                smartEvacStair = new SmartEvacStair(ifcStair.getGlobalId(), ifcStair.getName());
                                smartEvacStair.setLowerLevelSpace(smartEvacSpace);
                                alreadyCreatedSmartEvacStairMap.put(ifcStair.getGlobalId(),smartEvacStair);
                            }
                            smartEvacSpace.getStairs().add(smartEvacStair);
                            spaceSmartEvacStairs.add(smartEvacStair);
                        }
                        */

                        this.smartEvacSpacesMap.put(space.getGlobalId(),smartEvacSpace);


                        /**
                         *
                         *   connecting the current space network to global navigation network
                         *
                         * **/
                        //connecting space's centroids to doors and stairs
                        /*
                        spaceTinInterestPoints = new ArrayList<>();
                        for(int  l =0;l<spaceCentroids.size();l++) {
                            interestPoint = new InterestPoint(space.getGlobalId()+"_cd_"+l,spaceCentroids.get(l), false);
                            interestPoint.setType("Centroid");
                            globalInterestPointsMap.put(interestPoint.getGlobalId(),interestPoint);
                            spaceTinInterestPoints.add(interestPoint);
                        }
                        */


                        /**
                         * Generating doorsInterestPoints
                         * */

                        /*
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
                        */

                        /*
                        stairsInterestPoints = new ArrayList<>();
                        for(int m=0;m<spaceSmartEvacStairs.size();m++){
                            interestPoint = new InterestPoint(null,true);
                            interestPoint.setAssociatedElement(spaceSmartEvacStairs.get(m));
                            interestPoint.setType("IfcStair");
                            //graphBuilder.connect(spaceTinInterestPoints.get(l)).to(interestPoint).withEdge(new IndoorDistance(spaceTinInterestPoints.get(m).getVertex(),null));
                            stairsInterestPoints.add(interestPoint);
                            interestPoint.setGlobalId(spaceSmartEvacStairs.get(m).getStairGlobalId());
                        }
                        */


                        /*
                        for(int l=0;l<spaceTinInterestPoints.size();l++) {
                            if(!doneInterestPoints.contains(spaceTinInterestPoints.get(l).getGlobalId())) {
                                //connecting centroids
                                spaceTinOtherInterestPoints = new ArrayList<>();
                                for (int m = 0; m < spaceTinInterestPoints.size(); m++) {
                                    concatTwoGID = doneInterestPoints.contains(spaceTinInterestPoints.get(l).getGlobalId().concat(spaceTinInterestPoints.get(m).getGlobalId()))
                                            || doneInterestPoints.contains(spaceTinInterestPoints.get(m).getGlobalId().concat(spaceTinInterestPoints.get(l).getGlobalId()));
                                    if (l != m && !concatTwoGID) {
                                        doneInterestPoints.add(spaceTinInterestPoints.get(l).getGlobalId().concat(spaceTinInterestPoints.get(m).getGlobalId()));
                                        spaceTinOtherInterestPoints.add(spaceTinInterestPoints.get(m));
                                        //graphBuilder.connect(spaceTinInterestPoints.get(l)).to(spaceTinInterestPoints.get(m)).withEdge(new IndoorDistance(spaceTinInterestPoints.get(l).getVertex(), spaceTinInterestPoints.get(m).getVertex()));
                                    }
                                }



                                if (!indoorNavigationNetworkMap.containsKey(spaceTinInterestPoints.get(l).getGlobalId())) {
                                    //System.out.println("Centroid  doesn't exist");
                                    indoorNavigationNetworkMap.put(spaceTinInterestPoints.get(l).getGlobalId(), spaceTinOtherInterestPoints);

                                } else {
                                    //System.out.println("Centroid already exist");
                                    indoorNavigationNetworkMap.get(spaceTinInterestPoints.get(l).getGlobalId()).addAll(spaceTinOtherInterestPoints);
                                }

                                doneInterestPoints.add(spaceTinInterestPoints.get(l).getGlobalId());

                            }



                            //connecting centroids to space doors
                            //centroid not added before
                            if(!indoorNavigationNetworkMap.containsKey(spaceTinInterestPoints.get(l).getGlobalId()))   {
                                //System.out.println("indoorNavigationNetworkMap dont contains");
                                indoorNavigationNetworkMap.put(spaceTinInterestPoints.get(l).getGlobalId(),  doorsInterestPoints );
                                indoorNavigationNetworkMap.put(spaceTinInterestPoints.get(l).getGlobalId(),  stairsInterestPoints );
                            }
                            else{
                                //Centroid already existe
                                //System.out.println("Centroid Interest Point already exist");

                                interestPoints = indoorNavigationNetworkMap.get(spaceTinInterestPoints.get(l).getGlobalId());
                                temp = doorsInterestPoints;
                                for(InterestPoint point : temp){
                                    if(! interestPoints.contains(point))
                                        indoorNavigationNetworkMap.get(spaceTinInterestPoints.get(l).getGlobalId()).add(point);


                                    if( ! indoorNavigationNetworkMap.containsKey(point.getGlobalId())){
                                        interestPoints = new ArrayList<>();
                                        interestPoints.addAll(spaceTinInterestPoints);
                                        indoorNavigationNetworkMap.put(point.getGlobalId(),interestPoints);
                                    }else{
                                        for(InterestPoint pointCentroid : spaceTinInterestPoints)
                                            if( !indoorNavigationNetworkMap.get(point.getGlobalId()).contains(pointCentroid) )
                                                indoorNavigationNetworkMap.get(point.getGlobalId()).add(pointCentroid);
                                            else {
                                                indexO = indoorNavigationNetworkMap.get(point.getGlobalId()).indexOf(pointCentroid);
                                                pp = indoorNavigationNetworkMap.get(point.getGlobalId()).get(indexO);
                                                //System.out.println("Door map already contains point diff DoorPGID  : " +pointCentroid.getGlobalId()+" centroid GID :"+pp.getGlobalId());
                                                //print_line.printf("\nDoor map already contains point diff DoorPGID  : " +pointCentroid.getGlobalId()+" centroid GID :"+pp.getGlobalId());
                                            }
                                    }
                                    //if(! globalInterestPointsMap.containsKey(interestPoint.getGlobalId()))
                                     //   globalInterestPointsMap.put(interestPoint.getGlobalId(),interestPoint);

                                }
                                temp = stairsInterestPoints;
                                for(InterestPoint point : temp){
                                    if(! interestPoints.contains(point))
                                        indoorNavigationNetworkMap.get(spaceTinInterestPoints.get(l).getGlobalId()).add(point);


                                    if( ! indoorNavigationNetworkMap.containsKey(point.getGlobalId())){
                                        interestPoints = new ArrayList<>();
                                        interestPoints.addAll(spaceTinInterestPoints);
                                        indoorNavigationNetworkMap.put(point.getGlobalId(),interestPoints);
                                    }else{
                                        for(InterestPoint pointCentroid : spaceTinInterestPoints)
                                            if( !indoorNavigationNetworkMap.get(point.getGlobalId()).contains(pointCentroid) )
                                                indoorNavigationNetworkMap.get(point.getGlobalId()).add(pointCentroid);
                                            else {
                                                indexO = indoorNavigationNetworkMap.get(point.getGlobalId()).indexOf(pointCentroid);
                                                pp = indoorNavigationNetworkMap.get(point.getGlobalId()).get(indexO);
                                                //System.out.println("Door map already contains point diff StairPGID  : " +pointCentroid.getGlobalId()+" centroid GID :"+pp.getGlobalId());
                                                //print_line.printf("\nDoor map already contains point diff StairPGID  : " +pointCentroid.getGlobalId()+" centroid GID :"+pp.getGlobalId());
                                            }
                                    }
                                    //if(! globalInterestPointsMap.containsKey(interestPoint.getGlobalId()))
                                    //   globalInterestPointsMap.put(interestPoint.getGlobalId(),interestPoint);
                                }

                            }



                            //connecting centroids to space stairs
                            if(!indoorNavigationNetworkMap.containsKey(spaceTinInterestPoints.get(l).getGlobalId()))   {
                                indoorNavigationNetworkMap.put(spaceTinInterestPoints.get(l).getGlobalId(),  stairsInterestPoints );
                            }
                            else{
                                interestPoints = indoorNavigationNetworkMap.get(spaceTinInterestPoints.get(l).getGlobalId());
                                for(InterestPoint point : stairsInterestPoints)
                                    if(! interestPoints.contains(point))
                                        indoorNavigationNetworkMap.get(spaceTinInterestPoints.get(l).getGlobalId()).add(point);

                            }
                        }
                        */
                        //connecting stairs to doors
                        /*
                        for(int l=0;l<doorsInterestPoints.size();l++){

                            if(! globalInterestPointsMap.containsKey(doorsInterestPoints.get(l).getGlobalId()))
                                globalInterestPointsMap.put(doorsInterestPoints.get(l).getGlobalId(),doorsInterestPoints.get(l));


                            for(int m=0;m<stairsInterestPoints.size();m++){
                                if(! globalInterestPointsMap.containsKey(stairsInterestPoints.get(m).getGlobalId()))
                                    globalInterestPointsMap.put(stairsInterestPoints.get(m).getGlobalId(),stairsInterestPoints.get(m));
                                if( ! indoorNavigationNetworkMap.containsKey(stairsInterestPoints.get(m).getGlobalId())){
                                    interestPoints = new ArrayList<>();
                                    interestPoints.add(doorsInterestPoints.get(l));
                                    indoorNavigationNetworkMap.put(stairsInterestPoints.get(m).getGlobalId(),interestPoints);
                                }else{
                                    indoorNavigationNetworkMap.get(stairsInterestPoints.get(m).getGlobalId()).add(doorsInterestPoints.get(l));
                                }


                                if( ! indoorNavigationNetworkMap.containsKey(doorsInterestPoints.get(l).getGlobalId())){
                                    interestPoints = new ArrayList<>();
                                    interestPoints.add(stairsInterestPoints.get(m));
                                    indoorNavigationNetworkMap.put(doorsInterestPoints.get(l).getGlobalId(),interestPoints);
                                }else{
                                    indoorNavigationNetworkMap.get(doorsInterestPoints.get(l).getGlobalId()).add(stairsInterestPoints.get(m));
                                }

                                //indoorNavigationNetworkMap.put(doorsInterestPoints.get(l).getGlobalId(),)
                                //graphBuilder.connect(doorsInterestPoints.get(l)).to( stairsInterestPoints.get(m) ).withEdge(new IndoorDistance( null,null)  );
                            }

                            //System.out.println("Finished the second part :");
                        }
*/
                        ///generating space interest point
                        smartEvacSpaces.put(space.getGlobalId(),smartEvacSpace);




                        //alreadyCreatedSmartEvacStairMap.put()
                        //indoorNavigationNetwork
                        //getting space neibourhood relationship with other spaces
                        //from gotten doors we find the neighbour space by testing if the door is a part of it's boundaries
                        //list of neighbours
                        //The neighbourhood of a space will be deduct using the last code, so when we want to get
                        // this we get all the space's doors and from each of these door we get the associed spaces's list
                        //find also if the space contains a stair to access to other level
                        //if it does so it will be contained in the two spaces,  we have to get the other space, it's building storey (in wich level it is)
                        //A point if interest could be : a space (compound point of interest), a door,
                        // a centroid of TIN triangle of a space or a stair
                        /*
                        System.out.println("drawing generated graph");
                        GraphicsLibrary graphicsLibrary = new GraphicsLibrary();
                        graphicsLibrary.drawGraph("D:\\workplace\\"+space.getName()+"_"+space.getGlobalId()+"_graph_onespace.png",indoorNavigationNetworkMap,globalInterestPointsMap);
                        indoorNavigationNetworkMap = new HashMap<>();
                        globalInterestPointsMap = new HashMap<>();
                        */


                    }
                }
            }
        }

        //smartEvacSpaces

        System.out.println("drawing generated graph");
        GraphicsLibrary graphicsLibrary = new GraphicsLibrary();
        graphicsLibrary.drawDoorsSpacesGraph("F:\\workplace\\graph_allspaces.png",smartEvacSpaces);


        //generateExamplesSpaces(smartEvacSpaces);

        /*
        Iterator<Map.Entry<String, List<InterestPoint>>> interestPointIterator = indoorNavigationNetworkMap.entrySet().iterator();
        Map.Entry<String, List<InterestPoint>> entry;
        InterestPoint node;
        while(interestPointIterator.hasNext()){
            entry = interestPointIterator.next() ;
            System.out.println(""+entry.getKey());
            //entry.getValue()
            node = globalInterestPointsMap.get(entry.getKey());
            if(node !=null) {
                System.out.println("Entry size : "+entry.getValue().size());
                print_line.printf("\nENtry size  :" + entry.getValue().size());
                for (InterestPoint ip : entry.getValue()) {
                    System.out.println("Connecting :" + entry.getKey() + "to :" + ip.getGlobalId());
                    print_line.printf("\nConnecting :" + entry.getKey() + "to :" + ip.getGlobalId());
                    graphBuilder.connect(node).to(ip).withEdge(new IndoorDistance(null, null));
                    System.out.println("Connected");
                    print_line.printf("\nConnected :");
                }

            }else System.out.println("node is null  :  " + entry.getKey());
        }
        System.out.println("Generating the graph");
        indoorNavigationNetwork = graphBuilder.createDirectedGraph();

        print_line.printf("\n&&\nGenerated Graph  : ");

        Iterator<InterestPoint> nodes = indoorNavigationNetwork.vertices().iterator();
        InterestPoint point;
        SmartEvacElement evacElement;
        while(nodes.hasNext()){
                point = nodes.next();
                evacElement = point.getAssociatedElement();
                if(evacElement!=null)
                    print_line.printf("\n==> Sommet du graph  " +point.getType()+"  element "+evacElement.toString());
                else print_line.printf("\n==> Sommet du graph  " +point.getType());
        }

        print_line.printf("\n\nSpaces with all information   : " + goodSpaces+"/"+spacesCount+"\n");

        */

        print_line.close();

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


    public  void processLevels(IfcModelInterface model) {
        FileWriter writer = null;
        try {
            writer = new FileWriter("D:\\workplace\\IFC_levels_processing_log.txt",true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        PrintWriter print_line = new PrintWriter(writer);
        List<String> samplesGids = new ArrayList<>();
        /*
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
        */
       /* samplesGids.add("1hS0l0psT3ZP0d5DO1DqWd");
        samplesGids.add("1hS0l0psT3ZP0d5DO1Dqbe");
        samplesGids.add("1hS0l0psT3ZP0d5DO1Dqbf");
        samplesGids.add("1hS0l0psT3ZP0d5DO1DqWO");
        samplesGids.add("1hS0l0psT3ZP0d5DO1DqWR");
        samplesGids.add("1hS0l0psT3ZP0d5DO1DqYL");
        samplesGids.add("1hS0l0psT3ZP0d5DO1DqYA");
        samplesGids.add("1hS0l0psT3ZP0d5DO1DqWQ");
        samplesGids.add("1hS0l0psT3ZP0d5DO1DqYB");
        samplesGids.add("1hS0l0psT3ZP0d5DO1DqY8");
        samplesGids.add("1hS0l0psT3ZP0d5DO1DqWT");
        samplesGids.add("1hS0l0psT3ZP0d5DO1DqWS");
        samplesGids.add("1hS0l0psT3ZP0d5DO1DqWT");
        samplesGids.add("1hS0l0psT3ZP0d5DO1DqWS");
        samplesGids.add("1hS0l0psT3ZP0d5DO1DqWV");
        samplesGids.add("1hS0l0psT3ZP0d5DO1DqWb");
        samplesGids.add("1hS0l0psT3ZP0d5DO1DqX_");
        samplesGids.add("1hS0l0psT3ZP0d5DO1DqX$");
        samplesGids.add("1hS0l0psT3ZP0d5DO1DqXo");
        samplesGids.add("1hS0l0psT3ZP0d5DO1DqXz");
        samplesGids.add("1hS0l0psT3ZP0d5DO1DqXy");
        samplesGids.add("1hS0l0psT3ZP0d5DO1DqXv");
        samplesGids.add("1hS0l0psT3ZP0d5DO1DqXu");
        samplesGids.add("1hS0l0psT3ZP0d5DO1DqYF");
        samplesGids.add("1hS0l0psT3ZP0d5DO1DqWa");
        samplesGids.add("1hS0l0psT3ZP0d5DO1DqXx");
        samplesGids.add("1hS0l0psT3ZP0d5DO1DqWP");
        */

        /*
        samplesGids.add("1hS0l0psT3ZP0d5DO1Dqbe");
        samplesGids.add("1hS0l0psT3ZP0d5DO1DqWd");
*/


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
            EList<IfcRelAggregates> relAggregates = storey.getIsDecomposedBy();
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
                        System.out.println("Finished the first part :");
                        /**
                         *
                         * Connecting goten doors and stairs to space
                         *
                         *In
                         * */

                        //connecting goten doors to space
                        for(IfcDoor  ifcDoor : spaceDoors ){
                            if(alreadyCreatedSmartEvacDoors.containsKey(ifcDoor.getGlobalId()))
                                smartEvacDoor = alreadyCreatedSmartEvacDoors.get(ifcDoor.getGlobalId());
                            else {
                                smartEvacDoor = new SmartEvacDoor(ifcDoor.getGlobalId(), space.getName()+":"+ifcDoor.getGlobalId());
                                alreadyCreatedSmartEvacDoors.put(ifcDoor.getGlobalId(),smartEvacDoor);

                            }
                            smartEvacDoor.getAssociatedSpaces().add(smartEvacSpace);
                            smartEvacSpace.getDoors().add(smartEvacDoor);
                            spaceSmartEvacDoors.add(smartEvacDoor);
                        }
                        //connecting goten stairs to space
                        for(IfcStair ifcStair : ifcStairs){
                            if(alreadyCreatedSmartEvacStairMap.containsKey(ifcStair.getGlobalId())) {
                                smartEvacStair = alreadyCreatedSmartEvacStairMap.get(ifcStair.getGlobalId());
                                smartEvacStair.setUpperLevelSpace(smartEvacSpace);
                            }
                            else {
                                smartEvacStair = new SmartEvacStair(ifcStair.getGlobalId(), ifcStair.getName());
                                smartEvacStair.setLowerLevelSpace(smartEvacSpace);
                                alreadyCreatedSmartEvacStairMap.put(ifcStair.getGlobalId(),smartEvacStair);
                            }
                            smartEvacSpace.getStairs().add(smartEvacStair);
                            spaceSmartEvacStairs.add(smartEvacStair);
                        }

                        this.smartEvacSpacesMap.put(space.getGlobalId(),smartEvacSpace);


                        /**
                         *
                         *   connecting the current space network to global navigation network
                         *
                         * **/
                        //connecting space's centroids to doors and stairs

                        spaceTinInterestPoints = new ArrayList<>();
                        for(int  l =0;l<spaceCentroids.size();l++) {
                            interestPoint = new InterestPoint(space.getGlobalId()+"_cd_"+l,spaceCentroids.get(l), false);
                            interestPoint.setType("Centroid");
                            globalInterestPointsMap.put(interestPoint.getGlobalId(),interestPoint);
                            spaceTinInterestPoints.add(interestPoint);
                        }
                        /**
                         * Generating doorsInterestPoints
                         * */
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
                        stairsInterestPoints = new ArrayList<>();
                        for(int m=0;m<spaceSmartEvacStairs.size();m++){
                            interestPoint = new InterestPoint(null,true);
                            interestPoint.setAssociatedElement(spaceSmartEvacStairs.get(m));
                            interestPoint.setType("IfcStair");
                            //graphBuilder.connect(spaceTinInterestPoints.get(l)).to(interestPoint).withEdge(new IndoorDistance(spaceTinInterestPoints.get(m).getVertex(),null));
                            stairsInterestPoints.add(interestPoint);
                            interestPoint.setGlobalId(spaceSmartEvacStairs.get(m).getStairGlobalId());
                        }
                        /*
                        for(int l=0;l<spaceTinInterestPoints.size();l++) {
                            if(!doneInterestPoints.contains(spaceTinInterestPoints.get(l).getGlobalId())) {
                                //connecting centroids
                                spaceTinOtherInterestPoints = new ArrayList<>();
                                for (int m = 0; m < spaceTinInterestPoints.size(); m++) {
                                    concatTwoGID = doneInterestPoints.contains(spaceTinInterestPoints.get(l).getGlobalId().concat(spaceTinInterestPoints.get(m).getGlobalId()))
                                            || doneInterestPoints.contains(spaceTinInterestPoints.get(m).getGlobalId().concat(spaceTinInterestPoints.get(l).getGlobalId()));
                                    if (l != m && !concatTwoGID) {
                                        doneInterestPoints.add(spaceTinInterestPoints.get(l).getGlobalId().concat(spaceTinInterestPoints.get(m).getGlobalId()));
                                        spaceTinOtherInterestPoints.add(spaceTinInterestPoints.get(m));
                                        //graphBuilder.connect(spaceTinInterestPoints.get(l)).to(spaceTinInterestPoints.get(m)).withEdge(new IndoorDistance(spaceTinInterestPoints.get(l).getVertex(), spaceTinInterestPoints.get(m).getVertex()));
                                    }
                                }



                                if (!indoorNavigationNetworkMap.containsKey(spaceTinInterestPoints.get(l).getGlobalId())) {
                                    //System.out.println("Centroid  doesn't exist");
                                    indoorNavigationNetworkMap.put(spaceTinInterestPoints.get(l).getGlobalId(), spaceTinOtherInterestPoints);

                                } else {
                                    //System.out.println("Centroid already exist");
                                    indoorNavigationNetworkMap.get(spaceTinInterestPoints.get(l).getGlobalId()).addAll(spaceTinOtherInterestPoints);
                                }

                                doneInterestPoints.add(spaceTinInterestPoints.get(l).getGlobalId());

                            }



                            //connecting centroids to space doors
                            //centroid not added before
                            if(!indoorNavigationNetworkMap.containsKey(spaceTinInterestPoints.get(l).getGlobalId()))   {
                                //System.out.println("indoorNavigationNetworkMap dont contains");
                                indoorNavigationNetworkMap.put(spaceTinInterestPoints.get(l).getGlobalId(),  doorsInterestPoints );
                                indoorNavigationNetworkMap.put(spaceTinInterestPoints.get(l).getGlobalId(),  stairsInterestPoints );
                            }
                            else{
                                //Centroid already existe
                                //System.out.println("Centroid Interest Point already exist");

                                interestPoints = indoorNavigationNetworkMap.get(spaceTinInterestPoints.get(l).getGlobalId());
                                temp = doorsInterestPoints;
                                for(InterestPoint point : temp){
                                    if(! interestPoints.contains(point))
                                        indoorNavigationNetworkMap.get(spaceTinInterestPoints.get(l).getGlobalId()).add(point);


                                    if( ! indoorNavigationNetworkMap.containsKey(point.getGlobalId())){
                                        interestPoints = new ArrayList<>();
                                        interestPoints.addAll(spaceTinInterestPoints);
                                        indoorNavigationNetworkMap.put(point.getGlobalId(),interestPoints);
                                    }else{
                                        for(InterestPoint pointCentroid : spaceTinInterestPoints)
                                            if( !indoorNavigationNetworkMap.get(point.getGlobalId()).contains(pointCentroid) )
                                                indoorNavigationNetworkMap.get(point.getGlobalId()).add(pointCentroid);
                                            else {
                                                indexO = indoorNavigationNetworkMap.get(point.getGlobalId()).indexOf(pointCentroid);
                                                pp = indoorNavigationNetworkMap.get(point.getGlobalId()).get(indexO);
                                                //System.out.println("Door map already contains point diff DoorPGID  : " +pointCentroid.getGlobalId()+" centroid GID :"+pp.getGlobalId());
                                                //print_line.printf("\nDoor map already contains point diff DoorPGID  : " +pointCentroid.getGlobalId()+" centroid GID :"+pp.getGlobalId());
                                            }
                                    }
                                    //if(! globalInterestPointsMap.containsKey(interestPoint.getGlobalId()))
                                     //   globalInterestPointsMap.put(interestPoint.getGlobalId(),interestPoint);

                                }
                                temp = stairsInterestPoints;
                                for(InterestPoint point : temp){
                                    if(! interestPoints.contains(point))
                                        indoorNavigationNetworkMap.get(spaceTinInterestPoints.get(l).getGlobalId()).add(point);


                                    if( ! indoorNavigationNetworkMap.containsKey(point.getGlobalId())){
                                        interestPoints = new ArrayList<>();
                                        interestPoints.addAll(spaceTinInterestPoints);
                                        indoorNavigationNetworkMap.put(point.getGlobalId(),interestPoints);
                                    }else{
                                        for(InterestPoint pointCentroid : spaceTinInterestPoints)
                                            if( !indoorNavigationNetworkMap.get(point.getGlobalId()).contains(pointCentroid) )
                                                indoorNavigationNetworkMap.get(point.getGlobalId()).add(pointCentroid);
                                            else {
                                                indexO = indoorNavigationNetworkMap.get(point.getGlobalId()).indexOf(pointCentroid);
                                                pp = indoorNavigationNetworkMap.get(point.getGlobalId()).get(indexO);
                                                //System.out.println("Door map already contains point diff StairPGID  : " +pointCentroid.getGlobalId()+" centroid GID :"+pp.getGlobalId());
                                                //print_line.printf("\nDoor map already contains point diff StairPGID  : " +pointCentroid.getGlobalId()+" centroid GID :"+pp.getGlobalId());
                                            }
                                    }
                                    //if(! globalInterestPointsMap.containsKey(interestPoint.getGlobalId()))
                                    //   globalInterestPointsMap.put(interestPoint.getGlobalId(),interestPoint);
                                }

                            }



                            //connecting centroids to space stairs
                            if(!indoorNavigationNetworkMap.containsKey(spaceTinInterestPoints.get(l).getGlobalId()))   {
                                indoorNavigationNetworkMap.put(spaceTinInterestPoints.get(l).getGlobalId(),  stairsInterestPoints );
                            }
                            else{
                                interestPoints = indoorNavigationNetworkMap.get(spaceTinInterestPoints.get(l).getGlobalId());
                                for(InterestPoint point : stairsInterestPoints)
                                    if(! interestPoints.contains(point))
                                        indoorNavigationNetworkMap.get(spaceTinInterestPoints.get(l).getGlobalId()).add(point);

                            }
                        }
                        */
                        //connecting stairs to doors
                        /*
                        for(int l=0;l<doorsInterestPoints.size();l++){

                            if(! globalInterestPointsMap.containsKey(doorsInterestPoints.get(l).getGlobalId()))
                                globalInterestPointsMap.put(doorsInterestPoints.get(l).getGlobalId(),doorsInterestPoints.get(l));


                            for(int m=0;m<stairsInterestPoints.size();m++){
                                if(! globalInterestPointsMap.containsKey(stairsInterestPoints.get(m).getGlobalId()))
                                    globalInterestPointsMap.put(stairsInterestPoints.get(m).getGlobalId(),stairsInterestPoints.get(m));
                                if( ! indoorNavigationNetworkMap.containsKey(stairsInterestPoints.get(m).getGlobalId())){
                                    interestPoints = new ArrayList<>();
                                    interestPoints.add(doorsInterestPoints.get(l));
                                    indoorNavigationNetworkMap.put(stairsInterestPoints.get(m).getGlobalId(),interestPoints);
                                }else{
                                    indoorNavigationNetworkMap.get(stairsInterestPoints.get(m).getGlobalId()).add(doorsInterestPoints.get(l));
                                }


                                if( ! indoorNavigationNetworkMap.containsKey(doorsInterestPoints.get(l).getGlobalId())){
                                    interestPoints = new ArrayList<>();
                                    interestPoints.add(stairsInterestPoints.get(m));
                                    indoorNavigationNetworkMap.put(doorsInterestPoints.get(l).getGlobalId(),interestPoints);
                                }else{
                                    indoorNavigationNetworkMap.get(doorsInterestPoints.get(l).getGlobalId()).add(stairsInterestPoints.get(m));
                                }

                                //indoorNavigationNetworkMap.put(doorsInterestPoints.get(l).getGlobalId(),)
                                //graphBuilder.connect(doorsInterestPoints.get(l)).to( stairsInterestPoints.get(m) ).withEdge(new IndoorDistance( null,null)  );
                            }

                            //System.out.println("Finished the second part :");
                        }
                           */
                        ///generating space interest point
                        smartEvacSpaces.put(space.getGlobalId(),smartEvacSpace);




                        //alreadyCreatedSmartEvacStairMap.put()
                        //indoorNavigationNetwork
                        //getting space neibourhood relationship with other spaces
                        //from gotten doors we find the neighbour space by testing if the door is a part of it's boundaries
                        //list of neighbours
                        //The neighbourhood of a space will be deduct using the last code, so when we want to get
                        // this we get all the space's doors and from each of these door we get the associed spaces's list
                        //find also if the space contains a stair to access to other level
                        //if it does so it will be contained in the two spaces,  we have to get the other space, it's building storey (in wich level it is)
                        //A point if interest could be : a space (compound point of interest), a door,
                        // a centroid of TIN triangle of a space or a stair
                        /*
                        System.out.println("drawing generated graph");
                        GraphicsLibrary graphicsLibrary = new GraphicsLibrary();
                        graphicsLibrary.drawGraph("D:\\workplace\\"+space.getName()+"_"+space.getGlobalId()+"graph_onespace.png",indoorNavigationNetworkMap,globalInterestPointsMap);
                        indoorNavigationNetworkMap = new HashMap<>();
                        globalInterestPointsMap = new HashMap<>();
                        */

                    }
                }
            }
        }

        //smartEvacSpaces
        /*
        System.out.println("drawing generated graph");
        GraphicsLibrary graphicsLibrary = new GraphicsLibrary();
        graphicsLibrary.drawDoorsSpacesGraph("D:\\workplace\\graph_allspaces.png",smartEvacSpaces);
        */

        /*
        Iterator<Map.Entry<String, List<InterestPoint>>> interestPointIterator = indoorNavigationNetworkMap.entrySet().iterator();
        Map.Entry<String, List<InterestPoint>> entry;
        InterestPoint node;
        while(interestPointIterator.hasNext()){
            entry = interestPointIterator.next() ;
            System.out.println(""+entry.getKey());
            //entry.getValue()
            node = globalInterestPointsMap.get(entry.getKey());
            if(node !=null) {
                System.out.println("Entry size : "+entry.getValue().size());
                print_line.printf("\nENtry size  :" + entry.getValue().size());
                for (InterestPoint ip : entry.getValue()) {
                    System.out.println("Connecting :" + entry.getKey() + "to :" + ip.getGlobalId());
                    print_line.printf("\nConnecting :" + entry.getKey() + "to :" + ip.getGlobalId());
                    graphBuilder.connect(node).to(ip).withEdge(new IndoorDistance(null, null));
                    System.out.println("Connected");
                    print_line.printf("\nConnected :");
                }

            }else System.out.println("node is null  :  " + entry.getKey());
        }
        System.out.println("Generating the graph");
        indoorNavigationNetwork = graphBuilder.createDirectedGraph();

        print_line.printf("\n\nGenerated Graph  : ");

        Iterator<InterestPoint> nodes = indoorNavigationNetwork.vertices().iterator();
        InterestPoint point;
        SmartEvacElement evacElement;
        while(nodes.hasNext()){
                point = nodes.next();
                evacElement = point.getAssociatedElement();
                if(evacElement!=null)
                    print_line.printf("\n==> Sommet du graph  " +point.getType()+"  element "+evacElement.toString());
                else print_line.printf("\n==> Sommet du graph  " +point.getType());
        }

        print_line.printf("\n\nSpaces with all information   : " + goodSpaces+"/"+spacesCount+"\n");

        */

        print_line.close();

    }


    public List<SmartEvacSpace> getSpaceNeighbour(SmartEvacSpace space){
        List<SmartEvacSpace> smartEvacSpaces = new ArrayList<>();
        for(SmartEvacDoor door : space.getDoors())
            for(SmartEvacSpace evacSpace : door.getAssociatedSpaces())
                if(evacSpace!=space)
                    smartEvacSpaces.add(evacSpace);
        return smartEvacSpaces;
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
