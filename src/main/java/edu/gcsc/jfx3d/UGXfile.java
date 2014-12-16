/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.gcsc.jfx3d;


import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import java.awt.List;
import java.util.ArrayList;


/**
 *
 * @author Eugen
 */

public class UGXfile {
    
    private String path;
    
    
    private ArrayList grid = new ArrayList();
    
    @XStreamAlias("vertices")
    private String verticesString ;
    @XStreamAlias("edges")
    private String edgesString ;
    @XStreamAlias("triangles")
    private String triangleString;
    @XStreamAlias("quadrilaterals")
    private String quadrilateralString;
    @XStreamAlias("tetrahedrons")
    private String tetrahedronString;
    @XStreamAlias("hexahedrons")
    private String hexahedronString;
    @XStreamAlias("prisms")
    private String prismString;
    @XStreamAlias("pyramids")
    private String pyramidString;
    
    
    private ArrayList<Float> globalVertices = new ArrayList<Float>();
    
    
    private ArrayList<Integer> edges = new ArrayList<Integer>();
    
    private ArrayList<Integer> triangles = new ArrayList<Integer>();
    private ArrayList<Integer> quadrilaterals = new ArrayList<Integer>();
    private ArrayList<Integer> tetrahedrons = new ArrayList<Integer>();
    private ArrayList<Integer> hexahedrons = new ArrayList<Integer>();
    private ArrayList<Integer> prisms = new ArrayList<Integer>();
    private ArrayList<Integer> pyramids = new ArrayList<Integer>();
    
    
    @XStreamImplicit(itemFieldName="subset_handler")
    private ArrayList<UGXsubsetHandler> subset_handler = new ArrayList<UGXsubsetHandler>();
    
    
  //  @XStreamImplicit(itemFieldName ="subset")
  //  private ArrayList<UGXsubset> subsets = new ArrayList<UGXsubset>();
    
    
    public UGXfile(){
        System.out.println("UGX CONSTRUCT");
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setGrid(ArrayList grid) {
        this.grid = grid;
    }

    public void setGlobalVertices(ArrayList<Float> globalVertices) {
        this.globalVertices = globalVertices;
    }

    public void setEdges(ArrayList<Integer> edges) {
        this.edges = edges;
    }

    public void setTriangles(ArrayList<Integer> triangles) {
        this.triangles = triangles;
    }

    public void setQuadrilaterals(ArrayList<Integer> quadrilaterals) {
        this.quadrilaterals = quadrilaterals;
    }

    public void setTetrahedrons(ArrayList<Integer> tetrahedrons) {
        this.tetrahedrons = tetrahedrons;
    }

    public void setHexahedrons(ArrayList<Integer> hexahedrons) {
        this.hexahedrons = hexahedrons;
    }

    public void setPrisms(ArrayList<Integer> prisms) {
        this.prisms = prisms;
    }

    public void setPyramids(ArrayList<Integer> pyramids) {
        this.pyramids = pyramids;
    }

    public void setSubset_handler(ArrayList<UGXsubsetHandler> subset_handler) {
        this.subset_handler = subset_handler;
    }

    public String getPath() {
        return path;
    }

    public ArrayList getGrid() {
        return grid;
    }

    public ArrayList<Float> getGlobalVertices() {
        return globalVertices;
    }

    public ArrayList<Integer> getEdges() {
        return edges;
    }

    public ArrayList<Integer> getTriangles() {
        return triangles;
    }

    public ArrayList<Integer> getQuadrilaterals() {
        return quadrilaterals;
    }

    public ArrayList<Integer> getTetrahedrons() {
        return tetrahedrons;
    }

    public ArrayList<Integer> getHexahedrons() {
        return hexahedrons;
    }

    public ArrayList<Integer> getPrisms() {
        return prisms;
    }

    public ArrayList<Integer> getPyramids() {
        return pyramids;
    }

    public ArrayList<UGXsubsetHandler> getSubset_handler() {
        return subset_handler;
    }
    
    
    public void convertReaderStringToData(){
        this.tetrahedrons = new ArrayList<>();
        
        try {
            if (verticesString.length() > 0) {
                globalVertices = new ArrayList<Float>();
                String[] volumesStringArray = verticesString.split(" ");
                for (int i = 0; i < volumesStringArray.length; i++) {
                    globalVertices.add(Float.parseFloat(volumesStringArray[i]));
                }
            }
        } catch (Exception e) {
           e.printStackTrace(System.out);
        }
        
        try {
            if (edgesString.length() > 0) {
                edges = new ArrayList<Integer>();
                String[] edgesStringArray = edgesString.split(" ");
                for (int i = 0; i < edgesStringArray.length; i++) {
                    edges.add(Integer.parseInt(edgesStringArray[i]));
                }
            }
        } catch (Exception e) {
           e.printStackTrace(System.out);
        }
        
                try {
            if (triangleString.length() > 0) {
                triangles = new ArrayList<Integer>();
                String[] triangleStringArray = triangleString.split(" ");
                for (int i = 0; i < triangleStringArray.length; i++) {
                    triangles.add(Integer.parseInt(triangleStringArray[i]));
                }
            }
        } catch (Exception e) {
            e.printStackTrace(System.out);
           
        }
                
                
        
                if(tetrahedronString.length() > 0){
                    String[] tetraStringArray = tetrahedronString.split(" ");
                    for (int i = 0; i < tetraStringArray.length; i++) {
                        tetrahedrons.add(Integer.parseInt(tetraStringArray[i]));
                    }
                }
        
                
        
    }
    
    public float[] getGlobalVerticesArray() {
        float[] globalVertexArray = new float[globalVertices.size()];
        for (int i = 0; i < globalVertices.size(); i++) {
            globalVertexArray[i] = globalVertices.get(i);
        }
        return globalVertexArray;
    }
    
        public int[] getTrianglesArray(){
            int[] triangleArray = new int[triangles.size()];
            for (int i = 0; i < triangles.size(); i++) {
                triangleArray[i] = triangles.get(i);
            }
            return triangleArray;
        }
        
        public int[] getTetraedronsArray(){
            int[] tetraArray = new int[tetrahedrons.size()];
            for (int i = 0; i < tetrahedrons.size(); i++) {
                tetraArray[i] = tetrahedrons.get(i);
            }
            return tetraArray;
        }

}
