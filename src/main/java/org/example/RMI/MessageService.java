package org.example.RMI;

import org.example.model.CountryMessage;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface MessageService extends Remote {
    void sendMessage(CountryMessage message) throws RemoteException;
    List<CountryMessage> getMessages(String country) throws RemoteException;
}
