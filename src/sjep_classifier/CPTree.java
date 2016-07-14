/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sjep_classifier;

import java.util.ArrayList;

/**
 *
 * @author angel
 */
public class CPTree {
    
    public CPTreeNode root;
    
    public CPTree(){
        root = new CPTreeNode(0);
    }
     
   public void insertTree(ArrayList<Item> pattern, int clas){
       ArrayList<Item> p = new ArrayList<>(pattern);
        addInstance(p, root, clas);
    }
   
   
   private void addInstance(ArrayList<Item> pattern, CPTreeNode node, int clas){
        
        // End of the recursivity
        if(pattern.size() <= 0) return;
        
        // Search for the first item in the item list of the actual node.
        int value = node.items.indexOf(pattern.get(0));
        Item p = pattern.get(0);
        if(value == -1){
            // If item does not exist in the node:
            // D1 and D2 counts are 0
            p.setD1count(0);
            p.setD2count(0);
            p.child = null; 
            // Inserts p in the appropiate place according to the order
            node.items.add(p);
            node.items.sort(null);
            
            // Increments itemNumer by 1
            node.itemNumber++;
        }
        
        if(clas == 0 ){
            // Increments the number of counts in D1 by 1:
            node.items.get(node.items.indexOf(p)).incrementsD1();
        } else {
             // Increments the number of counts in D2 by 1:
            node.items.get(node.items.indexOf(p)).incrementsD2();
        }
        
        
        // Now, if patter is not empty
        if(pattern.size() > 0){
            // if item subtree is empty
            if(p.child == null){
                // create a new child node as subtree
                p.child = new CPTreeNode(0);
            }
            
            // recursive call with the next element of the pattern
            pattern.remove(0);
            addInstance(pattern, p.child, clas);
        }
    }
   
   
   /**
    * Merges T1's nodes into T2. T2 is updated(including new-node generation and
    *    existing-node changes, but no nodes
    *    deletion), while T1 remains unchanged.
    * The merge must be done T1 is the subtree and T2 is T1's parent. Else, the 
    * function would cause an stack overflow.
    * @param T1
    * @param T2 
    */
   public void mergeTree(CPTreeNode T1, CPTreeNode T2){
       // For each item in T1
       for(Item item : T1.items){
           // Search 'item' in T2
           int value = T2.items.indexOf(item);
           
           if(value != -1){
               // If 'item' is found in T2:
               int sumD1 = T2.items.get(value).getD1count() + item.getD1count();
               int sumD2 = T2.items.get(value).getD2count() + item.getD2count();
               // Update values of D1 and D2
               T2.items.get(value).setD1count(sumD1);
               T2.items.get(value).setD2count(sumD2);
           } else{
               // if 'item' is not found in T2:
               // insert the whole 'item' (including subtree) following the order
               T2.items.add(item);
               T2.items.sort(null);
               // increments itemNumber
               T2.itemNumber++;
           }
           
           // if 'item' subtree is not empty:
           if(item.child != null){
               // if T2.items[value] subtree is empty:
               value = T2.items.indexOf(item);
               if(T2.items.get(value).child == null){
                   // create a new node as subtree
                   T2.items.get(value).child = new CPTreeNode(0);
               } else {
                   // else, perform the recursive call on T2.items[value] subtree
                   mergeTree(item.child, T2.items.get(value).child);
               }
           }
           
       }
   }
    
    
}
