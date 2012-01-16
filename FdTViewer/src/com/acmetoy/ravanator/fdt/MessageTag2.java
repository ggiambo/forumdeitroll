package com.acmetoy.ravanator.fdt;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.apache.commons.lang3.StringEscapeUtils;

import com.acmetoy.ravanator.fdt.servlets.Messages;

public class MessageTag2 extends BodyTagSupport {
	private String search;
	public void setSearch(String search) {
		this.search = search;
	}
	public String getSearch() {
		return search;
	}
	
	private static String escape(String in) {return StringEscapeUtils.escapeHtml4(in);}
	
	private static class BodyState {
		public String token;
		public boolean openCode;
		public boolean inCode;
	}
	
	private static interface BodyTokenProcessor {
		public void process(Matcher matcher, BodyState state);
	}
	
	private static final Map<String, String> EMO_ALT_MAP = new HashMap<String, String>();
	static {
		EMO_ALT_MAP.put("1", "Sorride");
		EMO_ALT_MAP.put("2", "A bocca aperta");
		EMO_ALT_MAP.put("5", "Con la lingua fuori");
		EMO_ALT_MAP.put("12", "Deluso");
		EMO_ALT_MAP.put("3", "Occhiolino");
		EMO_ALT_MAP.put("4", "Sorpresa");
		EMO_ALT_MAP.put("7", "Arrabbiato");
		EMO_ALT_MAP.put("8", "Perplesso");
		EMO_ALT_MAP.put("10", "Triste");
		EMO_ALT_MAP.put("9", "Imbarazzato");
		EMO_ALT_MAP.put("13", "Ficoso");
		EMO_ALT_MAP.put("11", "In lacrime");
		EMO_ALT_MAP.put("6", "A bocca storta");
		EMO_ALT_MAP.put("angelo", "Angioletto");
		EMO_ALT_MAP.put("anonimo", "Anonimo");
		EMO_ALT_MAP.put("diavoletto", "Indiavolato");
		EMO_ALT_MAP.put("fantasmino", "Fantasma");
		EMO_ALT_MAP.put("geek", "Geek");
		EMO_ALT_MAP.put("idea", "Idea!");
		EMO_ALT_MAP.put("newbie", "Newbie, inesperto");
		EMO_ALT_MAP.put("noia3", "Annoiato");
		EMO_ALT_MAP.put("pirata", "Pirata");
		EMO_ALT_MAP.put("robot", "Cylon");
		EMO_ALT_MAP.put("rotfl", "Rotola dal ridere");
		EMO_ALT_MAP.put("lovewin", "Fan Windows");
		EMO_ALT_MAP.put("lovelinux", "Fan Linux");
		EMO_ALT_MAP.put("loveapple", "Fan Apple");
		EMO_ALT_MAP.put("love", "Innamorato");
		EMO_ALT_MAP.put("loveamiga", "Fan Amiga");
		EMO_ALT_MAP.put("loveatari", "Fan Atari");
		EMO_ALT_MAP.put("lovec64", "Fan Commodore64");
		EMO_ALT_MAP.put("nolove", "Disinnamorato");
		EMO_ALT_MAP.put("troll", "Troll");
		EMO_ALT_MAP.put("troll1", "Troll occhiolino");
		EMO_ALT_MAP.put("troll2", "Troll chiacchierone");
		EMO_ALT_MAP.put("troll3", "Troll occhi di fuori");
		EMO_ALT_MAP.put("troll4", "Troll di tutti i colori");
	}
	
	private static class SimpleBodyTokenProcessor implements BodyTokenProcessor {
		private String replacement;
		public SimpleBodyTokenProcessor(String replacement) {
			this.replacement = replacement;
		}
		@Override
		public void process(Matcher matcher, BodyState state) {
			state.token = matcher.replaceFirst(replacement);
		}
	}
	
