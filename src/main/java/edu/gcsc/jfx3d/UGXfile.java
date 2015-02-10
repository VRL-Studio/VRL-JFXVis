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
    
    
    @XStreamAlias("vertices")
    private String verticesString;
    @XStreamAlias("edges")
    private String edgesString;
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
    
    private int geometryCounter2D = 0;
    private int geometryCounter3D = 0;
    
    
    private boolean containsVertices = false;
    private boolean containsEdges = false;
    private boolean containsTriangles = false;
    private boolean containsQuadrilaterals = false;
    private boolean containsTetrahedrons = false;
    private boolean containsHexahedrons = false;
    private boolean containsPrisms = false;
    private boolean containsPyramids = false;
    
    
    private ArrayList<Float> globalVertices = new ArrayList<Float>();
    
    
    private ArrayList<Edge> edges = new ArrayList<Edge>();
    
    private ArrayList<Triangle> triangles = new ArrayList<Triangle>();
    private ArrayList<Quadrilateral> quadrilaterals = new ArrayList<Quadrilateral>();
    private ArrayList<Tetrahedron> tetrahedrons = new ArrayList<Tetrahedron>();
    private ArrayList<Hexahedron> hexahedrons = new ArrayList<Hexahedron>();
    private ArrayList<Prism> prisms = new ArrayList<Prism>();
    private ArrayList<Pyramid> pyramids = new ArrayList<Pyramid>();
    
    
    @XStreamImplicit(itemFieldName="subset_handler")
    private ArrayList<UGXsubsetHandler> subset_handler = new ArrayList<UGXsubsetHandler>();
    

    
    public UGXfile(){
        System.out.println("UGX CONSTRUCT");
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setGlobalVertices(ArrayList<Float> globalVertices) {
        this.globalVertices = globalVertices;
    }

    public void setEdges(ArrayList<Edge> edges) {
        this.edges = edges;
    }

    public void setTriangles(ArrayList<Triangle> triangles) {
        this.triangles = triangles;
    }

    public void setQuadrilaterals(ArrayList<Quadrilateral> quadrilaterals) {
        this.quadrilaterals = quadrilaterals;
    }

    public void setTetrahedrons(ArrayList<Tetrahedron> tetrahedrons) {
        this.tetrahedrons = tetrahedrons;
    }

    public void setHexahedrons(ArrayList<Hexahedron> hexahedrons) {
        this.hexahedrons = hexahedrons;
    }

    public void setPrisms(ArrayList<Prism> prisms) {
        this.prisms = prisms;
    }

    public void setPyramids(ArrayList<Pyramid> pyramids) {
        this.pyramids = pyramids;
    }

    public void setSubset_handler(ArrayList<UGXsubsetHandler> subset_handler) {
        this.subset_handler = subset_handler;
    }

    public String getPath() {
        return path;
    }

    public ArrayList<Float> getGlobalVertices() {
        return globalVertices;
    }

    public ArrayList<Edge> getEdges() {
        return edges;
    }

    public ArrayList<Triangle> getTriangles() {
        return triangles;
    }

    public ArrayList<Quadrilateral> getQuadrilaterals() {
        return quadrilaterals;
    }

    public ArrayList<Tetrahedron> getTetrahedrons() {
        return tetrahedrons;
    }

    public ArrayList<Hexahedron> getHexahedrons() {
        return hexahedrons;
    }

    public ArrayList<Prism> getPrisms() {
        return prisms;
    }

    public ArrayList<Pyramid> getPyramids() {
        return pyramids;
    }

    public ArrayList<UGXsubsetHandler> getSubset_handler() {
        return subset_handler;
    }
    
    
    public void convertReaderStringToData(){
        this.globalVertices = new ArrayList<Float>();
        this.edges = new ArrayList<Edge>();
        this.triangles = new ArrayList<Triangle>();
        this.quadrilaterals = new ArrayList<Quadrilateral>();
        this.tetrahedrons = new ArrayList<Tetrahedron>();
        this.hexahedrons = new ArrayList<Hexahedron>();
        this.prisms = new ArrayList<Prism>();
        this.pyramids = new ArrayList<Pyramid>();
        
        try {
            if (verticesString.length() > 0) {
                globalVertices = new ArrayList<Float>();
                String[] vertexStringArray = verticesString.split(" ");
                for (int i = 0; i < vertexStringArray.length; i++) {
                    globalVertices.add(Float.parseFloat(vertexStringArray[i]));
                }
                containsVertices = true;
            }
        } catch (Exception e) {
           System.out.println("No vertices were found.");
        }
        
        try {
            if (edgesString.length() > 0) {
                edges = new ArrayList<Edge>();
                String[] edgesStringArray = edgesString.split(" ");
                for (int i = 0; i < edgesStringArray.length; i+=2) {
                    Vertex3D p0 = new Vertex3D(globalVertices.get(Integer.parseInt(edgesStringArray[i])*3),
                                              globalVertices.get(Integer.parseInt(edgesStringArray[i])*3+1), 
                                              globalVertices.get(Integer.parseInt(edgesStringArray[i])*3+2));
                    Vertex3D p1 = new Vertex3D(globalVertices.get(Integer.parseInt(edgesStringArray[i+1])*3),
                                              globalVertices.get(Integer.parseInt(edgesStringArray[i+1])*3+1), 
                                              globalVertices.get(Integer.parseInt(edgesStringArray[i+1])*3+2));
                    
                    edges.add(new Edge(p0, p1));
                }
                containsEdges = true;
            }
        } catch (Exception e) {
           System.out.println("No edges were found.");
        }
        
                try {
            if (triangleString.length() > 0) {
                triangles = new ArrayList<Triangle>();
                String[] triangleStringArray = triangleString.split(" ");
             
                for (int i = 0; i < triangleStringArray.length; i+=3) {
                    
                    int pp0 = Integer.parseInt(triangleStringArray[i]);
                    int pp1 = Integer.parseInt(triangleStringArray[i+1]);
                    int pp2 = Integer.parseInt(triangleStringArray[i+2]);
//                    Vertex3D p0 = new Vertex3D(Integer.parseInt(triangleStringArray[i])*3,
//                                              Integer.parseInt(triangleStringArray[i])*3+1, 
//                                              Integer.parseInt(triangleStringArray[i])*3+2);
//                    Vertex3D p1 = new Vertex3D(Integer.parseInt(triangleStringArray[i+1])*3,
//                                              Integer.parseInt(triangleStringArray[i+1])*3+1, 
//                                              Integer.parseInt(triangleStringArray[i+1])*3+2);
//                    Vertex3D p2 = new Vertex3D(Integer.parseInt(triangleStringArray[i+2])*3,
//                                              Integer.parseInt(triangleStringArray[i+2])*3+1, 
//                                              Integer.parseInt(triangleStringArray[i+2])*3+2);

                    
                    triangles.add(new Triangle(pp0,pp1,pp2,geometryCounter2D++));
                }
                containsTriangles = true;
            }
        } catch (Exception e) {
            System.out.println("No triangles were found.");
           
        }
                
        try {
            if(quadrilateralString.length() > 0){
                String[] quadriStringArray = quadrilateralString.split(" ");
                for (int i = 0; i < quadriStringArray.length; i+=4) {
                    
                    int pp0 = Integer.parseInt(quadriStringArray[i]);
                    int pp1 = Integer.parseInt(quadriStringArray[i+1]);
                    int pp2 = Integer.parseInt(quadriStringArray[i+2]);
                    int pp3 = Integer.parseInt(quadriStringArray[i+3]);
                    
                    
//                    Vertex3D p0 = new Vertex3D(globalVertices.get(Integer.parseInt(quadriStringArray[i]) * 3),
//                                              globalVertices.get(Integer.parseInt(quadriStringArray[i]) * 3 + 1),
//                                              globalVertices.get(Integer.parseInt(quadriStringArray[i]) * 3 + 2));
//                    Vertex3D p1 = new Vertex3D(globalVertices.get(Integer.parseInt(quadriStringArray[i+1]) * 3),
//                                              globalVertices.get(Integer.parseInt(quadriStringArray[i+1]) * 3 + 1),
//                                              globalVertices.get(Integer.parseInt(quadriStringArray[i+1]) * 3 + 2));
//                    Vertex3D p2 = new Vertex3D(globalVertices.get(Integer.parseInt(quadriStringArray[i+2]) * 3),
//                                              globalVertices.get(Integer.parseInt(quadriStringArray[i+2]) * 3 + 1),
//                                              globalVertices.get(Integer.parseInt(quadriStringArray[i+2]) * 3 + 2));
//                    Vertex3D p3 = new Vertex3D(globalVertices.get(Integer.parseInt(quadriStringArray[i+3]) * 3),
//                                              globalVertices.get(Integer.parseInt(quadriStringArray[i+3]) * 3 + 1),
//                                              globalVertices.get(Integer.parseInt(quadriStringArray[i+3]) * 3 + 2)); 
                    
                    
//                    System.out.println(pp0);
//                    System.out.println(pp1);
//                    System.out.println(pp2);
//                    System.out.println(pp3);
//                    System.out.println("--------------");
                    
                    quadrilaterals.add(new Quadrilateral(pp0, pp1, pp2, pp3, geometryCounter2D++));
                }
                containsQuadrilaterals = true;
            }
            
        } catch (Exception e) {
            System.out.println("No quadrilaterals were found.");
        }
                
                
                
        try {
            if (tetrahedronString.length() > 0) {
                String[] tetraStringArray = tetrahedronString.split(" ");
                for (int i = 0; i < tetraStringArray.length; i+=4) {
                    
                    int pp0 = Integer.parseInt(tetraStringArray[i]);
                    int pp1 = Integer.parseInt(tetraStringArray[i+1]);
                    int pp2 = Integer.parseInt(tetraStringArray[i+2]);
                    int pp3 = Integer.parseInt(tetraStringArray[i+3]);
                    
                    
//                    Vertex3D p0 = new Vertex3D(globalVertices.get(Integer.parseInt(tetraStringArray[i]) * 3),
//                                              globalVertices.get(Integer.parseInt(tetraStringArray[i]) * 3 + 1),
//                                              globalVertices.get(Integer.parseInt(tetraStringArray[i]) * 3 + 2));
//                    Vertex3D p1 = new Vertex3D(globalVertices.get(Integer.parseInt(tetraStringArray[i+1]) * 3),
//                                              globalVertices.get(Integer.parseInt(tetraStringArray[i+1]) * 3 + 1),
//                                              globalVertices.get(Integer.parseInt(tetraStringArray[i+1]) * 3 + 2));
//                    Vertex3D p2 = new Vertex3D(globalVertices.get(Integer.parseInt(tetraStringArray[i+2]) * 3),
//                                              globalVertices.get(Integer.parseInt(tetraStringArray[i+2]) * 3 + 1),
//                                              globalVertices.get(Integer.parseInt(tetraStringArray[i+2]) * 3 + 2));
//                    Vertex3D p3 = new Vertex3D(globalVertices.get(Integer.parseInt(tetraStringArray[i+3]) * 3),
//                                              globalVertices.get(Integer.parseInt(tetraStringArray[i+3]) * 3 + 1),
//                                              globalVertices.get(Integer.parseInt(tetraStringArray[i+3]) * 3 + 2));
 
                    tetrahedrons.add(new Tetrahedron(pp0, pp1, pp2, pp3, geometryCounter3D++));
                }
                containsTetrahedrons = true;
            }
        } catch (Exception e) {
            System.out.println("No tetrahedrons were found.");
        }
                
        try {
            if (hexahedronString.length() > 0) {
                String[] hexaStringArray = hexahedronString.split(" ");
                for (int i = 0; i < hexaStringArray.length; i+=8) {
                    
                    
                    int pp0 = Integer.parseInt(hexaStringArray[i]);
                    int pp1 = Integer.parseInt(hexaStringArray[i+1]);
                    int pp2 = Integer.parseInt(hexaStringArray[i+2]);
                    int pp3 = Integer.parseInt(hexaStringArray[i+3]);
                    int pp4 = Integer.parseInt(hexaStringArray[i+4]);
                    int pp5 = Integer.parseInt(hexaStringArray[i+5]);
                    int pp6 = Integer.parseInt(hexaStringArray[i+6]);
                    int pp7 = Integer.parseInt(hexaStringArray[i+7]);
                    
//                    Vertex3D p0 = new Vertex3D(globalVertices.get(Integer.parseInt(hexaStringArray[i]) * 3),
//                                              globalVertices.get(Integer.parseInt(hexaStringArray[i]) * 3 + 1),
//                                              globalVertices.get(Integer.parseInt(hexaStringArray[i]) * 3 + 2));
//                    Vertex3D p1 = new Vertex3D(globalVertices.get(Integer.parseInt(hexaStringArray[i+1]) * 3),
//                                              globalVertices.get(Integer.parseInt(hexaStringArray[i+1]) * 3 + 1),
//                                              globalVertices.get(Integer.parseInt(hexaStringArray[i+1]) * 3 + 2));
//                    Vertex3D p2 = new Vertex3D(globalVertices.get(Integer.parseInt(hexaStringArray[i+2]) * 3),
//                                              globalVertices.get(Integer.parseInt(hexaStringArray[i+2]) * 3 + 1),
//                                              globalVertices.get(Integer.parseInt(hexaStringArray[i+2]) * 3 + 2));
//                    Vertex3D p3 = new Vertex3D(globalVertices.get(Integer.parseInt(hexaStringArray[i+3]) * 3),
//                                              globalVertices.get(Integer.parseInt(hexaStringArray[i+3]) * 3 + 1),
//                                              globalVertices.get(Integer.parseInt(hexaStringArray[i+3]) * 3 + 2));
//                    Vertex3D p4 = new Vertex3D(globalVertices.get(Integer.parseInt(hexaStringArray[i+4]) * 3),
//                                              globalVertices.get(Integer.parseInt(hexaStringArray[i+4]) * 3 + 1),
//                                              globalVertices.get(Integer.parseInt(hexaStringArray[i+4]) * 3 + 2));
//                    Vertex3D p5 = new Vertex3D(globalVertices.get(Integer.parseInt(hexaStringArray[i+5]) * 3),
//                                              globalVertices.get(Integer.parseInt(hexaStringArray[i+5]) * 3 + 1),
//                                              globalVertices.get(Integer.parseInt(hexaStringArray[i+5]) * 3 + 2));
//                    Vertex3D p6 = new Vertex3D(globalVertices.get(Integer.parseInt(hexaStringArray[i+6]) * 3),
//                                              globalVertices.get(Integer.parseInt(hexaStringArray[i+6]) * 3 + 1),
//                                              globalVertices.get(Integer.parseInt(hexaStringArray[i+6]) * 3 + 2));
//                    Vertex3D p7 = new Vertex3D(globalVertices.get(Integer.parseInt(hexaStringArray[i+7]) * 3),
//                                              globalVertices.get(Integer.parseInt(hexaStringArray[i+7]) * 3 + 1),
//                                              globalVertices.get(Integer.parseInt(hexaStringArray[i+7]) * 3 + 2));
                          
                    hexahedrons.add(new Hexahedron(pp0, pp1, pp2, pp3, pp4, pp5, pp6, pp7, geometryCounter3D++));
                }
                containsHexahedrons = true;
            }
        } catch (Exception e) {
            System.out.println("No hexahedrons were found.");
        }
        
        try {
            if(prismString.length() > 0){
                String[] prismStringArray = prismString.split(" ");
                for (int i = 0; i < prismStringArray.length; i+=6) {
                    
                    
                    int pp0 = Integer.parseInt(prismStringArray[i]);
                    int pp1 = Integer.parseInt(prismStringArray[i+1]);
                    int pp2 = Integer.parseInt(prismStringArray[i+2]);
                    int pp3 = Integer.parseInt(prismStringArray[i+3]);
                    int pp4 = Integer.parseInt(prismStringArray[i+4]);
                    int pp5 = Integer.parseInt(prismStringArray[i+5]);
                    
//                    Vertex3D p0 = new Vertex3D(globalVertices.get(Integer.parseInt(prismStringArray[i]) * 3),
//                                              globalVertices.get(Integer.parseInt(prismStringArray[i]) * 3 + 1),
//                                              globalVertices.get(Integer.parseInt(prismStringArray[i]) * 3 + 2));
//                    Vertex3D p1 = new Vertex3D(globalVertices.get(Integer.parseInt(prismStringArray[i+1]) * 3),
//                                              globalVertices.get(Integer.parseInt(prismStringArray[i+1]) * 3 + 1),
//                                              globalVertices.get(Integer.parseInt(prismStringArray[i+1]) * 3 + 2));
//                    Vertex3D p2 = new Vertex3D(globalVertices.get(Integer.parseInt(prismStringArray[i+2]) * 3),
//                                              globalVertices.get(Integer.parseInt(prismStringArray[i+2]) * 3 + 1),
//                                              globalVertices.get(Integer.parseInt(prismStringArray[i+2]) * 3 + 2));
//                    Vertex3D p3 = new Vertex3D(globalVertices.get(Integer.parseInt(prismStringArray[i+3]) * 3),
//                                              globalVertices.get(Integer.parseInt(prismStringArray[i+3]) * 3 + 1),
//                                              globalVertices.get(Integer.parseInt(prismStringArray[i+3]) * 3 + 2));
//                    Vertex3D p4 = new Vertex3D(globalVertices.get(Integer.parseInt(prismStringArray[i+4]) * 3),
//                                              globalVertices.get(Integer.parseInt(prismStringArray[i+4]) * 3 + 1),
//                                              globalVertices.get(Integer.parseInt(prismStringArray[i+4]) * 3 + 2));
//                    Vertex3D p5 = new Vertex3D(globalVertices.get(Integer.parseInt(prismStringArray[i+5]) * 3),
//                                              globalVertices.get(Integer.parseInt(prismStringArray[i+5]) * 3 + 1),
//                                              globalVertices.get(Integer.parseInt(prismStringArray[i+5]) * 3 + 2));
                    
                    prisms.add(new Prism(pp0, pp1, pp2, pp3, pp4, pp5, geometryCounter3D++));
                }
                containsPrisms = true;
            }
        } catch (Exception e) {
            System.out.println("No prisms were found.");
        }
        
        try {
            if(pyramidString.length() > 0){
                String[] pyraStringArray = pyramidString.split(" ");
                for (int i = 0; i < pyraStringArray.length; i+=5) {
                    
                    
                    int pp0 = Integer.parseInt(pyraStringArray[i]);
                    int pp1 = Integer.parseInt(pyraStringArray[i+1]);
                    int pp2 = Integer.parseInt(pyraStringArray[i+2]);
                    int pp3 = Integer.parseInt(pyraStringArray[i+3]);
                    int pp4 = Integer.parseInt(pyraStringArray[i+4]);
                    
//                    Vertex3D p0 = new Vertex3D(globalVertices.get(Integer.parseInt(pyraStringArray[i]) * 3),
//                                              globalVertices.get(Integer.parseInt(pyraStringArray[i]) * 3 + 1),
//                                              globalVertices.get(Integer.parseInt(pyraStringArray[i]) * 3 + 2));
//                    Vertex3D p1 = new Vertex3D(globalVertices.get(Integer.parseInt(pyraStringArray[i+1]) * 3),
//                                              globalVertices.get(Integer.parseInt(pyraStringArray[i+1]) * 3 + 1),
//                                              globalVertices.get(Integer.parseInt(pyraStringArray[i+1]) * 3 + 2));
//                    Vertex3D p2 = new Vertex3D(globalVertices.get(Integer.parseInt(pyraStringArray[i+2]) * 3),
//                                              globalVertices.get(Integer.parseInt(pyraStringArray[i+2]) * 3 + 1),
//                                              globalVertices.get(Integer.parseInt(pyraStringArray[i+2]) * 3 + 2));
//                    Vertex3D p3 = new Vertex3D(globalVertices.get(Integer.parseInt(pyraStringArray[i+3]) * 3),
//                                              globalVertices.get(Integer.parseInt(pyraStringArray[i+3]) * 3 + 1),
//                                              globalVertices.get(Integer.parseInt(pyraStringArray[i+3]) * 3 + 2));
//                    Vertex3D p4 = new Vertex3D(globalVertices.get(Integer.parseInt(pyraStringArray[i+4]) * 3),
//                                              globalVertices.get(Integer.parseInt(pyraStringArray[i+4]) * 3 + 1),
//                                              globalVertices.get(Integer.parseInt(pyraStringArray[i+4]) * 3 + 2));
                    
                    pyramids.add(new Pyramid(pp0, pp1, pp2, pp3, pp4, geometryCounter3D++));
                }
                containsPyramids = true;
            }
        } catch (Exception e) {
            System.out.println("No pyramids were found.");
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
               // triangleArray[i] = triangles.get(i);
            }
            return triangleArray;
        }
        
        public int[] getTetraedronsArray(){
            int[] tetraArray = new int[tetrahedrons.size()];
            for (int i = 0; i < tetrahedrons.size(); i++) {
              //  tetraArray[i] = tetrahedrons.get(i);
            }
            return tetraArray;
        }

    public boolean containsVertices() {
        return containsVertices;
    }

    public boolean containsEdges() {
        return containsEdges;
    }

    public boolean containsTriangles() {
        return containsTriangles;
    }

    public boolean containsQuadrilaterals() {
        return containsQuadrilaterals;
    }

    public boolean containsTetrahedrons() {
        return containsTetrahedrons;
    }

    public boolean containsHexahedrons() {
        return containsHexahedrons;
    }

    public boolean containsPrisms() {
        return containsPrisms;
    }

    public boolean containsPyramids() {
        return containsPyramids;
    }
        

        

}
