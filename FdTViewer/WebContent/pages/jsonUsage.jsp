<html>
	<head>
		<title>Perdincibacco, come funziona l'interfaccia JSON del FdT ?</title>
		<style type="text/css">
			dl {
				font-family: monospace;
			}
			dl dt {
				font-weight: bold;
			}
			dd {
				margin-bottom: 1.5em;
			}
			p.u {
				margin: .3em 0em .3em 0em;
				text-decoration: underline;
			}
			span.param {
				font-style: italic;
			}
		</style>
	</head>
	<%
		String url = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
		url += "/JSon?action=";
	%>
	<body>
		<h1>Perdincibacco, come funziona l'interfaccia JSON del FdT ?</h1>
		L'URL ha la forma <a href="<%=url%>action"><%=url%><i>azione</i></a>, dove il valore di <i>azione</i> pu&ograve; essere uno dei seguenti:
		<dl>
			<dt>getQuotes</dt>
			<dd>Tutte le frasi famose di un autore<br/>
				<p class="u">Parametri:</p>
				<ul>
					<li><span class="param">nick</span>: Nick dell'utente. Obbligatorio
				</ul>
				<p class="u">Esempi:</p>
				<ul>
					<li><a href="<%=url%>getQuotes&nick=Giambo"><%=url%>getQuotes&amp;nick=Giambo</a>
				</ul>
			</dd>
		
			<dt>getEmos</dt>
			<dd>Tutte le faccine (Serie classica e serie estesa), codificate Base64<br/>
				<p class="u">Esempi:</p>
				<ul>
					<li><a href="<%=url%>getEmos"><%=url%>getEmos</a>
				</ul>
			</dd>

			<dt>getForums</dt>
			<dd>Lista di tutti i forums<br/>
				<p class="u">Esempi:</p>
				<ul>
					<li><a href="<%=url%>getForums"><%=url%>getForums</a>
				</ul>
			</dd>

			<dt>getAuthor</dt>
			<dd>Informazioni su un utente<br/>
				<p class="u">Parametri:</p>
				<ul>
					<li><span class="param">nick</span>: Nick dell'utente. Obbligatorio
				</ul>
				<p class="u">Esempi:</p>
				<ul>
					<li><a href="<%=url%>getAuthor&nick=Giambo"><%=url%>getAuthor&amp;nick=Giambo</a>
				</ul>
			</dd>
		
			<dt>getAuthors</dt>
			<dd>Lista di tutti gli utenti<br/>
				<p class="u">Parametri:</p>
				<ul>
					<li><span class="param">onlyActive</span>: Se "true" allora mostra solo gli utenti attivi (Con una password).
				</ul>
				<p class="u">Esempi:</p>
				<ul>
					<li><a href="<%=url%>getAuthors"><%=url%>getAuthors</a>
					<li><a href="<%=url%>getAuthors&onlyActive=true"><%=url%>getAuthors&amp;onlyActive=true</a>
				</ul>
			</dd>

			<dt>getThread</dt>
			<dd>Tutti i messaggi di un thread<br/>
				<p class="u">Parametri:</p>
				<ul>
					<li><span class="param">threadId</span>: Id del thread. Obbligatorio
				</ul>
				<p class="u">Esempi:</p>
				<ul>
					<li><a href="<%=url%>getThread&threadId=2710439"><%=url%>getThread&amp;threadId=2710439</a>
				</ul>
			</dd>
				
			<dt>getThreads</dt>
			<dd>Threads, ordinati cronologicamente per data di creazione<br/>
				<p class="u">Parametri:</p>
				<ul>
					<li><span class="param">pageSize</span>: Numero di threads per pagina. Default 25, massimo 100
					<li><span class="param">page</span>: Pagina, la numerazione inizia da zero
					<li><span class="param">forum</span>: Nome del forum. Se il parametro non e' definito, tutto il forum, se e' vuoto solo il forum principale.
				</ul>
				<p class="u">Esempi:</p>
				<ul>
					<li><a href="<%=url%>getThreads"><%=url%>getThreads</a>: Ultimi 25 threads
					<li><a href="<%=url%>getThreads&pageSize=10"><%=url%>getThreads&amp;pageSize=10</a>: Ultimi 10 threads
					<li><a href="<%=url%>getThreads&pageSize=10&page=5&forum=Sesso"><%=url%>getThreads&amp;pageSize=10&amp;page=5&amp;forum=Sesso</a>: Del forum "Sesso", mostra i 10 treads della quinta pagina (51-60) 
				</ul>
			</dd>

			<dt>getMessage</dt>
			<dd>Singolo messaggio<br/>
				<p class="u">Parametri:</p>
				<ul>
					<li><span class="param">msgId</span>: Id del messaggio. Obbligatorio
				</ul>
				<p class="u">Esempi:</p>
				<ul>
					<li><a href="<%=url%>getMessage&threadId=2710439"><%=url%>getMessage&amp;threadId=2710439</a>
				</ul>
			</dd>
				
			<dt>getMessages</dt>
			<dd>Messaggi, ordinati cronologicamente<br/>
				<p class="u">Parametri:</p>
				<ul>
					<li><span class="param">pageSize</span>: Numero di messaggi per pagina. Default 25, massimo 100
					<li><span class="param">page</span>: Pagina, la numerazione inizia da zero
					<li><span class="param">forum</span>: Nome del forum. Se il parametro non e' definito, tutto il forum, se e' vuoto solo il forum principale.
				</ul>
				<p class="u">Esempi:</p>
				<ul>
										<li><a href="<%=url%>getThreads"><%=url%>getMessages</a>: Ultimi 25 messaggi
					<li><a href="<%=url%>getMessages&pageSize=10"><%=url%>getMessages&amp;pageSize=10</a>: Ultimi 10 messaggi
					<li><a href="<%=url%>getMessages&pageSize=10&page=5&forum=Sesso"><%=url%>getMessages&amp;pageSize=10&amp;page=5&amp;forum=Sesso</a>: Del forum "Sesso", mostra i 10 messaggi della quinta pagina (51-60) 
				</ul>
			</dd>
		</dl>
		Tutte le <i>azioni</i> supportano il parametro "callback", necessario per bypassare la "same origin policy" (JSONP). 
	</body>
</html>