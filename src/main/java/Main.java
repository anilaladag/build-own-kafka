import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

public class Main {

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

  public static void main(String[] args) {
    int port = 9092;
    try (ServerSocket serverSocket = new ServerSocket(port)) {
      serverSocket.setReuseAddress(true);
      System.out.println("Server started on port " + port);

      while (true) {
        Socket clientSocket = serverSocket.accept();
        // Create a new thread for each client connection
        new Thread(() -> {
          try {
            handleRequest(clientSocket);
          } catch (IOException e) {
            System.err.println("Error handling client request: " + e.getMessage());
          }
        }).start();
      }
    } catch (IOException e) {
      System.err.println("Server error: " + e.getMessage());
    }
  }

  private static void handleRequest(Socket clientSocket) throws IOException {
    try (InputStream in = clientSocket.getInputStream(); OutputStream out = clientSocket.getOutputStream()) {
      while (true) {
        int requestLength = byteArrayToInt(in.readNBytes(4));
        short apiKey = byteArrayToShort(in.readNBytes(2));
        short apiVersion = byteArrayToShort(in.readNBytes(2));
        byte[] correlationId = in.readNBytes(4);
        in.skipNBytes(requestLength - 8);

        System.err.println("Request received - length: " + requestLength);
        System.err.println("apiKey: " + apiKey);
        System.err.println("apiVersion: " + apiVersion);
        System.err.println("correlation id: " + byteArrayToInt(correlationId));

        ByteArrayOutputStream responseBody = new ByteArrayOutputStream();
        responseBody.write(correlationId);

        if (apiKey == ApiKeys.API_VERSION) {
          if (SUPPORTED_API_VERSIONS.contains(apiVersion)) {
            responseBody.write(shortToByteArray(ErrorCodes.NONE));
            responseBody.write(2); // Number of API keys + 1
            responseBody.write(shortToByteArray(apiKey));
            responseBody.write(shortToByteArray(Collections.min(SUPPORTED_API_VERSIONS)));
            responseBody.write(shortToByteArray(Collections.max(SUPPORTED_API_VERSIONS)));
            responseBody.write(0); // tag buffer
            responseBody.write(intToByteArray(0)); // throttle_time
            responseBody.write(0); // tag buffer
          } else {
            responseBody.write(shortToByteArray(ErrorCodes.UNSUPPORTED_VERSION));
            responseBody.write(intToByteArray(0)); // throttle_time
            responseBody.write(0); // tag buffer
          }
        }

        out.write(intToByteArray(responseBody.size()));
        out.write(responseBody.toByteArray());
        out.flush();
      }
    } finally {
      clientSocket.close();
    }
  }

  private static byte[] intToByteArray(int value) {
    return ByteBuffer.allocate(4).putInt(value).array();
  }

  private static byte[] shortToByteArray(short value) {
    return ByteBuffer.allocate(2).putShort(value).array();
  }

  private static int byteArrayToInt(byte[] bytes) {
    return ByteBuffer.wrap(bytes).getInt();
  }

  private static short byteArrayToShort(byte[] bytes) {
    return ByteBuffer.wrap(bytes).getShort();
  }
}
