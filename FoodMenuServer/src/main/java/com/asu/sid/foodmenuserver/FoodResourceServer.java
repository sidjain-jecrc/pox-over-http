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
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
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
    public Response addOrGetFoodItem(String foodItem) {

        try {
            DocumentBuilder builder = getDocBuilderInstance();
            Document reqDocument = builder.parse(new InputSource(new StringReader(foodItem)));
            reqDocument.getDocumentElement().normalize();
            String requestRoot = reqDocument.getDocumentElement().getNodeName();

            // parse the stored xml file to check if the food item is already added or whether it exists
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

                    try {
                        String localFoodId = null;
                        int maxId = 0;
                        FoodItemData foodItemData = (FoodItemData) jaxbUnmarshaller.unmarshal(foodXmlFile);
                        List<FoodItem> foodItems = foodItemData.getFoodItems();

                        // iterating over all food items in xml reqDocument
                        for (FoodItem food : foodItems) {

                            localFoodId = String.valueOf(food.getId());
                            String localFoodName = food.getName();
                            String localFoodCategory = food.getCategory();

                            // if food item already exists logic
                            if (reqFoodName.equalsIgnoreCase(localFoodName) && reqFoodCategory.equalsIgnoreCase(localFoodCategory)) {
                                LOG.info("Food item already exists");
                                doesFoodExists = true;

                                // creating FoodItemExists object and marshalling it into string
                                FoodItemExists foodExists = new FoodItemExists();
                                List<Integer> existIdsList = new ArrayList<>();
                                existIdsList.add(Integer.valueOf(localFoodId));
                                foodExists.setFoodItemId(existIdsList);

                                JAXBContext existsContext = JAXBContext.newInstance(FoodItemExists.class);
                                Marshaller existsMarshall = existsContext.createMarshaller();
                                existsMarshall.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

                                // Write to String
                                StringWriter foodExistsWriter = new StringWriter();
                                existsMarshall.marshal(foodExists, foodExistsWriter);
                                responseXmlString = foodExistsWriter.toString();
                                LOG.info(foodExistsWriter.toString());
                            }
                            // logic to keep track of the max id added to list
                            int id = Integer.valueOf(localFoodId);
                            if (id > maxId) {
                                maxId = id;
                            }
                        }

                        LOG.log(Level.INFO, "Max food item id: {0}", maxId);
                        int nextIdToAdd = maxId + 1;

                        // if given food item doesn't exist in the xml reqDocument adding it to xml
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

                            // Write to String
                            StringWriter stringWriter = new StringWriter();
                            m.marshal(foodItemData, stringWriter);
                            LOG.info(stringWriter.toString());

                            // Write to File
                            m.marshal(foodItemData, new File(foodXmlFile.getAbsolutePath()));

                            // creating FoodItemAdded object for marshalling into string
                            FoodItemAdded foodAdded = new FoodItemAdded();
                            List<Integer> addedFoodList = new ArrayList<>();
                            addedFoodList.add(nextIdToAdd);
                            foodAdded.setFoodItemId(addedFoodList);

                            JAXBContext existsContext = JAXBContext.newInstance(FoodItemAdded.class);
                            Marshaller existsMarshall = existsContext.createMarshaller();
                            existsMarshall.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

                            // Write to String
                            StringWriter foodAddedWriter = new StringWriter();
                            existsMarshall.marshal(foodAdded, foodAddedWriter);
                            responseXmlString = foodAddedWriter.toString();
                            LOG.info(foodAddedWriter.toString());
                            LOG.info("Food item added");
                        }

                    } catch (JAXBException | DOMException | NumberFormatException e) {
                        LOG.info(e.getMessage());
                        responseXmlString = INVALID_REQ_RESPONSE;
                    }
                } else {
                    responseXmlString = INVALID_REQ_RESPONSE;
                }

            } else if (requestRoot.equals(GET_FOOD)) {
                LOG.info("Get food section");

                // parse the stored xml file to check if the food item exists or not
                FoodItemData foodItemData = (FoodItemData) jaxbUnmarshaller.unmarshal(foodXmlFile);
                List<FoodItem> localFoodItems = foodItemData.getFoodItems();
                LOG.info("Done with unmarshalling");

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
                                LOG.info("Constructing retrieved food xml for food id: " + reqFoodId);
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
                        // if a food item does not exist, creating invalid food item xml response
                        if (!doesFoodItemExists) {
                            LOG.info("Constructing invalid food item xml");
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

                    JAXBContext retrievedContext = JAXBContext.newInstance(RetrievedFoodItems.class);
                    Marshaller retrievedMarshall = retrievedContext.createMarshaller();
                    retrievedMarshall.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

                    // Write to String
                    StringWriter retrievedFoodWriter = new StringWriter();
                    retrievedMarshall.marshal(retrievedFoodItems, retrievedFoodWriter);
                    responseXmlString = retrievedFoodWriter.toString();
                    LOG.info(retrievedFoodWriter.toString());

                } else {
                    responseXmlString = INVALID_REQ_RESPONSE;
                }
            }
        } catch (SAXException | IOException | JAXBException | DOMException | NumberFormatException e) {
            LOG.info("Inside general exception catch block");
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

    private String getStringOfXmlDoc(Document foodDoc) {
        String xmlString = null;
        try {
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            StringWriter writer = new StringWriter();
            transformer.transform(new DOMSource(foodDoc), new StreamResult(writer));
            xmlString = writer.getBuffer().toString().replaceAll("\n|\r", "");
        } catch (TransformerException ex) {
            LOG.log(Level.SEVERE, null, ex);
        }
        return xmlString;
    }

}
