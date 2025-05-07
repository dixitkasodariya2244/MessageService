# 🛰️ Java RMI & JMS (ActiveMQ) Multi-Country Messaging System

This project showcases a distributed client-server communication system combining **Java RMI** and **JMS with Apache ActiveMQ**. It simulates a real-world scenario where clients from different countries send messages to a central employee server and receive real-time or stored replies.

## 🧩 Project Structure

📦 src/

┣ 📂 org.example

┃ ┣ 📂 RMI

┃ ┃ ┣ 📜 MessageService.java # RMI interface for sending/receiving messages

┃ ┃ ┣ 📜 EmployeeServer.java # RMI server handling country-wise messages

┃ ┃ ┗ 📂 client

┃ ┃ ┗ 📜 MultiCountryClientSender.java # Client app to send messages via RMI and receive replies via RMI/JMS

┃ ┣ 📂 JMS

┃ ┃ ┗ 📜 InteractiveEmployeeReceiver.java # Employee listener responding via JMS and RMI

┃ ┗ 📂 model

┃ ┗ 📜 CountryMessage.java # Serializable model class for message object


## 🚀 Features

- ✅ **RMI-based messaging** for client-to-server communication.
- ✅ **JMS (Apache ActiveMQ)** used by employee to send real-time replies.
- ✅ **Multi-country support**: USA, China, India, UK.
- ✅ **Reply detection** using both RMI history and JMS queues.
- ✅ Interactive command-line clients for both senders and employees.

## 🔧 Requirements

- Java 8 or later
- Apache ActiveMQ (tested with version 5.x)
- RMI configured on port `1099`
- ActiveMQ broker running on `tcp://localhost:61616`

## ▶️ How to Run

### 1. Start Apache ActiveMQ

Download and run ActiveMQ:

`./activemq start`

### 2.  Run the RMI Server

`cd src/`

`javac org/example/RMI/EmployeeServer.java`

`java org.example.RMI.EmployeeServer`

### 3. Run the Client Sender

`javac org/example/RMI/client/MultiCountryClientSender.java`

`java org.example.RMI.client.MultiCountryClientSender`

### 4. Run the Employee Receiver

`javac org/example/JMS/InteractiveEmployeeReceiver.java`

`java org.example.JMS.InteractiveEmployeeReceiver`

## 🗃️ Message Flow

- Client sends message via RMI ➝ stored on server by country.

- Employee reads messages via RMI ➝ replies via both JMS (for real-time) and RMI (for persistence).

- Client checks RMI for replies, then checks JMS queue for real-time replies.

## 📌 Notes
- Make sure no firewall or port blocking is active on `1099` or `61616`.
- ActiveMQ Admin Console: `http://localhost:8161`.

## 🤝 Contributors
- dixitkasodariya2244@gmail.com
  
