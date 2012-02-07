package com.acmetoy.ravanator.fdt;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;

import com.acmetoy.ravanator.fdt.persistence.AuthorDTO;
import com.acmetoy.ravanator.fdt.servlets.MainServlet;
import com.acmetoy.ravanator.fdt.servlets.Messages;
import com.acmetoy.ravanator.fdt.servlets.User;

public class MessageTag extends BodyTagSupport {

	private static final long serialVersionUID = 1L;
	
	private String search;
	
	private AuthorDTO author;
	
	private static String escape(String in) {return simpleReplaceAll(StringEscapeUtils.escapeHtml4(in), "'", "&apos;");}
	
	private static class BodyState {
		public String token;
		public boolean openCode;
		public boolean inCode;
		public boolean openColor;
		public int open_b = 0, open_i = 0, open_u = 0, open_s = 0;
	}
	
	private static interface BodyTokenProcessor {
		public void process(Matcher matcher, BodyState state, String search, AuthorDTO author, AuthorDTO loggedUser);
	}
	
	private static final Pattern QUOTE = Pattern.compile("^(&gt; ?)+");

	private static final Map<Pattern, BodyTokenProcessor> patternProcessorMapping =
		new HashMap<Pattern, BodyTokenProcessor>() {{
			put(Pattern.compile("^(www\\.|https?://|ftp://|mailto:).*$"), new BodyTokenProcessor() {
				@Override
				public void process(Matcher matcher, BodyState state, String search, AuthorDTO author, AuthorDTO loggedUser) {
					if (state.inCode) return;
					String url = escape(state.token);
					String desc = url;
					if (url.startsWith("www.")) {
						url = "http://" + url;
					}
					if (state.token.length() > 50) {
						desc = escape(state.token.substring(0,50)) + "...";
					}
					desc = simpleReplaceAll(desc, search, "<span style='background-color: yellow'>" + search + "</span>");
					state.token = String.format("<a href='%s' target='_blank'>%s</a>",url, desc);
				}
			});
			put(Pattern.compile("\\[img\\](https?://.*?)\\[/img\\]"), new BodyTokenProcessor() {
				@Override
				public void process(Matcher matcher, BodyState state, String search, AuthorDTO author, AuthorDTO loggedUser) {
					if (state.inCode) return;
					String url = escape(matcher.group(1));
//					state.token = matcher.replaceFirst(
//						String.format("<a class='preview' href='%s'><img class='userPostedImage' alt='Immagine postata dall&#39;utente' src='%s'></a>", url, url)
//					);
					
					String showAnonImg = "yes";
					if (loggedUser != null) {
						showAnonImg = loggedUser.getPreferences().getProperty(User.PREF_SHOWANONIMG);
					}
					
					if (StringUtils.isEmpty(author.getNick()) && StringUtils.isEmpty(showAnonImg)) {
						state.token = state.token.substring(0, matcher.start()) + 
							String.format("<a class='preview' href='%s'>Immagine postata da ANOnimo</a>", url) +
							state.token.substring(matcher.end());
					} else {
						state.token = state.token.substring(0, matcher.start()) + 
							String.format("<a class='preview' href='%s'><img class='userPostedImage' alt='Immagine postata dall&#39;utente' src='%s'></a>", url, url) +
							state.token.substring(matcher.end());
					}
				}
			});
			put(Pattern.compile("\\[yt\\]([a-zA-Z0-9\\+\\/=\\-_]{7,12})\\[/yt\\]"), new BodyTokenProcessor() {
				Long ytCounter = 0l;
				@Override
				public void process(Matcher matcher, BodyState state, String search, AuthorDTO author, AuthorDTO loggedUser) {
					if (state.inCode) return;
					String youcode = escape(matcher.group(1));
					StringBuffer sb = new StringBuffer();
					
					String embeddYt = "yes";
					if (loggedUser != null) {
						embeddYt = loggedUser.getPreferences().getProperty(User.PREF_EMBEDDYT);
					}
					
					if (StringUtils.isEmpty(embeddYt)) {
						// mostra un link
						long myYtCounter = 0l;
						synchronized (ytCounter) {
							if (ytCounter == Long.MAX_VALUE) {
								ytCounter = 0l;
							} else {
								ytCounter++;
							}
							myYtCounter = ytCounter;
						}
						
						sb.append("<a href=\"http://www.youtube.com/watch?v=").append(youcode).append("\" ");
						sb.append("id=\"yt_").append(myYtCounter).append("\">");
						sb.append("http://www.youtube.com/watch?v=").append(youcode).append("</a>");
						sb.append("<script type='text/javascript'>YTgetInfo_");
						sb.append(myYtCounter).append("= YTgetInfo('");
						sb.append(myYtCounter).append("')</script>");
						sb.append("<script type='text/javascript' src=\"");
						sb.append("http://gdata.youtube.com/feeds/api/videos/").append(youcode);
						sb.append("?v=2&amp;alt=json-in-script&amp;callback=YTgetInfo_");
						sb.append(myYtCounter).append("\"></script>");
					} else {
						// un glande classico: l'embed
						sb.append("<object height='329' width='400'>");
						sb.append("<param value='http://www.youtube.com/v/").append(youcode).append("' name='movie'>");
						sb.append("<param value='transparent' name='wmode'>");
						sb.append("<embed height='329' width='400' wmode='transparent' ");
						sb.append("type='application/x-shockwave-flash' ");
						sb.append("src='http://www.youtube.com/v/").append(youcode).append("'></object>");
					}
//					state.token = matcher.replaceFirst(sb.toString());
					state.token = state.token.substring(0, matcher.start()) + sb.toString() + state.token.substring(matcher.end());
				}
			});
			put(Pattern.compile("\\[code$"), new BodyTokenProcessor() {
				@Override
				public void process(Matcher matcher, BodyState state, String search, AuthorDTO author, AuthorDTO loggedUser) {
					state.token = state.token.substring(0, matcher.start()) + "" + state.token.substring(matcher.end());
					state.openCode = true;
				}
			});
			put(Pattern.compile("^([a-zA-Z0-9]+)\\]"), new BodyTokenProcessor() {
				@Override
				public void process(Matcher matcher, BodyState state, String search, AuthorDTO author, AuthorDTO loggedUser) {
					if (state.openCode) {
						state.openCode = false;
//						state.token = matcher.replaceFirst(String.format("<pre class='brush: %s; class-name: code'>", matcher.group(1)));
						state.token = state.token.substring(0, matcher.start()) +
								String.format("<pre class='brush: %s; class-name: code'>", matcher.group(1)) +
								state.token.substring(matcher.end());
						state.inCode = true;
					}
				}
			});
			put(Pattern.compile("\\[/code\\]"), new BodyTokenProcessor() {
				@Override
				public void process(Matcher matcher, BodyState state, String search, AuthorDTO author, AuthorDTO loggedUser) {
					state.token = matcher.replaceFirst("</pre>");
					state.inCode = false;
				}
			});
			put(Pattern.compile("\\[color$"), new BodyTokenProcessor() {
				@Override
				public void process(Matcher matcher, BodyState state, String search, AuthorDTO author, AuthorDTO loggedUser) {
					state.token = state.token.substring(0, matcher.start()) + "" + state.token.substring(matcher.end());
					state.openColor = true;
				}
			});
			//http://www.w3schools.com/cssref/css_colornames.asp
			put(Pattern.compile("^([a-zA-Z]{3,15}|\\#[A-Za-z0-9]{6})\\]"), new BodyTokenProcessor() {
				@Override
				public void process(Matcher matcher, BodyState state, String search, AuthorDTO author, AuthorDTO loggedUser) {
					if (state.openColor) {
						String color = matcher.group(1);
						state.token = state.token.substring(0, matcher.start()) +
								String.format("<span style='color: %s'>", color) +
								state.token.substring(matcher.end());
						state.openColor = false;
					}
				}
			});
		}};
	
