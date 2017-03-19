/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asu.sid.foodmenuserver;

import com.asu.sid.beans.FoodItem;
import com.asu.sid.beans.FoodItemAdded;
import com.asu.sid.beans.FoodItemData;
import com.asu.sid.beans.FoodItemExists;
import com.asu.sid.beans.InvalidFoodItem;
import com.asu.sid.beans.RetrievedFoodItems;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 *
 * @author Siddharth
 */
@Path("inventory")
public class FoodResourceServer {

    private static final Logger LOG = Logger.getLogger(FoodResourceServer.class.getName());
    private static final String ADD_FOOD = "NewFoodItems";
    private static final String GET_FOOD = "SelectedFoodItems";
    private static final String INVALID_REQ_RESPONSE = "<InvalidMessage xmlns=”http://cse564.asu.edu/PoxAssignment”/>";
    private static String responseXmlString = null;

    public FoodResourceServer() {
        LOG.info("Creating a FoodResource Server Instance");
    }

    @POST
    @Produces(MediaType.APPLICATION_XML)
    @Consumes(MediaType.APPLICATION_XML)
    public Response addOrGetFoodItem(String requestFoodItem) {

        try {
            DocumentBuilder builder = getDocBuilderInstance();
            Document reqDocument = builder.parse(new InputSource(new StringReader(requestFoodItem)));
            reqDocument.getDocumentElement().normalize();
            String requestRoot = reqDocument.getDocumentElement().getNodeName();

            // unmarshal the stored xml file into object to check if the food item is already added or whether it exists
            ClassLoader classLoader = getClass().getClassLoader();
            File foodXmlFile = new File(classLoader.getResource("xml/FoodItemData.xml").getFile());

            JAXBContext jaxbContext = JAXBContext.newInstance(FoodItemData.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

            if (requestRoot.equals(ADD_FOOD)) {
                LOG.info("Add food section");
                boolean doesFoodExists = false;

                NodeList reqFoodList = reqDocument.getElementsByTagName("FoodItem");
                if (reqFoodList.getLength() > 0) {
                    Element reqFoodItem = (Element) reqFoodList.item(0);
                    String reqCountry = reqFoodItem.getAttribute("country");
                    String reqFoodName = reqFoodItem.getElementsByTagName("name").item(0).getTextContent();
                    String reqFoodDesc = reqFoodItem.getElementsByTagName("description").item(0).getTextContent();
                    String reqFoodCategory = reqFoodItem.getElementsByTagName("category").item(0).getTextContent();
                    float reqPrice = Float.valueOf(reqFoodItem.getElementsByTagName("price").item(0).getTextContent());

                    if (reqCountry.equals("") || reqFoodName.equals("") || reqFoodDesc.equals("") || reqFoodCategory.equals("")) {
                        responseXmlString = INVALID_REQ_RESPONSE;
                    } else {
                        try {
                            int localFoodId = 0;
                            int maxId = 0;
                            FoodItemData foodItemData = (FoodItemData) jaxbUnmarshaller.unmarshal(foodXmlFile);
                            List<FoodItem> foodItems = foodItemData.getFoodItems();

                            // iterating over all food items in xml reqDocument
                            for (FoodItem food : foodItems) {

                                localFoodId = food.getId();
                                String localFoodName = food.getName();
                                String localFoodCategory = food.getCategory();

                                // if food item already exists logic
                                if (reqFoodName.equalsIgnoreCase(localFoodName) && reqFoodCategory.equalsIgnoreCase(localFoodCategory)) {
                                    LOG.info("Food item already exists");
                                    doesFoodExists = true;

                                    // creating FoodItemExists object and marshalling it into string
                                    FoodItemExists foodExists = new FoodItemExists();
                                    List<Integer> existIdsList = new ArrayList<>();
                                    existIdsList.add(localFoodId);
                                    foodExists.setFoodItemId(existIdsList);
                                    responseXmlString = generateXmlString(FoodItemExists.class, foodExists);
                                }
                                // logic to keep track of the max id added to list
                                int id = localFoodId;
                                if (id > maxId) {
                                    maxId = id;
                                }
                            }

                            int nextIdToAdd = maxId + 1;

                            // if food item doesn't exist in the xml logic
                            if (!doesFoodExists) {
                                LOG.info("Food item does not exist");

                                FoodItem newItem = new FoodItem();
                                newItem.setId(nextIdToAdd);
                                newItem.setCountry(reqCountry);
                                newItem.setName(reqFoodName);
                                newItem.setDescription(reqFoodDesc);
                                newItem.setCategory(reqFoodCategory);
                                newItem.setPrice(reqPrice);
                                foodItems.add(newItem);
                                foodItemData.setFoodItems(foodItems);

                                // create JAXB context and instantiate marshaller
                                Marshaller m = jaxbContext.createMarshaller();
                                m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

                                // Write to File
                                m.marshal(foodItemData, new File(foodXmlFile.getAbsolutePath()));

                                // creating FoodItemAdded object for marshalling into xml string
                                FoodItemAdded foodAdded = new FoodItemAdded();
                                List<Integer> addedFoodList = new ArrayList<>();
                                addedFoodList.add(nextIdToAdd);
                                foodAdded.setFoodItemId(addedFoodList);
                                responseXmlString = generateXmlString(FoodItemAdded.class, foodAdded);
                            }

                        } catch (JAXBException | DOMException | NumberFormatException e) {
                            LOG.info(e.getMessage());
                            responseXmlString = INVALID_REQ_RESPONSE;
                        }
                    }
                } else {
                    responseXmlString = INVALID_REQ_RESPONSE;
                }

            } else if (requestRoot.equals(GET_FOOD)) {
                LOG.info("Get food section");

                // unmarshall the stored xml file to FoodItemData object
                FoodItemData foodItemData = (FoodItemData) jaxbUnmarshaller.unmarshal(foodXmlFile);
                List<FoodItem> localFoodItems = foodItemData.getFoodItems();

                // creating RetrievedFoodItems object and marshalling it in string
                RetrievedFoodItems retrievedFoodItems = new RetrievedFoodItems();
                List<FoodItem> validFoodItems = new ArrayList<>();
                InvalidFoodItem invalidFoodItems = new InvalidFoodItem();
                List<Integer> invalidFoodIdList = new ArrayList<>();
                boolean hasInvalidFoodId = false;

                LOG.info("iterating over requested food items");
                NodeList reqFoodItems = reqDocument.getElementsByTagName("FoodItemId");
                if (reqFoodItems != null && reqFoodItems.getLength() > 0) {

                    // iterating over all requested food items
                    for (int index = 0; index < reqFoodItems.getLength(); index++) {

                        boolean doesFoodItemExists = false;
                        Element reqFoodIdElement = (Element) reqFoodItems.item(index);
                        NodeList subList = reqFoodIdElement.getChildNodes();
                        int reqFoodId = 0;

                        if (subList != null && subList.getLength() > 0) {
                            reqFoodId = Integer.valueOf(subList.item(0).getNodeValue());
                            
                            
                        } else {
                            responseXmlString = INVALID_REQ_RESPONSE;
                            break;
                        }

                        // iterating over all existing food items to check whether requested item exists or not
                        for (FoodItem food : localFoodItems) {
                            int localFoodId = food.getId();

                            if (localFoodId == reqFoodId) {
                                doesFoodItemExists = true;

                                // constructing FoodItem object
                                FoodItem foodToRetrieve = new FoodItem();
                                foodToRetrieve.setCountry(food.getCountry());
                                foodToRetrieve.setId(food.getId());
                                foodToRetrieve.setName(food.getName());
                                foodToRetrieve.setDescription(food.getDescription());
                                foodToRetrieve.setCategory(food.getCategory());
                                foodToRetrieve.setPrice(food.getPrice());
                                validFoodItems.add(foodToRetrieve);
                            }
                        }
                        // checking if a food item does not exist
                        if (!doesFoodItemExists) {
                            hasInvalidFoodId = true;
                            invalidFoodIdList.add(reqFoodId);
                        }
                    }
                    if (!hasInvalidFoodId) {
                        retrievedFoodItems.setFoodItems(validFoodItems);
                        retrievedFoodItems.setInvalidFoodItem(null);
                    } else {
                        invalidFoodItems.setFoodItemId(invalidFoodIdList);
                        retrievedFoodItems.setInvalidFoodItem(invalidFoodItems);
                        retrievedFoodItems.setFoodItems(validFoodItems);
                    }
                    responseXmlString = generateXmlString(RetrievedFoodItems.class, retrievedFoodItems);

                } else {
                    responseXmlString = INVALID_REQ_RESPONSE;
                }
            }
        } catch (SAXException | IOException | JAXBException | DOMException | NumberFormatException e) {
            LOG.log(Level.SEVERE, null, e);
            responseXmlString = INVALID_REQ_RESPONSE;
        }

        if (responseXmlString == null) {
            responseXmlString = "something went wrong";
        }
        Response response = Response.status(Response.Status.OK).entity(responseXmlString).build();
        return response;
    }

    private DocumentBuilder getDocBuilderInstance() {
        DocumentBuilder builder = null;
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            builder = docFactory.newDocumentBuilder();
        } catch (ParserConfigurationException ex) {
            LOG.log(Level.SEVERE, null, ex);
        }
        return builder;
    }

    private String generateXmlString(Class instance, Object object) {
        String xmlString = null;
        JAXBContext jaxbContext;
        Marshaller marsh;
        try {
            jaxbContext = JAXBContext.newInstance(instance);
            marsh = jaxbContext.createMarshaller();
            marsh.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

            // Write to xml string
            StringWriter stringWriter = new StringWriter();
            if (object instanceof FoodItemAdded) {
                FoodItemAdded itemAdded = (FoodItemAdded) object;
                marsh.marshal(itemAdded, stringWriter);
            } else if (object instanceof FoodItemExists) {
                FoodItemExists itemExists = (FoodItemExists) object;
                marsh.marshal(itemExists, stringWriter);
            } else if (object instanceof RetrievedFoodItems) {
                RetrievedFoodItems retrieveItems = (RetrievedFoodItems) object;
                marsh.marshal(retrieveItems, stringWriter);
            }
            xmlString = stringWriter.toString();

        } catch (JAXBException ex) {
            Logger.getLogger(FoodResourceServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        return xmlString;
    }

}
