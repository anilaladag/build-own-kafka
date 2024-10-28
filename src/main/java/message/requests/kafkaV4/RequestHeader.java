package message.requests.kafkaV4;

public class RequestHeader {
    private short apikey;
    private short apiVersion;
    private int correlationId;

    public short getApikey() {
        return apikey;
    }

    public void setApikey(short apikey) {
        this.apikey = apikey;
    }

    public short getApiVersion() {
        return apiVersion;
    }

    public void setApiVersion(short apiVersion) {
        this.apiVersion = apiVersion;
    }

    public int getCorrelationId() {
        return correlationId;
    }

    public void setCorrelationId(int correlationId) {
        this.correlationId = correlationId;
    }
}
