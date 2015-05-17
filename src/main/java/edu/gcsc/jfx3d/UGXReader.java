/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.gcsc.jfx3d;

import com.thoughtworks.xstream.XStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.geometry.Point3D;
import javafx.scene.AmbientLight;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.PickResult;
import javafx.scene.paint.Color;
import javafx.scene.paint.Material;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.CullFace;
import javafx.scene.shape.DrawMode;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.Shape3D;
import javafx.scene.shape.Sphere;
import javafx.scene.shape.TriangleMesh;

/**
 *
 * @author Eugen
 */
public class UGXReader {
    
    
    private String path;
    private ArrayList<Float> vertices = new ArrayList<Float>() ;
    private ArrayList<Integer> triangles = new ArrayList<Integer>();
    private ArrayList<Integer> tetra = new ArrayList<Integer>();
    private ArrayList<UGXsubset> subsetList = new ArrayList<UGXsubset>();
    private UGXsubsetHandler subsetHandler;
    private ArrayList<Node> vertexNodeSelection = new ArrayList<Node>();
    private ArrayList<Node> edgeNodeSelection = new ArrayList<Node>();
    private ArrayList<Material> vertexNodeSelectionMaterial = new ArrayList<Material>();
    private ArrayList<Material> edgeNodeSelectionMaterial = new ArrayList<Material>();
    private ArrayList<Node> faceNodeSelection = new ArrayList<>();
    private ArrayList<Material> faceNodeSelectionMaterial = new ArrayList<>();
    private ArrayList<Node> subsetNodeSelection = new ArrayList<>();
    private ArrayList<Material> subsetNodeSelectionMaterial = new ArrayList<>();
    
    private HashMap<Sphere,float[]> vertexMap = new HashMap<Sphere,float[]>();
    private HashMap<MeshView,Edge> edgesMap = new HashMap<>();
    private HashMap<Integer,int[]> faceMap = new HashMap<>();
    private HashMap<MeshView,Geometry2D> newFaceMap = new HashMap<>();
    private HashMap<MeshView,Integer> faceMapMesh = new HashMap<>();
    private HashMap<MeshView,UGXsubset> subsetMapMeshToSubset = new HashMap<>();
    private HashMap<UGXsubset,ArrayList<MeshView>> subsetMapSubsetToMesh = new HashMap<>();
    
    private boolean highResolution = false;
    private boolean doubleFacesOnEdges = true;
    private boolean renderFaces = true;
    private boolean debugMode = false;
    private boolean renderVertices = true;
    private boolean renderEdges = true;
    private boolean renderVolumes = true;
    
    ArrayList<Float> globalVertexList = new ArrayList<>();
    boolean strgPressed = false; 
    


    /**
     *Constructor for the UGXReader class.Checks if the given filepath is valid.
     * 
     * @param filePath filepath to the target .ugx file
     */
    public UGXReader(String filePath){
        
        path = filePath;
        
        try {
        FileInputStream file = new FileInputStream(path);
        Scanner scanner = new Scanner(file);
        System.out.println("Testing file " + path);
        try {
            if (scanner.hasNext()) {       
            System.out.println("OK."); 
            }          
        } finally {
            scanner.close();   
        }
    }   catch (FileNotFoundException ex) {
        ex.printStackTrace(System.out);
    }
    }

