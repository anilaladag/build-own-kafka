import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.Arrays;

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
      OutputStream out = clientSocket.getOutputStream();
      
      in.readNBytes(4);
      var apiKey = in.readNBytes(2);
      var apiVersionBytes = in.readNBytes(2);
      var apiVersion = ByteBuffer.wrap(apiVersionBytes).getShort();
      byte[] cId = in.readNBytes(4);
      var bos = new ByteArrayOutputStream();
      // size 32bit
      bos.write(cId);
      if (apiVersion < 0 || apiVersion > 4) {
        bos.write(new byte[] {0, 35});
      } else {
        bos.write(new byte[] {0, 0});       // error code
        bos.write(2);                       // array size + 1
        bos.write(new byte[] {0, 18});      // api_key
        bos.write(new byte[] {0, 3});       // min version
        bos.write(new byte[] {0, 4});       // max version
        bos.write(0);                       // tagged fields
        bos.write(new byte[] {0, 0, 0, 0}); // throttle time
        
        bos.write(0); 
      }
     
      int size = bos.size();
      byte[] sizeBytes = ByteBuffer.allocate(4).putInt(size).array();
      var response = bos.toByteArray();
      System.out.println(Arrays.toString(sizeBytes));
      System.out.println(Arrays.toString(response));
      out.write(sizeBytes);
      out.write(response);
      out.flush();

  }

  private static boolean isValidApiVersion(short apiVersion) {
    return apiVersion >= 0 && apiVersion <= 4;
  }

  private static byte[] intToByteArray(int value) {
    return new byte[] { (byte) (value >>> 24), (byte) (value >>> 16),
        (byte) (value >>> 8), (byte) value };
  }
  
  private static byte[] shortToByteArray(short value) {
    return new byte[] {
        (byte) (value >>> 8), (byte) value };
  }
  
  private static int byteArrayToInt(byte[] bytes) {
    return ByteBuffer.wrap(bytes).getInt();

  }
  
  private static short byteArrayToShort(byte[] bytes) {
    return ByteBuffer.wrap(bytes).getShort();

  }
}

