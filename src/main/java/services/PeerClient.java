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
import threads.MetaDataVerifierThread;
import threads.MyMetaDataSenderThread;
import threads.NeighborsMetaDataSenderThread;
import threads.PeerListenerThread;

public class PeerClient {

    public static int localPort = 9001;
    public String localHost;
    public DatagramSocket socket;
    public List<Address> peerAddresses;

    private Peer iPeer;
    private ArrayList<PeerRecord> peerRecords = new ArrayList<PeerRecord>();

    public PeerClient(Integer localPeerPort, String remotePeersList) {
        initSocket(localPeerPort);
        initPeer(localPeerPort);
        initPeerRecords(remotePeersList);
    }

    private void initPeer(Integer localPeerPort) {
        try {
            this.localHost = InetAddress.getLocalHost().getHostAddress();
            this.iPeer = new Peer(localHost, localPeerPort);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    private void initPeerRecords(String remotePeersList) {

        List<Address> addresses = new ArrayList<>();
        String[] split = remotePeersList.split(",");

        for (String address : split) {
            String[] addressArray = address.split(":");
            addresses.add(new Address(addressArray[0],Integer.valueOf(addressArray[1])));
        }

        this.peerAddresses = addresses;

        for (Address address : this.peerAddresses) {
            this.peerRecords.add(new PeerRecord(address.getIp(), address.getPort()));
        }
    }

    private void initSocket(Integer localPeerPort) {
        try {
            this.socket = new DatagramSocket(localPeerPort);
        } catch (SocketException e) {
            e.printStackTrace();
            System.out.println("Não foi possível inicializar o socket");
        }
    }

    public static void main(String args[]) {


        if(args.length != 2){
            System.out.println("Os argumento são: ");
            System.out.println("(1) porta do peer local");
            System.out.println("(2) lista de ip1:porta1,ip2:porta2 separados por vírgulas dos peers remotos");
        }

        Integer localPeerPort = Integer.valueOf(args[0]);
        String remotePeersList = args[1];


        PeerClient client = new PeerClient(localPeerPort, remotePeersList);

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

        // Thread responsavel por enviar os de peers vizinhos para os peers vizinhos
        NeighborsMetaDataSenderThread neighborsMetadataSenderThread = new NeighborsMetaDataSenderThread(client.iPeer,
                client.socket, client.peerRecords, client.peerAddresses);
        neighborsMetadataSenderThread.start();

        // Thread responsavel por enviar os de peers vizinhos para os peers vizinhos
        MetaDataVerifierThread metaDataVerifierThread = new MetaDataVerifierThread(client.iPeer, client.peerRecords);
        metaDataVerifierThread.start();
    }

}
