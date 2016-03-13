package es.sisifo.tomcatutil.filters;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;

import es.sisifo.tomcatutil.util.IOUtil;
/**
 *
 * @author Sisifo
 *
 */
public class SimpleRequestDumperFilter implements Filter {
	private static final ThreadLocal<Timestamp> timestamp = new ThreadLocal<Timestamp>() {
		@Override
		protected SimpleRequestDumperFilter.Timestamp initialValue() {
			return new SimpleRequestDumperFilter.Timestamp();
		}
	};

	private static final Log LOG = LogFactory.getLog(SimpleRequestDumperFilter.class);



	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
	}

	@Override
	public void destroy() {
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		if (!(request instanceof HttpServletRequest) || !(response instanceof HttpServletResponse)) {
			LOG.info(Thread.currentThread().getName() + " Non HTTP Request or Response. Skip filter");
			chain.doFilter(request, response);
			return;
		}

		final ResettableStreamHttpServletRequest wrappedRequest = new ResettableStreamHttpServletRequest((HttpServletRequest) request);
		final CopyableStreamHttpServletResponse wrappedResponse = new CopyableStreamHttpServletResponse((HttpServletResponse) response);


		doLog("******************", "********************************************");
		doLog("START TIME       a ", getTimestamp());
		logRequest(wrappedRequest);
		doLog("------------------", "--------------------------------------------");

		try {
			chain.doFilter(wrappedRequest, wrappedResponse);
			wrappedResponse.flushBuffer();
		}
		finally {
			doLog("------------------", "--------------------------------------------");
			logResponse(wrappedResponse);
			doLog("END TIME          ", getTimestamp());
			doLog("==================", "============================================");
		}
	}




	private void logRequest(final ResettableStreamHttpServletRequest wrappedRequest) throws IOException {
		doLog("        requestURI", wrappedRequest.getRequestURI());
		doLog("          authType", wrappedRequest.getAuthType());

		doLog(" characterEncoding", wrappedRequest.getCharacterEncoding());
		doLog("     contentLength", Integer.valueOf(wrappedRequest.getContentLength()).toString());
		doLog("       contentType", wrappedRequest.getContentType());
		doLog("       contextPath", wrappedRequest.getContextPath());

		logRequestCookies(wrappedRequest);
		logRequestHeaders(wrappedRequest);

		doLog("            locale", wrappedRequest.getLocale().toString());
		doLog("            method", wrappedRequest.getMethod());

		logRequestParameters(wrappedRequest);

		doLog("          pathInfo", wrappedRequest.getPathInfo());

		doLog("          protocol", wrappedRequest.getProtocol());
		doLog("       queryString", wrappedRequest.getQueryString());
		doLog("        remoteAddr", wrappedRequest.getRemoteAddr());
		doLog("        remoteHost", wrappedRequest.getRemoteHost());
		doLog("        remoteUser", wrappedRequest.getRemoteUser());
		doLog("requestedSessionId", wrappedRequest.getRequestedSessionId());
		doLog("            scheme", wrappedRequest.getScheme());
		doLog("        serverName", wrappedRequest.getServerName());
		doLog("        serverPort", Integer.valueOf(wrappedRequest.getServerPort()).toString());
		doLog("       servletPath", wrappedRequest.getServletPath());
		doLog("          isSecure", Boolean.valueOf(wrappedRequest.isSecure()).toString());
		doLog("          authType", wrappedRequest.getAuthType());
		doLog("        remoteUser", wrappedRequest.getRemoteUser());
	}


	private void logRequestCookies(final HttpServletRequest hRequest) {
		final Cookie[] cookies = hRequest.getCookies();
		if (cookies != null) {
			for (final Cookie cookie : cookies) {
				doLog("            cookie", cookie.getName() + "=" + cookie.getValue());
			}
		}
	}

	private void logRequestHeaders(final HttpServletRequest hRequest) {
		final Enumeration<String> hnames = hRequest.getHeaderNames();
		while (hnames.hasMoreElements()) {
			final String hname = hnames.nextElement();
			final Enumeration<String> hvalues = hRequest.getHeaders(hname);
			while (hvalues.hasMoreElements()) {
				final String hvalue = hvalues.nextElement();
				doLog("            header", hname + "=" + hvalue);
			}
		}
	}

	private void logRequestParameters(final ResettableStreamHttpServletRequest wrappedRequest) throws IOException {
		final String body = IOUtil.toString(wrappedRequest.getReader());
		if (body != null && !body.trim().equals("")){
			final String[] paramValues = body.split("&");
			for (final String paramValue : paramValues) {
				doLog("         parameter", paramValue);
			}
		}
		wrappedRequest.resetInputStream();
	}


	//------------------------------------------------------------------------------------- //



	private void logResponse(final CopyableStreamHttpServletResponse wrappedResponse) throws UnsupportedEncodingException {
		doLog("       contentType", wrappedResponse.getContentType());

		logResponseHeaders(wrappedResponse);

		doLog("            status", Integer.valueOf(wrappedResponse.getStatus()).toString());
		doLog("              body", new String(wrappedResponse.getCopy(), wrappedResponse.getCharacterEncoding()));
	}


	private void logResponseHeaders(final CopyableStreamHttpServletResponse wrappedResponse) {
		for (final String rhname : wrappedResponse.getHeaderNames()) {
			final Iterable<String> rhvalues = wrappedResponse.getHeaders(rhname);
			for (final String rhvalue : rhvalues) {
				doLog("            header", rhname + "=" + rhvalue);
			}
		}
	}


	//------------------------------------------------------------------------------------- //



	private void doLog(String attribute, String value) {
		final StringBuilder sb = new StringBuilder(80);
		sb.append(Thread.currentThread().getName());
		sb.append(' ');
		sb.append(attribute);
		sb.append('=');
		sb.append(value);
		LOG.info(sb.toString());
	}

	private String getTimestamp() {
		final Timestamp ts = timestamp.get();
		final long currentTime = System.currentTimeMillis();
		if (ts.date.getTime() + 999L < currentTime) {
			ts.date.setTime(currentTime - currentTime % 1000L);
			ts.update();
		}
		return ts.dateString;
	}



	private static final class Timestamp {
		private final Date date = new Date(0L);
		private final SimpleDateFormat format = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");
		private String dateString = this.format.format(this.date);

		private void update() {
			this.dateString = this.format.format(this.date);
		}
	}

}
