package com.qolting.Blackout;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

public class BlackoutShapes implements Shape {

    public int gameWidth;
    public int gameHeight;

    public ArrayList<BlackoutQuad> allShapes = new ArrayList<>();

    public BlackoutShapes(int gameWidth, int gameHeight) {
        this.gameHeight = gameHeight;
        this.gameWidth = gameWidth;
    }

    @Override
    public Rectangle getBounds() {
        return new Rectangle(0,0,gameWidth,gameHeight);
    }

    @Override
    public Rectangle2D getBounds2D() {
        return new Rectangle(0,0,gameWidth,gameHeight);
    }

    @Override
    public boolean contains(double x, double y) {
        /*for(BlackoutQuad quad : allShapes) {
            if(quad.polygonPoint(x,y)) {
                return true;
            }
        }*/
        return false;
    }

    @Override
    public boolean contains(Point2D p) {
        /*for(BlackoutQuad quad : allShapes) {
            if(quad.polygonPoint(p.getX(),p.getY())) {
                return true;
            }
        }*/
        return false;
    }

    @Override
    public boolean intersects(double x, double y, double w, double h) {
        /*for(BlackoutQuad quad : allShapes) {
            if(quad.polyRect(x,y,w,h)) {
                return true;
            }
        }*/
        return false;
    }

    @Override
    public boolean intersects(Rectangle2D r) {
        /*for(BlackoutQuad quad : allShapes) {
            if(quad.polyRect(r.getX(),r.getY(),r.getWidth(),r.getHeight())) {
                return true;
            }
        }*/
        return false;
    }

    @Override
    public boolean contains(double x, double y, double w, double h) {
        return this.intersects(x,y,w,h);
    }

    @Override
    public boolean contains(Rectangle2D r) {
        return this.intersects(r);
    }

    @Override
    public PathIterator getPathIterator(AffineTransform at) {
        return null;
    }

    @Override
    public PathIterator getPathIterator(AffineTransform at, double flatness) {
        return null;
    }
}
