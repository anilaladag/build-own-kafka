package message.responses.kafkav4;

import java.nio.ByteBuffer;
import message.requests.kafkaV4.Request;

public class Response {
    private ResponseHeader header;
    private ResponseBody body;

    public Response() {
        this.header = new ResponseHeader();
        this.body = new ResponseBody();
    }

    // Modify the current instance instead of creating a new Response
    public void fromRequest(Request req) {
        // Set the correlation ID from the request
        header.setCorrelationId(req.getHeader().getCorrelationId());

        // Validate API version and set error code accordingly
        short errorCode = 0; // Default to no error
        int apiVersion = req.getHeader().getApiVersion();

        if (apiVersion <= -1 || apiVersion >= 5) {
            errorCode = (short) 35; // Invalid API version
        }

        body.setError_code(errorCode);

        // Logic to handle an array of API keys (placeholder for future implementation)
        body.setArrayLength(2); // This should be updated when array handling is implemented

        // Set response properties for API key and version constraints
        body.setApiKey((short) 18);
        body.setMinVersion((short) 3);
        body.setMaxVersion((short) 4);
        body.setArray_tagged_fields((byte) 0);
        body.setThrottle_time(0);
        body.setOuter_tagged_fields((byte) 0);
    }

    public byte[] encodeResponse() {
        ByteBuffer buffer = ByteBuffer.allocate(1024)
                .putInt(header.getCorrelationId())
                .putShort(body.getError_code())
                .put((byte) body.getArrayLength())
                .putShort(body.getApiKey())
                .putShort(body.getMinVersion())
                .putShort(body.getMaxVersion())
                .put(body.getArray_tagged_fields())
                .putInt(body.getThrottle_time())
                .put(body.getOuter_tagged_fields())
                .flip();

        byte[] resp = new byte[buffer.remaining()];
        buffer.get(resp);

        return resp;
    }

    public ResponseHeader getHeader() {
        return header;
    }

    public void setHeader(ResponseHeader header) {
        this.header = header;
    }

    public ResponseBody getBody() {
        return body;
    }

    public void setBody(ResponseBody body) {
        this.body = body;
    }
}
