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
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

/**
 *
 * @author Siddharth
 */
public class MainClass {

    private static final Logger LOG = LoggerFactory.getLogger(MainClass.class);
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
                float price = 0.0f;

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
                    price = Float.valueOf(br.readLine());

                    // creating NewFoodItems object and marshalling it into xml string
                    NewFoodItems newFoodItems = new NewFoodItems();
                    FoodItem newFood = new FoodItem();
                    newFood.setCountry(country);
                    newFood.setName(name);
                    newFood.setDescription(desciption);
                    newFood.setCategory(category);
                    newFood.setPrice(price);
                    newFoodItems.setFoodItem(newFood);

                    JAXBContext jaxbContext = JAXBContext.newInstance(NewFoodItems.class);
                    Marshaller newFoodMarshall = jaxbContext.createMarshaller();
                    newFoodMarshall.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

                    // Write to xml string
                    StringWriter newFoodWriter = new StringWriter();
                    newFoodMarshall.marshal(newFoodItems, newFoodWriter);
                    requestXML = newFoodWriter.toString();
                    System.out.println("-----> Request is: " + newFoodWriter.toString());

                    FoodResourceClient foodClient = new FoodResourceClient();
                    String responseMessage = foodClient.addOrGetFoodItem(requestXML);
                    System.out.println("-----> Response is: " + responseMessage);

                } else if ("get".equalsIgnoreCase(choice) || choice.equalsIgnoreCase("g")) {

                    System.out.print("How many items do you want to retrieve: ");
                    int itemToGet = Integer.valueOf(br.readLine());

                    // creating SelectedFoodItems object and marshalling it into xml string
                    SelectedFoodItems selectedFoodItems = new SelectedFoodItems();
                    List<Integer> selectedItemList = new ArrayList<>();
                    for (int index = 0; index < itemToGet; index++) {
                        System.out.print("Enter food item id: ");
                        int foodID = Integer.valueOf(br.readLine());
                        selectedItemList.add(foodID);
                    }
                    selectedFoodItems.setFoodItemId(selectedItemList);
                    
                    JAXBContext jaxbContext = JAXBContext.newInstance(SelectedFoodItems.class);
                    Marshaller selectedFoodMarshall = jaxbContext.createMarshaller();
                    selectedFoodMarshall.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

                    // Write to xml string
                    StringWriter selectedFoodWriter = new StringWriter();
                    selectedFoodMarshall.marshal(selectedFoodItems, selectedFoodWriter);
                    requestXML = selectedFoodWriter.toString();
                    System.out.println("-----> Request is: " + requestXML);

                    // sending the request through food resource client
                    FoodResourceClient foodClient = new FoodResourceClient();
                    String responseMessage = foodClient.addOrGetFoodItem(requestXML);
                    System.out.println("-----> Response is: " + responseMessage);
                }

                System.out.println("Would you like to continue? (Y|N): ");
                String exitChoice = br.readLine();
                if (exitChoice.equalsIgnoreCase("N") || exitChoice.equalsIgnoreCase("No")) {
                    break;
                }
            }
        } catch (IOException e) {
            System.out.println("IO Exception: " + e);
        } catch (JAXBException ex) {
            java.util.logging.Logger.getLogger(MainClass.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    LOG.error(e.getMessage());
                }
            }
        }
        System.out.println("------------------Ending Client Application----------------------");
    }

    private static String xmlDocToStringConverter(Document foodDoc) {
        String xmlString = null;
        try {
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            StringWriter writer = new StringWriter();
            transformer.transform(new DOMSource(foodDoc), new StreamResult(writer));
            xmlString = writer.getBuffer().toString().replaceAll("\n|\r", "");
        } catch (TransformerException ex) {
            LOG.error(ex.getMessage());
        }
        return xmlString;
    }

}
