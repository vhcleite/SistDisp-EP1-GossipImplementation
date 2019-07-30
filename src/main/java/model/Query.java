package model;

import java.io.Serializable;
import java.util.Objects;

public class Query implements Serializable {
    private ClientId clientId;
    private String file;
    private Integer ttl;

    public Query(ClientId clientId, String file, Integer ttl) {
        this.clientId = clientId;
        this.file = file;
        this.ttl = ttl;
    }

    public ClientId getClientId() {
        return clientId;
    }

    public void setClientId(ClientId clientId) {
        this.clientId = clientId;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public Integer getTtl() {
        return ttl;
    }

    public void setTtl(Integer ttl) {
        this.ttl = ttl;
    }

    public void decreaseTtl(){
        this.ttl--;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Query query = (Query) o;
        return clientId.equals(query.clientId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(clientId);
    }
}
