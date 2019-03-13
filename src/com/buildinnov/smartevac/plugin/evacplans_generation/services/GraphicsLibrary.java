package com.buildinnov.smartevac.plugin.evacplans_generation.services;



import com.buildinnov.smartevac.plugin.evacplans_generation.services.models.InterestPoint;
import com.mxgraph.layout.hierarchical.mxHierarchicalLayout;
import com.mxgraph.layout.mxIGraphLayout;
import com.mxgraph.util.mxCellRenderer;
import com.mxgraph.util.mxConstants;
import com.mxgraph.view.mxGraph;
import es.usc.citius.hipster.graph.HipsterDirectedGraph;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;

public class GraphicsLibrary {



    private Map<String,Object> graphVerticesMap = new HashMap<>();


    public static void drawPolygon(List<Integer> pointArrayX, List<Integer> pointArrayY, String filepath) throws IOException {


        BufferedImage image = new BufferedImage(9000,9000,BufferedImage.TYPE_3BYTE_BGR);
        Graphics2D g2d = image.createGraphics();


        g2d.fillRect(0, 0, image.getWidth(), image.getHeight());
        g2d.setColor(Color.RED);
        BasicStroke bs = new BasicStroke(20);
        g2d.setStroke(bs);

        int[] xPoly = new int[pointArrayX.size()];
        int[] yPoly = new int[pointArrayY.size()];
        for(int i = 0; i < pointArrayX.size(); i++) {
            xPoly[i] = pointArrayX.get(i);
            yPoly[i] = pointArrayY.get(i);
        }
        Polygon poly = new Polygon(xPoly, yPoly, xPoly.length);
        poly.getBounds();
        g2d.setPaint(Color.DARK_GRAY);
        g2d.drawPolygon(poly);
        g2d.fillPolygon(xPoly, yPoly, xPoly.length);
        //g2d.drawPolygon(xPoly, yPoly, xPoly.length);
        g2d.setStroke(bs);
        //g2d.drawPolyline(xPoly, yPoly, xPoly.length);
        //g2d.drawOval(100, 100, 200, 200);

        g2d.draw(poly);


        File outputfile = new File(filepath + "");
        ImageIO.write(image, "png", outputfile);

    }

    public static void drawPolyLine(List<Integer> pointArrayX, List<Integer> pointArrayY, String filepath) throws IOException {


        /////Getting the min
        Integer minX = Collections.min(pointArrayX);
        Integer minY = Collections.min(pointArrayY);
        if(minX < 0)
            for(int i=0;i<pointArrayX.size();i++)
                pointArrayX.set(i,pointArrayX.get(i) - minX + 10);
        if(minY < 0)
            for(int i=0;i<pointArrayX.size();i++)
                pointArrayY.set(i,pointArrayY.get(i) - minY + 10);

        BufferedImage image = new BufferedImage(9000,9000,BufferedImage.TYPE_3BYTE_BGR);
        Graphics2D g2d = image.createGraphics();


        g2d.fillRect(0, 0, image.getWidth(), image.getHeight());
        g2d.setColor(Color.RED);
        BasicStroke bs = new BasicStroke(20);
        g2d.setStroke(bs);

        int[] xPoly = new int[pointArrayX.size()];
        int[] yPoly = new int[pointArrayY.size()];
        for(int i = 0; i < pointArrayX.size(); i++) {
            xPoly[i] = pointArrayX.get(i);
            yPoly[i] = pointArrayY.get(i);
        }
        Polygon poly = new Polygon(xPoly, yPoly, xPoly.length);
        poly.getBounds();
        g2d.setPaint(Color.DARK_GRAY);
        //g2d.drawPolygon(poly);
        //g2d.fillPolygon(xPoly, yPoly, xPoly.length);
        //g2d.drawPolygon(xPoly, yPoly, xPoly.length);
        g2d.setStroke(bs);
        g2d.drawPolyline(xPoly, yPoly, xPoly.length);
        //g2d.drawOval(100, 100, 200, 200);

        g2d.draw(poly);


        File outputfile = new File(filepath + "");
        ImageIO.write(image, "png", outputfile);

    }


    public  void drawGraph(String filepath, Map<String,List<InterestPoint>> indoorNavigationNetworkMap ,Map<String, InterestPoint> globalInterestPointsMap){
        // Creates graph with model
        mxGraph graph = new mxGraph();
        Object parent = graph.getDefaultParent();

        // Sets the default vertex style
        Map<String, Object> style = graph.getStylesheet().getDefaultVertexStyle();
        style.put(mxConstants.STYLE_GRADIENTCOLOR, "#FFFFFF");
        style.put(mxConstants.STYLE_ROUNDED, true);
        style.put(mxConstants.STYLE_SHADOW, true);


        graph.getModel().beginUpdate();
        try
        {

            this.insertVertices(graph,globalInterestPointsMap);
            this.insertEdges(graph,indoorNavigationNetworkMap);
            mxIGraphLayout layout = new mxHierarchicalLayout(graph);
            layout.execute(parent);
        }
        finally
        {
            graph.getModel().endUpdate();
        }

        // Creates an image than can be saved using ImageIO
        BufferedImage image = mxCellRenderer.createBufferedImage(graph, null,
                1, Color.WHITE, true, null);


        File outputfile = new File(filepath + "");
        try {
            ImageIO.write(image, "png", outputfile);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void insertEdges(mxGraph graph, Map<String, List<InterestPoint>> indoorNavigationNetworkMap) {
        Iterator<Map.Entry<String, List<InterestPoint>>> interestPointIterator = indoorNavigationNetworkMap.entrySet().iterator();
        Map.Entry<String, List<InterestPoint>> entry;
        InterestPoint node;
        List<InterestPoint> interestPoints;

        while(interestPointIterator.hasNext()){
            entry = interestPointIterator.next();
            interestPoints = entry.getValue();
            for(InterestPoint point : interestPoints ){
                graph.insertEdge(graph.getDefaultParent(),null,"",graphVerticesMap.get(entry.getKey()),graphVerticesMap.get(point.getGlobalId()));
            }

        }
    }

    private void insertVertices(mxGraph graph,Map<String, InterestPoint> globalInterestPointsMap) {

        Iterator<Map.Entry<String, InterestPoint>> interestPointIterator = globalInterestPointsMap.entrySet().iterator();
        Map.Entry<String, InterestPoint> entry;
        InterestPoint node;
        Object o;
        /*Map<String, Object> styleDoor = graph.getStylesheet().getDefaultVertexStyle();
        styleDoor.put(mxConstants.STYLE_FILLCOLOR, "#FF0800");
        styleDoor.put(mxConstants.STYLE_ROUNDED, true);
        styleDoor.put(mxConstants.STYLE_SHADOW, true);
        */


        while(interestPointIterator.hasNext()){
            entry = interestPointIterator.next();
            System.out.println("Entry Type :" +  entry.getValue().getType());
            if(entry.getValue().getType().equals("IfcDoor")){
                //styleDoor.put(mxConstants.STYLE_FILLCOLOR, "#FF0800");
                o = graph.insertVertex(graph.getDefaultParent(),entry.getKey(),null,0.0,0.0,10,10,"fillColor=#FF0800") ;
            }

            else{
                //styleDoor.put(mxConstants.STYLE_FILLCOLOR, "#27AEE3");
                o = graph.insertVertex(graph.getDefaultParent(),entry.getKey(),null,0.0,0.0,10,10,"fillColor=#27AEE3")  ;//(graph.getDefaultParent(),entry.getKey(),entry.getValue().getType(),0,0,40,20);
            }
            graphVerticesMap.put(entry.getKey(),o);
        }
    }


}
