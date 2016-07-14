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
}
