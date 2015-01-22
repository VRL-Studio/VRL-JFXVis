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
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.AmbientLight;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.CullFace;
import javafx.scene.shape.DrawMode;
import javafx.scene.shape.MeshView;
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
    *reads the content of a .ugx file using the xStream library 
    * and stores them
    **/
    public UGXfile xread(){
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
    
    /*
    old read function, using a self made algorithm to read ugx files.
    does not support all geometries
    -do not use this function-
    */
    private void read(){
        String[] inputLine;
        
        String ssName = "NoName";
        double[] ssColor = new double[4];
        int ssState = -1;
        ArrayList<Integer> ssVertices = new ArrayList<Integer>();
        ArrayList<Integer> ssEdges = new ArrayList<Integer>();
        ArrayList<Integer> ssFaces = new ArrayList<Integer>();
        ArrayList<Integer> ssVolumes = new ArrayList<Integer>();
        boolean foundSubset = false;
        
        
        try {
        File file = new File(path);
       
        Scanner scanner = new Scanner(file);
        try {
            while(scanner.hasNextLine()) {

                inputLine = scanner.nextLine().trim().split(" ");
                for (int i = 0; i < inputLine.length; i++) {
                        //System.out.println(inputLine[i] );
                    }
               
                if ( inputLine[0].equalsIgnoreCase("<vertices")) { 
                    System.out.println("VERTICES");
                    
                    inputLine[1]= inputLine[1].split(">")[1];
                    inputLine[inputLine.length-1]= inputLine[inputLine.length-1].split("<")[0];
                   
                    for (int i = 1; i < inputLine.length; i++) {
                        vertices.add(Float.parseFloat(inputLine[i]));
                    }

                }
                if ( inputLine[0].contains("triangles")) {   
                    System.out.println("TRIANGLES");
                    inputLine[0]= inputLine[0].split(">")[1];
                    inputLine[inputLine.length-1]= inputLine[inputLine.length-1].split("<")[0];
                   
                    for (int i = 0; i < inputLine.length; i++) {
                        triangles.add(Integer.parseInt(inputLine[i]));
                    }
               
                }
                
                if ( inputLine[0].contains("tetrahedrons")) {   
                    System.out.println("tetrahedrons");
                    inputLine[0]= inputLine[0].split(">")[1];
                    inputLine[inputLine.length-1]= inputLine[inputLine.length-1].split("<")[0];
                   
                    for (int i = 0; i < inputLine.length; i++) {
                        tetra.add(Integer.parseInt(inputLine[i]));
                    }
                
                }
                
                
                if (inputLine[0].equalsIgnoreCase("<subset") && inputLine[1].contains("name")) {
                    
                    
                    ssName = inputLine[1].split("\"")[1];
                    ssColor[0] = Double.parseDouble(inputLine[2].split("\"")[1]);
                    ssColor[1] = Double.parseDouble(inputLine[3]);
                    ssColor[2] = Double.parseDouble(inputLine[4]);
                    ssColor[3] = Double.parseDouble(inputLine[5].split("\"")[0]);
                    //ssState = Integer.parseInt(inputLine[6].split("\"")[1]);
                    foundSubset = true;
                }
                if (foundSubset && inputLine[0].contains("vertices")) {
                    if (inputLine.length == 1) {
                        ssVertices.add(Integer.parseInt(inputLine[0].substring(10, inputLine[0].length()-11))); //</vertices>
                    }else{
                        ssVertices.add(Integer.parseInt(inputLine[0].split(">")[1]));
                        for (int i = 1; i < inputLine.length - 1; i++) {
                            ssVertices.add(Integer.parseInt(inputLine[i]));
                        }
                        ssVertices.add(Integer.parseInt(inputLine[inputLine.length - 1].split("<")[0]));
                       
                    }
                }
                
                if (foundSubset && inputLine[0].contains("edges")) {
                    if (inputLine.length == 1) {
                        ssEdges.add(Integer.parseInt(inputLine[0].substring(7, inputLine[0].length()-8)));
                    }else{
                        ssEdges.add(Integer.parseInt(inputLine[0].split(">")[1]));
                        for (int i = 1; i < inputLine.length - 1; i++) {
                            ssEdges.add(Integer.parseInt(inputLine[i]));
                        }
                        ssEdges.add(Integer.parseInt(inputLine[inputLine.length - 1].split("<")[0]));
                       
                    }
                    
                }
                
                if (foundSubset && inputLine[0].contains("faces")) {
                    if (inputLine.length == 1) {
                        ssFaces.add(Integer.parseInt(inputLine[0].substring(7, inputLine[0].length()-8)));
                    }else{
                        ssFaces.add(Integer.parseInt(inputLine[0].split(">")[1]));
                        for (int i = 1; i < inputLine.length - 1; i++) {
                            ssFaces.add(Integer.parseInt(inputLine[i]));
                        }
                        ssFaces.add(Integer.parseInt(inputLine[inputLine.length - 1].split("<")[0]));
                       
                    }
                }
                
                if (foundSubset && inputLine[0].contains("volumes")) {
                    if (inputLine.length == 1) {
                        ssVolumes.add(Integer.parseInt(inputLine[0].substring(9, inputLine[0].length()-10)));
                    }else{
                        ssVolumes.add(Integer.parseInt(inputLine[0].split(">")[1]));
                        for (int i = 1; i < inputLine.length - 1; i++) {
                            ssVolumes.add(Integer.parseInt(inputLine[i]));
                        }
                        ssVolumes.add(Integer.parseInt(inputLine[inputLine.length - 1].split("<")[0]));
                       
                    }
                }
                
                
                ArrayList<Integer> ssVerticesClone = new ArrayList<>(ssVertices);
                ArrayList<Integer> ssEdgesClone = new ArrayList<>(ssEdges);
                ArrayList<Integer> ssFacesClone = new ArrayList<>(ssFaces);
                ArrayList<Integer> ssVolumesClone = new ArrayList<>(ssVolumes);
                
                if (foundSubset && inputLine[0].equalsIgnoreCase("</subset>")) {
                    subsetList.add(new UGXsubset(ssName, ssColor, ssState, ssVerticesClone, ssEdgesClone, ssFacesClone, ssVolumesClone));
                    
                    ssName = "NoName";
                    ssColor = new double[]{1.0,1.0,1.0,1.0};
                    ssState = -1;
                    ssVertices.clear();
                    ssEdges.clear();
                    ssFaces.clear();
                    ssVolumes.clear();
                    foundSubset = false;
                    
                }
                
                
                
            }
            
        } finally {
            scanner.close();  
            for (int i = 0; i < subsetList.size(); i++) {
                System.out.print(subsetList.get(i).getSubsetName().toString() + ": ");
                System.out.print(subsetList.get(i).getVertices().size() + " vertices," +
                        subsetList.get(i).getEdges().size() + " edges," + subsetList.get(i).getFaces().size() + " faces," +
                        subsetList.get(i).getVolumes().size() + " volumes," +  " state," + 
                        subsetList.get(i).getColor()[0] + " " + subsetList.get(i).getColor()[1] + " " +
                        subsetList.get(i).getColor()[2] + " " + subsetList.get(i).getColor()[3] + " " + "color");
                System.out.println(" ");
            }
            
        }
        
    }   catch (FileNotFoundException ex) {
        ex.printStackTrace(System.out);
    } 
        
    }
    
    
    /**Creates a 3D object from a ugx file using the xStream library **/
    public Group xbuildUGX (boolean ambient, boolean fill){
        
        UGXfile ugxfile = xread();
        subsetHandler = ugxfile.getSubset_handler().get(0);
        
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
        for (int i = 0; i < ssNumber; i++) {

            meshArray[i].getPoints().addAll(vertices);
            meshArray[i].getTexCoords().addAll(0,0);
            Group vertexGroup = new Group();
            Group edgesGroup = new Group();
            
            ssVertices = ugxfile.getSubset_handler().get(0).getSubsets().get(i).getVertexArray();
            
            ssEdges = ugxfile.getSubset_handler().get(0).getSubsets().get(i).getEdgeArray();
            
            ssFaces = ugxfile.getSubset_handler().get(0).getSubsets().get(i).getFacesArray();
           
            ssVolumes = ugxfile.getSubset_handler().get(0).getSubsets().get(i).getVolumeArray();
            
            // start of vertex visualisation
            if (ugxfile.getSubset_handler().get(0).getSubsets().get(i).isHasVertices()) {
                Sphere[] sphereArray = new Sphere[ssVertices.length];
                
                for (int j = 0; j < ssVertices.length; j++) {
                    sphereArray[j] = new Sphere(0.025);
                    sphereArray[j].setTranslateX(vertices[ssVertices[j]*3]);
                    sphereArray[j].setTranslateY(vertices[ssVertices[j]*3+1]);
                    sphereArray[j].setTranslateZ(vertices[ssVertices[j]*3+2]);
                    PhongMaterial sphereMat = new PhongMaterial(new Color(ugxfile.getSubset_handler().get(0).getSubsets().get(i).getColor()[0],
                                                                ugxfile.getSubset_handler().get(0).getSubsets().get(i).getColor()[1],
                                                                ugxfile.getSubset_handler().get(0).getSubsets().get(i).getColor()[2],
                                                                Math.abs(ugxfile.getSubset_handler().get(0).getSubsets().get(i).getColor()[3])));
                    sphereArray[j].setMaterial(sphereMat);
                    vertexGroup.getChildren().add(sphereArray[j]);
                }     
            }// end of vertex visualisation
            
            
            // start of edge visualisation
            if (ugxfile.getSubset_handler().get(0).getSubsets().get(i).isHasEdges()) {
                TriangleMesh edgesMesh = new TriangleMesh();
                edgesMesh.getTexCoords().addAll(0,0);
                ArrayList<Prism> edgesPrisms = new ArrayList<Prism>();
                float width = 0.01f;
                
                for (int j = 0; j < ssEdges.length; j++) {
                    
                    float bottomXexact = edges.get(ssEdges[j]).getVertices()[0].getX();
                    float bottomYexact = edges.get(ssEdges[j]).getVertices()[0].getY();
                    float bottomZ = edges.get(ssEdges[j]).getVertices()[0].getZ();
                    
                    float topXexact = edges.get(ssEdges[j]).getVertices()[1].getX();
                    float topYexact = edges.get(ssEdges[j]).getVertices()[1].getY();
                    float topZ = edges.get(ssEdges[j]).getVertices()[1].getZ();
                    
                    //create triangle around the first Node
                        edgesMesh.getPoints().addAll(bottomXexact - width, bottomYexact - width, bottomZ-width);
                        edgesMesh.getPoints().addAll(bottomXexact + width, bottomYexact - width, bottomZ);
                        edgesMesh.getPoints().addAll(bottomXexact, bottomYexact + width, bottomZ);
                    //create triangle around the second Node
                        edgesMesh.getPoints().addAll(topXexact - width, topYexact - width, topZ-width);
                        edgesMesh.getPoints().addAll(topXexact + width, topYexact - width, topZ);
                        edgesMesh.getPoints().addAll(topXexact, topYexact + width, topZ);
                                     
                    //creat a prism from the two triagnles and add the faces
                    edgesPrisms.add(new Prism(j*6, j*6+2, j*6+1, j*6+3, j*6+5, j*6+4, j));
                    edgesMesh.getFaces().addAll(edgesPrisms.get(j).getFacesArray());

                }
                MeshView edgesMeshView = new MeshView(edgesMesh);
                PhongMaterial edgeMat = new PhongMaterial(new Color(ugxfile.getSubset_handler().get(0).getSubsets().get(i).getColor()[0],
                                                                ugxfile.getSubset_handler().get(0).getSubsets().get(i).getColor()[1],
                                                                ugxfile.getSubset_handler().get(0).getSubsets().get(i).getColor()[2],
                                                                Math.abs(ugxfile.getSubset_handler().get(0).getSubsets().get(i).getColor()[3])));
                edgesMeshView.setMaterial(edgeMat);
                //edgesMeshView.setDrawMode(DrawMode.FILL);
                //edgesMeshView.setCullFace(CullFace.NONE);
     
                edgesGroup.getChildren().addAll(edgesMeshView);
                
            } // end of edge visualisation
            
            // start of face visualisation
            if (ugxfile.getSubset_handler().get(0).getSubsets().get(i).isHasFaces()) {
                
                for (int j = 0; j < ssFaces.length; j++) {
                    meshArray[i].getFaces().addAll(geometry2DList.get(ssFaces[j]).getFacesArray());
                }
            } // end of face visualisation
            
            // start of volume visualisation
            if (ugxfile.getSubset_handler().get(0).getSubsets().get(i).isHasVolumes()){
                
                for (int j = 0; j < ssVolumes.length; j++) {
                    meshArray[i].getFaces().addAll(geometry3DList.get(ssVolumes[j]).getFacesArray());
                }
            }// end of volume visualisation
                

            meshViewArray[i] = new MeshView(meshArray[i]);
          
            if (ugxfile.getSubset_handler().get(0).getSubsets().get(i).isHasVertices() && ugxfile.getSubset_handler().get(0).getSubsets().get(i).isHasEdges() ) {
                Group fusedGroup = new Group(vertexGroup,edgesGroup,meshViewArray[i]);
                subsetGroup.getChildren().addAll(fusedGroup);
                
            } else if(ugxfile.getSubset_handler().get(0).getSubsets().get(i).isHasVertices()){
                Group fusedGroup = new Group(vertexGroup,meshViewArray[i]);
                subsetGroup.getChildren().addAll(fusedGroup);
                
            }else if(ugxfile.getSubset_handler().get(0).getSubsets().get(i).isHasEdges()){
                Group fusedGroup = new Group(edgesGroup,meshViewArray[i]);
                subsetGroup.getChildren().addAll(fusedGroup);
                
            }
            else{
                subsetGroup.getChildren().add(meshViewArray[i]);
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
        if (ambient) {
            for (int i = 0; i < ssNumber; i++) {
                
                AmbientLight light = new AmbientLight(Color.WHITE);
                light.getScope().add(meshViewArray[i]);
                subsetGroup.getChildren().add(light);
            }
        }
        if(fill) { 
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
        
}
