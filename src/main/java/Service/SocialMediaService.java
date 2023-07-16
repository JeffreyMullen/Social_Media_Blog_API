package Service;

import java.util.List;

import Model.Account;
import Model.Message;

public interface SocialMediaService
{
    //Account CRUD (create, read, update, delete)
    Account createAccount(String username, String password);
    Account getAccountByUsername(String username);
    boolean validatePassword(String username, String password);

    //Message CRUD (create, read, update, delete)
    Message createMessage(int account_id, String message_text, long time_posted_epoch);
    int getLastCreatedMessageId();
    Message getMessageById(int message_id);
    List<Message> getAllMessages();
    List<Message> getAllMessagesForUser(int account_id);
    boolean updateMessage(int message_id, String new_text);
    boolean deleteMessage(int message_id);
}