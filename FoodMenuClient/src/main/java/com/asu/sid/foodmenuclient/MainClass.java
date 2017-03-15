/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asu.sid.foodmenuclient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Siddharth
 */
public class MainClass {

    private static final Logger LOG = LoggerFactory.getLogger(MainClass.class);
    private static final String requestXML = "<NewFoodItems xmlns=”http://cse564.asu.edu/PoxAssignment”>\n"
            + "    <FoodItem country=\"GB\">\n"
            + "        <name>Cornish Pasty</name>\n"
            + "        <description>Tender cubes of steak, potatoes and swede wrapped in flakey short crust pastry.  Seasoned with lots of pepper.  Served with mashed potatoes, peas and a side of gravy</description>\n"
            + "        <category>Dinner</category>\n"
            + "        <price>15.95</price>\n"
            + "    </FoodItem>\n"
            + "</NewFoodItems >";

    public static void main(String[] args) {
        System.out.println("------------------Starting Client-----------------------------");

        FoodResourceClient foodClient = new FoodResourceClient();
        String responseMessage = foodClient.addOrGetFoodItem(requestXML);

        System.out.println("The message is " + responseMessage);

        System.out.println("------------------Ending Client Application----------------------");

    }

}
