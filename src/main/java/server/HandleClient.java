package server;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

import message.ConvertionOperations;



public class HandleClient extends Thread {
    private Socket clientSocket;

    public HandleClient(Socket socket) {
        this.clientSocket = socket;
    }

    @Override
    public void run() {
        try {
            handleClientConnection(clientSocket);
        } catch (IOException e) {
            System.err.println("CLIENT HANDLER ERROR: " + e.getMessage());
        } finally {
            closeClientSocket();
        }
    }

    private void handleClientConnection(Socket client) throws IOException {
        try (InputStream in = client.getInputStream(); OutputStream out = client.getOutputStream()) {
      while (true) {
        int requestLength = ConvertionOperations.byteArrayToInt(in.readNBytes(4));
        short apiKey = ConvertionOperations.byteArrayToShort(in.readNBytes(2));
        short apiVersion = ConvertionOperations.byteArrayToShort(in.readNBytes(2));
        byte[] correlationId = in.readNBytes(4);
        in.skipNBytes(requestLength - 8);

        System.err.println("Request received - length: " + requestLength);
        System.err.println("apiKey: " + apiKey);
        System.err.println("apiVersion: " + apiVersion);
        System.err.println("correlation id: " + ConvertionOperations.byteArrayToInt(correlationId));

        ByteArrayOutputStream responseBody = new ByteArrayOutputStream();
        responseBody.write(correlationId);

        if (apiKey == ApiKeys.API_VERSION) {
          if (SUPPORTED_API_VERSIONS.contains(apiVersion)) {
            responseBody.write(ConvertionOperations.shortToByteArray(ErrorCodes.NONE));
            responseBody.write(2); // Number of API keys + 1
            responseBody.write(ConvertionOperations.shortToByteArray(apiKey));
            responseBody.write(ConvertionOperations.shortToByteArray(Collections.min(SUPPORTED_API_VERSIONS)));
            responseBody.write(ConvertionOperations.shortToByteArray(Collections.max(SUPPORTED_API_VERSIONS)));
            responseBody.write(0); // tag buffer
            responseBody.write(ConvertionOperations.intToByteArray(0)); // throttle_time
            responseBody.write(0); // tag buffer
          } else {
            responseBody.write(ConvertionOperations.shortToByteArray(ErrorCodes.UNSUPPORTED_VERSION));
            responseBody.write(ConvertionOperations.intToByteArray(0)); // throttle_time
            responseBody.write(0); // tag buffer
          }
        }

        out.write(ConvertionOperations.intToByteArray(responseBody.size()));
        out.write(responseBody.toByteArray());
        out.flush();
      }
    } finally {
        client.close();
    }
    }


    private void closeClientSocket() {
        try {
            if (clientSocket != null && !clientSocket.isClosed()) {
                clientSocket.close();
                System.out.println("SERVER, message: Connection closed.");
            }
        } catch (IOException e) {
            System.err.println("Error closing client socket: " + e.getMessage());
        }
    }

    public static final Set<Short> SUPPORTED_API_VERSIONS = Set.of(0, 1, 2, 3, 4)
            .stream()
            .map(Integer::shortValue)
            .collect(Collectors.toUnmodifiableSet());

    public static class ApiKeys {
        public static final short API_VERSION = 18;
    }

    public static class ErrorCodes {
        public static final short NONE = 0;
        public static final short UNSUPPORTED_VERSION = 35;
    }
}
