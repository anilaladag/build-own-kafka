package message.requests.kafkaV4;

import java.io.IOException;
import java.io.InputStream;
import message.ConvertionOperations;

public class Request {
    private int length;
    private RequestHeader header;

    public Request() {
        length = 0;
        this.header = new RequestHeader();
    }

    public void readRequestFromStream(InputStream rawRequest) {
        try {
            this.length = ConvertionOperations.byteArrayToInt(rawRequest.readNBytes(4));
            this.header.setApikey(ConvertionOperations.byteArrayToShort(rawRequest.readNBytes(2)));
            this.header.setApiVersion(ConvertionOperations.byteArrayToShort(rawRequest.readNBytes(2)));
            this.header.setCorrelationId(ConvertionOperations.byteArrayToInt(rawRequest.readNBytes(4)));

        } catch (IOException e) {
            System.out.println("REQUEST Service, error: a problem occurred constructing the request : " + e.toString());
        }
    }

    // Getter ve Setter metodları
    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public RequestHeader getHeader() {
        return header;
    }

    public void setHeader(RequestHeader header) {
        this.header = header;
    }
}
