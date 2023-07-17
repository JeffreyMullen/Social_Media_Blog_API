package Controller;

import io.javalin.Javalin;
import io.javalin.http.Context;
import java.util.List;

import Model.Account;
import Model.Message;
import Service.SocialMediaService;
import Service.SocialMediaServiceImpl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;


public class SocialMediaController {
    /**
     * In order for the test cases to work, you will need to write the endpoints in the startAPI() method, as the test
     * suite must receive a Javalin object from this method.
     * @return a Javalin app object which defines the behavior of the Javalin controller.
     */

     //social media service object
     SocialMediaService socialMediaService;

     //constructor
     public SocialMediaController() {
         this.socialMediaService = new SocialMediaServiceImpl();
         
     }

     //method to start the Javalin app
    public Javalin startAPI() {
        //create a Javalin app
        Javalin app = Javalin.create();

        //register endpoints
        app.post("/register", this::registerUserHandler);
        app.post("/login", this::loginUserHandler);
        app.post("/messages", this::createMessageHandler);
        app.get("/messages", this::getAllMessagesHandler);
        app.get("/messages/{messageId}", this::getMessageHandler);
        app.get("/accounts/{accountId}/messages", this::getMessagesForUserHandler);
        app.delete("/messages/{messageId}", this::deleteMessageHandler);
        app.patch("/messages/{messageId}", this::updateMessageHandler);

        //app.start(8080);
        //return app
        return app;
    }

    /**
     * This is an example handler for an example endpoint.
     * @param context The Javalin Context object manages information about both the HTTP request and response.
     */

     //handler for registering a new user
    private void registerUserHandler(Context context) throws JsonProcessingException 
    {
        //create new mapper object
        ObjectMapper mapper = new ObjectMapper();
                //create new account object, retrieve username and password from context
        Account account = mapper.readValue(context.body(), Account.class);
                //retrieve username and password from account object
        String username = account.getUsername();
        String password = account.getPassword();
                account = socialMediaService.createAccount(username, password);
                //if createdAccount is not null
        if(account != null)
        {
            //respond successful and return account object
            context.status(200).json(account);
        }
        else
        {
            //return error 400
            context.status(400);
        }
    }

    //handler for validating username and password
    private void loginUserHandler(Context context) throws JsonProcessingException
    {
        ObjectMapper mapper = new ObjectMapper();

        Account account = mapper.readValue(context.body(), Account.class);
        //retrieve username and password from context
        String username = account.getUsername();
        String password = account.getPassword();
        
        //create account object using username
        boolean isValid = socialMediaService.validatePassword(username, password);
        //if account is not null and password matches stored password
        if(isValid)
        {
            account = socialMediaService.getAccountByUsername(username);
            //return account object
            context.json(account);
        }
        else
        {
            //return error 401
            context.status(401);
        }
    }

    //handler for creating a message
    private void createMessageHandler(Context context) throws JsonProcessingException
    {
        ObjectMapper mapper = new ObjectMapper();

        Message message = mapper.readValue(context.body(), Message.class);

        //retrieve messageText and postedBy from context
        String messageText = message.getMessage_text();
        int postedBy = message.getPosted_by();
        
        //create message
        Message createdMessage = socialMediaService.createMessage(postedBy, messageText, System.currentTimeMillis());

        //if created message is not null
        if(createdMessage != null)
        {
            //set message_id and create json with status 200
            createdMessage.setMessage_id(socialMediaService.getLastCreatedMessageId());
            context.status(200).json(createdMessage);
        }
        else
        {
            //return error 400
            context.status(400);
        }
    }

    //getAllMessagesHandler
    private void getAllMessagesHandler(Context context)
    {
        //get all messages
        List<Message> messages = socialMediaService.getAllMessages();

        //if messages is not empty
        if(messages.size() > 0)
        {
            //set status 200 and return messages
            context.status(200).json(messages);
        }
        else
        {
            //return 200 with no messages
            context.status(200);
        }
    }

    //getMessageHandler
    private void getMessageHandler(Context context) throws JsonProcessingException
    {
        ObjectMapper mapper = new ObjectMapper();

        Message message = mapper.readValue(context.body(), Message.class);


        //retrieve messageId from context
        int messageId = message.getMessage_id();
        //get message by message id
        message = socialMediaService.getMessageById(messageId);

        //if message is not null
        if(message != null)
        {
            //set status 200 and return message
            context.status(200).json(message);
        }
        else
        {
            //return error 200 with no message
            context.status(200);
        }
    }

    //getMessagesForUserHandler
    private void getMessagesForUserHandler(Context context) throws JsonProcessingException
    {
        ObjectMapper mapper = new ObjectMapper();

        Message message = mapper.readValue(context.body(), Message.class);

        //retrieve accountId from context
        int accountId = message.getPosted_by();
        //get all messages for user
        List<Message> messages = socialMediaService.getAllMessagesForUser(accountId);

        //if messages is not empty
        if(messages.size() > 0)
        {
            //set status 200 and return messages
            context.status(200).json(messages);
        }
        else
        {
            //return 200 with no messages
            context.status(200);
        }
    }

    //deleteMessageHandler
    private void deleteMessageHandler(Context context) throws JsonProcessingException
    {
        ObjectMapper mapper = new ObjectMapper();

        Message message = mapper.readValue(context.body(), Message.class);

        //retrieve messageId from context
        int messageId = message.getMessage_id();
        //delete message, store boolean true if successful, false if not
        Message deleteMessage = socialMediaService.getMessageById(messageId);

        //if deleted message was found
        if(deleteMessage != null)
        {
            //delete message and store boolean true if successful, false if not
            boolean deleted = socialMediaService.deleteMessage(messageId);
            if(deleted)
            {
                //set status 200 and return deleted message
                context.status(200).json(deleteMessage);
            }
            else
            {
                context.status(200);
            }
        }
        else
        {
            context.status(200);
        }
    }

    //updateMessageHandler
    private void updateMessageHandler(Context context)
    {
        //retrieve messageId from context
        int messageId = Integer.parseInt(context.pathParam("messageId"));
        //retrieve messageText from context
        String messageText = context.body();

        //update message, true if successful, false if not
        boolean updated = socialMediaService.updateMessage(messageId, messageText);

        //if updated
        if(updated)
        {
            //set status 200 and return updated message
            Message updatedMessage = socialMediaService.getMessageById(messageId);
            context.status(200).json(updatedMessage);
        }
        else
        {
            //return error 400
            context.status(400).result("Message not updated");
        }        
    }
}