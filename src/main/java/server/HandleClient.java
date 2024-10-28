package server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import message.ConvertionOperations;
import message.requests.kafkaV4.Request;
import message.responses.kafkav4.Response;

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
        InputStream inputStream = client.getInputStream();
        OutputStream outputStream = client.getOutputStream();

        System.out.println("SERVER, message: New connection established.");

        while (true) {
            try {
                // Read request from client
                Request request = new Request();
                request.readRequestFromStream(inputStream);

                // Log correlation ID
                int correlationId = request.getHeader().getCorrelationId();
                System.out.println("SERVER, new request: request's correlation Id is: " + correlationId);

                // Create and populate response based on request
                Response response = new Response();
                response.fromRequest(request); // Populate the response here

                // Ensure response is valid after population
                if (response.getBody().getError_code() != (short) 0) {
                    System.err.println("Error Code in response is not zero for correlation ID: " + correlationId);
                }
                if (response.getBody().getApiKey() != (short) 18 || response.getBody().getMaxVersion() < (short) 4) {
                    System.err.println("Response for API key 18 is invalid for correlation ID: " + correlationId);
                }

                // Encode and send the response
                byte[] encodedResponse = response.encodeResponse();
                int responseLength = encodedResponse.length;

                // Send length and response to the client
                outputStream.write(ConvertionOperations.intToByteArray(responseLength));
                outputStream.write(encodedResponse);
                outputStream.flush();
            } catch (IOException e) {
                System.err.println("Error reading/writing to client: " + e.getMessage());
                break; // Exit the loop on error
            }
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
}
