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
public class Tetrahedron extends Geometry3D{
    
    private int[] nodes = new int[4];
    private int index = -1;
    
    public Tetrahedron(int v0, int v1, int v2, int v3, int index){
        this.nodes[0] = v0;
        this.nodes[1] = v1;
        this.nodes[2] = v2;
        this.nodes[3] = v3;
        this.index = index;
    }

    @Override
    public int[] getNodes() {
        return nodes;
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
        int [] arr = {this.nodes[0],0,this.nodes[2],0,this.nodes[1],0,//-1
                      this.nodes[1],0,this.nodes[2],0,this.nodes[3],0,//-1
                      this.nodes[0],0,this.nodes[3],0,this.nodes[2],0,//-1
                      this.nodes[0],0,this.nodes[1],0,this.nodes[3],0};//-1
        return arr;
    }
    
}
