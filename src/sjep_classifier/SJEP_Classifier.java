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
import javafx.util.Pair;
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
        int countD1 = 0;
        int countD2 = 0;
        final double minSupp = 1;

        // Reads the original dataset
        try {
            a.readSet(args[0], true);
            ArrayList<String> classes = new ArrayList<>(Attributes.getOutputAttribute(0).getNominalValuesList());
            // Gets the count of examples for each class to calculate the growth rate.
            for (int i = 0; i < a.getNumInstances(); i++) {
                if (classes.indexOf(a.getInstance(i).getOutputNominalValues(0)) == 0) {
                    countD1++;
                } else {
                    countD2++;
                }
            }
        } catch (DatasetException | HeaderFormatException ex) {
            Logger.getLogger(SJEP_Classifier.class.getName()).log(Level.SEVERE, null, ex);
        }

       
        if (Attributes.getOutputAttribute(0).getNumNominalValues() <= 2) {
            // Normal Execution
            long t_ini = System.currentTimeMillis();
            // get simple itemsets to perform the ordering of the items
            ArrayList<Item> simpleItems = getSimpleItems(a, minSupp);
            // sort items by growth rate 
            simpleItems.sort(null);
            // gets all instances
            ArrayList<Pair<ArrayList<Item>, Integer>> instances = getInstances(a, simpleItems);
            for (int i = 0; i < instances.size(); i++) {
                // sort each arraylist of items
                instances.get(i).getKey().sort(null);
            }

            System.out.println("Loading the CP-Tree...");
            // Create the CP-Tree
            CPTree tree = new CPTree(countD1, countD2);
            // Add the instances on the CP-Tree
            for (Pair<ArrayList<Item>, Integer> inst : instances) {
                tree.insertTree(inst.getKey(), inst.getValue());
            }

            System.out.println("Mining SJEPs...");
            // Perform mining
            ArrayList<Pattern> mineTree = tree.mineTree(minSupp);

            if (mineTree.size() == 0) {
                System.out.println("NO SJEPs FOUND");
            } else {
                for (Pattern pat : mineTree) {
                    System.out.println(pat);
                }
                System.out.println("SJEPs found: " + mineTree.size());
            }
            System.out.println("Execution time: " + (System.currentTimeMillis() - t_ini) / 1000 + " seconds");
            // Rules TESTING here
            
        } else {
            // For multi-class, mining using the OVA binarization technique.
            
        }

    }

    public static ArrayList<Item> getSimpleItems(InstanceSet a, double minSupp) {
        // Reads the KEEL instance set.

        int countD1 = 0;   // counts of examples belonging to class D1 and D2.
        int countD2 = 0;
        ArrayList<Item> simpleItems = new ArrayList<>();
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
                double suppD1 = (double) countValueInD1 / (double) countD1;
                double suppD2 = (double) countValueInD2 / (double) countD2;
                // now calculate the growth rate of the item.
                double gr;
                if (suppD1 < minSupp && suppD2 < minSupp) {
                    gr = 0;
                } else if ((suppD1 == 0 && suppD2 >= minSupp) || (suppD1 >= minSupp && suppD2 == 0)) {
                    gr = Double.POSITIVE_INFINITY;
                } else {
                    gr = Math.max(suppD2 / suppD1, suppD1 / suppD2);
                }

                // Add the item to the list of simple items
                if (gr > 0) {
                    simpleItems.add(new Item(countId, value, attributes[i].getName(), gr));
                    countId++;
                }
            }
        }

        return simpleItems;
    }

    public static ArrayList<Pair<ArrayList<Item>, Integer>> getInstances(InstanceSet a, ArrayList<Item> simpleItems) {
        String[] att_names = new String[Attributes.getInputAttributes().length];
        ArrayList<Pair<ArrayList<Item>, Integer>> result = new ArrayList<>();
        ArrayList<String> classes = new ArrayList<>(Attributes.getOutputAttribute(0).getNominalValuesList());

        for (int i = 0; i < att_names.length; i++) {
            att_names[i] = Attributes.getAttribute(i).getName();
        }

        for (int i = 0; i < a.getNumInstances(); i++) {
            ArrayList<Item> list = new ArrayList<>();
            for (int j = 0; j < Attributes.getInputNumAttributes(); j++) {
                // Add the item into the pattern
                Item it = Item.find(simpleItems, att_names[j], a.getInputNominalValue(i, j));
                if (it != null) {
                    list.add(it);
                }
            }
            // Add into the set of instances, the second element is the class
            int clas = 0;
            if (classes.indexOf(a.getOutputNominalValue(i, 0)) != 0) {
                clas = 1;
            }
            result.add(new Pair(list, clas));
        }

        return result;
    }

}
