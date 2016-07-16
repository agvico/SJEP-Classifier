/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sjep_classifier;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
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
        final double minSupp = 0.01;

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
            // get simple itemsets to perform the ordering of the items and filter by gorwth rate
            ArrayList<Item> simpleItems = getSimpleItems(a, minSupp);
            // sort items by growth rate 
            simpleItems.sort(null);
            // gets all instances removing those instances
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
            ArrayList<Pattern> patterns = tree.mineTree(minSupp);

            if (patterns.size() == 0) {
                System.out.println("NO SJEPs FOUND");
            } else {
                try {
                    PrintWriter pw = new PrintWriter(args[0] + "-Patterns.csv");
                    for (int i = 0; i < patterns.size(); i++) {
                        pw.println(patterns.get(i).toString());
                    }
                    System.out.println("Testing patterns...");
                // pattern TESTING here
                    // First, reads the test set
                    InstanceSet test = new InstanceSet();
                    ArrayList<Pair<ArrayList<Item>, Integer>> testInstances;

                    test.readSet(args[1], false);
                    // Get the instances: It is not neccesary to sort that instances.
                    testInstances = getInstances(test, simpleItems);
                    // Writes the confusion matrix file in CSV format
                    pw = new PrintWriter(args[0] + "-CM.csv", "UTF-8");
                    pw.println("TP,FP,FN,TN"); // Prints the header

                    // Calculate the confusion matrix for each pattern to compute other quality measures
                    for (int i = 0; i < patterns.size(); i++) {
                        int tp = 0;
                        int tn = 0;
                        int fp = 0;
                        int fn = 0;
                        // for each instance
                        for (int j = 0; j < testInstances.size(); j++) {
                            // class '0' is considered the positive class
                            // If the pattern covers the example
                            if (patterns.get(i).covers(testInstances.get(j).getKey())) {
                                if (test.getOutputNumericValue(j, 0) == 0) {
                                    if (patterns.get(i).getClase() == 0) {
                                        tp++;
                                    } else {
                                        fn++;
                                    }
                                } else {
                                    if (patterns.get(i).getClase() == 1) {
                                        tn++;
                                    } else {
                                        fp++;
                                    }
                                }
                            }
                        }
                        // Saves on the file
                        pw.println(tp + "," + fp + "," + fn + "," + tn);
                    }
                    // close the writer
                    pw.close();

                    // Show statistics
                    System.out.println("================ STATISTICS =====================");
                    System.out.println("SJEPs found: " + patterns.size());
                    // Calculate the test accuracy:
                    computeAccuracy(testInstances, patterns, test, countD1, countD2);
                } catch (DatasetException | HeaderFormatException ex) {
                    Logger.getLogger(SJEP_Classifier.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(SJEP_Classifier.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
            System.out.println("Execution time: " + (System.currentTimeMillis() - t_ini) / 1000 + " seconds");

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

    public static double median(ArrayList<Integer> values) {
        if (values.size() > 1) {
            int middle = values.size() >>> 1;
            if (middle % 2 == 1) {
                return values.get(middle);
            } else {
                return (values.get(middle - 1) + values.get(middle)) / 2.0;
            }
        } else {
            if (values.size() == 1) {
                return values.get(0);
            }
            return 0;
        }
    }

    public static void calculateAccuracy(InstanceSet testSet, int[] predictions) {
        // we consider class 0 as positive and class 1 as negative
        int tp = 0;
        int fp = 0;
        int tn = 0;
        int fn = 0;

        // Calculate the confusion matrix.
        for (int i = 0; i < predictions.length; i++) {
            if (testSet.getOutputNumericValue(i, 0) == 0) {
                if (predictions[i] == 0) {
                    tp++;
                } else {
                    fn++;
                }
            } else {
                if (predictions[i] == 0) {
                    fp++;
                } else {
                    tn++;
                }
            }
        }

        System.out.println("Test Accuracy: " + ((double) (tp + tn) / (double) (tp + tn + fp + fn)) * 100 + "%");
    }

    public static void computeAccuracy(ArrayList<Pair<ArrayList<Item>, Integer>> testInstances, ArrayList<Pattern> patterns, InstanceSet test, int countD1, int countD2) {
        int[] predictions = new int[testInstances.size()];
        //Now, for each pattern
        for (int i = 0; i < testInstances.size(); i++) {
            // calculate the score for each class for classify:
            double scoreD1 = 0;
            double scoreD2 = 0;
            ArrayList<Integer> scoresD1 = new ArrayList<>();  // This is to calculate the base-score, that is the median
            ArrayList<Integer> scoresD2 = new ArrayList<>();
            // for each pattern mined
            for (int j = 0; j < patterns.size(); j++) {
                if (patterns.get(j).covers(testInstances.get(i).getKey())) {
                    // If the example is covered by the pattern.
                    // sum it support to the class of the pattern
                    if (testInstances.get(i).getValue() == 0) {
                        scoreD1 += patterns.get(j).getSupport();
                        scoresD1.add(patterns.get(j).getSupport());
                    } else {
                        scoreD2 += patterns.get(j).getSupport();
                        scoresD2.add(patterns.get(j).getSupport());
                    }

                }
            }

            // Now calculate the normalized score to make the prediction
            double medianD1 = median(scoresD1);
            double medianD2 = median(scoresD2);

            if (medianD1 == 0) {
                scoreD1 = 0;
            } else {
                scoreD1 = scoreD1 / medianD1;
            }

            if (medianD2 == 0) {
                scoreD2 = 0;
            } else {
                scoreD2 = scoreD2 / medianD2;
            }

            // make the prediction:
            if (scoreD1 > scoreD2) {
                predictions[i] = 0;
            } else if (scoreD1 < scoreD2) {
                predictions[i] = 1;
            } else {
                // In case of ties, the majority class is setted
                if (countD1 < countD2) {
                    predictions[i] = 0;
                } else {
                    predictions[i] = 1;
                }
            }
        }

        calculateAccuracy(test, predictions);
    }
}
