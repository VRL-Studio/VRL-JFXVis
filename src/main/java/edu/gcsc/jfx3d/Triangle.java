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
public class Triangle extends Geometry2D {
    
    
    private int[] nodes = new int[3];
    private int index = -1;
    
    public Triangle(int v0, int v1, int v2, int index){
        this.nodes[0] = v0;
        this.nodes[1] = v1;
        this.nodes[2] = v2;
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
        int[] arr = {this.nodes[0],0,this.nodes[1],0,this.nodes[2],0};
        return arr;
    }

    @Override
    public String getCoordinatesOfPoints(ArrayList listOfAllVertices) {
        return ("2D Object : " + this.index + "\n"+
                "Vertex 1: " + listOfAllVertices.get(nodes[0]*3) + " " +listOfAllVertices.get(nodes[0]*3+1) + " " + listOfAllVertices.get(nodes[0]*3+2) + "\n" +
                "Vertex 2: " + listOfAllVertices.get(nodes[1]*3) + " " +listOfAllVertices.get(nodes[1]*3+1) + " " + listOfAllVertices.get(nodes[1]*3+2) + "\n" +
                "Vertex 3: " + listOfAllVertices.get(nodes[2]*3) + " " +listOfAllVertices.get(nodes[2]*3+1) + " " + listOfAllVertices.get(nodes[2]*3+2) + "\n" );
    }
    
}
