package services;

import model.Address;
import model.Peer;

import java.io.IOException;
import java.net.*;

public class MetadataSenderService {

    private Peer iPeer;

    public MetadataSenderService(Peer iPeer) {
        this.iPeer = iPeer;
    }

    public void sendMessage(String json, Address targetPeerAddress) throws InterruptedException {
        try {
            DatagramSocket sendSocket = new DatagramSocket(iPeer.getAddress().getPort());
            InetAddress targetAddress = InetAddress.getByName(targetPeerAddress.getIp());

            byte[] jsonBytes = json.getBytes();

            DatagramPacket datagramPacket = new DatagramPacket(jsonBytes,
                    jsonBytes.length,
                    targetAddress,
                    targetPeerAddress.getPort());

            sendSocket.send(datagramPacket);
        } catch (IOException e){
            Thread.sleep(10);
            sendMessage(json, targetPeerAddress);
        }
    }
}