    /**
    *reads the content of the .ugx file using the xstream library
     * @return UGXfile object, containing all information about the given .ugx file
    **/
    private UGXfile xread(){
        FileReader filereader = null ;
        
        try {
            filereader = new FileReader(path);
            System.out.println("Reading file " + path);
        } catch (FileNotFoundException ex) {
            System.out.println("File was not found");
            ex.printStackTrace(System.out);
        }
        XStream xstream = new XStream();
        xstream.alias("grid", UGXfile.class);
        xstream.alias("subset_handler",UGXsubset.class);
        
        xstream.processAnnotations(UGXfile.class);
        xstream.processAnnotations(UGXsubset.class);
        xstream.omitField(UGXfile.class, "selector");
        xstream.omitField(UGXfile.class, "subset_handler name=\"markSH\"");
        
        xstream.useAttributeFor(UGXsubset.class, "name");
        xstream.useAttributeFor(UGXsubset.class, "colorString");
        
        
        UGXfile ugxfile = (UGXfile) xstream.fromXML(filereader);
        
       // xstream.toXML(ugxfile, System.out);
        ugxfile.convertReaderStringToData();
        
        for (int i = 0; i < ugxfile.getSubset_handler().get(0).getSubsets().size(); i++) {
            ugxfile.getSubset_handler().get(0).getSubsets().get(i).convertReaderStringToData();
        }

        for (int i = 0; i < ugxfile.getSubset_handler().get(0).getSubsets().size(); i++) {
            ugxfile.getSubset_handler().get(0).getSubsets().get(i).getSubsetName();
        }

        return ugxfile;
        
    }
    
    
    /**Creates a group Node representation of the .ugx file with selectable nodes depending on the parameters
     * that were set before using the setFlag* methods.
     * @return  group Node visualization of the .ugx file**/
    public Group xbuildUGX (){
        
        UGXfile ugxfile = xread();
        subsetHandler = ugxfile.getSubset_handler().get(0);
        globalVertexList = ugxfile.getGlobalVertices();
        
        float[] vertices = ugxfile.getGlobalVerticesArray();
        ArrayList<Edge> edges = ugxfile.getEdges();
        ArrayList<Triangle> triangles = ugxfile.getTriangles();
        ArrayList<Quadrilateral> quadrilaterals = ugxfile.getQuadrilaterals();
        ArrayList<Tetrahedron> tetrahedrons = ugxfile.getTetrahedrons();
        ArrayList<Hexahedron> hexahedrons = ugxfile.getHexahedrons();
        ArrayList<Prism> prisms = ugxfile.getPrisms();
        ArrayList<Pyramid> pyramids = ugxfile.getPyramids();
        TriangleMesh mesh = new TriangleMesh();
        
        
        ArrayList<Geometry2D> geometry2DList = new ArrayList<Geometry2D>();
        ArrayList<Geometry3D> geometry3DList = new ArrayList<Geometry3D>();
        
        if (ugxfile.containsTriangles()) {
            for (int i = 0; i < triangles.size(); i++) {
                geometry2DList.add(triangles.get(i));
            }
        }
        
        if (ugxfile.containsQuadrilaterals()) {
            for (int i = 0; i < quadrilaterals.size(); i++) {
                geometry2DList.add(quadrilaterals.get(i));
            }
        }
        
        if (ugxfile.containsTetrahedrons()) {
            for (int i = 0; i < tetrahedrons.size(); i++) {
                geometry3DList.add(tetrahedrons.get(i));
            }
        }
        
        if (ugxfile.containsHexahedrons()) {
            for (int i = 0; i < hexahedrons.size(); i++) {
                geometry3DList.add(hexahedrons.get(i));
            }
        }
        
        if (ugxfile.containsPrisms()) {
            for (int i = 0; i < prisms.size(); i++) {
                geometry3DList.add(prisms.get(i));
            }
        }
        
        if (ugxfile.containsPyramids()) {
            for (int i = 0; i < pyramids.size(); i++) {
                geometry3DList.add(pyramids.get(i));
            }
        }
        
        mesh.getPoints().addAll(vertices);
        mesh.getTexCoords().addAll(0,0);
        
        
        int ssNumber = ugxfile.getSubset_handler().get(0).getSubsets().size();
       
        TriangleMesh[] meshArray = new TriangleMesh[ssNumber];
        for (int i = 0; i < ssNumber; i++) {
            meshArray[i] = new TriangleMesh();
        }
        int[] ssVertices;
        int[] ssEdges;
        int[] ssFaces;
        int[] ssVolumes;
        
       
        MeshView[] meshViewArray = new MeshView[ssNumber];
        Group subsetGroup = new Group();
        for (int i = 0; i < ssNumber; i++) { //for each subset: visualize all elements in it (vertices,edges,...)
            
            meshArray[i].getPoints().addAll(vertices);
            meshArray[i].getTexCoords().addAll(0,0);
            Group vertexGroup = new Group();
            Group edgesGroup = new Group();
            Group faceGroup = new Group();
            ssVertices = ugxfile.getSubset_handler().get(0).getSubsets().get(i).getVertexArray();
            
            ssEdges = ugxfile.getSubset_handler().get(0).getSubsets().get(i).getEdgeArray();
            
            ssFaces = ugxfile.getSubset_handler().get(0).getSubsets().get(i).getFacesArray();
           
            ssVolumes = ugxfile.getSubset_handler().get(0).getSubsets().get(i).getVolumeArray();
            
            ArrayList<MeshView> subsetMeshViewArray = new ArrayList<>();
            
            // start of vertex visualisation
            if (ugxfile.getSubset_handler().get(0).getSubsets().get(i).isHasVertices() && renderVertices) {
                PhongMaterial sphereMat = new PhongMaterial(new Color(ugxfile.getSubset_handler().get(0).getSubsets().get(i).getColor()[0],
                        ugxfile.getSubset_handler().get(0).getSubsets().get(i).getColor()[1],
                        ugxfile.getSubset_handler().get(0).getSubsets().get(i).getColor()[2],
                        Math.abs(ugxfile.getSubset_handler().get(0).getSubsets().get(i).getColor()[3])));

                if (highResolution) { //high resolution vertex visualization
                    Sphere[] sphereArray = new Sphere[ssVertices.length];
                    for (int j = 0; j < ssVertices.length; j++) {
                        sphereArray[j] = new Sphere(0.03*ugxfile.getLogBiggestDistanceBetweenVertices(), 4);

                        float x = vertices[ssVertices[j] * 3];
                        float y = vertices[ssVertices[j] * 3 + 1];
                        float z = vertices[ssVertices[j] * 3 + 2];

                        sphereArray[j].setTranslateX(x);
                        sphereArray[j].setTranslateY(y);
                        sphereArray[j].setTranslateZ(z);

                        sphereArray[j].setMaterial(sphereMat);

                        vertexGroup.getChildren().add(sphereArray[j]);
                        vertexMap.put(sphereArray[j], new float[]{x, y, z});
                    }
                    addVertexInteraction(vertexGroup);
                } else { //low resolution vertex visualization. All vertices are part of only one meshView

                    TriangleMesh vertexMesh = new TriangleMesh();
                    vertexMesh.getTexCoords().addAll(0, 0);

                    float width = (float) (0.02f * ugxfile.getLogBiggestDistanceBetweenVertices());

                    for (int j = 0; j < ssVertices.length; j++) {

                        float x = vertices[ssVertices[j] * 3];
                        float y = vertices[ssVertices[j] * 3 + 1];
                        float z = vertices[ssVertices[j] * 3 + 2];

                        float negX = x - width;
                        float posX = x + width;
                        float negY = y - width;
                        float posY = y + width;
                        float negZ = z - width;
                        float posZ = z + width;

                        float points[]
                                = {posX, posY, posZ,
                                    posX, posY, negZ,
                                    posX, negY, posZ,
                                    posX, negY, negZ,
                                    negX, posY, posZ,
                                    negX, posY, negZ,
                                    negX, negY, posZ,
                                    negX, negY, negZ,};

                        int faces[]
                                = {0 + j * 8, 0, 2 + j * 8, 0, 1 + j * 8, 0,
                                    2 + j * 8, 0, 3 + j * 8, 0, 1 + j * 8, 0,
                                    4 + j * 8, 0, 5 + j * 8, 0, 6 + j * 8, 0,
                                    6 + j * 8, 0, 5 + j * 8, 0, 7 + j * 8, 0,
                                    0 + j * 8, 0, 1 + j * 8, 0, 4 + j * 8, 0,
                                    4 + j * 8, 0, 1 + j * 8, 0, 5 + j * 8, 0,
                                    2 + j * 8, 0, 6 + j * 8, 0, 3 + j * 8, 0,
                                    3 + j * 8, 0, 6 + j * 8, 0, 7 + j * 8, 0,
                                    0 + j * 8, 0, 4 + j * 8, 0, 2 + j * 8, 0,
                                    2 + j * 8, 0, 4 + j * 8, 0, 6 + j * 8, 0,
                                    1 + j * 8, 0, 3 + j * 8, 0, 5 + j * 8, 0,
                                    5 + j * 8, 0, 3 + j * 8, 0, 7 + j * 8, 0};

                        vertexMesh.getPoints().addAll(points);
                        vertexMesh.getFaces().addAll(faces);

                    }
                    MeshView vertexMeshView = new MeshView(vertexMesh);
                    subsetMapMeshToSubset.put(vertexMeshView, ugxfile.getSubset_handler().get(0).getSubsets().get(i));
                    subsetMapSubsetToMesh.put(ugxfile.getSubset_handler().get(0).getSubsets().get(i), subsetMeshViewArray);
                    subsetMapSubsetToMesh.get(ugxfile.getSubset_handler().get(0).getSubsets().get(i)).add(vertexMeshView);
                    vertexMeshView.setMaterial(sphereMat);
                    vertexMeshView.setDrawMode(DrawMode.FILL);
                    vertexMeshView.setCullFace(CullFace.NONE);

                    vertexGroup.getChildren().addAll(vertexMeshView);

                    addLowResolutionInteraction(vertexMeshView);
                }

            }// end of vertex visualisation

            // start of edge visualisation
            if (ugxfile.getSubset_handler().get(0).getSubsets().get(i).isHasEdges() && renderEdges) {
                TriangleMesh edgesMesh = new TriangleMesh();
                edgesMesh.getTexCoords().addAll(0, 0);

                float width = (float) (0.01f * ugxfile.getLogBiggestDistanceBetweenVertices());

                PhongMaterial edgeMat = new PhongMaterial(new Color(ugxfile.getSubset_handler().get(0).getSubsets().get(i).getColor()[0],
                        ugxfile.getSubset_handler().get(0).getSubsets().get(i).getColor()[1],
                        ugxfile.getSubset_handler().get(0).getSubsets().get(i).getColor()[2],
                        Math.abs(ugxfile.getSubset_handler().get(0).getSubsets().get(i).getColor()[3])));

                if (highResolution) { //high resolution visualization of the edges.
                    //each edge is a stretched hexaederon

                    ArrayList<TriangleMesh> triMesh = new ArrayList<>();

                    MeshView[] meshViews = new MeshView[ssEdges.length];
                    for (int j = 0; j < ssEdges.length; j++) {
                        triMesh.add(new TriangleMesh());
                        triMesh.get(j).getTexCoords().addAll(0, 0);

                        float p1xneg = edges.get(ssEdges[j]).getVertices()[0].getX() - width;
                        float p1xpos = edges.get(ssEdges[j]).getVertices()[0].getX() + width;
                        float p1yneg = edges.get(ssEdges[j]).getVertices()[0].getY() - width;
                        float p1ypos = edges.get(ssEdges[j]).getVertices()[0].getY() + width;
                        float p1zneg = edges.get(ssEdges[j]).getVertices()[0].getZ() - width;
                        float p1zpos = edges.get(ssEdges[j]).getVertices()[0].getZ() + width;

                        float p2xneg = edges.get(ssEdges[j]).getVertices()[1].getX() - width;;
                        float p2xpos = edges.get(ssEdges[j]).getVertices()[1].getX() + width;;
                        float p2yneg = edges.get(ssEdges[j]).getVertices()[1].getY() - width;
                        float p2ypos = edges.get(ssEdges[j]).getVertices()[1].getY() + width;
                        float p2zneg = edges.get(ssEdges[j]).getVertices()[1].getZ() - width;;
                        float p2zpos = edges.get(ssEdges[j]).getVertices()[1].getZ() + width;;

                        float points[]
                                = {
                                    p1xpos, p1ypos, p1zpos, //1
                                    p1xpos, p1yneg, p1zpos, //2
                                    p1xneg, p1ypos, p1zneg, //3
                                    p1xneg, p1yneg, p1zneg, //4
                                    p2xpos, p2ypos, p2zpos, //5
                                    p2xpos, p2yneg, p2zpos, //6
                                    p2xneg, p2ypos, p2zneg, //7
                                    p2xneg, p2yneg, p2zneg, //8
                                };

                        int faces[]
                                = {0, 0, 2, 0, 1, 0,
                                    2, 0, 3, 0, 1, 0,
                                    4, 0, 5, 0, 6, 0,
                                    6, 0, 5, 0, 7, 0,
                                    0, 0, 1, 0, 4, 0,
                                    4, 0, 1, 0, 5, 0,
                                    2, 0, 6, 0, 3, 0,
                                    3, 0, 6, 0, 7, 0,
                                    0, 0, 4, 0, 2, 0,
                                    2, 0, 4, 0, 6, 0,
                                    1, 0, 3, 0, 5, 0,
                                    5, 0, 3, 0, 7, 0};

                        triMesh.get(j).getPoints().addAll(points);

                        triMesh.get(j).getFaces().addAll(faces);

                        if (doubleFacesOnEdges) {
                            int faces2[]
                                    = {0, 0, 1, 0, 2, 0,
                                        2, 0, 1, 0, 3, 0,
                                        4, 0, 6, 0, 5, 0,
                                        6, 0, 7, 0, 5, 0,
                                        0, 0, 4, 0, 1, 0,
                                        4, 0, 5, 0, 1, 0,
                                        2, 0, 3, 0, 6, 0,
                                        3, 0, 7, 0, 6, 0,
                                        0, 0, 2, 0, 4, 0,
                                        2, 0, 6, 0, 4, 0,
                                        1, 0, 5, 0, 3, 0,
                                        5, 0, 7, 0, 3, 0};
                            triMesh.get(j).getFaces().addAll(faces2);
                        }

                        meshViews[j] = new MeshView(triMesh.get(j));
                        meshViews[j].setMaterial(edgeMat);
                        meshViews[j].setCullFace(CullFace.BACK);

                        edgesMap.put(meshViews[j], edges.get(ssEdges[j]));

                    }

                    edgesGroup.getChildren().addAll(meshViews);
                    addEdgeInteraction(edgesGroup);

                } else { //low resolution edge visualization

                    for (int j = 0; j < ssEdges.length; j++) {

                        float p1xneg = edges.get(ssEdges[j]).getVertices()[0].getX() - width;
                        float p1xpos = edges.get(ssEdges[j]).getVertices()[0].getX() + width;
                        float p1yneg = edges.get(ssEdges[j]).getVertices()[0].getY() - width;
                        float p1ypos = edges.get(ssEdges[j]).getVertices()[0].getY() + width;
                        float p1zneg = edges.get(ssEdges[j]).getVertices()[0].getZ() - width;
                        float p1zpos = edges.get(ssEdges[j]).getVertices()[0].getZ() + width;

                        float p2xneg = edges.get(ssEdges[j]).getVertices()[1].getX() - width;;
                        float p2xpos = edges.get(ssEdges[j]).getVertices()[1].getX() + width;;
                        float p2yneg = edges.get(ssEdges[j]).getVertices()[1].getY() - width;
                        float p2ypos = edges.get(ssEdges[j]).getVertices()[1].getY() + width;
                        float p2zneg = edges.get(ssEdges[j]).getVertices()[1].getZ() - width;;
                        float p2zpos = edges.get(ssEdges[j]).getVertices()[1].getZ() + width;;

                        float points[]
                                = {
                                    p1xpos, p1ypos, p1zpos, //1
                                    p1xpos, p1yneg, p1zpos, //2
                                    p1xneg, p1ypos, p1zneg, //3
                                    p1xneg, p1yneg, p1zneg, //4
                                    p2xpos, p2ypos, p2zpos, //5
                                    p2xpos, p2yneg, p2zpos, //6
                                    p2xneg, p2ypos, p2zneg, //7
                                    p2xneg, p2yneg, p2zneg, //8
                                };

                        int faces[]
                                = {0 + j * 8, 0, 2 + j * 8, 0, 1 + j * 8, 0,
                                    2 + j * 8, 0, 3 + j * 8, 0, 1 + j * 8, 0,
                                    4 + j * 8, 0, 5 + j * 8, 0, 6 + j * 8, 0,
                                    6 + j * 8, 0, 5 + j * 8, 0, 7 + j * 8, 0,
                                    0 + j * 8, 0, 1 + j * 8, 0, 4 + j * 8, 0,
                                    4 + j * 8, 0, 1 + j * 8, 0, 5 + j * 8, 0,
                                    2 + j * 8, 0, 6 + j * 8, 0, 3 + j * 8, 0,
                                    3 + j * 8, 0, 6 + j * 8, 0, 7 + j * 8, 0,
                                    0 + j * 8, 0, 4 + j * 8, 0, 2 + j * 8, 0,
                                    2 + j * 8, 0, 4 + j * 8, 0, 6 + j * 8, 0,
                                    1 + j * 8, 0, 3 + j * 8, 0, 5 + j * 8, 0,
                                    5 + j * 8, 0, 3 + j * 8, 0, 7 + j * 8, 0};

                        edgesMesh.getPoints().addAll(points);
                        edgesMesh.getFaces().addAll(faces);

                        if (doubleFacesOnEdges) {
                            int faces2[]
                                    = {0 + j * 8, 0, 1 + j * 8, 0, 2 + j * 8, 0,
                                        2 + j * 8, 0, 1 + j * 8, 0, 3 + j * 8, 0,
                                        4 + j * 8, 0, 6 + j * 8, 0, 5 + j * 8, 0,
                                        6 + j * 8, 0, 7 + j * 8, 0, 5 + j * 8, 0,
                                        0 + j * 8, 0, 4 + j * 8, 0, 1 + j * 8, 0,
                                        4 + j * 8, 0, 5 + j * 8, 0, 1 + j * 8, 0,
                                        2 + j * 8, 0, 3 + j * 8, 0, 6 + j * 8, 0,
                                        3 + j * 8, 0, 7 + j * 8, 0, 6 + j * 8, 0,
                                        0 + j * 8, 0, 2 + j * 8, 0, 4 + j * 8, 0,
                                        2 + j * 8, 0, 6 + j * 8, 0, 4 + j * 8, 0,
                                        1 + j * 8, 0, 5 + j * 8, 0, 3 + j * 8, 0,
                                        5 + j * 8, 0, 7 + j * 8, 0, 3 + j * 8, 0};

                            edgesMesh.getFaces().addAll(faces2);
                        }
                    }
                    MeshView edgesMeshView = new MeshView(edgesMesh);
                    subsetMapMeshToSubset.put(edgesMeshView, ugxfile.getSubset_handler().get(0).getSubsets().get(i));
                    subsetMapSubsetToMesh.put(ugxfile.getSubset_handler().get(0).getSubsets().get(i), subsetMeshViewArray);
                    subsetMapSubsetToMesh.get(ugxfile.getSubset_handler().get(0).getSubsets().get(i)).add(edgesMeshView);
                    edgesMeshView.setMaterial(edgeMat);
                    edgesMeshView.setDrawMode(DrawMode.FILL);
                    edgesMeshView.setCullFace(CullFace.BACK);

                    edgesGroup.getChildren().addAll(edgesMeshView);

                    addLowResolutionInteraction(edgesMeshView);
                    //addEdgeInteraction(edgesGroup);
                }

            } // end of edge visualisation
            
            // start of face visualisation
            if (ugxfile.getSubset_handler().get(0).getSubsets().get(i).isHasFaces() && renderFaces) {
                if(highResolution){
                    //each face has to be in a mesh view, so it can be indentified by clicking the mesh view
                    ArrayList<MeshView> quadriMeshList = new ArrayList<>();
                ArrayList<MeshView> triangleMeshList = new ArrayList<>();
                ArrayList<TriangleMesh> triMesh = new ArrayList<>();
                PhongMaterial faceMat = new PhongMaterial(new Color(ugxfile.getSubset_handler().get(0).getSubsets().get(i).getColor()[0],
                                                                ugxfile.getSubset_handler().get(0).getSubsets().get(i).getColor()[1],
                                                                ugxfile.getSubset_handler().get(0).getSubsets().get(i).getColor()[2],
                                                                Math.abs(ugxfile.getSubset_handler().get(0).getSubsets().get(i).getColor()[3])));
                for (int j = 0; j < ssFaces.length; j++) {
                    
                    float[] texCoords = {0,0};
                    triMesh = new ArrayList<>();
                    if (geometry2DList.get(ssFaces[j]).getClass().equals(Quadrilateral.class)) {
                        //the current face is part of a quadrilateral
 
                        TriangleMesh mesh1 = new TriangleMesh();
                        // first triangle of the quadrilateral
                        mesh1.getPoints().addAll(globalVertexList.get(geometry2DList.get(ssFaces[j]).getNodes()[0]*3),
                                                 globalVertexList.get((geometry2DList.get(ssFaces[j]).getNodes()[0]*3+1)),
                                                 globalVertexList.get((geometry2DList.get(ssFaces[j]).getNodes()[0]*3+2)));
                        mesh1.getPoints().addAll(globalVertexList.get(geometry2DList.get(ssFaces[j]).getNodes()[1]*3),
                                                 globalVertexList.get((geometry2DList.get(ssFaces[j]).getNodes()[1]*3+1)),
                                                 globalVertexList.get((geometry2DList.get(ssFaces[j]).getNodes()[1]*3+2)));
                        mesh1.getPoints().addAll(globalVertexList.get(geometry2DList.get(ssFaces[j]).getNodes()[2]*3),
                                                 globalVertexList.get((geometry2DList.get(ssFaces[j]).getNodes()[2]*3+1)),
                                                 globalVertexList.get((geometry2DList.get(ssFaces[j]).getNodes()[2]*3+2)));

                        
                        mesh1.getTexCoords().addAll(texCoords);
                        mesh1.getFaces().addAll(2, 0, 0, 0, 1, 0);

                        triMesh.add(mesh1);

                        TriangleMesh mesh2 = new TriangleMesh();
                        // second triangle of the quadrilateral
                        mesh2.getPoints().addAll(globalVertexList.get(geometry2DList.get(ssFaces[j]).getNodes()[0] * 3),
                                globalVertexList.get((geometry2DList.get(ssFaces[j]).getNodes()[0] * 3 + 1)),
                                globalVertexList.get((geometry2DList.get(ssFaces[j]).getNodes()[0] * 3 + 2)));
                        mesh2.getPoints().addAll(globalVertexList.get(geometry2DList.get(ssFaces[j]).getNodes()[2] * 3),
                                globalVertexList.get((geometry2DList.get(ssFaces[j]).getNodes()[2] * 3 + 1)),
                                globalVertexList.get((geometry2DList.get(ssFaces[j]).getNodes()[2] * 3 + 2)));
                        mesh2.getPoints().addAll(globalVertexList.get(geometry2DList.get(ssFaces[j]).getNodes()[3] * 3),
                                globalVertexList.get((geometry2DList.get(ssFaces[j]).getNodes()[3] * 3 + 1)),
                                globalVertexList.get((geometry2DList.get(ssFaces[j]).getNodes()[3] * 3 + 2)));

                        mesh2.getTexCoords().addAll(texCoords);
                        mesh2.getFaces().addAll(1, 0, 2, 0, 0, 0);

                        triMesh.add(mesh2);

                        MeshView mv1 = new MeshView(mesh1);
                        MeshView mv2 = new MeshView(mesh2);

                        quadriMeshList.add(mv1);
                        quadriMeshList.add(mv2);

                        newFaceMap.put(mv1, geometry2DList.get(ssFaces[j]));
                        newFaceMap.put(mv2, geometry2DList.get(ssFaces[j]));

                    } else {

                        TriangleMesh mesh1 = new TriangleMesh();

                        mesh1.getPoints().addAll(globalVertexList.get(geometry2DList.get(ssFaces[j]).getNodes()[0] * 3),
                                globalVertexList.get((geometry2DList.get(ssFaces[j]).getNodes()[0] * 3 + 1)),
                                globalVertexList.get((geometry2DList.get(ssFaces[j]).getNodes()[0] * 3 + 2)));
                        mesh1.getPoints().addAll(globalVertexList.get(geometry2DList.get(ssFaces[j]).getNodes()[1] * 3),
                                globalVertexList.get((geometry2DList.get(ssFaces[j]).getNodes()[1] * 3 + 1)),
                                globalVertexList.get((geometry2DList.get(ssFaces[j]).getNodes()[1] * 3 + 2)));
                        mesh1.getPoints().addAll(globalVertexList.get(geometry2DList.get(ssFaces[j]).getNodes()[2] * 3),
                                globalVertexList.get((geometry2DList.get(ssFaces[j]).getNodes()[2] * 3 + 1)),
                                globalVertexList.get((geometry2DList.get(ssFaces[j]).getNodes()[2] * 3 + 2)));
                        mesh1.getTexCoords().addAll(texCoords);
                        mesh1.getFaces().addAll(0, 0, 1, 0, 2, 0);

                        triMesh.add(mesh1);

                        MeshView mv1 = new MeshView(mesh1);
                        triangleMeshList.add(mv1);
                        newFaceMap.put(mv1, geometry2DList.get(ssFaces[j]));

                    }

                }

                MeshView[] faceMeshViewArray = new MeshView[triangleMeshList.size()+quadriMeshList.size()];
                
                for (int j = 0; j < triangleMeshList.size(); j++) {
                    faceMeshViewArray[j] = triangleMeshList.get(j);
                    faceMeshViewArray[j].setMaterial(faceMat);
                    faceMeshViewArray[j].setCullFace(CullFace.NONE);
                    
                }
                for (int j = triangleMeshList.size(), k = 0; k < quadriMeshList.size(); j++,k++) {
                    faceMeshViewArray[j] = quadriMeshList.get(k);
                    faceMeshViewArray[j].setMaterial(faceMat);
                    faceMeshViewArray[j].setCullFace(CullFace.NONE);
                    
                }

                faceGroup.getChildren().addAll(faceMeshViewArray);
                addFaceInteraction(faceGroup);
                    
                }else{
                    for (int j = 0; j < ssFaces.length; j++) {
                        meshArray[i].getFaces().addAll(geometry2DList.get(ssFaces[j]).getFacesArray());
                    }
                }
            } // end of face visualisation
            
            // start of volume visualisation
            if (ugxfile.getSubset_handler().get(0).getSubsets().get(i).isHasVolumes() && renderVolumes){
                
                // TODO: IMPLEMENT HIGH RESOLUTION VOLUME RENDERING
                for (int j = 0; j < ssVolumes.length; j++) {
                    meshArray[i].getFaces().addAll(geometry3DList.get(ssVolumes[j]).getFacesArray());
                }
            }// end of volume visualisation
                

            meshViewArray[i] = new MeshView(meshArray[i]);
            if (highResolution) {
                addVolumeInteraction(meshViewArray[i]);
            }
            
            subsetMapMeshToSubset.put(meshViewArray[i], ugxfile.getSubset_handler().get(0).getSubsets().get(i));
            subsetMapSubsetToMesh.put(ugxfile.getSubset_handler().get(0).getSubsets().get(i), subsetMeshViewArray);
            subsetMapSubsetToMesh.get(ugxfile.getSubset_handler().get(0).getSubsets().get(i)).add(meshViewArray[i]);

          
            if (highResolution) {
                if (ugxfile.getSubset_handler().get(0).getSubsets().get(i).isHasVertices() && ugxfile.getSubset_handler().get(0).getSubsets().get(i).isHasEdges()) {
                    Group fusedGroup = new Group(vertexGroup, edgesGroup, meshViewArray[i], faceGroup);
                    subsetGroup.getChildren().addAll(fusedGroup);
                    
                } else if (ugxfile.getSubset_handler().get(0).getSubsets().get(i).isHasVertices()) {
                    Group fusedGroup = new Group(vertexGroup, meshViewArray[i], faceGroup);
                    subsetGroup.getChildren().addAll(fusedGroup);
                    
                } else if (ugxfile.getSubset_handler().get(0).getSubsets().get(i).isHasEdges()) {
                    Group fusedGroup = new Group(edgesGroup, meshViewArray[i], faceGroup);
                    subsetGroup.getChildren().addAll(fusedGroup);
                    
                } else {
                    Group fusedGroup = new Group(meshViewArray[i], faceGroup);
                    subsetGroup.getChildren().add(fusedGroup);
                }
            } else {
                Group lowResGroup = new Group(vertexGroup,edgesGroup,meshViewArray[i]);
                addLowResolutionInteraction(meshViewArray[i]);
 
                subsetGroup.getChildren().add(lowResGroup);
            }
        }
        
        
           // setting color for each each subset
            for (int i = 0; i < ssNumber; i++) {
                
                Color ssColor = new Color(ugxfile.getSubset_handler().get(0).getSubsets().get(i).getColor()[0],
                ugxfile.getSubset_handler().get(0).getSubsets().get(i).getColor()[1],
                ugxfile.getSubset_handler().get(0).getSubsets().get(i).getColor()[2],
                Math.abs(ugxfile.getSubset_handler().get(0).getSubsets().get(i).getColor()[3]));
                
                PhongMaterial material = new PhongMaterial(ssColor);
                meshViewArray[i].setMaterial(material);
    
        }
        
        if(renderFaces) { 
            for (int i = 0; i < ssNumber; i++) {
                meshViewArray[i].setDrawMode(DrawMode.FILL);
            }
            
        } else {
            for (int i = 0; i < ssNumber; i++) {
                meshViewArray[i].setDrawMode(DrawMode.LINE);
            }
        }

        for (int i = 0; i < ssNumber; i++) {
                meshViewArray[i].setCullFace(CullFace.NONE);
            }
        
        return subsetGroup;
    }
   
