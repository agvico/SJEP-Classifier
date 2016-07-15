/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sjep_classifier;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
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

        ArrayList<Item> simpleItems = getSimpleItems(args[0]);
       
//        TreeMap<Item, Void> arbol = new TreeMap<>();
//        Item b = new Item(1, "hola", "Saludo", 1);
//        Item c = new Item(1, "adios", "Saludo", 3);
//        Item d = new Item(1, "feo", "Saludo", 0.2);
//        
//        ArrayList<Item> lista = new ArrayList<>();
//        lista.add(b);
//        lista.add(c);
//        lista.add(d);
//       
//        CPTree tree = new CPTree();
//        
//        tree.insertTree(lista, 0);
//        tree.insertTree(lista, 0);
//        
//        
//        ArrayList<Pattern> mineTree = tree.mineTree(0.01);
//       for(Pattern p : mineTree){
//           System.out.println(p);
//       }
//        System.out.println("HELOOOO");
        // First, get the list of all the individual itemsets:
    }

    public static ArrayList<Item> getSimpleItems(String path) {
        // Reads the KEEL instance set.
        InstanceSet a = new InstanceSet();
        int countD1 = 0;   // counts of examples belonging to class D1 and D2.
        int countD2 = 0;
        ArrayList<Item> simpleItems = new ArrayList<>();
        try {
            a.readSet(path, true);
            a.setAttributesAsNonStatic();

            // get classes
            ArrayList<String> classes = new ArrayList<>(Attributes.getOutputAttribute(0).getNominalValuesList());
            // Gets the count of examples for each class to calculate the growth rate.
            for (int i = 0; i < a.getNumInstances(); i++) {
                if (classes.indexOf(a.getInstance(i).getOutputNominalValues(0)) == 0) {
                    countD1++;
                } else {
                    countD2++;
                }
            }

            // Get the attributes
            Attribute[] attributes = Attributes.getInputAttributes();
            int countId = 0;
            // for each attribute
            for (int i = 0; i < attributes.length; i++) {
                // get nominal values of the attribute
                ArrayList<String> nominalValues = new ArrayList<>(attributes[i].getNominalValuesList());
                int countValueInD1 = 0;
                int countValueInD2 = 0;
                //for each nominal value
                for (String value : nominalValues) {
                    // counts the times the value appear for each class
                    for (int j = 0; j < a.getNumInstances(); j++) {
                        if (value.equals(a.getInputNominalValue(j, i))) {
                            // If are equals, check the class and increment counters
                            if (classes.indexOf(a.getOutputNominalValue(j, 0)) == 0) {
                                countValueInD1++;
                            } else {
                                countValueInD2++;
                            }
                        }
                    }

                    // now calculate the growth rate of the item.
                    double gr = 0;
                    if ((countValueInD1 == 0 && countValueInD2 != 0) || (countValueInD1 != 0 && countValueInD2 == 0)) {
                        gr = Double.POSITIVE_INFINITY;
                    } else if (countValueInD1 > 0 && countValueInD2 > 0) {
                        gr = Math.max(((double) countValueInD1 / (double) countD1) / ((double) countValueInD2 / (double) countD2),
                                ((double) countValueInD2 / (double) countD2) / ((double) countValueInD1 / (double) countD1));
                    }

                    // Add the item to the list of simple items
                    simpleItems.add(new Item(countId, value, attributes[i].getName(), gr));
                }
            }

        } catch (DatasetException ex) {
            Logger.getLogger(SJEP_Classifier.class.getName()).log(Level.SEVERE, null, ex);
        } catch (HeaderFormatException ex) {
            Logger.getLogger(SJEP_Classifier.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
        return simpleItems;
    }

}
