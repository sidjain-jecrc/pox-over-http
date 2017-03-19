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
@XmlRootElement(name = "FoodItemExists")
public class FoodItemExists {

    private String xmlns = "http://cse564.asu.edu/PoxAssignment";
    private List<Integer> FoodItemId;

    public FoodItemExists() {
    }

    public FoodItemExists(List<Integer> FoodItemId) {
        this.FoodItemId = FoodItemId;
    }

    @XmlAttribute(name = "xmlns")
    public String getXmlns() {
        return xmlns;
    }

    public void setXmlns(String xmlns) {
        this.xmlns = xmlns;
    }

    @XmlElement(name = "FoodItemId")
    public List<Integer> getFoodItemId() {
        return FoodItemId;
    }

    public void setFoodItemId(List<Integer> FoodItemId) {
        this.FoodItemId = FoodItemId;
    }

}
