/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.gcsc.jfx3d;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import java.util.ArrayList;

/**
 *
 * @author Eugen
 */
public class UGXsubset {
      
    private ArrayList<Integer> vertices = new ArrayList<Integer>();
    
    private ArrayList<Integer> edges = new ArrayList<Integer>();

    private ArrayList<Integer> faces = new ArrayList<Integer>();

    private ArrayList<Integer> volumes = new ArrayList<Integer>();
    
    private boolean hasVertices = false;
    private boolean hasEdges = false;
    private boolean hasFaces = false;
    private boolean hasVolumes = false;
    
    
    @XStreamAlias("name")
    private String name;
    
    @XStreamAlias("color")
    private String colorString = "";
    
    private double[] colors = new double[4];
    //private int state;
    
    @XStreamAlias("vertices")
    private String vert = "";
    @XStreamAlias("edges")
    private String edge = "";
    @XStreamAlias("faces")
    private String face = "";
    @XStreamAlias("volumes")
    private String vol = "";

    

    
  //  @XStreamImplicit(itemFieldName="subset")
  //  private ArrayList<UGXsubsetdata> data = new ArrayList<UGXsubsetdata>();

    
    public UGXsubset(String name,double[] colors,int state,ArrayList<Integer> vertices,
            ArrayList<Integer> edges,ArrayList<Integer> faces,ArrayList<Integer> volumes){
        
        System.out.println("NOT DEFAULT");
        this.name = name;
        this.colors = colors;
        //this.state = state;
        this.vertices = vertices;
        this.edges = edges;
        this.faces = faces;
        this.volumes = volumes;
        
    }
    
    public UGXsubset(){
        this.name = "NoName";
        this.colors = new double[]{1.0,1.0,1.0,1.0};
        System.out.println("DEFAULT");
        
        
    }
    
    
    public String getSubsetName() {
        return name;
    }

    public double[] getColor() {
        return colors;
    }

   // public int getState() {
   //     return state;
    //}

    public ArrayList<Integer> getVertices() {
        return vertices;
    }

    public ArrayList<Integer> getEdges() {
        return edges;
    }

    public ArrayList<Integer> getFaces() {
        return faces;
    }
    
    public ArrayList<Integer> getVolumes() {
        return volumes;
    }
    

    public void setSubsetName(String subsetName) {
        this.name = subsetName;
    }

    public void setColor(double[] color) {
        this.colors = color;
    }

   // public void setState(int state) {
  //      this.state = state;
   // }

    public void setVertices(ArrayList<Integer> vertices) {
        this.vertices = vertices;
    }

    public void setEdges(ArrayList<Integer> edges) {
        this.edges = edges;
    }

    public void setFaces(ArrayList<Integer> faces) {
        this.faces = faces;
    }
    
    public void setVolumes(ArrayList<Integer> volumes) {
        this.volumes = volumes;
    }
    
    public void addVertex(int vertex){
        this.vertices.add(vertex);
    }
    
    public void addEdge(int edge){
        this.edges.add(edge);
    }
    
    public void addFace(int face){
        this.faces.add(face);
    }
    
    public void addVolume(int volume){
        this.volumes.add(volume);
    }
    
    public int[] getFacesArray(){
            int[] facesArray = new int[faces.size()];
            for (int i = 0; i < faces.size(); i++) {
                facesArray[i] = faces.get(i);
            }
            return facesArray;
        }
    
    public int[] getVolumeArray(){
                   
            int[] volArray = new int[volumes.size()];
            for (int i = 0; i < volumes.size(); i++) {
                volArray[i] = volumes.get(i);
            }
            return volArray;
        }
    
    public void convertReaderStringToData(){
             
        this.vertices = new ArrayList<Integer>();
        this.edges = new ArrayList<Integer>();
        this.faces = new ArrayList<Integer>();
        this.volumes = new ArrayList<Integer>();
        
        try {
            if (vert.length() > 0) {
                vertices = new ArrayList<Integer>();
                String[] vertStringArray = vert.split(" ");
                for (int i = 0; i < vertStringArray.length; i++) {
                    vertices.add(Integer.parseInt(vertStringArray[i]));
                }
                hasVertices = true;
            }
        } catch (Exception e) {
           
        }
        
        try {
            if (edge.length() > 0) {
                edges = new ArrayList<Integer>();
                String[] edgesStringArray = edge.split(" ");
                for (int i = 0; i < edgesStringArray.length; i++) {
                    edges.add(Integer.parseInt(edgesStringArray[i]));
                }
                hasEdges = true;
            }
        } catch (Exception e) {
          
        }
        
        try {
            if (face.length() > 0) {
                faces = new ArrayList<Integer>();
                String[] facesStringArray = face.split(" ");
                for (int i = 0; i < facesStringArray.length; i++) {
                    faces.add(Integer.parseInt(facesStringArray[i]));
                }
                hasFaces = true;
            }
        } catch (Exception e) {
         
        }
        
        try {
            if (vol.length() > 0) {
                volumes = new ArrayList<Integer>();
                String[] volumesStringArray = vol.split(" ");
                for (int i = 0; i < volumesStringArray.length; i++) {
                    volumes.add(Integer.parseInt(volumesStringArray[i]));
                }
                hasVolumes = true;
            }
        } catch (Exception e) {
           
        }
        
        try {
            if (colorString.length() > 0) {
                this.colors = new double[4];
                String[] colorStringArray = colorString.split(" ");
                for (int i = 0; i < colorStringArray.length; i++) {
                    this.colors[i] = Double.parseDouble(colorStringArray[i]);
                }
            }
        } catch (Exception e) {
           
        }
        
        
    }

    public boolean isHasVertices() {
        return hasVertices;
    }

    public boolean isHasEdges() {
        return hasEdges;
    }

    public boolean isHasFaces() {
        return hasFaces;
    }

    public boolean isHasVolumes() {
        return hasVolumes;
    }
    
    
    
    
}