	private static String simpleReplaceAll(String src, String search, String replacement) {
		if (search == null || search.length() == 0) return src;
		int p = 0;
		while ((p=src.indexOf(search, p)) != -1) {
			src = src.substring(0, p) + replacement + src.substring(p + search.length());
			p += replacement.length();
		}
		return src;
	}
	
	/* TODO: Never used ? nope
	private static String simpleReplaceFirst(String src, String search, String replacement) {
		if (search == null || search.length() == 0) return src;
		int p = src.indexOf(search);
		return src.substring(0, p) + replacement + src.substring(p + search.length());
	}
	*/
	
	private static String emoticons(String line) {
		Map<String,String[]> emoMap = Messages.getEmoMap();
		for (Entry<String, String[]> entry : emoMap.entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue()[0];
			String alt = entry.getValue()[1];
			// tutte le emo sono lower case ora
			line = simpleReplaceAll(line, value, String.format("<img class='emoticon' alt='%s' title='%s' src='images/emo/%s.gif'>", alt, alt, key));
			line = simpleReplaceAll(line, value.toUpperCase(), String.format("<img class='emoticon' alt='%s' title='%s' src='images/emo/%s.gif'>", alt, alt, key));
		}
		return line;
	}
	
	private static String color_quote(String line) {
		Matcher m = QUOTE.matcher(line);
		if (m.find()) {
			int quoteLvl = m.group(0).replaceAll(" ", "").replaceAll("&gt;", " ").length();
			quoteLvl--;
			String cssClass = "quoteLvl" + ((quoteLvl % 4) + 1);
			line =  "<span class='" + cssClass + "'>" + line + "</span>";
		}
		return line;
	}
	
