package com.forumdeitroll;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.log4j.Logger;

import com.forumdeitroll.persistence.AuthorDTO;
import com.forumdeitroll.servlets.MainServlet;

public class PagerTag extends TagSupport  {

	/* esempi di output desiderati
	[1] 2 3 4 5 > >>
	1 [2] 3 4 5 6 > >>
	1 2 [3] 4 5 6 7 > >>
	<< < 2 3 [4] 5 6 7 8 > >>
	<< < 3 4 [5] 6 7 8 9 > >>
	<< < 4 5 [6] 7 8 9 10
	*/

	private static final long serialVersionUID = 1L;

	private static final Logger LOG = Logger.getLogger(PagerTag.class);

	// quanti elementi prima
	private static final int HEAD = 2;
	// quanti elementi dopo
	private static final int TAIL = 4;

	private static enum PagerType {
		CURRENT, PAGE, NEXT, LAST, FIRST, PREV
	};

	private static class PagerElem {
		 public final PagerType type;
		 public final int n;
		 public PagerElem(int n, PagerType type) {
			 this.type = type;
			 this.n = n;
		 }
		 @Override
		public String toString() {
			return "{type="+type.toString()+";n="+n+"}";
		}
	}

	// porting di una versione in javascript ben testata, non dovrebbe avere errori qua dentro
	private LinkedList<PagerElem> generatePager(int cur, int max) {
		//LOG.debug("generatePager("+cur+","+max+")");
		LinkedList<PagerElem> pager = new LinkedList<PagerTag.PagerElem>();
		if (HEAD - cur >= -1) {
			int limit = cur + TAIL;
			if (limit > max) limit = max;
			for (int i=0; i<=limit; ++i) {
				if (i == cur)
					pager.add(new PagerElem(i, PagerType.CURRENT));
				else
					pager.add(new PagerElem(i, PagerType.PAGE));
			}
			if (cur + TAIL < max) {
				if (cur + TAIL + 1 < max) {
					pager.add(new PagerElem(cur + TAIL + 1, PagerType.NEXT));
					pager.add(new PagerElem(max, PagerType.LAST));
				} else {
					pager.add(new PagerElem(cur + TAIL + 1, PagerType.LAST));
				}
			}
		} else {
			pager.add(new PagerElem(0, PagerType.FIRST));
			pager.add(new PagerElem(cur - HEAD - 1, PagerType.PREV));
			int limit = cur + TAIL;
			if (limit > max) limit = max;
			for (int i = cur - HEAD; i <= limit; ++i) {
				if (i == cur)
					pager.add(new PagerElem(i, PagerType.CURRENT));
				else
					pager.add(new PagerElem(i, PagerType.PAGE));
			}
			if (cur + TAIL < max) {
				if (cur + TAIL + 1 < max) {
					pager.add(new PagerElem(cur + TAIL + 1, PagerType.NEXT));
					pager.add(new PagerElem(max, PagerType.LAST));
				} else {
					pager.add(new PagerElem(cur + TAIL + 1, PagerType.LAST));
				}
			}
		}
		return pager;
	}

	private static void renderPager(LinkedList<PagerElem> pager, PageContext pageContext, PagerHandler handler, boolean mobileView) throws IOException {
		JspWriter out = pageContext.getOut();
		out.write("<ul class='pager'>");
		HttpServletRequest req = (HttpServletRequest)pageContext.getRequest();
		for (Iterator<PagerElem> pagerIterator = pager.iterator(); pagerIterator.hasNext();) {
			PagerElem page = pagerIterator.next();
			if (page.type == PagerType.CURRENT) {
				out.write("<li class='pager-current'>");
				if (mobileView) {
					out.write("<a href=javascript:location.reload() class=btn><b>" + Integer.toString(page.n + 1) + "</b></a>");
				} else {
					out.write(Integer.toString(page.n + 1)); //visualizza 1, usa 0 e cosi' via
				}
				out.write("</li>");
			} else {
				out.write("<li class='pager-");
				switch (page.type) {
				case FIRST:
					out.write("first'><a href=\"");break;
				case PREV:
					out.write("prev'><a href=\"");break;
				case NEXT:
					out.write("next'><a href=\"");break;
				case LAST:
					out.write("last'><a href=\"");break;
				case PAGE:
					out.write("page'><a href=\"");break;
				default:
					break;
				}
				out.write(handler.getLink(page.n, req)); //visualizza 1, usa 0 e cosi' via
				if (mobileView) {
					out.write("\" class=\"btn");
				}
				switch (page.type) {
				case FIRST:
					out.write("\">&#171;</a></li>");break;
				case PREV:
					out.write("\">&lt;</a></li>");break;
				case NEXT:
					out.write("\">&gt;</a></li>");break;
				case LAST:
					out.write("\">&#187;</a></li>");break;
				case PAGE:
					out.write(String.format("\">%d</a></li>", page.n + 1));break; //visualizza 1, usa 0 e cosi' via
				default:
					break;
				}
			}
		}
		out.write("</ul>");
	}

