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
public class Hexahedron extends Geometry3D{
    
    
    private int[] nodes = new int[8];
    private int index = -1;
    
    public Hexahedron(int v0, int v1, int v2, int v3,
                      int v4, int v5, int v6, int v7, int index){
        
        this.nodes[0] = v0;
        this.nodes[1] = v1;
        this.nodes[2] = v2;
        this.nodes[3] = v3;
        this.nodes[4] = v4;
        this.nodes[5] = v5;
        this.nodes[6] = v6;
        this.nodes[7] = v7;
        this.index = index;
    }
    
    @Override
    public int[] getNodes(){
        return this.nodes;
    }
    
    @Override
    public void setNodes(int[] nodes) {
        this.nodes = nodes;
    }
    
    public int getIndex(){
        return this.index;
    }

    @Override
    public int[] getFacesArray() {
        int arr[] = {this.nodes[0],0,this.nodes[2],0,this.nodes[1],0,//-1
                     this.nodes[2],0,this.nodes[0],0,this.nodes[3],0,    //-1
                     this.nodes[0],0,this.nodes[5],0,this.nodes[4],0,//-1
                     this.nodes[5],0,this.nodes[0],0,this.nodes[1],0,    //-1
                     this.nodes[1],0,this.nodes[6],0,this.nodes[5],0,//-1
                     this.nodes[6],0,this.nodes[1],0,this.nodes[2],0,    //-1
                     this.nodes[2],0,this.nodes[7],0,this.nodes[6],0,//-1
                     this.nodes[7],0,this.nodes[2],0,this.nodes[3],0,    //-1
                     this.nodes[3],0,this.nodes[4],0,this.nodes[7],0,//-1
                     this.nodes[4],0,this.nodes[3],0,this.nodes[0],0,    //-1
                     this.nodes[4],0,this.nodes[6],0,this.nodes[7],0,//-1
                     this.nodes[6],0,this.nodes[4],0,this.nodes[5],0};   //-1
        
        return arr;
    }
    
}
