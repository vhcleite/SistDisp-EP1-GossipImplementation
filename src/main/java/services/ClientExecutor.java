package services;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.SocketException;

import model.Address;
import model.ClientId;
import model.Message;
import model.MessageType;
import model.Query;
import resources.PeerAddressesList;
import threads.QueryResponseThread;

public class ClientExecutor {

    private static final long DELAY = 500;

    public static MessageHandler mHandler = new MessageHandler();

    public static void main(String args[]) throws IOException, InterruptedException {

        if (args.length != 2) {
            System.out.println("Os argumento são: ");
            System.out.println("(1) endenco do peer local");
            System.out.println("(2) lista de ip1:porta1,ip2:porta2 separados por vírgulas dos peers remotos");
        }

        String localAddrString = args[0];
        String remotePeersList = args[1];

        PeerAddressesList peerAddressesList = new PeerAddressesList(remotePeersList);
        Address localAddr = PeerAddressesList.getAddressFromString(localAddrString);

        String fileName;
        BufferedReader ob = new BufferedReader(new InputStreamReader(System.in));
        while (!(fileName = getInputFromUser(ob, "Enter a filename to query or type exit to leave: ")).equals("exit")) {
            int TTL = Integer.valueOf(getInputFromUser(ob, "With TTL: "));
            long TIMEOUT = Long.valueOf(getInputFromUser(ob, "And timeout in seconds: ")) * 1000;

            Query query = new Query(new ClientId(localAddr), fileName, TTL);

            Address address = getRandomAddress(peerAddressesList);
            System.out.println("Procurando por: " + fileName + ", no peer " + address);
            sendQuerytoAddress(query, address, localAddr.getPort());

            ServerSocket serverSocket = new ServerSocket(localAddr.getPort());
            QueryResponseThread queryReponseThread = new QueryResponseThread(serverSocket, query);
            queryReponseThread.start();

            long start = System.currentTimeMillis();
            while (true) {
                Thread.sleep(DELAY);

                if (queryReponseThread.isDownloadComplete()) {
                    System.out.println(String.format("Download de %s completo", fileName));
                    break;
                } else if (!queryReponseThread.isDownloadComplete() &&!queryReponseThread.isDownloading() && System.currentTimeMillis() - start >= TIMEOUT) {
                    System.out.println(String.format("Timeout para downlaod de %s", fileName));
                    break;
                }
            }
            serverSocket.close();
            queryReponseThread.setShouldRun(false);
            Thread.sleep(500);
        }

        System.out.println("Saindo do ClientExecutor");
    }

    private static void sendQuerytoAddress(Query query, Address targetAddress, int localPort) throws SocketException {
        DatagramSocket socket = new DatagramSocket(localPort);

        Message message = new Message(MessageType.QUERY, mHandler.stringfy(query));
        MessageSenderService.sendMessage(socket, mHandler.stringfy(message), targetAddress);

        System.out.println(String.format("Query [%s] enviada para o Peer [%s]", message, targetAddress));
        socket.close();
    }

    private static Address getRandomAddress(PeerAddressesList peerAddressesList) {
        return peerAddressesList.getAddress(LotteryService.getRandomInt(peerAddressesList.getSize()));
    }

    private static String getInputFromUser(BufferedReader ob, String message) {
        try {
            System.out.print(message);
            return ob.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

}
