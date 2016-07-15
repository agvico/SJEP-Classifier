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
    
    private final int itemID;              // The ID of the item
    private final String value;            // the value of the variable
    private final String variable;         // The name of the variable to represent
    private final double growthRate;       // The growthRate of the item
    
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
    public boolean equals(Object other) {
          if (other == null) return false;
          if (other == this) return true;
          if (!(other instanceof Item))return false;
          
          Item obj = (Item) other;
          boolean c1 = this.D1count == obj.D1count;
          boolean c2 = this.D2count == obj.D2count;
          boolean c3 = this.growthRate == obj.growthRate;
          boolean c4 = this.getItemID() == obj.getItemID();
          boolean c5 = this.value.equals(obj.value);
          boolean c6 = this.variable.equals(obj.variable);          
          return c1 && c2 && c3 && c4 && c5 && c6;
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
        return getVariable() + " = " + getValue();
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

    /**
     * @return the itemID
     */
    public int getItemID() {
        return itemID;
    }

    /**
     * @return the value
     */
    public String getValue() {
        return value;
    }

    /**
     * @return the variable
     */
    public String getVariable() {
        return variable;
    }
    
}
