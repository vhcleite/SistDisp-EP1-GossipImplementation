package services;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import model.Address;

public class MessageSenderService {

    public static void sendMessage(DatagramSocket sendSocket, String message, Address targetPeerAddress) {

        try {
            InetAddress targetAddress = InetAddress.getByName(targetPeerAddress.getIp());

            byte[] jsonBytes = message.getBytes();

            DatagramPacket datagramPacket = new DatagramPacket(//
                    jsonBytes, jsonBytes.length, targetAddress, targetPeerAddress.getPort());

            sendSocket.send(datagramPacket);
        } catch (IOException e) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
            sendMessage(sendSocket, message, targetPeerAddress);
        }
    }
}
