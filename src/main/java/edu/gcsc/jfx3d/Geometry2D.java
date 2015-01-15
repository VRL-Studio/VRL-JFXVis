/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.gcsc.jfx3d;

/**
 *
 * @author Eugen
 */
public abstract class Geometry2D {
    
    private int[] nodes;
    
    public abstract int[] getNodes();
    public abstract void setNodes(int[] array);
    public abstract int[] getFacesArray();
    
    
    
    
}