	private static final Map<Pattern, BodyTokenProcessor> patternProcessorMapping =
		new HashMap<Pattern, BodyTokenProcessor>() {{
			put(Pattern.compile("^(www.|https?://|ftp://|mailto:).*$"), new BodyTokenProcessor() {
				@Override
				public void process(Matcher matcher, BodyState state) {
					String url = escape(state.token);
					if (url.startsWith("www.")) {
						url = "http://" + url;
					}
					String desc = url;
					if (state.token.length() > 50) {
						desc = escape(state.token.substring(0,50)) + "...";
					}
					state.token = String.format("<a href='%s'>%s</a>",state.token, desc);
				}
			});
			put(Pattern.compile("\\[img\\](.*)\\[/img\\]"), new BodyTokenProcessor() {
				@Override
				public void process(Matcher matcher, BodyState state) {
					String url = escape(matcher.group(1));
					state.token = matcher.replaceFirst(
						String.format("<a class='preview' href='%s'><img class='userPostedImage' alt='Immagine postata dall&#39;utente' src='%s'></a>", url, url)
					);
				}
			});
			put(Pattern.compile("\\[yt\\](.*)\\[/yt\\]"), new BodyTokenProcessor() {
				@Override
				public void process(Matcher matcher, BodyState state) {
					String youcode = escape(matcher.group(1));
					StringBuffer sb = new StringBuffer()
					.append("<object height=\"329\" width=\"400\">")
					.append("<param value=\"http://www.youtube.com/v/").append(youcode).append("\" name=\"movie\">")
					.append("<param value=\"transparent\" name=\"wmode\">")
					.append("<embed height=\"329\" width=\"400\" wmode=\"transparent\" ")
					.append("type=\"application/x-shockwave-flash\" ")
					.append("src=\"http://www.youtube.com/v/").append(youcode).append("\"></object>");
					state.token = matcher.replaceFirst(sb.toString());
				}
			});
			put(Pattern.compile("\\[code\\]"), new BodyTokenProcessor() {
				@Override
				public void process(Matcher matcher, BodyState state) {
					state.token = matcher.replaceFirst("<pre class='code'>");
					state.inCode = true;
				}
			});
			put(Pattern.compile("\\[code$"), new BodyTokenProcessor() {
				@Override
				public void process(Matcher matcher, BodyState state) {
					state.token = matcher.replaceFirst("");
					state.openCode = true;
				}
			});
			put(Pattern.compile("^([a-zA-Z0-9]+)\\]"), new BodyTokenProcessor() {
				@Override
				public void process(Matcher matcher, BodyState state) {
					if (state.openCode) {
						state.openCode = false;
						state.token = matcher.replaceFirst(String.format("<pre class='brush: %s; class-name: code'>", matcher.group(1)));
						state.inCode = true;
					}
				}
			});
			put(Pattern.compile("\\[/code\\]"), new BodyTokenProcessor() {
				@Override
				public void process(Matcher matcher, BodyState state) {
					state.token = matcher.replaceFirst("</pre>");
					state.inCode = false;
				}
			});
		}};
	
	private static String simpleReplaceAll(String src, String search, String replacement) {
		int p = 0;
		while ((p=src.indexOf(search, p)) != -1) {
			src = src.substring(0, p) + replacement + src.substring(p + search.length());
			p += replacement.length();
		}
		return src;
	}
	
	private static String emoticons(String line) {
		Map<String,String> emoMap = Messages.getEmoMap();
		for (String key: emoMap.keySet()) {
			String value = emoMap.get(key);
			String alt = EMO_ALT_MAP.get(key);
			line = simpleReplaceAll(line, value.trim(), String.format("<img class='emoticon' alt='%s' title='%s' src='images/emo/%s.gif'>", alt, alt, key));
		}
		return line;
	}
	
	private static String color_quote(String line) {
		if (line.startsWith("&gt; &gt; &gt; &gt;")) {
			line = "<span class='quoteLvl4'>" + line + "</span>";
		} else if (line.startsWith("&gt; &gt; &gt; ")) {
			line = "<span class='quoteLvl3'>" + line + "</span>";
		} else if (line.startsWith("&gt; &gt; ")) {
			line = "<span class='quoteLvl2'>" + line + "</span>";
		} else if (line.startsWith("&gt; ")) {
			line = "<span class='quoteLvl1'>" + line + "</span>";
		}
		return line;
	}
	
	
	public int doAfterBody() throws JspTagException {
		try {
			JspWriter out = getBodyContent().getEnclosingWriter();
			String body = getBodyContent().getString();
//			System.out.println("------ body ------");
//			System.out.println(body);
//			System.out.println("------ body ------");
			
			BodyState state = new BodyState();
			
			String[] lines = body.split("<BR>");
			for (int i=0;i<lines.length;++i) {
				String line = lines[i];
				StringTokenizer tokens = new StringTokenizer(line, " ", true);
				StringBuffer lineBuf = new StringBuffer();
				boolean wasInCode = state.inCode;
				while (tokens.hasMoreTokens()) {
					String token = tokens.nextToken();
					state.token = token;
					if (!token.equals(" ")) {
						for(Iterator<Pattern> it = patternProcessorMapping.keySet().iterator(); it.hasNext();) {
							Pattern pattern = it.next();
							Matcher matcher = pattern.matcher(state.token);
							while (matcher.find()) {
								BodyTokenProcessor processor = patternProcessorMapping.get(pattern);
//								System.out.println("token: "+state.token);
//								System.out.println("regex: "+pattern.pattern());
								processor.process(matcher, state);
//								System.out.println("---->: "+state.token);
							}
						}	
					}
					lineBuf.append(state.token);
				}
				line = lineBuf.toString();
				if (!state.inCode && !wasInCode) {
					line = emoticons(line);
					line = color_quote(line);
				}
				out.write(line);
				if (state.inCode)
					out.write("\n");
				else
					out.write("<BR>");
			}
			
		} catch (Exception e) {
			throw new JspTagException(e);
		}
		return SKIP_BODY;
	}
}
