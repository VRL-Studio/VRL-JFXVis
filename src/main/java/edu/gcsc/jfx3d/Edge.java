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
public class Edge {
    
    
    private Vertex3D[] vertices = new Vertex3D[2];
    private int index;
    
    public Edge(Vertex3D v1, Vertex3D v2,int index){
        this.vertices[0] = v1;
        this.vertices[1] = v2;
        this.index = index;
    }
    
    public Edge(float p11, float p12, float p21, float p22,int index){
        this.vertices[0].setCoords(p11,p12);
        this.vertices[1].setCoords(p21, p22);
        this.index = index;
    }

    public Vertex3D[] getVertices() {
        return vertices;
    }

    public void setVertices(Vertex3D[] vertices) {
        this.vertices = vertices;
    }

    @Override
    public String toString() {
        return ("Edge : " + this.index + "\n"+
                "Vertex 1: " + vertices[0].getX() + " " +vertices[0].getY() + " " + vertices[0].getZ() + "\n" +
                "Vertex 2: " + vertices[1].getX() + " " +vertices[1].getY() + " " + vertices[1].getZ()  );
    }
    
    
}
