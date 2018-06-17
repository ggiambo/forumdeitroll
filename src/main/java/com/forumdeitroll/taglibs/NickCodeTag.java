package com.forumdeitroll.taglibs;

import org.apache.commons.lang3.StringUtils;

import javax.servlet.jsp.tagext.TagSupport;
import javax.xml.bind.annotation.adapters.HexBinaryAdapter;
import java.security.MessageDigest;

public class NickCodeTag extends TagSupport {
	private String nick;
	public void setNick(String nick) {
		this.nick = nick;
	}
	@Override
	public int doEndTag() {
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

	private static String last6(String s) {
		return s.substring(s.length() - 6, s.length());
	}
	private static String md5(String s) throws Exception {
		MessageDigest md = MessageDigest.getInstance("md5");
		md.update(s.getBytes("UTF-8"));
		return hex(md.digest());
	}
	private static String hex(byte[] b) {
		return new HexBinaryAdapter().marshal(b);
	}
}
