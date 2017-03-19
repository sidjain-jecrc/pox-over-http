/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asu.sid.beans;

import java.util.List;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Siddharth
 */
@XmlRootElement(name = "RetrievedFoodItems")
public class RetrievedFoodItems {

    private String xmlns = "http://cse564.asu.edu/PoxAssignment";
    private List<FoodItem> foodItems;
    private InvalidFoodItem invalidFoodItem;

    public RetrievedFoodItems() {

    }

    public RetrievedFoodItems(List<FoodItem> foodItems, InvalidFoodItem invalidFoodItem) {
        this.foodItems = foodItems;
        this.invalidFoodItem = invalidFoodItem;
    }

    @XmlAttribute(name = "xmlns")
    public String getXmlns() {
        return xmlns;
    }

    public void setXmlns(String xmlns) {
        this.xmlns = xmlns;
    }

    @XmlElement(name = "FoodItem")
    public List<FoodItem> getFoodItems() {
        return foodItems;
    }

    public void setFoodItems(List<FoodItem> foodItems) {
        this.foodItems = foodItems;
    }

    @XmlElement(name = "InvalidFoodItem")
    public InvalidFoodItem getInvalidFoodItem() {
        return invalidFoodItem;
    }

    public void setInvalidFoodItem(InvalidFoodItem invalidFoodItem) {
        this.invalidFoodItem = invalidFoodItem;
    }

}
