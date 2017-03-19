/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asu.sid.foodmenuclient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import org.w3c.dom.Element;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
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
                String price = null;

                // Add food item choice
                if ("add".equalsIgnoreCase(choice)) {

                    // create request string once the user has entered choice and food item information
                    DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
                    DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

                    // creating root element for new food items request
                    Document addFoodDoc = docBuilder.newDocument();
                    Element rootElement = addFoodDoc.createElement("NewFoodItems");
                    addFoodDoc.appendChild(rootElement);
                    rootElement.setAttribute("xmlns", "http://cse564.asu.edu/PoxAssignment");

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

                    Element foodItem = addFoodDoc.createElement("FoodItem");
                    rootElement.appendChild(foodItem);
                    foodItem.setAttribute("country", country);

                    Element foodName = addFoodDoc.createElement("name");
                    foodName.appendChild(addFoodDoc.createTextNode(name));
                    foodItem.appendChild(foodName);

                    Element foodDesc = addFoodDoc.createElement("description");
                    foodDesc.appendChild(addFoodDoc.createTextNode(desciption));
                    foodItem.appendChild(foodDesc);

                    Element foodCategory = addFoodDoc.createElement("category");
                    foodCategory.appendChild(addFoodDoc.createTextNode(category));
                    foodItem.appendChild(foodCategory);

                    Element foodPrice = addFoodDoc.createElement("price");
                    foodPrice.appendChild(addFoodDoc.createTextNode(price));
                    foodItem.appendChild(foodPrice);

                    requestXML = xmlDocToStringConverter(addFoodDoc);
                    System.out.println(requestXML);

                    // sending the request through food resource client
                    FoodResourceClient foodClient = new FoodResourceClient();
                    String responseMessage = foodClient.addOrGetFoodItem(requestXML);
                    System.out.println("Response is: " + responseMessage);

                } else if ("get".equalsIgnoreCase(choice)) {

                    System.out.print("How many items do you want to retrieve: ");
                    int itemToGet = Integer.valueOf(br.readLine());

                    DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
                    DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

                    // creating root element for new food items request
                    Document getFoodDoc = docBuilder.newDocument();
                    Element rootElement = getFoodDoc.createElement("SelectedFoodItems");
                    getFoodDoc.appendChild(rootElement);
                    rootElement.setAttribute("xmlns", "http://cse564.asu.edu/PoxAssignment");

                    for (int index = 0; index < itemToGet; index++) {
                        System.out.print("Enter food item id: ");
                        String foodID = br.readLine();

                        Element foodItemId = getFoodDoc.createElement("FoodItemId");
                        foodItemId.appendChild(getFoodDoc.createTextNode(foodID));
                        rootElement.appendChild(foodItemId);
                    }

                    requestXML = xmlDocToStringConverter(getFoodDoc);
                    System.out.println(requestXML);

                    // sending the request through food resource client
                    FoodResourceClient foodClient = new FoodResourceClient();
                    String responseMessage = foodClient.addOrGetFoodItem(requestXML);
                    System.out.println("The message is " + responseMessage);
                }

                System.out.println("Would you like to continue? (Y|N): ");
                String exitChoice = br.readLine();
                if (exitChoice.equalsIgnoreCase("N") || exitChoice.equalsIgnoreCase("No")) {
                    break;
                }
            }
        } catch (IOException e) {
           System.out.println("IO Exception: " + e);
        } catch (ParserConfigurationException ex) {
            System.out.println("Parser exception: " + ex);
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
