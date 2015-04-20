/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.gcsc.jfx3d;

import java.util.ArrayList;

/**
 *
 * @author Eugen
 */
public abstract class Geometry2D {
    
    private int[] nodes;
    private int index;
    
    public abstract int[] getNodes();
    public abstract void setNodes(int[] array);
    public abstract int[] getFacesArray();
    public abstract int getIndex();
    public abstract String getCoordinatesOfPoints(ArrayList listOfAllVertices);
    
    
    
    
}
