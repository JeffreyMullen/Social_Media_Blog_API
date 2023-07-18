package DAO;

import java.util.List;
import Util.ConnectionUtil;
import java.sql.*;
import java.util.ArrayList;
import Model.Account;
import Model.Message;

public class SocialMediaDAOImpl implements SocialMediaDAO
{
    //create account
    @Override
    public Account createAccount(String username, String password)
    {
        //if username and password are not null and not empty
        if(username == null || username.isEmpty() || password == null || password.isEmpty())
        {
            return null;
        }

        //if username is less than 4 characters
        if(password.length() < 4)
        {
            return null;
        }

        //attempt to retrieve account from database
        Account account = getAccountByUsername(username);

        //if account exists
        if(account != null)
        {
            return null;
        }

        //create connection to database
        Connection connection = ConnectionUtil.getConnection();

        //try catch block
        try
        {
            //change sql statement to insert new account username and password
            String sql = "INSERT INTO account (username, password) VALUES (?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            //set values of sql prepared statement
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);

            //execute sql statement and store the number of generated keys
            int affectedRows = preparedStatement.executeUpdate();

            //if at least one key was generated
            if(affectedRows > 0)
            {
                //get generated keys
                try(ResultSet generatedKeys = preparedStatement.getGeneratedKeys())
                {
                    //if at least one key was generated
                    if(generatedKeys.next())
                    {
                        //get generated keys and store as account_id
                        int account_id = generatedKeys.getInt(1);
                        //return new account object
                        account = new Account(account_id, username, password);
                    }
                }
            }
            //throw exception if no rows were affected
            else
            {
                throw new SQLException("Creating account failed, no rows affected.");
            }
        }
        //catch exception if connection fails
        catch(SQLException e)
        {
            System.out.println("Error creating account: " + e.getMessage());
        }
        //return account, set to null on failure
        return account;
    }

    //get account by username
    @Override
    public Account getAccountByUsername(String username)
    {
        //create null account
        Account account = null;

        //create connection to database
        Connection connection = ConnectionUtil.getConnection();

        //try catch block
        try
        {
            //create sql prepared statement to find accounts with matching username
            String sql = "SELECT * FROM account WHERE username = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);

            //set values of sql prepared statement
            preparedStatement.setString(1, username);

            //execute sql statement and get results, if any
            ResultSet resultSet = preparedStatement.executeQuery();

            //if there are results, return them
            if(resultSet.next())
            {
                //get results and store account_id and password
                int accountId = resultSet.getInt("account_id");
                String password = resultSet.getString("password");
                //return new account object
                return new Account(accountId, username, password);
            }
        }
        //catch exception if connection fails
        catch(SQLException e)
        {
            System.out.println("Error finding account: " + e.getMessage());
        }
        //return null if no results
        return account;
    }

    //create message
    @Override
    public Message createMessage(int account_id, String message_text, long time_posted_epoch)
    {
        //create connection to database
        Connection connection = ConnectionUtil.getConnection();

        //try catch block
        try
        {
            //create sql prepared statement to insert values into message table
            String sql = "INSERT INTO message (posted_by, message_text, time_posted_epoch) VALUES (?, ?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            //set values of sql prepared statement
            preparedStatement.setInt(1, account_id);
            preparedStatement.setString(2, message_text);
            preparedStatement.setLong(3, time_posted_epoch);

            //execute sql statement and store results, if any
            int affectedRows = preparedStatement.executeUpdate();

            //if at least one key was generated
            if(affectedRows > 0)
            {
                //get generated keys
                ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
                //iterated through keys
                if(generatedKeys.next())
                {
                    //generate messageId from key
                    int messageId = generatedKeys.getInt(1);
                    //return new message object
                    return new Message(messageId, account_id, message_text, time_posted_epoch);
                }
            }

        }
        //catch exception if connection fails
        catch(SQLException e)
        {
            System.out.println("Error: " + e.getMessage());
        }
        //return null if no message created
        return null;
    }

    //get message by message id
    @Override
    public Message getMessageById(int message_id)
    {
        //create connection to database
        Connection connection = ConnectionUtil.getConnection();

        //try catch block
        try
        {
            //create sql prepared statement searching for message by message_id
            String sql = "SELECT * FROM message WHERE message_id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);

            //set values of sql prepared statement
            preparedStatement.setInt(1, message_id);

            //execute sql prepared statement and get results, if any
            ResultSet resultSet = preparedStatement.executeQuery();

            //if there are results
            if(resultSet.next())
            {
                //retrieve message meta data, account_id, message_text, time_posted_epoch
                int account_id = resultSet.getInt("posted_by");
                String message_text = resultSet.getString("message_text");
                long time_posted_epoch = resultSet.getLong("time_posted_epoch");
                //return new message object using data parameters
                return new Message(message_id, account_id, message_text, time_posted_epoch);
            }
        }
        //catch exception if connection fails
        catch(SQLException e)
        {
            System.out.println("Error: " + e.getMessage());
        }
        //return null if no results
        return null;
    }

    //get all messages
    @Override
    public List<Message> getAllMessages()
    {
        //create connection to database
        Connection connection = ConnectionUtil.getConnection();

        //try catch block
        try
        {
            //create sql prepared statement searching for all messages
            String sql = "SELECT * FROM message";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);

            //execute sql prepared statement and get results, if any
            ResultSet resultSet = preparedStatement.executeQuery();

            //create list to store any messages
            List<Message> messages = new ArrayList<>();

            //iterate through resultSet to store all results, creating new message objects and storing in message List
            while(resultSet.next())
            {
                //retrieve message meta data, message_id, account_id, message_text, time_posted_epoch
                int message_id = resultSet.getInt("message_id");
                int account_id = resultSet.getInt("posted_by");
                String message_text = resultSet.getString("message_text");
                long time_posted_epoch = resultSet.getLong("time_posted_epoch");
                //create Message object and add to messages list
                messages.add(new Message(message_id, account_id, message_text, time_posted_epoch));
            }
            //return list of results
            return messages;
        }
        //catch exception if connection fails
        catch(SQLException e)
        {
            System.out.println("Error: " + e.getMessage());
        }
        //return null if no results
        return null;
    }
    
    //get all messages for user
    @Override
    public List<Message> getAllMessagesForUser(int account_id)
    {
        //create connection to database
        Connection connection = ConnectionUtil.getConnection();

        //try catch block
        try
        {
            //create sql prepared statement searching for message by account_id
            String sql = "SELECT * FROM message WHERE posted_by = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);

            //set values of sql prepared statement
            preparedStatement.setInt(1, account_id);

            //execute sql prepared statement and get results, if any
            ResultSet resultSet = preparedStatement.executeQuery();

            //create list to store any messages
            List<Message> messages = new ArrayList<>();

            //iterate through resultSet to store all results, creating new message objects and storing in message List
            while(resultSet.next())
            {
                //retrieve message meta data, message_id, account_id, message_text, time_posted_epoch
                int message_id = resultSet.getInt("message_id");
                String message_text = resultSet.getString("message_text");
                long time_posted_epoch = resultSet.getLong("time_posted_epoch");
                //create Message object and add to messages list
                messages.add(new Message(message_id, account_id, message_text, time_posted_epoch));
            }
            //return list of results
            return messages;
        }
        //catch exception if connection fails
        catch(SQLException e)
        {
            System.out.println("Error: " + e.getMessage());
        }
        //return null if no results
        return null;
    }

    //update message
    @Override
    public Message updateMessage(int message_id, String new_text)
    {
        //create connection to database
        Connection connection = ConnectionUtil.getConnection();

        //create null message object
        Message message = null;

        //try catch block
        try
        {
            //create sql prepared statement searching for message by message_id
            String sql = "UPDATE message SET message_text = ? WHERE message_id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);

            //set values of sql prepared statement
            preparedStatement.setString(1, new_text);
            preparedStatement.setInt(2, message_id);

            //execute sql prepared statement and store results, if any
            int affectedRows = preparedStatement.executeUpdate();
            if(affectedRows > 0)
            {
                //get updated message from database using message_id
                message = getMessageById(message_id);
            }
        }
        //catch exception if connection fails
        catch(SQLException e)
        {
            System.out.println("Error: " + e.getMessage());
        }
        //return message
        return message;
    }

    //delete message
    @Override
    public Message deleteMessage(int message_id)
    {
        //create connection to database
        Connection connection = ConnectionUtil.getConnection();
        
        //create null message object
        Message message = null;
        //try catch block
        try
        {
            //retrieve message from database
            message = getMessageById(message_id);
            
            //if message exists
            if(message != null)
            {
                //create sql prepared statement deleting message
                String sql = "DELETE FROM message WHERE message_id = ?";
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
            
                //set values of sql prepared statement and execute
                preparedStatement.setInt(1, message_id);
                preparedStatement.executeUpdate();
            }            
        }
        //catch exception if connection fails
        catch(SQLException e)
        {
            System.out.println("Error: " + e.getMessage());
        }
        //return message object
        return message;
    }
}
