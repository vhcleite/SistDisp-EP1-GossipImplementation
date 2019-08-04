package threads;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;

import model.Address;
import model.Message;
import model.MessageType;
import model.Peer;
import model.PeerRecord;
import model.Query;
import services.LotteryService;
import services.MessageHandler;
import services.MessageSenderService;

public class ClientQueryHandlerThread extends AbstractThread {

    private ArrayList<Query> queriesDone;
    private Query query;
    private Semaphore semaphore;
    private ArrayList<PeerRecord> peerRecords;
    private DatagramSocket socket;

    private MessageHandler messageHandler = new MessageHandler();

    public ClientQueryHandlerThread(DatagramSocket socket, Peer iPeer, ArrayList<PeerRecord> peerRecords,
            ArrayList<Query> queries, Query query, Semaphore semaphore) {
        super(iPeer);
        this.socket = socket;
        this.peerRecords = peerRecords;
        this.queriesDone = queries;
        this.query = query;
        this.semaphore = semaphore;
    }

    @Override
    public void run() {
        // TODO verificar se eh necessario implementar um contains
        if (!queriesDone.contains(query)) {
            ThreadLog(String.format("Recebida query [%s]", query));
            query.decreaseTTL();

            String queryFile = query.getFileName();

            if (getPeer().hasFile(queryFile)) {
                sendFileToClient(query);
            } else if (query.getTtl() > 0) {
                fowardQuery(queryFile, getPeer());
            } else {
                ThreadLog(String.format("TTL da query zerado. Não repassando query"));
            }

        } else {
            ThreadLog(String.format("A query %s ja foi realizada previamente", query.getClientId().getToken()));
        }
    }

    private void fowardQuery(String queryFile, Peer iPeer) {
        Address nextPeerAddress = getNextPeer(queryFile, iPeer);
        MessageHandler messageHandler = new MessageHandler();
        Message message = new Message(MessageType.QUERY, messageHandler.stringfy(query));

        MessageSenderService.sendMessage(socket, messageHandler.stringfy(message), nextPeerAddress);
    }

    private void sendFileToClient(Query query) {

        Address clientAddress = query.getClientId().getAddress();
        String filePath = getPeer().getMonitoringFolderName() + "/" + query.getFileName();

        try {
            // Abrir socket na porta atual usada pelo peer + 1
            Socket socket = new Socket(InetAddress.getByName(clientAddress.getIp()), clientAddress.getPort() + 1);

            Message serverResponse = getPermissionToSendFile(query, socket);
            if (serverResponse != null) {
                if (serverResponse.getType() == MessageType.SEND_REQUEST_ALLOWED) {
                    // Enviar o arquivo
                    sendFile(clientAddress, filePath, socket);
                } else if (serverResponse.getType() == MessageType.SEND_REQUEST_NOT_ALLOWED)
                    ThreadLog("Cliente informou que nao precisa mais do arquivo");
                else {
                    ThreadLog("Resposta fora do protocolo");
                }
            } else {
                ThreadLog("Resposta mal formatada do cliente");
            }
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendFile(Address clientAddress, String filePath, Socket socket)
            throws FileNotFoundException, IOException {
        ThreadLog(String.format("Enviando arquivo %s para o client %s", filePath, clientAddress.toString()));

        FileInputStream fis = new FileInputStream(filePath);
        OutputStream out = socket.getOutputStream();
        byte b[] = new byte[1000];

        while ((fis.read(b)) > 0) {
            out.write(b, 0, b.length);
        }

        out.close();
        fis.close();

        ThreadLog(String.format("Arquivo %s enviado com sucesso!", filePath));
    }

    private Message getPermissionToSendFile(Query query, Socket socket) throws IOException {
        OutputStream os = socket.getOutputStream();
        DataOutputStream socketOut = new DataOutputStream(os);

        InputStreamReader isrServer = new InputStreamReader(socket.getInputStream());
        BufferedReader socketIn = new BufferedReader(isrServer);

        // Envia mensagem de requisicao de permissao de envio
        Message message = new Message(MessageType.QUERY_SEND_REQUEST, messageHandler.stringfy(query));
        socketOut.writeBytes(messageHandler.stringfy(message));

        Message serverResponse = messageHandler.parseMessage(socketIn.readLine());
        return serverResponse;
    }

    private File getFile(String fileName) {
        String gossipFolder = getPeer().getMonitoringFolderName() + "/" + fileName;
        return new File(gossipFolder);
    }

    private Address getNextPeer(String queryFile, Peer iPeer) {
        Address peerHasFileAddress = null;
        try {
            // Colocado semaforo para que thread possa consultar lista de peer vizinhos sem
            // ocorrer concorrência com outras threads
            semaphore.acquire();

            for (PeerRecord pr : peerRecords) {
                if (pr.getPeer().hasFile(queryFile)) {
                    ThreadLog(String.format("Encontrado peer [%s] com arquivo [%s] na lista de peer vizinhos",
                            pr.getPeer().getAddress(), queryFile));

                    peerHasFileAddress = pr.getPeer().getAddress();
                    break;
                }
            }

            if (peerHasFileAddress == null) {
                do {
                    peerHasFileAddress = peerRecords.get(LotteryService.getRandomInt(peerRecords.size())).getPeer()
                            .getAddress();
                } while (peerHasFileAddress.equals(iPeer.getAddress()));
                ThreadLog(String.format("Escolhido peer aleatorio [%s] para encaminhar query de arquivo[%s]",
                        peerHasFileAddress.toString(), queryFile));
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            semaphore.release();
        }
        return peerHasFileAddress;
    }

    @Override
    public String getThreadName() {
        return ClientQueryHandlerThread.class.getSimpleName();
    }
}
