package org.example.JMS;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.example.RMI.MessageService;
import org.example.model.CountryMessage;

import javax.jms.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.Scanner;

public class InteractiveEmployeeReceiver {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        try {
            // RMI setup - connecting to RMI registry
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);
            MessageService service = (MessageService) registry.lookup("MessageService");

            // JMS setup
            ConnectionFactory factory = new ActiveMQConnectionFactory("tcp://localhost:61616");
            Connection conn = factory.createConnection();
            conn.start();
            Session session = conn.createSession(false, Session.AUTO_ACKNOWLEDGE);

            // Countries and queue names
            Map<Integer, String> countries = new HashMap<>();
            countries.put(1, "usa");
            countries.put(2, "china");
            countries.put(3, "india");
            countries.put(4, "uk");

            while (true) {
                System.out.println("\n--- Checking for incoming messages ---");

                Map<Integer, CountryMessage> receivedMessages = new HashMap<>();

                // Check messages from RMI service (pull all messages from each country)
                for (Map.Entry<Integer, String> entry : countries.entrySet()) {
                    List<CountryMessage> messages = service.getMessages(entry.getValue());
                    for (CountryMessage msg : messages) {
                        if (!msg.isReply()) {
                            receivedMessages.put(entry.getKey(), msg);
                            System.out.println(entry.getKey() + ". Message from " + entry.getValue().toUpperCase() + ": " + msg.getContent());
                        }
                    }
                }

                if (receivedMessages.isEmpty()) {
                    System.out.println("No new messages from any country.");
                    Thread.sleep(2000); // wait before retry
                    continue;
                }

                // Show reply menu to employee
                System.out.println("\nChoose a country to reply:");
                for (Map.Entry<Integer, String> entry : countries.entrySet()) {
                    System.out.println(entry.getKey() + ". " + entry.getValue().toUpperCase());
                }
                System.out.println("5. Exit");
                System.out.print("> ");
                int choice = scanner.nextInt();
                scanner.nextLine(); // consume newline

                if (choice == 5) {
                    break;
                }

                if (!receivedMessages.containsKey(choice)) {
                    System.out.println("No message to reply for selected country.");
                    continue;
                }

                String country = countries.get(choice);
                Queue replyQueue = session.createQueue("queue." + country + ".reply");

                // Retrieve the selected message
                CountryMessage selectedMessage = receivedMessages.get(choice);
                System.out.println("\nYou are replying to: " + selectedMessage.getContent());

                System.out.print("Enter your reply to " + country.toUpperCase() + ": ");
                String replyText = scanner.nextLine();

                // Send reply via JMS (ActiveMQ)
                MessageProducer producer = session.createProducer(replyQueue);
                TextMessage reply = session.createTextMessage(replyText);
                producer.send(reply);

                System.out.println("Reply sent to " + country.toUpperCase() + " client.");

                // Send the reply also via RMI (if desired, for consistency)
                CountryMessage replyMessage = new CountryMessage(
                        country,
                        "employee",  // Sender is the employee
                        replyText,
                        new java.util.Date(),
                        true  // Mark as a reply
                );
                service.sendMessage(replyMessage);

                Thread.sleep(1000); // wait before checking again

            }

            session.close();
            conn.close();
            System.out.println("Employee session ended.");

        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
