/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asu.sid.beans;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

/**
 *
 * @author Siddharth
 */
public class FoodItem {

    int id;
    String name;
    String description;
    String category;
    String country;
    float price;

    public int getId() {
        return id;
    }

    public String getCountry() {
        return country;
    }

    @XmlAttribute
    public void setCountry(String country) {
        this.country = country;
    }

    @XmlElement
    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    @XmlElement
    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    @XmlElement
    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    @XmlElement
    public void setCategory(String category) {
        this.category = category;
    }

    public float getPrice() {
        return price;
    }

    @XmlElement
    public void setPrice(float price) {
        this.price = price;
    }

}
