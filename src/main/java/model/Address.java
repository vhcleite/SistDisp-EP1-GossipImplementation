package model;

public class Address {

    private String ip;
    private int port;

    public Address(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String toString() {
        return String.format("%s:%d", ip, port);
    }

    public boolean equals(Address address) {
//        System.out.println("ip: " + this.ip);
//        System.out.println("address.getIp(): " + address.getIp());

        if (this.ip.equals(address.getIp()) && (this.port == address.getPort())) {
            return true;
        }
        return false;
    }
}
