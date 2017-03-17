/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asu.sid.foodmenuserver;

import java.io.File;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
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
    private static String responseXmlString = null;

    public FoodResourceServer() {
        LOG.info("Creating a FoodResource Resource");
    }

    @POST
    @Produces(MediaType.APPLICATION_XML)
    @Consumes(MediaType.APPLICATION_XML)
    public Response addOrGetFoodItem(String foodItem) {
        
        LOG.info("Adding food item");
        LOG.info("Request Content = {}" + foodItem);

        try {
            DocumentBuilder builder = getDocBuilderInstance();
            Document document = builder.parse(new InputSource(new StringReader(foodItem)));
            document.getDocumentElement().normalize();
            String root = document.getDocumentElement().getNodeName();
            LOG.info("Root element: " + root);
            String xmlNamespace = document.getDocumentElement().getAttribute("xmlns");
            LOG.info("Root attribute: " + xmlNamespace);

            if (xmlNamespace == null || !XML_NAMESPACE.equalsIgnoreCase(xmlNamespace)) {
                responseXmlString = "<InvalidMessage xmlns=”http://cse564.asu.edu/PoxAssignment”/>";
            }

            if (root.equals(ADD_FOOD)) {
                NodeList foodList = document.getElementsByTagName("FoodItem");
                for (int i = 0; i < foodList.getLength(); i++) {
                    Element foodElement = (Element) foodList.item(i);
                    String reqFoodName = foodElement.getElementsByTagName("name").item(0).getTextContent();
                    String reqFoodCategory = foodElement.getElementsByTagName("category").item(0).getTextContent();
                    LOG.info("Food name: " + reqFoodName + " and Category: " + reqFoodCategory);

                    // parse the stored xml file to check if the food item is already added or whether it exists
                    ClassLoader classLoader = getClass().getClassLoader();
                    File foodXmlFile = new File(classLoader.getResource("xml/FoodItemData.xml").getFile());

                    DocumentBuilder dBuilder = getDocBuilderInstance();
                    Document doc = dBuilder.parse(foodXmlFile);

                    LOG.info("Root element :" + doc.getDocumentElement().getNodeName());
                    NodeList foodItemList = doc.getElementsByTagName("FoodItem");

                    for (int index = 0; index < foodItemList.getLength(); index++) {
                        Element foodItemElement = (Element) foodItemList.item(index);
                        String itemId = foodItemElement.getElementsByTagName("id").item(0).getTextContent();
                        String localFoodName = foodItemElement.getElementsByTagName("name").item(0).getTextContent();
                        String localFoodCategory = foodItemElement.getElementsByTagName("category").item(0).getTextContent();

                        if (reqFoodName.equalsIgnoreCase(localFoodName) && reqFoodCategory.equalsIgnoreCase(localFoodCategory)) {
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
                        
                        }else{
                            // add new food item into the ezisting xml
                        }
                    }

                }
            } else if (root.equals(GET_FOOD)) {

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        Response response;
        response = Response.status(Response.Status.OK).entity(responseXmlString).build();

        return response;
    }

    private DocumentBuilder getDocBuilderInstance() {
        DocumentBuilder builder = null;
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            builder = docFactory.newDocumentBuilder();
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(FoodResourceServer.class.getName()).log(Level.SEVERE, null, ex);
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
            Logger.getLogger(FoodResourceServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        return xmlString;
    }

}
