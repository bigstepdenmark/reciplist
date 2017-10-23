package controller;

import com.rabbitmq.client.*;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class ReceiverController {

    private String username,
            host,
            EXCHANGE_NAME;
    private Connection connection;
    private Channel channel;
    private ConnectionFactory factory;

    public ReceiverController(String username, String host, String EXCHANGE_NAME) {
        this.username = username;
        this.host = host;
        this.EXCHANGE_NAME = EXCHANGE_NAME;
        connect();
    }

    public void printMessages() {
        try {
            handleDelivery();
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
    }

    private boolean connect() {
        try {
            return createFactory() && newConnection() && createChannel();
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
        catch( TimeoutException e ) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean close() {
        try {
            channel.close();
            connection.close();
            return true;
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
        catch( TimeoutException e ){
            e.printStackTrace();
        }
        return false;
    }

    private boolean createFactory() {
        if( factory == null )
            factory = new ConnectionFactory();

        // datdb.cphbusiness.dk or localhost
        factory.setHost( host );

        // student or guest
        factory.setUsername( username );

        // cph or guest
        // factory.setPassword( "cph" );

        // 5672 if local else 15672
        // factory.setPort( 15672 );

        return factory.getHost().equals( host );
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

    private void handleDelivery() throws IOException {
        channel.exchangeDeclare(EXCHANGE_NAME,"fanout");
        String queueName = channel.queueDeclare().getQueue();
        channel.queueBind(queueName,EXCHANGE_NAME,"");

        System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope,
                                       AMQP.BasicProperties properties, byte[] body) throws IOException {
                String message = new String(body, "UTF-8");
                System.out.println(" [x] Received '" + message + "'");
            }
        };
        channel.basicConsume(queueName, true, consumer);

    }
}
