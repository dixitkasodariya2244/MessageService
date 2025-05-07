package org.example.RMI.client;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.example.RMI.MessageService;
import org.example.model.CountryMessage;

import javax.jms.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

public class MultiCountryClientSender {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        try {
            // RMI setup
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);
            MessageService service = (MessageService) registry.lookup("MessageService");

            // JMS setup
            ConnectionFactory factory = new ActiveMQConnectionFactory("tcp://localhost:61616");
            Connection connection = factory.createConnection();
            connection.start();
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            while (true) {
                System.out.println("\nChoose country to communicate:");
                System.out.println("1. USA\n2. China\n3. India\n4. UK\n5. Exit");
                System.out.print("> ");
                int choice = scanner.nextInt();
                scanner.nextLine(); // consume newline

                String country = switch (choice) {
                    case 1 -> "usa";
                    case 2 -> "china";
                    case 3 -> "india";
                    case 4 -> "uk";
                    case 5 -> null;
                    default -> "";
                };

                if (country == null) {
                    System.out.println("Exiting...");
                    break;
                }

                if (country.isEmpty()) {
                    System.out.println("Invalid choice.");
                    continue;
                }

                // Check RMI for any stored reply messages
                List<CountryMessage> messages = service.getMessages(country);
                boolean replyShown = false;
                for (CountryMessage msg : messages) {
                    if (msg.isReply() && msg.getSender().equalsIgnoreCase("employee")) {
                        System.out.println("Reply from Employee (" + country.toUpperCase() + "): " + msg.getContent());
                        replyShown = true;
                    }
                }

                if (!replyShown) {
                    // Fallback: check ActiveMQ queue for real-time reply
                    Queue replyQueue = session.createQueue("queue." + country + ".reply");
                    MessageConsumer replyConsumer = session.createConsumer(replyQueue);
                    Message pendingReply = replyConsumer.receive(1000);
                    if (pendingReply instanceof TextMessage) {
                        System.out.println("Real-time reply from Employee:");
                        System.out.println(((TextMessage) pendingReply).getText());
                    } else {
                        System.out.println("No new replies.");
                    }
                    replyConsumer.close();
                }

                System.out.print("Enter your message (or press Enter to skip): ");
                String input = scanner.nextLine();

                if (!input.trim().isEmpty()) {
                    // Send via RMI
                    CountryMessage msg = new CountryMessage(
                            country,
                            "client",
                            input,
                            new Date(),
                            false
                    );
                    service.sendMessage(msg);
                    System.out.println("Message sent to Employee (" + country.toUpperCase() + ") via RMI.");
                } else {
                    System.out.println("No message sent.");
                }
            }

            session.close();
            connection.close();

        } catch (Exception e) {
            System.err.println("Client error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
