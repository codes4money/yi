package com.rectsoft.ppsip;

public class G729ACodec {

    public G729ACodec() {
      
    }
    
    public int getCurrentDecoderErrorCode()
    {
    	return GetCurrentDecoderErrorCode();
    }
    public  int getDecoderDataInLength()
    {
    	return GetDecoderDataInLength();
    }
    public int initDecoder()
    {
    	return InitDecoder();
    }
    /*
   	�����bitStream����ĳ���ֻ����10
   	pcm����ĳ���ֻ����80
     * */
    public int decode(byte[] bitStream, short[] pcm)
    {
    	int l = Decode(bitStream, pcm);
    	return l;
    }
    public int deInitDecoder()
    {
    	return DeInitDecoder();
    }
    
    public int getCurrentEncoderErrorCode()
    {
    	return GetCurrentEncoderErrorCode();
    }
    public  int getEncoderDataInLength()
    {
    	return GetEncoderDataInLength();
    }
    public int initEncoder()
    {
    	return InitEncoder();
    }
    /*
   	�����pcm����ĳ���ֻ����80
   	bitStream����ĳ���ֻ����10
     * */
    public int encode(short[] pcm, byte[] bitStream)
    {
    	int l = Encode(pcm, bitStream);
    	return l;
    }
    public int deInitEncoder()
    {
    	return DeInitEncoder();
    }
    
    private native int GetCurrentDecoderErrorCode();
    private native  int GetDecoderDataInLength();
    private native int InitDecoder();
    private native int Decode(byte[] bitStream, short[] pcm);
    private native int DeInitDecoder();
    
    private native int GetCurrentEncoderErrorCode();
    private native  int GetEncoderDataInLength();
    private native int InitEncoder();
    private native int Encode(short[] pcm, byte[] bitStream);
    private native int DeInitEncoder();
}
