package Controller;

import io.javalin.Javalin;
import io.javalin.http.Context;
import java.util.List;

import Model.Account;
import Model.Message;
import Service.SocialMediaService;

/**
 * TODO: You will need to write your own endpoints and handlers for your controller. The endpoints you will need can be
 * found in readme.md as well as the test cases. You should
 * refer to prior mini-project labs and lecture materials for guidance on how a controller may be built.
 */
public class SocialMediaController {
    /**
     * In order for the test cases to work, you will need to write the endpoints in the startAPI() method, as the test
     * suite must receive a Javalin object from this method.
     * @return a Javalin app object which defines the behavior of the Javalin controller.
     */

     //social media service object
     SocialMediaService socialMediaService;

     //constructor
     public SocialMediaController(SocialMediaService socialMediaService) {
         this.socialMediaService = socialMediaService;
         
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
        app.get("/messages/:messageId", this::getMessageHandler);
        app.get("/messages/:messageId", this::getMessagesForUserHandler);
        app.delete("/messages/:messageId", this::deleteMessageHandler);
        app.patch("/messages/:messageId", this::updateMessageHandler);

        //return app
        return app;
    }

    /**
     * This is an example handler for an example endpoint.
     * @param context The Javalin Context object manages information about both the HTTP request and response.
     */

     //handler for registering a new user
    private void registerUserHandler(Context context) {
        //retrieve username and password from context
        String username = context.formParam("username");
        String password = context.formParam("password");

        //if username and password are not null and not empty, else return error 400
        if (username != null && !username.isEmpty() && password != null && !password.isEmpty()) {
            //create account object using username and password
            Account account = socialMediaService.createAccount(username, password);
            //if account is not null
            if(account != null)
            {
                //return account object
                context.json(account);
            }
            else
            {
                //return error 400
                context.status(400);
            }
        }
        else
        {
            //return error 400
            context.status(400);
        }
    }

    //handler for validating username and password
    private void loginUserHandler(Context context) 
    {
        //retrieve username and password from context
        String username = context.formParam("username");
        String password = context.formParam("password");
        
        //if username and password are not null and not empty, else return error 400
        if(username != null && !username.isEmpty() && password != null && !password.isEmpty())
        {
            //create account object using username
            boolean isValid = socialMediaService.validatePassword(username, password);
            //if account is not null and password matches stored password
            if(isValid)
            {
                Account account = socialMediaService.getAccountByUsername(username);
                //return account object
                context.json(account);
            }
            else
            {
                //return error 401
                context.status(401);
            }
        }
        else
        {
            //return error 400
            context.status(400);
        }
    }

    //handler for creating a message
    private void createMessageHandler(Context context)
    {
        //retrieve messageText and postedBy from context
        String messageText = context.formParam("messageText");
        int postedBy = Integer.parseInt(context.formParam("postedBy"));

        //if messageText is not null, not empty, and message length is less than or equal to 255
        if(messageText != null && !messageText.isBlank() && messageText.length() <= 255)
        {
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
    private void getMessageHandler(Context context)
    {
        //retrieve messageId from context
        int messageId = Integer.parseInt(context.pathParam("messageId"));
        //get message by message id
        Message message = socialMediaService.getMessageById(messageId);

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
    private void getMessagesForUserHandler(Context context)
    {
        //retrieve accountId from context
        int accountId = Integer.parseInt(context.pathParam("accountId"));
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
    private void deleteMessageHandler(Context context)
    {
        //retrieve messageId from context
        int messageId = Integer.parseInt(context.pathParam("messageId"));
        //delete message, store boolean true if successful, false if not
        Message deletedMessage = socialMediaService.getMessageById(messageId);

        //if deleted message was found
        if(deletedMessage != null)
        {
            //delete message and store boolean true if successful, false if not
            boolean deleted = socialMediaService.deleteMessage(messageId);
            if(deleted)
            {
                //set status 200 and return deleted message
                context.status(200).json(deletedMessage);
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

        //if messageText is not null, not empty, and message length is less than or equal to 255
        if(messageText != null && !messageText.isBlank() && messageText.length() <= 255)
        {
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
        else
        {
            //return error 400
            context.status(400).result("Message not updated");
        }
        
    }
}