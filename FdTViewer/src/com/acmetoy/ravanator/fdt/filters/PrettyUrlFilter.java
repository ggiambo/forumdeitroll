/*    */ package com.acmetoy.ravanator.fdt.filters;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.util.regex.Matcher;
/*    */ import java.util.regex.Pattern;
/*    */ import javax.servlet.Filter;
/*    */ import javax.servlet.FilterChain;
/*    */ import javax.servlet.FilterConfig;
/*    */ import javax.servlet.RequestDispatcher;
/*    */ import javax.servlet.ServletContext;
/*    */ import javax.servlet.ServletException;
/*    */ import javax.servlet.ServletRequest;
/*    */ import javax.servlet.ServletResponse;
/*    */ import javax.servlet.http.HttpServletRequest;
/*    */ import javax.servlet.http.HttpServletResponse;
/*    */ import org.apache.log4j.Logger;
/*    */ 
/*    */ public class PrettyUrlFilter
/*    */   implements Filter
/*    */ {
/* 19 */   private Pattern threadPattern = Pattern.compile("/thread/([0-9]{6,8})/.*");
/*    */ 
/* 21 */   private String images = "/images/";
/* 22 */   private String ctx_images = null;
/* 23 */   private String js = "/js/";
/* 24 */   private String ctx_js = null;
/* 25 */   private String css = "/css/";
/* 26 */   private String ctx_css = null;
/*    */ 
/* 28 */   private static Logger LOG = Logger.getLogger(PrettyUrlFilter.class);
/*    */ 
/*    */   public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException
/*    */   {
/* 32 */     HttpServletRequest hsRequest = (HttpServletRequest)request;
/* 33 */     HttpServletResponse hsResponse = (HttpServletResponse)response;
/* 34 */     String requestUri = hsRequest.getRequestURI();
/* 35 */     if ((requestUri.contains(this.images)) && (!requestUri.startsWith(this.ctx_images)))
/*    */     {
/* 37 */       String real_url = hsRequest.getContextPath() + requestUri.substring(requestUri.indexOf(this.images));
/* 38 */       if (request.getParameter("v") != null) {
/* 39 */         real_url = real_url + "?v=" + request.getParameter("v");
/*    */       }
/* 41 */       LOG.debug(requestUri + " -> " + real_url);
/* 42 */       hsResponse.sendRedirect(real_url);
/* 43 */       return;
/* 44 */     }if ((requestUri.contains(this.js)) && (!requestUri.startsWith(this.ctx_js)))
/*    */     {
/* 46 */       String real_url = hsRequest.getContextPath() + requestUri.substring(requestUri.indexOf(this.js));
/* 47 */       if (request.getParameter("v") != null) {
/* 48 */         real_url = real_url + "?v=" + request.getParameter("v");
/*    */       }
/* 50 */       LOG.debug(requestUri + " -> " + real_url);
/* 51 */       hsResponse.sendRedirect(real_url);
/* 52 */       return;
/* 53 */     }if ((requestUri.contains(this.css)) && (!requestUri.startsWith(this.ctx_css))) {
/* 54 */       String real_url = hsRequest.getContextPath() + requestUri.substring(requestUri.indexOf(this.css));
/* 55 */       if (request.getParameter("v") != null) {
/* 56 */         real_url = real_url + "?v=" + request.getParameter("v");
/*    */       }
/* 58 */       LOG.debug(requestUri + " -> " + real_url);
/* 59 */       hsResponse.sendRedirect(real_url);
/* 60 */       return;
/* 61 */     }if (requestUri.startsWith(hsRequest.getContextPath() + "/thread/"))
/*    */     {
/* 63 */       Matcher matcher = this.threadPattern.matcher(requestUri);
/* 64 */       if (matcher.find()) {
/* 65 */         String threadId = matcher.group(1);
/* 66 */         String real_url = "/Threads?action=getByThread&threadId=" + threadId;
/* 67 */         LOG.debug(requestUri + " -> " + real_url);
/* 68 */         request.getRequestDispatcher(real_url).forward(request, response);
/* 69 */         return;
/*    */       }
/*    */     }
/* 72 */     chain.doFilter(request, response);
/*    */   }
/*    */ 
/*    */   public void destroy() {
/*    */   }
/*    */ 
/*    */   public void init(FilterConfig config) throws ServletException {
/* 79 */     this.ctx_images = (config.getServletContext().getContextPath() + this.images);
/* 80 */     this.ctx_js = (config.getServletContext().getContextPath() + this.js);
/* 81 */     this.ctx_css = (config.getServletContext().getContextPath() + this.css);
/*    */   }
/*    */ }

/* Location:           /home/daniele/war/WEB-INF/classes/
 * Qualified Name:     com.acmetoy.ravanator.fdt.filters.PrettyUrlFilter
 * JD-Core Version:    0.6.0
 */