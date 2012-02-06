/*    */ package com.acmetoy.ravanator.fdt;
/*    */ 
/*    */ import javax.servlet.ServletContext;
/*    */ import javax.servlet.jsp.JspException;
/*    */ import javax.servlet.jsp.JspWriter;
/*    */ import javax.servlet.jsp.PageContext;
/*    */ import javax.servlet.jsp.tagext.TagSupport;
/*    */ import org.apache.log4j.Logger;
/*    */ 
/*    */ public class ThreadPrettyUrlTag extends TagSupport
/*    */ {
/* 13 */   private static Logger LOG = Logger.getLogger(ThreadPrettyUrlTag.class);
/*    */   private String threadId;
/*    */   private String subject;
/*    */   private String msgId;
/*    */ 
/*    */   private static String prettifySubject(String subject)
/*    */   {
/* 16 */     StringBuilder out = new StringBuilder();
/* 17 */     for (char c : subject.toCharArray()) {
/* 18 */       if ((Character.isAlphabetic(c)) || (Character.isDigit(c)))
/* 19 */         out.append(c);
/*    */       else {
/* 21 */         out.append('-');
/*    */       }
/*    */     }
/* 24 */     return out.toString();
/*    */   }
/*    */ 
/*    */   public int doEndTag() throws JspException
/*    */   {
/*    */     try {
/* 30 */       String prettySubject = prettifySubject(this.subject);
/* 31 */       JspWriter out = this.pageContext.getOut();
/* 32 */       out.write("<a href=\"");
/* 33 */       out.write(this.pageContext.getServletContext().getContextPath());
/* 34 */       out.write("/thread/");
/* 35 */       out.write(this.threadId);
/* 36 */       out.write("/");
/* 37 */       out.write(prettySubject);
/* 38 */       out.write("#msg");
/* 39 */       out.write(this.msgId);
/* 40 */       out.write("\">");
/* 41 */       out.write(this.subject);
/* 42 */       out.write("</a>");
/* 43 */       out.flush();
/*    */     } catch (Exception e) {
/* 45 */       LOG.error(e.getMessage(), e);
/*    */     }
/* 47 */     return 0;
/*    */   }
/*    */ 
/*    */   public String getThreadId()
/*    */   {
/* 54 */     return this.threadId;
/*    */   }
/*    */   public void setThreadId(String threadId) {
/* 57 */     this.threadId = threadId;
/*    */   }
/*    */   public String getSubject() {
/* 60 */     return this.subject;
/*    */   }
/*    */   public void setSubject(String subject) {
/* 63 */     this.subject = subject;
/*    */   }
/*    */   public String getMsgId() {
/* 66 */     return this.msgId;
/*    */   }
/*    */   public void setMsgId(String msgId) {
/* 69 */     this.msgId = msgId;
/*    */   }
/*    */ }

/* Location:           /home/daniele/war/WEB-INF/classes/
 * Qualified Name:     com.acmetoy.ravanator.fdt.ThreadPrettyUrlTag
 * JD-Core Version:    0.6.0
 */