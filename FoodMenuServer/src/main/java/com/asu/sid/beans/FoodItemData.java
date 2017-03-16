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
@XmlRootElement
public class FoodItemData {

    List<FoodItem> FoodItems;

    public List<FoodItem> getFoodItems() {
        return FoodItems;
    }

    @XmlElement(name = "FoodItem")
    public void setFoodItems(List<FoodItem> FoodItems) {
        this.FoodItems = FoodItems;
    }
}
