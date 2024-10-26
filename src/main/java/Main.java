import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {
  public static void main(String[] args) {

    ServerSocket serverSocket = null;
    Socket clientSocket = null;
    int port = 9092;
    try {
      serverSocket = new ServerSocket(port);
      serverSocket.setReuseAddress(true);
      clientSocket = serverSocket.accept();
      handleRequest(clientSocket);
    } catch (IOException e) {
      System.out.println("IOException: " + e.getMessage());
    } finally {
      try {
        if (clientSocket != null) {
          clientSocket.close();
        }
      } catch (IOException e) {
        System.out.println("IOException: " + e.getMessage());
      }
    }
  }
  
  private static void handleRequest(final Socket clientSocket)
      throws IOException {
    byte[] messageSize = intToByteArray(1);
    byte[] headerCorrelationId = intToByteArray(7);
    var out = clientSocket.getOutputStream();
    out.write(messageSize);
    out.write(headerCorrelationId);
  }

  private static byte[] intToByteArray(int value) {
    return new byte[] { (byte) (value >>> 24), (byte) (value >>> 16),
        (byte) (value >>> 8), (byte) value };
  }
}

