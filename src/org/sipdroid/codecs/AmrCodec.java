package org.sipdroid.codecs;

public class AmrCodec {
	  static
	  {
	    System.loadLibrary("AmrCodec");
	  }
	  public static native int nbDecode(int paramInt1, byte[] paramArrayOfByte1, int paramInt2, byte[] paramArrayOfByte2, int paramInt3);

	  public static native void nbDestroy(int paramInt);

	  public static native int nbInit();

	  public static native int wbDecode(int paramInt1, byte[] paramArrayOfByte1, int paramInt2, byte[] paramArrayOfByte2, int paramInt3);

	  public static native void wbDestroy(int paramInt);

	  public static native int wbInit();
}
