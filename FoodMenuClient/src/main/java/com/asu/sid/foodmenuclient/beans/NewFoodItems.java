/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asu.sid.foodmenuclient.beans;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Siddharth
 */
@XmlRootElement(name = "NewFoodItems")
public class NewFoodItems {
    
    private FoodItem foodItem;
    private String xmlns = "http://cse564.asu.edu/PoxAssignment";

    public NewFoodItems() {
    }

    public NewFoodItems(FoodItem foodItem) {
        this.foodItem = foodItem;
    }

    @XmlElement(name = "FoodItem")
    public FoodItem getFoodItem() {
        return foodItem;
    }

    public void setFoodItem(FoodItem foodItem) {
        this.foodItem = foodItem;
    }

    @XmlAttribute(name = "xmlns")
    public String getXmlns() {
        return xmlns;
    }

    public void setXmlns(String xmlns) {
        this.xmlns = xmlns;
    }
        
}
