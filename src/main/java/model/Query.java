package model;

import java.io.Serializable;
import java.util.Objects;

public class Query implements Serializable {
    private ClientId clientId;
    private String fileName;
    private Integer ttl;

    public Query(ClientId clientId, String file, Integer ttl) {
        this.clientId = clientId;
        this.fileName = file;
        this.ttl = ttl;
    }

    public ClientId getClientId() {
        return clientId;
    }

    public void setClientId(ClientId clientId) {
        this.clientId = clientId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFile(String file) {
        this.fileName = file;
    }

    public Integer getTtl() {
        return ttl;
    }

    public void setTtl(Integer ttl) {
        this.ttl = ttl;
    }

    public void decreaseTTL() {
        this.ttl--;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Query query = (Query) o;
        return clientId.equals(query.clientId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(clientId);
    }

    @Override
    public String toString() {
        return "Query [clientId=" + clientId + ", file=" + fileName + ", ttl=" + ttl + "]";
    }
}
