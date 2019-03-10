package com.buildinnov.smartevac.plugin.evacplans_generation.services;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class GraphicsLibrary {




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


}
