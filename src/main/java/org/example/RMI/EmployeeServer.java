package org.example.RMI;

import org.example.model.CountryMessage;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

public class EmployeeServer implements MessageService {

    private final Map<String, List<CountryMessage>> countryMessages = new HashMap<>();

    public EmployeeServer() {
        countryMessages.put("usa", new ArrayList<>());
        countryMessages.put("china", new ArrayList<>());
        countryMessages.put("india", new ArrayList<>());
        countryMessages.put("uk", new ArrayList<>());
    }

    @Override
    public synchronized void sendMessage(CountryMessage message) {
        String country = message.getCountry().toLowerCase();
        if (countryMessages.containsKey(country)) {
            countryMessages.get(country).add(message);
            System.out.println("Received from " + message.getSender().toUpperCase() +
                    " (" + country.toUpperCase() + "): " + message.getContent());
        } else {
            System.out.println("Invalid country: " + country);
        }
    }

    @Override
    public synchronized List<CountryMessage> getMessages(String country) {
        return new ArrayList<>(countryMessages.getOrDefault(country.toLowerCase(), new ArrayList<>()));
    }

    public static void main(String[] args) throws Exception {
        EmployeeServer server = new EmployeeServer();
        MessageService stub = (MessageService) UnicastRemoteObject.exportObject(server, 0);
        Registry registry = LocateRegistry.createRegistry(1099);
        registry.rebind("MessageService", stub);
        System.out.println("Employee RMI Server is running...");
    }
}
