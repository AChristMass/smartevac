package com.buildinnov.smartevac.plugin.evacplans_generation.services;

import org.tinfour.common.IIncrementalTin;
import org.tinfour.common.IQuadEdge;
import org.tinfour.common.Vertex;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
        import java.awt.BasicStroke;
        import java.awt.Color;
        import java.awt.GradientPaint;
        import java.awt.Graphics2D;
        import java.awt.RenderingHints;
        import java.awt.Shape;
        import java.awt.geom.AffineTransform;
        import java.awt.geom.Ellipse2D;
        import java.awt.geom.Line2D;
        import java.awt.geom.NoninvertibleTransformException;
        import java.awt.geom.Point2D;
        import java.awt.geom.Rectangle2D;
        import java.awt.geom.Point2D.Double;
        import java.awt.image.BufferedImage;
        import java.io.File;
        import java.io.IOException;
        import java.util.Iterator;
        import java.util.List;
        import javax.imageio.ImageIO;
        import org.tinfour.common.IIncrementalTin;
        import org.tinfour.common.IQuadEdge;
        import org.tinfour.common.Vertex;
import org.tinfour.demo.utils.TestPalette;


public class TinDrawingUtility {
    public TinDrawingUtility() {
    }

    public AffineTransform initTransform(int width, int height, double x0, double x1, double y0, double y1) {
        double rImage = (double)width / (double)height;
        double rData = (x1 - x0) / (y1 - y0);
        double rAspect = rImage / rData;
        double uPerPixel;
        if (rAspect >= 1.0D) {
            uPerPixel = (y1 - y0) / (double)height;
        } else {
            uPerPixel = (x1 - x0) / (double)width;
        }

        double scale = 1.0D / uPerPixel;
        double xCenter = (x0 + x1) / 2.0D;
        double yCenter = (y0 + y1) / 2.0D;
        double xOffset = (double)(width / 2) - scale * xCenter;
        double yOffset = (double)(height / 2) + scale * yCenter;
        AffineTransform af = new AffineTransform(scale, 0.0D, 0.0D, -scale, xOffset, yOffset);

        try {
            af.createInverse();
            return af;
        } catch (NoninvertibleTransformException var31) {
            throw new IllegalArgumentException("Input elements result in a degenerate transform: " + var31.getMessage(), var31);
        }
    }

    private boolean inBounds(Vertex p, double x0, double x1, double y0, double y1) {
        double x = p.getX();
        double y = p.getY();
        return x0 <= x && x <= x1 && y0 <= y && y <= y1;
    }

