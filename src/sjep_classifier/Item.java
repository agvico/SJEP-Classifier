/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sjep_classifier;

/**
 *
 * @author angel
 */
public class Item implements Comparable<Item>{
    
    private int itemID;              // The ID of the item
    private String value;            // the value of the variable
    private String variable;         // The name of the variable to represent
    private double growthRate;       // The growthRate of the item
    
    private int D1count;             // Counts of the actual item in the node  for class 1
    private int D2count;             // Counts of the actual item in the node  for class 2
    
    public CPTreeNode child;         // the child of the node
    
    public Item(int id, String value, String variable, double gr){
        itemID = id;
        this.value = value;
        this.variable = variable;
        growthRate = gr;
        D1count = 0;
        D2count = 0;
    }
    
    
    @Override
    public int compareTo(Item o) {
       if(this.growthRate > o.growthRate){
           return -1;
       }
       
       if(this.growthRate < o.growthRate){
           return 1;
       }
       
       return 0;
    }
    
    @Override
    public String toString(){
        return variable + ": " + value;
    }
    
    public void incrementsD1(){
        D1count++;
    }
    
    public void incrementsD2(){
        D2count++;
    }

    /**
     * @return the D1count
     */
    public int getD1count() {
        return D1count;
    }

    /**
     * @return the D2count
     */
    public int getD2count() {
        return D2count;
    }

    /**
     * @param D1count the D1count to set
     */
    public void setD1count(int D1count) {
        this.D1count = D1count;
    }

    /**
     * @param D2count the D2count to set
     */
    public void setD2count(int D2count) {
        this.D2count = D2count;
    }
    
}