	public int doAfterBody() throws JspTagException {
		try {
			String body = getBodyContent().getString();
			AuthorDTO loggedUser = (AuthorDTO) pageContext.getSession().getAttribute(MainServlet.LOGGED_USER_SESSION_ATTR);
			getBodyContent().getEnclosingWriter().write(getMessage(body, search, author, loggedUser));
		} catch (Exception e) {
			throw new JspTagException(e);
		}
		return SKIP_BODY;
	}
		
	public static String getMessage(String body, String search, AuthorDTO author, AuthorDTO loggedUser) throws Exception {
		StringBuilder out = new StringBuilder();
		
		// questo elimina il caso di multipli tags sulla stessa linea non separati
		body = simpleReplaceAll(body, "[/code]", "[/code] ");
		body = simpleReplaceAll(body, "[/img]", "[/img] ");
		body = simpleReplaceAll(body, "[/yt]", "[/yt] ");
		body = simpleReplaceAll(body, "[/color]", "[/color] ");
		
//			System.out.println("------ body ------");
//			System.out.println(body);
//			System.out.println("------ body ------");

			BodyState state = new BodyState();
			// contiamo 'sti cazzo di tag
			Matcher tagMatcher = Pattern.compile("(<b>|</b>|<i>|</i>|<s>|</s>|<u>|</u>)", Pattern.CASE_INSENSITIVE).matcher(body);
			while (tagMatcher.find()) {
				String tag = tagMatcher.group(1);
				if (      tag.equalsIgnoreCase("<b>") ) state.open_b++;
				else if (tag.equalsIgnoreCase("</b>")) state.open_b--;
				else if (tag.equalsIgnoreCase("<i>") ) state.open_i++;
				else if (tag.equalsIgnoreCase("</i>")) state.open_i--;
				else if (tag.equalsIgnoreCase("<s>") ) state.open_s++;
				else if (tag.equalsIgnoreCase("</s>")) state.open_s--;
				else if (tag.equalsIgnoreCase("<u>") ) state.open_u++;
				else if (tag.equalsIgnoreCase("</u>")) state.open_u--;
			}

			String[] lines = body.split("<BR>");
			for (int i=0;i<lines.length;++i) {
				String line = lines[i];
				StringTokenizer tokens = new StringTokenizer(line, " ", true);
				StringBuffer lineBuf = new StringBuffer();
				boolean wasInCode = state.inCode;
				while (tokens.hasMoreTokens()) {
					String token = tokens.nextToken();
					state.token = token;
					boolean noMatch = true;
					if (token.length() > 3) { // non c'è niente da matchare di interessante sotto i 4 caratteri
						state.token = simpleReplaceAll(state.token, "[code]", "<pre class='code'>");
						//state.token = simpleReplaceAll(state.token, "[/code]", "</pre>");
						state.token = simpleReplaceAll(state.token, "[/color]", "</span>");
						for(Iterator<Pattern> it = patternProcessorMapping.keySet().iterator(); it.hasNext();) {
							Pattern pattern = it.next();
							Matcher matcher = pattern.matcher(state.token);
							while (matcher.find()) {
								noMatch = false;
								BodyTokenProcessor processor = patternProcessorMapping.get(pattern);

//								System.out.println("token: "+state.token);
//								System.out.println("regex: "+pattern.pattern());
							processor.process(matcher, state, search, author, loggedUser);
//								System.out.println("---->: "+state.token);
			}
		}
				}
				if (noMatch) {
					state.token = simpleReplaceAll(state.token, search, "<span style='background-color: yellow'>" + search + "</span>");
				}
				lineBuf.append(state.token);
			}
			line = lineBuf.toString();
			if ((!state.inCode) && (!wasInCode)) {
				line = emoticons(line);
				line = color_quote(line);
			}
			out.append(line);
			if (state.inCode)
				out.append("\n");
			else
				out.append("<BR>");
		}
		
		int i;
		for (i=0;i<state.open_b;++i) out.append("</b>");
		for (i=0;i<state.open_i;++i) out.append("</i>");
		for (i=0;i<state.open_s;++i) out.append("</s>");
		for (i=0;i<state.open_u;++i) out.append("</u>");
		
		return out.toString();
	}

	public void setSearch(String search) {
		this.search = search;
	}

	public String getSearch() {
		return search;
	}
	
	public void setAuthor(AuthorDTO author) {
		this.author = author;
	}
	
	public AuthorDTO getAuthor() {
		return author;
	}
}
