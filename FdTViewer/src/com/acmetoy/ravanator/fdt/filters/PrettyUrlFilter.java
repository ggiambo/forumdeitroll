package com.acmetoy.ravanator.fdt.filters;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;

public class PrettyUrlFilter
  implements Filter
{
  private Pattern threadPattern = Pattern.compile("/thread/([0-9]{6,8})/.*");

  private String images = "/images/";
  private String ctx_images = null;
  private String js = "/js/";
  private String ctx_js = null;
  private String css = "/css/";
  private String ctx_css = null;

  private static Logger LOG = Logger.getLogger(PrettyUrlFilter.class);

  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException
  {
    HttpServletRequest hsRequest = (HttpServletRequest)request;
    HttpServletResponse hsResponse = (HttpServletResponse)response;
    String requestUri = hsRequest.getRequestURI();
    if ((requestUri.contains(this.images)) && (!requestUri.startsWith(this.ctx_images)))
    {
      String real_url = hsRequest.getContextPath() + requestUri.substring(requestUri.indexOf(this.images));
      if (request.getParameter("v") != null) {
        real_url = real_url + "?v=" + request.getParameter("v");
      }
      LOG.debug(requestUri + " -> " + real_url);
      hsResponse.sendRedirect(real_url);
      return;
    }if ((requestUri.contains(this.js)) && (!requestUri.startsWith(this.ctx_js)))
    {
      String real_url = hsRequest.getContextPath() + requestUri.substring(requestUri.indexOf(this.js));
      if (request.getParameter("v") != null) {
        real_url = real_url + "?v=" + request.getParameter("v");
      }
      LOG.debug(requestUri + " -> " + real_url);
      hsResponse.sendRedirect(real_url);
      return;
    }if ((requestUri.contains(this.css)) && (!requestUri.startsWith(this.ctx_css))) {
      String real_url = hsRequest.getContextPath() + requestUri.substring(requestUri.indexOf(this.css));
      if (request.getParameter("v") != null) {
        real_url = real_url + "?v=" + request.getParameter("v");
      }
      LOG.debug(requestUri + " -> " + real_url);
      hsResponse.sendRedirect(real_url);
      return;
    }if (requestUri.startsWith(hsRequest.getContextPath() + "/thread/"))
    {
      Matcher matcher = this.threadPattern.matcher(requestUri);
      if (matcher.find()) {
        String threadId = matcher.group(1);
        String real_url = "/Threads?action=getByThread&threadId=" + threadId;
        LOG.debug(requestUri + " -> " + real_url);
        request.getRequestDispatcher(real_url).forward(request, response);
        return;
      }
    }
    chain.doFilter(request, response);
  }

  public void destroy() {
  }

  public void init(FilterConfig config) throws ServletException {
    this.ctx_images = (config.getServletContext().getContextPath() + this.images);
    this.ctx_js = (config.getServletContext().getContextPath() + this.js);
    this.ctx_css = (config.getServletContext().getContextPath() + this.css);
  }
}

