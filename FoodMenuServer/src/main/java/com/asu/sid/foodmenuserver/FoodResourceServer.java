/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asu.sid.foodmenuserver;

import com.asu.sid.beans.FoodItem;
import com.asu.sid.beans.FoodItemData;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
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
    private static final String XML_NAMESPACE = "http://cse564.asu.edu/PoxAssignment";
    private static final String INVALID_REQ_RESPONSE = "<InvalidMessage xmlns=”http://cse564.asu.edu/PoxAssignment”/>";
    private static String responseXmlString = null;

    public FoodResourceServer() {
        LOG.info("Creating a FoodResource Resource");
    }

    @POST
    @Produces(MediaType.APPLICATION_XML)
    @Consumes(MediaType.APPLICATION_XML)
    public Response addOrGetFoodItem(String foodItem) {

        boolean isValid = true;
        try {
            DocumentBuilder builder = getDocBuilderInstance();
            Document reqDocument = builder.parse(new InputSource(new StringReader(foodItem)));
            reqDocument.getDocumentElement().normalize();
            String root = reqDocument.getDocumentElement().getNodeName();
            String xmlNamespace = reqDocument.getDocumentElement().getAttribute("xmlns");

            // parse the stored xml file to check if the food item is already added or whether it exists
            ClassLoader classLoader = getClass().getClassLoader();
            File foodXmlFile = new File(classLoader.getResource("xml/FoodItemData.xml").getFile());

            JAXBContext jaxbContext = JAXBContext.newInstance(FoodItemData.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

            if (!XML_NAMESPACE.equalsIgnoreCase(xmlNamespace)) {
                LOG.info("XML Namespace not equal");
                responseXmlString = INVALID_REQ_RESPONSE;
                isValid = false;

            } else if (root.equals(ADD_FOOD) && isValid) {
                LOG.info("Add food section");
                boolean doesFoodExists = false;
                
                Element reqFoodElement = (Element) reqDocument.getElementsByTagName("FoodItem");
                String reqCountry = reqFoodElement.getAttribute("country");
                String reqFoodName = reqFoodElement.getElementsByTagName("name").item(0).getTextContent();
                String reqFoodDesc = reqFoodElement.getElementsByTagName("description").item(0).getTextContent();
                String reqFoodCategory = reqFoodElement.getElementsByTagName("category").item(0).getTextContent();
                float reqPrice = Float.valueOf(reqFoodElement.getElementsByTagName("price").item(0).getTextContent());

                try {
                    String itemId = null;
                    int maxId = 0;
                    FoodItemData foodItemData = (FoodItemData) jaxbUnmarshaller.unmarshal(foodXmlFile);
                    List<FoodItem> foodItems = foodItemData.getFoodItems();

                    // iterating over all food items in xml reqDocument
                    for (FoodItem food : foodItems) {
                        LOG.info("Iterating over all food items");

                        itemId = String.valueOf(food.getId());
                        String localFoodName = food.getName();
                        String localFoodCategory = food.getCategory();

                        // if food item already exists logic
                        if (reqFoodName.equalsIgnoreCase(localFoodName) && reqFoodCategory.equalsIgnoreCase(localFoodCategory)) {
                            LOG.info("Food item already exists");
                            doesFoodExists = true;

                            // create an FoodItemExists xml
                            DocumentBuilder existBuilder = getDocBuilderInstance();
                            Document foodExistsDoc = existBuilder.newDocument();

                            Element itemExistsElement = foodExistsDoc.createElement("FoodItemExists");
                            itemExistsElement.setAttribute("xmlns", "http://cse564.asu.edu/PoxAssignment");
                            foodExistsDoc.appendChild(itemExistsElement);

                            Element idElement = foodExistsDoc.createElement("FoodItemId");
                            idElement.appendChild(foodExistsDoc.createTextNode(itemId));
                            itemExistsElement.appendChild(idElement);

                            responseXmlString = getStringOfXmlDoc(foodExistsDoc);
                        }
                        // logic to keep track of the max id added to list
                        int id = Integer.valueOf(itemId);
                        if (id > maxId) {
                            maxId = id;
                        }
                    }

                    LOG.log(Level.INFO, "Max food item id: {0}", maxId);
                    int foodIdToAdd = maxId + 1;
                    // if given food item doesn't exist in the xml reqDocument adding it to xml
                    if (!doesFoodExists && isValid) {
                        LOG.info("Food item does not exist");

                        FoodItem newItem = new FoodItem();
                        newItem.setId(foodIdToAdd);
                        newItem.setCountry(reqCountry);
                        newItem.setName(reqFoodName);
                        newItem.setDescription(reqFoodDesc);
                        newItem.setPrice(reqPrice);
                        foodItems.add(newItem);
                        foodItemData.setFoodItems(foodItems);

                        // create JAXB context and instantiate marshaller
                        JAXBContext context = JAXBContext.newInstance(FoodItemData.class);
                        Marshaller m = context.createMarshaller();
                        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

                        LOG.info("xml file path: " + foodXmlFile.getAbsolutePath());

                        // Write to File
                        m.marshal(foodItemData, new File(foodXmlFile.getAbsolutePath()));

                        // creating food item added response xml
                        DocumentBuilder newAddedBuilder = getDocBuilderInstance();
                        Document foodAddedDoc = newAddedBuilder.newDocument();

                        Element itemAddedElement = foodAddedDoc.createElement("FoodItemAdded");
                        itemAddedElement.setAttribute("xmlns", "http://cse564.asu.edu/PoxAssignment");
                        foodAddedDoc.appendChild(itemAddedElement);

                        Element idElement = foodAddedDoc.createElement("FoodItemId");
                        idElement.appendChild(foodAddedDoc.createTextNode(String.valueOf(foodIdToAdd)));
                        itemAddedElement.appendChild(idElement);

                        responseXmlString = getStringOfXmlDoc(foodAddedDoc);
                        LOG.info("Food item added");
                    }

                } catch (JAXBException e) {
                    LOG.info(e.getMessage());
                    responseXmlString = INVALID_REQ_RESPONSE;
                }

            } else if (root.equals(GET_FOOD) && isValid) {
                LOG.info("Get food section");

                // parse the stored xml file to check if the food item exists or not
                FoodItemData foodItemData = (FoodItemData) jaxbUnmarshaller.unmarshal(foodXmlFile);
                List<FoodItem> foodItems = foodItemData.getFoodItems();
                LOG.info("Done with unmarshalling");

                // creating retrieved food items response xml
                DocumentBuilder getFoodDocBuilder = getDocBuilderInstance();
                Document retrievedFoodDoc = getFoodDocBuilder.newDocument();

                Element retrievedFoodRoot = retrievedFoodDoc.createElement("RetrievedFoodItems");
                retrievedFoodRoot.setAttribute("xmlns", "http://cse564.asu.edu/PoxAssignment");
                retrievedFoodDoc.appendChild(retrievedFoodRoot);

                try {
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
                            }

                            // iterating over all existing food items to check whether requested item exists or not
                            for (FoodItem food : foodItems) {
                                int localFoodId = food.getId();
                                if (localFoodId == reqFoodId) {
                                    LOG.info("Constructing retrieved food xml for food id: " + reqFoodId);
                                    doesFoodItemExists = true;

                                    Element retrievedFoodItem = retrievedFoodDoc.createElement("FoodItem");
                                    retrievedFoodRoot.appendChild(retrievedFoodItem);
                                    retrievedFoodItem.setAttribute("country", food.getCountry());

                                    Element retrievedFoodId = retrievedFoodDoc.createElement("id");
                                    retrievedFoodId.appendChild(retrievedFoodDoc.createTextNode(String.valueOf(reqFoodId)));
                                    retrievedFoodItem.appendChild(retrievedFoodId);

                                    Element foodName = retrievedFoodDoc.createElement("name");
                                    foodName.appendChild(retrievedFoodDoc.createTextNode(food.getName()));
                                    retrievedFoodItem.appendChild(foodName);

                                    Element foodDesc = retrievedFoodDoc.createElement("description");
                                    foodDesc.appendChild(retrievedFoodDoc.createTextNode(food.getDescription()));
                                    retrievedFoodItem.appendChild(foodDesc);

                                    Element foodCategory = retrievedFoodDoc.createElement("category");
                                    foodCategory.appendChild(retrievedFoodDoc.createTextNode(food.getCategory()));
                                    retrievedFoodItem.appendChild(foodCategory);

                                    Element foodPrice = retrievedFoodDoc.createElement("price");
                                    foodPrice.appendChild(retrievedFoodDoc.createTextNode(String.valueOf(food.getPrice())));
                                    retrievedFoodItem.appendChild(foodPrice);
                                }
                            }
                            // if a food item does not exist, creating invalid food item xml response
                            if (!doesFoodItemExists) {
                                LOG.info("Constructing invalid food item xml");
                                Element invalidFoodItem = retrievedFoodDoc.createElement("InvalidFoodItem");
                                retrievedFoodRoot.appendChild(invalidFoodItem);

                                Element invalidFoodId = retrievedFoodDoc.createElement("FoodItemId");
                                invalidFoodId.appendChild(retrievedFoodDoc.createTextNode(String.valueOf(reqFoodId)));
                                invalidFoodItem.appendChild(invalidFoodId);
                            }
                        }
                    } else {
                        responseXmlString = INVALID_REQ_RESPONSE;
                    }
                    responseXmlString = getStringOfXmlDoc(retrievedFoodDoc);

                } catch (NumberFormatException e) {
                    LOG.info("Inside number format exception catch block");
                    LOG.log(Level.SEVERE, e.getMessage());
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
