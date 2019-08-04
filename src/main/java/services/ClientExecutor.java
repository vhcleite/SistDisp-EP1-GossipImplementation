package services;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramSocket;

import model.Address;
import model.ClientId;
import model.Message;
import model.MessageType;
import model.Query;
import resources.PeerAddressesList;

public class ClientExecutor {

    public final static int TTL = 2;
    public final static int TIMEOUT = 1000;
    public final static int LOCAL_PORT = 3000;
    public final static String LOCAL_IP = "127.0.0.1";

    public static MessageHandler mHandler = new MessageHandler();

    public static void main(String args[]) throws IOException {

        String fileName;
        BufferedReader ob = new BufferedReader(new InputStreamReader(System.in));
        while (!(fileName = getInputFromUser(ob)).equals("exit")) {
            System.out.println("Searching for: " + fileName);

            DatagramSocket socket = new DatagramSocket(LOCAL_PORT);

            Query query = new Query(new ClientId(new Address(LOCAL_IP, LOCAL_PORT)), fileName, TTL);

            Address address = getRandomAddress();
            sendQuerytoAddress(socket, query, address);

            socket.close();
        }

        System.out.println("Saindo do ClientExecutor");
    }

    private static void sendQuerytoAddress(DatagramSocket socket, Query query, Address targetAddress) {
        Message message = new Message(MessageType.QUERY, mHandler.stringfy(query));
        MessageSenderService.sendMessage(socket, mHandler.stringfy(message), targetAddress);

        System.out.println(String.format("Query [%s] enviada para o Peer [%s]", message, targetAddress));
    }

    private static Address getRandomAddress() {
        return PeerAddressesList.getAddress(LotteryService.getRandomInt(PeerAddressesList.getSize()));
    }

    private static String getInputFromUser(BufferedReader ob) {
        try {
            System.out.print("Enter a filename: ");
            return ob.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

}
