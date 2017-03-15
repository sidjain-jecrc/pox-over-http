/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asu.sid.foodmenuclient;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Siddharth
 */
public class FoodResourceClient {

    private static final Logger LOG = LoggerFactory.getLogger(FoodResourceClient.class);

    private WebResource webResource = null;
    private Client client;
    private static final String BASE_URI = "http://localhost:8080/FoodMenuServer/webapi";

    public FoodResourceClient() {
        LOG.info("Creating a Food REST Client");

        ClientConfig config = new DefaultClientConfig();
        client = Client.create(config);

        webResource = client.resource(BASE_URI).path("inventory");
        LOG.debug("webResource = {}", webResource.getURI());
    }

    public String addOrGetFoodItem(String foodItem) throws UniformInterfaceException {
        String result = null;
        System.out.println("Sending food item \"" + foodItem + "\" to the food server");
        ClientResponse response = webResource.type(MediaType.APPLICATION_XML).accept(MediaType.APPLICATION_XML).post(ClientResponse.class, foodItem);
        if (response.getStatus() != 200) {
            throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
        } else{
            result = response.toString();
        }
        return result;
    }

    public void close() {
        LOG.info("Closing the REST Client");
        client.destroy();
    }

}
