/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asu.sid.foodmenuserver;

import com.asu.sid.beans.FoodItemData;
import java.io.File;
import java.io.StringReader;
import java.util.logging.Logger;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
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

    public FoodResourceServer() {
        LOG.info("Creating a FoodResource Resource");
    }

    @POST
    @Produces(MediaType.APPLICATION_XML)
    @Consumes(MediaType.APPLICATION_XML)
    public Response addOrGetFoodItem(String foodItem) {
        LOG.info("Adding food item");
        LOG.info("Request Content = {}" + foodItem);

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;
        String responseXML = null;
        try {
            builder = factory.newDocumentBuilder();
            Document document = builder.parse(new InputSource(new StringReader(foodItem)));
            document.getDocumentElement().normalize();
            String root = document.getDocumentElement().getNodeName();
            LOG.info("Root element: " + root);
            String xmlNamespace = document.getDocumentElement().getAttribute("xmlns");
            LOG.info("Root attribute: " + xmlNamespace);

            if (root == null || !root.equals(ADD_FOOD) || !root.equals(GET_FOOD) || xmlNamespace == null || !XML_NAMESPACE.equalsIgnoreCase(XML_NAMESPACE)) {
                // return error xml
            }

            if (root.equals(ADD_FOOD)) {
                NodeList foodList = document.getElementsByTagName("FoodItem");
                for (int i = 0; i < foodList.getLength(); i++) {
                    Element foodElement = (Element) foodList.item(i);
                    String foodName = foodElement.getElementsByTagName("name").item(0).getTextContent();
                    String foodCategory = foodElement.getElementsByTagName("category").item(0).getTextContent();
                    LOG.info("Food name: " + foodName + " and Category: " + foodCategory);

                    // parse the stored xml file to check if the food item is already added or whether it exists
                    ClassLoader classLoader = getClass().getClassLoader();
                    File foodXmlFile = new File(classLoader.getResource("xml/FoodItemData.xml").getFile());

                    DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                    DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                    Document doc = dBuilder.parse(foodXmlFile);

//                    JAXBContext jaxbContext = JAXBContext.newInstance(FoodItemData.class);
//                    Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
//                    FoodItemData foodItemData = (FoodItemData) jaxbUnmarshaller.unmarshal(foodXmlFile);
//                    LOG.info("" + foodItemData);
                }
            } else if (root.equals(GET_FOOD)) {

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        Response response;
        response = Response.status(Response.Status.OK).entity(responseXML).build();

        return response;
    }

}
