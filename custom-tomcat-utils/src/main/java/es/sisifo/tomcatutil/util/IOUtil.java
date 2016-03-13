package es.sisifo.tomcatutil.util;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;

public class IOUtil {
	private static final int DEFAULT_BUFFER_SIZE = 1024 * 4;

	public static byte[] toByteArray(BufferedReader input) throws IOException {
		final ByteArrayOutputStream output = new ByteArrayOutputStream();
		final OutputStreamWriter writer = new OutputStreamWriter(output);
		copy(input, writer);
		writer.flush();
		return output.toByteArray();
	}


	public static String toString(BufferedReader reader) throws IOException {
		final StringWriter writer = new StringWriter();
		copy(reader, writer);
		return writer.toString();
	}


	private static long copy(Reader input, Writer output) throws IOException {
		final char[] buffer = new char[DEFAULT_BUFFER_SIZE];
		long count = 0;
		int n = 0;
		while (-1 != (n = input.read(buffer))) {
			output.write(buffer, 0, n);
			count += n;
		}
		return count;
	}
}
