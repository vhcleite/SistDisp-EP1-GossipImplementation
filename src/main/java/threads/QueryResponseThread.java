package threads;

import java.net.ServerSocket;

import model.Query;

public class QueryResponseThread extends Thread {

    private ServerSocket serverSocket;
    private Query query;
    private boolean isDownloadComplete = false;
    private boolean shouldRun = true;

    public QueryResponseThread(ServerSocket serverSocket, Query query) {
        super();
        this.serverSocket = serverSocket;
        this.query = query;
    }

    @Override
    public void run() {
        while (getShouldRun()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
            }
        }
    }

    public boolean isDownloadComplete() {
        return isDownloadComplete;
    }

    public boolean getShouldRun() {
        return shouldRun;
    }

    public void setShouldRun(boolean shouldRun) {
        this.shouldRun = shouldRun;
    }
}
