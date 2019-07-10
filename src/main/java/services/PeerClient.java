package services;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import model.Address;
import model.Peer;
import model.PeerRecord;
import threads.MetaDataBuilderThread;
import threads.MyMetaDataSenderThread;
import threads.NeighborsMetaDataSenderThread;
import threads.PeerListenerThread;

public class PeerClient {

    public static final int MY_PORT = 9001;
    public String localHost;
    public DatagramSocket socket;
    public List<Address> peerAddresses;

    private Peer iPeer;
    private ArrayList<PeerRecord> peerRecords = new ArrayList<PeerRecord>();

    public PeerClient() {
        initSocket();
        initPeer();
        initPeerRecords();
    }

    private void initPeer() {
        try {
            this.localHost = InetAddress.getLocalHost().getHostAddress();
            this.iPeer = new Peer(localHost, MY_PORT);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    private void initPeerRecords() {

        this.peerAddresses = Arrays.asList(//
                new Address(this.localHost, 9000), //
                new Address(this.localHost, 9001)); //
//                new Address(this.localHost, 9002), //
//                new Address(this.localHost, 9003), //
//                new Address(this.localHost, 9004)); // ;

        for (Address address : this.peerAddresses) {
            this.peerRecords.add(new PeerRecord(address.getIp(), address.getPort()));
        }
    }

    private void initSocket() {
        try {
            this.socket = new DatagramSocket(MY_PORT);
        } catch (SocketException e) {
            e.printStackTrace();
            System.out.println("Não foi possível inicializar o socket");
        }
    }

    public static void main(String args[]) {

        PeerClient client = new PeerClient();

        // Para thread funcionar é esperado que exista a pasta gossip_test_folder na
        // home do usuario
        MetaDataBuilderThread builderThread = new MetaDataBuilderThread(client.iPeer);
        builderThread.start();

        // Thread responsavel por escutar mensagens
        PeerListenerThread peerListenerThread = new PeerListenerThread(client.iPeer, client.socket, client.peerRecords);
        peerListenerThread.start();

        // Thread responsavel por enviar os meus metadados para os peers vizinhos
        MyMetaDataSenderThread myMetadataSenderThread = new MyMetaDataSenderThread(client.socket, client.iPeer,
                client.peerAddresses);
        myMetadataSenderThread.start();

        // Thread responsavel por enviar os meus metadados para os peers vizinhos
        NeighborsMetaDataSenderThread neighborsMetadataSenderThread = new NeighborsMetaDataSenderThread(client.iPeer,
                client.socket, client.peerRecords, client.peerAddresses);
        neighborsMetadataSenderThread.start();
    }

}
