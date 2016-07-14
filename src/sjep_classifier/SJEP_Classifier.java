/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sjep_classifier;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import keel.Dataset.*;
import org.core.*;

/**
 *
 * @author angel
 */
public class SJEP_Classifier {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        InstanceSet a = new InstanceSet();
        try {
            a.readSet(args[0], true);
            a.setAttributesAsNonStatic();
        } catch (DatasetException ex) {
            Logger.getLogger(SJEP_Classifier.class.getName()).log(Level.SEVERE, null, ex);
        } catch (HeaderFormatException ex) {
            Logger.getLogger(SJEP_Classifier.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        TreeMap<Item, Void> arbol = new TreeMap<>();
        Item b = new Item(1, "hola", "Saludo", 1);
        Item c = new Item(1, "adios", "Saludo", 3);
        Item d = new Item(1, "feo", "Saludo", 0.2);
        
        ArrayList<Item> lista = new ArrayList<>();
        lista.add(b);
        lista.add(c);
        lista.add(d);
       
        CPTree tree = new CPTree();
        
        tree.insertTree(lista, 0);
        tree.insertTree(lista, 0);
        System.out.println("HELOOOO");
        // First, get the list of all the individual itemsets:
        
        
    }
    
}
