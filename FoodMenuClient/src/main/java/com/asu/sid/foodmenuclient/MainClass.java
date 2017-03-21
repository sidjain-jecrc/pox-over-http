/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asu.sid.foodmenuclient;

import com.asu.sid.foodmenuclient.beans.FoodItem;
import com.asu.sid.foodmenuclient.beans.NewFoodItems;
import com.asu.sid.foodmenuclient.beans.SelectedFoodItems;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

/**
 *
 * @author Siddharth
 */
public class MainClass {

    private static final Logger LOG = Logger.getLogger(MainClass.class.getName());
    private static String requestXML = null;

    public static void main(String[] args) {
        System.out.println("------------------Starting Client-----------------------------");
        BufferedReader br = null;

        // Provide user with an option to enter his/her choice, whether he would like to add or get food item
        try {
            br = new BufferedReader(new InputStreamReader(System.in));
            while (true) {

                System.out.print("Would you like to add or get food item from server, type \"add\" or \"get\"): ");
                String choice = br.readLine();

                String country = null;
                String name = null;
                String desciption = null;
                String category = null;
                String price = null;

                // Add food item choice
                if ("add".equalsIgnoreCase(choice) || choice.equalsIgnoreCase("a")) {

                    System.out.print("Country: ");
                    country = br.readLine();
                    System.out.print("Name: ");
                    name = br.readLine();
                    System.out.print("Description: ");
                    desciption = br.readLine();
                    System.out.print("Category: ");
                    category = br.readLine();
                    System.out.print("Price: ");
                    price = br.readLine();

                    // creating NewFoodItems object and marshalling it into xml string
                    NewFoodItems newFoodItems = new NewFoodItems();
                    FoodItem newFood = new FoodItem();
                    newFood.setCountry(country);
                    newFood.setName(name);
                    newFood.setDescription(desciption);
                    newFood.setCategory(category);
                    newFood.setPrice(price);
                    newFoodItems.setFoodItem(newFood);

                    requestXML = generateXmlString(NewFoodItems.class, newFoodItems);
                    System.out.println("-----Request------");
                    System.out.println(requestXML);

                    FoodResourceClient foodClient = new FoodResourceClient();
                    String responseMessage = foodClient.addOrGetFoodItem(requestXML);
                    System.out.println("-----Response------");
                    System.out.println(responseMessage);

                } else if ("get".equalsIgnoreCase(choice) || choice.equalsIgnoreCase("g")) {

                    System.out.print("How many items do you want to retrieve: ");
                    int itemToGet = Integer.valueOf(br.readLine());

                    // creating SelectedFoodItems object and marshalling it into xml string
                    SelectedFoodItems selectedFoodItems = new SelectedFoodItems();
                    List<String> selectedItemList = new ArrayList<>();
                    for (int index = 0; index < itemToGet; index++) {
                        System.out.print("Enter food item id: ");
                        String foodID = br.readLine();
                        selectedItemList.add(foodID);
                    }
                    selectedFoodItems.setFoodItemId(selectedItemList);

                    requestXML = generateXmlString(SelectedFoodItems.class, selectedFoodItems);
                    System.out.println("-----Request------");
                    System.out.println(requestXML);

                    // sending the request through food resource client
                    FoodResourceClient foodClient = new FoodResourceClient();
                    String responseMessage = foodClient.addOrGetFoodItem(requestXML);
                    System.out.println("-----Response------");
                    System.out.println(responseMessage);
                }

                System.out.print("Would you like to continue? (Y|N): ");
                String exitChoice = br.readLine();
                if (exitChoice.equalsIgnoreCase("N") || exitChoice.equalsIgnoreCase("No")) {
                    break;
                }
            }
        } catch (IOException e) {
            LOG.log(Level.SEVERE, e.getMessage());
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    LOG.log(Level.SEVERE, e.getMessage());
                }
            }
        }
        System.out.println("------------------Ending Client Application----------------------");
    }

    private static String generateXmlString(Class instance, Object object) {
        String xmlString = null;
        JAXBContext jaxbContext;
        Marshaller marsh;
        try {
            jaxbContext = JAXBContext.newInstance(instance);
            marsh = jaxbContext.createMarshaller();
            marsh.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

            // Write to xml string
            StringWriter stringWriter = new StringWriter();
            if (object instanceof SelectedFoodItems) {
                SelectedFoodItems selectedItem = (SelectedFoodItems) object;
                marsh.marshal(selectedItem, stringWriter);
            } else if (object instanceof NewFoodItems) {
                NewFoodItems newItem = (NewFoodItems) object;
                marsh.marshal(newItem, stringWriter);
            }
            xmlString = stringWriter.toString();

        } catch (JAXBException ex) {
            LOG.log(Level.SEVERE, ex.getMessage());
        }
        return xmlString;
    }

}
