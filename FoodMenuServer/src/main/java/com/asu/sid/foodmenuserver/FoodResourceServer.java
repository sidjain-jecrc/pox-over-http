/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asu.sid.foodmenuserver;

import java.io.File;
import java.io.StringReader;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.CharacterData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/**
 *
 * @author Siddharth
 */
@Path("inventory")
public class FoodResourceServer {

    private static final Logger LOG = LoggerFactory.getLogger(FoodResourceServer.class);
    private static final String ADD_FOOD = "NewFoodItems";
    private static final String GET_FOOD = "SelectedFoodItems";
    
    public FoodResourceServer() {
        LOG.info("Creating a FoodResource Resource");
    }

    @POST
    @Produces(MediaType.APPLICATION_XML)
    @Consumes(MediaType.APPLICATION_XML)
    public Response addOrGetFoodItem(String foodItem) {
        LOG.info("Adding food item");
        LOG.info("Request Content = {}", foodItem);

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;
        String responseXML = null;
        try {
            builder = factory.newDocumentBuilder();
            Document document = builder.parse(new InputSource(new StringReader(foodItem)));
            document.getDocumentElement().normalize();
            String root = document.getDocumentElement().getNodeName();
            LOG.info("Root element: " + root);
            
            if(root.equals(ADD_FOOD)){
                
            }else if(root.equals(GET_FOOD)){
                
            }else{
                // return error XML
            }
            
            // parse the stored xml file to check if the food item is already added or whether it exists
            NodeList nList = document.getElementsByTagName("FoodItem");
            for (int i = 0; i < nList.getLength(); i++) {
                Element element = (Element) nList.item(i);

                NodeList name = element.getElementsByTagName("name");
                Element line = (Element) name.item(0);
                System.out.println("Name: " + getCharacterDataFromElement(line));

                NodeList title = element.getElementsByTagName("title");
                line = (Element) title.item(0);
                System.out.println("Title: " + getCharacterDataFromElement(line));
                
                File foodXmlFile = new File("FoodItemData.xml");
            
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        Response response;
        response = Response.status(Response.Status.OK).entity(responseXML).build();

        return response;
    }

    public static String getCharacterDataFromElement(Element e) {
        Node child = e.getFirstChild();
        if (child instanceof CharacterData) {
            CharacterData cd = (CharacterData) child;
            return cd.getData();
        }
        return "";
    }

}
