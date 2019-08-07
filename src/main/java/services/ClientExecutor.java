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

//    public final static int TTL = 2;
//    public final static long TIMEOUT = 50 * 1000;
    public final static int LOCAL_PORT = 3001;
    public final static String LOCAL_IP = "127.0.0.1";
    private static final long DELAY = 500;

    public static MessageHandler mHandler = new MessageHandler();

    public static void main(String args[]) throws IOException, InterruptedException {

        String fileName;
        BufferedReader ob = new BufferedReader(new InputStreamReader(System.in));
        while (!(fileName = getInputFromUser(ob, "Enter a filename to query or type exit to leave: ")).equals("exit")) {
            int TTL = Integer.valueOf(getInputFromUser(ob, "With TTL: "));
            long TIMEOUT = Long.valueOf(getInputFromUser(ob, "And timeout in seconds: ")) * 1000;

            Query query = new Query(new ClientId(new Address(LOCAL_IP, LOCAL_PORT)), fileName, TTL);

            Address address = getRandomAddress();
            System.out.println("Procurando por: " + fileName + ", no peer " + address);
            sendQuerytoAddress(query, address);

            ServerSocket serverSocket = new ServerSocket(LOCAL_PORT);
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

    private static void sendQuerytoAddress(Query query, Address targetAddress) throws SocketException {
        DatagramSocket socket = new DatagramSocket(LOCAL_PORT);

        Message message = new Message(MessageType.QUERY, mHandler.stringfy(query));
        MessageSenderService.sendMessage(socket, mHandler.stringfy(message), targetAddress);

        System.out.println(String.format("Query [%s] enviada para o Peer [%s]", message, targetAddress));
        socket.close();
    }

    private static Address getRandomAddress() {
        return PeerAddressesList.getAddress(LotteryService.getRandomInt(PeerAddressesList.getSize()));
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