    /**Adds the high resolution vertex interaction to a group, that consists of sphere objects.
     * Do not use this on the low resolution model.
     * @param vGroup the group of spheres that should be selectable 
     **/
    private void addVertexInteraction(Group vGroup) {

        vGroup.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseClick -> {

            if (mouseClick.getButton().toString().matches("SECONDARY")) {
                Node pickRes = mouseClick.getPickResult().getIntersectedNode();

                handleSelection(vertexNodeSelection, vertexNodeSelectionMaterial, pickRes, new Sphere());
            }


        }
        );
    }
    
    /**Adds the high resolution edge interaction to a group, that consists of meshView objects.
     * Do not use this on the low resolution model.
     * @param eGroup the group of meshViews (edges/hexahedrons) that should be selectable 
     **/
    private void addEdgeInteraction(Group eGroup) {
        eGroup.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseClick -> {
            if (mouseClick.getButton().toString().matches("SECONDARY")) {
                
                Node pickRes = mouseClick.getPickResult().getIntersectedNode();

                handleSelection(edgeNodeSelection, edgeNodeSelectionMaterial, pickRes, new MeshView());
            }

        }
        );

    }
    


    /**Adds the high resolution face interaction to a group, that consists of meshView objects.
     * Do not use this on the low resolution model.
     * @param faceMeshView the group of meshViews (faces) that should be selectable 
     **/
        private void addFaceInteraction(Group faceMeshView) {

        faceMeshView.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseClick -> {

            if (mouseClick.getButton().toString().matches("SECONDARY")) {

                Node pickedNode = mouseClick.getPickResult().getIntersectedNode();
                PickResult res = mouseClick.getPickResult();

                if (debugMode) {
                    System.out.println(newFaceMap.get(pickedNode));
                }
                handleSelection(faceNodeSelection, faceNodeSelectionMaterial, pickedNode, new MeshView());
            }

        });

    }
        
        
     /**Adds the high resolution volume interaction to a group, that consists of meshView objects.
     * Do not use this on the low resolution model.
     * @param volumeMeshView the group of meshViews (volumes) that should be selectable 
     **/
        private void addVolumeInteraction(MeshView volumeMeshView) {

        volumeMeshView.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseClick -> {

            if (mouseClick.getButton().toString().matches("SECONDARY")) {

                Node pickedNode = mouseClick.getPickResult().getIntersectedNode();
                PickResult res = mouseClick.getPickResult();

                if (debugMode) {
                    System.out.println("Selected node is a volume!");
                    System.out.println(newFaceMap.get(pickedNode));
                }
                handleSelection(faceNodeSelection, faceNodeSelectionMaterial, pickedNode, new MeshView());
            }

        });

    }
        /**Adds the low resolution interaction to the low resolution model. 
         * Do not use this on the high resolution model.
         * @param shape the node (spheres,MeshViews) that should be selectable
         **/
        private void addLowResolutionInteraction(Shape3D shape){
            
            shape.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseClick -> {

            if (mouseClick.getButton().toString().matches("SECONDARY")) {

                Node pickedNode = mouseClick.getPickResult().getIntersectedNode();

                if (debugMode) {
                    System.out.println("CLICKED NODE "+pickedNode);
                }

                //handleSelection(subsetNodeSelection, subsetNodeSelectionMaterial, pickedNode, new MeshView());
                handleLowResolutionSelection(pickedNode);
            }

        });
        }
        
        /**Handles the selection for the low resolution model.
         * @param pickResult the node that was clicked by the user
         **/
        private void handleLowResolutionSelection(Node pickResult){
            if (!subsetNodeSelection.contains(pickResult)) { 
                if (!Main.ctrlPressedDown) { //ctrl not pressed and selected node was not selected before -> remove all nodes
                    
                    for (int i = 0; i < subsetNodeSelection.size(); i++) {
                        
                        ((MeshView)subsetNodeSelection.get(i)).setMaterial(subsetNodeSelectionMaterial.get(i));
                        
                    }
                    subsetNodeSelection.clear();
                    subsetNodeSelectionMaterial.clear();
                    
                }
                // add new node to the selection
                
                Color col = new Color(1, 1, 1, 1);
                PhongMaterial mat = new PhongMaterial(col);
                
                UGXsubset pickedSubset = subsetMapMeshToSubset.get(pickResult);
                
                for (int i = 0; i < subsetMapSubsetToMesh.get(pickedSubset).size(); i++) {
                    
                    subsetNodeSelection.add(subsetMapSubsetToMesh.get(pickedSubset).get(i));
                    subsetNodeSelectionMaterial.add(subsetMapSubsetToMesh.get(pickedSubset).get(i).getMaterial());
                    subsetMapSubsetToMesh.get(pickedSubset).get(i).setMaterial(mat);
                }

            }else{ //selected node is already in the selection
                
                if (Main.ctrlPressedDown) { //ctrl is pressed, the selected node was already selected before, so we only remove this node from the selection
                    
                    UGXsubset pickedSubset = subsetMapMeshToSubset.get(pickResult);
                    
                    for (int i = 0; i < subsetMapSubsetToMesh.get(pickedSubset).size(); i++) {
                        int index = subsetNodeSelection.indexOf(subsetMapSubsetToMesh.get(pickedSubset).get(i));
                        subsetMapSubsetToMesh.get(pickedSubset).get(i).setMaterial(subsetNodeSelectionMaterial.get(index));
                        
                    }
                    for (int i = 0; i < subsetMapSubsetToMesh.get(pickedSubset).size(); i++) {
                        subsetNodeSelection.remove(subsetMapSubsetToMesh.get(pickedSubset).get(i));
                        subsetNodeSelectionMaterial.remove(subsetMapSubsetToMesh.get(pickedSubset).get(i).getMaterial());
                    }
                    
                }else{ // ctrl is not pressed, the selected node was already selected before. we remove the whole selection
                    UGXsubset pickedSubset = subsetMapMeshToSubset.get(pickResult);
                    for (int i = 0; i < subsetMapSubsetToMesh.get(pickedSubset).size(); i++) {
                        int index = subsetNodeSelection.indexOf(subsetMapSubsetToMesh.get(pickedSubset).get(i));
                        subsetMapSubsetToMesh.get(pickedSubset).get(i).setMaterial(subsetNodeSelectionMaterial.get(index));  
                    }
                    subsetNodeSelection.clear();
                    subsetNodeSelectionMaterial.clear();
                    
                }
            }
            if (debugMode) {
                System.out.print("\nClicked Subset: ");
                System.out.println(subsetMapMeshToSubset.get(pickResult).getSubsetName());
                UGXsubset testss = subsetMapMeshToSubset.get(pickResult);
                
                System.out.println("Selected subset is split in the following MeshViews " + subsetMapSubsetToMesh.get(testss));
            }
        }
    
        /**Handles the selection for the high resolution model.
         * @param nodeList the arrayList of nodes that stores the selected elements (e.g. a list of Spheres for vertices or a list of meshViews for edges/faces/volumes)
         * @param materialList the arrayList of materials, that each node has. It is saved so it can be restored later, when user deselected the node
         * @param pickResult the node, that was selected by the user
         * @param geometryType the type of the shape, that was selected by the user(e.g. Sphere for vertices or MeshView for edges/faces/volumes)
         **/
    private void handleSelection(ArrayList<Node> nodeList, ArrayList<Material> materialList, Node pickResult, Shape3D geometryType) {

        if (!nodeList.contains(pickResult)) {

            if (!Main.ctrlPressedDown) { // ctrl not pressed and selected node was not selected before
                // remove all nodes from the selection

                    for (int i = 0; i < vertexNodeSelection.size(); i++) {
                        
                        ((Sphere) vertexNodeSelection.get(i)).setMaterial(vertexNodeSelectionMaterial.get(i));
                    }
                    vertexNodeSelection.clear();
                    vertexNodeSelectionMaterial.clear();
                    
                    for (int i = 0; i < edgeNodeSelection.size(); i++) {
                        
                        ((MeshView) edgeNodeSelection.get(i)).setMaterial(edgeNodeSelectionMaterial.get(i));
                    }
                    edgeNodeSelection.clear();
                    edgeNodeSelectionMaterial.clear();
                    
                    for (int i = 0; i < faceNodeSelection.size(); i++) {
                        
                        ((MeshView) faceNodeSelection.get(i)).setMaterial(faceNodeSelectionMaterial.get(i));
                        
                    }
                    faceNodeSelection.clear();
                    faceNodeSelectionMaterial.clear();
              

            }
            // ctrl is pressed down and the selected node will be added to the selection
            Color col = new Color(1, 1, 1, 1);
            PhongMaterial mat = new PhongMaterial(col);
            
                nodeList.add(pickResult);
                materialList.add((geometryType.getClass().cast(pickResult)).getMaterial());
                geometryType.getClass().cast(pickResult).setMaterial(mat);
            
        } else {
            // node was already in the selection, remove it from the selection
            // ctrl is pressed down, so we only remove the selected node and not the whole selection

            if (Main.ctrlPressedDown) {
                int index = nodeList.indexOf(pickResult);
                    geometryType.getClass().cast(nodeList.get(index)).setMaterial(materialList.get(index));
                    nodeList.remove(index);
                    materialList.remove(index);
              
            }
            if (!Main.ctrlPressedDown) { // ctrl not pressed, so we remove the whole selection

                    for (int i = 0; i < vertexNodeSelection.size(); i++) {
                        
                        ((Sphere) vertexNodeSelection.get(i)).setMaterial(vertexNodeSelectionMaterial.get(i));
                    }
                    vertexNodeSelection.clear();
                    vertexNodeSelectionMaterial.clear();
                    
                    for (int i = 0; i < edgeNodeSelection.size(); i++) {
                        
                        ((MeshView) edgeNodeSelection.get(i)).setMaterial(edgeNodeSelectionMaterial.get(i));
                    }
                    edgeNodeSelection.clear();
                    edgeNodeSelectionMaterial.clear();
                    
                    for (int i = 0; i < faceNodeSelection.size(); i++) {
                        
                        ((MeshView) faceNodeSelection.get(i)).setMaterial(faceNodeSelectionMaterial.get(i));
                        
                    }
                    faceNodeSelection.clear();
                    faceNodeSelectionMaterial.clear();
              
            }

        }
           if (debugMode) {
            System.out.println("Elements in selection :");
            for (int i = 0; i < nodeList.size(); i++) {
                System.out.println(nodeList.get(i) + "  ");
                
                if (vertexMap.containsKey(pickResult)) {
                    float[] resultV = vertexMap.get(vertexNodeSelection.get(i));
                    System.out.println(resultV[0] + " " + resultV[1] + " " + resultV[2]);
                } else if (edgesMap.containsKey(pickResult)) {
                    System.out.println(edgesMap.get(edgeNodeSelection.get(i)).toString());
                } else if (newFaceMap.containsKey(pickResult)) {                    
                    System.out.println(newFaceMap.get(faceNodeSelection.get(i)).getCoordinatesOfPoints(globalVertexList));                    
                }
            }
            System.out.println("\nAll selected nodes: " + vertexNodeSelection.size() + " vertices. "
                    + edgeNodeSelection.size() + " edges. " + faceNodeSelection.size() + " faces.");
        }

    }

    /** Returns the points of the vertices as a float array, as it was saved in the ugx file.
     * @return array of all vertices in the ugx file
     **/
    public float[] getVerticesFloatArray(){
        float[] verticesArray = new float[vertices.size()];
        for (int i = 0; i < vertices.size(); i++) {
            verticesArray[i] = vertices.get(i);
        }
        return verticesArray;
    }
    
        public int[] getTrianglesFloatArray(){
        int[] normalsArray = new int[triangles.size()];
        for (int i = 0; i < triangles.size(); i++) {
            normalsArray[i] = triangles.get(i);
        }
        return normalsArray;
    }
        
        
            public int[] getTetraFloatArray(){
        int[] tetraArray = new int[tetra.size()];
        for (int i = 0; i < tetra.size(); i++) {
            tetraArray[i] = tetra.get(i);
        }
        return tetraArray;
    }
              
        
        public ArrayList<UGXsubset> getSubsetList(){
            return subsetList;
        }
        
        public float[] getSubsetVerticesFloatArray(int i){
            int vertexNum = subsetList.get(i).getVertices().size();
            float[] ssVertices = new float[vertexNum];
            for (int j = 0; j < vertexNum ; j++) {
                ssVertices[j] = subsetList.get(i).getVertices().get(j);
            }
            
            return ssVertices;
        }
        
        public int getNumberOfSubsets(){
            return subsetHandler.getSubsets().size();
        }
    
    
        public String[] getSubssetNameArray(){
            String[] ssNameArray = new String[subsetHandler.getSubsets().size()];
            
            for (int i = 0; i < subsetHandler.getSubsets().size(); i++) {
                ssNameArray[i] = subsetHandler.getSubsets().get(i).getSubsetName();
            }
            return ssNameArray;
        }
        
        /**Sets the resolution of the rendered objects and determines the level of interaction that is available.
         * High resolution enables a high level on interaction in which you can select individual vertices/edges/faces but takes longer to load
         * on big files. This mode is suggested for smaller geometries.
         * Low resolution is faster but limits the selectable objects to whole subsets only. This mode is suggested for large geometries.
         * Default value : fals
     * @param res*/
        public void setFlagHighResolution(boolean res){
            highResolution = res;
        }
        
        /**Draws the faces for the edges a second time in reversed winding order to make sure only the front sides will be shown.
         * Default value : true
     * @param bool
         */
        public void setFlagDoubleFacesOnEdges(boolean bool){
            doubleFacesOnEdges = bool;
        }
        /**Determines if the whole geometry will be rendered (including faces) or just vertices and edges.
         * Default value : true
     * @param rfaces
        */
        public void setFlagRenderFaces(boolean rfaces){
            renderFaces = rfaces;
        }
        
        public void setFlagDebugMode(boolean debug){
            debugMode = debug;
        }
        
        public void setFlagRenderedElements(boolean showVertices,boolean showEdges,boolean showFaces,boolean showVolumes){
            renderVertices = showVertices;
            renderEdges = showEdges;
            renderFaces = showFaces;
            renderVolumes = showVolumes;
        }
        
}
