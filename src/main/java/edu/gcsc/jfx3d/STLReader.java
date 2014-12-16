/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.gcsc.jfx3d;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 *
 * @author Eugen
 */
public class STLReader {
    
    private String path;
    private ArrayList<Float> vertices = new ArrayList<Float>() ;
    private ArrayList<Float> normals = new ArrayList<Float>();
    private boolean asciiFormat;
    
    public STLReader(String filePath){
        
        path = filePath;
        asciiFormat = false;
        
        try {
        FileInputStream file = new FileInputStream(path);
        Scanner scanner = new Scanner(file);
        System.out.println("Reading from " + path);
        try {
            if (scanner.nextLine().contains("solid") && scanner.nextLine().contains("facet")) {       
            System.out.println("ASCII file detected");
            asciiFormat = true;   
            }else{       
            System.out.println("BINARY file detected");   
            }           
        } finally {
            scanner.close();   
        }
    }   catch (FileNotFoundException ex) {
        ex.printStackTrace(System.out);
    }
    }
    
    public void start()
    {
        
        if (asciiFormat) {
            readFromAscii();
        }else{
            readFromBinary();
        }
        
        System.out.println("Reading successful!");
    }    
    private void readFromBinary(){
        
        int amountOfTriangles = 0;
        
        try {

        FileInputStream file = new FileInputStream(path);
       
        byte[] buffer = new byte[84];
        file.read(buffer, 0, 84);
            
        amountOfTriangles = (int) (((buffer[83] & 0xff) << 24)
                | ((buffer[82] & 0xff) << 16) | ((buffer[81] & 0xff) << 8) | (buffer[80] & 0xff));
            
            for (int i = 0; i < amountOfTriangles; i++) {
                byte[] triangle = new byte[50];
                for (int j = 0; j < 50; j++) {
                    triangle[j] = (byte) file.read();
                }
                normals.add(bytesToFloat(triangle[0], triangle[1], triangle[2], triangle[3]));
                normals.add(bytesToFloat(triangle[4], triangle[5], triangle[6], triangle[7]));
                normals.add(bytesToFloat(triangle[8], triangle[9], triangle[10], triangle[11]));
                
                for (int k = 0; k < 3; k++) {
                    final int x = k * 12 +12 ;
                    vertices.add(bytesToFloat(triangle[x], triangle[x + 1], triangle[x + 2],
                            triangle[x + 3])) ;
                    vertices.add(bytesToFloat(triangle[x + 4], triangle[x + 5],
                            triangle[x + 6], triangle[x + 7])) ;
                    vertices.add(bytesToFloat(triangle[x + 8], triangle[x + 9],
                            triangle[x + 10], triangle[x + 11])) ;
            
                }
            }
        file.close();
    }   catch (Exception ex) {
        ex.printStackTrace(System.out);
    }
        
    }
    
    private float bytesToFloat(byte b0, byte b1, byte b2, byte b3) {
        return Float.intBitsToFloat((((b3 & 0xff) << 24) | ((b2 & 0xff) << 16)
                | ((b1 & 0xff) << 8) | (b0 & 0xff)));
    }
    
    
    
    private void readFromAscii(){
        String[] inputLine;
        try {
        File file = new File(path);
       
        Scanner scanner = new Scanner(file);
        try {
            while(scanner.hasNextLine()) {

                inputLine = scanner.nextLine().trim().split(" ");
                if ( inputLine[0].equalsIgnoreCase("vertex")) {   
                    vertices.add(Float.parseFloat(inputLine[1]));
                    vertices.add(Float.parseFloat(inputLine[2]));
                    vertices.add(Float.parseFloat(inputLine[3]));
                }
                if ( inputLine[0].equalsIgnoreCase("facet")) {    
                    normals.add(Float.parseFloat(inputLine[2]));
                    normals.add(Float.parseFloat(inputLine[3]));
                    normals.add(Float.parseFloat(inputLine[4]));            
                }
            }
            
        } finally {
            scanner.close();    
        }
        
    }   catch (FileNotFoundException ex) {
        ex.printStackTrace(System.out);
    } 
        
    }
    
   
    public float[] getFacetPoints(int i){
        float[] triangleArray = new float[9];
        for (int j = 0; j < 9; j++) {
            triangleArray[j] = vertices.get(i*9+j);
            
//            System.out.print(triangleArray[j] + " ");
//            if (((j+1) % 3) == 0) {
//                System.out.println("");
//            }
        }
         
        
        return triangleArray;
    }
    
    public float[] getNormal(int i){
        float[] normalArray = new float[3];
        for (int j = 0; j < 3; j++) {
            normalArray[j] = normals.get(i*3+j);
            
          //  System.out.print(normalArray[j] + " ");
            
        }
        return normalArray;
    }
    
    public ArrayList<Float> getVertices(){
        return vertices;
    }
    
    public float[] getVerticesFloatArray(){
        float[] verticesArray = new float[vertices.size()];
        for (int i = 0; i < vertices.size(); i++) {
            verticesArray[i] = vertices.get(i);
        }
        return verticesArray;
    }
    
    public float[] getNormalsFloatArray(){
        float[] normalsArray = new float[normals.size()];
        for (int i = 0; i < normals.size(); i++) {
            normalsArray[i] = normals.get(i);
        }
        return normalsArray;
    }
    
    
    public ArrayList<Float> getNormals(){
        return normals;
    }
    
}
