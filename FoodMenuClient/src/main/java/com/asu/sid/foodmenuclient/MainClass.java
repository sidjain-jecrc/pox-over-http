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
import java.util.logging.Level;
import org.w3c.dom.Element;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
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

                System.out.print("Would you like to add or get food item from server (add or get): ");
                String choice = br.readLine();

                String country = null;
                String name = null;
                String desciption = null;
                String category = null;
                String price = null;

                // Add food item choice
                if ("add".equalsIgnoreCase(choice)) {
                    System.out.println("Country: ");
                    country = br.readLine();
                    System.out.println("Name: ");
                    name = br.readLine();
                    System.out.println("Description: ");
                    desciption = br.readLine();
                    System.out.println("Category: ");
                    category = br.readLine();
                    System.out.println("Price: ");
                    price = br.readLine();

                    // create request string once the user has entered choice and food item information
                    DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
                    DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

                    // root elements
                    Document foodDoc = docBuilder.newDocument();
                    Element rootElement = foodDoc.createElement("NewFoodItems");
                    foodDoc.appendChild(rootElement);
                    rootElement.setAttribute("xmlns", "http://cse564.asu.edu/PoxAssignment");

                    Element foodItem = foodDoc.createElement("FoodItem");
                    rootElement.appendChild(foodItem);
                    foodItem.setAttribute("country", country);

                    Element foodName = foodDoc.createElement("name");
                    foodName.appendChild(foodDoc.createTextNode(name));
                    foodItem.appendChild(foodName);

                    Element foodDesc = foodDoc.createElement("description");
                    foodDesc.appendChild(foodDoc.createTextNode(desciption));
                    foodItem.appendChild(foodDesc);

                    Element foodCategory = foodDoc.createElement("category");
                    foodCategory.appendChild(foodDoc.createTextNode(category));
                    foodItem.appendChild(foodCategory);

                    Element foodPrice = foodDoc.createElement("price");
                    foodPrice.appendChild(foodDoc.createTextNode(price));
                    foodItem.appendChild(foodPrice);

                    TransformerFactory tf = TransformerFactory.newInstance();
                    Transformer transformer = tf.newTransformer();
                    transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
                    StringWriter writer = new StringWriter();
                    transformer.transform(new DOMSource(foodDoc), new StreamResult(writer));
                    requestXML = writer.getBuffer().toString().replaceAll("\n|\r", "");
                    System.out.println(requestXML);

                    // sending the request through food resource client
                    FoodResourceClient foodClient = new FoodResourceClient();
                    String responseMessage = foodClient.addOrGetFoodItem(requestXML);
                    System.out.println("The message is " + responseMessage);
                    break;

                } else if ("get".equalsIgnoreCase(choice)) {

                } else {
                    // wrong input message

                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException ex) {
            System.out.println("Parser exception: " + ex);
        } catch (TransformerConfigurationException ex) {
            System.out.println("Transformer Configuration exception: " + ex);
        } catch (TransformerException ex) {
            System.out.println("Transformer exception: " + ex);
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        System.out.println("------------------Ending Client Application----------------------");

    }

}
