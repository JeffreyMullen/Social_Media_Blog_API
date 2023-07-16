package Service;

import java.util.List;

import Model.Account;
import Model.Message;
import DAO.SocialMediaDAO;


public class SocialMediaServiceImpl implements SocialMediaService {

    //SocialMediaDAO object
    private SocialMediaDAO socialDAO;

    //constructor
    public SocialMediaServiceImpl(SocialMediaDAO socialDAO)
    {
        this.socialDAO = socialDAO;
    }
    
    //create account
    public Account createAccount(String username, String password)
    {
        //interact with DAO and return created account
        return socialDAO.createAccount(username, password);
    }

    //get account by username
    public Account getAccountByUsername(String username)
    {
        //interact with DAO and return account
        return socialDAO.getAccountByUsername(username);
    }
    
    //validate login credentials
    public boolean validatePassword(String username, String password)
    {
        //create account object and retrieve account from DAO
        Account account = socialDAO.getAccountByUsername(username);

        //return true if username and password are valid, else return false
        return account != null && account.getUsername().equals(username) && account.getPassword().equals(password);
    }

    //create message
    public Message createMessage(int account_id, String message_text, long time_posted_epoch)
    {
        //interact with DAO and return created message
        return socialDAO.createMessage(account_id, message_text, time_posted_epoch);
    }

    //get last created message id
    public int getLastCreatedMessageId()
    {
        //interact with DAO and return last created message id
        return socialDAO.getLastCreatedMessageId();
    }

    //get message by message id
    public Message getMessageById(int message_id)
    {
        //interact with DAO and return message
        return socialDAO.getMessageById(message_id);
    }

    //get all messages
    public List<Message> getAllMessages()
    {
        //interact with DAO and return all messages
        return socialDAO.getAllMessages();
    }

    //get all messages for user
    public List<Message> getAllMessagesForUser(int account_id)
    {
        //interact with DAO and return all messages
        return socialDAO.getAllMessagesForUser(account_id);
    }

    //update message
    public boolean updateMessage(int message_id, String new_text)
    {
        //interact with DAO and return boolean
        return socialDAO.updateMessage(message_id, new_text);
    }

    //delete message
    public boolean deleteMessage(int message_id)
    {
        //interact with DAO and return boolean
        return socialDAO.deleteMessage(message_id);
    }
}