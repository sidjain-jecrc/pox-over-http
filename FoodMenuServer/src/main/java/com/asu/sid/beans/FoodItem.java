/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asu.sid.beans;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

/**
 *
 * @author Siddharth
 */
@XmlType(propOrder = { "id", "name", "description", "category", "price"})
public class FoodItem {

    String country;
    int id;
    String name;
    String description;
    String category;
    float price;

    public FoodItem() {

    }

    public FoodItem(int id, String name, String description, String category, String country, float price) {
        super();
        this.id = id;
        this.name = name;
        this.description = description;
        this.category = category;
        this.country = country;
        this.price = price;
    }

    public int getId() {
        return id;
    }

    @XmlAttribute(name = "country")
    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

}
