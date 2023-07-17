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
            //throw exception error
            return null;
        }

        //if username is less than 4 characters
        if(password.length() < 4)
        {
            //throw exception error
            return null;
        }

        //create account set to null
        Account account = null;

        //try catch block to connect to database
        try(Connection connection = ConnectionUtil.getConnection())
        {
            //check if username is already in database
            String sql = "SELECT * FROM account WHERE username = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next())
            {
                //throw exception error
                return null;
            }

            //change sql statement to insert new account username and password
            sql = "INSERT INTO account (username, password) VALUES (?, ?)";
            preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

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
            //throw exception error if no keys were generated
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
        Account account = null;
        //try catch block to connect to database
        try(Connection connection = ConnectionUtil.getConnection())
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
        //try catch block to connect to database
        try(Connection connection = ConnectionUtil.getConnection())
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

    //get last created message id
    @Override
    public int getLastCreatedMessageId()
    {
        //try catch block to connect to database
        try(Connection connection = ConnectionUtil.getConnection())
        {
            //create sql prepared statement looking for the max message_id
            String sql = "SELECT MAX(message_id) FROM message";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);

            //execute sql prepared statement and store results, if any
            ResultSet resultSet = preparedStatement.executeQuery();

            //if there are results
            if(resultSet.next())
            {
                //return id of last created message
                return resultSet.getInt(1);
            }
        }
        //catch exception if connection fails
        catch(SQLException e)
        {
            System.out.println("Error: " + e.getMessage());
        }
        //return 0 if no results
        return 0;
    }

    //get message by message id
    @Override
    public Message getMessageById(int message_id)
    {
        //try catch block to connect to database
        try(Connection connection = ConnectionUtil.getConnection())
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
        //try catch block to connect to database
        try(Connection connection = ConnectionUtil.getConnection())
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
        //try catch block to connect to database
        try(Connection connection = ConnectionUtil.getConnection())
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
        Message message = null;
        int affectedRows = 0;
        //try catch block to connect to database
        try(Connection connection = ConnectionUtil.getConnection())
        {
            //create sql prepared statement searching for message by message_id
            String sql = "UPDATE message SET message_text = ? WHERE message_id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);

            //set values of sql prepared statement
            preparedStatement.setString(1, new_text);
            preparedStatement.setInt(2, message_id);

            //execute sql prepared statement and store results, if any
            affectedRows = preparedStatement.executeUpdate();
            if(affectedRows > 0)
            {
                sql = "SELECT * FROM message WHERE message_id = ?";
                preparedStatement = connection.prepareStatement(sql);

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
                    message = new Message(message_id, account_id, message_text, time_posted_epoch);
                }
            }
        }
        //catch exception if connection fails
        catch(SQLException e)
        {
            System.out.println("Error: " + e.getMessage());
        }
        //return false if no message updated
        return message;
    }

    //delete message
    @Override
    public Message deleteMessage(int message_id)
    {
        Message message = null;
        //try catch block to connect to database
        try(Connection connection = ConnectionUtil.getConnection())
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
                //store new message object using data parameters
                message = new Message(message_id, account_id, message_text, time_posted_epoch);
            }

            if(message != null)
            {
                //create sql prepared statement deleting message
                sql = "DELETE FROM message WHERE message_id = ?";
                preparedStatement = connection.prepareStatement(sql);
            
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
