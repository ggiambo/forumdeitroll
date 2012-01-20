package com.acmetoy.ravanator.fdt;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;

import com.acmetoy.ravanator.fdt.persistence.AuthorDTO;
import com.acmetoy.ravanator.fdt.servlets.Messages;

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
		public void process(Matcher matcher, BodyState state, String search, AuthorDTO author);
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

	private static final Map<Pattern, BodyTokenProcessor> patternProcessorMapping =
		new HashMap<Pattern, BodyTokenProcessor>() {{
			put(Pattern.compile("^(www\\.|https?://|ftp://|mailto:).*$"), new BodyTokenProcessor() {
				@Override
				public void process(Matcher matcher, BodyState state, String search, AuthorDTO author) {
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
				public void process(Matcher matcher, BodyState state, String search, AuthorDTO author) {
					if (state.inCode) return;
					String url = escape(matcher.group(1));
//					state.token = matcher.replaceFirst(
//						String.format("<a class='preview' href='%s'><img class='userPostedImage' alt='Immagine postata dall&#39;utente' src='%s'></a>", url, url)
//					);
					if (StringUtils.isEmpty(author.getNick())) {
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
				@Override
				public void process(Matcher matcher, BodyState state, String search, AuthorDTO author) {
					if (state.inCode) return;
					String youcode = escape(matcher.group(1));
					StringBuffer sb = new StringBuffer()
					.append("<object height='329' width='400'>")
					.append("<param value='http://www.youtube.com/v/").append(youcode).append("' name='movie'>")
					.append("<param value='transparent' name='wmode'>")
					.append("<embed height='329' width='400' wmode='transparent' ")
					.append("type='application/x-shockwave-flash' ")
					.append("src='http://www.youtube.com/v/").append(youcode).append("'></object>");
//					state.token = matcher.replaceFirst(sb.toString());
					state.token = state.token.substring(0, matcher.start()) + sb.toString() + state.token.substring(matcher.end());
				}
			});
			put(Pattern.compile("\\[code$"), new BodyTokenProcessor() {
				@Override
				public void process(Matcher matcher, BodyState state, String search, AuthorDTO author) {
					state.token = state.token.substring(0, matcher.start()) + "" + state.token.substring(matcher.end());
					state.openCode = true;
				}
			});
			put(Pattern.compile("^([a-zA-Z0-9]+)\\]"), new BodyTokenProcessor() {
				@Override
				public void process(Matcher matcher, BodyState state, String search, AuthorDTO author) {
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
				public void process(Matcher matcher, BodyState state, String search, AuthorDTO author) {
					state.token = matcher.replaceFirst("</pre>");
					state.inCode = false;
				}
			});
			put(Pattern.compile("\\[color$"), new BodyTokenProcessor() {
				@Override
				public void process(Matcher matcher, BodyState state, String search, AuthorDTO author) {
					state.token = state.token.substring(0, matcher.start()) + "" + state.token.substring(matcher.end());
					state.openColor = true;
				}
			});
			//http://www.w3schools.com/cssref/css_colornames.asp
			put(Pattern.compile("^([a-zA-Z]{3,15}|\\#[A-Za-z0-9]{6})\\]"), new BodyTokenProcessor() {
				@Override
				public void process(Matcher matcher, BodyState state, String search, AuthorDTO author) {
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
	
	private static String simpleReplaceFirst(String src, String search, String replacement) {
		if (search == null || search.length() == 0) return src;
		int p = src.indexOf(search);
		return src.substring(0, p) + replacement + src.substring(p + search.length());
	}
	
	private static String emoticons(String line) {
		Map<String,String> emoMap = Messages.getEmoMap();
		for (String key: emoMap.keySet()) {
			String value = emoMap.get(key).trim(); // trim() e' male ?
			String alt = EMO_ALT_MAP.get(key);
			line = simpleReplaceAll(line, value, String.format("<img class='emoticon' alt='%s' title='%s' src='images/emo/%s.gif'>", alt, alt, key));
			line = simpleReplaceAll(line, value.toUpperCase(), String.format("<img class='emoticon' alt='%s' title='%s' src='images/emo/%s.gif'>", alt, alt, key));
			line = simpleReplaceAll(line, value.toLowerCase(), String.format("<img class='emoticon' alt='%s' title='%s' src='images/emo/%s.gif'>", alt, alt, key));
		}
		return line;
	}
	
	private static String color_quote(String line) {
		if (line.startsWith("&gt; &gt; &gt; &gt; ")) {
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
		String body = getBodyContent().getString();
			getBodyContent().getEnclosingWriter().write(getMessage(body, search, author));
		} catch (Exception e) {
			throw new JspTagException(e);
		}
		return SKIP_BODY;
		}
		
	public static String getMessage(String body, String search, AuthorDTO author) throws Exception {
		StringBuilder out = new StringBuilder();
		
		// questo elimina il caso di multipli [code] sulla stessa linea (casino da gestire, bisognava cambiare stile di parsing del body)
		body = simpleReplaceAll(body, "[/code]", "[/code]<BR>");
		
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
							processor.process(matcher, state, search, author);
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
