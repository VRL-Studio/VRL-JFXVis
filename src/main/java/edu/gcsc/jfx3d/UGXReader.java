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

/**
 *
 * @author Eugen
 */
public class UGXReader {
    
    
    private String path;
    private ArrayList<Float> vertices = new ArrayList<Float>() ;
    private ArrayList<Integer> triangles = new ArrayList<Integer>();
    private ArrayList<Integer> tetra = new ArrayList<Integer>();
    private boolean asciiFormat;
    private ArrayList<UGXsubset> subsetList = new ArrayList<UGXsubset>();
    
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
        //read();
        
    }
    
    /*
    reads the content of a .ugx file using the xStream library
    - currently only works with files that consist of triangles and tetraedrons
    */
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
    
    
}
