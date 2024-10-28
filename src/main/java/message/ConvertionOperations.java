package message;

import java.nio.ByteBuffer;

public class ConvertionOperations {
  public static byte[] intToByteArray(int value) {
    return ByteBuffer.allocate(4).putInt(value).array();
  }

  public static byte[] shortToByteArray(short value) {
    return ByteBuffer.allocate(2).putShort(value).array();
  }

  public static int byteArrayToInt(byte[] bytes) {
    return ByteBuffer.wrap(bytes).getInt();
  }

  public static short byteArrayToShort(byte[] bytes) {
    return ByteBuffer.wrap(bytes).getShort();
  }
}