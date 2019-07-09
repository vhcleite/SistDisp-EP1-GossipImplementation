package threads;

import model.Peer;

public abstract class AbstractThread extends Thread {

    private Peer iPeer;

    public AbstractThread(Peer iPeer) {
        setPeer(iPeer);
    }

    public Peer getPeer() {
        return iPeer;
    }

    public void setPeer(Peer peer) {
        this.iPeer = peer;
    }

    protected void ThreadLog(String log) {
        System.out.println(getIdentifier() + log);
    }

    private String getIdentifier() {
        return String.format("[%s] %s: ", getPeer().getAddress(), getThreadName());
    }

    abstract public String getThreadName();
}
