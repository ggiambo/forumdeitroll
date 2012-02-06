package com.acmetoy.ravanator.fdt;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.log4j.Logger;

import com.acmetoy.ravanator.fdt.persistence.AuthorDTO;
import com.acmetoy.ravanator.fdt.persistence.IPersistence;
import com.acmetoy.ravanator.fdt.persistence.MessageDTO;
import com.acmetoy.ravanator.fdt.persistence.PersistenceFactory;
import com.acmetoy.ravanator.fdt.servlets.MainServlet;
import com.acmetoy.ravanator.fdt.servlets.Messages;
import com.acmetoy.ravanator.fdt.servlets.Threads;

public class PagerTag extends TagSupport  {
	
	/* esempi di output desiderati
	[1] 2 3 4 5 > >>
	1 [2] 3 4 5 6 > >>
	1 2 [3] 4 5 6 7 > >>
	<< < 2 3 [4] 5 6 7 8 > >>
	<< < 3 4 [5] 6 7 8 9 > >>
	<< < 4 5 [6] 7 8 9 10
	*/
	
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
			for (int i=0; i<limit; ++i) {
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
	
	private static void renderPager(LinkedList<PagerElem> pager, PageContext pageContext, PagerHandler handler) throws IOException {
		JspWriter out = pageContext.getOut();
		out.write("<ul class='pager'>");
		for (Iterator<PagerElem> pagerIterator = pager.iterator(); pagerIterator.hasNext();) {
			PagerElem page = pagerIterator.next();
			if (page.type == PagerType.CURRENT) {
				out.write("<li class='pager-current'>");
				out.write(Integer.toString(page.n + 1)); //visualizza 1, usa 0 e cosi' via
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
				}
				out.write(handler.getLink(page.n, pageContext)); //visualizza 1, usa 0 e cosi' via
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
				}
			}
		}
		out.write("</ul>");
	}
	
	@Override
	public int doEndTag() throws JspException {
		try {
			PagerHandler pagerHandler = handlers.get(handler);
			
			int cur = pagerHandler.getCurrentPage(pageContext);
			int max = pagerHandler.getMaxPages(pageContext);
			
			/*{
				// fuzzy pager per test
				max = (int) (Math.random() * 20);
				cur = (int) (Math.random() * max);
				System.out.println("DATI TEST: max = "+max+" cur = "+cur);
			}*/
			LinkedList<PagerElem> pager = generatePager(cur, max);
			//LOG.debug("pager -> "+pager);
			renderPager(pager, pageContext, handlers.get(handler));
			
		} catch (Exception e) {
			LOG.error("Errore durante il rendering del pager: "+e.getMessage(), e);
		}
		return SKIP_BODY;
	}
	
	private static final HashMap<String, PagerHandler> handlers = new HashMap<String, PagerTag.PagerHandler>() {
		private static final long serialVersionUID = -7479181330396773734L;
		private IPersistence persistence;
		
		private IPersistence getPersistence() {
			if (persistence == null) {
				try {
					persistence = PersistenceFactory.getInstance();
				} catch (Exception e) {
					LOG.error("Impossibile inizializzare la persistence in PagerTag", e);
					return null;
				}
			}
			return persistence;
		}
	{
		put("pvt", new PagerHandler() {
			
			@Override
			public int getMaxPages(PageContext pageContext) {
				//se vedi la lista dei pvt sei loggato per forza
				AuthorDTO author = (AuthorDTO) pageContext.getSession().getAttribute(MainServlet.LOGGED_USER_SESSION_ATTR);
				if (author != null) {
					if (pageContext.getRequest().getAttribute("from").equals("inbox"))
						return getPersistence().getInboxPages(author);
					else if (pageContext.getRequest().getAttribute("from").equals("outbox"))
						return getPersistence().getOutboxPages(author);
					else return -1; //imbozzibile
				}
				return -1; //furmigamento
			}
			
			@Override
			public int getCurrentPage(PageContext pageContext) {
				try {
					return Integer.parseInt(pageContext.getRequest().getParameter("page"));
				} catch (Exception e) {
					return 0; //default
				}
			}
			
			@Override
			public String getLink(int pageNumber, PageContext pageContext) {
				return "?action=" + pageContext.getRequest().getAttribute("from") + "&page=" + pageNumber;
			}
		});
		
		put("Messages", new PagerHandler() {
			// test Messages?action=init (default) ok
			// test Messages?action=getByForum ok
			// test Messages?action=getByPage INCOGNITA: non viene chiamato direttamente dalle pagine, ma la GiamboAction e' usata altrove
			// test Threads?action=getAuthorThreadsByLastPost FIXATO a mano, ma non dovrebbe essere cosi'
			// test Threads?action=init (default) ok
			// test Threads?action=getThreadsByLastPost ok
			// test Messages?action=getByAuthor ok
			@Override
			public int getMaxPages(PageContext pageContext) {
				List<MessageDTO> messages = (List<MessageDTO>) pageContext.getRequest().getAttribute("messages");
				if (messages.size() < 20) // MainServlet.PAGE_SIZE
					return getCurrentPage(pageContext);
				else
					return Integer.MAX_VALUE; //sto barando per non fare la query dipendente dal parametro action
			}
			private Class[] servlets = new Class[] {
				Messages.class,
				Threads.class
			};
			@Override
			public String getLink(int pageNumber, PageContext pageContext) {
				String action = (String) pageContext.getRequest().getAttribute("action");
				String servlet = (String) pageContext.getRequest().getAttribute("servlet");
				servlet = servlet.substring(servlet.lastIndexOf('.') + 1);
				
				String link = servlet +
						"?action=" + action +
						"&pageNr=" + pageNumber;
				if (pageContext.getRequest().getAttribute("specificParams") != null) {
					HashMap<String, String> specificParams =
							(HashMap<String, String>) pageContext.getRequest().getAttribute("specificParams");
					for (String key: specificParams.keySet()) {
						try {
							link += "&" + key + "=" + java.net.URLEncoder.encode(specificParams.get(key), "UTF-8");
						} catch (UnsupportedEncodingException e) {
							// ignore
						}
					}
				}
				if ("getAuthorThreadsByLastPost".equals(action) && pageContext.getRequest().getParameter("author") != null) {
					try {
						link += "&author=" + java.net.URLEncoder.encode(pageContext.getRequest().getParameter("author"), "UTF-8");
					} catch (UnsupportedEncodingException e) {
						// ignore
					} 
				}
				return link;
			}
			
			@Override
			public int getCurrentPage(PageContext pageContext) {
				try {
					return Integer.parseInt(pageContext.getRequest().getParameter("pageNr"));
				} catch (Exception e) {
					return 0; //default
				}
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
	
	private static interface PagerHandler {
		public int getCurrentPage(PageContext pageContext);
		public int getMaxPages(PageContext pageContext);
		public String getLink(int pageNumber, PageContext pageContext);
	}
}
