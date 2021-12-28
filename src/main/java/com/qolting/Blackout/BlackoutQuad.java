package com.qolting.Blackout;

import java.awt.*;

public class BlackoutQuad {

    BlackoutVector p1;
    BlackoutVector p2;
    BlackoutVector p3;
    BlackoutVector p4;

    public BlackoutQuad(BlackoutVector p1, BlackoutVector p2, BlackoutVector p3, BlackoutVector p4) {
        this.p1 = p1;
        this.p2 = p2;
        this.p3 = p3;
        this.p4 = p4;
    }

    public BlackoutQuad(int x, int y, int w, int h) {
        this.p1 = new BlackoutVector(x,y);
        this.p2 = new BlackoutVector(x+w,y);
        this.p3 = new BlackoutVector(x+w,y+h);
        this.p4 = new BlackoutVector(x,y+h);
    }

    public BlackoutVector[] getVectors() {
        return new BlackoutVector[]{p1,p2,p3,p4};
    }

    public static Rectangle expandRectangle(Rectangle rectangle, int padding) {
        return new Rectangle(rectangle.x-padding,rectangle.y-padding,rectangle.width+padding*2,rectangle.height+padding*2);
    }

    public Rectangle getBounds() {
        int minX = 10000;
        int minY = 10000;
        int maxX = -1000;
        int maxY = -1000;
        for(BlackoutVector v: getVectors()) {
            if(v.x < minX)
                minX = v.x;
            if(v.x > maxX)
                maxX = v.x;
            if(v.y > maxY)
                maxY = v.y;
            if(v.y < minY)
                minY = v.y;
        }
        return new Rectangle(minX,minY,maxX-minX,maxY-minY);
    }


    //currently useless stuff but may come in useful
    /*public boolean intersects(int rx, int ry, int rw, int rh) {
        return polyRect(rx,ry,rw,rh);
    }

    //https://www.jeffreythompson.org/collision-detection/poly-rect.php
    // POLYGON/RECTANGLE
    boolean polyRect(double rx, double ry, double rw, double rh) {
        BlackoutVector arr[] = {p1,p2,p3,p4};
        // go through each of the vertices, plus the next
        // vertex in the list
        int next = 0;
        for (int current=0; current<arr.length; current++) {

            // get next vertex in list
            // if we've hit the end, wrap around to 0
            next = current+1;
            if (next == arr.length) next = 0;

            // get the PVectors at our current position
            // this makes our if statement a little cleaner
            BlackoutVector vc = arr[current];    // c for "current"
            BlackoutVector vn = arr[next];       // n for "next"

            // check against all four sides of the rectangle
            boolean collision = lineRect(vc.x,vc.y,vn.x,vn.y, rx,ry,rw,rh);
            if (collision) return true;

            // optional: test if the rectangle is INSIDE the polygon
            // note that this iterates all sides of the polygon
            // again, so only use this if you need to
            boolean inside = polygonPoint(rx,ry);
            if (inside) return true;
        }

        return false;
    }

    //https://www.jeffreythompson.org/collision-detection/poly-rect.php
    // LINE/RECTANGLE
    boolean lineRect(double x1, double y1, double x2, double y2, double rx, double ry, double rw, double rh) {

        // check if the line has hit any of the rectangle's sides
        // uses the Line/Line function below
        boolean left =   lineLine(x1,y1,x2,y2, rx,ry,rx, ry+rh);
        boolean right =  lineLine(x1,y1,x2,y2, rx+rw,ry, rx+rw,ry+rh);
        boolean top =    lineLine(x1,y1,x2,y2, rx,ry, rx+rw,ry);
        boolean bottom = lineLine(x1,y1,x2,y2, rx,ry+rh, rx+rw,ry+rh);

        // if ANY of the above are true,
        // the line has hit the rectangle
        if (left || right || top || bottom) {
            return true;
        }
        return false;
    }

    //https://www.jeffreythompson.org/collision-detection/poly-rect.php
    // LINE/LINE
    boolean lineLine(double x1, double y1, double x2, double y2, double x3, double y3, double x4, double y4) {

        // calculate the direction of the lines
        double uA = ((x4-x3)*(y1-y3) - (y4-y3)*(x1-x3)) / ((y4-y3)*(x2-x1) - (x4-x3)*(y2-y1));
        double uB = ((x2-x1)*(y1-y3) - (y2-y1)*(x1-x3)) / ((y4-y3)*(x2-x1) - (x4-x3)*(y2-y1));

        // if uA and uB are between 0-1, lines are colliding
        if (uA >= 0 && uA <= 1 && uB >= 0 && uB <= 1) {
            return true;
        }
        return false;
    }

    //https://www.jeffreythompson.org/collision-detection/poly-rect.php
    // POLYGON/POINT
    // only needed if you're going to check if the rectangle
    // is INSIDE the polygon
    boolean polygonPoint(double px, double py) {

        BlackoutVector vertices[] = {p1,p2,p3,p4};
        boolean collision = false;

        // go through each of the vertices, plus the next
        // vertex in the list
        int next = 0;
        for (int current=0; current<vertices.length; current++) {

            // get next vertex in list
            // if we've hit the end, wrap around to 0
            next = current+1;
            if (next == vertices.length) next = 0;

            // get the PVectors at our current position
            // this makes our if statement a little cleaner
            BlackoutVector vc = vertices[current];    // c for "current"
            BlackoutVector vn = vertices[next];       // n for "next"

            // compare position, flip 'collision' variable
            // back and forth
            if (((vc.y > py && vn.y < py) || (vc.y < py && vn.y > py)) &&
                    (px < (vn.x-vc.x)*(py-vc.y) / (vn.y-vc.y)+vc.x)) {
                collision = !collision;
            }
        }
        return collision;
    }*/
}
