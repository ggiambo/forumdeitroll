<html>
	<head>
		<script type="text/javascript">
		
		// copia il tag selezionato nell'input
		function copyToInput(nick) {
			document.getElementById("input").value = nick;
		}
		
		// timer per la ricerca dei nick
		var timer;
		function changeInput(elem) {
			clearTimeout(timer);
			if (elem.value.length > 2) {
				timer = setTimeout("updateDiv()", 500);
			}
		}
		
		// fa la ricerca dei tags, update del div
		function updateDiv() {
			var tagName =  document.getElementById("input").value;
			var xmlhttp = new XMLHttpRequest();          
			xmlhttp.onreadystatechange = function() {
				if (xmlhttp.readyState == 4 && xmlhttp.status == 200) {
					var lines = xmlhttp.responseText.split("\n");
					var content = "";
					for (var i = 0; i < lines.length; i++) {
						content += "<a href='#' onClick='copyToInput(\"" + lines[i] + "\")'>" + lines[i] + "</a><br/>";
					}
					document.getElementById("listaTag").innerHTML = content;
				}
			}
			xmlhttp.open("GET","http://ravanator.acmetoy.com/fdtduezero/tagsAjax.php?tagName="+tagName,true);
			xmlhttp.send();
		}
		
		// crea un tag
		function create() {
			var tagName =  document.getElementById("input").value;
			var xmlhttp = new XMLHttpRequest();
			xmlhttp.onreadystatechange = function() {
				if (xmlhttp.readyState == 4 && xmlhttp.status == 200) {
					updateDiv();
				}
			}
			xmlhttp.open("GET","http://ravanator.acmetoy.com/fdtduezero/tagsAjax.php?createTagName="+tagName,true);
			xmlhttp.send();
		}
		
		</script>
	</head>
	<body>
		<input id="input" onKeyUp="changeInput(this)"></input>
		<input type="button" onClick="create()" value="Create"/><br/>
		<div id="listaTag"></div>
	</body>
</html>
