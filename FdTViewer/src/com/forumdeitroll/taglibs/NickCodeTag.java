package com.forumdeitroll.taglibs;

import java.security.MessageDigest;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;
import javax.xml.bind.annotation.adapters.HexBinaryAdapter;

import org.apache.commons.lang3.StringUtils;

public class NickCodeTag extends TagSupport {
	private String nick;
	public void setNick(String nick) {
		this.nick = nick;
	}
	@Override
	public int doEndTag() throws JspException {
		if (!StringUtils.isEmpty(nick)) {
			try {
				String hash = md5(nick);
				StringBuilder out = new StringBuilder();
				out.append("<div style='color: #");
				out.append(last6(hash));
				out.append("' title='");
				out.append(hash);
				out.append("'>&#9786;</div>");
				pageContext.getOut().println(out.toString());
			} catch (Exception e) {}
		}
		return SKIP_BODY;
	}
	public static String lpad(String s, int minLength, char c) {
		StringBuilder out = new StringBuilder();
		while (s.length() <=-- minLength) {
			out.append(c);
		}
		return out.append(s).toString();
	}
	public static String last6(String s) {
		return s.substring(s.length() - 6, s.length());
	}
	public static String last8(String s) {
		return s.substring(s.length() - 8, s.length());
	}
	public static String init8(String s) {
		return s.substring(0, 8);
	}
	public static String md5(String s) throws Exception {
		MessageDigest md = MessageDigest.getInstance("md5");
		md.update(s.getBytes("UTF-8"));
		return hex(md.digest());
	}
	public static String sha1(String s) throws Exception {
		MessageDigest md = MessageDigest.getInstance("sha1");
		md.update(s.getBytes("UTF-8"));
		return hex(md.digest());
	}
	public static String hex(byte[] b) {
		return new HexBinaryAdapter().marshal(b);
	}
}
