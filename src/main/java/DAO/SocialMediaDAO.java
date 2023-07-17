package DAO;

import Model.Account;
import Model.Message;
import java.util.List;

public interface SocialMediaDAO 
{
    //Account CRUD (create, read, update, delete)
    Account createAccount(String username, String password);
    Account getAccountByUsername(String username);
    //boolean validatePassword(String username, String password);
    
    //Message CRUD (create, read, update, delete)
    Message createMessage(int account_id, String message_text, long time_posted_epoch);
    int getLastCreatedMessageId();
    Message getMessageById(int message_id);
    List<Message> getAllMessages();
    List<Message> getAllMessagesForUser(int account_id);
    boolean updateMessage(int message_id, String new_text);
    Message deleteMessage(int message_id);
}