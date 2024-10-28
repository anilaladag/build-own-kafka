package message.responses.kafkav4;

public class ResponseHeader {
    int correlationId ;
    public ResponseHeader() {
    }
    public ResponseHeader(int correlationId){
        this.correlationId = correlationId ;
    }

    public int getCorrelationId() {
        return correlationId;
    }

    public void setCorrelationId(int correlationId) {
        this.correlationId = correlationId;
    }


}