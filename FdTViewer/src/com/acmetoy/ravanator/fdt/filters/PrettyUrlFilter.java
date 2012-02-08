package com.acmetoy.ravanator.fdt.filters;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

public class PrettyUrlFilter implements Filter {
	private Pattern threadPattern = Pattern.compile("/thread/([0-9]{6,8})/.*");

	private String images = "/images/";
	private String ctx_images = null;
	private String js = "/js/";
	private String ctx_js = null;
	private String css = "/css/";
	private String ctx_css = null;
	private String Messages = "/Messages";
	private String ctx_Messages = null;
	private String Threads = "/Threads";
	private String ctx_Threads = null;
	private String User = "/User";
	private String ctx_User = null;
	
	private String GET = "GET";

	private static Logger LOG = Logger.getLogger(PrettyUrlFilter.class);

	private String rebuildParams(String input, HttpServletRequest request) {
		String out = "?";
		for (Enumeration e = request.getParameterNames(); e.hasMoreElements();) {
			String key = e.nextElement().toString();
			String value = request.getParameter(key);
			try {
				out += key + "=" + java.net.URLEncoder.encode(value, "UTF-8") + "&";
			} catch (UnsupportedEncodingException e1) {
				// ignore
			}
		}
		return input + out;
	}
	
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest hsRequest = (HttpServletRequest) request;
		HttpServletResponse hsResponse = (HttpServletResponse) response;
		String requestUri = hsRequest.getRequestURI();
		
		String rewriteTo = null;
		boolean redirect = false;
		
		if ((requestUri.contains(images)) && (!requestUri.startsWith(ctx_images))) {
			rewriteTo = hsRequest.getContextPath() + requestUri.substring(requestUri.indexOf(images));
			redirect = true;
		}
		if ((requestUri.contains(js)) && (!requestUri.startsWith(ctx_js))) {
			rewriteTo = hsRequest.getContextPath() + requestUri.substring(requestUri.indexOf(js));
			redirect = true;
		}
		if ((requestUri.contains(css)) && (!requestUri.startsWith(ctx_css))) {
			rewriteTo = hsRequest.getContextPath() + requestUri.substring(requestUri.indexOf(css));
			redirect = true;
		}
		if (rewriteTo != null) {
			if (request.getParameter("v") != null) {
				rewriteTo += "?v=" + request.getParameter("v");
			}
		}
		
		if (requestUri.endsWith(Messages) && !requestUri.equals(ctx_Messages)) {
			if (GET.equals(hsRequest.getMethod())) {
				rewriteTo = rebuildParams(ctx_Messages, hsRequest);
				redirect = true;
			} else {
				rewriteTo = Messages;
			}
		}
		if (requestUri.endsWith(Threads) && !requestUri.equals(ctx_Threads)) {
			if (GET.equals(hsRequest.getMethod())) {
				rewriteTo = rebuildParams(ctx_Threads, hsRequest);
				redirect = true;
			} else {
				rewriteTo = Threads;
			}
		}
		if (requestUri.endsWith(User) && !requestUri.equals(ctx_User)) {
			if (GET.equals(hsRequest.getMethod())) {
				rewriteTo = rebuildParams(ctx_User, hsRequest);
				redirect = true;
			} else {
				rewriteTo = User;
			}
		}
		// sarebbe da adottare uno strategy pattern...
		if (rewriteTo == null && requestUri.startsWith(hsRequest.getContextPath() + "/thread/")) {
			Matcher matcher = threadPattern.matcher(requestUri);
			if (matcher.find()) {
				String threadId = matcher.group(1);
				rewriteTo = "/Threads?action=getByThread&threadId=" + threadId;
			}
		}
		
		if (rewriteTo != null) {
			if (redirect) {
				LOG.debug("302 "+requestUri+" -> "+rewriteTo);
				hsResponse.sendRedirect(rewriteTo);
			} else {
				LOG.debug("FW "+requestUri+" -> "+rewriteTo);
				hsRequest.getRequestDispatcher(rewriteTo).forward(request, response);
			}
		} else {
			chain.doFilter(request, response);	
		}
		
	}

	public void destroy() {
	}

	public void init(FilterConfig config) throws ServletException {
		this.ctx_images = (config.getServletContext().getContextPath() + this.images);
		this.ctx_js = (config.getServletContext().getContextPath() + this.js);
		this.ctx_css = (config.getServletContext().getContextPath() + this.css);
		this.ctx_Messages = (config.getServletContext().getContextPath() + this.Messages);
		this.ctx_Threads = (config.getServletContext().getContextPath() + this.Threads);
		this.ctx_User = (config.getServletContext().getContextPath() + this.User);
	}
}
