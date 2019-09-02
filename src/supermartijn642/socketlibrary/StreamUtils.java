package supermartijn642.socketlibrary;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;

public class StreamUtils {
	
	public static void writeInt(OutputStream stream, int i) throws IOException {
		stream.write(ByteBuffer.allocate(4).putInt(i).array());
	}
	
	public static int readInt(InputStream stream) throws IOException {
		byte[] bytes = new byte[4];
		stream.read(bytes, 0, 4);
		return ByteBuffer.wrap(bytes).getInt();
	}
	
	public static void writeFloat(OutputStream stream, float f) throws IOException {
		stream.write(ByteBuffer.allocate(4).putFloat(f).array());
	}
	
	public static float readFloat(InputStream stream) throws IOException {
		byte[] bytes = new byte[4];
		stream.read(bytes, 0, 4);
		return ByteBuffer.wrap(bytes).getFloat();
	}
	
	public static void writeDouble(OutputStream stream, double d) throws IOException {
		stream.write(ByteBuffer.allocate(8).putDouble(d).array());
	}
	
	public static double readDouble(InputStream stream) throws IOException {
		byte[] bytes = new byte[8];
		stream.read(bytes, 0, 8);
		return ByteBuffer.wrap(bytes).getDouble();
	}
	
	public static void writeString(OutputStream stream, String s) throws IOException {
		writeInt(stream, s.length());
		stream.write(s.getBytes());
	}
	
	public static String readString(InputStream stream) throws IOException {
		int length = readInt(stream);
		byte[] bytes = new byte[length];
		stream.read(bytes, 0, length);
		return new String(bytes);
	}
	
	public static void writeBoolean(OutputStream stream, boolean b) throws IOException {
		stream.write(b == true ? 1 : 0);
	}
	
	public static boolean readBoolean(InputStream stream) throws IOException {
		return stream.read() == 1;
	}
	
	public static byte[] readBytes(InputStream stream, int length) throws IOException, IndexOutOfBoundsException {
		ByteBuffer buffer = ByteBuffer.allocate(length);
		int total = 0;
		while(total < length){
			byte[] bytes = new byte[length - total];
			int read = stream.read(bytes, 0, length - total);
			buffer.put(bytes, 0, read);
			total += read;
		}
		return buffer.array();
	}

}
