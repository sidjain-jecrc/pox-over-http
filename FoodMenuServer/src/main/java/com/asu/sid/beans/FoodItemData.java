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
@XmlRootElement(name = "FoodItemData")
public class FoodItemData {

    private List<FoodItem> foodItems;

    public FoodItemData() {

    }

    public FoodItemData(List<FoodItem> foodItems) {
        this.foodItems = foodItems;
    }

    @XmlElement(name = "FoodItem")
    public List<FoodItem> getFoodItems() {
        return foodItems;
    }

    public void setFoodItems(List<FoodItem> foodItems) {
        this.foodItems = foodItems;
    }

}
