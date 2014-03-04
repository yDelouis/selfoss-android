package fr.ydelouis.selfoss.util;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Streams {

	public static String stringOf(InputStream inputStream) {
		inputStream.mark(Integer.MAX_VALUE);
		BufferedReader r = new BufferedReader(new InputStreamReader(inputStream));
		StringBuilder strBuilder = new StringBuilder();
		String line;
		try {
			while ((line = r.readLine()) != null)
				strBuilder.append(line);
		} catch (IOException ignored) {}
		try {
			inputStream.reset();
		} catch (IOException ignored) {}
		return strBuilder.toString();
	}

	public static byte[] byteArrayOf(InputStream body) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int len;
		while ((len = body.read(buffer)) > -1 ) {
			baos.write(buffer, 0, len);
		}
		baos.flush();
		return baos.toByteArray();
	}
}
