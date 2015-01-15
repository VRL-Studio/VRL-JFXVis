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
public class Vertex3D {


    private float[] coords = new float[3];
    private int index = -1;
    
    public Vertex3D(float[] coordinates){
        this.coords[0] = coordinates[0];
        this.coords[1] = coordinates[1];
        this.coords[2] = coordinates[2];;
    }
    
    public Vertex3D(float c1, float c2, float c3,int index){
        this.coords[0] = c1;
        this.coords[1] = c2;
        this.coords[2] = c3;
        this.index = index;
    }
    
    public Vertex3D(float c1, float c2, float c3){
        this.coords[0] = c1;
        this.coords[1] = c2;
        this.coords[2] = c3;
    }
    
    public Vertex3D(){
        this.coords[0] = 0;
        this.coords[1] = 0;
        this.coords[2] = 0;
    }
    
    
    
    public float[] getCoords() {
        return coords;
    }

    public void setCoords(float[] coords) {
        this.coords = coords;
    }
    
    public void setCoords(float p1, float p2){
        this.coords[0] = p1;
        this.coords[1] = p2;
    }
    
    public float getX(){
        return this.coords[0];
    }
    
    public float getY(){
        return this.coords[1];
    }
    
    public float getZ(){
        return this.coords[2];
    }
    
    public float getIndex(){
        return this.index;
    }
    
    public String printCoords(){
        
        return ("X: " + getX() + "\tY: " +getY() + "\tZ: "+ getZ());
        
    }
    
    
}