    BufferedImage render(AffineTransform af, int width, int height, double x0, double x1, double y0, double y1, double zMin, double zMax, Color background, Color foreground, TestPalette palette, IIncrementalTin tin) {
        BufferedImage bImage = new BufferedImage(width, height, 2);
        Graphics2D g2d = bImage.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2d.setColor(background);
        g2d.fillRect(0, 0, width, height);
        g2d.setColor(foreground);
        Point2D llCorner = new Point2D.Double(x0, y0);
        Point2D urCorner = new Point2D.Double(x1, y1);
        af.transform(llCorner, llCorner);
        af.transform(urCorner, urCorner);
        Rectangle2D clipBounds = new java.awt.geom.Rectangle2D.Double(llCorner.getX(), urCorner.getY(), urCorner.getX() - llCorner.getX(), llCorner.getY() - urCorner.getY());
        g2d.setStroke(new BasicStroke(3.0F));
        g2d.draw(clipBounds);
        g2d.setClip(clipBounds);
        Point2D p0 = new Point2D.Double();
        Point2D p1 = new Point2D.Double();
        Line2D l2d = new java.awt.geom.Line2D.Double();
        Ellipse2D e2d = new java.awt.geom.Ellipse2D.Double();
        g2d.setStroke(new BasicStroke(2.0F));
        java.util.List<IQuadEdge> edges = tin.getEdges();
        Rectangle2D bounds = tin.getBounds();
        Iterator var30 = edges.iterator();

        double mx = tin.getVertices().get(0).getX();
        double my = tin.getVertices().get(0).getY();

        while(true) {
            Vertex v0;
            Vertex v1;
            do {
                do {
                    do {
                        if (!var30.hasNext()) {
                            mx = (p0.getX() + p1.getX())/2;
                            my = (p0.getY() + p1.getY())/2;
                            List<Vertex> vertexList = tin.getVertices();
                            g2d.setStroke(new BasicStroke(1.0F));
                            Iterator var42 = vertexList.iterator();

                            while(var42.hasNext()) {
                                v0 = (Vertex)var42.next();
                                if (this.inBounds(v0, x0, x1, y0, y1)) {
                                    p0.setLocation(v0.getX(), v0.getY());
                                    double z = v0.getZ();
                                    if (palette != null) {
                                        Color c = palette.getColor(z, zMin, zMax);
                                        g2d.setColor(c);
                                    }

                                    if(bounds.contains(mx,my)) {
                                        af.transform(p0, p1);
                                        e2d.setFrame(p1.getX() - 2.0D, p1.getY() - 2.0D, 6.0D, 6.0D);
                                        g2d.fill(e2d);
                                        g2d.draw(e2d);
                                    }else{
                                        System.out.println("Medium outside bounds");
                                    }
                                }
                            }

                            g2d.setColor(foreground);
                            g2d.setStroke(new BasicStroke(3.0F));
                            g2d.setClip((Shape)null);
                            g2d.draw(clipBounds);
                            g2d.dispose();
                            return bImage;
                        }

                        IQuadEdge edge = (IQuadEdge)var30.next();
                        v0 = edge.getA();
                        v1 = edge.getB();
                    } while(v0 == null);
                } while(v1 == null);
            } while(!this.inBounds(v0, x0, x1, y0, y1) && !this.inBounds(v1, x0, x1, y0, y1)  && bounds.contains(mx,my));

            p0.setLocation(v0.getX(), v0.getY());
            p1.setLocation(v1.getX(), v1.getY());
            af.transform(p0, p0);
            af.transform(p1, p1);
            mx = (p0.getX() + p1.getX())/2;
            my = (p0.getY() + p1.getY())/2;
            if(bounds.contains(mx,my))
                l2d.setLine(p0, p1);
            double z0 = v0.getZ();
            double z1 = v1.getZ();
            if (palette != null) {
                Color c0 = palette.getColor(z0, zMin, zMax);
                Color c1 = palette.getColor(z1, zMin, zMax);
                GradientPaint paint = new GradientPaint((float)v0.getX(), (float)v0.getY(), c0, (float)v1.getX(), (float)v1.getY(), c1);
                g2d.setPaint(paint);
            }

            mx = (p0.getX() + p1.getX())/2;
            my = (p0.getY() + p1.getY())/2;
            if(bounds.contains(mx,my))
                g2d.draw(l2d);
            else System.out.println("Medium outside bounds");
        }
    }

    public static void drawTin(IIncrementalTin tin, int width, int height, File file) throws IOException {
        Rectangle2D r2d = tin.getBounds();
        double x0 = r2d.getMinX();
        double x1 = r2d.getMaxX();
        double y0 = r2d.getMinY();
        double y1 = r2d.getMaxY();
        if (r2d == null) {
            throw new IllegalArgumentException("Input TIN is not bootstrapped");
        } else {
            TinDrawingUtility tru = new TinDrawingUtility();
            AffineTransform af = tru.initTransform(width, height, x0, x1, y0, y1);
            BufferedImage bImage = tru.render(af, width, height, x0, x1, y0, y1, y1, y1, Color.white, Color.darkGray, (TestPalette)null, tin);
            String s = file.getName();
            int i = s.lastIndexOf(".");
            String fmt = null;
            if (i > 0) {
                fmt = s.substring(i + 1, s.length());
                if ("png".equalsIgnoreCase(fmt)) {
                    fmt = "PNG";
                } else if ("jpg".equalsIgnoreCase(fmt)) {
                    fmt = "JPEG";
                } else if ("jepg".equalsIgnoreCase(fmt)) {
                    fmt = "JPEG";
                } else if ("gif".equalsIgnoreCase(fmt)) {
                    fmt = "GIF";
                } else {
                    fmt = null;
                }
            }

            if (fmt == null) {
                fmt = "PNG";
            }

            ImageIO.write(bImage, fmt, file);
        }
    }
}
