package es.sisifo.tomcatutil.filters;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

/**
 * http://stackoverflow.com/questions/8933054/how-to-log-response-content-from-a-java-web-server
 * @author another guy
 *
 */
public class CopyableStreamHttpServletResponse extends HttpServletResponseWrapper {
	private final HttpServletResponse response;
	private final ResettableServletOutputStream servletStream;
	private PrintWriter writer;


	public CopyableStreamHttpServletResponse(HttpServletResponse response) {
		super(response);
		this.response = response;
		this.servletStream = new ResettableServletOutputStream();
	}


	@Override
    public ServletOutputStream getOutputStream() throws IOException {
        if (servletStream.outputStream == null) {
        	servletStream.outputStream = response.getOutputStream();
        }

        return servletStream;
    }

    @Override
    public PrintWriter getWriter() throws IOException {
    	if (servletStream.outputStream == null) {
        	servletStream.outputStream = response.getOutputStream();
        }

        if (writer == null) {
            writer = new PrintWriter(new OutputStreamWriter(servletStream, response.getCharacterEncoding()), true);
        }

        return writer;
    }

    @Override
    public void flushBuffer() throws IOException {
        if (writer != null) {
            writer.flush();
        } else if (servletStream.outputStream != null) {
        	servletStream.flush();
        }
    }

    public byte[] getCopy() {
        if (servletStream != null) {
            return servletStream.getCopy();
        } else {
            return new byte[0];
        }
    }


	private
	class ResettableServletOutputStream extends ServletOutputStream  {
		private OutputStream outputStream;
		private final ByteArrayOutputStream copy = new ByteArrayOutputStream(1024 * 4);

		@Override
		public void write(int b) throws IOException {
			outputStream.write(b);
			copy.write(b);
		}

		public byte[] getCopy() {
	        return copy.toByteArray();
	    }

	}
}