	@Override
	public int doEndTag() throws JspException {
		try {
			PagerHandler pagerHandler = handlers.get(handler);
			HttpServletRequest req = (HttpServletRequest)pageContext.getRequest();
			int cur = pagerHandler.getCurrentPage(req);
			int max = pagerHandler.getMaxPages(req);

			/*{
				// fuzzy pager per test
				max = (int) (Math.random() * 20);
				cur = (int) (Math.random() * max);
				System.out.println("DATI TEST: max = "+max+" cur = "+cur);
			}*/
			LinkedList<PagerElem> pager = generatePager(cur, max);
			//LOG.debug("pager -> "+pager);
			renderPager(pager, pageContext, handlers.get(handler), MainServlet.isMobileView(req));

		} catch (Exception e) {
			LOG.error("Errore durante il rendering del pager: "+e.getMessage(), e);
		}
		return SKIP_BODY;
	}

	public static int pagify(int itemCount, int pageSize) {
		return (int)Math.floor((double)(itemCount-1) / (double)pageSize);
	}

	private static final HashMap<String, PagerHandler> handlers = new HashMap<String, PagerTag.PagerHandler>() {
		private static final long serialVersionUID = -7479181330396773734L;

	{
		put("pvt", new PagerHandler() {

			@Override
			public int getMaxPages(HttpServletRequest req) {
				//se vedi la lista dei pvt sei loggato per forza
				AuthorDTO author = (AuthorDTO) req.getAttribute(MainServlet.LOGGED_USER_REQ_ATTR);
				if (author != null) {
					Integer totalSize = (Integer)req.getAttribute("totalSize");
					return totalSize == null ? -1 : totalSize;
				}
				return -1; //furmigamento
			}

			@Override
			public String getLink(int pageNumber, HttpServletRequest req) {
				String servlet = (String) req.getAttribute("servlet");
				return servlet + "?action=" +req.getAttribute("from") + "&amp;page=" + pageNumber;
			}
		});

		put("Messages", new PagerHandler() {
			@Override
			public int getMaxPages(HttpServletRequest req) {
				Integer resultSize = (Integer)req.getAttribute("resultSize");
				if (resultSize != null && resultSize < MainServlet.PAGE_SIZE) {
					return getCurrentPage(req);
				} 
				Integer totalSize = (Integer)req.getAttribute("totalSize");
				if (totalSize == null) {
					// vabbeh, io ci ho provato :$ ...
					totalSize = Integer.MAX_VALUE;
				}
				return ((totalSize - 1) / MainServlet.PAGE_SIZE);
			}
			@Override
			public String getLink(int pageNumber, HttpServletRequest req) {
				String action = (String) req.getAttribute("action");
				String servlet = (String) req.getAttribute("servlet");

				StringBuilder link = new StringBuilder(servlet)
					.append("?action=").append(action)
					.append("&amp;page=").append(pageNumber);
				if (req.getAttribute("specificParams") != null) {
					@SuppressWarnings("unchecked")
					Map<String, String> specificParams = (Map<String, String>) req.getAttribute("specificParams");
					for (Map.Entry<String, String> entry: specificParams.entrySet()) {
						if (entry.getValue() == null) {
							continue;
						}
						try {
							link.append("&amp;").append(entry.getKey());
							link.append("=").append(URLEncoder.encode(entry.getValue(), "UTF-8"));
						} catch (UnsupportedEncodingException e) {
							// ignore
						}
					}
				}
				return link.toString();
			}
		});
	}};

	private String handler;
	public String getHandler() {
		return handler;
	}
	public void setHandler(String handler) {
		this.handler = handler;
	}

	private static abstract class PagerHandler {
		public final int getCurrentPage(HttpServletRequest req) {
			try {
				return Integer.parseInt(req.getParameter("page"));
			} catch (Exception e) {
				return 0; //default
			}
		}
		public abstract int getMaxPages(HttpServletRequest req);
		public abstract String getLink(int pageNumber, HttpServletRequest req);
	}
}
