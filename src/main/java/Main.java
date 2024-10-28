import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;

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

    InputStream in = clientSocket.getInputStream();
    var out = clientSocket.getOutputStream();

    byte[] messageSizeBytes = in.readNBytes(4);
    byte[] apiKey = in.readNBytes(2);
    byte[] apiVersion = in.readNBytes(2);
    byte[] correlationIdBytes = in.readNBytes(4);

    out.write(messageSizeBytes);
    out.write(correlationIdBytes);

  }

  private static byte[] intToByteArray(int value) {
    return new byte[] { (byte) (value >>> 24), (byte) (value >>> 16),
        (byte) (value >>> 8), (byte) value };
  }
  
  private static int byteArrayToInt(byte[] bytes) {
        return ByteBuffer.wrap(bytes).getInt();

  }
}

