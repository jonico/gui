package com.collabnet.ccf;

import java.io.IOException;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

public class Obfuscator {

	public final static BASE64Encoder encoder = new BASE64Encoder();
	public final static BASE64Decoder decoder = new BASE64Decoder();
	
	public static String obfuscateString(String string) {
		byte[] bytes = string.getBytes();
		for (int i =0;i<bytes.length;++i) {
			bytes[i] = cyclicShiftBitsRight(bytes[i], 4);
		}		
		return encoder.encode(bytes);
	}
	
	private static byte cyclicShiftBitsRight(int b, int i) {
		if (b < 0) {
			b+=256;
		}
		int j = (((b >>> i) % 256) | (b << (8-i)));
		if (j > 127) {
			j-=256;
		}
		return (byte) j;
	}
	
	private static byte cyclicShiftBitsLeft(int b, int i) {
		if (b < 0) {
			b+=256;
		}
		int j = ((b << i) | ((b >>> (8-i)) % 256));
		if (j > 127) {
			j-=256;
		}
		return (byte) j;
	}

	public static String deObfuscateString(String string) {
		byte[] bytes;
		try {
			bytes = decoder.decodeBuffer(string);
		} catch (IOException e) {
			return null;
		}
		for (int i =0;i<bytes.length;++i) {
			bytes[i] = cyclicShiftBitsLeft(bytes[i], 4);
		}
		return new String(bytes);
	}
	
}
