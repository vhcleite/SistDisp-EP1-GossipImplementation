package threads;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

import model.Message;
import model.MessageType;
import model.Query;
import services.MessageHandler;

public class QueryResponseThread extends Thread {

    private ServerSocket serverSocket;
    private Query query;
    private boolean isDownloading = false;
    private boolean isDownloadCompleted = false;
    private boolean shouldRun = true;
    private MessageHandler mHandler;

    public QueryResponseThread(ServerSocket serverSocket, Query query) {
        super();
        this.serverSocket = serverSocket;
        this.setQuery(query);
    }

    @Override
    public void run() {

        while (getShouldRun()) {
            try {
                Socket socket = serverSocket.accept();
                System.out.println("Recebendo requisição para transferência de: " + socket.getInetAddress());

                InputStream is = socket.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(is));

                String peerInput = br.readLine();
                System.out.println("peerInput: " + peerInput);

                mHandler = new MessageHandler();
                Message message = mHandler.parseMessage(peerInput);
                System.out.println("Message: " + message);

                if (message.getType() == MessageType.QUERY_SEND_REQUEST) {
                    System.out.println("Recebido requisicao de envio de arquivo");
                    Query query = mHandler.parseQueryMessage(message.getContent());

                    DataOutputStream out = new DataOutputStream(socket.getOutputStream());

                    Message requestResponse;
                    if (query.equals(getQuery())) {
                        System.out.println("Consulta valida. Permitir envio de arquivo");
                        requestResponse = new Message(MessageType.SEND_REQUEST_ALLOWED, "");
                        responseDownloadRequest(out, requestResponse);
                        System.out.println("Realizando download");
                        isDownloading = true;
                        downloadFile(is);
                        isDownloadCompleted = true;
                    } else {
                        System.out.println("Consulta Invalida. Nao permitir envio de arquivo");
                        requestResponse = new Message(MessageType.SEND_REQUEST_NOT_ALLOWED, "");
                        responseDownloadRequest(out, requestResponse);
                    }
                }
            } catch (IOException e) {
                System.out.println("Socket fechado");
            } finally {
                isDownloading = false;
            }
        }
    }

    private void downloadFile(InputStream is) throws IOException {
        int bytesRead = 0;
        int currentTotal = bytesRead;

        byte[] bytearray = new byte[200 * 1024 * 1024];

        FileOutputStream fos = new FileOutputStream(getQuery().getFileName());
        BufferedOutputStream bos = new BufferedOutputStream(fos);
        System.out.print("Baixando arquivo");
        do {
            bytesRead = is.read(bytearray, currentTotal, (bytearray.length - currentTotal));
            if (bytesRead >= 0) {
                currentTotal += bytesRead;
//                System.out.println((currentTotal/1024) + " Kbytes");
                System.out.print(".");
            }
        } while ((bytesRead > -1) && getShouldRun());

        if (bytesRead < 0) {
            System.out.println(String.format("\nDownload finalizado. Foram baixados %s bytes", currentTotal));
        }

        bos.write(bytearray, 0, currentTotal);
        bos.flush();
        bos.close();
        fos.close();
    }

    private void responseDownloadRequest(DataOutputStream out, Message requestResponse) throws IOException {
        String requestResponseString = mHandler.stringfy(requestResponse) + "\n";
        out.writeBytes(requestResponseString);
        System.out.println("Resposta da requisicao enviada: " + requestResponseString);
    }

    public boolean isDownloadComplete() {
        return isDownloadCompleted;
    }

    public boolean getShouldRun() {
        return shouldRun;
    }

    public void setShouldRun(boolean shouldRun) {
        this.shouldRun = shouldRun;
    }

    public Query getQuery() {
        return query;
    }

    public void setQuery(Query query) {
        this.query = query;
    }

    public boolean isDownloading() {
        return isDownloading;
    }
}
