package es.sisifo.tomcatutil.filters;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import es.sisifo.tomcatutil.util.IOUtil;

/**
 * https://gist.github.com/calo81/2071634
 * @author another guy
 *
 */
public class ResettableStreamHttpServletRequest extends HttpServletRequestWrapper {
	private final HttpServletRequest request;
	private final ResettableServletInputStream servletStream;
	private byte[] rawData;

	public ResettableStreamHttpServletRequest(HttpServletRequest request) {
		super(request);
		this.request = request;
		this.servletStream = new ResettableServletInputStream();
	}

	public void resetInputStream() {
		servletStream.stream = new ByteArrayInputStream(rawData);
	}

	@Override
	public ServletInputStream getInputStream() throws IOException {
		if (rawData == null) {
			rawData = IOUtil.toByteArray(this.request.getReader());
			servletStream.stream = new ByteArrayInputStream(rawData);
		}
		return servletStream;
	}

	@Override
	public BufferedReader getReader() throws IOException {
		if (rawData == null) {
			rawData = IOUtil.toByteArray(request.getReader());
			servletStream.stream = new ByteArrayInputStream(rawData);
		}
		return new BufferedReader(new InputStreamReader(servletStream));
	}


	private class ResettableServletInputStream extends ServletInputStream {
		private InputStream stream;

		@Override
		public int read() throws IOException {
			return stream.read();
		}
	}
}