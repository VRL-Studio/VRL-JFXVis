/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.gcsc.jfx3d;

/**
 *Abstract class for all 3D geometries.
 * Derive from this class if you want to add new 3D geometry parts.
 * @author Eugen
 */
public abstract class Geometry3D {
    
    private int[] nodes;
    
    public abstract int[] getNodes();
    public abstract void setNodes(int[] array);
    public abstract int[] getFacesArray();
    
}
