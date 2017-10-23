package controller;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;
import entity.Loan;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class SenderController {
    private String username,
            host,
            EXCHANGE_NAME;
    private Connection connection;
    private Channel channel;
    private ConnectionFactory factory;

    public SenderController(String username, String host,String EXCHANGE_NAME) {
        this.username = username;
        this.host = host;
        this.EXCHANGE_NAME = EXCHANGE_NAME;
        connect();
    }

    private boolean connect() {
        try
        {
            return createFactory() && newConnection() && createChannel();
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
        catch(TimeoutException e)
        {
            e.printStackTrace();
        }

        return false;
    }

    public boolean close(){
        try {
            channel.close();
            connection.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean createFactory(){
        if(factory == null){factory = new ConnectionFactory();}

        factory.setHost(host);
        factory.setUsername(username);

        return factory.getHost().equals(host);
    }

    private boolean newConnection() throws IOException, TimeoutException {

        if( connection == null )
            connection = factory.newConnection();

        return connection.isOpen();
    }

    private boolean createChannel() throws IOException, TimeoutException {
        if( channel == null )
            channel = connection.createChannel();

        return channel.isOpen();
    }

    private boolean isValid(Loan loan){
        return loan.getSsn() > 0 && loan.getSsn() <= 8;
    }

    public void sendMessage(Loan loan)
    {
        String response = "Message could not be sent!";

        response = Publish( loan );

        System.out.println( response );
    }

    private String Publish(Loan loan){
        try {
            channel.exchangeDeclare(EXCHANGE_NAME,BuiltinExchangeType.FANOUT);
            channel.basicPublish(EXCHANGE_NAME,"",null,loan.toString().getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "[Sent] --> '" + loan.toString() + "'";
    }

}
