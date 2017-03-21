# Plain Old Xml (POX) over HTTP

### This is a client-server application that communicate via XML messages transmitted via HTTP

In this application, we have a FoodItemData.xml containing information of a food item in an xml format. 

The objective was to create a client-server application that user single URI and HTTP verb(post) to "add" or "get" food items from server.  

Development IDE - Netbeans 8.1

Server - GlassFish Server 4.1.1

Java - JDK 1.8

Instructions for running the client-server application:-

1. Open both these projects in Netbeans IDE.

2. Clean and Build both the projects.

3. Run FoodMenuServer web application on GlassFish Server 4.1.1.

4. Run FoodMenuClient application, it will run MainClass.java (Go to console window)

5. In the console window of FoodMenuClient, it will ask "would you like to add or get food item from server?".

6. If you type "add", it will directly ask you to provide one food item's information. 
     Once you are done with filling in the information, it will create a request XML and print it on console before sending request to the server. 
     After response is received from server, it will print that on console.

7. If you type "get", it will ask "how many items would you like to retrieve?". 
     Type in the number and fill in the other requested information. 
     Once you are done with that, it will again print the request XML on console for you to verify.
     After response is received from server, it will print that on console.

8. In the end, it will ask "would like to continue or not?" You can type in "no" or "n" to discontinue, otherwise it will again start from step 5.

END NOTE - I am not persisting xml changes onto file but memory. So, you can test the changes in one server session by running client application multiple times.

