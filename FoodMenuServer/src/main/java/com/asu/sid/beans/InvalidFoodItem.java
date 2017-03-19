/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asu.sid.beans;

import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Siddharth
 */
@XmlRootElement(name = "InvalidFoodItem")
public class InvalidFoodItem {
    
    private List<Integer> FoodItemId;

    public InvalidFoodItem(){        
    }
    
    public InvalidFoodItem(List<Integer> FoodItemId) {
        this.FoodItemId = FoodItemId;
    }

    @XmlElement(name = "FoodItemId")
    public List<Integer> getFoodItemId() {
        return FoodItemId;
    }

    public void setFoodItemId(List<Integer> FoodItemId) {
        this.FoodItemId = FoodItemId;
    }
    
    
}
