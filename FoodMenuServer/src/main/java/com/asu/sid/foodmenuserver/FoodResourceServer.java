/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asu.sid.foodmenuserver;

import com.asu.sid.beans.FoodItem;
import com.asu.sid.beans.FoodItemData;
import java.io.File;
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
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

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
            Document document = builder.parse(new InputSource(new StringReader(foodItem)));
            document.getDocumentElement().normalize();
            String root = document.getDocumentElement().getNodeName();
            String xmlNamespace = document.getDocumentElement().getAttribute("xmlns");

            if (!XML_NAMESPACE.equalsIgnoreCase(xmlNamespace)) {
                LOG.info("XML Namespace not equal");
                responseXmlString = INVALID_REQ_RESPONSE;
                isValid = false;
            }

            if (root.equals(ADD_FOOD) && isValid) {
                LOG.info("Add food section");
                boolean doesExists = false;
                NodeList foodList = document.getElementsByTagName("FoodItem");

                for (int i = 0; i < foodList.getLength(); i++) {
                    Element foodElement = (Element) foodList.item(i);
                    String reqCountry = foodElement.getAttribute("country");
                    String reqFoodName = foodElement.getElementsByTagName("name").item(0).getTextContent();
                    String reqFoodDesc = foodElement.getElementsByTagName("description").item(0).getTextContent();
                    String reqFoodCategory = foodElement.getElementsByTagName("category").item(0).getTextContent();
                    float reqPrice = Float.valueOf(foodElement.getElementsByTagName("price").item(0).getTextContent());

                    // parse the stored xml file to check if the food item is already added or whether it exists
                    ClassLoader classLoader = getClass().getClassLoader();
                    File foodXmlFile = new File(classLoader.getResource("xml/FoodItemData.xml").getFile());

                    try {
                        JAXBContext jaxbContext = JAXBContext.newInstance(FoodItemData.class);
                        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
                        FoodItemData foodItemData = (FoodItemData) jaxbUnmarshaller.unmarshal(foodXmlFile);
                        List<FoodItem> foodItems = foodItemData.getFoodItems();
                        String itemId = null;
                        int maxId = 0;

                        // iterating over all food items in xml document
                        for (FoodItem food : foodItems) {
                            LOG.info(food.getName());

                            itemId = String.valueOf(food.getId());
                            String localFoodName = food.getName();
                            String localFoodCategory = food.getCategory();

                            // if food item already exists logic
                            if (reqFoodName.equalsIgnoreCase(localFoodName) && reqFoodCategory.equalsIgnoreCase(localFoodCategory)) {
                                LOG.info("Food item already exists");
                                doesExists = true;

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
                        // if given food item doesn't exist in the xml document adding it to xml
                        if (!doesExists && isValid) {
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

                            LOG.info("xml file path: " + classLoader.getResource("xml/FoodItemData.xml").getPath());

                            // Write to File
                            m.marshal(foodItemData, new File(classLoader.getResource("xml/FoodItemData.xml").getPath()));
                            m.marshal(foodItemData, new File("./FoodItemData.xml"));


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
                    }
                }
            } else if (root.equals(GET_FOOD) && isValid) {

            }

        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
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
