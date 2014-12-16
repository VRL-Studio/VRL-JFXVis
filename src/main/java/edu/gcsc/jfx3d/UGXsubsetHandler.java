/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.gcsc.jfx3d;

import com.thoughtworks.xstream.annotations.XStreamImplicit;
import java.util.ArrayList;

/**
 *
 * @author Eugen
 */
public class UGXsubsetHandler {
    
    @XStreamImplicit(itemFieldName ="subset")
    private ArrayList<UGXsubset> subset = new ArrayList<UGXsubset>();
    

    public ArrayList<UGXsubset> getSubsets() {
        return subset;
    }


    
    
    
    
}
