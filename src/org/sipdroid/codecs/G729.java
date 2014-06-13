package org.sipdroid.codecs;
import org.sipdroid.sipua.ui.Sipdroid;
import com.rectsoft.ppsip.G729ACodec;

public class G729 extends CodecBase implements Codec {
	G729ACodec g729Acodec;

	public G729() {
		CODEC_NAME = "G729";
		CODEC_USER_NAME = "G729";
		CODEC_DESCRIPTION = "10kbps";
		CODEC_NUMBER = 18;
		super.update();
	}

	@Override
	public int decode(byte[] encoded, short[] lin, int size) {
		if (size < 10)
			return 0;
		int t = size / 10;
		byte[] b = new byte[10];
		short[] lin2 = new short[80];
		for (int j = 0; j < t; j++) {
			System.arraycopy(encoded, 12+ 10 * j, b, 0, 10);
				g729Acodec.decode(b, lin2);
			System.arraycopy(lin2,0, lin, 80*j, 80);
		}

		return t * 80;
	}

	@Override
	public int encode(short[] lin, int offset, byte[] alaw, int frames) {
	
		if (frames < 160)
			return 0;

		short[] lin2 = new short[80];
		byte[] t = new byte[10];
		for (int m = 0; m < 2; m++) {
		System.arraycopy(lin, offset+ 80 * m, lin2, 0, 80);
			g729Acodec.encode(lin2, t);
		System.arraycopy(t, 0, alaw, 12+10*m, 10);
		
		}

		return 20;
	}

	@Override
	public void init() {
		
		load();
		g729Acodec = new G729ACodec();
		g729Acodec.initEncoder();
		g729Acodec.initDecoder();
	}

	@Override
	public void close() {
		g729Acodec.deInitDecoder();
		g729Acodec.deInitEncoder();
	}

	void load() {
		try {
			System.loadLibrary("ppsip_g729_codec");
			super.load();
		} catch (Throwable e) {
			if (!Sipdroid.release)
				e.printStackTrace();
		}

	}
}
